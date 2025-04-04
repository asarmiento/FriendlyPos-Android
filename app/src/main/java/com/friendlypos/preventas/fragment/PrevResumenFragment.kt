package com.friendlysystemgroup.friendlypos.preventas.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.preventas.activity.PreventaActivity
import com.friendlysystemgroup.friendlypos.preventas.adapters.PrevResumenAdapter
import com.friendlysystemgroup.friendlypos.preventas.util.TotalizeHelperPreventa

class PrevResumenFragment : BaseFragment() {
    var recyclerView: RecyclerView? = null
    private var adapter: PrevResumenAdapter? = null
    var slecTAB: Int = 0

    var totalizeHelper: TotalizeHelperPreventa? = null

    var activity: PreventaActivity? = null

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* FragmentTransaction ftr = getFragmentManager().beginTransaction();
               ftr.detach(DistResumenFragment.this).attach(DistResumenFragment.this).commit();
       */

        val rootView = inflater.inflate(
            R.layout.fragment_prev_resumen, container,
            false
        )

        recyclerView = rootView.findViewById<View>(R.id.recyclerViewPreventaResumen) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())
        slecTAB = activity!!.selecClienteTabPreventa

        totalizeHelper = TotalizeHelperPreventa(activity)
        if (slecTAB == 1) {
            val list = activity!!.allPivotDelegate
            adapter = PrevResumenAdapter(activity, this, list)
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
        this.activity = activity as PreventaActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }


    override fun updateData() {
        slecTAB = activity!!.selecClienteTabPreventa
        if (slecTAB == 1) {
            activity!!.cleanTotalize()
            val list = activity!!.allPivotDelegate

            adapter!!.updateData(list)
            totalizeHelper!!.totalize(list)
        } else {
            Log.d("SelecUpdateResumen", "No hay productos")
        }
    }
}
