package edu.umich.yanfuguo.contactap.ui

import android.R.layout.simple_spinner_dropdown_item
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.R.color.share_active
import edu.umich.yanfuguo.contactap.R.color.share_inactive
import edu.umich.yanfuguo.contactap.databinding.ActivityShareBinding
import edu.umich.yanfuguo.contactap.model.MyInfoStore.getMaskedInfo
import edu.umich.yanfuguo.contactap.model.Profile
import edu.umich.yanfuguo.contactap.model.ProfileStore
import edu.umich.yanfuguo.contactap.model.ProfileStore.profiles
import edu.umich.yanfuguo.contactap.nfc.KHostApduService
import edu.umich.yanfuguo.contactap.toast
import edu.umich.yanfuguo.contactap.ui.profileList.ProfileActivity
import org.json.JSONException
import org.json.JSONObject

class ShareActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var isSharing = false

    // the position of the selected profile in profiles
    private var selectedId = 0

    private lateinit var shareView: ActivityShareBinding

    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var mTurnNfcDialog: AlertDialog

    private lateinit var spinnerAdapter :ArrayAdapter<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareView = ActivityShareBinding.inflate(layoutInflater)
        setContentView(shareView.root)

        if (profiles.size == 0) {
            toast("Please create a profile to share")
            finish()
        }


        shareView.editButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            Log.d("toggleEdit", "start ProfileActivity with position=$selectedId")
            intent.putExtra("position", selectedId)
            startActivity(intent)
        }

        // add back (left arrow) button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // init profile selection Spinner
        val items = profiles.map {p ->p?.name}
        spinnerAdapter = ArrayAdapter(this, simple_spinner_dropdown_item, items)
        shareView.profileSelector.adapter = spinnerAdapter
        shareView.profileSelector.setSelection(
            intent.getIntExtra("profileId", selectedId))
        shareView.profileSelector.onItemSelectedListener = this

        // init NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        shareView.forkButton.setOnClickListener{
            val forkName = profiles[selectedId]?.name + "(fork)"
            val forkDescription = "This profile is forked from ${profiles[selectedId]?.name}"

            profiles[selectedId]?.includeBitString?.let { it1 ->
                Profile(forkName, "", forkDescription, it1)
            }?.let { it2 ->
                //TODO: insert() is only used for local testing, change insert() to createProfile()
                ProfileStore.insert(this, it2)
            }
            spinnerAdapter.clear()
            val tempitems = profiles.map {p ->p?.name}
            spinnerAdapter.addAll(tempitems)
            spinnerAdapter.notifyDataSetChanged()
            shareView.profileSelector.setSelection(profiles.size - 1)
        }
    }

    private fun initNFCFunction(): Boolean {
        return if (checkNFCEnable() && packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
            toggleService(true)
        } else {
            showTurnOnNfcDialog()
            false
        }
    }

    private fun toggleService(enable: Boolean): Boolean {
        val intent = Intent(this@ShareActivity, KHostApduService::class.java)
        if (selectedId >= profiles.size) return false

        // gen message
        val info = getMaskedInfo(profiles[selectedId])
        intent.putExtra("ndefMessage", Gson().toJson(info))

        // toggle service
        if(enable) {
            packageManager.setComponentEnabledSetting(
                ComponentName(this@ShareActivity, KHostApduService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
            startService(intent)
        } else {
            packageManager.setComponentEnabledSetting(
                ComponentName(this@ShareActivity, KHostApduService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
            stopService(intent)
        }
        return enable
    }

    private fun checkNFCEnable(): Boolean {
        return if (mNfcAdapter == null) {
            toast(getString(R.string.tv_noNfc))
            false
        } else {
            mNfcAdapter!!.isEnabled
        }
    }

    private fun showTurnOnNfcDialog() {
        mTurnNfcDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.ad_nfcTurnOn_title))
            .setMessage(getString(R.string.ad_nfcTurnOn_message))
            .setPositiveButton(
                getString(R.string.ad_nfcTurnOn_pos)
            ) { _, _ ->
                startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
            }.setNegativeButton(getString(R.string.ad_nfcTurnOn_neg)) { _, _ ->
                onBackPressed()
            }
            .create()
        mTurnNfcDialog.show()
    }

    override fun onPause() {
        if (isSharing) toggleShare(null)
        super.onPause()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        selectedId = pos
        shareView.headerTitle.text = profiles[selectedId]?.name
        shareView.headerSubtitle.text = profiles[selectedId]?.description

        // gen preview
        val info = getMaskedInfo(profiles[selectedId])
        val obj = try { JSONObject(Gson().toJson(info)) } catch (e: JSONException) { JSONObject() }
        var preview = ""
        obj.keys().forEach { k->
            try {
                if (obj.getString(k).isNotEmpty())
                    preview += "$k: ${obj.getString(k)}\n"
            } catch (e: JSONException) {}
        }
        shareView.previewText.text = preview.removeSuffix("\n")
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    fun toggleShare(view: View?){
        if (!isSharing) {
            isSharing = initNFCFunction()
        } else {
            toggleService(false)
            isSharing = false
        }

        // Update UI
        shareView.shareButton.backgroundTintList = ColorStateList.valueOf(
            getColor(if(isSharing) share_active else share_inactive))
        shareView.shareButton.text = if (isSharing) "SHAREING" else "SHARE"
    }



}
