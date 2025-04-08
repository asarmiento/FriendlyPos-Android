package com.friendlysystemgroup.friendlypos.reimprimirRecibos.adapters

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.ReimprimirRecibos.activity.ReimprimirRecibosActivity
import com.friendlysystemgroup.friendlypos.databinding.ListaReimprimirRecibosFacturasBinding
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils
import java.text.NumberFormat
import java.util.Locale

/**
 * Created by Delvo on 03/12/2017.
 */
class ReimprimirReciboFacturaAdapter(
    private val context: Context?,
    private val activity: ReimprimirRecibosActivity,
    private var contentList: List<receipts>
) : RecyclerView.Adapter<ReimprimirReciboFacturaAdapter.CharacterViewHolder>() {
    
    private var selectedPosition = -1
    private val numberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ListaReimprimirRecibosFacturasBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CharacterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val receipt = contentList[position]
        holder.bind(receipt, position == selectedPosition)
    }

    override fun getItemCount(): Int = contentList.size
    
    fun updateData(newList: List<receipts>) {
        contentList = newList
        notifyDataSetChanged()
    }

    inner class CharacterViewHolder(
        private val binding: ListaReimprimirRecibosFacturasBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.cardViewResumenRecibosFactura.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                
                showProgressDialog()
                
                val previousPosition = selectedPosition
                selectedPosition = position
                
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                
                val clickedRecibo = contentList[position]
                val reciboId = clickedRecibo.reference
                
                Toast.makeText(context, "Seleccionado: $reciboId", Toast.LENGTH_SHORT).show()
                
                activity.selecReciboTab = 1
                activity.invoiceIdReimprimirRecibo = reciboId
                Log.d("ReciboSelected", "ID: $reciboId")
            }
        }
        
        fun bind(receipt: receipts, isSelected: Boolean) {
            val realm = Realm.getDefaultInstance()
            try {
                val nuevoRecibo = realm.where(recibos::class.java)
                    .equalTo("customer_id", receipt.customer_id)
                    .findFirst()
                
                val cliente = realm.where(Clientes::class.java)
                    .equalTo("id", receipt.customer_id)
                    .findFirst()
                
                binding.apply {
                    txtResumenNumeracionRecibosFactura.text = "Número del Recibo: ${receipt.numeration ?: ""}"
                    txtClienteFacturaReferenciaRecibosFactura.text = "Referencia del Recibo: ${receipt.reference ?: ""}"
                    
                    txtClienteFacturaFantasynameRecibosFactura.text = cliente?.fantasyName ?: ""
                    txtClienteFacturaCompanynameRecibosFactura.text = cliente?.companyName ?: ""
                    
                    cardViewResumenRecibosFactura.setCardBackgroundColor(
                        if (isSelected) Color.parseColor("#d1d3d4") else Color.WHITE
                    )
                }
            } finally {
                realm.close()
            }
        }
        
        private fun showProgressDialog() {
            context?.let { ctx ->
                val progresRing = ProgressDialog(ctx)
                val message = "Seleccionando Recibo"
                val titulo = "Cargando"
                
                val spannableString = SpannableString(message)
                val spannableStringTitulo = SpannableString(titulo)
                
                val typefaceSpan = CalligraphyTypefaceSpan(
                    TypefaceUtils.load(ctx.assets, "font/monse.otf")
                )
                
                spannableString.setSpan(
                    typefaceSpan,
                    0,
                    message.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                
                spannableStringTitulo.setSpan(
                    typefaceSpan, 
                    0, 
                    titulo.length, 
                    Spanned.SPAN_PRIORITY
                )
                
                progresRing.apply {
                    setTitle(spannableStringTitulo)
                    setMessage(spannableString)
                    setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    isIndeterminate = true
                    setCancelable(true)
                    show()
                }
                
                // Auto-dismiss after delay
                Thread {
                    try {
                        Thread.sleep(2000)
                    } catch (e: Exception) {
                        Log.e("ProgressDialog", "Error during sleep", e)
                    }
                    progresRing.dismiss()
                }.start()
            }
        }
    }
}
