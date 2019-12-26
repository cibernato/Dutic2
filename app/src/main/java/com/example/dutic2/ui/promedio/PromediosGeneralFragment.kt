package com.example.dutic2.ui.promedio


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.activities.SharedMainViewModel
import com.example.dutic2.adapters.PromedioGeneralAdapter
import com.example.dutic2.models.Curso
import kotlinx.android.synthetic.main.fragment_promedios_general.*

/**
 * A simple [Fragment] subclass.
 */
class PromediosGeneralFragment : Fragment(), PromedioGeneralAdapter.PromedioGeneralClickListener {

    var cursos: ArrayList<Curso>? = arrayListOf()
    var cursos2: ArrayList<Curso>? = arrayListOf()
    lateinit var adapter : PromedioGeneralAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val t = arguments?.getParcelableArray("cursos") as Array<Curso>
        cursos?.addAll(t)
        return inflater.inflate(R.layout.fragment_promedios_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("Promedios general", "before $cursos2 ")
        activity?.let {
            val sharedViewModel = ViewModelProviders.of(it).get(SharedMainViewModel::class.java)

            observeInput(sharedViewModel)
        }
        Log.e("Promedios general", "after $cursos2 ")
        promedios_general_recycler_view.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = PromedioGeneralAdapter(cursos2!!, this)
        promedios_general_recycler_view.adapter = adapter

    }

    private fun observeInput(sharedViewModel: SharedMainViewModel) {
        sharedViewModel.getCursosActualizados().observe(this, Observer {
            cursos2?.clear()
            cursos2?.addAll(it)
            adapter.notifyDataSetChanged()
        })

    }

    override fun onPromedioGeneralListener(curso: Curso) {
        findNavController().navigate(R.id.nav_promedio, bundleOf("curso" to curso))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.promedio_general_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_curso_rapido) {
            findNavController().navigate(R.id.nav_promedio, bundleOf("curso" to Curso()))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
