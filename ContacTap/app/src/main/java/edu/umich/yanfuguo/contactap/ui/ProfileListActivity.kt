package edu.umich.yanfuguo.contactap.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclersample.data.Profile
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.ui.ProfileData.*

const val PROFILE_ID = "profile_id"

class ProfileListActivity: AppCompatActivity() {
    private val newProfileActivityRequestCode = 1
    private val profilesListViewModel by viewModels<ProfileListViewModel> {
       ProfileListViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        val profileAdapter = ProfileAdapter { profile -> adapterOnClick(profile) }
        //val concatAdapter = ConcatAdapter(headerAdapter, profileAdapter)

        val recyclerView: RecyclerView = findViewById(R.id.profile_recycler_view)
        recyclerView.adapter = profileAdapter

        profilesListViewModel.profileLiveData.observe(this, {
            it?.let {
                profileAdapter.submitList(it as MutableList<Profile>)
            }
        })

        val fab: View = findViewById(R.id.profile_fab)
        fab.setOnClickListener {
            fabOnClick()
        }
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

    private fun adapterOnClick(profile: Profile) {
        // start the profile edit activity
    }

    // start add profile activity
    private fun fabOnClick() {
        val intent = Intent(this, AddProfileActivity::class.java)
        startActivityForResult(intent, newProfileActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        /* Inserts profile into viewModel. */
        if (requestCode == newProfileActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val firstName = data.getStringExtra(PROFILE_FIRST_NAME)
                val lastName = data.getStringExtra(PROFILE_LAST_NAME)

                if (lastName != null && firstName != null) {
                    profilesListViewModel.insertProfile(firstName, lastName)
                }
            }
        }
    }
}