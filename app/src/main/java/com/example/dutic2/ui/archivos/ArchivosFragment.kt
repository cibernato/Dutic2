package com.example.dutic2.ui.archivos

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
import android.provider.DocumentsContract
import android.provider.DocumentsProvider
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.activities.SharedMainViewModel
import com.example.dutic2.adapters.DocumentoAdapter
import com.example.dutic2.models.Curso
import com.example.dutic2.utils.DIRECTORIO_RAIZ
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.archivos_fragment.*
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

class ArchivosFragment : Fragment(), DocumentoAdapter.OnDocumentoClickListener {

    companion object {
        fun newInstance() = ArchivosFragment()
        private val EXTENSIONS = arrayOf("pdf", "ppt", "pptx", "doc", "docx", "xls", "xlsx")
        internal val IMAGE_FILTER: FilenameFilter = FilenameFilter { _, name ->
            for (ext in EXTENSIONS) {
                if (name.endsWith(".$ext")) {
                    return@FilenameFilter true
                }
            }
            false
        }
    }

    var documentos = arrayListOf<Uri>()
    lateinit var photoURI: Uri
    private var saved: Boolean = false
    lateinit var mNotifiManager: NotificationManager
    lateinit var mBuilder: NotificationCompat.Builder
    private lateinit var pathToFile: String
    private var user = FirebaseAuth.getInstance().currentUser
    private lateinit var viewModel: SharedMainViewModel
    lateinit var adapter: DocumentoAdapter
    private lateinit var curso: Curso
    private lateinit var pathCarpeta: File
    var storageReference = FirebaseStorage.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.archivos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(SharedMainViewModel::class.java)
        }
        curso = arguments?.getParcelable<Curso>("curso") as Curso
        crearCarpeta()
        archivos_add_documento.setOnClickListener {
            if (checkPermissionGranted()) {
                selectFileIntent()
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 2)
                }
            }
        }
    }

    override fun onResume() {
        viewModel.viewModelScope.launch {
            llenarDocumentos()
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        archivos_list_documentos.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        adapter = DocumentoAdapter(documentos, this)
        archivos_list_documentos.adapter = adapter
    }

    private fun crearCarpeta() {
        val folder =
            File(DIRECTORIO_RAIZ + File.separator + curso.nombre + File.separator + "Documentos")
        if (!folder.exists()) {
            folder.mkdirs()
            pathCarpeta = folder
            Log.e("Creacioon de carptea", "Carpeta creada :${folder.absolutePath}")
        } else {
            pathCarpeta = folder
        }
    }


    private fun checkPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun selectFileIntent() {
        val intent: Intent
//        val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        val chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.type = "*/*"
        intent = Intent.createChooser(chooseFile, "Escoje un documento")
        startActivityForResult(intent, 1500)
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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1500 && resultCode == Activity.RESULT_OK) {
//            continueAcrossRestarts()
            moverArchivoSeleccionado(data?.data)
            documentos.clear()
            viewModel.viewModelScope.launch {
                llenarDocumentos()
            }

        }
        if (requestCode == 21) {
            if (resultCode == Activity.RESULT_OK) {
                documentos.clear()
                viewModel.viewModelScope.launch {
                    llenarDocumentos()
                }
//                fotos.clear()
//                llenarFotos()
//                imgController.requestModelBuild()
            }
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun moverArchivoSeleccionado(data: Uri?) {
        try {
            val entrada = activity?.contentResolver?.openInputStream(data!!)
            val c = activity?.contentResolver?.query(
                data!!,
                arrayOf(MediaStore.Files.FileColumns.DISPLAY_NAME),
                null,
                null,
                null
            )
            val i = c?.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            c?.moveToFirst()
            val fileName = c?.getString(i!!)
            Log.e("ArchivosFragment", "Posible nombre $fileName")
            c?.close()
            val fileSalida =
                File("$DIRECTORIO_RAIZ${File.separator}${curso.nombre}${File.separator}Documentos${File.separator}$fileName")
            Log.e("ArchivosFragment", "File: ${fileSalida.absolutePath}")

            val salida = FileOutputStream(fileSalida)

            val buf = ByteArray(1024)
            var len = entrada?.read(buf)!!
            while ((len) > 0) {
                salida.write(buf, 0, len)
                len = entrada.read(buf)
            }
            entrada.close()
            salida.close()
        } catch (e: Exception) {
            Log.e("ArchivosFragment", "Error: ${e.printStackTrace()}")
        }
    }

    private fun continueAcrossRestarts() {
        var sessionUri: Uri? = null
        var uploadTask: UploadTask

        mNotifiManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(context!!, "1")
        mBuilder.setContentTitle("Subiendo Foto").setContentText("Subida en Progreso")
            .setSmallIcon(R.drawable.ic_play_icon)
        // [START save_before_restart]
        uploadTask =
            storageReference.getReference("/${user?.uid}/${curso.uid}/documentos/${photoURI.lastPathSegment!!}/")
                .putFile(photoURI)
        uploadTask.addOnProgressListener { taskSnapshot ->
            sessionUri = taskSnapshot.uploadSessionUri
            if (sessionUri != null && !saved) {
                saved = true
                // A persisted session has begun with the server.
                // Save this to persistent storage in case the process dies.
            }
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            mBuilder.setProgress(100, progress.toInt(), false)
            mNotifiManager.notify(1, mBuilder.build())
            Log.e("Porcentaje ", "Upload is $progress% done")
        }.addOnCompleteListener {
            mBuilder.setContentText("Subida finalizada").setProgress(0, 0, false)
            mNotifiManager.notify(1, mBuilder.build())

        }
        uploadTask = storageReference.reference.putFile(
            photoURI,
            StorageMetadata.Builder().build(), sessionUri
        )
        // [END restore_after_restart]
    }

    private fun llenarDocumentos() {
//        Log.e("ArchivosFragment", "llenarDocumentos: ${pathCarpeta.absolutePath}")
        documentos.clear()
        val f = pathCarpeta
        val files = f.listFiles(IMAGE_FILTER)
//        Log.e("ArchivosFragment", "llenarDocumentos: ${files?.size}")
        for (inFile in files)
            try {
                if (inFile.length() != 0L) {
                    println("image: " + inFile.name)
                    println(" size  : " + f.length())
                    documentos.add(inFile.toUri())
                } else {
                    inFile.delete()
                }
            } catch (e: Exception) {
            }
        documentos.sortWith(Comparator { o1, o2 ->
            val p1 = o1.toFile()
            val p2 = o2.toFile()

            when {
                p1.lastModified() > p2.lastModified() -> -1
                p1.lastModified() < p2.lastModified() -> +1
                else -> 0
            }
        })
        adapter.notifyDataSetChanged()


    }


    override fun onDocumentoClick(documento: Uri) {
        try {
            val type = MimeTypeMap.getSingleton()
            var mime = ""
            if (documento.lastPathSegment?.get(documento.lastPathSegment?.lastIndex!!) == 'x') {
                val ext =
                    documento.lastPathSegment?.substring(documento.lastPathSegment?.lastIndexOf('.')!! + 1)
                when (ext) {
                    "pptx" -> {
                        mime = "application/vnd.ms-powerpoint"
                    }
                    "docx" -> {
                        mime = "application/msword"
                    }
                    "xlsx" -> {
                        mime = "application/vnd.ms-excel"
                    }
                }
            } else {
                mime = type.getMimeTypeFromExtension(
                    documento.lastPathSegment?.substring(
                        documento.lastPathSegment?.lastIndexOf(".")!! + 1
                    )
                )!!
            }
            Log.e("Documentclick", " mime ${mime}")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                FileProvider.getUriForFile(
                    context!!,
                    "com.example.dutic2.fileprovider",
                    documento.toFile()
                ), mime
            )
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(Intent.createChooser(intent,"Seleccione aplicacion:"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

