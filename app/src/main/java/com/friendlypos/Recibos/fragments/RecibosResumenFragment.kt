package com.friendlysystemgroup.friendlypos.Recibos.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.adapters.RecibosResumenAdapter
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import io.realm.Realm
import io.realm.RealmResults

class RecibosResumenFragment : BaseFragment() {
    var recyclerView: RecyclerView? = null
    private var adapter: RecibosResumenAdapter? = null
    var slecTAB: Int = 0

    //TotalizeHelperPreventa totalizeHelper;
    var activity: RecibosActivity? = null

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as RecibosActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* FragmentTransaction ftr = getFragmentManager().beginTransaction();
               ftr.detach(DistResumenFragment.this).attach(DistResumenFragment.this).commit();
       */

        val rootView = inflater.inflate(
            R.layout.fragment_recibos_resumen, container,
            false
        )

        recyclerView = rootView.findViewById<View>(R.id.recyclerViewRecibosResumen) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())

        val list = listResumen

        adapter = RecibosResumenAdapter(activity, this, list)
        recyclerView!!.adapter = adapter

        /*activity.cleanTotalize();
        totalizeHelper = new TotalizeHelper(activity);
        totalizeHelper.totalize(list);
        Log.d("listaResumen", list + "");*/
        return rootView


        /*     recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewPreventaResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        slecTAB = activity.getSelecClienteTabPreventa();

        totalizeHelper = new TotalizeHelperPreventa(activity);
        if (slecTAB == 1) {
           // List<Pivot> list = activity.getAllPivotDelegate();
            adapter = new RecibosResumenAdapter(activity, this,  list);
            recyclerView.setAdapter(adapter);


            activity.cleanTotalize();

            totalizeHelper.totalize(list);
            Log.d("listaResumen",  list + "");
        }
        return rootView;*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //totalizeHelper.destroy();
    }


    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    private val listResumen: List<recibos>
        get() {
            val clienteId = activity!!.clienteIdRecibos

            val realm = Realm.getDefaultInstance()
            val result1: RealmResults<recibos> =
                realm.where(recibos::class.java).equalTo("customer_id", clienteId)
                    .equalTo("abonado", 1).equalTo("mostrar", 1).findAll()
            realm.close()

            return result1
        }

    override fun updateData() {
        slecTAB = activity!!.selecClienteTabRecibos
        if (slecTAB == 1) {
            activity!!.cleanTotalize()
            val list = listResumen

            adapter!!.updateData(list)
            // totalizeHelper.totalize(list);
        } else {
            Log.d("SelecUpdateResumen", "No hay productos")
        }
    }
}

