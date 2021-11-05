package edu.umich.yanfuguo.contactap.model

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

object MyInfoStore {
    var myContact: Contact = Contact()

    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("my_info_data", Context.MODE_PRIVATE)
        sharedPref.getString("my_info_data", "{}")?.let {
            myContact = Gson().fromJson(it, Contact::class.java)
        }
    }

    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("my_info_data", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("my_info_data", Gson().toJson(myContact))
        ed.apply()
    }
}