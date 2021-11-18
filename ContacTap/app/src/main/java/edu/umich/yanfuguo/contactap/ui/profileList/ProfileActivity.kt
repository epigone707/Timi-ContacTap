package edu.umich.yanfuguo.contactap.ui.profileList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ActivityProfileBinding
import edu.umich.yanfuguo.contactap.model.MyInfoStore
import edu.umich.yanfuguo.contactap.model.Profile
import edu.umich.yanfuguo.contactap.model.ProfileStore
import edu.umich.yanfuguo.contactap.toast


class ProfileActivity : AppCompatActivity() {
    private lateinit var profileView: ActivityProfileBinding
    private var current_profile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileView = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileView.root)

        val position = intent.getIntExtra("position",0)
        current_profile = ProfileStore.profiles[position]

        // basic
        profileView.checkboxName.text = MyInfoStore.myInfo.name
        profileView.checkboxBEmail.text = MyInfoStore.myInfo.businessEmail
        profileView.checkboxPEmail.text = MyInfoStore.myInfo.personalEmail
        profileView.checkboxBPhone.text = MyInfoStore.myInfo.businessPhone
        profileView.checkboxPPhone.text = MyInfoStore.myInfo.personalPhone
        profileView.checkboxOPhone.text = MyInfoStore.myInfo.otherPhone
        profileView.checkboxBio.text = MyInfoStore.myInfo.bio
        // social media
        profileView.checkboxInsta.text = MyInfoStore.myInfo.instagram
        profileView.checkboxSnap.text = MyInfoStore.myInfo.snapchat
        profileView.checkboxTwitter.text = MyInfoStore.myInfo.twitter
        profileView.checkboxLinkedin.text = MyInfoStore.myInfo.linkedIn
        // hobbies & other
        profileView.checkboxHobbies.text = MyInfoStore.myInfo.hobbies
        profileView.checkboxOtherinfo.text = MyInfoStore.myInfo.other



    }
    fun checkHelper(){

    }

    fun onCheckboxClicked(view: View) {

        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox_name -> {
                    if (checked) {
                        // Put some meat on the sandwich
                    } else {
                        // Remove the meat
                    }
                }
                R.id.checkbox_p_email -> {
                    if (checked) {
                        // Cheese me
                    } else {
                        // I'm lactose intolerant
                    }
                }
                // TODO: Veggie sandwich
            }
        }
    }
}