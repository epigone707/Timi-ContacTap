package edu.umich.yanfuguo.contactap.model;

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONArray
import org.json.JSONException
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
     * Get all current user's profiles from server and update the local profiles list
     */
    fun getProfiles(context: Context, completion: () -> Unit) {
        val uri = MyInfoStore.serverUrl +"profiles/?userId=${MyInfoStore.userId}"

        val getRequest = JsonObjectRequest(uri,
            { response ->
                val profilesArray = try { response.getJSONArray("profiles") } catch (e: JSONException) { JSONArray() }
                profiles.clear()
                for (i in 0 until profilesArray.length()) {
                    val jsonObj = profilesArray.getJSONObject(i)
                    profiles.add(
                        Profile(
                            name = jsonObj.getString("name"),
                            profileId = jsonObj.getString("profileId"),
                            description = jsonObj.getString("description"),
                            includeBitString = jsonObj.getString("includeBitString")
                        )
                    )
                }
                completion()
            }, { completion() }
        )

        if (!MyInfoStore.isThingInitialized) {
            MyInfoStore.queue = Volley.newRequestQueue(context)
        }
        MyInfoStore.queue.add(getRequest)
    }
}
