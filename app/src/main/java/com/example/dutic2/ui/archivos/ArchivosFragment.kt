package com.example.dutic2.ui.archivos

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.example.dutic2.R
import kotlinx.android.synthetic.main.archivos_fragment.*

class ArchivosFragment : Fragment() {

    companion object {
        fun newInstance() = ArchivosFragment()
    }

    private lateinit var viewModel: ArchivosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.archivos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ArchivosViewModel::class.java)
        viewModel.t.observe(this, Observer {
            archivos_text.text = it
        })
    }

}
