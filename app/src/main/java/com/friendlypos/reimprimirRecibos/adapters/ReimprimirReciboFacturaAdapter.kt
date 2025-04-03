package com.friendlypos.reimprimirRecibos.adapters

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.Recibos.modelo.receipts
import com.friendlypos.Recibos.modelo.recibos
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.reimprimirRecibos.activity.ReimprimirRecibosActivity
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

/**
 * Created by Delvo on 03/12/2017.
 */
class ReimprimirReciboFacturaAdapter(
    context: Context?,
    private val activity: ReimprimirRecibosActivity,
    var contentList: List<receipts>
) :
    RecyclerView.Adapter<ReimprimirReciboFacturaAdapter.CharacterViewHolder>() {
    private var selected_position = -1
    var facturaID: String? = null
    var tabCliente: Int = 0

    init {
        QuickContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_reimprimir_recibos_facturas, parent, false)

        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val receipt = contentList[position]

        val realm = Realm.getDefaultInstance()

        val cantidadPivot: Long = 0

        val nuevoRecibo =
            realm.where(recibos::class.java).equalTo("customer_id", receipt.customer_id).findFirst()


        val clientes =
            realm.where(Clientes::class.java).equalTo("id", receipt.customer_id).findFirst()
        Log.d("nuevoRecibo", nuevoRecibo.toString() + "")

        val cardCliente = clientes!!.card
        val companyCliente = clientes.companyName
        val fantasyCliente = clientes.fantasyName

        val numeracionFactura = receipt.numeration
        val totalFactura = receipt.montoPagado

        holder.txt_resumen_numeracionRecibosFactura.text = "Número del Recibo: $numeracionFactura"
        holder.txt_cliente_factura_referenciaRecibosFactura.text =
            "Referencia del Recibo: " + receipt.reference

        holder.txt_cliente_factura_fantasynameRecibosFactura.text = fantasyCliente
        holder.txt_cliente_factura_companynameRecibosFactura.text = companyCliente

        holder.cardView.setBackgroundColor(
            if (selected_position == position) Color.parseColor("#d1d3d4") else Color.parseColor(
                "#FFFFFF"
            )
        )
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_resumen_numeracionRecibosFactura: TextView =
            view.findViewById<View>(R.id.txt_resumen_numeracionRecibosFactura) as TextView
        val txt_cliente_factura_referenciaRecibosFactura: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_referenciaRecibosFactura) as TextView
        val txt_cliente_factura_fantasynameRecibosFactura: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_fantasynameRecibosFactura) as TextView
        val txt_cliente_factura_companynameRecibosFactura: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_companynameRecibosFactura) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewResumenRecibosFactura) as CardView


        init {
            cardView.setOnClickListener(View.OnClickListener { view ->
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@OnClickListener


                val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                                "Seleccionando Cliente", true);*/
                val message = "Seleccionando Recibo"
                val titulo = "Cargando"
                val spannableString = SpannableString(message)
                val spannableStringTitulo = SpannableString(titulo)

                val typefaceSpan = CalligraphyTypefaceSpan(
                    TypefaceUtils.load(
                        QuickContext!!.assets,
                        "font/monse.otf"
                    )
                )
                spannableString.setSpan(
                    typefaceSpan,
                    0,
                    message.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length, Spanned.SPAN_PRIORITY)

                progresRing.setTitle(spannableStringTitulo)
                progresRing.setMessage(spannableString)
                progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progresRing.isIndeterminate = true
                progresRing.setCancelable(true)
                progresRing.show()


                Thread {
                    try {
                        Thread.sleep(3000)
                    } catch (e: Exception) {
                    }
                    progresRing.dismiss()
                }.start()



                notifyItemChanged(selected_position)
                selected_position = adapterPosition
                notifyItemChanged(selected_position)

                val clickedDataItem = contentList[pos]
                facturaID = clickedDataItem.reference

                Toast.makeText(view.context, "You clicked $facturaID", Toast.LENGTH_SHORT).show()

                tabCliente = 1
                activity.selecReciboTab = tabCliente
                activity.invoiceIdReimprimirRecibo = facturaID
                Log.d("metodoPago", facturaID + "")
            })
        }
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private var QuickContext: Context? = null
    }
}
