package edu.umich.yanfuguo.contactap.ui.profileList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.databinding.ActivityProfileListBinding
import edu.umich.yanfuguo.contactap.model.Profile
import edu.umich.yanfuguo.contactap.model.ProfileStore.createProfile
import edu.umich.yanfuguo.contactap.model.ProfileStore.insert
import edu.umich.yanfuguo.contactap.model.ProfileStore.profiles

class ProfileListActivity: AppCompatActivity() {
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var binding: ActivityProfileListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        profileAdapter = ProfileAdapter(this, profiles)
        binding.profileListView.adapter = profileAdapter


        binding.fab.setOnClickListener {
            val intent = Intent(this@ProfileListActivity, ProfileAddActivity::class.java)
            forAddResult.launch(intent)
        }
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
            val name = data?.getStringExtra("name")
            val description = data?.getStringExtra("description")

            // add the new profile to both local storage and back end server
            if (name != null && description != null) {
                //TODO: insert() is only used for local testing, change insert() to createProfile()
                insert(this, Profile(name, "", description, "10000000000000"))
                //createProfile(this,"10000000000000", name, description)
                profileAdapter.notifyDataSetChanged()
            }
        }
    }
}
