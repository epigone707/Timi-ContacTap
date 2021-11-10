package edu.umich.yanfuguo.contactap.model;

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import edu.umich.yanfuguo.contactap.model.MyInfoStore.userId
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


object ProfileStore {

    // all current user's profiles
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
        val uri = MyInfoStore.serverUrl +"profiles/?userId=${userId}"

        val getRequest = JsonObjectRequest(uri,
            { response ->
                val profilesArray = try { response.getJSONArray("profiles") } catch (e: JSONException) { JSONArray() }
                profiles.clear()
                for (i in 0 until profilesArray.length()) {
                    val entry = profilesArray.getJSONObject(i)
                    profiles.add(
                        Profile(
                            name = entry.getString("name"),
                            profileId = entry.getString("profileId"),
                            description = entry.getString("description"),
                            includeBitString = entry.getString("includeBitString")
                        )
                    )
                }
                commit(context)
                completion()
            }, { completion() }
        )

        if (!MyInfoStore.isThingInitialized) {
            MyInfoStore.queue = Volley.newRequestQueue(context)
        }
        MyInfoStore.queue.add(getRequest)
    }

    /**
     * Create a profile, send the userId and includeBitString to server, get the profileId
     * then store the profile in local storage
     */
    fun createProfile(context: Context, includeBitString: String, profileName: String, description: String) {
        val jsonObj = mapOf(
            "userId" to userId,
            "includeBitString" to includeBitString,
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            MyInfoStore.serverUrl +"profile/",
            JSONObject(jsonObj),
            {
                Log.d("createProfile", " get the response!")
                val strResp = it.toString()
                val entry = JSONObject(strResp)
                profiles.add(
                    Profile(
                        name = profileName,
                        profileId = entry.getString("profileId"),
                        description = description,
                        includeBitString = includeBitString
                    )
                )
                Log.d("createProfile", " created!")
                commit(context)
            },
            { error -> Log.e("createProfile", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!MyInfoStore.isThingInitialized) {
            MyInfoStore.queue = Volley.newRequestQueue(context)
        }
        MyInfoStore.queue.add(postRequest)
    }

    /**
     * Update a profile based on user's modification on what
     * information they choose to include in the profile.
     */
    fun updateProfile(context: Context, profile: Profile) {
        val jsonObject = JSONObject()

        try {
            jsonObject.put("profileId", profile.profileId)
            jsonObject.put("includeBitString", profile.includeBitString)
        } catch (e: JSONException) {
            // handle exception
            Log.e("json_error: ", "$e")
        }

        val putRequest: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.PUT, MyInfoStore.serverUrl +"profile/", jsonObject,
                Response.Listener { response ->
                    Log.d("response: ", "$response")
                },
                Response.ErrorListener { error ->
                    Log.e("error: ", "$error")
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> =
                        HashMap()
                    headers["Content-Type"] = "application/json"
                    headers["Accept"] = "application/json"
                    return headers
                }
                override fun getBody(): ByteArray {
                    Log.i("json", jsonObject.toString())
                    return jsonObject.toString().toByteArray(charset("UTF-8"))
                }

            }
        if (!MyInfoStore.isThingInitialized) {
            MyInfoStore.queue = Volley.newRequestQueue(context)
        }
        MyInfoStore.queue.add(putRequest)
    }



}
