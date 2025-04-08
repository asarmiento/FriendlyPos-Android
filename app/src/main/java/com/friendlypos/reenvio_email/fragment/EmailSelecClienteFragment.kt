package com.friendlysystemgroup.friendlypos.reenvio_email.fragment

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
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.databinding.FragmentEmailSelecClienteBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.Reenvio_email.activity.EmailActivity
import com.friendlysystemgroup.friendlypos.Reenvio_email.adapters.EmailClientesAdapter
import io.realm.Realm
import io.realm.RealmResults
import java.util.Locale

class EmailSelecClienteFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var binding: FragmentEmailSelecClienteBinding? = null
    private var realm: Realm? = null
    private var adapter: EmailClientesAdapter? = null
    private var clientes: List<Clientes> = listOf()
    private val clientesFilter: MutableList<Clientes> = mutableListOf()

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
        binding = FragmentEmailSelecClienteBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding?.recyclerViewEmailCliente?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            
            clientes = listClientes
            clientesFilter.clear()
            clientesFilter.addAll(clientes)
            
            adapter = EmailClientesAdapter(
                context, 
                activity as EmailActivity?,
                clientesFilter
            ).also {
                this@EmailSelecClienteFragment.adapter = it
            }
        }
        
        Log.d("listadeClientes", "Clientes cargados: ${clientes.size}")
    }

    private val listClientes: List<Clientes>
        get() {
            val realm = Realm.getDefaultInstance()
            try {
                val result: RealmResults<Clientes> = realm.where(Clientes::class.java).findAll()
                
                if (result.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Favor descargar datos primero",
                        Toast.LENGTH_LONG
                    ).show()
                    return emptyList()
                }
                
                return realm.copyFromRealm(result)
            } finally {
                realm.close()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        binding = null
    }

    override fun updateData() {
        // Implementar si es necesario
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_email, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)

        MenuItemCompat.setOnActionExpandListener(
            item,
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    adapter?.setFilter(listClientes)
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

    private fun filterList(query: String) {
        clientesFilter.clear()
        
        if (query.isEmpty()) {
            clientesFilter.addAll(clientes)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            
            clientes.forEach { cliente ->
                cliente.fantasyName?.let { name ->
                    if (name.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                        clientesFilter.add(cliente)
                    }
                }
            }
        }
        
        adapter?.setFilter(clientesFilter)
    }
}
