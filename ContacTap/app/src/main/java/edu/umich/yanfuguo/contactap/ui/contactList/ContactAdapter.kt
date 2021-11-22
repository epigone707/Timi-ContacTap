package edu.umich.yanfuguo.contactap.ui.contactList

import android.content.Context
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
        }

        return listItemView.root
    }
}
