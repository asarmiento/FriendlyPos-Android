package com.friendlypos.Recibos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.Recibos.activity.RecibosActivity
import com.friendlypos.Recibos.fragments.RecibosResumenFragment
import com.friendlypos.Recibos.modelo.recibos

/**
 * Created by Delvo on 19/09/2018.
 */
class RecibosResumenAdapter : RecyclerView.Adapter<RecibosResumenAdapter.CharacterViewHolder> {
    private var productosList: List<recibos>? = null
    private var activity: RecibosActivity? = null
    private val selected_position1 = -1
    var amount_inventario: Double = 0.0
    var idInvetarioSelec: Int = 0
    var nextId: Int = 0
    private var fragment: RecibosResumenFragment? = null

    constructor(
        activity: RecibosActivity?,
        fragment: RecibosResumenFragment?,
        productosList: List<recibos>?
    ) {
        this.productosList = productosList
        this.activity = activity
        this.fragment = fragment
    }

    constructor()

    fun updateData(productosList: List<recibos>?) {
        activity!!.cleanTotalize()
        this.productosList = productosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_recibos_resumen, parent, false)
        return CharacterViewHolder(view)
    }


    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val recibo = productosList!![position]
        val numeracion = recibo.numeration
        val montoPagado = recibo.montoCanceladoPorFactura

        holder.txt_resumen_numeracionRecibos.text = "# de factura: $numeracion"
        holder.txt_resumen_abonoRecibos.text =
            "Cantidad pagada: " + String.format("%,.2f", montoPagado)
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
        val txt_resumen_numeracionRecibos: TextView =
            view.findViewById<View>(R.id.txt_resumen_numeracionRecibos) as TextView
        val txt_resumen_abonoRecibos: TextView =
            view.findViewById<View>(R.id.txt_resumen_abonoRecibos) as TextView
        protected var cardView: CardView =
            view.findViewById<View>(R.id.cardViewResumenRecibos) as CardView
        var btnEliminarResumenRecibos: ImageButton =
            view.findViewById<View>(R.id.btnEliminarResumenRecibos) as ImageButton

        init {
            btnEliminarResumenRecibos.setOnClickListener { /*
                           activity.cleanTotalize();
                            int pos = getAdapterPosition();
        
                            notifyItemChanged(selected_position1);
                            selected_position1 = getAdapterPosition();
                            notifyItemChanged(selected_position1);
        
                            final Pivot clickedDataItem = productosList.get(pos);
        
                            final int resumenProductoId = clickedDataItem.getId();
                            final double cantidadProducto = Double.parseDouble(clickedDataItem.getAmount());
        
                            // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                            Realm realm3 = Realm.getDefaultInstance();
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
        
        
                            // TRANSACCIÓN BD PARA BORRAR EL CAMPO
        
                            final Realm realm5 = Realm.getDefaultInstance();
                            realm5.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm5) {
                                    Pivot inv_actualizado = realm5.where(Pivot.class).equalTo("id", resumenProductoId).findFirst();
                                    inv_actualizado.setDevuelvo(1);
                                    realm5.insertOrUpdate(inv_actualizado);
                                    realm5.close();
                                }
                            });
        
                            notifyDataSetChanged();
                            fragment.updateData();
        */
                notifyDataSetChanged()
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }


    companion object {
        private val aListdata = ArrayList<recibos>()
    }
}