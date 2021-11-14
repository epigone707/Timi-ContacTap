package edu.umich.yanfuguo.contactap.model

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.gson.Gson
import edu.umich.yanfuguo.contactap.databinding.FragmentContactInfoBinding
import org.json.JSONObject

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


    fun updateContent(contactInfoView: FragmentContactInfoBinding) : Uri? {
        val imageUri = if (myInfo.imageUrl != null) {
            val uri = Uri.parse(myInfo.imageUrl)
            contactInfoView.previewImage.setImageURI(uri)
            uri
        } else {null}
        contactInfoView.contactNameEdit.setText(myInfo.name)
        contactInfoView.contactPersonalPhoneEdit.setText(myInfo.personalPhone)
        contactInfoView.contactBusinessPhoneEdit.setText(myInfo.businessPhone)
        contactInfoView.contactOtherPhoneEdit.setText(myInfo.otherPhone)
        contactInfoView.contactPersonalEmailEdit.setText(myInfo.personalEmail)
        contactInfoView.contactBusinessEmailEdit.setText(myInfo.businessEmail)
        contactInfoView.contactInstaEdit.setText(myInfo.instagram)
        contactInfoView.contactSnapEdit.setText(myInfo.snapchat)
        contactInfoView.contactTwitterEdit.setText(myInfo.twitter)
        contactInfoView.contactLinkedinEdit.setText(myInfo.linkedIn)
        contactInfoView.contactHobbiesEdit.setText(myInfo.hobbies)
        contactInfoView.contactOtherinfoEdit.setText(myInfo.other)
        contactInfoView.contactBioEdit.setText(myInfo.bio)
        return imageUri
    }

    fun saveContent(context: Context, contactInfoView: FragmentContactInfoBinding, imageUri: Uri?) {
        imageUri?.let { myInfo.imageUrl = it.toString() }
        contactInfoView.contactNameEdit.text?.let { myInfo.name = it.toString() }
        contactInfoView.contactPersonalPhoneEdit.text?.let{ myInfo.personalPhone = it.toString() }
        contactInfoView.contactBusinessPhoneEdit.text?.let{ myInfo.businessPhone = it.toString() }
        contactInfoView.contactOtherPhoneEdit.text?.let{ myInfo.otherPhone = it.toString() }
        contactInfoView.contactPersonalEmailEdit.text?.let{ myInfo.personalEmail = it.toString() }
        contactInfoView.contactBusinessEmailEdit.text?.let{ myInfo.businessEmail = it.toString() }
        contactInfoView.contactInstaEdit.text?.let{ myInfo.instagram = it.toString() }
        contactInfoView.contactSnapEdit.text?.let{ myInfo.snapchat = it.toString() }
        contactInfoView.contactTwitterEdit.text?.let{ myInfo.twitter = it.toString() }
        contactInfoView.contactLinkedinEdit.text?.let{ myInfo.linkedIn = it.toString() }
        contactInfoView.contactHobbiesEdit.text?.let{ myInfo.hobbies = it.toString() }
        contactInfoView.contactOtherinfoEdit.text?.let{ myInfo.other = it.toString() }
        contactInfoView.contactBioEdit.text?.let{ myInfo.bio = it.toString() }
        commit(context)
    }

    fun getMaskedInfo(profile: Profile?) : Contact {
        val contact = myInfo.copy()
        profile?.includeBitString?.forEachIndexed { i, inc ->
            if (inc == '0')
                when (i) {
                    0 -> contact.name = ""
                    1 -> contact.imageUrl = ""
                    2 -> contact.personalEmail = ""
                    3 -> contact.businessEmail = ""
                    4 -> contact.personalPhone = ""
                    5 -> contact.businessPhone = ""
                    6 -> contact.otherPhone = ""
                    7 -> contact.bio = ""
                    8 -> contact.instagram = ""
                    9 -> contact.snapchat = ""
                    10 -> contact.twitter = ""
                    11 -> contact.linkedIn = ""
                    12 -> contact.hobbies = ""
                    13 -> contact.other = ""
                }
        }
        return contact
    }
}