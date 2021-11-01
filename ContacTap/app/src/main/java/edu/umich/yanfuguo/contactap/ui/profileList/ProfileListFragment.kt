package edu.umich.yanfuguo.contactap.ui.profileList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.FragmentHomeBinding
import edu.umich.yanfuguo.contactap.ui.home.HomeViewModel

class ProfileListFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root: View = inflater.inflate(R.layout.fragment_profile_list, container, false)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}