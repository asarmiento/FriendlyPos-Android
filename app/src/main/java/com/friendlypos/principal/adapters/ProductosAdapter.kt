package com.friendlysystemgroup.friendlypos.principal.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Marcas
import com.friendlysystemgroup.friendlypos.principal.modelo.Productos
import io.realm.Realm


/**
 * Created by DelvoM on 21/09/2017.
 */
class ProductosAdapter(private var productosList: MutableList<Productos>, var ctx: Context) :
    RecyclerView.Adapter<ProductosAdapter.CharacterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lista_productos, parent, false)

        //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
        // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val producto = productosList[position]
        val realm = Realm.getDefaultInstance()
        var marca: String? = ""
        val tamaño = Math.toIntExact(realm.where(Marcas::class.java).count())

        if (tamaño == 0) {
            Log.d("marca", "null$tamaño")
            Toast.makeText(ctx, "La marca no se ha descargado", Toast.LENGTH_SHORT).show()
        } else {
            marca = realm.where(Marcas::class.java).equalTo("id", producto.brand_id)
                .findFirst()!!.name
        }


        //  String tipoProducto = realm.where(TipoProducto.class).equalTo("id", producto.getProduct_type_id()).findFirst().getName();
        val inventario =
            realm.where(Inventario::class.java).equalTo("product_id", producto.id).findFirst()
        precio = producto.sale_price.toDouble()
        val tipoProducto: String
        val impuesto = producto.iva

        tipoProducto = if (impuesto == 0.0) {
            "Exento"
        } else {
            "Gravado"
        }

        realm.close()

        holder.txt_producto_nombre.text = producto.description
        holder.txt_producto_codbarras.text = producto.barcode
        holder.txt_producto_marca.text = marca
        holder.txt_producto_tipo.text = tipoProducto
        holder.txt_producto_stock.text = producto.stock_max

        if (inventario == null) {
            holder.txt_producto_inventario.text = "0.0"
        } else {
            holder.txt_producto_inventario.text = inventario.amount
        }


        holder.txt_producto_precio.text =
            String.format("%,.2f", precio)
    }


    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return productosList.size
    }

    fun setFilter(countryModels: List<Productos>) {
        productosList = ArrayList()
        productosList.addAll(countryModels)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_producto_nombre: TextView =
            view.findViewById<View>(R.id.txt_producto_nombre) as TextView
        val txt_producto_codbarras: TextView =
            view.findViewById<View>(R.id.txt_producto_codbarras) as TextView
        val txt_producto_marca: TextView =
            view.findViewById<View>(R.id.txt_producto_marca) as TextView
        val txt_producto_tipo: TextView =
            view.findViewById<View>(R.id.txt_producto_tipo) as TextView
        val txt_producto_stock: TextView =
            view.findViewById<View>(R.id.txt_producto_stock) as TextView
        val txt_producto_inventario: TextView =
            view.findViewById<View>(R.id.txt_producto_inventario) as TextView
        val txt_producto_precio: TextView =
            view.findViewById<View>(R.id.txt_producto_precio) as TextView
        protected var cardView: CardView =
            view.findViewById<View>(R.id.cardViewProductos) as CardView
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private var precio = 0.0
    }
}

