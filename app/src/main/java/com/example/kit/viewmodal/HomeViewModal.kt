package com.example.kit.viewmodal

import android.os.Build
import android.util.Log
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kit.Constants
import com.example.kit.data.Article
import com.example.kit.data.Response
import com.example.kit.data.network
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeViewModal:ViewModel() {
    private val _articlesList = mutableStateOf<List<Article>>(emptyList())
    val articleList: State<List<Article>> get() = _articlesList

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage:State<String?> get() = _errorMessage

    private val _isLoading = mutableStateOf(false)
    val isLoading:State<Boolean> get() = _isLoading

//    init {
//        fetchNews()
//    }

    fun fetchNews(
        q:String,
        lang:String,
        country:String,
        sortby:String,
        noOfNews:Int
    ){
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(
                    GsonConverterFactory.create()).build()
                val service = retrofit.create(network::class.java)
                val response = service.getNews(
                    q,
                    lang,
                    country,
                    sortby,
                    noOfNews,
                    Constants.APP_ID
                )
                response.enqueue(object : Callback<Response>{
                    override fun onResponse(
                        call: Call<Response>,
                        response: retrofit2.Response<Response>
                    ) {
                        if (response.isSuccessful){
                            val newsList = response.body()
                            val responseJsonString = Gson().toJson(newsList)
                            _articlesList.value = (response.body()?.articles)!!
                            _isLoading.value = false
                            Log.e("List", response.body()!!.articles.toString())
                        }else{
                            _isLoading.value = false
                            val rc = response.code()
                            when(rc){
                                400 -> {
                                    _errorMessage.value = "Bad Connection"
                                    Log.e("Error 400", "Bad Connection")}
                                404 -> {
                                    _errorMessage.value = "Not Found"
                                    Log.e("Error 404", "Not Found")}
                                else -> {
                                    _errorMessage.value = rc.toString()
                                    Log.e("Error ", "Generic Error")}
                            }
                        }
                    }

                    override fun onFailure(call: Call<Response>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                        _errorMessage.value = t.message.toString()
                    }
                })
//
            }catch (e:Exception){
                Log.e("Network Error", e.toString())
                _errorMessage.value = e.message.toString()
            }
        }
    }
}