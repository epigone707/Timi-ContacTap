package edu.umich.yanfuguo.contactap.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import edu.umich.yanfuguo.contactap.R
import edu.umich.yanfuguo.contactap.model.ConnectionStore
import edu.umich.yanfuguo.contactap.model.LoginInfo
import edu.umich.yanfuguo.contactap.model.MyInfoStore
import edu.umich.yanfuguo.contactap.model.MyInfoStore.serverUrl
import edu.umich.yanfuguo.contactap.model.ProfileStore
import edu.umich.yanfuguo.contactap.toast
import org.json.JSONObject


/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mStatusTextView: TextView? = null
    //private var idToken :String? = null
    private lateinit var queue: RequestQueue
    //private val clientId = LoginInfo.clientId
    //private val navHeaderText= findViewById<TextView>(R.id.nav_header_text)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Views
        mStatusTextView = findViewById(R.id.status)

        // Button listeners
        findViewById<View>(R.id.sign_in_button).setOnClickListener(this)
        findViewById<View>(R.id.sign_out_button).setOnClickListener(this)
        //findViewById<View>(R.id.disconnect_button).setOnClickListener(this)

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(LoginInfo.clientId)
            .requestEmail()
            .build()
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT)
        // [END customize_button]
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    public override fun onStart() {
        super.onStart()

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
        // [END on_start_sign_in]
    }

    // [START onActivityResult]
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // [END onActivityResult]
    // [START handleSignInResult]
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            LoginInfo.idToken = account.idToken
            LoginInfo.displayName = account.displayName
            // SEND TO BACKEND
            val jsonObj = mapOf(
                "idToken" to LoginInfo.idToken,
                "displayName" to LoginInfo.displayName,
                "clientId" to LoginInfo.clientId
            )

            val postRequest = JsonObjectRequest(
                Request.Method.POST,
                serverUrl+"login/", JSONObject(jsonObj),
                { Log.d("login", "Logged in!") },
                { error -> Log.e("login", error.localizedMessage ?: "JsonObjectRequest error") }
            )

            if (!this::queue.isInitialized) {
                queue = newRequestQueue(applicationContext)
            }
            queue.add(postRequest)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    // [END handleSignInResult]
    // [START signIn]
    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // [END signIn]
    // [START signOut]
    private fun signOut() {
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
    }

    // [END signOut]
    // [START revokeAccess]
    private fun revokeAccess() {
        mGoogleSignInClient!!.revokeAccess()
            .addOnCompleteListener(this) {
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
    }

    // [END revokeAccess]
    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            mStatusTextView!!.text = getString(R.string.signed_in_fmt, account.displayName)
            //navHeaderText.text = getString(R.string.signed_in_fmt, account.displayName)
            findViewById<View>(R.id.sign_in_button).visibility = View.GONE
            findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.VISIBLE
            findViewById<View>(R.id.restore_button).visibility = View.VISIBLE
        } else {
            mStatusTextView!!.setText(R.string.signed_out)
            //navHeaderText.text = getString(R.string.signed_out)
            findViewById<View>(R.id.sign_in_button).visibility = View.VISIBLE
            findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.GONE
            findViewById<View>(R.id.restore_button).visibility = View.GONE
        }
        LoginInfo.commit(this)
    }

    fun restore(v: View){
        MyInfoStore.getMyInfo(this) { toast("Contact info restored") }
        ProfileStore.getProfiles(this) { toast("Profiles restored") }
        ConnectionStore.getMyConnections(this) { toast("Connection restored") }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
            R.id.sign_out_button -> signOut()
            //R.id.disconnect_button -> revokeAccess()
        }
    }

    companion object {
        private const val TAG = "SignInActivity"
        private const val RC_SIGN_IN = 9001
    }
}