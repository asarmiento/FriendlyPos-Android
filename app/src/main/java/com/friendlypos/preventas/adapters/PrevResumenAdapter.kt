package com.friendlysystemgroup.friendlypos.preventas.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.preventas.activity.PreventaActivity
import com.friendlysystemgroup.friendlypos.preventas.fragment.PrevResumenFragment
import com.friendlysystemgroup.friendlypos.preventas.modelo.Bonuses
import com.friendlysystemgroup.friendlypos.principal.modelo.Productos
import io.realm.Realm


/**
 * Created by DelvoM on 21/09/2017.
 */
class PrevResumenAdapter : RecyclerView.Adapter<PrevResumenAdapter.CharacterViewHolder> {
    private var productosList: List<Pivot>? = null
    private var activity: PreventaActivity? = null
    private var selected_position1 = -1
    var amount_inventario: Double = 0.0
    var idInvetarioSelec: Int = 0
    var nextId: Int = 0
    private var fragment: PrevResumenFragment? = null

    constructor(
        activity: PreventaActivity?,
        fragment: PrevResumenFragment?,
        productosList: List<Pivot>?
    ) {
        this.productosList = productosList
        this.activity = activity
        this.fragment = fragment
    }

    constructor()

    fun updateData(productosList: List<Pivot>?) {
        activity!!.cleanTotalize()
        this.productosList = productosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_distribucion_resumen, parent, false)
        return CharacterViewHolder(view)
    }

    private fun getProductDescriptionByPivotId(id: String?): String {
        val realm = Realm.getDefaultInstance()
        val description = realm.where(Productos::class.java)
            .equalTo("id", id).findFirst()!!
            .description
        realm.close()
        return description
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val pivot = productosList!![position]
        val pivotTotal: String
        val amountBonif: Double
        holder.txt_resumen_factura_nombre.text = getProductDescriptionByPivotId(pivot.product_id)
        holder.txt_resumen_factura_precio.text = "P: " + pivot.price!!.toDouble()
        holder.txt_resumen_factura_descuento.text = "Descuento de: " + pivot.discount!!.toDouble()
        holder.txt_resumen_factura_cantidad.text = "C: " + pivot.amount!!.toDouble()

        val realm0 = Realm.getDefaultInstance()
        val bonus = realm0.where(Productos::class.java)
            .equalTo("id", pivot.product_id).findFirst()!!
            .bonus
        realm0.close()

        if (bonus == "1") {
            val realmBonus = Realm.getDefaultInstance()

            realmBonus.executeTransaction { realmBonus ->
                val productoConBonus = realmBonus.where(Bonuses::class.java)
                    .equalTo("product_id", pivot.product_id!!.toInt()).findFirst()
                productosDelBonus =
                    productoConBonus!!.product_bonus.toDouble()
                Log.d(
                    "BONIFTOTAL",
                    productoConBonus.product_id.toString() + " " + productosDelBonus
                )
            }
            amountBonif = pivot.amount!!.toDouble() - productosDelBonus
            pivotTotal = String.format("%,.2f", (pivot.price!!.toDouble()) * amountBonif)
            Log.d("BONIFTOTALPIVOT", "$pivotTotal ")
        } else {
            pivotTotal =
                String.format("%,.2f", (pivot.price!!.toDouble() * pivot.amount!!.toDouble()))
            Log.d("TOTALPIVOT", "$pivotTotal ")
        }

        holder.txt_resumen_factura_total.text = "T: $pivotTotal"
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return productosList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }


    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_resumen_factura_nombre: TextView =
            view.findViewById<View>(R.id.txt_resumen_factura_nombre) as TextView
        val txt_resumen_factura_descuento: TextView =
            view.findViewById<View>(R.id.txt_resumen_factura_descuento) as TextView
        val txt_resumen_factura_precio: TextView =
            view.findViewById<View>(R.id.txt_resumen_factura_precio) as TextView
        val txt_resumen_factura_cantidad: TextView =
            view.findViewById<View>(R.id.txt_resumen_factura_cantidad) as TextView
        val txt_resumen_factura_total: TextView =
            view.findViewById<View>(R.id.txt_resumen_factura_total) as TextView
        protected var cardView: CardView =
            view.findViewById<View>(R.id.cardViewResumen) as CardView
        var btnEliminarResumen: ImageButton =
            view.findViewById<View>(R.id.btnEliminarResumen) as ImageButton

        init {
            btnEliminarResumen.setOnClickListener { view ->
                activity!!.cleanTotalize()
                val pos = adapterPosition

                notifyItemChanged(selected_position1)
                selected_position1 = adapterPosition
                notifyItemChanged(selected_position1)

                val clickedDataItem = productosList!![pos]

                val resumenProductoId = clickedDataItem.id
                val cantidadProducto = clickedDataItem.amount!!.toDouble()

                Toast.makeText(view.context, "Se borró el producto", Toast.LENGTH_SHORT).show()

                // TRANSACCIÓN BD PARA BORRAR EL CAMPO
                activity!!.initProducto(pos)

                notifyDataSetChanged()
                fragment!!.updateData()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }


    companion object {
        private val aListdata = ArrayList<Pivot>()
        private var productosDelBonus = 0.0
    }
}

