package com.friendlysystemgroup.friendlypos.reimpresion.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.databinding.ListaReimprimirFacturasBinding
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import com.friendlysystemgroup.friendlypos.reimpresion.activity.ReimprimirActivity
import io.realm.Realm
import java.text.NumberFormat
import java.util.Locale

/**
 * Adaptador para mostrar la lista de facturas a reimprimir
 */
class ReimprimirFacturaAdapter(
    private val context: Context,
    private val activity: ReimprimirActivity,
    var contentList: List<sale>
) : RecyclerView.Adapter<ReimprimirFacturaAdapter.CharacterViewHolder>() {
    
    // Posición seleccionada para marcar visualmente
    private var selectedPosition = -1
    
    // Formato para mostrar valores monetarios
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    
    // Colores para UI
    private val selectedColor = Color.parseColor("#d1d3d4")
    private val normalColor = Color.parseColor("#FFFFFF")
    private val notUploadedColor = Color.parseColor("#FF0000")
    private val uploadedColor = Color.parseColor("#607d8b")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ListaReimprimirFacturasBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val sale = contentList[position]
        val realm = Realm.getDefaultInstance()
        
        try {
            // Obtener datos de cliente y factura
            val cliente = realm.where(Clientes::class.java)
                .equalTo("id", sale.customer_id)
                .findFirst()
                
            val factura = realm.where(invoice::class.java)
                .equalTo("id", sale.invoice_id)
                .findFirst()
                
            // Contar productos en la factura
            val cantidadProductos = realm.where(Pivot::class.java)
                .equalTo("invoice_id", sale.invoice_id)
                .equalTo("devuelvo", 0)
                .count()
                
            val numeracionFactura = factura?.numeration ?: ""
            val nombreCliente = cliente?.fantasyName ?: ""
            val fecha = factura?.date ?: ""
            val hora = factura?.times ?: ""
            val subida = factura?.subida ?: 0
            val total = factura?.total?.toDoubleOrNull() ?: 0.0
                
            // Establecer datos en las vistas
            with(holder.binding) {
                txtReimprimirFacturaNumeracion.text = numeracionFactura
                txtReimprimirFacturaFechahora.text = "$fecha $hora"
                txtReimprimirFacturaFantasyname.text = nombreCliente
                txtReimprimirFacturaAnombrede.text = sale.customer_name
                
                // Color según si está subida o no
                txtSubida.setBackgroundColor(
                    if (subida == 1) notUploadedColor else uploadedColor
                )
                
                // Formatear monto con separador de miles y decimales
                txtReimprimirFacturaTotal.text = numberFormat.format(total)
                
                // Cantidad de productos
                txtReimprimirFacturaCantidad.text = cantidadProductos.toString()
                
                // Marcar la seleccionada
                cardViewReimprimirFactura.setBackgroundColor(
                    if (selectedPosition == position) selectedColor else normalColor
                )
            }
        } finally {
            realm.close()
        }
    }

    override fun getItemCount(): Int = contentList.size

    inner class CharacterViewHolder(val binding: ListaReimprimirFacturasBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.cardViewReimprimirFactura.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                
                // Actualizar selección y visualización
                val previousSelected = selectedPosition
                selectedPosition = position
                
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                
                // Obtener datos de la factura seleccionada
                val selectedItem = contentList[position]
                val facturaId = selectedItem.invoice_id
                
                // Mensaje de confirmación
                Toast.makeText(context, "Seleccionada: $facturaId", Toast.LENGTH_SHORT).show()
                
                // Actualizar estado en la actividad
                activity.selecFacturaTab = 1
                activity.invoiceIdReimprimir = facturaId
                
                Log.d("FacturaSeleccionada", "ID: $facturaId")
            }
        }
    }
}
