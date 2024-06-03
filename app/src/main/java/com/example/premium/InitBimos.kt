package com.example.premium

import com.example.bimos.Bimos

object InitBimos {

    fun initialization(bimos: Bimos) {

        val test = true

        if(test) {
            bimos.init(

                // Тестовые ключи
                shopId = 257666,
                secretKey = "test_vsRksy4MubYrHTap8NL1pUSuF9VeI1K_03Y9hSt6f5E",
                clientApplicationKey = "test_MjU3NjY2V7-f7duA9bdfOLR9Y0twiVxUA9bv_UyqxZ8",
                applicationId = "ae8b1e91-b318-41a0-a8f9-ef090058af86",
                colorScheme = "#8573B6",
                applicationName = "PremiumExample"
            )
        } else {
            bimos.init(

                // Реальные ключи
                shopId = 254820,
                secretKey = "live_YeX1KZVv_Mfcvd8yIlVYDjYKwpdgTRwOQkPa1Wk586E",
                clientApplicationKey = "live_MjU0ODIwYwikIkmGV8IdJ1W1BnFkpUPDfuvnCG1UIGE",
                applicationId = "ae8b1e91-b318-41a0-a8f9-ef090058af86",
                colorScheme = "#8573B6",
                applicationName = "PremiumExample"
            )
        }
    }
}