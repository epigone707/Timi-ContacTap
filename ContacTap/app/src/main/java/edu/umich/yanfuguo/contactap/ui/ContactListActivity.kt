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
import edu.umich.yanfuguo.contactap.ui.contactinfo.Contact
import edu.umich.yanfuguo.contactap.ui.contactinfo.ContactAdapter
import edu.umich.yanfuguo.contactap.ui.contactinfo.ContactListViewModel
import edu.umich.yanfuguo.contactap.ui.contactinfo.ContactListViewModelFactory

const val CONTACT_ID = "contact_id"

class ContactListActivity: AppCompatActivity() {
    private val newContactActivityRequestCode = 1
    private val contactsListViewModel by viewModels<ContactListViewModel> {
        ContactListViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        val contactAdapter = ContactAdapter { contact -> adapterOnClick(contact) }
        //val concatAdapter = ConcatAdapter(headerAdapter, profileAdapter)

        val recyclerView: RecyclerView = findViewById(R.id.contact_recycler_view)
        recyclerView.adapter = contactAdapter

        contactsListViewModel.contactLiveData.observe(this, {
            it?.let {
                contactAdapter.submitList(it as MutableList<Contact>)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun adapterOnClick(contact: Contact) {
        // start the contact edit activity
    }
}