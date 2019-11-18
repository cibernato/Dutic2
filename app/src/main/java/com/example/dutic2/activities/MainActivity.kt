package com.example.dutic2.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.example.dutic2.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.threetenabp.AndroidThreeTen
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    //    lateinit var expandableListView: ExpandableListView
//    var titles: MutableList<String> = ArrayList()
//    var items: MutableList<MutableList<String>> = ArrayList()
//    private lateinit var adapter: ExpandableListAdapter
    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var registered = false
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        checkSharedPreferences()
        registerUserInFirestore()
        AndroidThreeTen.init(this)
        mFirebaseAuth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
        checkPermisos()
        /*     navView = findViewById(R.id.nav_view)
             expandableListView = this.findViewById(R.id.navList)
             setupDrawerContent(navView)
             genData()
             addDrawersItem()
             expandableListView.setAdapter(adapter)*/
        mDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_tareas,
                R.id.nav_promedio,
                R.id.nav_mensajes,
                R.id.nav_configuraciones,
                R.id.nav_notas_de_voz,
                R.id.nav_archivos,
                R.id.cerrar_sesion,
                R.id.nav_cursoDetallesFragment,
                R.id.nav_cursoFotos,
                R.id.nav_temas
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setNavigationItemSelectedListener(this)
        mAuthStateListener = FirebaseAuth.AuthStateListener {
            if (user != null) {
                Log.d("FirebaseAuth", ".AuthStateListener Logged in")
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkSharedPreferences() {
        registered = sharedPreferences.getBoolean(user?.uid, true)
    }

    private fun registerUserInFirestore() {
        if (registered) {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            try {
                FirebaseFirestore.getInstance().document("/usuarios/${user?.uid}")
                    .set(hashMapOf("fechaCreated" to sdf.format(c.time)))
                    .addOnFailureListener {
                        Log.e("error", "$it, irrecuperable")
                    }.addOnSuccessListener {
                        editor = sharedPreferences.edit()
                        registered = false
                        editor.putBoolean(user?.uid, registered)
                        editor.apply()
                        Log.e("success", "$it, añadido a SharedPrefrences : $registered")

                    }.addOnCompleteListener {
                        Log.e("completed", "$it, completado de crear")
                    }
            } catch (e: Exception) {
                Log.e("CreatingDocumentForUser", "$e")
            }

        } else {
            Log.e("registerUserInFirestore", "Ya registrado no se requiere acciones")
        }
    }

    private fun checkPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
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

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
            }
            R.id.nav_tareas -> {
                navController.navigate(R.id.nav_tareas)
            }
            R.id.nav_promedio -> {
                navController.navigate(R.id.nav_promedio)
            }
            R.id.nav_mensajes -> {
                navController.navigate(R.id.nav_mensajes)
            }
            R.id.nav_configuraciones -> {
                navController.navigate(R.id.nav_configuraciones)
            }
            R.id.nav_notas_de_voz -> {
                navController.navigate(R.id.nav_notas_de_voz)
            }
            R.id.nav_archivos -> {
                navController.navigate(R.id.nav_archivos)
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
        mFirebaseAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
//                val user = FirebaseAuth.getInstance().currentUser
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




    /* private fun selectFirstItemAsDefault() {

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

     private fun setupDrawerContent(navView: NavigationView?) {
         navView?.setNavigationItemSelectedListener {
             it.isChecked = true
             drawerLayout.closeDrawers()
             return@setNavigationItemSelectedListener true
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
     }*/


}
