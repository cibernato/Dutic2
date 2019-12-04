package com.example.dutic2.negocios

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dutic2.R
import com.example.dutic2.negocios.models.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class NegociosViewModel : ViewModel() {

    private val refTransaciones =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/transacciones")
            .orderBy("codigo")
    private val refTrabajadores =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/trabajadores")
            .orderBy("codigo")
    private val refPermisos =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/permisos").orderBy("codigo")

    private var trabajadoresActualizados =
        MutableLiveData<Array<Trabajador>>().apply { value = arrayOf() }
    private var permisosActualizados = MutableLiveData<Array<Permiso>>().apply { value = arrayOf() }
    private var transaccionesActualizados =
        MutableLiveData<Array<Transaccion>>().apply { value = arrayOf() }

    private val optionTransaciones = FirestoreRecyclerOptions.Builder<Transaccion>()
        .setQuery(refTransaciones, SnapshotParser<Transaccion?> { snapshot: DocumentSnapshot ->
            val retornar = snapshot.toObject(Transaccion::class.java)
            retornar?.id = snapshot.id
            return@SnapshotParser retornar!!
        }).build()

    private val optionTrabajadores = FirestoreRecyclerOptions.Builder<Trabajador>()
        .setQuery(refTrabajadores, SnapshotParser<Trabajador?> { snapshot: DocumentSnapshot ->
            val retornar = snapshot.toObject(Trabajador::class.java)
            retornar?.id = snapshot.id
            return@SnapshotParser retornar!!
        }).build()

    private val optionPermisos = FirestoreRecyclerOptions.Builder<Permiso>()
        .setQuery(refPermisos, SnapshotParser<Permiso?> { snapshot: DocumentSnapshot ->
            val retornar = snapshot.toObject(Permiso::class.java)
            retornar?.id = snapshot.id
            return@SnapshotParser retornar!!
        }).build()

    private val adapterPermiso by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Permiso, PermisoViewHolder>>().apply {
            value = object : FirestoreRecyclerAdapter<Permiso, PermisoViewHolder>(optionPermisos) {

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): PermisoViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.permiso_list_item, parent, false)
                    return PermisoViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: PermisoViewHolder,
                    position: Int,
                    model: Permiso
                ) {
                    holder.bind()
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                    val t = this@apply.value?.snapshots?.toTypedArray()
                    permisosActualizados.postValue(t)
                }
            }
        }
    }

    private val adapterTrabajador by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Trabajador, TrabajadorViewHolder>>().apply {
            value = object :
                FirestoreRecyclerAdapter<Trabajador, TrabajadorViewHolder>(optionTrabajadores) {

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): TrabajadorViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.permiso_list_item, parent, false)
                    return TrabajadorViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: TrabajadorViewHolder,
                    position: Int,
                    model: Trabajador
                ) {
                    holder.bind()
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                    val t = this@apply.value?.snapshots?.toTypedArray()
                    trabajadoresActualizados.postValue(t)
                }
            }
        }
    }

    private val adapterTransaccion by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Transaccion, TranasaccionViewHolder>>().apply {
            value = object :
                FirestoreRecyclerAdapter<Transaccion, TranasaccionViewHolder>(optionTransaciones) {

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): TranasaccionViewHolder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.transaccion_list_item, parent, false)
                    return TranasaccionViewHolder(itemView)
                }

                override fun onBindViewHolder(
                    holder: TranasaccionViewHolder,
                    position: Int,
                    model: Transaccion
                ) {
                    if (model.estadoPermiso == "a" && model.estadoTrabajdor == "a") {
                        holder.bind()
                    }

                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                    val t = this@apply.value?.snapshots?.toTypedArray()
                    transaccionesActualizados.postValue(t)
                }
            }
        }
    }

    fun getAdapterPermiso(): LiveData<FirestoreRecyclerAdapter<Permiso, PermisoViewHolder>> {
        return adapterPermiso
    }

    fun getPermisosActualizados(): LiveData<Array<Permiso>> {
        return permisosActualizados
    }

    fun getAdapterTrabajadores(): LiveData<FirestoreRecyclerAdapter<Trabajador, TrabajadorViewHolder>> {
        return adapterTrabajador
    }

    fun getTrabajadoresActualizados(): LiveData<Array<Trabajador>> {
        return trabajadoresActualizados
    }

    fun getAdapterTransacciones(): LiveData<FirestoreRecyclerAdapter<Transaccion, TranasaccionViewHolder>> {
        return adapterTransaccion
    }

    fun getTransaccionesActualizados(): LiveData<Array<Transaccion>> {
        return transaccionesActualizados
    }

}