package edu.umich.yanfuguo.contactap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.umich.yanfuguo.contactap.R

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // get profile name from profile list activity
        val selectedProfile = intent.getStringExtra("profile selected")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}