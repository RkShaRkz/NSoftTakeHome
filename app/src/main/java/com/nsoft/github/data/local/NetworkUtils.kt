package com.nsoft.github.data.local

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.nsoft.github.NSoftApplication

/**
 * Class for checking networking connectivity, mainly if we have internet so far
 *
 * @see isInternetAvailable
 */
object NetworkUtils {

    /**
     * Internally calls [isInternetAvailable] but with [NSoftApplication.getAppContext] context
     */
    fun isIntenetAvailableUsingAppContext(): Boolean {
        return isInternetAvailable(NSoftApplication.getAppContext())
    }

    /**
     * Method for checking whether internet is available, or is currently connecting
     *
     * @param context the [Context] to use to grab the [ConnectivityManager] and make the check
     * @return whether internet is available (or connecting) or not
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = 
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isInternetAvailable_postM(connectivityManager = connectivityManager)
        } else {
            isInternetAvailable_preM(connectivityManager = connectivityManager)
        }
    }

    /**
     * Version of [isInternetAvailable] for API 23+
     *
     * @see isInternetAvailable
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun isInternetAvailable_postM(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork

        return if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities != null) {
                // If we have NET_CAPABILITY_INTERNET and NET_CAPABILITY_VALIDATED and
                // either WIFI, CELLULAR or ETHERNET, we're good and have internet.
                // Otherwise, there's no internet
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
            } else {
                // No network capabilities means we have no internet.
                false
            }
        } else {
            // No network obviously means we have no internet either
            false
        }
    }

    /**
     * Version of [isInternetAvailable] for API < 23
     *
     * @see isInternetAvailable
     */
    private fun isInternetAvailable_preM(connectivityManager: ConnectivityManager): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo

        return if (networkInfo != null) {
            networkInfo.isConnectedOrConnecting == true
        } else {
            // No networkInfo means we don't have internet
            false
        }
    }
}