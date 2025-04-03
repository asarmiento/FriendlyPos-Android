package com.friendlypos.reimpresion.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.invoice
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.reimpresion.activity.ReimprimirActivity
import io.realm.Realm
import io.realm.RealmResults

/**
 * Created by Delvo on 03/12/2017.
 */
class ReimprimirFacturaAdapter(
    context: Context?,
    private val activity: ReimprimirActivity,
    var contentList: List<sale>
) :
    RecyclerView.Adapter<ReimprimirFacturaAdapter.CharacterViewHolder>() {
    //private boolean isSelected = false;
    private var selected_position = -1
    var facturaid1: RealmResults<Pivot>? = null
    var idInvetarioSelec: Int = 0
    var amount_dist_inventario: Double = 0.0
    var facturaID: String? = null
    var clienteID: String? = null
    var nextId: Int = 0
    var tabCliente: Int = 0

    init {
        QuickContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_reimprimir_facturas, parent, false)

        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val sale = contentList[position]

        val realm = Realm.getDefaultInstance()

        var cantidadPivot: Long = 0

        val clientes = realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
        val invoice = realm.where(invoice::class.java).equalTo("id", sale.invoice_id).findFirst()

        cantidadPivot = realm.where(Pivot::class.java).equalTo("invoice_id", sale.invoice_id)
            .equalTo("devuelvo", 0).count()

        val query1 = realm.where(Pivot::class.java).equalTo("invoice_id", sale.invoice_id)
        val result1 = query1.findAll()
        Log.d("pivot", result1.toString() + "")

        val numeracionFactura = invoice!!.numeration
        val fantasyCliente = clientes!!.fantasyName
        val fecha1 = invoice.date
        val hora1 = invoice.times
        val subida = invoice.subida
        val aNombreDe = sale.customer_name
        val totalFactura = invoice.total!!.toDouble()

        holder.txt_reimprimir_factura_numeracion.text = numeracionFactura
        holder.txt_reimprimir_factura_fechahora.text = "$fecha1 $hora1"
        holder.txt_reimprimir_factura_fantasyname.text = fantasyCliente
        holder.txt_reimprimir_factura_anombrede.text = aNombreDe

        if (subida == 1) {
            holder.txtSubida.setBackgroundColor(Color.parseColor("#FF0000"))
        } else {
            holder.txtSubida.setBackgroundColor(Color.parseColor("#607d8b"))
        }

        holder.txt_reimprimir_factura_total.text = String.format("%,.2f", totalFactura)
        holder.txt_reimprimir_factura_cantidad.text = cantidadPivot.toInt().toString() + ""

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
        val txt_reimprimir_factura_numeracion: TextView =
            view.findViewById<View>(R.id.txt_reimprimir_factura_numeracion) as TextView
        val txt_reimprimir_factura_fechahora: TextView =
            view.findViewById<View>(R.id.txt_reimprimir_factura_fechahora) as TextView
        val txt_reimprimir_factura_fantasyname: TextView =
            view.findViewById<View>(R.id.txt_reimprimir_factura_fantasyname) as TextView
        val txt_reimprimir_factura_anombrede: TextView =
            view.findViewById<View>(R.id.txt_reimprimir_factura_anombrede) as TextView
        val txt_reimprimir_factura_total: TextView =
            view.findViewById<View>(R.id.txt_reimprimir_factura_total) as TextView
        val txt_reimprimir_factura_cantidad: TextView =
            view.findViewById<View>(R.id.txt_reimprimir_factura_cantidad) as TextView
        val txtSubida: TextView = view.findViewById<View>(R.id.txtSubida) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewReimprimirFactura) as CardView


        init {
            cardView.setOnClickListener(View.OnClickListener { view ->
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@OnClickListener
                notifyItemChanged(selected_position)
                selected_position = adapterPosition
                notifyItemChanged(selected_position)

                val clickedDataItem = contentList[pos]
                facturaID = clickedDataItem.invoice_id

                Toast.makeText(view.context, "You clicked $facturaID", Toast.LENGTH_SHORT).show()

                tabCliente = 1
                activity.selecFacturaTab = tabCliente
                activity.invoiceIdReimprimir = facturaID
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
