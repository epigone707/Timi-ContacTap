package edu.umich.yanfuguo.contactap.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import edu.umich.yanfuguo.contactap.databinding.FragmentHomeBinding
import edu.umich.yanfuguo.contactap.model.MyInfoStore
import edu.umich.yanfuguo.contactap.model.MyInfoStore.myInfo
import edu.umich.yanfuguo.contactap.model.ProfileStore
import org.json.JSONException
import org.json.JSONObject
import edu.umich.yanfuguo.contactap.R
import android.util.Log
import edu.umich.yanfuguo.contactap.model.MyInfoStore.getOverview


class HomeFragment : Fragment() {

    private var fragmentHomeBinding: FragmentHomeBinding? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        fragmentHomeBinding = binding
        val preview = getOverview()
        Log.d("HomeFragment",preview)
        binding.cardTitle.text = myInfo.name
        binding.cardInfo.text = preview.removeSuffix("\n")
        binding.cardView.setOnClickListener{
            val intent = Intent(activity, ContactInfoActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentHomeBinding = null
        super.onDestroyView()
    }



}