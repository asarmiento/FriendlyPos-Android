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
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrResumenAdapter
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.util.TotalizeHelper
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade
import java.util.Locale

class DistSelecProductoFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var realm: Realm? = null
    var recyclerView: RecyclerView? = null
    private var adapter: DistrSeleccionarProductosAdapter? = null
    private var adapter2: DistrResumenAdapter? = null
    private val resumenFrag1: DistResumenFragment? = null
    var slecTAB: Int = 0
    var activity: DistribucionActivity? = null
    var totalizeHelper: TotalizeHelper? = null
    var datosEnFiltro: Int = 0

    override fun onDestroy() {
        super.onDestroy()
    }

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
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(
            R.layout.fragment_distribucion_selecproduct, container,
            false
        )
        setHasOptionsMenu(true)
        recyclerView =
            rootView.findViewById<View>(R.id.recyclerViewDistrSeleccProducto) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())
        recyclerView!!.setHasFixedSize(true)

        if (adapter == null) {
            adapter = DistrSeleccionarProductosAdapter(
                activity!!, this,
                listProductos
            )
        }
        recyclerView!!.adapter = adapter
        creditoLimite = rootView.findViewById<View>(R.id.restCredit) as TextView

        Log.d("listaProducto", listProductos.toString() + "")
        adapter2 = DistrResumenAdapter()
        creditoDisponible()

        return rootView
    }

    private val listProductos: List<Inventario>
        get() {
            realm = Realm.getDefaultInstance()


            val query1 =
                realm.where(Inventario::class.java).notEqualTo("amount", "0")
                    .notEqualTo("amount", "0.0").notEqualTo("amount", "0.000")
            val result2 = query1.findAll()

            if (result2.isEmpty()) {
                Toast.makeText(
                    SyncObjectServerFacade.getApplicationContext(),
                    "Favor descargar datos primero",
                    Toast.LENGTH_LONG
                ).show()
            }

            /*  else{
          for (int i = 0; i < result2.size(); i++) {

              List<Inventario> salesList1 = realm.where(Inventario.class).notEqualTo("amount", "0").notEqualTo("amount", "0.0").notEqualTo("amount", "0.000").findAll();
              String nombre = salesList1.get(i).getNombre_producto();
              if (nombre == null){
                  String facturaId1 = salesList1.get(i).getProduct_id();

                  Productos salesList2 = realm.where(Productos.class).equalTo("id", facturaId1).findFirst();

                  final String facturaId2 = salesList2.getId();
                  final String desc = salesList2.getDescription();

                  final Realm realm3 = Realm.getDefaultInstance();

                  realm3.executeTransactionAsync(new Realm.Transaction() {
                      @Override
                      public void execute(Realm bgRealm) {
                          Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("product_id", facturaId2).findFirst();
                          //  inv_actualizado.setProducto(new RealmList<Productos>(salesList2.toArray(new Productos[salesList2.size()])));
                          inv_actualizado.setNombre_producto(desc);
                          realm3.insertOrUpdate(inv_actualizado); // using insert API
                      }
                  }, new Realm.Transaction.OnSuccess() {
                      @Override
                      public void onSuccess() {
                          realm3.close();
                      }
                  }, new Realm.Transaction.OnError() {
                      @Override
                      public void onError(Throwable error) {
                          realm3.close();
                      }
                  });


              }
          }

      }*/
            return result2
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
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


    companion object {
        private var bill_type = 1
        var creditoLimite: TextView? = null
        var creditoLimiteCliente: Double = 0.0
        val instance: DistSelecProductoFragment
            get() = DistSelecProductoFragment()
    }
}
