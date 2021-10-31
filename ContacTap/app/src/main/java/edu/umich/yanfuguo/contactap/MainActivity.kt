package edu.umich.yanfuguo.contactap

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import edu.umich.yanfuguo.contactap.databinding.ActivityMainBinding
import edu.umich.yanfuguo.contactap.ui.ContactInfoActivity
import edu.umich.yanfuguo.contactap.ui.ShareActivity
import edu.umich.yanfuguo.contactap.ui.WelcomeActivity

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
                R.id.nav_home, R.id.nav_contact_info,R.id.nav_profiles,R.id.nav_contacts, R.id.nav_share
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //TODO: FINISH THIS BLOCK OF CODE:
        //Idea:
        //if(user had not set up their wholeset profile)
        //{load WelcomeActivity;}
        //else
        //{stay in home page}

        //if(user had not set up their wholeset profile)
        //{
            val intent = Intent(this, WelcomeActivity::class.java).apply {}
            startActivity(intent)
        //}
        //else
        //{

        //}
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

    fun startContactInfoActivity(view: View?) = startActivity(Intent(this, ContactInfoActivity::class.java))
}