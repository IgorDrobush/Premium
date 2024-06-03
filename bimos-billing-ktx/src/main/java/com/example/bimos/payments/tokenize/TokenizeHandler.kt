package com.example.bimos.payments.tokenize

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import com.example.bimos.payments.models.Payment
import com.example.bimos.payments.models.PaymentResponse
import com.example.bimos.products.models.Product
import com.example.bimos.payments.models.YookassaError
import com.example.bimos.payments.CreatePaymentManager
import com.example.bimos.payments.SberDialog
import com.example.bimos.payments.models.InitParameters
import retrofit2.Call
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType

object TokenizeHandler {

    fun processingTokenizeResult(
        result: ActivityResult,
        product: Product,
        initParameters: InitParameters,
        activity: AppCompatActivity,
        creatingPaymentCallback: () -> Unit,
        successfulCreateCallback: (Payment) -> Unit,
        yookassaErrorCreateCallback: (YookassaError) -> Unit,
        errorCreateCallback: (Int, String, String?) -> Unit,
        failureCreateCallback: (Call<PaymentResponse>, Throwable) -> Unit,
        callback: (Payment) -> Unit,
        callbackAddPayment: (Payment) -> Unit
    ) {
        Log.d("Log1", "Пришел ответ в объекте TokenizeHandler")
        // Обработка результата здесь
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("Log1", "Токенизация успешная TokenizeHandler")
            val resultData = result.data?.let { intent -> Checkout.createTokenizationResult(intent) }
            val paymentToken = resultData?.paymentToken
            val paymentMethodType = resultData?.paymentMethodType
            Log.d("Log1", "paymentToken в TokenizeHandler: $paymentToken")
            Log.d("Log1", "paymentMethodType в TokenizeHandler: $paymentMethodType")

            if (paymentToken != null) {
                // При повторном запросе product.trialPeriod почему-то = 5, хотя, на самом деле 7
                if (product.trialPeriod > 5 && paymentMethodType == PaymentMethodType.SBERBANK) {
                    SberDialog.showDialog(
                        activity = activity,
                        product = product,
                        initParameters = initParameters,
                        paymentToken = paymentToken,
                        creatingPaymentCallback = creatingPaymentCallback,
                        successfulCreateCallback = successfulCreateCallback,
                        yookassaErrorCreateCallback = yookassaErrorCreateCallback,
                        errorCreateCallback = errorCreateCallback,
                        failureCreateCallback = failureCreateCallback, { pendingPayment ->
                        callback(pendingPayment)
                        }, {
                            callbackAddPayment(it)
                        }
                    )
                } else {
                    val createPaymentManager = CreatePaymentManager()
                    createPaymentManager.createPaymentRequest(
                        product = product,
                        initParameters = initParameters,
                        paymentToken = paymentToken,
                        creatingPaymentCallback = creatingPaymentCallback,
                        successfulCreateCallback = successfulCreateCallback,
                        yookassaErrorCreateCallback = yookassaErrorCreateCallback,
                        errorCreateCallback = errorCreateCallback,
                        failureCreateCallback = failureCreateCallback, { pendingPayment ->
                        callback(pendingPayment)
                        }, {
                            callbackAddPayment(it)
                        }
                    )
                }
            }
        } else {
            Log.d("Log1", "Отмена токенизации в TokenizeHandler")
        }
    }
}
