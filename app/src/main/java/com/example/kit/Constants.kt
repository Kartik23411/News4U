package com.example.kit

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log

object Constants {

    const val APP_ID:String = "2395cb403f35f1728f36134ea8909ba6"
    const val BASE_URL:String = "https://gnews.io/api/"

    val countryList = listOf(
        "any","au", "br",	"ca","cn","eg","fr","de","gr","hk","in","ie","il","it","jp","nl","no","pk","pe","ph","pt","ro","ru","sg","es","se","ch","tw","ua","gb","us"
    )

    val languageList = listOf(
        "any","ar","zh","nl","en","fr","de","el","he","hi","it","ja","ml","mr","no","pt","ro","ru","es","sv","ta","te","uk"
    )

    val categoryList = listOf(
        "any", "banking","business","education","general","sports", "technology", "trading"
    )

    fun isNetWorkAvailable(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Execute Android 15+ specific code
            Log.e("android 15", " running android 15")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                else -> return false
            }
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo!=null && networkInfo.isConnectedOrConnecting
        }
    }
}