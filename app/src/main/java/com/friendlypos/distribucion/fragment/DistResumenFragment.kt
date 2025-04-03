package com.friendlypos.distribucion.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlypos.distribucion.adapters.DistrResumenAdapter
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.util.TotalizeHelper
import io.realm.Realm
import io.realm.RealmResults

class DistResumenFragment : BaseFragment() {
    var recyclerView: RecyclerView? = null
    private var adapter: DistrResumenAdapter? = null
    var slecTAB: Int = 0
    var totalizeHelper: TotalizeHelper? = null
    var activity: DistribucionActivity? = null

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_distribucion_resumen, container,
            false
        )

        recyclerView = rootView.findViewById<View>(R.id.recyclerViewDistrResumen) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())

        val list = listResumen

        adapter = DistrResumenAdapter(activity, this, list)
        recyclerView!!.adapter = adapter

        activity!!.cleanTotalize()
        totalizeHelper = TotalizeHelper(activity)
        totalizeHelper!!.totalize(list)
        Log.d("listaResumen", list.toString() + "")

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        totalizeHelper!!.destroy()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as DistribucionActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }


    val listResumen: List<Pivot>
        get() {
            val facturaId = activity!!.invoiceId
            val realm = Realm.getDefaultInstance()
            val facturaid1: RealmResults<Pivot> =
                realm.where(Pivot::class.java).equalTo("invoice_id", facturaId)
                    .equalTo("devuelvo", 0).findAll()
            realm.close()
            return facturaid1
        }

    override fun updateData() {
        slecTAB = activity!!.selecClienteTab
        if (slecTAB == 1) {
            activity!!.cleanTotalize()
            val list = listResumen

            adapter!!.updateData(list)
            totalizeHelper!!.totalize(list)
        } else {
            Log.d("SelecUpdateResumen", "No hay productos")
        }
    }
}
