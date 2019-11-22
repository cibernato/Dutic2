package com.example.dutic2.ui.cursoFotos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.dutic2.R
import com.example.dutic2.activities.ViewPagerDetalleFotosActivity
import com.example.dutic2.models.Curso
import com.example.dutic2.models.FotoModel_
import com.example.dutic2.utils.FotoController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.curso_fotos_fragment.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CursoFotosFragment : Fragment(), FotoController.EpoxyClickListener {

    var fotos = arrayListOf<File>()
    private lateinit var pathToFile: String
    private val references = mutableListOf<String>()
    private var user = FirebaseAuth.getInstance().currentUser
    private lateinit var viewModel: CursoFotosViewModel
    lateinit var archivosController: FotoController
    private var prueba = arrayListOf<String>()
    private lateinit var curso: Curso
    private lateinit var pathCarpeta: File
    var storageReference = FirebaseStorage.getInstance()
    lateinit var photoURI: Uri
    private var saved: Boolean = false
    lateinit var mNotifiManager :NotificationManager
    lateinit var mBuilder : NotificationCompat.Builder

    companion object {
        private val EXTENSIONS = arrayOf("jpg")
        internal val IMAGE_FILTER: FilenameFilter = FilenameFilter { _, name ->
            for (ext in EXTENSIONS) {
                if (name.endsWith(".$ext")) {
                    return@FilenameFilter true
                }
            }
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.curso_fotos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CursoFotosViewModel::class.java)
        curso = arguments?.getSerializable("curso") as Curso
        crearCarpeta()
        viewModel.viewModelScope.launch {
            llenarFotos()
        }
        val recyclerView = curso_fotos_recycler_view as EpoxyRecyclerView
        anadir_foto.setOnClickListener {
            if (checkPermissionGranted()) {
                dispatchTakePictureIntent()
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 2)
                }
            }
        }
        /*storageReference.getReference("/prueba").listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { storageReference ->
                    //                    references.add(storageReference)
                    Log.e("Path", "path= ${storageReference.path}")
                    prueba.add(storageReference.path)
                    archivosController.requestModelBuild()
                }
            }.addOnFailureListener {
                it.printStackTrace()
            }
        try {
            storageReference.getReference("/${user?.uid}").listAll()
                .addOnSuccessListener { listResult ->
                    listResult.items.forEach { storageReference ->
                        references.add(storageReference.path)
//                        prueba.add(storageReference.path)
                        archivosController.requestModelBuild()
                    }
                }.addOnFailureListener {
                    Log.e("FailureListener", "$it")
                }.addOnCompleteListener {
                    Log.e("CompleteListener", " ${it.result}, $it")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
//        recyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        archivosController = FotoController(fotos, this)
        recyclerView.setControllerAndBuildModels(archivosController)

    }

    private fun crearCarpeta() {
        val folder =
            File(Environment.getExternalStorageDirectory().toString() + File.separator + getString(R.string.app_name) + File.separator + curso.nombre)
        if (!folder.exists()) {
            folder.mkdirs()
            pathCarpeta = folder
            Log.e("Creacioon de carptea", "Carpeta creada :${folder.absolutePath}")
        } else {
            pathCarpeta = folder
        }
    }

    override fun onClickEpoxyModel(model: FotoModel_, position: Int) {
        try {

            val i = Intent(context, ViewPagerDetalleFotosActivity::class.java).apply {
                putExtra("paths", fotos)
                putExtra("pos", position)
                putExtra("curso", curso)
            }
            startActivityForResult(i, 21)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile != null) {
                pathToFile = photoFile.absolutePath
                photoURI = FileProvider.getUriForFile(
                    context!!,
                    "com.example.dutic2.fileprovider",
                    photoFile
                )
                Log.e("mmmm", " $photoURI    $pathToFile")
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, 996)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("ddMMyyyy").format(Date())
        val imageFileName = curso.nombre + timeStamp + "_"
        val storgeDir = pathCarpeta
        var image: File? = null
        try {
            image = File.createTempFile(imageFileName, ".jpg", storgeDir)
        } catch (e: IOException) {
            Log.d("my log", "Excep: $e")
        }
        Log.d("my log", "${image?.absolutePath}")
        return image!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 996 && resultCode == Activity.RESULT_OK) {
            continueAcrossRestarts()
            fotos.clear()
            viewModel.viewModelScope.launch {
                llenarFotos()
            }

        }
        if (requestCode == 21) {
            if (resultCode == Activity.RESULT_OK) {
                fotos.clear()
                viewModel.viewModelScope.launch {
                    llenarFotos()
                }
//                fotos.clear()
//                llenarFotos()
//                imgController.requestModelBuild()
            }
        }
    }

    private fun continueAcrossRestarts() {
        var sessionUri: Uri? = null
        var uploadTask: UploadTask

        mNotifiManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(context!! ,"1")
        mBuilder.setContentTitle("Subiendo Foto").setContentText("Subida en Progreso").setSmallIcon(R.drawable.ic_play_icon)
        // [START save_before_restart]
        uploadTask =
            storageReference.getReference("/${user?.uid}/${curso.uid}/${photoURI.lastPathSegment!!}/")
                .putFile(photoURI)
        uploadTask.addOnProgressListener { taskSnapshot ->
            sessionUri = taskSnapshot.uploadSessionUri
            if (sessionUri != null && !saved) {
                saved = true
                // A persisted session has begun with the server.
                // Save this to persistent storage in case the process dies.
            }
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            mBuilder.setProgress(100,progress.toInt(),false)
            mNotifiManager.notify(1,mBuilder.build())
            Log.e("Porcentaje ", "Upload is $progress% done")
        }.addOnCompleteListener {
            mBuilder.setContentText("Subida finalizada").setProgress(0,0,false)
            mNotifiManager.notify(1,mBuilder.build())

        }
        uploadTask = storageReference.reference.putFile(
            photoURI,
            StorageMetadata.Builder().build(), sessionUri
        )
        // [END restore_after_restart]
    }

    private fun llenarFotos() {
        val f = pathCarpeta
        val files = f.listFiles(IMAGE_FILTER)
        for (inFile in files)
            try {
                if (inFile.length() != 0L) {
                    println("image: " + inFile.name)
                    println(" size  : " + f.length())
                    fotos.add(inFile)

                } else {
                    inFile.delete()
                }
            } catch (e: Exception) {
            }
        fotos.sortWith(Comparator { o1, o2 ->
            when {
                o1.lastModified() > o2.lastModified() -> -1
                o1.lastModified() < o2.lastModified() -> +1
                else -> 0
            }
        })
        archivosController.requestModelBuild()
    }
}
