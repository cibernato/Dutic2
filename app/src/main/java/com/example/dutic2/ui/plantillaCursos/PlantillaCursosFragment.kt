package com.example.dutic2.ui.plantillaCursos

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.dutic2.R
import com.example.dutic2.adapters.PlantillaCursoAdapter
import com.example.dutic2.models.Curso
import kotlinx.android.synthetic.main.plantilla_cursos_fragment.*

class PlantillaCursosFragment : Fragment(), PlantillaCursoAdapter.PlantillaClickListener {

    lateinit var cursos: Array<Curso>
    lateinit var flag: String
    lateinit var adapter: PlantillaCursoAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plantilla_cursos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("erroe on pause", "${arguments?.getSerializable("cursos")}")
        cursos = arguments?.getParcelableArray("cursos") as Array<Curso>
        flag = arguments?.getString("flag")!!
        Log.e("Flag", "incoming $flag")
        adapter = PlantillaCursoAdapter(cursos, this)
        plantilla_cursos_recycler_view.layoutManager =
            GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        plantilla_cursos_recycler_view.adapter = adapter
    }

    companion object {
        fun newInstance() = PlantillaCursosFragment()
    }

    override fun onPlantillaClickListener(curso: Curso) {
        val navCtrl = findNavController()
        val args = bundleOf("curso" to curso)
        when (flag) {
            "voz" -> {
                navCtrl.navigate(R.id.nav_notas_de_voz,args)
            }
            "imagenes"-> {
                navCtrl.navigate(R.id.nav_cursoFotos,args)
            }
            else -> {
                navCtrl.navigate(R.id.nav_archivos)
            }

        }
    }
}
