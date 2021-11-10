package edu.umich.yanfuguo.contactap.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

import com.android.volley.RequestQueue


/**
 * used to store current user's connections(friends/contacts)
 */
object ContactsStore {
    val contacts = arrayListOf<Contact?>()



    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("contacts_data", Context.MODE_PRIVATE)
        sharedPref.getString("contacts_data", "[]")?.let {
            try {
                contacts.clear()
                Gson().fromJson(it, Array<Contact?>::class.java).forEach{
                        contact -> contacts.add(contact)
                }
            } catch (e: JsonSyntaxException) {
                Log.e("ProfileStore", "Init failed, get $it")
            }
        }
    }

    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("contacts_data", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("contacts_data", Gson().toJson(contacts))
        ed.apply()
    }

    fun insert(context: Context, contact: Contact) {
        contacts.add(contact)
        commit(context)
    }

    fun delete(context: Context, pos: Int) {
        contacts.removeAt(pos)
        commit(context)
    }


}
