package com.example.dutic2.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.example.dutic2.negocios.NegociosActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.content_main.*
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
    var cursos: Array<Curso>? = arrayOf()
    var sharedMainViewModel: SharedMainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("Oncreate MEthod", "SE ejecuta en cada camnbio de landscape a viewport")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        sharedMainViewModel = ViewModelProviders.of(this).get(SharedMainViewModel::class.java)
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
                R.id.nav_configuraciones,
                R.id.nav_notas_de_voz,
                R.id.nav_archivos,
                R.id.cerrar_sesion,
                R.id.nav_cursoDetallesFragment,
                R.id.nav_cursoFotos,
                R.id.nav_publicaciones,
                R.id.nav_plantilla,
                R.id.nav_promediosGeneral
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
        sharedMainViewModel?.getCursosActualizados()?.observe(this, androidx.lifecycle.Observer {
            cursos = it
        })
        when (menuItem.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
            }
            R.id.nav_tareas -> {
                navController.navigate(R.id.nav_tareas)
            }
            R.id.nav_promediosGeneral -> {
                val args = Bundle()
                try {
                    args.apply {
                        putParcelableArray("cursos", cursos)
                        putString("flag", "voz")
                    }
                    navController.navigate(R.id.nav_promediosGeneral, args)
                } catch (e: java.lang.Exception) {
                    Log.e("Error en try", "$e, values $cursos , args $args")
                }
            }
            R.id.nav_configuraciones -> {
//                navController.navigate(R.id.nav_configuraciones)
                startActivity(Intent(this, NegociosActivity::class.java))
            }

            R.id.nav_notas_de_voz -> {
                val args = Bundle()
                try {
                    args.apply {
                        putParcelableArray("cursos", cursos)
                        putString("flag", "voz")
                    }
                    navController.navigate(R.id.nav_plantilla, args)
                } catch (e: java.lang.Exception) {
                    Log.e("Error en try", "$e, values $cursos , args $args")
                }
            }
            R.id.nav_archivos -> {
                val args = Bundle()
                try {
                    args.apply {
                        putParcelableArray("cursos", cursos)
                        putString("flag", "archivos")
                    }
                    navController.navigate(R.id.nav_plantilla, args)
                } catch (e: java.lang.Exception) {
                    Log.e("Error en try", "$e, values $cursos , args $args")
                }
            }
            R.id.cerrar_sesion -> {
                cerrarSesion()
            }
            R.id.nav_galeria -> {
                val args = Bundle()
                try {
                    args.apply {
                        putParcelableArray("cursos", cursos)
                        putString("flag", "imagenes")
                    }
                    navController.navigate(R.id.nav_plantilla, args)
                } catch (e: java.lang.Exception) {
                    Log.e("Error en try", "$e, values $cursos , args $args")
                }
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

    fun setColorBar(color: Int) {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            AlertDialog.Builder(this).setTitle(getString(R.string.cambiar_idioma_titulo))
                .setMessage(getString(R.string.cambiar_idioma))
                .setPositiveButton(getString(R.string.si)) { dialog, _ ->
                    val res = resources
                    val dm = res.displayMetrics
                    val conf = res.configuration
                    if (resources.configuration.locale.language == "es") {
                        val myLocale = Locale("en")
                        conf.locale = myLocale
                        res.updateConfiguration(conf, dm)
                    } else if (resources.configuration.locale.language == "en") {
                        val myLocale = Locale("es")
                        conf.locale = myLocale
                        res.updateConfiguration(conf, dm)
                    }
                    val refresh = Intent(this, MainActivity::class.java)
                    dialog.dismiss()
                    this.finish()
                    startActivity(refresh)
                }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
            Log.e("onOptionsItemSelected", "Entra ")

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val e = NavHostFragment.findNavController(nav_host_fragment).currentDestination!!.id
        if (e == R.id.nav_home) {
            val context = this
            val builder = AlertDialog.Builder(context)
            builder.setMessage("¿Desea salir de la aplicacion?")
                .setCancelable(false)
                .setPositiveButton("Si") { _, _ ->
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }.show()

        }else{
            super.onBackPressed()
        }
    }
}
