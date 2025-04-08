package com.friendlysystemgroup.friendlypos.distribucion.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.databinding.FragmentDistSelecProductoBinding
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrResumenAdapter
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.util.TotalizeHelper
import com.friendlysystemgroup.friendlypos.modelos.Producto
import io.realm.Realm
import io.realm.RealmResults
import java.util.Locale

class DistSelecProductoFragment : BaseFragment() {
    private var binding: FragmentDistSelecProductoBinding? = null
    private var realm: Realm? = null
    var recyclerView: RecyclerView? = null
    private var adapter: DistrSeleccionarProductosAdapter? = null
    private var adapter2: DistrResumenAdapter? = null
    private val resumenFrag1: DistResumenFragment? = null
    var slecTAB: Int = 0
    var activity: DistribucionActivity? = null
    var totalizeHelper: TotalizeHelper? = null
    var datosEnFiltro: Int = 0
    private var productos: List<Producto> = listOf()
    private val productosFilter: MutableList<Producto> = mutableListOf()

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as DistribucionActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDistSelecProductoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
    }

    private fun setupViews() {
        binding?.apply {
            recyclerViewDistProductos?.layoutManager = LinearLayoutManager(context)
            
            productos = listaProductos
            productosFilter.clear()
            productosFilter.addAll(productos)
            
            adapter = DistrSeleccionarProductosAdapter(
                context,
                this@DistSelecProductoFragment.activity,
                productosFilter
            )
            recyclerViewDistProductos?.adapter = adapter
            
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    filterList(newText)
                    return true
                }
            })
        }
    }

    private fun filterList(filterText: String) {
        productosFilter.clear()
        
        if (filterText.isEmpty()) {
            productosFilter.addAll(productos)
        } else {
            val lowerCaseFilter = filterText.lowercase(Locale.getDefault())
            
            productos.forEach { producto ->
                val nombre = producto.nombre?.lowercase(Locale.getDefault()) ?: ""
                val codigo = producto.codigo?.lowercase(Locale.getDefault()) ?: ""
                
                if (nombre.contains(lowerCaseFilter) || codigo.contains(lowerCaseFilter)) {
                    productosFilter.add(producto)
                }
            }
        }
        
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        realm?.close()
    }

    fun creditoDisponible() {
        slecTAB = activity!!.selecClienteTab

        if (slecTAB == 1) {
            val metodoPagoCliente = activity!!.metodoPagoCliente
            val dueCliente = activity!!.dueCliente

            Log.d("PagoProductoSelec", metodoPagoCliente + "")
            Log.d("PagoProductoSelec", creditoLimiteCliente.toString() + "")
            Log.d("PagoProductoSelec", dueCliente + "")

            if (metodoPagoCliente == "1") {
                bill_type = 1
                creditoLimite!!.visibility =
                    View.GONE
            } else if (metodoPagoCliente == "2") {
                bill_type = 2
                try {
                    creditoLimite!!.visibility =
                        View.VISIBLE
                    creditoLimite!!.text =
                        "C.Disponible: " + String.format(
                            "%,.2f",
                            creditoLimiteCliente
                        )
                } catch (e: Exception) {
                    Log.d("JD", "Error " + e.message)
                }
            }
        } else {
            Toast.makeText(getActivity(), "nadaSelecProducto", Toast.LENGTH_LONG).show()
        }
    }

    override fun updateData() {
        if (datosEnFiltro == 1) {
            Log.d("OSCARUpdate", "No actualiza xq esta en $datosEnFiltro")
        } else {
            datosEnFiltro = 0
            adapter!!.updateData(listProductos)
            adapter2!!.notifyDataSetChanged()
            Log.d("OSCARUpdate1", "Actualiza xq esta en $datosEnFiltro")
        }


        if (slecTAB == 1) {
            creditoLimiteCliente =
                (getActivity() as DistribucionActivity).creditoLimiteCliente!!.toDouble()
            creditoLimite!!.text =
                "C.Disponible: " + String.format(
                    "%,.2f",
                    creditoLimiteCliente
                )
        } else {
            Toast.makeText(getActivity(), "nadaSelecProducto", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredModelList = filter(listProductos, newText)
        adapter!!.setFilter(filteredModelList)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }


    private fun filter(models: List<Inventario>, query: String): List<Inventario> {
        var query = query
        if (query.isEmpty()) {
            Log.d("OSCARVAC", "esta vacio la consulta")
            datosEnFiltro = 0
        } else {
            datosEnFiltro = 1

            Log.d("OSCARLLE", "esta llena la consulta")
        }

        query = query.lowercase(Locale.getDefault())

        val filteredModelList: MutableList<Inventario> = ArrayList()
        for (model in models) {
            if (model.description == null) {
            } else {
                val text = model.description.lowercase(Locale.getDefault())
                Log.d("dasdad", text)
                if (text.contains(query)) {
                    filteredModelList.add(model)
                }
            }
        }
        return filteredModelList
    }

    val listaProductos: List<Producto>
        get() {
            val realm = Realm.getDefaultInstance()
            try {
                val productos: RealmResults<Producto> = realm.where(Producto::class.java)
                    .equalTo("isVisible", true)
                    .findAll()
                
                return realm.copyFromRealm(productos)
            } finally {
                realm.close()
            }
        }

    companion object {
        private var bill_type = 1
        var creditoLimite: TextView? = null
        var creditoLimiteCliente: Double = 0.0
        val instance: DistSelecProductoFragment
            get() = DistSelecProductoFragment()
    }
}
