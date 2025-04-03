package com.friendlypos.preventas.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.preventas.activity.PreventaActivity
import com.friendlypos.preventas.adapters.PrevResumenAdapter
import com.friendlypos.preventas.adapters.PrevSeleccionarProductoAdapter
import com.friendlypos.preventas.util.TotalizeHelperPreventa
import com.friendlypos.principal.modelo.Productos
import io.realm.Realm
import java.util.Locale

class PrevSelecProductoFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var realm: Realm? = null
    var recyclerView: RecyclerView? = null
    private var adapter: PrevSeleccionarProductoAdapter? = null
    private var adapter2: PrevResumenAdapter? = null
    var slecTAB: Int = 0
    var activity: PreventaActivity? = null
    var totalizeHelper: TotalizeHelperPreventa? = null
    var datosEnFiltro: Int = 0

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as PreventaActivity
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
            R.layout.fragment_prev_selecproducto, container,
            false
        )
        setHasOptionsMenu(true)

        recyclerView =
            rootView.findViewById<View>(R.id.recyclerViewPrevSeleccProducto) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())
        recyclerView!!.setHasFixedSize(true)

        if (adapter == null) {
            adapter = PrevSeleccionarProductoAdapter(
                activity!!, this,
                listProductos
            )
        }
        recyclerView!!.adapter = adapter

        creditoLimite = rootView.findViewById<View>(R.id.restCreditPreventa) as TextView
        Log.d("listaProducto", listProductos.toString() + "")
        adapter2 = PrevResumenAdapter()
        creditoDisponible()

        return rootView
    }

    private val listProductos: List<Productos>
        get() {
            realm = Realm.getDefaultInstance()
            val query =
                realm.where(Productos::class.java).equalTo("status", "Activo")
            val result1 = query.findAll()

            return result1
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
    }

    fun creditoDisponible() {
        slecTAB = activity!!.selecClienteTabPreventa

        if (slecTAB == 1) {
            // creditoLimiteCliente = Double.parseDouble(((DistribucionActivity) getActivity()).getCreditoLimiteClienteSlecc());
            //    creditoLimiteCliente = 0.0;
            val invoiceDetallePreventa = activity!!.currentInvoice


            //String metodoPagoCliente = invoiceDetallePreventa.getP_payment_method_id();
            val metodoPagoCliente = activity!!.metodoPagoClientePreventa

            val limite = activity!!.creditoLimiteClientePreventa
            // creditoLimiteCliente = Double.parseDouble(limite);
            val dueCliente = activity!!.dueClientePreventa

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
            adapter2!!.notifyDataSetChanged()
            Log.d("OSCARUpdate1", "Actualiza xq esta en $datosEnFiltro")
        }


        if (slecTAB == 1) {
            creditoLimiteCliente =
                (getActivity() as PreventaActivity).creditoLimiteClientePreventa!!.toDouble()
            creditoLimite!!.text =
                "C.Disponible: " + String.format(
                    "%,.2f",
                    creditoLimiteCliente
                )
        } else {
            Log.d("SelecUpdate", "No hay productos")
        }
        //  adapter.updateData(getListProductos());
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


    private fun filter(models: List<Productos>, query: String): List<Productos> {
        var query = query
        if (query.isEmpty()) {
            Log.d("OSCARVAC", "esta vacio la consulta")
            datosEnFiltro = 0
        } else {
            datosEnFiltro = 1

            Log.d("OSCARLLE", "esta llena la consulta")
        }

        query = query.lowercase(Locale.getDefault())

        Log.d("listaProductoFiltro", listProductos.toString() + "")
        val filteredModelList: MutableList<Productos> = ArrayList()
        for (model in models) {
            val text = model.description.lowercase(Locale.getDefault())
            Log.d("FiltroPreventa", text)
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
        val instance: PrevSelecProductoFragment
            get() = PrevSelecProductoFragment()
    }
}
