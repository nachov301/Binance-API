package app.iggy.binancechallenge.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import app.iggy.binancechallenge.network.BASE_URL
import app.iggy.binancechallenge.network.BinanceNetwork
import app.iggy.binancechallenge.network.listModel.JsonClassListModel
import app.iggy.binancechallenge.view.MainActivity
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URI
import javax.net.ssl.SSLSocketFactory

class MyRepository(val application: Application) {

    private val TAG = "MyRepository"
    private lateinit var webSocketClient: WebSocketClient
    private val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory

    //+++++++++++++ MutableLiveData +++++++++++++
    val response = MutableLiveData<JsonClassListModel>()
    val showProgress = MutableLiveData<Boolean>()
    val webSocketMessage = MutableLiveData<String>()

    fun getList() {
        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(BinanceNetwork::class.java)

        showProgress.value = true

        service.getInfo().enqueue(object : Callback<JsonClassListModel> {
            override fun onResponse(
                call: Call<JsonClassListModel>,
                resp: Response<JsonClassListModel>
            ) {
                response.value = resp.body()
                showProgress.value = false
            }

            override fun onFailure(call: Call<JsonClassListModel>, t: Throwable) {
                Toast.makeText(application, "Error while accessing the API", Toast.LENGTH_SHORT)
                    .show()
                showProgress.value = false
            }

        })
    }

    fun initWebSocket(symbol: String) {
        val WEB_SOCKET_URL = "wss://stream.binance.com:9443/ws/${symbol}@depth5"
        val binanceUri: URI? = URI(WEB_SOCKET_URL)

        subscribeToWebSocket(binanceUri, symbol)

        //web socket
        webSocketClient.setSocketFactory(socketFactory)
        webSocketClient.connect()
    }

    fun closeWebSocket(){
        webSocketClient.close()
    }

    private fun subscribeToWebSocket(binanceUri: URI?, symbol: String) {
        webSocketClient = object : WebSocketClient(binanceUri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "onOpen: starts...")
                webSocketClient.send(
                    "{\n" +
                            "    \"method\": \"SUBSCRIBE\",\n" +
                            "    \"params\": [ \"${symbol}@aggTrade\", \"${symbol}@depth\"],\n" +
                            "    \"id\": 1\n" +
                            "}"
                )
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "onMessage: gets called...")
                webSocketMessage.postValue(message)//we use post value for asynchronous calls
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "onClose: starts...")
                webSocketClient.send(
                    "{\n" +
                            "    \"type\": \"unsubscribe\",\n" +
                            "    \"channels\": [\"ticker\"]\n" +
                            "}"
                )
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "onError: ${ex?.message}")
            }

        }
    }

}