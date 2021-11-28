package edu.umich.yanfuguo.contactap.model

import android.content.Context
import android.content.SharedPreferences

object LoginInfo {
    var idToken :String? = null
    var displayName = ""
    const val clientId = "142853893477-6fcj283umuv16rsev0mbe97d8v6se7ad.apps.googleusercontent.com"

    /**
     * initialize myInfo from the SharedPreference file
     */
    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("idToken", Context.MODE_PRIVATE)
        sharedPref.getString("idToken", null)?.let {
            idToken = it
        }
        sharedPref.getString("displayName", null)?.let {
            displayName = it
        }
    }

    /**
     * store myInfo to the SharedPreference file
     */
    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("idToken", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("idToken", idToken)
        ed.putString("displayName", displayName)
        ed.apply()
    }
}
