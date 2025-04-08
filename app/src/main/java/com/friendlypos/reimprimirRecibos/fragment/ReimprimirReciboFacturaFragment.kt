package com.friendlysystemgroup.friendlypos.reimprimirRecibos.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.databinding.FragmentReimprimirReciboFacturaBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.ReimprimirRecibos.activity.ReimprimirRecibosActivity
import com.friendlysystemgroup.friendlypos.ReimprimirRecibos.adapters.ReimprimirReciboFacturaAdapter
import io.realm.Realm
import io.realm.RealmResults

class ReimprimirReciboFacturaFragment : BaseFragment() {
    private var binding: FragmentReimprimirReciboFacturaBinding? = null
    private var adapter: ReimprimirReciboFacturaAdapter? = null
    private var realm: Realm? = null

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
        binding = FragmentReimprimirReciboFacturaBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        context?.let { ctx ->
            (activity as? ReimprimirRecibosActivity)?.let { act ->
                val recibos = listRecibos
                
                adapter = ReimprimirReciboFacturaAdapter(ctx, act, recibos)
                
                binding?.recyclerViewReimprimirReciboFactura?.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(ctx)
                    adapter = this@ReimprimirReciboFacturaFragment.adapter
                }
            }
        }
    }

    private val listRecibos: List<receipts>
        get() {
            val realmInstance = Realm.getDefaultInstance()
            try {
                val result: RealmResults<receipts> = realmInstance.where(receipts::class.java).findAll()
                return realmInstance.copyFromRealm(result)
            } finally {
                // No cerramos aquí el realm para mantenerlo abierto durante el ciclo de vida del fragmento
                // Se cierra en onDestroyView
                realm = realmInstance
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm?.close()
        realm = null
        binding = null
    }

    override fun updateData() {
        adapter?.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): ReimprimirReciboFacturaFragment = ReimprimirReciboFacturaFragment()
    }
}
