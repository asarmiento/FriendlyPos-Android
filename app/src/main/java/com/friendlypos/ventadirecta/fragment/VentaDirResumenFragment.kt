package com.friendlypos.ventadirecta.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity
import com.friendlypos.ventadirecta.adapters.VentaDirResumenAdapter
import com.friendlypos.ventadirecta.util.TotalizeHelperVentaDirecta

class VentaDirResumenFragment : BaseFragment() {
    var recyclerView: RecyclerView? = null
    private var adapter: VentaDirResumenAdapter? = null
    var slecTAB: Int = 0

    var totalizeHelper: TotalizeHelperVentaDirecta? = null
    var activity: VentaDirectaActivity? = null

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_ventadir_resumen, container,
            false
        )

        recyclerView =
            rootView.findViewById<View>(R.id.recyclerViewVentaDirectaResumen) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())
        slecTAB = activity!!.selecClienteTabVentaDirecta
        totalizeHelper = TotalizeHelperVentaDirecta(activity)


        if (slecTAB == 1) {
            val list = activity!!.allPivotDelegate
            adapter = VentaDirResumenAdapter(activity, this, list)
            recyclerView!!.adapter = adapter

            activity!!.cleanTotalize()

            totalizeHelper!!.totalize(list)
            Log.d("listaResumen", list.toString() + "")
        }
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        totalizeHelper!!.destroy()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as VentaDirectaActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun updateData() {
        slecTAB = activity!!.selecClienteTabVentaDirecta
        if (slecTAB == 1) {
            activity!!.cleanTotalize()
            val list = activity!!.allPivotDelegate

            adapter!!.updateData(list)
            totalizeHelper!!.totalize(list)
        } else {
            Log.d("SelecUpdateResumenVD", "No hay productos")
        }
    }
}
