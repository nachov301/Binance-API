package app.iggy.binancechallenge.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import app.iggy.binancechallenge.network.listModel.JsonClassListModel
import app.iggy.binancechallenge.repository.MyRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MyRepository(application)
    val response : LiveData<JsonClassListModel>
    val showProgress : LiveData<Boolean>
    val webSocketMessage: LiveData<String>

    init {
        this.showProgress = repository.showProgress
        this.response = repository.response
        this.webSocketMessage = repository.webSocketMessage
    }

    fun getInfo(){
        repository.getList()
    }

    fun subscribeToWebSocket(symbol: String){
        repository.initWebSocket(symbol)
    }

    fun closeWebSocket(){
        repository.closeWebSocket()
    }
}