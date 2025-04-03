package com.friendlypos.reimpresion_pedidos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.principal.modelo.Productos
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity
import com.friendlypos.reimpresion_pedidos.fragment.ReimPedidoResumenFragment
import io.realm.Realm


/**
 * Created by DelvoM on 21/09/2017.
 */
class ReimPedidoResumenAdapter :
    RecyclerView.Adapter<ReimPedidoResumenAdapter.CharacterViewHolder> {
    private var productosList: List<Pivot>? = null
    private var activity: ReimprimirPedidosActivity? = null
    private var selected_position1 = -1
    var amount_inventario: Double = 0.0
    var idInvetarioSelec: Int = 0
    var nextId: Int = 0
    private var fragment: ReimPedidoResumenFragment? = null

    constructor(
        activity: ReimprimirPedidosActivity?,
        fragment: ReimPedidoResumenFragment?,
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

        val pivotTotal =
            String.format("%,.2f", (pivot.price!!.toDouble() * pivot.amount!!.toDouble()))

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

                // Updating old as well as new positions
                notifyItemChanged(selected_position1)
                selected_position1 = adapterPosition
                notifyItemChanged(selected_position1)

                val clickedDataItem = productosList!![pos]

                val resumenProductoId = clickedDataItem.id
                val cantidadProducto = clickedDataItem.amount!!.toDouble()

                Toast.makeText(view.context, "You clicked $resumenProductoId", Toast.LENGTH_SHORT)
                    .show()

                //     activity.initProducto(pos);
                // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                /*   Realm realm3 = Realm.getDefaultInstance();
                            realm3.executeTransaction(new Realm.Transaction() {
        
                                @Override
                                public void execute(Realm realm3) {
        
                                    Inventario inventario = realm3.where(Inventario.class).equalTo("product_id", clickedDataItem.getProduct_id()).findFirst();
                                    idInvetarioSelec = inventario.getId();
                                    amount_inventario = Double.valueOf(inventario.getAmount());
                                    realm3.close();
                                    Log.d("idinventario", idInvetarioSelec + "");
                                }
                            });
        
                            // OBTENER NUEVO AMOUNT
                            final Double nuevoAmountDevuelto = cantidadProducto + amount_inventario;
                            Log.d("nuevoAmount", nuevoAmountDevuelto + "");
        
                            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT EN EL INVENTARIO
                            final Realm realm2 = Realm.getDefaultInstance();
                            realm2.executeTransaction(new Realm.Transaction() {
        
                                @Override
                                public void execute(Realm realm2) {
                                    Inventario inv_actualizado = realm2.where(Inventario.class).equalTo("id", idInvetarioSelec).findFirst();
                                    inv_actualizado.setAmount(String.valueOf(nuevoAmountDevuelto));
                                    realm2.insertOrUpdate(inv_actualizado);
        
                                    realm2.close();
                                }
                            });
        
        
        
                            // TRANSACCIÓN PARA COPIAR DATOS EN LA BASE DEVUELTA
                            final Realm realmInvDevolver = Realm.getDefaultInstance();
                            realmInvDevolver.executeTransaction(new Realm.Transaction() {
        
                                @Override
                                public void execute(Realm realmInvDevolver) {
        
                                    // increment index
                                    Number currentIdNum = realmInvDevolver.where(InventarioDevuelto.class).max("id_devuelto");
        
                                    if (currentIdNum == null) {
                                        nextId = 1;
                                    }
                                    else {
                                        nextId = currentIdNum.intValue() + 1;
                                    }
        
                                    InventarioDevuelto inv_actualizado = realmInvDevolver.where(InventarioDevuelto.class).findFirst();
                                    inv_actualizado.setId_devuelto(nextId);
                                    inv_actualizado.setDate_devuelto(Functions.getDate());
                                    inv_actualizado.setTimes_devuelto(Functions.get24Time());
                                    realmInvDevolver.insertOrUpdate(inv_actualizado);
                                    realmInvDevolver.close();
                                }
                            });
        
                            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO CREDIT_LIMIT EN LA FACTURA
                           final Realm realm5 = Realm.getDefaultInstance();
                            realm5.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm5) {
                                    Clientes inv_actualizado = realm5.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();
                                    inv_actualizado.setCreditLimit(String.valueOf(credi));
                                    realm5.insertOrUpdate(inv_actualizado);
                                    realm5.close();
                                }
                            });
        */
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


                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->
                    val result =
                        realm.where(Pivot::class.java).equalTo("id", resumenProductoId)
                            .findAll()
                    result.deleteAllFromRealm()
                    realm.close()
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

