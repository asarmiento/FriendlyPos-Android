package com.friendlysystemgroup.friendlypos.distribucion.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendlysystemgroup.friendlypos.databinding.FragmentDistribucionResumenBinding
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistrResumenAdapter
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.util.TotalizeHelper
import io.realm.Realm
import io.realm.RealmResults

class DistResumenFragment : BaseFragment() {
    private var binding: FragmentDistribucionResumenBinding? = null
    private var adapter: DistrResumenAdapter? = null
    var slecTAB: Int = 0
    var totalizeHelper: TotalizeHelper? = null
    var activity: DistribucionActivity? = null

    override fun onResume() {
        super.onResume()
    }

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as DistribucionActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDistribucionResumenBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding?.recyclerViewDistrResumen?.apply {
            layoutManager = LinearLayoutManager(context)
            
            val list = listResumen
            
            adapter = DistrResumenAdapter(
                this@DistResumenFragment.activity,
                this@DistResumenFragment,
                list
            )
            this.adapter = adapter
            
            this@DistResumenFragment.activity?.cleanTotalize()
            totalizeHelper = TotalizeHelper(this@DistResumenFragment.activity)
            totalizeHelper?.totalize(list)
            Log.d("listaResumen", list.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        totalizeHelper?.destroy()
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    val listResumen: List<Pivot>
        get() {
            val facturaId = activity?.invoiceId ?: return emptyList()
            
            val realm = Realm.getDefaultInstance()
            try {
                val facturaid1: RealmResults<Pivot> =
                    realm.where(Pivot::class.java)
                        .equalTo("invoice_id", facturaId)
                        .equalTo("devuelvo", 0)
                        .findAll()
                
                return realm.copyFromRealm(facturaid1)
            } finally {
                realm.close()
            }
        }

    override fun updateData() {
        activity?.let { act ->
            slecTAB = act.selecClienteTab
            if (slecTAB == 1) {
                act.cleanTotalize()
                val list = listResumen
                
                adapter?.updateData(list)
                totalizeHelper?.totalize(list)
            } else {
                Log.d("SelecUpdateResumen", "No hay productos")
            }
        }
    }
}
