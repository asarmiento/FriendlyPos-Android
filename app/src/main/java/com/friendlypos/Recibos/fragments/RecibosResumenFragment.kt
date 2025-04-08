package com.friendlysystemgroup.friendlypos.Recibos.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.adapters.RecibosResumenAdapter
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.databinding.FragmentRecibosResumenBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import io.realm.Realm
import io.realm.RealmResults

class RecibosResumenFragment : BaseFragment() {
    private var binding: FragmentRecibosResumenBinding? = null
    private var adapter: RecibosResumenAdapter? = null
    private var slecTAB: Int = 0
    private var recibosActivity: RecibosActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RecibosActivity) {
            recibosActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecibosResumenBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
    }
    
    private fun setupRecyclerView() {
        binding?.recyclerViewRecibosResumen?.apply {
            layoutManager = LinearLayoutManager(activity)
            
            val list = listResumen
            
            adapter = RecibosResumenAdapter(
                recibosActivity, 
                this@RecibosResumenFragment, 
                list
            )
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        recibosActivity = null
    }

    private val listResumen: List<recibos>
        get() {
            val clienteId = recibosActivity?.clienteIdRecibos ?: return emptyList()

            val realm = Realm.getDefaultInstance()
            val result: RealmResults<recibos> =
                realm.where(recibos::class.java)
                    .equalTo("customer_id", clienteId)
                    .equalTo("abonado", 1)
                    .equalTo("mostrar", 1)
                    .findAll()
            realm.close()

            return result
        }

    override fun updateData() {
        recibosActivity?.let { act ->
            slecTAB = act.selecClienteTabRecibos
            if (slecTAB == 1) {
                act.cleanTotalize()
                val list = listResumen
                adapter?.updateData(list)
            } else {
                Log.d("SelecUpdateResumen", "No hay productos")
            }
        }
    }
    
    companion object {
        fun newInstance(): RecibosResumenFragment = RecibosResumenFragment()
    }
}

