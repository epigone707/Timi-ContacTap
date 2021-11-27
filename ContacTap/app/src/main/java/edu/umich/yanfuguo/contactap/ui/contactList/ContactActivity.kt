package edu.umich.yanfuguo.contactap.ui.contactList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ActivityProfileBinding
import coil.load
import edu.umich.yanfuguo.contactap.ReceiveActivity
import edu.umich.yanfuguo.contactap.databinding.ActivityContactBinding
import edu.umich.yanfuguo.contactap.model.*


class ContactActivity : ReceiveActivity() {
    private lateinit var profileView: ActivityContactBinding
    private var current_connection: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileView = ActivityContactBinding.inflate(layoutInflater)
        setContentView(profileView.root)

        var position = intent.getIntExtra("position", ConnectionStore.connections.size - 1)
        if (position > ConnectionStore.connections.size - 1)
            position = ConnectionStore.connections.size - 1
        current_connection = ConnectionStore.connections[position]

        current_connection?.imageUrl?.let {
            profileView.profileImage.setVisibility(View.VISIBLE)
            profileView.profileImage.load(it) {
                crossfade(true)
                crossfade(1000)
            }
        } ?: run {
            // if no image has been uploaded
            // there won’t be a big empty space
            profileView.profileImage.setVisibility(View.GONE)
            profileView.profileImage.setImageBitmap(null)
        }

        // basic
        current_connection?.name.let { profileView.checkboxName.text = it }
        current_connection?.businessEmail.let { profileView.checkboxBEmail.text = it }
        current_connection?.personalEmail.let { profileView.checkboxPEmail.text = it }
        current_connection?.businessPhone.let { profileView.checkboxBPhone.text = it }
        current_connection?.personalPhone.let { profileView.checkboxPPhone.text = it }
        current_connection?.otherPhone.let { profileView.checkboxOPhone.text = it }
        current_connection?.bio.let { profileView.checkboxBio.text = it }
        // social media
        current_connection?.instagram.let { profileView.checkboxInsta.text = it }
        current_connection?.snapchat.let { profileView.checkboxSnap.text = it }
        current_connection?.twitter.let { profileView.checkboxTwitter.text = it }
        current_connection?.linkedIn.let { profileView.checkboxLinkedin.text = it }
        // hobbies & other
        current_connection?.hobbies.let { profileView.checkboxHobbies.text = it }
        current_connection?.other.let { profileView.checkboxOtherinfo.text = it }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}