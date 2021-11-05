package edu.umich.yanfuguo.contactap.ui.ProfileData

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.databinding.ActivityAddProfileBinding

class AddProfileActivity : AppCompatActivity() {
    private lateinit var view: ActivityAddProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityAddProfileBinding.inflate(layoutInflater)
        setContentView(view.root)

        findViewById<Button>(R.id.done_button).setOnClickListener {
            addProfile()
        }
    }

    /* The onClick action for the done button. Closes the activity and returns the new flower name
    and description as part of the intent. If the name or description are missing, the result is set
    to cancelled. */

    private fun addProfile() {
        val resultIntent = Intent()

        if (view.name.text.isNullOrEmpty() || view.description.text.isNullOrEmpty()) {
            setResult(Activity.RESULT_CANCELED, resultIntent)
        } else {
            val firstName = view.name.text.toString()
            val lastName = view.description.text.toString()
            resultIntent.putExtra("name", firstName)
            resultIntent.putExtra("description", lastName)
            setResult(Activity.RESULT_OK, resultIntent)
        }
        finish()
    }
}
