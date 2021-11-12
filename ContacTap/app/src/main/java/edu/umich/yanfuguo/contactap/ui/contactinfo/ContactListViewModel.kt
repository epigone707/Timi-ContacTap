package edu.umich.yanfuguo.contactap.ui.contactinfo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recyclersample.data.DataSource
import com.example.recyclersample.data.ContactDataSource
import kotlin.random.Random

class ContactListViewModel(val dataSource: ContactDataSource) : ViewModel() {

    val contactLiveData = dataSource.getContactList()

    /* If the name and description are present, create new Flower and add it to the datasource */
    fun insertContact(contactFirstName: String, contactLastName: String) {
        if (contactFirstName == null || contactLastName == null) {
            return
        }

        val newContact = Contact(
            Random.nextLong(),
            contactFirstName,
            contactLastName,
        )

        dataSource.addContact(newContact)
    }
}

class ContactListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactListViewModel(
                dataSource = ContactDataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}