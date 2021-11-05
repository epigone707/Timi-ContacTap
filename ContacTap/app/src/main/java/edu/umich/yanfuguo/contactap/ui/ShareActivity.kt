package edu.umich.yanfuguo.contactap.ui

import android.R.layout.simple_spinner_dropdown_item
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import edu.umich.yanfuguo.contactap.nfc.KHostApduService
import edu.umich.yanfuguo.contactap.databinding.ActivityShareBinding
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.R.color.share_active
import edu.umich.yanfuguo.contactap.R.color.share_inactive
import edu.umich.yanfuguo.contactap.toast
import edu.umich.yanfuguo.contactap.model.ProfileStore.profiles

class ShareActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var isSharing = false
    private var selectedId = 0
    private lateinit var shareView: ActivityShareBinding

    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var mTurnNfcDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareView = ActivityShareBinding.inflate(layoutInflater)
        setContentView(shareView.root)

        // add back (left arrow) button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // init profile selection Spinner
        val items = profiles.map {p ->p?.name}
        val adapter = ArrayAdapter(this, simple_spinner_dropdown_item, items)
        shareView.profileSelector.adapter = adapter
        shareView.profileSelector.setSelection(
            intent.getIntExtra("profileId", selectedId))
        shareView.profileSelector.onItemSelectedListener = this

        // init NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    private fun initNFCFunction(): Boolean {
        return if (checkNFCEnable() && packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
            toggleService(true)
            true
        } else {
            showTurnOnNfcDialog()
            false
        }
    }

    private fun toggleService(enable: Boolean) {
        val intent = Intent(this@ShareActivity, KHostApduService::class.java)
        intent.putExtra("ndefMessage", "Name: Place Holder, Phone: 1234567890, Email:1234567890@umich.edu")
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

    override fun onResume() {
        super.onResume()
        if (isSharing && mNfcAdapter!!.isEnabled) {
            initNFCFunction()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        selectedId = pos
        shareView.headerTitle.text = profiles[selectedId]?.name
        shareView.headerSubtitle.text = profiles[selectedId]?.description
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
