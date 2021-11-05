package edu.umich.yanfuguo.contactap.model;

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject


object ProfileStore {
    val profiles = arrayListOf<Profile?>()

    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("profiles_data", Context.MODE_PRIVATE)
        sharedPref.getString("myObjectKey", "[]")?.let {
            profiles.clear()
            val profilesReceived = JSONArray(it)
            for (i in 0 until profilesReceived.length()) {
                val profileEntry = profilesReceived[i] as JSONObject
                profiles.add(
                    Profile(
                        name = profileEntry.getString("name"),
                        description = profileEntry.getString("description"),
                    )
                )
            }
        }
    }

    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("profiles_data", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("myObjectKey", Gson().toJson(profiles))
        ed.apply()
    }

    fun insertProfile(context: Context, profileFirstName: String, profileLastName: String) {
        val newProfile = Profile(
            profileFirstName,
            profileLastName,
        )
        profiles.add(newProfile)
        commit(context)
    }

    fun deleteProfile(context: Context, pos: Int) {
        profiles.removeAt(pos)
        commit(context)
    }
}
