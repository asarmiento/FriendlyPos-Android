package com.friendlysystemgroup.friendlypos.reimpresion_pedidos.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView

import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.fragment.DistSelecProductoFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.activity.ReimprimirPedidosActivity
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.adapters.ReimPedidoClientesAdapter
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.internal.SyncObjectServerFacade

class ReimPedidoSelecClienteFragment : BaseFragment() {
    private var realm: Realm? = null

    @BindView(R.id.recyclerViewReimPedidoCliente)
    lateinit var recyclerView: RecyclerView

    private var adapter: ReimPedidoClientesAdapter? = null

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
            R.layout.fragment_reim_pedido_selec_cliente, container,
            false
        )
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReimPedidoClientesAdapter(
            context, ((activity as ReimprimirPedidosActivity?)!!),
            listClientes
        )

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }


    private val listClientes: List<sale>
        get() {
            realm = Realm.getDefaultInstance()
            val query: RealmQuery<sale> =
                realm!!.where(sale::class.java)
                    .equalTo("aplicada", 1)
                    .equalTo("facturaDePreventa", "Proforma")
                    .or()
                    .equalTo("facturaDePreventa", "Preventa")
            val result1 = query.findAll()
            if (result1.size == 0) {
                Toast.makeText(
                    SyncObjectServerFacade.getApplicationContext(),
                    "No hay facturas para editar",
                    Toast.LENGTH_LONG
                ).show()
            }
            return result1
        }


    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
    }

    override fun updateData() {
    }

    companion object {
        val instance: DistSelecProductoFragment
            get() = DistSelecProductoFragment()
    }
}
