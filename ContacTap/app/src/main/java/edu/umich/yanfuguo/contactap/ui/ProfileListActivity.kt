package edu.umich.yanfuguo.contactap.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.R

class ProfileListActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun sendToShare(view: View) {
        val selectedProfile = view.findViewById<TextView>(R.id.profileTitle)
        val intent = Intent(this, ShareActivity::class.java).apply {
            //putExtra("selected profile", selectedProfile.toString())
        }
        startActivity(intent)
    }
}