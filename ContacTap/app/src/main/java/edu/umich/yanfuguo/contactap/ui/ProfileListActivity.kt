package edu.umich.yanfuguo.contactap.ui

import android.os.Bundle
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
}