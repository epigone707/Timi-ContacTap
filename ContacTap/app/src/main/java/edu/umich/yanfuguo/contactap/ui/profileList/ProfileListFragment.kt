package edu.umich.yanfuguo.contactap.ui.profileList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import edu.umich.yanfuguo.contactap.databinding.FragmentProfileListBinding
import edu.umich.yanfuguo.contactap.model.Profile
import edu.umich.yanfuguo.contactap.model.ProfileStore

class ProfileListFragment : Fragment() {

    private var _binding: FragmentProfileListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var profileAdapter: ProfileAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileListBinding.inflate(layoutInflater)

        profileAdapter = activity?.let { ProfileAdapter(it, ProfileStore.profiles) }
        binding.profileListView.adapter = profileAdapter

        binding.fab.setOnClickListener {
            val intent = Intent(activity, ProfileAddActivity::class.java)
            forAddResult.launch(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private var forAddResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val name = data?.getStringExtra("name")
            val description = data?.getStringExtra("description")

            if (name != null && description != null) {
                activity?.let { ProfileStore.insert(it, Profile(name, description)) }
                profileAdapter?.notifyDataSetChanged()
            }
        }
    }
}
