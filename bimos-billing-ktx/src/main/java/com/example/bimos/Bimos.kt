package com.example.bimos

import android.graphics.Color
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bimos.payments.*
import com.example.bimos.payments.models.*
import com.example.bimos.payments.tokenize.models.TokenizeParameters
import com.example.bimos.payments.tokenize.Tokenize
import com.example.bimos.payments.tokenize.TokenizeHandler
import com.example.bimos.products.GetApplicationProductsCallback
import com.example.bimos.products.ApplicationProductsManager
import com.example.bimos.products.ProductsMapper
import com.example.bimos.products.models.Product
import com.example.bimos.products.models.ProductsLists
import com.example.bimos.productsAndPayments.ProductsAndPaymentsManager
import com.example.bimos.productsAndPayments.ProductsAndPaymentsCallback
import com.example.bimos.productsAndPayments.ProductsAndPaymentsMapper
import com.example.bimos.productsAndPayments.models.ProductsAndPaymentsLists
import retrofit2.Call
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.TestParameters
import ru.yoomoney.sdk.kassa.payments.ui.color.ColorScheme

class Bimos(private val activity: AppCompatActivity) {

    private var _applicationId = ""
    private var _shopId = 0
    private var _secretKey = ""
    private var _clientApplicationKey = ""
    private var _colorScheme = ""
    private var _applicationName = ""
    private var _userUid = ""
    private var startTokenize = false

    private lateinit var _paymentMethodType: PaymentMethodType
    private lateinit var productDto: Product
    private lateinit var paymentDto: Payment

    private var productList: MutableList<Product> = mutableListOf()
    private var productMap: MutableMap<String, Product> = mutableMapOf()
    private var paymentList: MutableList<Payment> = mutableListOf()

    private lateinit var creatingPaymentCallback: () -> Unit
    private lateinit var successfulCreateCallback: (Payment) -> Unit
    private lateinit var yookassaErrorCreateCallback: (YookassaError) -> Unit
    private lateinit var errorCreateCallback: (Int, String, String?) -> Unit
    private lateinit var failureCreateCallback: (Call<PaymentResponse>, Throwable) -> Unit

    private lateinit var canceledConfirmationCallback: () -> Unit
    private lateinit var errorConfirmationCallback: (code: Int?, description: String?, url: String?) -> Unit

    private lateinit var successfulStatusResponse: (Payment) -> Unit
    private lateinit var yookassaErrorStatusCallback: (YookassaError) -> Unit
    private lateinit var errorStatusCallback: (Int, String, String?) -> Unit
    private lateinit var failureStatusCallback: (Call<PaymentResponse>, Throwable) -> Unit

    private val tokenizeLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("Log1", "Пришел ответ в ланчере tokenizeLauncher")

        val resultData = result.data?.let { intent -> Checkout.createTokenizationResult(intent) }
        val paymentMethodType = resultData?.paymentMethodType
        if (paymentMethodType != null) {
            _paymentMethodType = paymentMethodType
        }

        startTokenize = false
        TokenizeHandler.processingTokenizeResult(
            result = result,
            product = productDto,
            initParameters = getInitParametersClass(),
            activity = activity,
            creatingPaymentCallback = creatingPaymentCallback,
            successfulCreateCallback = successfulCreateCallback,
            yookassaErrorCreateCallback = yookassaErrorCreateCallback,
            errorCreateCallback = errorCreateCallback,
            failureCreateCallback = failureCreateCallback, { pendingPayment ->
            Log.d("Log1", "Сработал колбэк pendingPayment: $pendingPayment")
            paymentDto = pendingPayment
            startConfirmation(pendingPayment)
            }, {
                addPaymentToPaymentList(it)
            }
        )
    }

    private val confirmationLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("Log1", "Пришел ответ в ланчере confirmationLauncher")
        ConfirmationHandler.processingConfirmationResult(
            result = result,
            payment = paymentDto,
            canceledConfirmationCallback = canceledConfirmationCallback,
            errorConfirmationCallback = errorConfirmationCallback
        ) {
            Log.d("Log1", "Сработал колбэк processingConfirmationResult, paymentDto: $paymentDto")

            val paymentStatusRequest = PaymentStatusRequest(
                paymentId = paymentDto.paymentId,
                shopId = _shopId,
                secretKey = _secretKey
            )

            val paymentStatusManager = PaymentStatusManager()
            paymentStatusManager.paymentStatusRequest(
                paymentStatusRequest = paymentStatusRequest,
                applicationId = paymentDto.applicationId,
                successfulStatusResponse = successfulStatusResponse,
                yookassaErrorStatusCallback = yookassaErrorStatusCallback,
                errorStatusCallback = errorStatusCallback,
                failureStatusCallback = failureStatusCallback
            ) {
                rewritePaymentList(it)
            }
        }
    }

    fun getProductList(): List<Product> {
        return productList
    }

    fun getProductMap(): Map<String, Product> {
        return productMap
    }

    fun getPaymentList(): List<Payment> {
        return paymentList
    }

    fun cancelPayment(
        payment: Payment,
        cancelPaymentCallback: CancelPaymentCallback
    ) {
        Log.d("Log1", "Сработала cancelPayment() в Bimos")

        if (payment.status == "waiting_for_capture") {
            val cancelPaymentManager = CancelPaymentManager()

            cancelPaymentManager.cancelPaymentRequest(
                payment = payment,
                cancellationPaymentCallback = cancelPaymentCallback::onCancellationPayment,
                successfulCancelCallback = cancelPaymentCallback::onSuccessfulResponse,
                yookassaErrorCancelCallback = cancelPaymentCallback::onYookassaErrorResponse,
                errorCancelCallback = cancelPaymentCallback::onErrorResponse,
                failureCancelCallback = cancelPaymentCallback::onFailure
            ) {
                val productsAndPaymentsLists = ProductsAndPaymentsMapper.getProductsAndPaymentsLists(it)
                initProductsAndPaymentsLists(productsAndPaymentsLists)
            }
        }
    }

    fun recoverSubscription(
        payment: Payment,
        recoverSubscriptionCallback: RecoverSubscriptionCallback
    ) {
        Log.d("Log1", "Сработала recoverSubscription() в Bimos")

        if (payment.status == "forbidden" && payment.type == "subscription") {
            val recoverSubscriptionManager = RecoverSubscriptionManager()

            recoverSubscriptionManager.recoverSubscriptionRequest(
                payment = payment,
                successRecoverSubscriptionCallback = recoverSubscriptionCallback::onSuccessfulResponse,
                errorRecoverSubscriptionCallback = recoverSubscriptionCallback::onErrorResponse,
                failureRecoverSubscriptionCallback = recoverSubscriptionCallback::onFailure
            ) {
                val productsAndPaymentsLists = ProductsAndPaymentsMapper.getProductsAndPaymentsLists(it)
                initProductsAndPaymentsLists(productsAndPaymentsLists)
            }
        }
    }

    fun cancelSubscription(
        payment: Payment,
        cancelSubscriptionCallback: CancelSubscriptionCallback
    ) {
        Log.d("Log1", "Сработала cancelSubscription() в Bimos")

        if (payment.status == "succeeded" && payment.type == "subscription") {
            val cancelSubscriptionManager = CancelSubscriptionManager()

            cancelSubscriptionManager.cancelSubscriptionRequest(
                payment = payment,
                successCancelSubscriptionCallback = cancelSubscriptionCallback::onSuccessfulResponse,
                errorCancelSubscriptionCallback = cancelSubscriptionCallback::onErrorResponse,
                failureCancelSubscriptionCallback = cancelSubscriptionCallback::onFailure
            ) {
                val productsAndPaymentsLists = ProductsAndPaymentsMapper.getProductsAndPaymentsLists(it)
                initProductsAndPaymentsLists(productsAndPaymentsLists)
            }
        }
    }

    private fun addPaymentToPaymentList(payment: Payment) {
        Log.d("Log1", "addPaymentToPaymentList до прибавления: $paymentList")
        paymentList.add(payment)
        Log.d("Log1", "addPaymentToPaymentList после прибавления: $paymentList")
    }

    private fun rewritePaymentList(payment: Payment) {

        val newList = RewritePaymentManager.rewritePayment(payment, paymentList)
        paymentList.clear()
        paymentList.addAll(newList)
        Log.d("Log1", "rewritePaymentList: $paymentList")
    }

    fun getUserPayments(
        userUid: String,
        getUserPaymentsCallback: GetUserPaymentsCallback
    ) {

        val userPaymentsManager = UserPaymentsManager()
        userPaymentsManager.userPaymentsRequest(
            userUid = userUid,
            applicationId = _applicationId,
            successCallback = getUserPaymentsCallback::onSuccessfulResponse,
            errorCallback = getUserPaymentsCallback::onErrorResponse,
            failureCallback = getUserPaymentsCallback::onFailure
        ) {
            paymentList.clear()
            paymentList.addAll(it)
            Log.d("Log1", "paymentList в getUserPayments(): $paymentList")
        }
    }

    fun getApplicationProducts(getApplicationProductsCallback: GetApplicationProductsCallback) {

        val applicationProductsManager = ApplicationProductsManager()
        applicationProductsManager.applicationProductsRequest(
            applicationId = _applicationId,
            successCallback = getApplicationProductsCallback::onSuccessfulResponse,
            errorCallback = getApplicationProductsCallback::onErrorResponse,
            failureCallback = getApplicationProductsCallback::onFailure
        ) {
            val productsLists = ProductsMapper.getProductsLists(it)
            initProductsLists(productsLists)
        }
    }

    private fun initProductsLists(productsLists: ProductsLists) {
        Log.d("Log1", "Запустилась initProductsLists(), productsLists: $productsLists")
        productList.clear()
        productList.addAll(productsLists.productList)
        productMap.clear()
        productMap.putAll(productsLists.productMap)

        Log.d("Log1", "productList: $productList")
        Log.d("Log1", "productMap: $productMap")
        Log.d("Log1", "paymentList: $paymentList")
    }

    fun getProductsAndPayments(
        userUid: String,
        productsAndPaymentsCallback: ProductsAndPaymentsCallback
    ) {
        Log.d("Log1", "Сработала getProductAndPaymentList() в Bimos")

        val productsAndPaymentsManager = ProductsAndPaymentsManager()

        val paymentsListRequest = PaymentsListRequest(
            userUid = userUid,
            applicationId = _applicationId
        )

        productsAndPaymentsManager.productsAndPaymentsRequest(
            paymentsListRequest = paymentsListRequest,
            successCallback = productsAndPaymentsCallback::onSuccessfulResponse,
            errorCallback = productsAndPaymentsCallback::onErrorResponse,
            failureCallback = productsAndPaymentsCallback::onFailure
        ) {
            val productsAndPaymentsLists = ProductsAndPaymentsMapper.getProductsAndPaymentsLists(it)
            initProductsAndPaymentsLists(productsAndPaymentsLists)
        }
    }

    fun createPayment(
        userUid: String,
        customerId: String,
        product: Product?,
        callbackCreatePayment: CreatePaymentCallback,
        callbackConfirmationResult: ConfirmationPaymentCallback,
        callbackStatusPayment: GetPaymentStatusCallback
    ) {
        if (product != null) {
            if (!startTokenize) {
                startTokenize = true
                Log.d("Log1", "startTokenizePurchase product: $product")

                Log.d("Log1", "Передается product: $product")
                productDto = product
                Log.d("Log1", "productDto: $productDto")
                Log.d("Log1", "productList: $productList")
                Log.d("Log1", "productMap: $productMap")
                Log.d("Log1", "paymentList: $paymentList")

                _userUid = userUid

                creatingPaymentCallback = callbackCreatePayment::onCreatingPayment
                successfulCreateCallback = callbackCreatePayment::onSuccessfulResponse
                yookassaErrorCreateCallback = callbackCreatePayment::onYookassaErrorResponse
                errorCreateCallback = callbackCreatePayment::onErrorResponse
                failureCreateCallback = callbackCreatePayment::onFailure

                canceledConfirmationCallback = callbackConfirmationResult::onCanceledConfirmationResult
                errorConfirmationCallback = callbackConfirmationResult::onErrorConfirmationResult

                successfulStatusResponse = callbackStatusPayment::onSuccessfulResponse
                yookassaErrorStatusCallback = callbackStatusPayment::onYookassaErrorResponse
                errorStatusCallback = callbackStatusPayment::onErrorResponse
                failureStatusCallback = callbackStatusPayment::onFailure

                val tokenize = Tokenize()
                val tokenizeParameters = TokenizeParameters(
                    customerId = customerId,
                    product = product,
                    context = activity
                )

                val intent = tokenize.launchTokenize(
                    tokenizeParameters = tokenizeParameters,
                    clientApplicationKey = _clientApplicationKey,
                    shopId = _shopId,
                    colorScheme = _colorScheme
                )

                tokenizeLauncher.launch(intent)
            }
        } else {
            Log.d("Log1", "product: null")
        }
    }

    private fun startConfirmation(payment: Payment) {
        val intent = Checkout.createConfirmationIntent(
            context = activity,
            confirmationUrl = payment.confirmationUrl,
            paymentMethodType = _paymentMethodType,
            clientApplicationKey = _clientApplicationKey,
            shopId = "$_shopId",
            colorScheme = ColorScheme(Color.parseColor(_colorScheme)),
            testParameters = TestParameters(showLogs = true)
        )

        confirmationLauncher.launch(intent)
    }

    fun init(
        shopId: Int,
        secretKey: String,
        clientApplicationKey: String,
        applicationId: String,
        colorScheme: String,
        applicationName: String
    ) {
        Log.d("Log1", "Сработала init() в Bimos")

        _applicationId = applicationId
        _shopId = shopId
        _secretKey = secretKey
        _clientApplicationKey = clientApplicationKey
        _colorScheme = colorScheme
        _applicationName = applicationName
    }

    private fun initProductsAndPaymentsLists(productsAndPaymentsLists: ProductsAndPaymentsLists) {
        Log.d("Log1", "Запустилась initProductsAndPaymentsLists(), " +
                "productsAndPaymentsLists: $productsAndPaymentsLists")
        productList.clear()
        productList.addAll(productsAndPaymentsLists.productList)
        productMap.clear()
        productMap.putAll(productsAndPaymentsLists.productMap)
        paymentList.clear()
        paymentList.addAll(productsAndPaymentsLists.paymentList)

        Log.d("Log1", "productList: $productList")
        Log.d("Log1", "productMap: $productMap")
        Log.d("Log1", "paymentList: $paymentList")
    }

    private fun getInitParametersClass(): InitParameters {
        return InitParameters(
            userUid = _userUid,
            applicationId = _applicationId,
            shopId = _shopId,
            secretKey = _secretKey,
            clientApplicationKey = _clientApplicationKey,
            colorScheme = _colorScheme,
            applicationName = _applicationName
        )
    }
}