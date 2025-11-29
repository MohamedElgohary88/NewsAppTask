package com.elgohary.newsapptask.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

interface ConnectivityObserver {
    enum class Status {
        Available, Unavailable, Losing, Lost
    }

    fun observe(): Flow<Status>

    fun observeDetailed(): Flow<DetailedStatus>

    data class DetailedStatus(
        val status: Status,
        val transport: Transport,
        val isMetered: Boolean,
        val isValidated: Boolean
    )

    enum class Transport { WIFI, CELLULAR, ETHERNET, VPN, OTHER, NONE }
}

class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityObserver {

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun observe(): Flow<ConnectivityObserver.Status> =
        observeDetailed().map { it.status }.distinctUntilChanged()
    override fun observeDetailed(): Flow<ConnectivityObserver.DetailedStatus> = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(detailedStatus(ConnectivityObserver.Status.Available, network))
            }
            override fun onUnavailable() {
                trySend(detailedStatus(ConnectivityObserver.Status.Unavailable, null))
            }
            override fun onLosing(network: Network, maxMsToLive: Int) {
                trySend(detailedStatus(ConnectivityObserver.Status.Losing, network))
            }
            override fun onLost(network: Network) {
                trySend(detailedStatus(ConnectivityObserver.Status.Lost, network))
            }
        }


        try {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } catch (_: Exception) {
            registerLegacyRequest(callback)
        }

        trySend(currentSnapshot())

        awaitClose { runCatching { connectivityManager.unregisterNetworkCallback(callback) } }
    }.distinctUntilChanged()

    private fun registerLegacyRequest(callback: ConnectivityManager.NetworkCallback) {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)
    }

    private fun currentSnapshot(): ConnectivityObserver.DetailedStatus {
        val active = connectivityManager.activeNetwork
        return detailedStatus(
            status = if (isNetworkValidated(active)) ConnectivityObserver.Status.Available else ConnectivityObserver.Status.Unavailable,
            network = active
        )
    }

    private fun isNetworkValidated(network: Network?): Boolean {
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val validated =
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return hasInternet && validated
    }

    private fun detailedStatus(status: ConnectivityObserver.Status, network: Network?): ConnectivityObserver.DetailedStatus {
        val caps = network?.let { connectivityManager.getNetworkCapabilities(it) }
        val transport = when {
            caps == null -> ConnectivityObserver.Transport.NONE
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityObserver.Transport.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityObserver.Transport.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectivityObserver.Transport.ETHERNET
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectivityObserver.Transport.VPN
            else -> ConnectivityObserver.Transport.OTHER
        }
        val isMetered = if (caps != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        } else {
            when (transport) {
                ConnectivityObserver.Transport.WIFI, ConnectivityObserver.Transport.ETHERNET -> false
                ConnectivityObserver.Transport.NONE -> true
                else -> true
            }
        }
        val isValidated = network != null && isNetworkValidated(network)
        return ConnectivityObserver.DetailedStatus(status, transport, isMetered, isValidated)
    }
}