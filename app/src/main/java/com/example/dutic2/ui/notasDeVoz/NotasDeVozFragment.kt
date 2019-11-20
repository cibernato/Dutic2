package com.example.dutic2.ui.notasDeVoz

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.dutic2.R
import kotlinx.android.synthetic.main.fragment_notas_de_voz.*
import java.io.IOException


class NotasDeVozFragment : Fragment() {

    private lateinit var notasDeVozViewModel: NotasDeVozViewModel
    lateinit var mRecorder :MediaRecorder

    var mPlayer = MediaPlayer()
    private val LOG_TAG = "AudioRecording"
    private var mFileName: String? = null
    private val REQUEST_AUDIO_PERMISSION_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notasDeVozViewModel =
            ViewModelProviders.of(this).get(NotasDeVozViewModel::class.java)
        return inflater.inflate(R.layout.fragment_notas_de_voz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notas_voz_stop_button.isEnabled= false
        notas_voz_pause_button.isEnabled= false
        mFileName = Environment.getExternalStorageDirectory().absolutePath
        mFileName += "/AudioRecording.3gp"

        notas_voz_rec_button.setOnClickListener {
            if(checkPermissions()){
                notas_voz_pause_button.isEnabled=true
                notas_voz_stop_button.isEnabled= true

                mRecorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(mFileName)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                    try {
                        prepare()
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "prepare() failed")
                        e.printStackTrace()
                    }

                    start()
                }

                Toast.makeText(context, "Recording Started", Toast.LENGTH_LONG)
                    .show()
            }else{
                requestPermissions()
            }
        }
        notas_voz_stop_button.setOnClickListener {
            notas_voz_stop_button.isEnabled= false
            notas_voz_pause_button.isEnabled= false
            mRecorder.stop()
            mRecorder.release()
            mRecorder = MediaRecorder()
            Toast.makeText(context, "Recording Stopped", Toast.LENGTH_LONG).show()
        }
        play_button.setOnClickListener {

            mPlayer = MediaPlayer()
            try {
                mPlayer.setDataSource(mFileName)
                mPlayer.prepare()
                mPlayer.start()
                Toast.makeText(
                    context,
                    "Recording Started Playing",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Log.e(LOG_TAG, "prepare() failed")
            }

        }

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
}