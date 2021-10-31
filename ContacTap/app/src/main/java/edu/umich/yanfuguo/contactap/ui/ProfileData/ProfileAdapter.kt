package edu.umich.yanfuguo.contactap.ui.ProfileData

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

class ProfileAdapter(private val onClick: (Profile) -> Unit) :
    ListAdapter<Profile, ProfileAdapter.ProfileViewHolder>(ProfileDiffCallback) {

    /* ViewHolder for Flower, takes in the inflated view and the onClick behavior. */
    class ProfileViewHolder(itemView: View, val onClick: (Profile) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val profileTitleView: TextView = itemView.findViewById(R.id.profileTitle)
        //private val flowerImageView: ImageView = itemView.findViewById(R.id.flower_image)
        private var currentProfile: Profile? = null

        init {
            itemView.setOnClickListener {
                currentProfile?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(profile: Profile) {
            currentProfile = profile

            profileTitleView.text = profile.firstName
        }
    }

    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_item, parent, false)
        return ProfileViewHolder(view, onClick)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = getItem(position)
        holder.bind(profile)

    }
}

object ProfileDiffCallback : DiffUtil.ItemCallback<Profile>() {
    override fun areItemsTheSame(oldItem: Profile, newItem: Profile): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Profile, newItem: Profile): Boolean {
        return oldItem.id == newItem.id
    }
}