package edu.umich.yanfuguo.contactap.ui

import android.R.layout.simple_spinner_dropdown_item
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import edu.umich.yanfuguo.contactap.KHostApduService
import edu.umich.yanfuguo.contactap.databinding.ActivityShareBinding
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.R.color.share_active
import edu.umich.yanfuguo.contactap.R.color.share_inactive
import edu.umich.yanfuguo.contactap.toast

class ShareActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var isSharing = false
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
        val items = arrayOf("Personal", "Business", "EECS 441")
        val adapter = ArrayAdapter(this, simple_spinner_dropdown_item, items)
        shareView.profileSelector.adapter = adapter
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
        intent.putExtra("ndefMessage", "somedata")
        if(enable) startService(intent) else stopService(intent)
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
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        toast("Selected profile $pos")
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
