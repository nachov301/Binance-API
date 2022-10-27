package app.iggy.binancechallenge.network.websocketModel

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BidsAndAsks(
    val asks: List<List<String>>? = null,
    val bids: List<List<String>>? = null
)