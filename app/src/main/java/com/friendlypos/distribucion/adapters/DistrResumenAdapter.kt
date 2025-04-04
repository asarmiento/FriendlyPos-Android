package com.friendlysystemgroup.friendlypos.distribucion.adapters

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
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.fragment.DistResumenFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.principal.modelo.Productos
import io.realm.Realm


/**
 * Created by DelvoM on 21/09/2017.
 */
class DistrResumenAdapter : RecyclerView.Adapter<DistrResumenAdapter.CharacterViewHolder> {
    private var productosList: List<Pivot>? = null
    private var activity: DistribucionActivity? = null
    private var selected_position1 = -1
    var amount_inventario: Double = 0.0
    var idInvetarioSelec: Int = 0
    var amount_dist_inventario: Double = 0.0
    var nextId: Int = 0
    private var fragment: DistResumenFragment? = null

    constructor(
        activity: DistribucionActivity?,
        fragment: DistResumenFragment?,
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

    private fun getProductDescriptionByPivotId(id: String): String {
        val realm = Realm.getDefaultInstance()
        val description = realm.where(Productos::class.java)
            .equalTo("id", id).findFirst()!!
            .description
        realm.close()
        return description
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val pivot = productosList!![position]

        holder.txt_resumen_factura_nombre.text = getProductDescriptionByPivotId(pivot.product_id)
        holder.txt_resumen_factura_precio.text = "P: " + pivot.price.toDouble()
        holder.txt_resumen_factura_descuento.text = "Descuento de: " + pivot.discount.toDouble()
        holder.txt_resumen_factura_cantidad.text = "C: " + pivot.amount.toDouble()

        val pivotTotal = String.format("%,.2f", (pivot.price.toDouble() * pivot.amount.toDouble()))

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
                val cantidadProducto = clickedDataItem.amount.toDouble()

                Toast.makeText(view.context, "Se borró el producto", Toast.LENGTH_SHORT).show()

                // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                val realm3 = Realm.getDefaultInstance()
                realm3.executeTransaction { realm3 ->
                    val inventario = realm3.where(Inventario::class.java)
                        .equalTo("product_id", clickedDataItem.product_id).findFirst()
                    idInvetarioSelec = inventario!!.id
                    amount_inventario = inventario.amount.toDouble()
                    amount_dist_inventario = inventario.amount_dist.toDouble()
                    realm3.close()
                    Log.d("idinventario", idInvetarioSelec.toString() + "")
                }

                // OBTENER NUEVO AMOUNT
                val nuevoAmountDevuelto = cantidadProducto + amount_inventario
                Log.d("nuevoAmount", nuevoAmountDevuelto.toString() + "")

                val nuevoAmountDistDevuelto = amount_dist_inventario - cantidadProducto
                Log.d("nuevoAmountDist", nuevoAmountDistDevuelto.toString() + "")

                // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT EN EL INVENTARIO
                val realm2 = Realm.getDefaultInstance()
                realm2.executeTransaction { realm2 ->
                    val inv_actualizado = realm2.where(Inventario::class.java)
                        .equalTo("id", idInvetarioSelec).findFirst()
                    inv_actualizado!!.amount = nuevoAmountDevuelto.toString()
                    inv_actualizado.amount_dist = nuevoAmountDistDevuelto.toString()
                    inv_actualizado.devuelvo = 1
                    realm2.insertOrUpdate(inv_actualizado)
                    Log.d("DevolucionTotal", inv_actualizado.toString() + "")
                    realm2.close()
                }


                // TRANSACCIÓN BD PARA BORRAR EL CAMPO
                val realm5 = Realm.getDefaultInstance()
                realm5.executeTransaction { realm5 ->
                    val inv_actualizado =
                        realm5.where(Pivot::class.java).equalTo("id", resumenProductoId)
                            .findFirst()
                    inv_actualizado!!.devuelvo = 1
                    realm5.insertOrUpdate(inv_actualizado)
                    realm5.close()
                }

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
    }
}

