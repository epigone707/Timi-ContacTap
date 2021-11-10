package edu.umich.yanfuguo.contactap.model;

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONArray
import org.json.JSONObject


object ProfileStore {
    var profiles = arrayListOf<Profile?>()

    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("profiles_data", Context.MODE_PRIVATE)
        sharedPref.getString("profiles_data", "[]")?.let {
            try {
                profiles.clear()
                Gson().fromJson(it, Array<Profile?>::class.java).forEach{
                    profile -> profiles.add(profile)
                }
            } catch (e: JsonSyntaxException) {
                Log.e("ProfileStore", "Init failed, get $it")
            }
        }
    }

    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("profiles_data", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("profiles_data", Gson().toJson(profiles))
        ed.apply()
    }

    fun insert(context: Context, profile: Profile) {
        profiles.add(profile)
        commit(context)
    }

    fun delete(context: Context, pos: Int) {
        profiles.removeAt(pos)
        commit(context)
    }

    /**
     * Get profiles
     */
    fun getProfiles(context: Context, completion: () -> Unit) {
        val uri = MyInfoStore.serverUrl +"profiles/?userId=${MyInfoStore.userId}"

        val getRequest = JsonObjectRequest(uri,
            { response ->
                val strResp = response.toString()
                val jsonObj: JSONObject = JSONObject(strResp)
                MyInfoStore.myInfo.name = jsonObj.getString("name")
                MyInfoStore.myInfo.imageUrl = jsonObj.getString("imageUrl")
                MyInfoStore.myInfo.personalEmail = jsonObj.getString("personalEmail")
                MyInfoStore.myInfo.businessEmail = jsonObj.getString("businessEmail")
                MyInfoStore.myInfo.personalPhone = jsonObj.getString("personalPhone")
                MyInfoStore.myInfo.businessPhone = jsonObj.getString("businessPhone")
                MyInfoStore.myInfo.otherPhone = jsonObj.getString("otherPhone")
                MyInfoStore.myInfo.bio = jsonObj.getString("bio")
                MyInfoStore.myInfo.instagram = jsonObj.getString("instagram")
                MyInfoStore.myInfo.snapchat = jsonObj.getString("snapchat")
                MyInfoStore.myInfo.twitter = jsonObj.getString("twitter")
                MyInfoStore.myInfo.linkedIn = jsonObj.getString("linkedIn")
                MyInfoStore.myInfo.hobbies = jsonObj.getString("hobbies")
                MyInfoStore.myInfo.other = jsonObj.getString("other")
                completion()
            }, { completion() }
        )

        if (!MyInfoStore.isThingInitialized) {
            MyInfoStore.queue = Volley.newRequestQueue(context)
        }
        MyInfoStore.queue.add(getRequest)
    }
}
