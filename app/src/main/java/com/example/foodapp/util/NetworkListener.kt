package com.example.foodapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkListener: ConnectivityManager.NetworkCallback() {

    private val isNetworkAvailable = MutableStateFlow(false)

    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isConnected = false

        connectivityManager.registerDefaultNetworkCallback(this)
        connectivityManager.activeNetwork.let { network ->
            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            if(networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                isConnected = true
            }
        }

        isNetworkAvailable.value = isConnected
        return isNetworkAvailable
    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}