package edu.umich.yanfuguo.contactap.ui

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.databinding.FragmentContactInfoBinding
import edu.umich.yanfuguo.contactap.display
import edu.umich.yanfuguo.contactap.model.MyInfoStore
import edu.umich.yanfuguo.contactap.toast

class ContactInfoActivity : AppCompatActivity() {

    private lateinit var contactInfoView: FragmentContactInfoBinding

    private lateinit var forCropResult: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactInfoView = FragmentContactInfoBinding.inflate(layoutInflater)
        setContentView(contactInfoView.root)

        val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach {
                if (!it.value) {
                    toast("${it.key} access denied")
                    finish()
                }
            }
        }.launch(arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE))

        val cropIntent = initCropIntent()

        forCropResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data.let {
                        imageUri?.run {
                            if (!toString().contains("ORIGINAL")) {
                                // delete uncropped photo taken for posting
                                contentResolver.delete(this, null, null)
                            }
                        }
                        imageUri = it
                        imageUri?.let { contactInfoView.previewImage.display(it) }
                    }
                } else {
                    Log.d("Crop", result.resultCode.toString())
                }
            }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            toast("Device has no camera!")
            return
        }

        val takePictureResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri?.let { contactInfoView.previewImage.display(it) }
                doCrop(cropIntent)
            } else {
                Log.d("TakePicture", "failed")
            }
        }

        contactInfoView.cameraButton.setOnClickListener{
            imageUri = mediaStoreAlloc("image/jpeg")
            takePictureResult.launch(imageUri)
        }

        updateContent()
        contactInfoView.doneButton.setOnClickListener {
            saveContent()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    private fun updateContent() {
        contactInfoView.previewImage.setImageURI(Uri.parse(MyInfoStore.myContact.photo))
        contactInfoView.contactNameEdit.setText(MyInfoStore.myContact.name)
        contactInfoView.contactPhoneEdit.setText(MyInfoStore.myContact.phone)
        contactInfoView.contactEmailEdit.setText(MyInfoStore.myContact.email)
        contactInfoView.contactInstaEdit.setText(MyInfoStore.myContact.insta)
        contactInfoView.contactSnapEdit.setText(MyInfoStore.myContact.snap)
        contactInfoView.contactTwitterEdit.setText(MyInfoStore.myContact.twitter)
        contactInfoView.contactLinkedinEdit.setText(MyInfoStore.myContact.linkedin)
        contactInfoView.contactOtherEdit.setText(MyInfoStore.myContact.other)
    }

    private fun saveContent() {
        imageUri?.let { MyInfoStore.myContact.photo = it.toString() }
        contactInfoView.contactNameEdit.text?.let { MyInfoStore.myContact.name = it.toString() }
        contactInfoView.contactPhoneEdit.text?.let{ MyInfoStore.myContact.phone = it.toString() }
        contactInfoView.contactEmailEdit.text?.let{ MyInfoStore.myContact.email = it.toString() }
        contactInfoView.contactInstaEdit.text?.let{ MyInfoStore.myContact.insta = it.toString() }
        contactInfoView.contactSnapEdit.text?.let{ MyInfoStore.myContact.snap = it.toString() }
        contactInfoView.contactTwitterEdit.text?.let{ MyInfoStore.myContact.twitter = it.toString() }
        contactInfoView.contactLinkedinEdit.text?.let{ MyInfoStore.myContact.linkedin = it.toString() }
        contactInfoView.contactOtherEdit.text?.let{ MyInfoStore.myContact.other = it.toString() }
        MyInfoStore.commit(this)
        toast("Contact info saved")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initCropIntent(): Intent? {
        // Is there any published Activity on device to do image cropping?
        val intent = Intent("com.android.camera.action.CROP")
        intent.type = "image/*"
        val listofCroppers = packageManager.queryIntentActivities(intent, 0)
        // No image cropping Activity published
        if (listofCroppers.size == 0) {
            toast("Device does not support image cropping")
            return null
        }

        intent.component = ComponentName(
            listofCroppers[0].activityInfo.packageName,
            listofCroppers[0].activityInfo.name)

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
            imageUri?.let { contactInfoView.previewImage.display(it) }
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

        return contentResolver.insert(
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imageUri = savedInstanceState.getParcelable<Uri>("imageUri")
        imageUri?.let { contactInfoView.previewImage.display(it) }
    }
}
