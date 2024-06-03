package com.example.bimos.payments.tokenize

import android.content.Intent
import android.graphics.Color
import com.example.bimos.payments.tokenize.models.TokenizeParameters
import ru.yoomoney.sdk.kassa.payments.Checkout
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.*
import ru.yoomoney.sdk.kassa.payments.ui.color.ColorScheme
import java.math.BigDecimal
import java.util.*

class Tokenize {

        fun launchTokenize(
            tokenizeParameters: TokenizeParameters,
            clientApplicationKey: String,
            shopId: Int,
            colorScheme: String
        ): Intent {

        val paymentMethodTypes = setOf(
            PaymentMethodType.BANK_CARD,
            PaymentMethodType.SBERBANK,
        )

        val uiParameters = UiParameters(
            showLogo = true,
            colorScheme = ColorScheme(Color.parseColor(colorScheme))
        )

        val paymentParameters = PaymentParameters(
            amount = Amount(
                value = BigDecimal.valueOf(tokenizeParameters.product.price.toDouble()),
                currency = Currency.getInstance("RUB")
            ),
            title = tokenizeParameters.product.name,
            subtitle = tokenizeParameters.product.description,
            clientApplicationKey = clientApplicationKey,
            shopId = "$shopId",
            savePaymentMethod = if (tokenizeParameters.product.type == "purchase") {
                SavePaymentMethod.OFF
            } else {
                SavePaymentMethod.ON
                   },
            paymentMethodTypes = paymentMethodTypes,
            userPhoneNumber = "",
            customerId = tokenizeParameters.customerId
        )

        return Checkout.createTokenizeIntent(
            tokenizeParameters.context,
            paymentParameters,
            TestParameters(showLogs = true),
            uiParameters
        )
    }
}