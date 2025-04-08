package com.friendlysystemgroup.friendlypos.Recibos.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.adapters.RecibosSeleccionarFacturaAdapter
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.Recibos.util.TotalizeHelperRecibos
import com.friendlysystemgroup.friendlypos.databinding.FragmentRecibosSeleccionarFacturaBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class RecibosSeleccionarFacturaFragment : BaseFragment() {
    private var binding: FragmentRecibosSeleccionarFacturaBinding? = null
    private var adapter: RecibosSeleccionarFacturaAdapter? = null
    private var totalizeHelper: TotalizeHelperRecibos? = null
    private var slecTAB: Int = 0
    private var recibosActivity: RecibosActivity? = null
    private var debePagar: Double = 0.0
    private var sb: StringBuffer? = null
    private var cantidadPagar: Double = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RecibosActivity) {
            recibosActivity = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        recibosActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecibosSeleccionarFacturaBinding.inflate(inflater, container, false)
        return binding?.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setHasOptionsMenu(true)
        
        setupViews()
        setupRecyclerView()
        setupListeners()
    }
    
    private fun setupViews() {
        recibosActivity?.let { activity ->
            totalizeHelper = TotalizeHelperRecibos(activity)
            
            slecTAB = activity.selecClienteTabRecibos
            if (slecTAB == 1) {
                val list = listProductos
                activity.cleanTotalize()
                totalizeHelper?.totalizeRecibos(list)
                Log.d("listaResumen", list.toString())
            }
        }
    }
    
    private fun setupRecyclerView() {
        binding?.recyclerViewRecibosSeleccFactura?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            
            if (adapter == null) {
                adapter = RecibosSeleccionarFacturaAdapter(
                    recibosActivity ?: return@apply,
                    this@RecibosSeleccionarFacturaFragment,
                    listProductos
                )
            }
            
            this.adapter = this@RecibosSeleccionarFacturaFragment.adapter
        }
    }
    
    private fun setupListeners() {
        binding?.btnPagoTotal?.setOnClickListener {
            handlePagoTotalClick()
        }
    }
    
    private fun handlePagoTotalClick() {
        sb = StringBuffer()
        for (r in adapter?.checked ?: emptyList()) {
            sb?.append(r.numeration)

            val total = r.total
            val pago = r.paid
            debePagar = total - pago
            recibosActivity?.setTotalizarTotalCheck(debePagar)
        }

        if ((adapter?.checked?.size ?: 0) > 0) {
            val totalCheck = recibosActivity?.getTotalizarTotalCheck() ?: 0.0
            context?.let {
                Toast.makeText(it, totalCheck.toString(), Toast.LENGTH_LONG).show()
            }
        } else {
            context?.let {
                Toast.makeText(it, "No hay facturas seleccionadas", Toast.LENGTH_LONG).show()
            }
        }

        val totalP = recibosActivity?.getTotalizarCancelado() ?: 0.0
        Log.d("totalRecibos", totalP.toString())

        showPagoTotalDialog(totalP)
    }
    
    private fun showPagoTotalDialog(totalP: Double) {
        val activity = recibosActivity ?: return
        
        val layoutInflater = LayoutInflater.from(activity)
        val promptView = layoutInflater.inflate(
            com.friendlysystemgroup.friendlypos.R.layout.promptrecibospagototal, 
            null
        )

        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setView(promptView)

        val label = promptView.findViewById<android.widget.TextView>(
            com.friendlysystemgroup.friendlypos.R.id.promtClabelRecibosPagoTotal
        )
        label.text = "Escriba un pago maximo de ${String.format("%,.2f", totalP)} minima de 1"

        val input = promptView.findViewById<android.widget.EditText>(
            com.friendlysystemgroup.friendlypos.R.id.promtCtextRecibosPagoTotal
        )

        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            if (input.text.toString().isNotEmpty()) {
                var numIngresado = input.text.toString().toDoubleOrNull() ?: 0.0
                
                if (numIngresado > totalP) {
                    numIngresado = totalP
                    context?.let {
                        Toast.makeText(it, "Ajustado a $numIngresado", Toast.LENGTH_LONG).show()
                    }
                }
                
                if (numIngresado <= 0.0) {
                    context?.let {
                        Toast.makeText(it, "Ingrese una cantidad mayor a cero", Toast.LENGTH_LONG).show()
                    }
                    return@setPositiveButton
                }

                cantidadPagar = numIngresado
                Log.d("cantidadPagar1", String.format("%,.2f", cantidadPagar))
                
                showConfirmacionPagoDialog()
            } else {
                input.error = "Campo requerido"
                input.requestFocus()
            }
        }
        
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            recibosActivity?.cleanTotalizeCkeck()
        }

        val alertD = alertDialogBuilder.create()
        alertD.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertD.show()
    }
    
    private fun showConfirmacionPagoDialog() {
        val activity = recibosActivity ?: return
        
        AlertDialog.Builder(activity)
            .setTitle("Pago total")
            .setMessage("¿Desea proceder con el pago de las facturas?")
            .setPositiveButton("OK") { _, _ ->
                try {
                    procesarPago()
                } catch (e: Exception) {
                    context?.let {
                        Toast.makeText(it, e.message, Toast.LENGTH_LONG).show()
                    }
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }
    
    private fun procesarPago() {
        val clienteId = recibosActivity?.clienteIdRecibos ?: return
        val realm = Realm.getDefaultInstance()
        
        try {
            val result: RealmResults<recibos> = realm.where(recibos::class.java)
                .equalTo("customer_id", clienteId)
                .findAllSorted("date", Sort.DESCENDING)

            if (result.isEmpty()) {
                context?.let {
                    Toast.makeText(it, "No hay recibos emitidos", Toast.LENGTH_LONG).show()
                }
                return
            }
            
            // Procesar cada factura
            for (i in result.indices) {
                val salesList = realm.where(recibos::class.java)
                    .equalTo("customer_id", clienteId)
                    .findAllSorted("date", Sort.DESCENDING)
                
                salesList[i]?.let { recibo ->
                    val totalFactura = recibo.total
                    val totalPagado = recibo.paid
                    val facturaId = recibo.invoice_id
                    
                    Log.d("totalFactura", totalFactura.toString())
                    Log.d("totalPagado", totalPagado.toString())

                    if (totalFactura != totalPagado) {
                        val restante = totalFactura - totalPagado
                        Log.d("restante", String.format("%,.2f", restante))

                        // Si todavía tenemos saldo a distribuir
                        if (cantidadPagar > 0.0) {
                            val pagoParcial = if (cantidadPagar >= restante) restante else cantidadPagar
                            val irPagando = pagoParcial + totalPagado
                            
                            // Actualizar el recibo con el pago
                            realm.executeTransaction { r ->
                                val reciboActualizado = r.where(recibos::class.java)
                                    .equalTo("invoice_id", facturaId)
                                    .findFirst()
                                    
                                reciboActualizado?.let { ra ->
                                    ra.paid = irPagando
                                    ra.abonado = 1
                                    ra.montoCanceladoPorFactura = irPagando
                                    
                                    val cant = ra.montoCancelado
                                    ra.montoCancelado = if (cant == 0.0) irPagando else cant + irPagando
                                }
                            }
                            
                            // Actualizar el saldo restante
                            cantidadPagar -= pagoParcial
                            
                            Log.d("irPagando", String.format("%,.2f", irPagando))
                        }
                    }
                }
            }

            context?.let {
                Toast.makeText(it, "Se realizó el pago total", Toast.LENGTH_LONG).show()
            }
            
            updateData()
        } finally {
            realm.close()
        }
    }

    private val listProductos: List<recibos>
        get() {
            val clienteId = recibosActivity?.clienteIdRecibos ?: return emptyList()

            val realm = Realm.getDefaultInstance()
            val result: RealmResults<recibos> = realm.where(recibos::class.java)
                .equalTo("customer_id", clienteId)
                .findAllSorted("date", Sort.DESCENDING)
                
            realm.close()
            Log.d("RECIBOSCLIENTE", result.toString())
            return result
        }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun updateData() {
        recibosActivity?.let { activity ->
            slecTAB = activity.selecClienteTabRecibos
            if (slecTAB == 1) {
                activity.cleanTotalize()
                val list = listProductos

                adapter?.updateData(list)
                totalizeHelper?.totalizeRecibos(list)

                val totalT = activity.getTotalizarTotal()
                val totalP = activity.getTotalizarCancelado()

                binding?.txtPagoTotal?.text = "Total de todas: ${String.format("%,.2f", totalT)}"
                binding?.txtPagoCancelado?.text = "Total por pagar: ${String.format("%,.2f", totalP)}"
                Log.d("totalFull", totalT.toString())
            } else {
                Log.d("SelecUpdateResumen", "No hay productos")
            }
        }
    }

    companion object {
        fun newInstance(): RecibosSeleccionarFacturaFragment = RecibosSeleccionarFacturaFragment()
    }
}
