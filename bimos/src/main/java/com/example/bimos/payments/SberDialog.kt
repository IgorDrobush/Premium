package com.example.bimos.payments

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bimos.R
import com.example.bimos.payments.models.Payment
import com.example.bimos.payments.models.PaymentResponse
import com.example.bimos.products.models.Product
import com.example.bimos.payments.models.YookassaError
import com.example.bimos.payments.models.InitParameters
import retrofit2.Call

object SberDialog {

    fun showDialog(
        activity: AppCompatActivity,
        product: Product,
        initParameters: InitParameters,
        paymentToken: String,
        creatingPaymentCallback: () -> Unit,
        successfulCreateCallback: (Payment) -> Unit,
        yookassaErrorCreateCallback: (YookassaError) -> Unit,
        errorCreateCallback: (Int, String, String?) -> Unit,
        failureCreateCallback: (Call<PaymentResponse>, Throwable) -> Unit,
        callback: (Payment) -> Unit,
        callbackAddPayment: (Payment) -> Unit
    ) {
        Log.d("Log1", "Сработала SberDialog.showDialog() в библиотеке Bimos")

        val titleAttention = activity.getString(R.string.trial_period_sber_attention)
        val textAttention = activity.getString(R.string.text_trial_period_sber_attention)
        val textContinue = activity.getString(R.string.text_continue)
        val textCancel = activity.getString(R.string.cancel)


        BimosAlertDialog.showDialog(
            activity = activity,
            color = initParameters.colorScheme,
            title = titleAttention,
            message = textAttention,
            positiveText = textContinue,
            negativeText = textCancel
        ) {
            val newProduct = Product(
                id = product.id,
                applicationId = product.applicationId,
                type = product.type,
                subscriptionPeriod = product.subscriptionPeriod,
                name = product.name,
                description = product.description,
                price = product.price,
                pricePerMonth = product.pricePerMonth,
                number = product.number,
                currency = product.currency,
                discount = product.discount,
                fullPrice = product.fullPrice,
                applyNewPrice = product.applyNewPrice,
                trialPeriod = 5,
                promo = product.promo,
                tag = product.tag
            )
            val createPaymentManager = CreatePaymentManager()
            createPaymentManager.createPaymentRequest(
                product = newProduct,
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
}