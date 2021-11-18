package edu.umich.yanfuguo.contactap.ui.profileList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ActivityProfileBinding
import edu.umich.yanfuguo.contactap.model.MyInfoStore

class ProfileActivity : AppCompatActivity() {
    private lateinit var view: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        view.checkboxName.text = MyInfoStore.myInfo.name
        view.checkboxBEmail.text = MyInfoStore.myInfo.businessEmail
        view.checkboxPEmail.text = MyInfoStore.myInfo.personalEmail
        view.checkboxBPhone.text = MyInfoStore.myInfo.businessPhone
        view.checkboxPPhone.text = MyInfoStore.myInfo.personalPhone
        view.checkboxOPhone.text = MyInfoStore.myInfo.otherPhone
        view.checkboxBio.text = MyInfoStore.myInfo.bio

    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox_meat -> {
                    if (checked) {
                        // Put some meat on the sandwich
                    } else {
                        // Remove the meat
                    }
                }
                R.id.checkbox_cheese -> {
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