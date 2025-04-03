package com.friendlypos.reimpresion_pedidos.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.modelo.Marcas
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.TipoProducto
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.preventas.modelo.Bonuses
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.principal.modelo.Productos
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity
import com.friendlypos.reimpresion_pedidos.fragment.ReimPedidoSelecProductoFragment
import com.friendlypos.reimpresion_pedidos.util.TotalizeHelperReimPedido
import io.realm.Realm
import io.realm.RealmResults
import java.util.Date


/**
 * Created by DelvoM on 21/09/2017.
 */
class ReimPedidoSeleccionarProductosAdapter(
    private val activity: ReimprimirPedidosActivity,
    private val fragment: ReimPedidoSelecProductoFragment,
    var productosList: MutableList<Productos>
) :
    RecyclerView.Adapter<ReimPedidoSeleccionarProductosAdapter.CharacterViewHolder>() {
    private var context: Context? = null
    var fechaExpiracionBonus: Date? = null
    private var selected_position = -1
    var totalCredito: Double = 0.0
    var totalizeHelper: TotalizeHelperReimPedido
    var nextId: Int = 0

    init {
        totalizeHelper = TotalizeHelperReimPedido(activity)
    }

    fun updateData(productosList: MutableList<Productos>) {
        this.productosList = productosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_reimpedido_productos, parent, false)
        context = parent.context
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val producto = productosList[position]

        val realm = Realm.getDefaultInstance()


        //  Productos producto = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst();
        val description = producto.description
        val marca = producto.brand_id
        val tipo = producto.product_type_id
        val precio = producto.sale_price

        val marca2 = realm.where(Marcas::class.java).equalTo("id", marca)
            .findFirst()!!.name
        val tipoProducto = realm.where(TipoProducto::class.java)
            .equalTo("id", tipo).findFirst()!!.name

        realm.close()



        holder.txt_producto_factura_nombre.text = description
        holder.txt_producto_factura_marca.text = "Marca: $marca2"
        holder.txt_producto_factura_tipo.text = "Tipo: $tipoProducto"
        holder.txt_producto_factura_precio.text = precio
        holder.cardView.setBackgroundColor(
            if (selected_position == position) Color.parseColor("#d1d3d4") else Color.parseColor(
                "#FFFFFF"
            )
        )
        holder.fillData(producto)
    }


    //}
    fun addProduct(
        producto_id: String, description: String?, Precio1: String, Precio2: String,
        Precio3: String, Precio4: String, Precio5: String, bonusProducto: String
    ) {
        val idFacturaSeleccionada = (activity).invoiceId
        Log.d("idFacturaSeleccionada", idFacturaSeleccionada + "")
        Log.d("idProductoSeleccionado", producto_id + "")


        val layoutInflater = LayoutInflater.from(context)
        val promptView = layoutInflater.inflate(R.layout.promptamount, null)

        val alertDialogBuilder = AlertDialog.Builder(
            context!!
        )
        alertDialogBuilder.setView(promptView)

        val txtNombreProducto = promptView.findViewById<View>(R.id.txtNombreProducto) as TextView
        txtNombreProducto.text = description

        val label = promptView.findViewById<View>(R.id.promtClabel) as TextView
        label.text = "Escriba la cantidad requerida del producto"

        val txtBonificacion = promptView.findViewById<View>(R.id.txtBonificacion) as TextView

        if (bonusProducto == "1") {
            boni
            val realmBonus = Realm.getDefaultInstance()

            realmBonus.executeTransaction { realmBonus ->
                val productoConBonus = realmBonus.where(Bonuses::class.java)
                    .equalTo("product_id", producto_id.toInt()).findFirst()
                productosParaObtenerBonus =
                    productoConBonus!!.product_sale!!.toDouble()
                productosDelBonus =
                    productoConBonus.product_bonus!!.toDouble()
                fechaExpiracionBonus = productoConBonus.expiration
                Log.d(
                    "BONIF",
                    productoConBonus.product_id.toString() + " " + productosParaObtenerBonus + " " + productosDelBonus + " " + fechaExpiracionBonus.toString()
                )
            }

            txtBonificacion.visibility = View.VISIBLE
            txtBonificacion.text =
                " La cantidad para bonificarle es de: " + productosParaObtenerBonus
        }

        val input = promptView.findViewById<View>(R.id.promtCtext) as EditText
        val desc = promptView.findViewById<View>(R.id.promtCDesc) as EditText


        val spPrices = promptView.findViewById<View>(R.id.spPrices) as Spinner
        val pricesList = ArrayList<Double>()
        val precio1 = Precio1.toDouble()
        val precio2 = Precio2.toDouble()
        val precio3 = Precio3.toDouble()
        val precio4 = Precio4.toDouble()
        val precio5 = Precio5.toDouble()

        pricesList.add(precio1)

        if (precio2 != 0.0) pricesList.add(precio2)

        if (precio3 != 0.0) pricesList.add(precio3)

        if (precio4 != 0.0) pricesList.add(precio4)

        if (precio5 != 0.0) pricesList.add(precio5)

        val pricesAdapter =
            ArrayAdapter(label.context, android.R.layout.simple_spinner_item, pricesList)
        spPrices.adapter = pricesAdapter
        spPrices.setSelection(0)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { dialog, id ->
            try {
                producto_amount_dist_add =
                    (if (input.text.toString()
                            .isEmpty()
                    ) "0" else input.text.toString()).toDouble()
                producto_descuento_add =
                    (if (desc.text.toString()
                            .isEmpty()
                    ) "0" else desc.text.toString()).toDouble()


                if (producto_descuento_add >= 0 && producto_descuento_add <= 10) {
                    val precioSeleccionado = spPrices.selectedItem as Double
                    Log.d("precioSeleccionado", precioSeleccionado.toString() + "")

                    //  CREDITO
                    val metodoPagoCliente = activity.metodoPagoCliente
                    val cred = activity.creditoLimiteCliente!!.toDouble()
                    if (metodoPagoCliente == "1") {
                        creditoLimiteCliente =
                            cred
                        totalCredito =
                            creditoLimiteCliente
                        Log.d(
                            "ads",
                            creditoLimiteCliente.toString() + ""
                        )
                    } else if (metodoPagoCliente == "2") {
                        val totalProducSlecc =
                            precioSeleccionado * producto_amount_dist_add
                        creditoLimiteCliente =
                            cred
                        totalCredito =
                            creditoLimiteCliente - totalProducSlecc
                        Log.d("ads", totalCredito.toString() + "")
                    }

                    // LIMITAR SEGUN EL LIMITE DEL CREDITO
                    if (totalCredito >= 0) {
                        val realm2 = Realm.getDefaultInstance()

                        realm2.executeTransaction { realm -> // increment index
                            val currentIdNum = realm.where(Pivot::class.java).max("id")

                            nextId = if (currentIdNum == null) {
                                1
                            } else {
                                currentIdNum.toInt() + 1
                            }

                            val pivotnuevo = Pivot() // unmanaged
                            pivotnuevo.id = nextId
                            pivotnuevo.invoice_id = idFacturaSeleccionada
                            pivotnuevo.product_id = producto_id
                            pivotnuevo.price = precioSeleccionado.toString()
                            pivotnuevo.amount =
                                producto_amount_dist_add.toString()
                            pivotnuevo.discount =
                                producto_descuento_add.toString()
                            pivotnuevo.delivered =
                                producto_amount_dist_add.toString()

                            realm2.insertOrUpdate(pivotnuevo) // using insert API
                            /*Pivot pivotnuevo = realm2.createObject(Pivot.class, nextId);
                                                       
                                                                                           */
                        }

                        /*   final Double nuevoAmount = cantidadDisponible - producto_amount_dist_add;
       Log.d("nuevoAmount", nuevoAmount + "");
    
    
       final Realm realm3 = Realm.getDefaultInstance();
    
       realm3.executeTransaction(new Realm.Transaction() {
    
           @Override
           public void execute(Realm realm3) {
    
               Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("id", inventario_id).findFirst();
               inv_actualizado.setAmount(String.valueOf(nuevoAmount));
    
               realm3.insertOrUpdate(inv_actualizado); // using insert API
           }
       });
    
    */
                        // TRANSACCION PARA ACTUALIZAR EL CREDIT_LIMIT DEL CLIENTE
                        val realm4 = Realm.getDefaultInstance()
                        realm4.executeTransaction(object : Realm.Transaction {
                            override fun execute(realm4: Realm) {
                                val ventas = realm4.where(sale::class.java)
                                    .equalTo("invoice_id", idFacturaSeleccionada).findFirst()
                                val clientes = realm4.where(Clientes::class.java)
                                    .equalTo("id", ventas!!.customer_id).findFirst()
                                Log.d("ads", clientes.toString() + "")
                                clientes!!.creditLimit = totalCredito.toString()

                                realm4.insertOrUpdate(clientes) // using insert API

                                realm4.close()
                                activity.creditoLimiteCliente = totalCredito.toString()

                                fragment.updateData()
                                val list: List<Pivot> = this.listResumen
                                activity.cleanTotalize()
                                totalizeHelper = TotalizeHelperReimPedido(activity)
                                totalizeHelper.totalize(list)
                                Log.d("listaResumenADD", list.toString() + "")
                            }
                        })


                        Toast.makeText(
                            context,
                            nextId.toString() + "agregocanti ",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Has excedido el monto del crédito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "El producto no se agrego, El descuento debe ser >0 <11",
                        Toast.LENGTH_LONG
                    ).show()
                }
                notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
                /* Functions.createSnackBar(QuickContext, coordinatorLayout, "Sucedio un error Revise que el producto y sus dependientes tengan existencias", 2, Snackbar.LENGTH_LONG);
                            Functions.CreateMessage(QuickContext, "Error", e.getMessage());*/
            }
        }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, id -> dialog.cancel() }

        val alertD = alertDialogBuilder.create()
        alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertD.show()
    }

    val listResumen: List<Pivot>
        get() {
            val facturaId = activity.invoiceId
            val realm = Realm.getDefaultInstance()
            val facturaid1: RealmResults<Pivot> =
                realm.where(Pivot::class.java).equalTo("invoice_id", facturaId)
                    .equalTo("devuelvo", 0).findAll()
            realm.close()
            return facturaid1
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

    private val boni: List<Bonuses>
        get() {
            val realm = Realm.getDefaultInstance()
            val query = realm.where(Bonuses::class.java)
            val result1 = query.findAll()
            Log.d("BONIFICACION", result1.toString() + "")
            return result1
        }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_producto_factura_nombre: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_nombre) as TextView
        val txt_producto_factura_marca: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_marca) as TextView
        val txt_producto_factura_tipo: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_tipo) as TextView
        val txt_producto_factura_precio: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_precio) as TextView
        private val txt_producto_factura_disponible =
            view.findViewById<View>(R.id.txt_producto_factura_disponible) as TextView
        private val txt_producto_factura_seleccionado =
            view.findViewById<View>(R.id.txt_producto_factura_seleccionado) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewSeleccionarProductos) as CardView

        fun fillData(producto: Productos) {
            cardView.setOnClickListener(View.OnClickListener { view ->
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@OnClickListener

                notifyItemChanged(selected_position)
                selected_position = adapterPosition
                notifyItemChanged(selected_position)

                val clickedDataItem = productosList[pos]
                val ProductoID = clickedDataItem.id

                val precio = producto.sale_price
                val precio2 = producto.sale_price2
                val precio3 = producto.sale_price3
                val precio4 = producto.sale_price4
                val precio5 = producto.sale_price5
                val bonusProducto = producto.bonus

                //   Double ProductoAmount = Double.valueOf(clickedDataItem.getAmount());
                val realm1 = Realm.getDefaultInstance()
                val producto =
                    realm1.where(Productos::class.java).equalTo("id", ProductoID)
                        .findFirst()


                val description = producto!!.description

                realm1.close()

                Toast.makeText(view.context, "You clicked $ProductoID", Toast.LENGTH_SHORT).show()
                addProduct(
                    ProductoID!!, description,
                    precio!!, precio2!!, precio3!!, precio4!!, precio5!!, bonusProducto!!
                )
            })
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private var producto_amount_dist_add = 0.0
        private var producto_descuento_add = 0.0
        var creditoLimiteCliente: Double = 0.0
        private var productosParaObtenerBonus = 0.0
        private var productosDelBonus = 0.0
    }
}

