package edu.umich.yanfuguo.contactap.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.umich.yanfuguo.contactap.databinding.FragmentHomeBinding
import edu.umich.yanfuguo.contactap.model.MyInfoStore.myInfo
import edu.umich.yanfuguo.contactap.R
import android.util.Log
import edu.umich.yanfuguo.contactap.toast
import edu.umich.yanfuguo.contactap.model.LoginInfo
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(LoginInfo.idToken==null) {
            activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.dialog_text)
                builder.apply {
                    setPositiveButton(R.string.dialog_login
                    ) { dialog, _ ->
                        // User clicked OK button
                        val intent = Intent(activity, SignInActivity::class.java)
                        startActivity(intent)
                    }
                    setNegativeButton("No",
                    ){ dialog, _ ->
                            dialog.cancel()
                    }
                    setTitle("Sorry!")
                }
                // Set other dialog properties

                // Create the AlertDialog
                builder.create()
            }?.show()
            Log.d("HomeFragment", "not login. show alert dialog.")
        }else{
            Log.d("HomeFragment","already login.")
        }
    }

    override fun onDestroyView() {
        fragmentHomeBinding = null
        super.onDestroyView()
    }

    override fun onResume() {  // After a pause OR at startup
        super.onResume()
        //Refresh your stuff here
        Log.d("HomeFragment","onResume, refresh")
        val preview = getOverview()
        fragmentHomeBinding?.cardTitle?.text  = myInfo.name
        fragmentHomeBinding?.cardInfo?.text = preview.removeSuffix("\n")
    }



}