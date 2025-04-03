package com.friendlypos.distribucion.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
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
import com.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment
import com.friendlypos.distribucion.modelo.Inventario
import com.friendlypos.distribucion.modelo.Marcas
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.distribucion.util.TotalizeHelper
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.principal.modelo.Productos
import io.realm.Realm
import io.realm.RealmResults


/**
 * Created by DelvoM on 21/09/2017.
 */
class DistrSeleccionarProductosAdapter(
    private val activity: DistribucionActivity,
    private val fragment: DistSelecProductoFragment,
    var productosList: MutableList<Inventario>
) :
    RecyclerView.Adapter<DistrSeleccionarProductosAdapter.CharacterViewHolder>() {
    private var context: Context? = null
    private var selected_position = -1
    var totalCredito: Double = 0.0
    var totalizeHelper: TotalizeHelper

    var nextId: Int = 0

    init {
        totalizeHelper = TotalizeHelper(activity)
    }

    fun updateData(productosList: MutableList<Inventario>) {
        this.productosList = productosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_distribucion_productos, parent, false)
        context = parent.context
        return CharacterViewHolder(view)
    }


    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val pivots = listResumen
        val inventario = productosList[position]

        val realm = Realm.getDefaultInstance()
        val producto =
            realm.where(Productos::class.java).equalTo("id", inventario.product_id).findFirst()


        val description = producto!!.description
        val marca = producto.brand_id
        val tipo = producto.product_type_id
        val precio = producto.sale_price

        val status = producto.status

        val marca2 = realm.where(Marcas::class.java).equalTo("id", marca)
            .findFirst()!!
            .name
        // String tipoProducto = realm.where(TipoProducto.class).equalTo("id", tipo).findFirst().getName();
        val tipoProducto: String
        val impuesto = producto.iva

        tipoProducto = if (impuesto == 0.0) {
            "Exento"
        } else {
            "Gravado"
        }

        realm.close()

        if (status == "Activo") {
            holder.txt_producto_factura_nombre.text = description
            holder.txt_producto_factura_marca.text = "Marca: $marca2"

            holder.txt_producto_factura_tipo.text = "Tipo: $tipoProducto"
            holder.txt_producto_factura_precio.text = precio
            holder.txt_producto_factura_disponible.text = "Disp: " + inventario.amount
            holder.fillData(producto)

            for (pivot in pivots) {
                if (producto.id == pivot.product_id) {
                    Log.d("jd", "seteando color x lista")
                    holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
                    return
                } else {
                    Log.d("jd", "se limpia")
                    holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
            }
        } else {
            holder.cardView.visibility = View.GONE
            holder.cardView.layoutParams.height = 0
            val layoutParams =
                holder.cardView.layoutParams as MarginLayoutParams
            layoutParams.setMargins(0, 0, 0, 0)
            holder.cardView.requestLayout()
            Log.d("inactivo", "inactivo")
        }
    }


    fun addProduct(
        inventario_id: Int,
        producto_id: String,
        cantidadDisponible: Double,
        description: String?,
        Precio1: String,
        Precio2: String,
        Precio3: String,
        Precio4: String,
        Precio5: String
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
        label.text = "Escriba una cantidad maxima de $cantidadDisponible minima de 1"
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
                    (if (input.text.toString().isEmpty()) "0" else input.text.toString()).toDouble()
                Log.d(
                    "productoAmountDistAdd",
                    producto_amount_dist_add.toString() + ""
                )

                //  productoAmountDistAdd = String.format("%,.2f", producto_amount_dist_add);
                producto_descuento_add =
                    (if (desc.text.toString().isEmpty()) "0" else desc.text.toString()).toDouble()
                // productoDescuentoAdd = String.format("%,.2f", producto_descuento_add);
                Log.d(
                    "productoDescuentoAdd",
                    producto_descuento_add.toString() + ""
                )

                if (producto_descuento_add >= 0 && producto_descuento_add <= 10) {
                    if (producto_amount_dist_add > 0 && producto_amount_dist_add <= cantidadDisponible) {
                        val precioSeleccionado = spPrices.selectedItem as Double
                        Log.d("precioSeleccionado", precioSeleccionado.toString() + "")

                        //  CREDITO
                        val metodoPagoCliente = activity.metodoPagoCliente
                        val cred = activity.creditoLimiteCliente!!.toDouble()
                        if (metodoPagoCliente == "1") {
                            creditoLimiteCliente = cred
                            totalCredito =
                                creditoLimiteCliente
                            Log.d(
                                "ads",
                                creditoLimiteCliente.toString() + ""
                            )
                        } else if (metodoPagoCliente == "2") {
                            val totalProducSlecc =
                                precioSeleccionado * producto_amount_dist_add
                            creditoLimiteCliente = cred
                            totalCredito =
                                creditoLimiteCliente - totalProducSlecc
                            Log.d("ads", totalCredito.toString() + "")
                        }

                        // LIMITAR SEGUN EL LIMITE DEL CREDITO
                        if (totalCredito >= 0) {
                            val realm2 = Realm.getDefaultInstance()

                            realm2.executeTransaction { realm -> // increment index
                                val currentIdNum =
                                    realm.where(Pivot::class.java).max("id")

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
                                pivotnuevo.bonus = 0

                                realm2.insertOrUpdate(pivotnuevo) // using insert API
                                /*Pivot pivotnuevo = realm2.createObject(Pivot.class, nextId);
                                                               
                                                                                                        */
                            }

                            val nuevoAmount =
                                cantidadDisponible - producto_amount_dist_add
                            Log.d("nuevoAmount", nuevoAmount.toString() + "")


                            val realm3 = Realm.getDefaultInstance()

                            realm3.executeTransaction { realm3 ->
                                val inv_actualizado = realm3.where(
                                    Inventario::class.java
                                ).equalTo("id", inventario_id).findFirst()
                                inv_actualizado!!.amount = nuevoAmount.toString()

                                realm3.insertOrUpdate(inv_actualizado) // using insert API
                            }


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
                                    totalizeHelper = TotalizeHelper(activity)
                                    totalizeHelper.totalize(list)
                                    Log.d("listaResumenADD", list.toString() + "")
                                }
                            })


                            Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG)
                                .show()
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
                            "El producto no se agrego, verifique la cantidad que esta ingresando",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "El producto no se agrego, El descuento debe ser >0 <11",
                        Toast.LENGTH_LONG
                    ).show()
                }

                //      notifyDataSetChanged();
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

    fun setFilter(countryModels: List<Inventario>) {
        productosList = ArrayList()
        productosList.addAll(countryModels)
        notifyDataSetChanged()
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
        val txt_producto_factura_disponible: TextView =
            view.findViewById<View>(R.id.txt_producto_factura_disponible) as TextView
        private val txt_producto_factura_seleccionado =
            view.findViewById<View>(R.id.txt_producto_factura_seleccionado) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewSeleccionarProductos) as CardView

        fun fillData(producto: Productos) {
            cardView.setOnClickListener(View.OnClickListener {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@OnClickListener

                notifyItemChanged(selected_position)
                selected_position = adapterPosition
                notifyItemChanged(selected_position)

                val clickedDataItem = productosList[pos]
                val ProductoID = clickedDataItem.product_id
                val InventarioID = clickedDataItem.id

                val precio = producto.sale_price
                val precio2 = producto.sale_price2
                val precio3 = producto.sale_price3
                val precio4 = producto.sale_price4
                val precio5 = producto.sale_price5

                val ProductoAmount = clickedDataItem.amount.toDouble()

                val realm1 = Realm.getDefaultInstance()
                val producto =
                    realm1.where(Productos::class.java).equalTo("id", ProductoID)
                        .findFirst()


                val description = producto!!.description

                realm1.close()
                addProduct(
                    InventarioID,
                    ProductoID,
                    ProductoAmount,
                    description,
                    precio,
                    precio2,
                    precio3,
                    precio4,
                    precio5
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
    }
}

