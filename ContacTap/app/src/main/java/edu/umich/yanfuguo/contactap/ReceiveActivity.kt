package edu.umich.yanfuguo.contactap

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import edu.umich.yanfuguo.contactap.databinding.ActivityReceiveBinding
import edu.umich.yanfuguo.contactap.model.ConnectionStore
import edu.umich.yanfuguo.contactap.model.ConnectionStore.insert
import edu.umich.yanfuguo.contactap.model.Contact
import edu.umich.yanfuguo.contactap.nfc.NdefMessageParser
import edu.umich.yanfuguo.contactap.ui.contactList.ContactListActivity

class ReceiveActivity : AppCompatActivity() {
    lateinit var binding: ActivityReceiveBinding

    private var mNfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (checkNFCEnable()) {
            mPendingIntent = PendingIntent.getActivity(
                this, 0,
                Intent(this, this.javaClass)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
            )
        }
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                // Process the messages array.
                parserNDEFMessage(messages)
            }
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter?.enableForegroundDispatch(this, mPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        startActivity(Intent(this, ContactListActivity::class.java))
        super.onDestroy()
    }

    private fun parserNDEFMessage(messages: List<NdefMessage>) {
        val builder = StringBuilder()
        val records = NdefMessageParser.parse(messages[0])
        val size = records.size

        for (i in 0 until size) {
            val record = records.get(i)
            val str = record.str()
            builder.append(str).append("\n")
        }
        toast("Contact received!")
        parseMessage(builder.toString())
    }

    private fun checkNFCEnable(): Boolean {
        return if (mNfcAdapter == null) {
            toast(getString(R.string.tv_noNfc))
            false
        } else {
            mNfcAdapter!!.isEnabled
        }
    }

    private fun parseMessage(msg: String) {
        ConnectionStore.init(this)
        val contact = Gson().fromJson(msg, Contact::class.java)
        insert(this, contact)
    }
}