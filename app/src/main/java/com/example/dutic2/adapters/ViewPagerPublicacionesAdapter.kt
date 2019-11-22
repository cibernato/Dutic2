package com.example.dutic2.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.dutic2.models.Curso
import com.example.dutic2.ui.publicaciones.NovedadesFragment
import com.example.dutic2.ui.publicaciones.TareasEntregadasFragment

class ViewPagerPublicacionesAdapter(fm: FragmentManager,var curso :Curso) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                NovedadesFragment.newInstance(curso)
            }
            else -> {
                TareasEntregadasFragment.newInstance(curso)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}