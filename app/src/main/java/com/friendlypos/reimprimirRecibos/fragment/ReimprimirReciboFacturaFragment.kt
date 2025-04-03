package com.friendlypos.reimprimirRecibos.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.friendlypos.R
import com.friendlypos.Recibos.modelo.receipts
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.reimprimirRecibos.activity.ReimprimirRecibosActivity
import com.friendlypos.reimprimirRecibos.adapters.ReimprimirReciboFacturaAdapter
import io.realm.Realm
import io.realm.RealmResults

class ReimprimirReciboFacturaFragment : BaseFragment() {
    @BindView(R.id.recyclerViewReimprimirReciboFactura)
    lateinit var recyclerView: RecyclerView

    private var adapter: ReimprimirReciboFacturaAdapter? = null

    var result1: RealmResults<receipts>? = null
    var realm: Realm? = null

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
            R.layout.fragment_reimprimir_recibo_factura, container,
            false
        )
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReimprimirReciboFacturaAdapter(
            context, ((activity as ReimprimirRecibosActivity?)!!),
            listClientes
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private val listClientes: List<receipts>
        get() {
            realm = Realm.getDefaultInstance()

            //  RealmQuery<recibos> query = realm.where(recibos.class).equalTo("aplicada", 1).equalTo("facturaDePreventa", "Distribucion").or().equalTo("facturaDePreventa", "VentaDirecta");
            val query = realm.where(receipts::class.java)
            val result1 = query.findAll()

            return result1
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
    }

    override fun updateData() {
    }

    companion object {
        val instance: ReimprimirReciboFacturaFragment
            get() = ReimprimirReciboFacturaFragment()
    }
}
