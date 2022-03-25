package khanhle.imageapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class CheckConnectInternet {
    fun check(context: Context): Boolean {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isMetered = cm.isActiveNetworkMetered

        if (isMetered) {
            return true // Not connect
        }

        return false
    }
}