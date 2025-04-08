package com.friendlysystemgroup.friendlypos.distribucion.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.databinding.FragmentDistTotalizarBinding
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.adapters.DistTotalizarAdapter
import com.friendlysystemgroup.friendlypos.distribucion.modelo.InvoiceItem
import com.friendlysystemgroup.friendlypos.distribucion.util.TotalizeHelper
import com.friendlysystemgroup.friendlypos.utils.Utils
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class DistTotalizarFragment : Fragment() {
    private var binding: FragmentDistTotalizarBinding? = null
    private var adapter: DistTotalizarAdapter? = null
    private var activity: DistribucionActivity? = null
    private var esEfectivo = true
    private var paidAmount = 0.0
    private lateinit var mNumberFormat: NumberFormat
    private var helper: TotalizeHelper? = null
    private val calendar = Calendar.getInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DistribucionActivity) {
            activity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDistTotalizarBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mNumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
        helper = TotalizeHelper(activity)
        
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        binding?.apply {
            recyclerViewDistTotalizar?.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = DistTotalizarAdapter(
                    context,
                    this@DistTotalizarFragment.activity,
                    obtenerItems()
                ).also {
                    this@DistTotalizarFragment.adapter = it
                }
            }
            
            // Establecer valores iniciales
            switchEfectivo.isChecked = true
            textViewFecha.text = Utils.getDateToday()
            totalizeHelper?.let { helper ->
                textViewTotal.text = Utils.formatCurrency(helper.getTotal())
                textViewImpuesto.text = Utils.formatCurrency(helper.getTotalTax())
                textViewSubtotal.text = Utils.formatCurrency(helper.getSubTotal())
            }
            
            // Establecer valores de los totales
            tvSubtotalGravado.text = mNumberFormat.format(helper?.getSubtotalGrabado() ?: 0.0)
            tvSubtotalExento.text = mNumberFormat.format(helper?.getSubtotalExento() ?: 0.0)
            tvSubtotal.text = mNumberFormat.format(helper?.getSubTotal() ?: 0.0)
            tvDescuento.text = mNumberFormat.format(helper?.getDescuento() ?: 0.0)
            tvImpuesto.text = mNumberFormat.format(helper?.getTotalTax() ?: 0.0)
            tvTotal.text = mNumberFormat.format(helper?.getTotal() ?: 0.0)
            
            // Configurar el RadioGroup de forma de pago
            rgFormaPago.check(R.id.rbEfectivo)
            llPayCash.visibility = View.VISIBLE
            llPayCredit.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding?.apply {
            switchEfectivo.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    linearLayoutCredito.visibility = View.GONE
                } else {
                    linearLayoutCredito.visibility = View.VISIBLE
                }
            }
            
            editTextMonto.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    calcularCambio()
                }
                
                override fun afterTextChanged(s: Editable?) {}
            })
            
            buttonGuardar.setOnClickListener {
                guardarFactura()
            }
            
            // Listener para el RadioGroup de forma de pago
            rgFormaPago.setOnCheckedChangeListener { _: RadioGroup, checkedId: Int ->
                when (checkedId) {
                    R.id.rbEfectivo -> {
                        esEfectivo = true
                        llPayCash.visibility = View.VISIBLE
                        llPayCredit.visibility = View.GONE
                    }
                    R.id.rbCredito -> {
                        esEfectivo = false
                        llPayCash.visibility = View.GONE
                        llPayCredit.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun obtenerItems(): List<InvoiceItem> {
        return activity?.listItems ?: emptyList()
    }

    private fun calcularCambio() {
        val totalFactura = helper?.getTotal() ?: 0.0
        
        binding?.let { b ->
            val pagoText = b.editTextMonto.text.toString()
            paidAmount = if (pagoText.isNotEmpty()) pagoText.toDouble() else 0.0
            
            val cambio = paidAmount - totalFactura
            b.textViewCambio.text = Utils.formatCurrency(if (cambio >= 0) cambio else 0.0)
            
            // Cambiar color según si alcanza o no el monto
            if (paidAmount >= totalFactura) {
                b.textViewCambio.setTextColor(Color.GREEN)
            } else {
                b.textViewCambio.setTextColor(Color.RED)
            }
        }
    }

    private fun guardarFactura() {
        val totalFactura = helper?.getTotal() ?: 0.0
        val notas = binding?.editTextNotas?.text?.toString() ?: ""
        
        // Validar según forma de pago
        if (esEfectivo) {
            if (paidAmount < totalFactura) {
                mostrarSnackbar("El monto pagado es menor al total de la factura")
                return
            }
        }
        
        // Guardar factura
        val exitoso = helper?.saveInvoice(esEfectivo, notas, activity?.clienteId) ?: false
        
        if (exitoso) {
            mostrarSnackbar("Factura guardada exitosamente")
            activity?.clearData()
            activity?.navigateToProductList()
        } else {
            mostrarSnackbar("Error al guardar la factura")
        }
    }

    private fun mostrarSnackbar(mensaje: String) {
        binding?.root?.let {
            Snackbar.make(it, mensaje, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
        helper = null
    }

    override fun updateData() {
        adapter?.notifyDataSetChanged()
        totalizeHelper?.let { helper ->
            binding?.apply {
                textViewTotal.text = Utils.formatCurrency(helper.getTotal())
                textViewImpuesto.text = Utils.formatCurrency(helper.getTotalTax())
                textViewSubtotal.text = Utils.formatCurrency(helper.getSubTotal())
            }
        }
        calcularCambio()
    }
    
    companion object {
        fun newInstance(): DistTotalizarFragment = DistTotalizarFragment()
    }
}