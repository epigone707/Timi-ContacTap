package edu.umich.yanfuguo.contactap.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue

object ContactsStore {
    val contacts = arrayListOf<Contact?>()

    private lateinit var queue: RequestQueue
    private const val serverUrl = ""

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

    fun postContacts(context: Context, contact: Contact) {
        val jsonObj = mapOf(
            "name" to contact.name,
            "phone" to contact.phone,
            "phone" to contact.phone,
            "email" to contact.email,
            "insta" to contact.insta,
            "snap" to contact.snap,
            "twitter" to contact.twitter,
            "linkedin" to contact.linkedin,
            "other" to contact.other,
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl+"postmaps/", JSONObject(jsonObj),
            { Log.d("postChatt", "chatt posted!") },
            { error -> Log.e("postChatt", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
    }
}
