package com.friendlysystemgroup.friendlypos.reenvio_email.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendlysystemgroup.friendlypos.databinding.FragmentEmailSelecFacturaBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.Reenvio_email.activity.EmailActivity
import com.friendlysystemgroup.friendlypos.Reenvio_email.adapters.EmailFacturasAdapter
import com.friendlysystemgroup.friendlypos.Reenvio_email.modelo.invoices
import io.realm.Realm
import io.realm.RealmResults

class EmailSelecFacturaFragment : BaseFragment() {
    private var binding: FragmentEmailSelecFacturaBinding? = null
    private var adapter: EmailFacturasAdapter? = null
    private var activity: EmailActivity? = null
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmailSelecFacturaBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }
    
    private fun setupRecyclerView() {
        binding?.recyclerViewEmailFactura?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            
            val facturas = listFacturas
            
            adapter = EmailFacturasAdapter(
                this@EmailSelecFacturaFragment.activity, 
                this@EmailSelecFacturaFragment, 
                facturas
            ).also {
                this@EmailSelecFacturaFragment.adapter = it
            }
            
            Log.d("listaFacturas", "Facturas cargadas: ${facturas.size}")
        }
    }

    private val listFacturas: List<invoices>
        get() {
            val realm = Realm.getDefaultInstance()
            try {
                val result: RealmResults<invoices> = realm.where(invoices::class.java).findAll()
                
                if (result.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Favor descargar datos primero",
                        Toast.LENGTH_LONG
                    ).show()
                    return emptyList()
                }
                
                return realm.copyFromRealm(result)
            } finally {
                realm.close()
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EmailActivity) {
            activity = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun updateData() {
        activity?.let { act ->
            if (act.selecClienteTabEmail == 1) {
                adapter?.updateData(listFacturas)
            } else {
                context?.let {
                    Toast.makeText(it, "No hay datos para actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

