package com.friendlysystemgroup.friendlypos.reimpresion.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendlysystemgroup.friendlypos.databinding.FragmentReimprimirFacturaBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.reimpresion.activity.ReimprimirActivity
import com.friendlysystemgroup.friendlypos.reimpresion.adapters.ReimprimirFacturaAdapter
import io.realm.Realm
import io.realm.Sort

/**
 * Fragmento para seleccionar una factura a reimprimir
 */
class ReimprimirFacturaFragment : BaseFragment() {
    private var _binding: FragmentReimprimirFacturaBinding? = null
    private val binding get() = _binding!!
    
    private var adapter: ReimprimirFacturaAdapter? = null
    private var realm: Realm? = null

    override fun onCreateView(
        inflater: LayoutInflater, 
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReimprimirFacturaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }
    
    private fun setupRecyclerView() {
        val activity = activity as? ReimprimirActivity
        activity?.let {
            val facturas = obtenerListaFacturas()
            
            adapter = ReimprimirFacturaAdapter(
                requireContext(), 
                it,
                facturas
            )
            
            binding.recyclerViewReimprimirFactura.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = this@ReimprimirFacturaFragment.adapter
            }
        }
    }

    /**
     * Obtiene la lista de facturas para mostrar
     */
    private fun obtenerListaFacturas(): List<sale> {
        realm = Realm.getDefaultInstance()
        
        return realm?.let { realm ->
            realm.where(sale::class.java)
                .equalTo("aplicada", 1)
                .beginGroup()
                    .equalTo("facturaDePreventa", "Distribucion")
                    .or()
                    .equalTo("facturaDePreventa", "VentaDirecta")
                .endGroup()
                .findAllSorted("id", Sort.DESCENDING)
                .toList()
        } ?: emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }

    override fun updateData() {
        // Actualizar datos cuando cambia la pestaña
        adapter?.let { adapter ->
            adapter.contentList = obtenerListaFacturas()
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        fun newInstance(): ReimprimirFacturaFragment = ReimprimirFacturaFragment()
    }
}
