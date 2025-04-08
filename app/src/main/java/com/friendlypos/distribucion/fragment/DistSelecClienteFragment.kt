package com.friendlysystemgroup.friendlypos.distribucion.fragment

import android.app.Activity
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
import com.friendlysystemgroup.friendlypos.databinding.FragmentDistSelecClienteBinding
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrClientesAdapter
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrResumenAdapter
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.modelos.Cliente
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort
import io.realm.internal.SyncObjectServerFacade
import java.util.Locale

class DistSelecClienteFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var binding: FragmentDistSelecClienteBinding? = null
    private var realm: Realm? = null
    private var adapter: DistrClientesAdapter? = null
    private var adapter2: DistrResumenAdapter? = null
    var activity: DistribucionActivity? = null
    private var clientes: List<Cliente> = listOf()
    private val clientesFilter: MutableList<Cliente> = mutableListOf()

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as DistribucionActivity
    }

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
        binding = FragmentDistSelecClienteBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding?.apply {
            recyclerViewDistrCliente?.layoutManager = LinearLayoutManager(context)
            
            clientes = listaClientes
            clientesFilter.clear()
            clientesFilter.addAll(clientes)
            
            adapter = DistrClientesAdapter(
                context, activity,
                clientesFilter
            )
            adapter2 = DistrResumenAdapter()
            recyclerViewDistrCliente?.adapter = adapter
            
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
        clientesFilter.clear()
        
        if (filterText.isEmpty()) {
            clientesFilter.addAll(clientes)
        } else {
            val lowerCaseFilter = filterText.lowercase(Locale.getDefault())
            
            clientes.forEach { cliente ->
                val nombre = cliente.nombre?.lowercase(Locale.getDefault()) ?: ""
                val codigo = cliente.codigo?.lowercase(Locale.getDefault()) ?: ""
                
                if (nombre.contains(lowerCaseFilter) || codigo.contains(lowerCaseFilter)) {
                    clientesFilter.add(cliente)
                }
            }
        }
        
        adapter?.setFilter(clientesFilter)
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    val listaClientes: List<Cliente>
        get() {
            val facturaId = activity?.invoiceId ?: return emptyList()
            Log.d("facturaid", facturaId.toString())
            
            val realm = Realm.getDefaultInstance()
            try {
                val clientes: RealmResults<Cliente> = realm.where(Cliente::class.java)
                    .equalTo("isAplicado", false)
                    .equalTo("isDevuelto", false)
                    .findAll()
                
                return realm.copyFromRealm(clientes)
            } finally {
                realm.close()
            }
        }

    override fun updateData() {
        activity?.selecClienteTab = 0
        adapter?.notifyDataSetChanged()
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
                    adapter!!.setFilter(this.listaClientes)
                    return true
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return true
                }
            })
    }

    override fun onQueryTextChange(newText: String): Boolean {
        filterList(newText)
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
        val instance: DistSelecClienteFragment
            get() = DistSelecClienteFragment()
    }
}
