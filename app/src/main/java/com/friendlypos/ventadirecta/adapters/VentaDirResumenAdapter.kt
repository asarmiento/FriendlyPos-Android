package com.friendlypos.ventadirecta.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.modelo.Inventario
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.principal.modelo.Productos
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity
import com.friendlypos.ventadirecta.fragment.VentaDirResumenFragment
import io.realm.Realm


/**
 * Created by DelvoM on 21/09/2017.
 */
class VentaDirResumenAdapter : RecyclerView.Adapter<VentaDirResumenAdapter.CharacterViewHolder> {
    private var productosList: List<Pivot>? = null
    private var activity: VentaDirectaActivity? = null
    private var selected_position1 = -1
    var amount_inventario: Double = 0.0
    var idInvetarioSelec: Int = 0
    var nextId: Int = 0
    private var fragment: VentaDirResumenFragment? = null
    var pivotTotal: String? = null

    constructor(
        activity: VentaDirectaActivity?,
        fragment: VentaDirResumenFragment?,
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

    private fun getProductDescriptionByPivotId(id: String?): String? {
        val realm = Realm.getDefaultInstance()
        val description = realm.where(Productos::class.java)
            .equalTo("id", id).findFirst()!!.description
        realm.close()
        return description
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val pivot = productosList!![position]

        holder.txt_resumen_factura_nombre.text = getProductDescriptionByPivotId(pivot.product_id)
        holder.txt_resumen_factura_precio.text = "P: " + pivot.price!!.toDouble()
        holder.txt_resumen_factura_descuento.text = "Descuento de: " + pivot.discount!!.toDouble()
        holder.txt_resumen_factura_cantidad.text = "C: " + pivot.amount!!.toDouble()

        val bonus = getProductBonusByPivotId(pivot.product_id)

        pivotTotal = if (bonus == "1" && pivot.bonus == 1) {
            String.format("%,.2f", (pivot.price!!.toDouble() * pivot.amountSinBonus))
        } else {
            String.format("%,.2f", (pivot.price!!.toDouble() * pivot.amount!!.toDouble()))
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
            btnEliminarResumen.setOnClickListener {
                activity!!.cleanTotalize()
                val pos = adapterPosition

                // Updating old as well as new positions
                notifyItemChanged(selected_position1)
                selected_position1 = adapterPosition
                notifyItemChanged(selected_position1)

                val clickedDataItem = productosList!![pos]

                val cantidadProducto = clickedDataItem.amount!!.toDouble()


                // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                val realm3 = Realm.getDefaultInstance()
                realm3.executeTransaction { realm3 ->
                    val inventario = realm3.where(Inventario::class.java)
                        .equalTo("product_id", clickedDataItem.product_id).findFirst()
                    idInvetarioSelec = inventario!!.id
                    amount_inventario = inventario.amount!!.toDouble()
                    realm3.close()
                    Log.d("idinventario", idInvetarioSelec.toString() + "")
                }

                // OBTENER NUEVO AMOUNT
                val nuevoAmountDevuelto = cantidadProducto + amount_inventario
                Log.d("nuevoAmount", nuevoAmountDevuelto.toString() + "")

                // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT EN EL INVENTARIO
                val realm2 = Realm.getDefaultInstance()
                realm2.executeTransaction { realm2 ->
                    val inv_actualizado = realm2.where(Inventario::class.java)
                        .equalTo("id", idInvetarioSelec).findFirst()
                    inv_actualizado!!.amount = nuevoAmountDevuelto.toString()
                    realm2.insertOrUpdate(inv_actualizado)
                    realm2.close()
                }

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

    private fun getProductBonusByPivotId(id: String?): String? {
        val realm = Realm.getDefaultInstance()
        val bonus = realm.where(Productos::class.java).equalTo("id", id)
            .findFirst()!!.bonus
        realm.close()
        return bonus
    }

    companion object {
        private val aListdata = ArrayList<Pivot>()
    }
}
