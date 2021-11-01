package edu.umich.yanfuguo.contactap.ui.ProfileData

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import edu.umich.yanfuguo.contactap.R

const val PROFILE_FIRST_NAME = "fname"
const val PROFILE_LAST_NAME = "lname"

class AddProfileActivity : AppCompatActivity() {
    private lateinit var addProfileFirstName: TextInputEditText
    private lateinit var addProfileLastName: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profile)

        findViewById<Button>(R.id.done_button).setOnClickListener {
            addProfile()
        }
        addProfileFirstName = findViewById(R.id.add_first_name)
        addProfileLastName = findViewById(R.id.add_last_name)
    }

    /* The onClick action for the done button. Closes the activity and returns the new flower name
    and description as part of the intent. If the name or description are missing, the result is set
    to cancelled. */

    private fun addProfile() {
        val resultIntent = Intent()

        if (addProfileFirstName.text.isNullOrEmpty() || addProfileLastName.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val firstName = addProfileFirstName.text.toString()
            val lastName = addProfileLastName.text.toString()
            resultIntent.putExtra(PROFILE_FIRST_NAME, firstName)
            resultIntent.putExtra(PROFILE_LAST_NAME, lastName)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}