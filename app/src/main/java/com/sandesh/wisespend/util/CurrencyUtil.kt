package com.sandesh.wisespend.util

import com.sandesh.wisespend.data.model.CurrencyItem

object CurrencyUtils {

    val currencies = listOf(
        CurrencyItem(
            country = "Nepal",
            currency = "Nepalese Rupee",
            code = "NPR",
            symbol = "रू",
            flag = "🇳🇵",
            countryCode = "np"
        ),
        CurrencyItem(
            country = "India",
            currency = "Indian Rupee",
            code = "INR",
            symbol = "₹",
            flag = "🇮🇳",
            countryCode = "in"
        ),
        CurrencyItem(
            country = "United States",
            currency = "US Dollar",
            code = "USD",
            symbol = "$",
            flag = "🇺🇸",
            countryCode = "us"
        ),
        CurrencyItem(
            country = "United Kingdom",
            currency = "Pound Sterling",
            code = "GBP",
            symbol = "£",
            flag = "🇬🇧",
            countryCode = "gb"
        )
    )

    fun getCurrencyByCode(code: String): CurrencyItem {
        return currencies.find { it.code == code }
            ?: currencies.first { it.code == "NPR" }
    }
}