package com.friendlysystemgroup.friendlypos.Recibos.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.adapters.RecibosClientesAdapter
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.databinding.FragmentRecibosClientesBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import io.realm.Realm
import io.realm.RealmResults

class RecibosClientesFragment : BaseFragment() {
    private var realm: Realm? = null
    private var binding: FragmentRecibosClientesBinding? = null
    private var adapter: RecibosClientesAdapter? = null

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
        binding = FragmentRecibosClientesBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
    }
    
    private fun setupRecyclerView() {
        if (adapter == null) {
            context?.let { ctx ->
                (activity as? RecibosActivity)?.let { recibosActivity ->
                    adapter = RecibosClientesAdapter(
                        ctx, recibosActivity, listClientes
                    )
                }
            }
        }
        
        binding?.recyclerViewRecibosCliente?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = this@RecibosClientesFragment.adapter
        }
    }

    private val listClientes: List<recibos>
        get() {
            val realmInstance = Realm.getDefaultInstance()
            realm = realmInstance
            
            val query = realmInstance.where(recibos::class.java)
            val result: RealmResults<recibos> = query.findAll().distinct("customer_id")

            if (result.isEmpty()) {
                context?.let {
                    Toast.makeText(
                        it,
                        "Favor descargar datos primero",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            Log.d("Resultado", result.toString())
            return result
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        binding = null
    }

    override fun updateData() {
        // Si es necesario actualizar datos desde la actividad principal
    }

    companion object {
        fun newInstance(): RecibosClientesFragment = RecibosClientesFragment()
    }
}
