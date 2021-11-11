package edu.umich.yanfuguo.contactap.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.toolbox.Volley.newRequestQueue
import java.lang.String

object MyInfoStore {
    var myInfo: Contact = Contact()

    lateinit var queue: RequestQueue
    val isThingInitialized get() = this::queue.isInitialized

    const val serverUrl = "exmaple/"

    var userId = ""

    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("my_info_data", Context.MODE_PRIVATE)
        sharedPref.getString("my_info_data", "{}")?.let {
            myInfo = Gson().fromJson(it, Contact::class.java)
        }
    }

    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("my_info_data", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("my_info_data", Gson().toJson(myInfo))
        ed.apply()
    }

    /**
     * Get user's contact info from the server.
     */
    fun getMyInfo(context: Context, completion: () -> Unit) {
        val uri = serverUrl+"contactinfo/?id=$userId"

        val getRequest = JsonObjectRequest(uri,
            { response ->
                val strResp = response.toString()
                val jsonObj = JSONObject(strResp)
                myInfo.name = jsonObj.getString("name")
                myInfo.imageUrl = jsonObj.getString("imageUrl")
                myInfo.personalEmail = jsonObj.getString("personalEmail")
                myInfo.businessEmail = jsonObj.getString("businessEmail")
                myInfo.personalPhone = jsonObj.getString("personalPhone")
                myInfo.businessPhone = jsonObj.getString("businessPhone")
                myInfo.otherPhone = jsonObj.getString("otherPhone")
                myInfo.bio = jsonObj.getString("bio")
                myInfo.instagram = jsonObj.getString("instagram")
                myInfo.snapchat = jsonObj.getString("snapchat")
                myInfo.twitter = jsonObj.getString("twitter")
                myInfo.linkedIn = jsonObj.getString("linkedIn")
                myInfo.hobbies = jsonObj.getString("hobbies")
                myInfo.other = jsonObj.getString("other")
                commit(context)
                completion()
            }, { completion() }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(getRequest)
    }

    /**
     * Create new user's whole set contact info and send it to server.
     * When the new user create an account and log in for the first time,
     * the app will send this request.
     */
    fun postMyInfo(context: Context, contact: Contact) {
        val jsonObj = mapOf(
            "name" to contact.name,
            "imageUrl" to contact.imageUrl,
            "businessEmail" to contact.personalEmail,
            "personalPhone" to contact.personalPhone,
            "businessPhone" to contact.businessPhone,
            "otherPhone" to contact.otherPhone,
            "bio" to contact.bio,
            "instagram" to contact.instagram,
            "snapchat" to contact.snapchat,
            "twitter" to contact.twitter,
            "linkedIn" to contact.linkedIn,
            "hobbies" to contact.hobbies,
            "other" to contact.other,
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            this.serverUrl +"contactinfo/", JSONObject(jsonObj),
            { Log.d("postMyInfo", " posted!") },
            { error -> Log.e("postMyInfo", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            this.queue = Volley.newRequestQueue(context)
        }
        this.queue.add(postRequest)
    }


}