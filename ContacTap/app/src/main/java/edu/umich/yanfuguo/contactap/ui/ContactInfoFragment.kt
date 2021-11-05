package edu.umich.yanfuguo.contactap.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.fragment.app.Fragment
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.FragmentContactInfoBinding
import edu.umich.yanfuguo.contactap.display
import edu.umich.yanfuguo.contactap.model.Contact
import edu.umich.yanfuguo.contactap.model.MyInfoStore
import edu.umich.yanfuguo.contactap.model.MyInfoStore.myContact
import edu.umich.yanfuguo.contactap.toast

class ContactInfoFragment : Fragment() {
    private var _binding: FragmentContactInfoBinding? = null

    private lateinit var forCropResult: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentContactInfoBinding.inflate(inflater, container, false)

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { success ->
            if (!success) {
                Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermission.launch(Manifest.permission.CAMERA);

        val cropIntent = initCropIntent()

        forCropResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data.let {
                        imageUri?.run {
                            if (!toString().contains("ORIGINAL")) {
                                // delete uncropped photo taken for posting
                                activity?.contentResolver?.delete(this, null, null)
                            }
                        }
                        imageUri = it
                        imageUri?.let { binding.previewImage.display(it) }
                    }
                } else {
                    Log.d("Crop", result.resultCode.toString())
                }
            }

        if (!activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)!!) {
            Toast.makeText(activity, "No camera", Toast.LENGTH_SHORT).show()
        }

        val takePictureResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let { binding.previewImage.display(it) }
                doCrop(cropIntent)
            } else {
                Log.d("TakePicture", "failed")
            }
        }

        activity?.let {
            binding.cameraButton.setOnClickListener{
                imageUri = mediaStoreAlloc("image/jpeg")
                takePictureResult.launch(imageUri)
            }
        }

        updateContent()
        binding.doneButton.setOnClickListener {
            saveContent()
        }

        return binding.root
    }

    private fun updateContent() {
        binding.previewImage.setImageURI(Uri.parse(myContact.photo))
        binding.contactNameEdit.setText(myContact.name)
        binding.contactPhoneEdit.setText(myContact.phone)
        binding.contactEmailEdit.setText(myContact.email)
        binding.contactInstaEdit.setText(myContact.insta)
        binding.contactSnapEdit.setText(myContact.snap)
        binding.contactTwitterEdit.setText(myContact.twitter)
        binding.contactLinkedinEdit.setText(myContact.linkedin)
        binding.contactOtherEdit.setText(myContact.other)
    }

    private fun saveContent() {
        imageUri?.let { myContact.photo = it.toString() }
        binding.contactNameEdit.text?.let { myContact.name = it.toString() }
        binding.contactPhoneEdit.text?.let{ myContact.phone = it.toString() }
        binding.contactEmailEdit.text?.let{ myContact.email = it.toString() }
        binding.contactInstaEdit.text?.let{ myContact.insta = it.toString() }
        binding.contactSnapEdit.text?.let{ myContact.snap = it.toString() }
        binding.contactTwitterEdit.text?.let{ myContact.twitter = it.toString() }
        binding.contactLinkedinEdit.text?.let{ myContact.linkedin = it.toString() }
        binding.contactOtherEdit.text?.let{ myContact.other = it.toString() }
        context?.let {
            MyInfoStore.commit(it)
            Toast.makeText(activity, "Contact info saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCropIntent(): Intent? {
        // Is there any published Activity on device to do image cropping?
        val intent = Intent("com.android.camera.action.CROP")
        intent.type = "image/*"
        val listofCroppers = activity?.packageManager?.queryIntentActivities(intent, 0)
        // No image cropping Activity published
        if (listofCroppers?.size == 0) {
            Toast.makeText(activity, "Device does not support image cropping", Toast.LENGTH_SHORT).show()
            return null
        }

        // create a square crop box:
        intent.putExtra("outputX", 500)
            .putExtra("outputY", 500)
            .putExtra("aspectX", 1)
            .putExtra("aspectY", 1)
            // enable zoom and crop
            .putExtra("scale", true)
            .putExtra("crop", true)
            .putExtra("return-data", true)

        return intent
    }

    private fun doCrop(intent: Intent?) {
        intent ?: run {
            //imageUri?.let { previewImage.display(it) }
            return
        }

        imageUri?.let {
            intent.data = it
            forCropResult.launch(intent)
        }
    }

    private fun mediaStoreAlloc(mediaType: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.MIME_TYPE, mediaType)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        return activity?.contentResolver?.insert(
            if (mediaType.contains("video"))
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable("imageUri", imageUri)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable<Uri>("imageUri")
        }
        //imageUri?.let { view?.findViewById<ImageView>(R.id.previewImage).display(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}