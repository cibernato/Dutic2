package com.example.dutic2.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.CalendarContract
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
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
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val REQUEST_AUDIO_PERMISSION_CODE = 1
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
    var gson = Gson()
    var cursos: Array<Curso>? = arrayOf()
    var sharedMainViewModel: SharedMainViewModel? = null
    val permisos = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_CALENDAR
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkPermissions(permisos)) {
            requestPermissions(
                permisos
            )
        }
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        checkNightMode()
        sharedMainViewModel = ViewModelProviders.of(this).get(SharedMainViewModel::class.java)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        checkSharedPreferences()
        registerUserInFirestore()
        AndroidThreeTen.init(this)
        mFirebaseAuth = FirebaseAuth.getInstance()
        drawerLayout = findViewById(R.id.drawer_layout)
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

    private fun checkNightMode() {
        if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            toolbar.background = ColorDrawable(Color.parseColor("#1E212121"))
        }
    }

    private fun checkPermissions(permisions: Array<String>): Boolean {
        var x = true
        permisions.iterator().forEach {
            x = x and (ContextCompat.checkSelfPermission(this, it) == 0)
        }
        return x
        /*val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED*/
    }


    private fun requestPermissions(permisions: Array<String>) {
        ActivityCompat.requestPermissions(
            this,
            permisions,
            REQUEST_AUDIO_PERMISSION_CODE
        )
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


    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        mDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle.onConfigurationChanged(newConfig)
    }

    @SuppressLint("MissingPermission")
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        sharedMainViewModel?.getCursosActualizados()?.observe(this, androidx.lifecycle.Observer {
            cursos = it
        })
        when (menuItem.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.nav_home)
            }
            R.id.nav_promediosGeneral -> {
                val args = Bundle()
                try {
                    args.apply {
                        putParcelableArray("cursos", cursos)
                    }
                    navController.navigate(R.id.nav_promediosGeneral, args)
                } catch (e: java.lang.Exception) {
                    Log.e("Error en try", "$e, values $cursos , args $args")
                }
            }
            R.id.nav_configuraciones -> {
                navController.navigate(R.id.nav_configuraciones)
            }

            R.id.nav_notas_de_voz -> {
                val audioPermisisons = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                pedirPermisosYContinuar(audioPermisisons, "voz")

            }
            R.id.nav_archivos -> {
                val archivosPermisos = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                pedirPermisosYContinuar(archivosPermisos, "archivos")
            }

            R.id.nav_galeria -> {
                val galeriaPermisos = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                pedirPermisosYContinuar(galeriaPermisos, "imagenes")
            }

            R.id.calendario_nav_bar -> {
//                val calendarioPermisos = arrayOf(
//                    Manifest.permission.WRITE_CALENDAR,
//                    Manifest.permission.READ_CALENDAR
//                )
//                pedirPermisosYContinuar(calendarioPermisos,"imagenes")
                /*val cal = Calendar.getInstance()
                val intent = Intent(Intent.ACTION_EDIT)
                intent.data = CalendarContract.Events.CONTENT_URI
                intent.putExtra("beginTime", cal.timeInMillis)
                intent.putExtra("allDay", false)
                intent.putExtra("rrule", "FREQ=DAILY")
                intent.putExtra("endTime", cal.timeInMillis + 60 * 60 * 1000)
                intent.putExtra("title", "A Test Event from android app")
                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, cal.timeInMillis+5*1000)
                    put(CalendarContract.Events.DTEND, cal.timeInMillis + 60 * 60 * 1000)
                    put(CalendarContract.Events.TITLE, "Jazzercise")
                    put(CalendarContract.Events.DESCRIPTION, "Group workout")
                    put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles")
                    put(CalendarContract.Events.CALENDAR_ID, 3)
                }
                val uri: Uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)!!
                Log.e("Prueba Click", "id del evento creado ${uri.lastPathSegment?.toLong()} ")*/
                val startMillis = Calendar.getInstance().timeInMillis

                val builder: Uri.Builder = CalendarContract.CONTENT_URI.buildUpon()
                    .appendPath("time")
                ContentUris.appendId(builder, startMillis)
                val intent = Intent(Intent.ACTION_VIEW)
                    .setData(builder.build())
                startActivityForResult(intent, 654)

//                startActivity(intent)
            }

            R.id.cerrar_sesion -> {
                cerrarSesion()
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun pedirPermisosYContinuar(audioPermisisons: Array<String>, flag: String) {
        if (checkPermissions(audioPermisisons)) {
            val args = Bundle()
            try {
                args.apply {
                    putParcelableArray("cursos", cursos)
                    putString("flag", flag)
                }
                navController.navigate(R.id.nav_plantilla, args)
            } catch (e: java.lang.Exception) {
                Log.e("Error en try", "$e, values $cursos , args $args")
            }
        } else {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Se necesitan permisos",
                Snackbar.LENGTH_LONG
            ).setAction(
                "Cambiar"
            ) {
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivityForResult(settingsIntent, 7)
            }.show()
            requestPermissions(audioPermisisons)
        }
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
        if (requestCode == 654) {
            val c = NavHostFragment.findNavController(nav_host_fragment).currentDestination!!
            when (c.id) {
                R.id.nav_promediosGeneral -> {
                    navView.setCheckedItem(R.id.nav_promediosGeneral)
                }
                else -> {
                    navView.setCheckedItem(R.id.nav_home)

                }
            }

        }
    }

    fun setColorBar(color: Int) {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
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

        } else {
            super.onBackPressed()
        }
    }
}
