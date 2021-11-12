package edu.umich.yanfuguo.contactap.ui.contactinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclersample.data.Profile
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.ui.ProfileData.ProfileAdapter

class ContactAdapter(private val onClick: (Contact) -> Unit) :
    ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback) {

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class ContactViewHolder(itemView: View, val onClick: (Contact) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val contactTitleView: TextView = itemView.findViewById(R.id.contactName)
        //private val flowerImageView: ImageView = itemView.findViewById(R.id.flower_image)
        private var currentContact: Contact? = null

        init {
            itemView.setOnClickListener {
                currentContact?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(contact: Contact) {
            currentContact = contact

            contactTitleView.text = contact.firstName
        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)

    }
}

object ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }
}