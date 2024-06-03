package com.example.premium

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.bimos.Bimos
import com.example.bimos.payments.*
import com.example.bimos.payments.models.CancelPaymentResponse
import com.example.bimos.productsAndPayments.ProductsAndPaymentsCallback
import com.example.bimos.productsAndPayments.models.ProductsAndPayments
import com.example.bimos.payments.models.Payment
import com.example.bimos.payments.models.PaymentResponse
import com.example.bimos.payments.models.YookassaError
import com.example.bimos.products.GetApplicationProductsCallback
import com.example.bimos.products.models.Product
import com.example.premium.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bimos: Bimos
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayoutPremium, PremiumFragment.newInstance())
            .commit()

//        bimos = Bimos(this)
//        bimos.init(
//            shopId = 257666,
//            secretKey = "test_vsRksy4MubYrHTap8NL1pUSuF9VeI1K_03Y9hSt6f5E",
//            clientApplicationKey = "test_MjU3NjY2V7-f7duA9bdfOLR9Y0twiVxUA9bv_UyqxZ8",
//            applicationId = "ae8b1e91-b318-41a0-a8f9-ef090058af86",
//            colorScheme = "#8573B6",
//            applicationName = "PremiumExample"
//        )
////        InitBimos.initialization(bimos)
//        userEmail = "i.drobush@rambler.ru"
//
////        getUserPayments()
////        getApplicationProducts()
//        getProductsAndPayments()

        binding.cv50.setBackgroundResource(R.drawable.cardview_border)

        binding.cardView8.setOnClickListener {
            createPayment("subscription_one_month")
        }

        binding.cardView9.setOnClickListener {
            createPayment("subscription_three_month")
        }

        binding.cardView10.setOnClickListener {
            createPayment("purchase")
        }

        binding.button2.setOnClickListener {

        }

        binding.cancelSubs.setOnClickListener {

            val paymentList = bimos.getPaymentList()
            cancelDialog(paymentList[0])
        }

        binding.recoverSubs.setOnClickListener {

            showUploadingData()

            val paymentList = bimos.getPaymentList()
            recoverSubscription(paymentList[0])
        }
    }

    private fun getProductsAndPayments() {

        showUploadingData()

        bimos.getProductsAndPayments(userEmail, object : ProductsAndPaymentsCallback {
            override fun onSuccessfulResponse(productsAndPayments: ProductsAndPayments) {
                // Успешный ответ
                Log.d("Log1", "Успешный ответ: $productsAndPayments")
                showProductsAndPayments(productsAndPayments)
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                // Ошибка в ответе
                Log.d("Log1", "Ошибка в ответе: код $statusCode, сообщение: $statusMessage, тело ошибки: $errorBody")
                showError("Ошибка в ответе")
            }

            override fun onFailure(call: Call<ProductsAndPayments>, t: Throwable) {
                // Ошибка выполнения запроса (проблемы с сетью или сервером)
                Log.d("Log1", "Ошибка выполнения запроса: ${t.message}")
                showError("Ошибка выполнения запроса")
            }
        })
    }

    private fun showProductsAndPayments(productsAndPayments: ProductsAndPayments) {

        val paymentsList = productsAndPayments.payments
        val productList = productsAndPayments.products

        if(paymentsList.isNotEmpty()) {

            showPayments(paymentsList[0])
        } else {
            if(productList.isNotEmpty()) {
                Log.d("Log1", "Открывается список: $productList")
                binding.linearProgress.visibility = View.GONE
                binding.layoutPremium.visibility = View.VISIBLE

                showProducts(productList)
            } else {
                showError("Нет продуктов и покупок")
            }
        }
    }

    private fun showError(text: String) {
        binding.textView7.visibility = View.VISIBLE
        binding.textView7.text = text
        binding.linearProgress.visibility = View.GONE
        binding.layoutPremium.visibility = View.GONE
        Log.d("Log1", text)
    }

    private fun cancelDialog(payment: Payment) {

        Log.d("Log1", "Диалог отмена платежа")

        val dialog = MaterialAlertDialogBuilder(this, R.style.roundedDialog)
            .setView(R.layout.dialog_standard)
            .show()

        dialog?.window?.setDimAmount(0.5f)

        val title = "Подтверждение отмены"
        dialog!!.findViewById<TextView>(R.id.textViewDialogTitle)!!.text = title

        val text = if(payment.type == "purchase") {
            "Вы действительно хотите отменить покупку?"
        } else {
            "Вы действительно хотите отменить подписку?"
        }
        dialog.findViewById<TextView>(R.id.textViewDialogText)!!.text = text

        dialog.findViewById<TextView>(R.id.textViewDialogCancel)!!.setOnClickListener {

            dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.textViewDialogConfirm)!!.setOnClickListener {

            cancelPayment(payment)
            dialog.dismiss()
        }
    }

    private fun showError() {
        val productsAndPayments = ProductsAndPayments(
            bimos.getProductList(),
            bimos.getPaymentList()
        )
        showProductsAndPayments(productsAndPayments)
    }

    private fun recoverSubscription(payment: Payment) {
        Log.d("Log1", "Запустилась recoverSubscription")

        bimos.recoverSubscription(payment, object : RecoverSubscriptionCallback {
            override fun onSuccessfulResponse(productsAndPayments: ProductsAndPayments) {
                Log.d("Log1", "RecoverSubscriptionCallback.onSuccessfulResponse: $productsAndPayments")
                showProductsAndPayments(productsAndPayments)
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                Log.d("Log1", "RecoverSubscriptionCallback.onErrorResponse: код $statusCode, " +
                        "сообщение: $statusMessage, тело ошибки: $errorBody")
                showError()
            }

            override fun onFailure(call: Call<ProductsAndPayments>, t: Throwable) {
                Log.d("Log1", "RecoverSubscriptionCallback.onFailure: ${t.message}")
                showError()
            }
        })
    }

    private fun cancelPayment(payment: Payment) {

        Log.d("Log1", "Запустилась отмена платежа")

        showUploadingData()

        when(payment.status) {
            "waiting_for_capture" -> {
                cancelPaymentWaitingForCapture(payment)
            }
            "succeeded" -> {
                if (payment.type == "subscription") {
                    cancelSubscription(payment)
                }
            }
        }
    }

    private fun cancelPaymentWaitingForCapture(payment: Payment) {
        Log.d("Log1", "Запустилась cancelPayment")

        bimos.cancelPayment(payment, object : CancelPaymentCallback {
            override fun onCancellationPayment() {
                Log.d("Log1", "CancelPaymentCallback.onCancellationPayment")
            }

            override fun onSuccessfulResponse(productsAndPayments: ProductsAndPayments) {
                Log.d("Log1", "CancelPaymentCallback.onSuccessfulResponse: $productsAndPayments")
                showProductsAndPayments(productsAndPayments)
            }

            override fun onYookassaErrorResponse(yookassaError: YookassaError) {
                Log.d("Log1", "CancelPaymentCallback.onYookassaErrorResponse: $yookassaError")
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                Log.d("Log1", "CancelPaymentCallback.onErrorResponse: код $statusCode, " +
                        "сообщение: $statusMessage, тело ошибки: $errorBody")
                showError()
            }

            override fun onFailure(call: Call<CancelPaymentResponse>, t: Throwable) {
                Log.d("Log1", "CancelPaymentCallback.onFailure: ${t.message}")
                showError()
            }
        })
    }

    private fun cancelSubscription(payment: Payment) {
        Log.d("Log1", "Запустилась cancelSubscription()")

        bimos.cancelSubscription(payment, object : CancelSubscriptionCallback {
            override fun onSuccessfulResponse(productsAndPayments: ProductsAndPayments) {
                Log.d("Log1", "CancelSubscriptionCallback.onSuccessfulResponse: $productsAndPayments")
                showProductsAndPayments(productsAndPayments)
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                Log.d("Log1", "CancelSubscriptionCallback.onErrorResponse: код $statusCode, " +
                        "сообщение: $statusMessage, тело ошибки: $errorBody")
                showError()
            }

            override fun onFailure(call: Call<ProductsAndPayments>, t: Throwable) {
                Log.d("Log1", "CancelSubscriptionCallback.onFailure: ${t.message}")
                showError()
            }
        })
    }

    private fun createPayment(id: String) {

        val productMap = bimos.getProductMap()
        val product = productMap[id]

        bimos.createPayment(userEmail, userEmail, product, object : CreatePaymentCallback {
            override fun onCreatingPayment() {
                Log.d("Log1", "CreatePaymentCallback.onCreatingPayment")
            }

            override fun onSuccessfulResponse(payment: Payment) {
                Log.d("Log1", "CreatePaymentCallback.onSuccessfulResponse: $payment")
                when(payment.status) {
                    "succeeded" -> {
                        Log.d("Log1", "onSuccessfulResponse succeeded")
                        showPayments(payment)
                    }
                    "waiting_for_capture" -> {
                        Log.d("Log1", "onSuccessfulResponse waiting_for_capture")
                        showPayments(payment)
                    }
                    "canceled" -> {
                        Log.d("Log1", "onSuccessfulResponse canceled")
                        val cancellationReason = payment.cancellationReason
                        Log.d("Log1", "cancellationReason: $cancellationReason")
                    }
                }
            }

            override fun onYookassaErrorResponse(yookassaError: YookassaError) {
                Log.d("Log1", "CreatePaymentCallback.onYookassaErrorResponse: $yookassaError")
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                Log.d("Log1", "CreatePaymentCallback.onErrorResponse: код $statusCode, " +
                        "сообщение: $statusMessage, тело ошибки: $errorBody")
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                Log.d("Log1", "CreatePaymentCallback.onFailure: ${t.message}")
            }

        }, object : ConfirmationPaymentCallback {
            override fun onCanceledConfirmationResult() {
                Log.d("Log1", "ConfirmCallback.onCanceledConfirmationResult")
            }

            override fun onErrorConfirmationResult(code: Int?, description: String?, url: String?) {
                Log.d("Log1", "ConfirmCallback.onErrorConfirmationResult: " +
                        "code: $code, description: $description, url: $url")
            }

        }, object : GetPaymentStatusCallback {
            override fun onSuccessfulResponse(payment: Payment) {
                Log.d("Log1", "GetPaymentStatusCallback.onSuccessfulResponse: $payment")
                when(payment.status) {
                    "succeeded" -> {
                        Log.d("Log1", "onSuccessfulResponse succeeded")
                        showPayments(payment)
                    }
                    "waiting_for_capture" -> {
                        Log.d("Log1", "onSuccessfulResponse waiting_for_capture")
                        showPayments(payment)
                    }
                    "canceled" -> {
                        Log.d("Log1", "onSuccessfulResponse canceled")
                        val cancellationReason = payment.cancellationReason
                        Log.d("Log1", "cancellationReason: $cancellationReason")
                    }
                    "pending" -> {
                        Log.d("Log1", "onSuccessfulResponse pending")
                    }
                }
            }

            override fun onYookassaErrorResponse(yookassaError: YookassaError) {
                Log.d("Log1", "GetPaymentStatusCallback.onYookassaErrorResponse: $yookassaError")
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                Log.d("Log1", "GetPaymentStatusCallback.onErrorResponse: код $statusCode, " +
                        "сообщение: $statusMessage, тело ошибки: $errorBody")
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                Log.d("Log1", "GetPaymentStatusCallback.onFailure: ${t.message}")
            }
        })
    }

    private fun getApplicationProducts() {

        showUploadingData()

        bimos.getApplicationProducts(object : GetApplicationProductsCallback {
            override fun onSuccessfulResponse(products: List<Product>) {
                Log.d("Log1", "GetApplicationProductsCallback.onSuccessfulResponse: $products")
                showProducts(products)
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                Log.d("Log1", "GetApplicationProductsCallback.onErrorResponse: код $statusCode, " +
                        "сообщение: $statusMessage, тело ошибки: $errorBody")
                showProducts(bimos.getProductList())
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.d("Log1", "GetApplicationProductsCallback.onFailure: ${t.message}")
                showProducts(bimos.getProductList())
            }
        })
    }

    private fun getUserPayments() {

        bimos.getUserPayments(userEmail, object : GetUserPaymentsCallback {
            override fun onSuccessfulResponse(payments: List<Payment>) {
                Log.d("Log1", "GetUserPaymentsCallback.onSuccessfulResponse: $payments")
                showPayments(payments[0])
            }

            override fun onErrorResponse(statusCode: Int, statusMessage: String, errorBody: String?) {
                Log.d("Log1", "GetUserPaymentsCallback.onErrorResponse: код $statusCode, " +
                        "сообщение: $statusMessage, тело ошибки: $errorBody")
                val paymentList = bimos.getPaymentList()
                showPayments(paymentList[0])
            }

            override fun onFailure(call: Call<List<Payment>>, t: Throwable) {
                Log.d("Log1", "GetUserPaymentsCallback.onFailure: ${t.message}")
                val paymentList = bimos.getPaymentList()
                showPayments(paymentList[0])
            }
        })
    }

    private fun showUploadingData() {

        Log.d("Log1", "Сработала showUploadingData()")

        binding.linearProgress.visibility = View.VISIBLE
        binding.layoutPremium.visibility = View.GONE
        binding.progressBar10.visibility = View.VISIBLE
        binding.textView73.visibility = View.VISIBLE
        binding.textView122.visibility = View.GONE
        binding.cancelSubs.visibility = View.GONE
        binding.recoverSubs.visibility = View.GONE
        binding.textView7.visibility = View.GONE
    }

    private fun showPayments(payment: Payment) {

        Log.d("Log1", "Сработала showPayments()")

        binding.linearProgress.visibility = View.VISIBLE
        binding.layoutPremium.visibility = View.GONE

        binding.progressBar10.visibility = View.GONE
        binding.textView73.visibility = View.GONE
        binding.textView122.visibility = View.VISIBLE

        when(payment.productId) {
            "subscription_one_month" -> {
                Log.d("Log1", "Сработала subscription_one_month")

                showSubscriptionCondition(payment)

            }
            "subscription_three_month" -> {
                Log.d("Log1", "Сработала subscription_three_month")

                showSubscriptionCondition(payment)
            }
            "purchase" -> {
                Log.d("Log1", "purchase")

                binding.cancelSubs.visibility = View.GONE
                binding.recoverSubs.visibility = View.GONE
                val text = "Вы оформили Премиум навсегда. " +
                        "Вам доступны все функции приложения."
                binding.textView122.text = text

                if(payment.status == "waiting_for_capture") {
                    binding.cancelSubs.visibility = View.VISIBLE
                    binding.cancelSubs.text = "Отменить покупку"
                }
            }
        }
    }

    private fun showSubscriptionCondition(payment: Payment) {

        Log.d("Log1", "Сработала showSubscriptionCondition()")

        if(payment.status == "forbidden") {
            binding.cancelSubs.visibility = View.GONE
            binding.recoverSubs.visibility = View.VISIBLE

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val dateTimeString = dateFormat.format(Date(payment.endOfTermTime))
            val text = "Вы отменили подписку. Доступны все функции приложения до $dateTimeString"
            binding.textView122.text = text
        } else {

            binding.cancelSubs.text = "Отменить подписку"
            binding.cancelSubs.visibility = View.VISIBLE
            binding.recoverSubs.visibility = View.GONE

            val term = when(payment.subscriptionPeriod) {
                1 -> "1 месяц"
                3 -> "3 месяца"
                6 -> "6 месяцев"
                12 -> "12 месяцев"
                else -> "1 месяц"
            }
            val text = "Вы оформили подписку Премиум на $term. " +
                    "Вам доступны все функции приложения."
            binding.textView122.text = text
        }
    }

    private fun showProducts(productList: List<Product>) {

        Log.d("Log1", "Сработала showProducts()")

        for (product in productList) {

            when(product.id){
                "subscription_one_month" -> {
                    binding.textView57.text = product.name
                    val textPrice = "${product.price.toString()} р."
                    binding.textView58.text = textPrice
                    val textPerMonth = "${product.pricePerMonth.toString()} р."
                    binding.textView60.text = textPerMonth

                    if(product.trialPeriod == 0) {
                        binding.textView59.visibility = View.GONE
                    } else {
                        binding.textView59.visibility = View.VISIBLE
                        val days = if (product.trialPeriod == 3) "дня" else "дней"
                        val text = "Бесплатный пробный период ${product.trialPeriod} " + days
                        binding.textView59.text = text
                    }
                }
                "subscription_three_month" -> {
                    binding.textView62.text = product.name
                    val textPrice = "${product.price.toString()} р."
                    binding.textView63.text = textPrice
                    val textPerMonth = "${product.pricePerMonth.toString()} р."
                    binding.textView65.text = textPerMonth

                    if(product.trialPeriod == 0) {
                        binding.textView64.visibility = View.GONE
                    } else {
                        binding.textView64.visibility = View.VISIBLE
                        val days = if (product.trialPeriod == 3) "дня" else "дней"
                        val text = "Бесплатный пробный период ${product.trialPeriod} " + days
                        binding.textView64.text = text
                    }

                    if(product.discount == "") {
                        binding.cardView11.visibility = View.GONE
                    } else {
                        binding.cardView11.visibility = View.VISIBLE
                        val textDiscount = product.discount + "%"
                        binding.textView52.text = textDiscount
                    }
                }
                "purchase" -> {
                    binding.textView6.paintFlags = binding.textView6.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    binding.textView69.visibility = View.GONE
                    binding.textView72.visibility = View.GONE
                    binding.textView70.text = product.name
                    val textPrice = "${product.price.toString()} р."
                    binding.textView71.text = textPrice

                    if(product.trialPeriod == 0) {
                        binding.textView74.visibility = View.GONE
                    } else {
                        binding.textView74.visibility = View.VISIBLE
                        val days = if (product.trialPeriod == 3) "дня" else "дней"
                        val text = "Бесплатный пробный период ${product.trialPeriod} " + days
                        binding.textView74.text = text
                    }

                    if(product.fullPrice == 0) {
                        binding.textView6.visibility = View.GONE
                    } else {
                        binding.textView6.visibility = View.VISIBLE
                        val textFullPrice = "${product.fullPrice} р."
                        binding.textView6.text = textFullPrice
                    }

                    if(product.discount == "") {
                        binding.cv50.visibility = View.GONE
                    } else {
                        binding.cv50.visibility = View.VISIBLE
                        val textDiscount = product.discount + "%"
                        binding.tv52.text = textDiscount
                    }
                }
            }
        }

        binding.linearProgress.visibility = View.GONE
        binding.layoutPremium.visibility = View.VISIBLE
    }

//    private fun bimosLibraryCustomDialog() {
//        val dialog = MaterialAlertDialogBuilder(activity, com.example.bimos.R.style.roundedDialog)
//            .setView(com.example.bimos.R.layout.dialog_standard)
//            .show()
//
//        dialog?.window?.setDimAmount(0.5f)
//
//        val titleAttention = activity.getString(com.example.bimos.R.string.trial_period_sber_attention)
//        dialog!!.findViewById<TextView>(com.example.bimos.R.id.textViewDialogTitle)!!.text = titleAttention
//        val textAttention = activity.getString(com.example.bimos.R.string.text_trial_period_sber_attention)
//        dialog.findViewById<TextView>(com.example.bimos.R.id.textViewDialogText)!!.text = textAttention
//        val textContinue = activity.getString(com.example.bimos.R.string.text_continue)
//        val textViewDialogConfirm = dialog.findViewById<TextView>(com.example.bimos.R.id.textViewDialogConfirm)!!
//        textViewDialogConfirm.text = textContinue
//        textViewDialogConfirm.setTextColor(Color.parseColor(initParameters.colorScheme))
//        val textCancel = activity.getString(com.example.bimos.R.string.cancel)
//        val textViewDialogCancel = dialog.findViewById<TextView>(com.example.bimos.R.id.textViewDialogCancel)!!
//        textViewDialogCancel.text = textCancel
//        textViewDialogCancel.setTextColor(Color.parseColor(initParameters.colorScheme))
//
//        dialog.findViewById<TextView>(com.example.bimos.R.id.textViewDialogCancel)!!.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.findViewById<TextView>(com.example.bimos.R.id.textViewDialogConfirm)!!.setOnClickListener {
//            dialog.dismiss()
//        }
//    }
}