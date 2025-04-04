package com.friendlysystemgroup.friendlypos.reenvio_email.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.Reenvio_email.activity.EmailActivity
import com.friendlysystemgroup.friendlypos.Reenvio_email.adapters.EmailFacturasAdapter
import com.friendlysystemgroup.friendlypos.Reenvio_email.modelo.invoices
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade

class EmailSelecFacturaFragment : BaseFragment() {
    private var realm: Realm? = null

    @BindView(R.id.recyclerViewEmailFactura)
    lateinit var recyclerView: RecyclerView

    private var adapter: EmailFacturasAdapter? = null

    var slecTAB: Int = 0
    var activity: EmailActivity? = null

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
            R.layout.fragment_email_selec_factura, container,
            false
        )
        setHasOptionsMenu(true)
        //ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(getActivity())

        //List<invoices> list = ;
        adapter = EmailFacturasAdapter(activity, this, listClientes)
        //adapter2 = new DistrResumenAdapter();
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        Log.d("listaProducto", listClientes.toString() + "")
    }

    private val listClientes: List<invoices>
        get() {
            realm = Realm.getDefaultInstance()
            val query = realm.where(invoices::class.java)
            val result1 = query.findAll()
            if (result1.size == 0) {
                Toast.makeText(
                    SyncObjectServerFacade.getApplicationContext(),
                    "Favor descargar datos primero",
                    Toast.LENGTH_LONG
                ).show()
            }
            return result1
        }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as EmailActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // realm.close();
    }

    override fun updateData() {
        slecTAB = activity!!.selecClienteTabEmail
        if (slecTAB == 1) {
            // List<invoices> list = ;

            adapter!!.updateData(listClientes)
        } else {
            Toast.makeText(getActivity(), "nadaresumenUpdate", Toast.LENGTH_LONG).show()
        }
    }
}

