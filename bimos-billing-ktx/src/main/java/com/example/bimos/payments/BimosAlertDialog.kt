package com.example.bimos.payments

import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

object BimosAlertDialog {

    fun showDialog(
        activity: AppCompatActivity,
        color: String,
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        callback: () -> Unit
    ) {
        val builder = AlertDialog.Builder(activity)

        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(positiveText) { _, _ ->
            callback()
        }

        builder.setNegativeButton(negativeText) { _, _ ->

        }

        val dialog: AlertDialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor(color))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor(color))
    }
}