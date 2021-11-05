package edu.umich.yanfuguo.contactap.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.umich.yanfuguo.contactap.databinding.FragmentContactListBinding

class ContactListActivity : AppCompatActivity() {
    lateinit var contactListView: FragmentContactListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactListView = FragmentContactListBinding.inflate(layoutInflater)
        setContentView(contactListView.root)
    }
}