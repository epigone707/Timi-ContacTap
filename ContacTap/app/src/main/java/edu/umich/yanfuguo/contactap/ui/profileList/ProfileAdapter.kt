package edu.umich.yanfuguo.contactap.ui.profileList

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.startActivity
import edu.umich.yanfuguo.contactap.model.Profile
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ProfileItemBinding
import edu.umich.yanfuguo.contactap.model.ProfileStore.delete
import edu.umich.yanfuguo.contactap.ui.ShareActivity

class ProfileAdapter(context: Context, users: ArrayList<Profile?>) :
    ArrayAdapter<Profile?>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView = (convertView?.tag /* reuse binding */ ?: run {
            val rowView = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false)
            rowView.tag = ProfileItemBinding.bind(rowView) // cache binding
            rowView.tag
        }) as ProfileItemBinding

        getItem(position)?.run {
            listItemView.profileTitle.text = name
            listItemView.profileDescription.text = description
            listItemView.shareButton.setOnClickListener{
                val intent = Intent(context, ShareActivity::class.java)
                intent.putExtra("profileId", position)
                context.startActivity(intent)
            }
            listItemView.deleteButton.setOnClickListener{
                delete(context, position)
                notifyDataSetChanged()
            }
            listItemView.editButton.setOnClickListener{
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("position", position)
                context.startActivity(intent)
            }

        }

        return listItemView.root
    }
}
