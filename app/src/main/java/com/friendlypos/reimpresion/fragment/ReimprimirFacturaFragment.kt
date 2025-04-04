package com.friendlysystemgroup.friendlypos.reimpresion.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView

import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.Reimpresion.activity.ReimprimirActivity
import com.friendlysystemgroup.friendlypos.Reimpresion.adapters.ReimprimirFacturaAdapter
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort

class ReimprimirFacturaFragment : BaseFragment() {
    @BindView(R.id.recyclerViewReimprimirFactura)
    lateinit var recyclerView: RecyclerView

    private var adapter: ReimprimirFacturaAdapter? = null

    var result1: RealmResults<sale>? = null
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
            R.layout.fragment_reimprimir_factura, container,
            false
        )
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReimprimirFacturaAdapter(
            context, ((activity as ReimprimirActivity?)!!),
            listClientes
        )
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private val listClientes: List<sale>
        get() {
            realm = Realm.getDefaultInstance()
            val query: RealmQuery<sale> = realm.where(sale::class.java).equalTo("aplicada", 1)
                .equalTo("facturaDePreventa", "Distribucion").or()
                .equalTo("facturaDePreventa", "VentaDirecta")
            val result1: RealmResults<sale> = query.findAllSorted("id", Sort.DESCENDING)

            return result1
        }

    override fun onDestroyView() {
        super.onDestroyView()
        realm!!.close()
    }

    override fun updateData() {
    }

    companion object {
        val instance: ReimprimirFacturaFragment
            get() = ReimprimirFacturaFragment()
    }
}
