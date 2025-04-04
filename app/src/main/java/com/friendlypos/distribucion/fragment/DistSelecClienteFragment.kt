package com.friendlysystemgroup.friendlypos.distribucion.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrClientesAdapter
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrResumenAdapter
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort
import io.realm.internal.SyncObjectServerFacade
import java.util.Locale

class DistSelecClienteFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var realm: Realm? = null

    @BindView(R.id.recyclerViewDistrCliente)
    lateinit var recyclerView: RecyclerView

    private var adapter: DistrClientesAdapter? = null
    private var adapter2: DistrResumenAdapter? = null


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_distribucion_cliente, container,
            false
        )
        setHasOptionsMenu(true)
        //ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (adapter == null) {
            adapter = DistrClientesAdapter(
                context, ((activity as DistribucionActivity?)!!),
                listClientes
            )
            adapter2 = DistrResumenAdapter()
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        Log.d("listaProducto", listClientes.toString() + "")
    }

    private val listClientes: List<sale>
        get() {
            realm = Realm.getDefaultInstance()
            val query: RealmQuery<sale> =
                realm.where(sale::class.java).equalTo("aplicada", 0).equalTo("devolucion", 0)
            val result1: RealmResults<sale> =
                query.findAllSorted("created_at", Sort.DESCENDING)

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
    }

    override fun updateData() {
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
                    adapter!!.setFilter(this.listClientes)
                    return true
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return true
                }
            })
    }

    override fun onQueryTextChange(newText: String): Boolean {
        val filteredModelList = filter(listClientes, newText)
        adapter!!.setFilter(filteredModelList)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }


    private fun filter(models: List<sale>, query: String): List<sale> {
        var query = query
        query = query.lowercase(Locale.getDefault())
        val filteredModelList: MutableList<sale> = ArrayList()

        for (model in models) {
            if (model.fantasy == null) {
            } else {
                val text = model.fantasy.lowercase(Locale.getDefault())
                Log.d("FiltroPreventa", text)
                if (text.contains(query)) {
                    filteredModelList.add(model)
                }
            }
        }
        return filteredModelList
    }

    companion object {
        val instance: DistSelecProductoFragment
            get() = DistSelecProductoFragment()
    }
}
