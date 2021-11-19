package edu.umich.yanfuguo.contactap

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import edu.umich.yanfuguo.contactap.databinding.ActivityMainBinding
import edu.umich.yanfuguo.contactap.model.ConnectionStore
import edu.umich.yanfuguo.contactap.model.MyInfoStore
import edu.umich.yanfuguo.contactap.model.ProfileStore
import edu.umich.yanfuguo.contactap.nfc.KHostApduService
import edu.umich.yanfuguo.contactap.ui.ContactInfoActivity
import edu.umich.yanfuguo.contactap.ui.ShareActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_contact_info,R.id.nav_profiles,R.id.nav_contacts, R.id.nav_share, R.id.nav_login
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Restore data model
        ProfileStore.init(this)
        MyInfoStore.init(this)
        ConnectionStore.init(this)

        // Disable sharing by default
        packageManager.setComponentEnabledSetting(
            ComponentName(this@MainActivity, KHostApduService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP)

        if (MyInfoStore.myInfo.name == "") {
            val forWelcome = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_CANCELED) {
                    finish()
                }
            }
            val intent = Intent(this@MainActivity, ContactInfoActivity::class.java)
            forWelcome.launch(intent)
        }
        onNewIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun startShareActivity(view: View?) = startActivity(Intent(this, ShareActivity::class.java))
}