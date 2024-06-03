package com.example.bimos.payments

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import com.example.bimos.payments.models.Payment
import ru.yoomoney.sdk.kassa.payments.Checkout

object ConfirmationHandler {

    fun processingConfirmationResult(
        result: ActivityResult,
        payment: Payment,
        canceledConfirmationCallback: () -> Unit,
        errorConfirmationCallback: (code: Int?, description: String?, url: String?) -> Unit,
        callback: () -> Unit
    ) {
        Log.d("Log1", "Пришел ответ в объекте ConfirmationHandler")

        when (result.resultCode) {
            Activity.RESULT_OK -> {

                // При оплате через Сбер этот код срабатывает даже если пользователь нажимает "Отмена"
                // при подтверждении.
                // RESULT_CANCELED будет только если пользователь нажмет "Назад"
                // при открытии приложения Сбера (поле ввода пароля)
                Log.d("Log1", "Подтверждение прошло успешно в ConfirmationHandler")
                callback()
            }
            Activity.RESULT_CANCELED -> {

                canceledConfirmationCallback()
                Log.d("Log1", "Подтверждение отменено  в ConfirmationHandler")

                // Удаление не подтвержденного платежа, чтобы не висел просто так на сервере
                val deletePaymentManager = DeletePaymentManager()
                deletePaymentManager.deletePaymentRequest(payment)
            }
            Checkout.RESULT_ERROR -> {

                try {
                    Log.d("Log1", "Произошла ошибка подтверждения  в ConfirmationHandler ${result.data}")
                    val code = result.data?.getIntExtra(Checkout.EXTRA_ERROR_CODE, 0)
                    val description = result.data?.getStringExtra(Checkout.EXTRA_ERROR_DESCRIPTION)
                    val url = result.data?.getStringExtra(Checkout.EXTRA_ERROR_FAILING_URL)
                    errorConfirmationCallback(code, description, url)
                } catch (e: Exception) {
                    Log.d("Log1", "Произошла ошибка: ${e.message}")
                }

                // Удаление не подтвержденного платежа, чтобы не висел просто так на сервере
                val deletePaymentManager = DeletePaymentManager()
                deletePaymentManager.deletePaymentRequest(payment)
            }
        }
    }
}