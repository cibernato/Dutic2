package com.example.dutic2

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.dutic2.adapters.ExpandableListAdapter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    lateinit var drawerLayout: DrawerLayout
    lateinit var navController: NavController



    lateinit var expandableListView: ExpandableListView
    var titles: MutableList<String> = ArrayList()
    var items: MutableList<MutableList<String>> = ArrayList()
    lateinit var adapter: ExpandableListAdapter
    lateinit var mDrawerToggle: ActionBarDrawerToggle
    lateinit var navView: NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        mFirebaseAuth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)


   /*     navView = findViewById(R.id.nav_view)
        expandableListView = this.findViewById(R.id.navList)
        setupDrawerContent(navView)
        genData()
        addDrawersItem()
        expandableListView.setAdapter(adapter)*/



          navView = findViewById(R.id.nav_view)
         navController  = findNavController(R.id.nav_host_fragment)
         appBarConfiguration = AppBarConfiguration(
             setOf(
                 R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                 R.id.nav_tools, R.id.nav_share, R.id.nav_send, R.id.cerrar_sesion
             ), drawerLayout
         )
         setupActionBarWithNavController(navController, appBarConfiguration)
         navView.setNavigationItemSelectedListener(this)


        mAuthStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                Toast.makeText(this, "logged in", Toast.LENGTH_SHORT).show()
            } else {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.mipmap.logo)
                        .setIsSmartLockEnabled(false)
                        .build(),
                    589
                )
            }

        }
    }

    private fun setupDrawerContent(navView: NavigationView?) {
        navView?.setNavigationItemSelectedListener {
            it.isChecked = true
            drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        mDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle.onConfigurationChanged(newConfig)
    }

    private fun selectFirstItemAsDefault() {

    }

    private fun setupDrawer() {
        mDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                supportActionBar?.title = resources.getString(R.string.app_name)
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                supportActionBar?.title = "Cerrado"
                invalidateOptionsMenu()
            }
        }
        mDrawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.setDrawerListener(mDrawerToggle)
    }

    private fun addDrawersItem() {
        adapter = ExpandableListAdapter(this, titles, items)
        expandableListView.setAdapter(adapter)
        expandableListView.setOnGroupExpandListener {
            supportActionBar?.title = titles[it]
        }
        expandableListView.setOnGroupCollapseListener {
            supportActionBar?.title = resources.getString(R.string.app_name)
        }
        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val sItem = items[childPosition]
            Toast.makeText(this, sItem.toString(), Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setOnChildClickListener true
        }

    }


    private fun genData() {

        titles.add("elemento 1")
        titles.add("elemento 2")
        titles.add("elemento 3")
        titles.add("elemento 4")
        val ele1 = ArrayList<String>()
        ele1.add("item 1")
        ele1.add("item 2")
        ele1.add("item 3")
        val ele2 = ArrayList<String>()
        ele2.add("item 4")
        ele2.add("item 5")
        ele2.add("item 6")
        items.add(ele1)
        items.add(ele2)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
            }
            R.id.nav_gallery -> {
                navController.navigate(R.id.nav_gallery)
            }
            R.id.nav_slideshow -> {
                navController.navigate(R.id.nav_slideshow)
            }
            R.id.nav_tools -> {
                navController.navigate(R.id.nav_tools)
            }
            R.id.nav_share -> {
                navController.navigate(R.id.nav_share)
            }
            R.id.nav_send -> {
                navController.navigate(R.id.nav_send)
            }
            R.id.cerrar_sesion -> {
                cerrarSesion()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun cerrarSesion() {
        AuthUI.getInstance().signOut(this)
    }


    override fun onPause() {
        super.onPause()
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 589) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // ...
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish()
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (mDrawerToggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }*/
}
