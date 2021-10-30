package edu.umich.yanfuguo.contactap.ui.contactinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.FragmentContactInfoBinding

class ContactInfoFragment : Fragment() {

    private lateinit var contactInfoViewModel: ContactInfoViewModel
    private var _binding: FragmentContactInfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contactInfoViewModel =
            ViewModelProvider(this).get(ContactInfoViewModel::class.java)

        _binding = FragmentContactInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}