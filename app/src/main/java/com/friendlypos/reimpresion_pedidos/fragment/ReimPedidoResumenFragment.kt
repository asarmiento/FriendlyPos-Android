package com.friendlypos.reimpresion_pedidos.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity
import com.friendlypos.reimpresion_pedidos.adapters.ReimPedidoResumenAdapter
import com.friendlypos.reimpresion_pedidos.util.TotalizeHelperReimPedido
import io.realm.Realm
import io.realm.RealmResults

class ReimPedidoResumenFragment : BaseFragment() {
    var recyclerView: RecyclerView? = null
    private var adapter: ReimPedidoResumenAdapter? = null
    var slecTAB: Int = 0

    var totalizeHelper: TotalizeHelperReimPedido? = null

    var activity: ReimprimirPedidosActivity? = null

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
            R.layout.fragment_reim_pedido_resumen, container,
            false
        )

        recyclerView =
            rootView.findViewById<View>(R.id.recyclerViewReimPedidoResumen) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(getActivity())

        val list = listResumen

        adapter = ReimPedidoResumenAdapter(activity, this, list)
        recyclerView!!.adapter = adapter

        activity!!.cleanTotalize()
        totalizeHelper = TotalizeHelperReimPedido(activity)
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
        this.activity = activity as ReimprimirPedidosActivity
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
            Toast.makeText(getActivity(), "nadaresumenUpdate", Toast.LENGTH_LONG).show()
        }
    }
}
