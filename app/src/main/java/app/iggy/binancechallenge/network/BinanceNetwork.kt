package app.iggy.binancechallenge.network

import app.iggy.binancechallenge.network.listModel.JsonClassListModel
import retrofit2.Call
import retrofit2.http.GET


const val BASE_URL = "https://api.binance.com/api/v3/"

interface BinanceNetwork {
    @GET("ticker/24hr?symbols=[\"BTCUSDT\",\"ETHBUSD\",\"BNBBUSD\",\"LUNABUSD\"," +
            "\"SOLBUSD\",\"LTCBUSD\",\"MATICBUSD\",\"AVAXBUSD\",\"XRPBUSD\",\"BUSDUSDT\"]")
    fun getInfo(): Call<JsonClassListModel>
}