package edu.umich.yanfuguo.contactap.ui.profileList

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ActivityProfileListBinding
import edu.umich.yanfuguo.contactap.model.LoginInfo
import edu.umich.yanfuguo.contactap.model.Profile
import edu.umich.yanfuguo.contactap.model.ProfileStore.createProfile
import edu.umich.yanfuguo.contactap.model.ProfileStore.insert
import edu.umich.yanfuguo.contactap.model.ProfileStore.profiles
import edu.umich.yanfuguo.contactap.ui.SignInActivity

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
        if(LoginInfo.idToken==null) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.dialog_text)
            builder.setPositiveButton(
                    R.string.dialog_login
                ) { _, _ ->
                    // User clicked OK button
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
            builder.setNegativeButton(
                "No",
            ){ dialog, _ ->
                    dialog.cancel()
                }
            builder.setNeutralButton("No. I want to have a taste first."){ dialog, _ ->
                dialog.cancel()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=DIosQAuC2qU"))
                startActivity(intent);
            }
            builder.setTitle("Sorry!")
            val dialog = builder.create()
            dialog.show()

            Log.d("ProfileListActivity", "not login. show alert dialog.")
        }else{
            Log.d("ProfileListActivity","already login.")
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
                createProfile(this, "10000000000000", name, description)
                {
                    profileAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
