package com.example.dutic2.ui.publicaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dutic2.R
import com.example.dutic2.adapters.ViewPagerPublicacionesAdapter
import com.example.dutic2.models.Curso
import kotlinx.android.synthetic.main.fragment_publicaciones.*


class PublicacionesFragment : Fragment() {
    lateinit var curso: Curso
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        curso = arguments?.getParcelable<Curso>("curso") as Curso
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_publicaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ViewPagerPublicacionesAdapter(fragmentManager!!, curso)
        val t = publicaciones_nombre_curso.text.toString()
        publicaciones_nombre_curso.text = t.format(curso.nombre)
        publicaciones_view_pager.adapter = adapter
        publicaciones_tab_layout.setupWithViewPager(publicaciones_view_pager)
        publicaciones_tab_layout.getTabAt(0)?.setIcon(R.drawable.ic_novedades_icon)?.text =
            "Novedades"
        publicaciones_tab_layout.getTabAt(1)?.setIcon(R.drawable.ic_list_tareas_icon)?.text =
            "Tareas"
        super.onViewCreated(view, savedInstanceState)
    }

}
