package edu.umich.yanfuguo.contactap.ui.profileList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ActivityProfileBinding
import edu.umich.yanfuguo.contactap.model.MyInfoStore
import edu.umich.yanfuguo.contactap.model.Profile
import edu.umich.yanfuguo.contactap.model.ProfileStore
import coil.load



class ProfileActivity : AppCompatActivity() {
    private lateinit var profileView: ActivityProfileBinding
    private var current_profile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileView = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileView.root)

        val position = intent.getIntExtra("position",0)
        current_profile = ProfileStore.profiles[position]

        MyInfoStore.myInfo.imageUrl?.let {
            profileView.profileImage.setVisibility(View.VISIBLE)
            profileView.profileImage.load(it) {
                crossfade(true)
                crossfade(1000)
            }
            profileView.checkboxImage.text = getString(R.string.profile_image_title)
        } ?: run {
            // if no image has been uploaded
            // there won’t be a big empty space
            profileView.profileImage.setVisibility(View.GONE)
            profileView.profileImage.setImageBitmap(null)

        }
        for(i in 0..13){
            current_profile?.includeBitString?.let { initCheckBoxes(i, it[i]) }
        }

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
    fun initCheckBoxes(index:Int,bit:Char){
        if(index <0 || index >13){
            Log.d("initCheckBoxes","out of index")
        }
        var initCheckVal = false
        if(bit=='1'){
            initCheckVal = true
        }
        when (index) {
            0 -> {
                profileView.checkboxName.isChecked =initCheckVal
            }
            1 -> {
                profileView.checkboxImage.isChecked =initCheckVal
            }
            2 -> {
                profileView.checkboxPEmail.isChecked =initCheckVal
            }
            3 -> {
                profileView.checkboxBEmail.isChecked =initCheckVal
            }
            4 -> {
                profileView.checkboxPPhone.isChecked =initCheckVal
            }
            5 -> {
                profileView.checkboxBPhone.isChecked =initCheckVal
            }
            6 -> {
                profileView.checkboxOPhone.isChecked =initCheckVal
            }
            7 -> {
                profileView.checkboxBio.isChecked =initCheckVal
            }
            8 -> {
                profileView.checkboxInsta.isChecked =initCheckVal
            }
            9 -> {
                profileView.checkboxSnap.isChecked =initCheckVal
            }
            10 -> {
                profileView.checkboxTwitter.isChecked =initCheckVal
            }
            11 -> {
                profileView.checkboxLinkedin.isChecked =initCheckVal
            }
            12-> {
                profileView.checkboxHobbies.isChecked =initCheckVal
            }
            13 -> {
                profileView.checkboxOtherinfo.isChecked =initCheckVal
            }
        }

    }

    fun checkHelper(checked: Boolean,index: Int){
        if(index <0 || index >13){
            Log.d("checkHelper","out of index")
        }
        if (checked) {
            var string = current_profile?.includeBitString
            string = string?.substring(0, index) + '1' + string?.substring(index + 1)
            current_profile?.includeBitString = string
        } else {
            var string = current_profile?.includeBitString
            string = string?.substring(0, index) + '0' + string?.substring(index + 1)
            current_profile?.includeBitString = string
        }

    }

    fun onCheckboxClicked(view: View) {

        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox_name -> {
                    checkHelper(checked,0)
                }
                R.id.checkbox_p_email -> {
                    checkHelper(checked,2)
                }
                R.id.checkbox_b_email -> {
                    checkHelper(checked,3)
                }
                R.id.checkbox_p_phone -> {
                    checkHelper(checked,4)
                }
                R.id.checkbox_b_phone -> {
                    checkHelper(checked,5)
                }
                R.id.checkbox_o_phone -> {
                    checkHelper(checked,6)
                }
                R.id.checkbox_bio -> {
                    checkHelper(checked,7)
                }
                R.id.checkbox_insta -> {
                    checkHelper(checked,8)
                }
                R.id.checkbox_snap -> {
                    checkHelper(checked,9)
                }
                R.id.checkbox_twitter -> {
                    checkHelper(checked,10)
                }
                R.id.checkbox_linkedin -> {
                    checkHelper(checked,11)
                }
                R.id.checkbox_hobbies -> {
                    checkHelper(checked,12)
                }
                R.id.checkbox_otherinfo -> {
                    checkHelper(checked,13)
                }
            }
        }
    }
}