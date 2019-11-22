package com.example.dutic2.ui.notasDeVoz

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.example.dutic2.models.GrabacionModel_
import com.example.dutic2.utils.GrabacionesController
import kotlinx.android.synthetic.main.fragment_notas_de_voz.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FilenameFilter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator


class NotasDeVozFragment : Fragment(), GrabacionesController.OpcionesClickListener,
    GrabacionesController.PlayPauseClickListener {

    var isPlaying = false
    var running = false
    var pauseOffSet = 0
    private lateinit var notasDeVozViewModel: NotasDeVozViewModel
    var mRecorder: MediaRecorder? = null
    lateinit var curso: Curso
    private lateinit var pathCarpeta: File
    private var grabaciones = arrayListOf<Uri>()
    private var duraciones = arrayListOf<String>()
    var mPlayer = MediaPlayer()
    private val LOG_TAG = "AudioRecording"
    private val REQUEST_AUDIO_PERMISSION_CODE = 1
    lateinit var gController: GrabacionesController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notasDeVozViewModel =
            ViewModelProviders.of(this).get(NotasDeVozViewModel::class.java)
        curso = arguments?.getSerializable("curso") as Curso
        return inflater.inflate(R.layout.fragment_notas_de_voz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!checkPermissions()) requestPermissions()
        notas_voz_stop_button.isEnabled = false
        notas_voz_pause_button.isEnabled = false
        crearCarpeta()
        val recyclerViewGrabaciones = notas_voz_lista_grabaciones as EpoxyRecyclerView
        notasDeVozViewModel.viewModelScope.launch {
            llenarGrabaciones()
        }
        gController = GrabacionesController(grabaciones, duraciones, this, this)
        recyclerViewGrabaciones.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerViewGrabaciones.setControllerAndBuildModels(gController)
        notas_voz_rec_button.setOnClickListener {
            empezarGrabacion()
        }
        notas_voz_stop_button.setOnClickListener {
            detenerGrabacion()
        }
    }

    private fun crearCarpeta() {
        val folder =
            File(Environment.getExternalStorageDirectory().toString() + File.separator + getString(R.string.app_name) + File.separator + curso.nombre + File.separator + "Recordings")
        if (!folder.exists()) {
            folder.mkdirs()
            pathCarpeta = folder
            Log.e("Creacioon de carptea", "Carpeta creada :${folder.absolutePath}")
        } else {
            pathCarpeta = folder
        }
    }

    private fun detenerGrabacion() {
        try {
            notas_voz_stop_button.isEnabled = false
            notas_voz_pause_button.isEnabled = false
            mRecorder?.stop()
            mRecorder?.release()
            mRecorder = MediaRecorder()
            if (running) {
                cronometro.stop()
                cronometro.base = SystemClock.elapsedRealtime()
//                pauseOffSet = SystemClock.elapsedRealtime() - cronometro.base
                running = false
            }
            notasDeVozViewModel.viewModelScope.launch {
                llenarGrabaciones()
            }
            Toast.makeText(context, "Recording Stopped", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun empezarGrabacion() {
        if (checkPermissions()) {
            notas_voz_pause_button.isEnabled = false
            notas_voz_stop_button.isEnabled = true
            mRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(crearGrabacionFile())
                try {
                    prepare()
                    start()
                    if (!running) {
                        cronometro.base = SystemClock.elapsedRealtime() - pauseOffSet
                        cronometro.start()
                        running = true
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "prepare() failed")
                    e.printStackTrace()
                }
            }
            Toast.makeText(context, "Recording Started", Toast.LENGTH_LONG)
                .show()
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun crearGrabacionFile(): String? {
        val timeStamp = SimpleDateFormat("ddMMyyyy").format(Date())
        val nombre = curso.nombre + timeStamp + "_"
        var image: File? = null
        try {
            image = File.createTempFile(nombre, ".mp3", pathCarpeta)
        } catch (e: Exception) {
            Log.d("my log", "Excep: $e")
        }
        return image?.absolutePath
    }

    private fun llenarGrabaciones() {
        grabaciones.clear()
        duraciones.clear()
        val mdr = MediaMetadataRetriever()
        try {
            val f = pathCarpeta
            val files = f.listFiles(IMAGE_FILTER)
            for (inFile in files) {
                if (inFile.length() != 0L) {
                    mdr.setDataSource(inFile.absolutePath)
                    val duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val dur = duration.toLong()
                    grabaciones.add(inFile.toUri())
                    duraciones.add(((dur % 60000) / 1000).toString())
                } else {
                    inFile.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mdr.release()
        gController.requestModelBuild()
    }


    private fun checkPermissions(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(context!!, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(context!!, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE
        )
    }

    override fun onPlayPauseClick(model: GrabacionModel_, position: Int, clickedView: View) {
        val image = (clickedView as ImageButton)
        if (mPlayer.isPlaying) {
            mPlayer.pause()
        } else {
            if (mPlayer.currentPosition != 0) {
                mPlayer.start()
            } else {
                try {
                    mPlayer = MediaPlayer()
                    mPlayer.setDataSource(model.path().path)
                    mPlayer.prepare()
                    mPlayer.start()
                    image.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_icon))
                    mPlayer.setOnCompletionListener {
                        it.release()
                        reiniciarMediaPlayer()
                        image.setImageDrawable(resources.getDrawable(R.drawable.ic_play_icon))
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "prepare() failed")
                }
            }
        }
    }

    private fun reiniciarMediaPlayer() {
        mPlayer = MediaPlayer()
    }

    override fun onOpcionesClick(model: GrabacionModel_, position: Int) {
        try {
            model.path().toFile().delete()
            notasDeVozViewModel.viewModelScope.launch {
                llenarGrabaciones()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val EXTENSIONS = arrayOf("mp3")
        internal val IMAGE_FILTER: FilenameFilter = FilenameFilter { _, name ->
            for (ext in EXTENSIONS) {
                if (name.endsWith(".$ext")) {
                    return@FilenameFilter true
                }
            }
            false
        }

    }
}