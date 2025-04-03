package com.friendlypos.reimpresion_pedidos.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.adapters.DistrResumenAdapter
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.distribucion.util.TotalizeHelper
import com.friendlypos.principal.modelo.Productos
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity
import com.friendlypos.reimpresion_pedidos.adapters.ReimPedidoSeleccionarProductosAdapter
import io.realm.Realm
import java.util.Locale

class ReimPedidoSelecProductoFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var realm: Realm? = null
    var recyclerView: RecyclerView? = null
    private var adapter: ReimPedidoSeleccionarProductosAdapter? = null
    private var adapter2: DistrResumenAdapter? = null
    var slecTAB: Int = 0
    var activity: ReimprimirPedidosActivity? = null
    var totalizeHelper: TotalizeHelper? = null

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as ReimprimirPedidosActivity
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
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(
            R.layout.fragment_reim_pedido_selec_producto, container,
            false
        )
        setHasOptionsMenu(true)
        recyclerView =
            rootView.findViewById<View>(R.id.recyclerViewReimPedidoSeleccProducto) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())
        recyclerView!!.setHasFixedSize(true)
        adapter = ReimPedidoSeleccionarProductosAdapter(
            activity!!, this,
            listProductos
        )
        recyclerView!!.adapter = adapter
        creditoLimite = rootView.findViewById<View>(R.id.restCredit) as TextView

        Log.d("listaProducto", listProductos.toString() + "")
        adapter2 = DistrResumenAdapter()
        creditoDisponible()

        return rootView
    }


    private val listProductos: List<Productos>
        get() {
            realm = Realm.getDefaultInstance()
            val query = realm.where(Productos::class.java)
            val result1 = query.findAll()

            return result1
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
    }

    fun creditoDisponible() {
        slecTAB = activity!!.selecClienteTab

        if (slecTAB == 1) {
            // creditoLimiteCliente = Double.parseDouble(((ReimprimirPedidosActivity) getActivity()).getCreditoLimiteClienteSlecc());
            //    creditoLimiteCliente = 0.0;
            val metodoPagoCliente = activity!!.metodoPagoCliente
            val limite = activity!!.creditoLimiteCliente
            // creditoLimiteCliente = Double.parseDouble(limite);
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
        adapter!!.updateData(listProductos)
        adapter2!!.notifyDataSetChanged()

        /* totalizeHelper = new TotalizeHelper(activity);
        totalizeHelper.totalize(resumenFrag1.getListResumen());*/
        if (slecTAB == 1) {
            creditoLimiteCliente =
                (getActivity() as ReimprimirPedidosActivity).creditoLimiteCliente!!.toDouble()
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

        MenuItemCompat.setOnActionExpandListener(
            item,
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    // Do something when collapsed
                    adapter!!.setFilter(this.listProductos)
                    return true // Return true to collapse action view
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    // Do something when expanded
                    return true // Return true to expand action view
                }
            })
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
        query = query.lowercase(Locale.getDefault())

        val filteredModelList: MutableList<Productos> = ArrayList()
        for (model in models) {
            val text = model.description!!.lowercase(Locale.getDefault())
            Log.d("dasdad", text)
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
        val instance: ReimPedidoSelecProductoFragment
            get() = ReimPedidoSelecProductoFragment()
    }
}
