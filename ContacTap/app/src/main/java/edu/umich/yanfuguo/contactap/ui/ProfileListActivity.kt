package edu.umich.yanfuguo.contactap.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.databinding.ActivityProfileListBinding
import edu.umich.yanfuguo.contactap.ui.ProfileData.*
import edu.umich.yanfuguo.contactap.ui.ProfileData.ProfileStore.insertProfile
import edu.umich.yanfuguo.contactap.ui.ProfileData.ProfileStore.profiles
import edu.umich.yuangzh.kotlinChatter.ProfileAdapter

class ProfileListActivity: AppCompatActivity() {
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var profileListView: ActivityProfileListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileListView = ActivityProfileListBinding.inflate(layoutInflater)
        setContentView(profileListView.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        profileAdapter = ProfileAdapter(this, profiles)
        profileListView.profileListView.adapter = profileAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private var forAddResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val firstName = data?.getStringExtra("name")
            val lastName = data?.getStringExtra("description")

            if (lastName != null && firstName != null) {
                insertProfile(this, firstName, lastName)
                profileAdapter.notifyDataSetChanged()
            }
        }
    }

    // start add profile activity
    fun addProfile(view: View?) {
        val intent = Intent(this, AddProfileActivity::class.java)
        forAddResult.launch(intent)
    }
}