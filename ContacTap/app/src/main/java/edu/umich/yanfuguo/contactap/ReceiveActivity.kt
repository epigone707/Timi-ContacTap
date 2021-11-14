package edu.umich.yanfuguo.contactap

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import edu.umich.yanfuguo.contactap.model.ConnectionStore
import edu.umich.yanfuguo.contactap.model.Contact
import edu.umich.yanfuguo.contactap.nfc.NdefMessageParser

open class ReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also {
                val msg = it[0] as NdefMessage
                val builder = StringBuilder()
                NdefMessageParser.parse(msg).forEach { record->
                    builder.append(record.str()).append("\n")
                }
                toast("Contact received!")
                ConnectionStore.init(this)
                ConnectionStore.insert(this, Gson().fromJson(builder.toString(), Contact::class.java))
            }
        }
    }
}