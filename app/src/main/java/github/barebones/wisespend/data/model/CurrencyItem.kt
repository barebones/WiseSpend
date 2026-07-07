package github.barebones.wisespend.data.model

data class CurrencyItem(
    val country: String,
    val currency: String,
    val code: String,
    val symbol: String,
    val flag: String,
    val countryCode: String
)