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
import edu.umich.yanfuguo.contactap.databinding.ActivityContactInfoBinding
import org.json.JSONException
import org.json.JSONObject

object MyInfoStore {
    var myInfo: Contact = Contact()

    val InfoKeys: Array<String> =  arrayOf(
    "name",
    "imageUrl",
    "personalEmail",
    "businessEmail",
    "personalPhone",
    "businessPhone",
    "otherPhone",
    "bio",
    "instagram",
    "snapchat",
    "twitter",
    "linkedIn",
    "hobbies",
    "other",)

    lateinit var queue: RequestQueue
    val isThingInitialized get() = this::queue.isInitialized

    const val serverUrl = "https://www.contactap.xyz/"


    var userId = ""

    /**
     * initialize myInfo from the SharedPreference file
     */
    fun init(context: Context) {
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("my_info_data", Context.MODE_PRIVATE)
        sharedPref.getString("my_info_data", "{}")?.let {
            myInfo = Gson().fromJson(it, Contact::class.java)
        }
    }

    /**
     * store myInfo to the SharedPreference file
     */
    fun commit(context: Context){
        val sharedPref: SharedPreferences =
            context.getSharedPreferences("my_info_data", Context.MODE_PRIVATE)
        val ed = sharedPref.edit()
        ed.putString("my_info_data", Gson().toJson(myInfo))
        ed.apply()
    }




    /**
     * Get user's contact info from the server and
     * store it to the SharedPreference file
     */
    fun getMyInfo(context: Context, completion: () -> Unit) {

        val jsonObj = mapOf(
            "idToken" to LoginInfo.idToken,
            "clientId" to LoginInfo.clientId
        )
        val postRequest = JsonObjectRequest(
            Request.Method.POST,
            serverUrl +"contactinfo/",
            JSONObject(jsonObj),
            { response ->
                val contactInfo = response.getJSONObject("contactInfo")
                myInfo.name = contactInfo.getString("name")
                myInfo.imageUrl = contactInfo.getString("imageUrl")
                myInfo.personalEmail = contactInfo.getString("personalEmail")
                myInfo.businessEmail = contactInfo.getString("businessEmail")
                myInfo.personalPhone = contactInfo.getString("personalPhone")
                myInfo.businessPhone = contactInfo.getString("businessPhone")
                myInfo.otherPhone = contactInfo.getString("otherPhone")
                myInfo.bio = contactInfo.getString("bio")
                myInfo.instagram = contactInfo.getString("instagram")
                myInfo.snapchat = contactInfo.getString("snapchat")
                myInfo.twitter = contactInfo.getString("twitter")
                myInfo.linkedIn = contactInfo.getString("linkedIn")
                myInfo.hobbies = contactInfo.getString("hobbies")
                myInfo.other = contactInfo.getString("other")
                commit(context)
                completion()
            },
            { error -> Log.e("getMyInfo", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
    }

    /**
     * Create new user's whole set contact info and send it to server.
     * When the new user create an account and log in for the first time,
     * the app will send this request.
     */
    fun postMyInfo(context: Context, contact: Contact) {
        val jsonObj = mapOf(
            "idToken" to LoginInfo.idToken,
            "clientId" to LoginInfo.clientId,
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
            this.serverUrl +"contactinfo/create/", JSONObject(jsonObj),
            { Log.d("postMyInfo", " posted!") },
            { error -> Log.e("postMyInfo", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            this.queue = Volley.newRequestQueue(context)
        }
        this.queue.add(postRequest)
    }

    /**
     * Back up user's whole set profiles setting to the server.
     * Every time the user makes changes to whole set contact
     * information (without changing profile image), the app will send this request.
     */
    fun updateMyInfo(context: Context, contact: Contact) {
        if (LoginInfo.idToken == null) {
            commit(context)
        }
        val jsonObj = mapOf(
            "idToken" to LoginInfo.idToken,
            "clientId" to LoginInfo.clientId,
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
            this.serverUrl +"contactinfo/update/", JSONObject(jsonObj),
            {
                Log.d("updateMyInfo", " posted!")
                commit(context)
            },
            { error -> Log.e("updateMyInfo", error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            this.queue = Volley.newRequestQueue(context)
        }
        this.queue.add(postRequest)
    }

    /**
     * When user changes their profile image, this request is sent
     * TODO
     */
    fun updateImage(){

    }


    /**
     * update the view
     */
    fun updateContent(contactInfoView: ActivityContactInfoBinding) : Uri? {
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

    fun saveContent(context: Context, contactInfoView: ActivityContactInfoBinding, imageUri: Uri?) {
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
        updateMyInfo(context, myInfo)
    }

    /**
     * generate an overview of the user's info
     * return a string
     * used in home screen(HomeFragment)
     */
    fun getOverview():String{
        val obj = try { JSONObject(Gson().toJson(myInfo)) } catch (e: JSONException) { JSONObject() }
        var preview = ""
        val keys =  arrayOf("personalEmail", "businessEmail", "personalPhone","businessPhone",)
        try{
            if(obj.getString("bio").isNotEmpty()){
                preview += "${obj.getString("bio")}\n\n"
            }else{
                Log.d("HomeFragment","bio empty")
            }
            keys.forEach { k ->
                if (obj.getString(k).isNotEmpty()) {
                    preview += "$k: ${obj.getString(k)}\n"
                } else {
                    Log.d("HomeFragment", "getOverview empty")
                }
            }
        }catch (e: JSONException) {
            Log.e("HomeFragment","getOverview JSONException")
        }
        if (preview.length < 40){
            preview += "\nClick this card to add more info!"
        }
        return preview
    }

    /**
     * return a JSONObject that contains real data of a profile instead of bitstring
     * used in share screen(ShareActivity)
     */
    fun getMaskedInfo(profile: Profile?) : String {
        val obj = try { JSONObject(Gson().toJson(myInfo)) } catch (e: JSONException) { JSONObject() }
        if (profile != null) {
            for (idx in profile.includeBitString.indices) {
                if(profile.includeBitString[idx] =='0'){
                    obj.put(InfoKeys[idx], "")
                }
            }
            obj.put("profileId", profile.profileId)
            obj.put("includeBitString", profile.includeBitString)
        }
        return obj.toString()
    }

    /**
     * generate an overview of a profile
     * return a string
     * used in share screen(ShareActivity)
     */
    fun getMaskedOverview(profile: Profile?):String {
        var preview = ""
        val obj = try { JSONObject(Gson().toJson(myInfo)) } catch (e: JSONException) { JSONObject() }
        if (profile != null) {
            for (idx in profile.includeBitString.indices) {
                if(profile.includeBitString[idx] =='1'){
                    preview += "${InfoKeys[idx]}: ${obj.getString(InfoKeys[idx])}\n"
                }
            }
        }
        return preview
    }
}