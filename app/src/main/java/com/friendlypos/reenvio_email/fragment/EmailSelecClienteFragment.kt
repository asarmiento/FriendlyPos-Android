package com.friendlypos.reenvio_email.fragment

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
import butterknife.BindView
import butterknife.ButterKnife
import com.friendlypos.R
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.reenvio_email.activity.EmailActivity
import com.friendlypos.reenvio_email.adapters.EmailClientesAdapter
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade
import java.util.Locale

class EmailSelecClienteFragment : BaseFragment(), SearchView.OnQueryTextListener {
    private var realm: Realm? = null

    @BindView(R.id.recyclerViewEmailCliente)
    lateinit var recyclerView: RecyclerView
    private var adapter: EmailClientesAdapter? = null

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
            R.layout.fragment_email_selec_cliente, container,
            false
        )
        setHasOptionsMenu(true)
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (adapter == null) {
            adapter = EmailClientesAdapter(
                context, ((activity as EmailActivity?)!!),
                listClientes
            )
            //adapter2 = new DistrResumenAdapter();
        }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        Log.d("listadeClientes", listClientes.toString() + "")
    }

    private val listClientes: List<Clientes>
        get() {
            realm = Realm.getDefaultInstance()
            val query = realm.where(Clientes::class.java)
            val result1 = query.findAll()
            if (result1.size == 0) {
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

    override fun updateData() {
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
                    // Do something when collapsed
                    adapter!!.setFilter(this.listClientes)
                    return true // Return true to collapse action view
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    // Do something when expanded
                    return true // Return true to expand action view
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


    private fun filter(models: List<Clientes>, query: String): List<Clientes> {
        var query = query
        query = query.lowercase(Locale.getDefault())

        val filteredModelList: MutableList<Clientes> = ArrayList()
        for (model in models) {
            val text = model.fantasyName!!.lowercase(Locale.getDefault())
            Log.d("FiltroEmail", text)
            if (text.contains(query)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }
}
