package edu.umich.yanfuguo.contactap.ui.contactList

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ContactItemBinding
import edu.umich.yanfuguo.contactap.model.ConnectionStore.delete
import edu.umich.yanfuguo.contactap.model.Contact

class ContactAdapter(context: Context, users: ArrayList<Contact?>) :
    ArrayAdapter<Contact?>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
            rowView.tag = ContactItemBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ContactItemBinding

        getItem(position)?.run {
            listItemView.contactName.text = name
            listItemView.deleteButton.setOnClickListener{
                delete(context, position)
                notifyDataSetChanged()
            }
            listItemView.saveButton.setOnClickListener{
                // Creates a new Intent to insert a contact
                val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                    // Sets the MIME type to match the Contacts Provider
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                }
                intent.apply {
                    // Inserts an email address
                    putExtra(ContactsContract.Intents.Insert.NAME, name)
                    if (personalEmail.isNotEmpty()) {
                        putExtra(ContactsContract.Intents.Insert.EMAIL, personalEmail)
                    } else {
                        putExtra(ContactsContract.Intents.Insert.EMAIL, businessEmail)
                        putExtra(
                            ContactsContract.Intents.Insert.EMAIL_TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE_WORK
                        )
                    }
                    // Inserts a phone number
                    if (personalPhone.isNotEmpty()) {
                        putExtra(ContactsContract.Intents.Insert.PHONE, personalPhone)
                    } else {
                        putExtra(ContactsContract.Intents.Insert.PHONE, businessPhone)
                        putExtra(
                            ContactsContract.Intents.Insert.PHONE_TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                        )
                    }
                }
                context.startActivity(intent)
            }
            listItemView.card.setOnClickListener{
                val intent = Intent(context, ContactActivity::class.java)
                intent.putExtra("position", position)
                context.startActivity(intent)
            }
        }

        return listItemView.root
    }
}
