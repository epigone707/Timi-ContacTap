package edu.umich.yanfuguo.contactap.ui.contactList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.databinding.FragmentContactListBinding
import edu.umich.yanfuguo.contactap.model.ConnectionStore.connections

class ContactListActivity : AppCompatActivity() {
    lateinit var contactListView: FragmentContactListBinding
    lateinit var contectAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactListView = FragmentContactListBinding.inflate(layoutInflater)
        setContentView(contactListView.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        /* Instantiates headerAdapter and flowersAdapter. Both adapters are added to concatAdapter.
        which displays the contents sequentially */
        contectAdapter = ContactAdapter(this, connections)
        contactListView.contactListView.adapter = contectAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        contectAdapter.notifyDataSetChanged()
    }
}