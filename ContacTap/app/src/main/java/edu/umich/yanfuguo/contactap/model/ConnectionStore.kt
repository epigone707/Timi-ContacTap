package edu.umich.yanfuguo.contactap.model

import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonSyntaxException
import edu.umich.yanfuguo.contactap.model.MyInfoStore.userId
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * used to store current user's connections(friends/contacts)
 */
object ConnectionStore {
    val connections = arrayListOf<Contact?>()

    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("contacts_data", Context.MODE_PRIVATE)
        sharedPref.getString("contacts_data", "[]")?.let {
            try {
                connections.clear()
                Gson().fromJson(it, Array<Contact?>::class.java).forEach{
                        contact -> connections.add(contact)
                }
            } catch (e: JsonSyntaxException) {
                Log.e("ProfileStore", "Init failed, get $it")
            }
        }
    }

    /**
     * store connections to the SharedPreference file
     */
    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("contacts_data", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("contacts_data", Gson().toJson(connections))
        ed.apply()
    }

    fun insert(context: Context, contact: Contact) {
        connections.add(contact)
        commit(context)
    }

    fun delete(context: Context, pos: Int) {
        connections.removeAt(pos)
        commit(context)
    }

    /**
     * Communicate with Get Connections API
     */
    fun getMyConnections(context: Context, completion: () -> Unit) {
        val jsonObj = mapOf(
            "idToken" to LoginInfo.idToken,
            "clientId" to LoginInfo.clientId
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            MyInfoStore.serverUrl +"connections/",
            JSONObject(jsonObj),
            { response ->
                val received = try { response.getJSONArray("connections") } catch (e: JSONException) { JSONArray() }
                for (i in 0 until received.length()) {
                    val entry = received[i] as JSONObject
                    connections.add(Contact(
                        name=try {entry.getString("name") } catch (e: JSONException) { "" },
                        imageUrl=try {entry.getString("imageUrl") } catch (e: JSONException) { "" },
                        personalEmail=try {entry.getString("personalEmail") } catch (e: JSONException) { "" },
                        businessEmail=try {entry.getString("businessEmail") } catch (e: JSONException) { "" },
                        personalPhone=try {entry.getString("personalPhone") } catch (e: JSONException) { "" },
                        businessPhone=try {entry.getString("businessPhone") } catch (e: JSONException) { "" },
                        otherPhone=try {entry.getString("otherPhone") } catch (e: JSONException) { "" },
                        bio=try {entry.getString("bio") } catch (e: JSONException) { "" },
                        instagram=try {entry.getString("instagram") } catch (e: JSONException) { "" },
                        snapchat=try {entry.getString("snapchat") } catch (e: JSONException) { "" },
                        twitter=try {entry.getString("twitter") } catch (e: JSONException) { "" },
                        linkedIn=try {entry.getString("linkedIn") } catch (e: JSONException) { "" },
                        hobbies=try {entry.getString("hobbies") } catch (e: JSONException) { "" },
                        other=try {entry.getString("other") } catch (e: JSONException) { "" },
                    ))
                }
                completion()
            }, { completion() }
        )

        if (!MyInfoStore.isThingInitialized) {
            MyInfoStore.queue = Volley.newRequestQueue(context)
        }
        MyInfoStore.queue.add(postRequest)
    }

    /**
     * Create a new connection between a user and a profile.
     * Happens after a successful contact exchange between two users.
     * Communicate with Create Connection API
     */
    fun createConnection(context: Context, profileId: String, newIncludeBitString: String, location: String) {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)
        val jsonObj = mapOf(
            "idToken" to LoginInfo.idToken,
            "clientId" to LoginInfo.clientId,
            "profileId" to profileId,
            "newIncludeBitString" to newIncludeBitString,
            "location" to location,
            "time" to formatedDate
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            MyInfoStore.serverUrl + "connection/create/", JSONObject(jsonObj),
            { Log.d("createConnection", " posted!") },
            { error ->
                Log.e(
                    "createConnection",
                    error.localizedMessage ?: "JsonObjectRequest error"
                )
            }
        )

        if (!MyInfoStore.isThingInitialized) {
            MyInfoStore.queue = Volley.newRequestQueue(context)
        }
        MyInfoStore.queue.add(postRequest)
    }

}
