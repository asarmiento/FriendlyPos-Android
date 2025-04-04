package com.friendlysystemgroup.friendlypos.ventadirecta.fragment

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
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.ventadirecta.activity.VentaDirectaActivity
import com.friendlysystemgroup.friendlypos.ventadirecta.adapters.VentaDirSeleccionarProductoAdapter
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade
import java.util.Locale

class VentaDirSelecProductoFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var realm: Realm? = null
    var recyclerView: RecyclerView? = null
    private var adapter: VentaDirSeleccionarProductoAdapter? = null

    var slecTAB: Int = 0
    var activity: VentaDirectaActivity? = null
    var listaInventario: List<Inventario>? = null
    var datosEnFiltro: Int = 0
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as VentaDirectaActivity
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
        val rootView = inflater.inflate(
            R.layout.fragment_ventadir_selecproducto, container,
            false
        )
        setHasOptionsMenu(true)

        recyclerView =
            rootView.findViewById<View>(R.id.recyclerViewVentaDirectaSeleccProducto) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())
        recyclerView!!.setHasFixedSize(true)

        if (adapter == null) {
            adapter = VentaDirSeleccionarProductoAdapter(
                activity!!, this,
                listProductos
            )
        }

        recyclerView!!.adapter = adapter

        creditoLimite = rootView.findViewById<View>(R.id.restCreditVentaDirecta) as TextView
        listaInventario = listProductos
        Log.d("listaProducto", listProductos.toString() + "")
        creditoDisponible()

        return rootView
    }


    private val listProductos: List<Inventario>
        get() {
            realm = Realm.getDefaultInstance()
            val query =
                realm.where(Inventario::class.java).notEqualTo("amount", "0")
                    .notEqualTo("amount", "0.0").notEqualTo("amount", "0.000")
            val result1 = query.findAll()

            if (result1.isEmpty()) {
                Toast.makeText(
                    SyncObjectServerFacade.getApplicationContext(),
                    "Favor descargar datos primero",
                    Toast.LENGTH_LONG
                ).show()
            }
            return result1
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
    }

    fun creditoDisponible() {
        slecTAB = activity!!.selecClienteTabVentaDirecta

        if (slecTAB == 1) {
            val invoiceDetallePreventa = activity!!.currentInvoice


            val metodoPagoCliente = activity!!.metodoPagoClienteVentaDirecta
            val dueCliente = activity!!.dueClienteVentaDirecta

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
            Log.d("Selec", "No hay productos")
        }
    }

    override fun updateData() {
        if (datosEnFiltro == 1) {
            Log.d("OSCARUpdate", "No actualiza xq esta en $datosEnFiltro")
        } else {
            datosEnFiltro = 0
            adapter!!.updateData(listProductos)
            // adapter2.notifyDataSetChanged();
            Log.d("OSCARUpdate1", "Actualiza xq esta en $datosEnFiltro")
        }

        if (slecTAB == 1) {
            creditoLimiteCliente =
                (getActivity() as VentaDirectaActivity).creditoLimiteClienteVentaDirecta!!.toDouble()
            creditoLimite!!.text =
                "C.Disponible: " + String.format(
                    "%,.2f",
                    creditoLimiteCliente
                )
        } else {
            Log.d("SelecUpdate", "No hay productos")
        }
        // adapter.updateData(getListProductos());
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onQueryTextChange(newText: String): Boolean {
        val filteredModelList = filter(listaInventario!!, newText)
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
            val text = model.description!!.lowercase(Locale.getDefault())
            Log.d("FiltroVentaDirecta", text)
            if (text.contains(query)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }


    companion object {
        private var bill_type = 1
        var creditoLimite: TextView? = null
        var creditoLimiteCliente: Double = 0.0
        val instance: VentaDirSelecProductoFragment
            get() = VentaDirSelecProductoFragment()
    }
}
