package edu.umich.yanfuguo.contactap

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import edu.umich.yanfuguo.contactap.model.ConnectionStore
import edu.umich.yanfuguo.contactap.model.Contact
import edu.umich.yanfuguo.contactap.nfc.NdefMessageParser
import edu.umich.yanfuguo.contactap.ui.contactList.ContactActivity
import edu.umich.yanfuguo.contactap.ui.contactList.ContactListActivity
import edu.umich.yanfuguo.contactap.ui.profileList.ProfileActivity

open class ReceiveActivity : AppCompatActivity() {
    private val CHANNEL_ID = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also {
                val msg = it[0] as NdefMessage
                val builder = StringBuilder()
                NdefMessageParser.parse(msg).forEach { record->
                    builder.append(record.str()).append("\n")
                }
                ConnectionStore.init(this)
                val _contact = Gson().fromJson(builder.toString(), Contact::class.java)
                ConnectionStore.insert(this, _contact)
                ConnectionStore.createConnection(this, _contact.profileId, _contact.includeBitString, "")

                val _intent = Intent(this, ContactActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, _intent, 0)
                createNotificationChannel()
                val notification_builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_person_24)
                    .setContentTitle("ContacTap")
                    .setContentText("New contact received!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                // Issue the notification.
                with(NotificationManagerCompat.from(this)) {
                    notify(0, notification_builder.build())
                }
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "Contact", importance).apply {
                description = "Notify on new contact received"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}