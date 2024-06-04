package com.example.bimos.payments

import android.util.Log
import com.example.bimos.payments.models.Payment

object RewritePaymentManager {

    fun rewritePayment(newPayment: Payment, paymentList: List<Payment>): List<Payment> {

        val newList: MutableList<Payment> = mutableListOf()

        var changed = false
        Log.d("Log1", "В начале newPayment: $newPayment, paymentList: $paymentList, changed: $changed")

        for (payment in paymentList) {
            if (payment.paymentId == newPayment.paymentId) {
                newList.add(newPayment)
                changed = true
            } else {
                newList.add(payment)
            }
        }

        if (!changed) {
            newList.add(newPayment)
        }

        Log.d("Log1", "В конце newList: $newList, changed: $changed")

        return newList
    }
}