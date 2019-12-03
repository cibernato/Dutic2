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

    private val ref_transaciones =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/transacciones")
    private val ref_trabajadores =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/trabajadores")
    private val ref_permisos =
        FirebaseFirestore.getInstance().collection("/negocios/tablas/permisos")

    private var trabajadoresActualizados =
        MutableLiveData<Array<Trabajador>>().apply { value = arrayOf() }
    private var permisosActualizados = MutableLiveData<Array<Permiso>>().apply { value = arrayOf() }
    private var transaccionesActualizados =
        MutableLiveData<Array<Transaccion>>().apply { value = arrayOf() }

    private val option_transaciones = FirestoreRecyclerOptions.Builder<Transaccion>()
        .setQuery(ref_transaciones, SnapshotParser<Transaccion?> { snapshot: DocumentSnapshot ->
            val retornar = snapshot.toObject(Transaccion::class.java)
            return@SnapshotParser retornar!!
        }).build()

    private val option_trabajadores = FirestoreRecyclerOptions.Builder<Trabajador>()
        .setQuery(ref_trabajadores, SnapshotParser<Trabajador?> { snapshot: DocumentSnapshot ->
            val retornar = snapshot.toObject(Trabajador::class.java)
            return@SnapshotParser retornar!!
        }).build()

    private val option_permisos = FirestoreRecyclerOptions.Builder<Permiso>()
        .setQuery(ref_permisos, SnapshotParser<Permiso?> { snapshot: DocumentSnapshot ->
            val retornar = snapshot.toObject(Permiso::class.java)
            return@SnapshotParser retornar!!
        }).build()

    private val adapterPermiso by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Permiso, PermisoViewHolder>>().apply {
            value = object : FirestoreRecyclerAdapter<Permiso, PermisoViewHolder>(option_permisos) {

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
                    holder.bind(model)
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                    val t = this@apply.value?.snapshots?.toTypedArray()
                    permisosActualizados.postValue(t)
                    Log.e("viewModelShared fra", "Noptficia el cambio en datos ")
                }
            }
        }
    }

    private val adapterTrabajador by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Trabajador, TrabajadorViewHolder>>().apply {
            value = object :
                FirestoreRecyclerAdapter<Trabajador, TrabajadorViewHolder>(option_trabajadores) {

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
                    holder.bind(model)
                }

                override fun onError(e: FirebaseFirestoreException) {
                    Log.e("Firestore Adapter", "$e")
                    super.onError(e)
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                    val t = this@apply.value?.snapshots?.toTypedArray()
                    trabajadoresActualizados.postValue(t)
                    Log.e("viewModelShared fra", "Noptficia el cambio en datos ")
                }
            }
        }
    }

    private val adapterTransaccion by lazy {
        MutableLiveData<FirestoreRecyclerAdapter<Transaccion, TranasaccionViewHolder>>().apply {
            value = object :
                FirestoreRecyclerAdapter<Transaccion, TranasaccionViewHolder>(option_transaciones) {

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
                        holder.bind(model)
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
                    Log.e("viewModelShared fra", "Noptficia el cambio en datos ")
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