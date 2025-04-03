package com.friendlypos.preventas.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.distribucion.modelo.Marcas
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.login.util.SessionPrefes
import com.friendlypos.preventas.activity.PreventaActivity
import com.friendlypos.preventas.fragment.PrevSelecProductoFragment
import com.friendlypos.preventas.modelo.Bonuses
import com.friendlypos.preventas.modelo.invoiceDetallePreventa
import com.friendlypos.preventas.util.TotalizeHelperPreventa
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.principal.modelo.Productos
import io.realm.Realm
import io.realm.internal.SyncObjectServerFacade
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrevSeleccionarProductoAdapter(
    private val activity: PreventaActivity,
    private val fragment: PrevSelecProductoFragment,
    var productosList: MutableList<Productos>
) :
    RecyclerView.Adapter<PrevSeleccionarProductoAdapter.CharacterViewHolder>(),
    Filterable {
    private var context: Context? = null
    var countryModels: MutableList<Productos> = ArrayList()
    var fechaExpiracionBonus: Date? = null
    private var selected_position = -1
    var totalCredito: Double = 0.0
    var totalizeHelper: TotalizeHelperPreventa
    var idDetallesFactura: Int = 0
    var nextId: Int = 0
    var customer: String? = null
    var session: SessionPrefes
    var spPrices: Spinner? = null
    var idProducto: String? = null
    var idFacturaSeleccionada: Int = 0

    private val mFilter: CustomFilter

    init {
        countryModels.addAll(productosList)
        session = SessionPrefes(SyncObjectServerFacade.getApplicationContext())
        totalizeHelper = TotalizeHelperPreventa(activity)
        this.mFilter = CustomFilter(this@PrevSeleccionarProductoAdapter)
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    fun updateData(productosList: MutableList<Productos>) {
        this.productosList = productosList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_preventa_productos, parent, false)
        context = parent.context
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val pivots = activity.allPivotDelegate
        val producto = productosList[position]

        val realm = Realm.getDefaultInstance()

        // Productos producto = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst();
        val description = producto.description
        val marca = producto.brand_id
        val tipo = producto.product_type_id
        val precio = producto.sale_price

        val marca2 = realm.where(Marcas::class.java).equalTo("id", marca)
            .findFirst()!!.name
        //String tipoProducto = realm.where(TipoProducto.class).equalTo("id", tipo).findFirst().getName();
        val tipoProducto: String
        val impuesto = producto.iva

        tipoProducto = if (impuesto == 0.0) {
            "Exento"
        } else {
            "Gravado"
        }

        holder.txt_producto_factura_nombre.text = description
        holder.txt_producto_factura_marca.text = "Marca: $marca2"
        holder.txt_producto_factura_tipo.text = "Tipo: $tipoProducto"
        holder.txt_producto_factura_precio.text = precio
        holder.fillData(producto)
        Log.d("prodListActivoPrev", "" + productosList)
        if (pivots.size == 0) {
            Log.d("jd", "se limpia x 0")
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        } else {
            for (pivot in pivots) {
                /*  for (int i = 0; i <= pivots.size(); i++){*/
                if (producto.id == pivot.product_id) {
                    Log.d("jd", "seteando color x lista")
                    holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
                    return
                } else {
                    Log.d("jd", "se limpia")
                    holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
            }
        }
        realm.close()
    }

    //}
    fun addProduct(
        producto_id: String, description: String?, Precio1: String, Precio2: String,
        Precio3: String, Precio4: String, Precio5: String, bonusProducto: String
    ) {
        val invoiceDetallePreventa = activity.currentInvoice


        idDetallesFactura = invoiceDetallePreventa.p_id
        Log.d("FACTURAIDDELEG", idDetallesFactura.toString() + "")

        // invoiceDetallePreventa.setP_code(weqweq);
        idFacturaSeleccionada = (activity).invoiceIdPreventa
        Log.d("idFacturaSeleccionada", idFacturaSeleccionada.toString() + "")
        idProducto = producto_id
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
        // label.setText("Escriba una cantidad maxima de " + cantidadDisponible + " minima de 1");
        label.text = "Escriba la cantidad requerida del producto"

        val txtBonificacion = promptView.findViewById<View>(R.id.txtBonificacion) as TextView

        if (bonusProducto == "1") {
            boni
            val realmBonus = Realm.getDefaultInstance()

            realmBonus.executeTransaction { realmBonus ->
                val productoConBonus = realmBonus.where(Bonuses::class.java)
                    .equalTo("product_id", producto_id.toInt()).findFirst()
                productosParaObtenerBonus =
                    productoConBonus!!.product_sale.toDouble()
                productosDelBonus =
                    productoConBonus.product_bonus.toDouble()
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

        spPrices = promptView.findViewById<View>(R.id.spPrices) as Spinner
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
        spPrices!!.adapter = pricesAdapter
        spPrices!!.setSelection(0)

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "OK"
        ) { dialog, id ->
            try {
                // TODO obtiene la cantidad del producto

                producto_amount_dist_add =
                    (if (input.text.toString()
                            .isEmpty()
                    ) "0" else input.text.toString()).toDouble()

                // TODO obtiene el descuento del producto
                producto_descuento_add =
                    (if (desc.text.toString()
                            .isEmpty()
                    ) "0" else desc.text.toString()).toDouble()


                if (producto_descuento_add >= 0 && producto_descuento_add <= 10) {
                    if (producto_amount_dist_add > 0) {
                        if (bonusProducto == "1") {
                            Log.d("idProductoBONIF", producto_id + "")

                            val fechaexp = fechaExpiracionBonus!!.time
                            Log.d("fechaExpBONIF", fechaexp.toString() + "")

                            val cal = Calendar.getInstance()
                            val hoy = cal.timeInMillis
                            Log.d("fechaBONIF", hoy.toString() + "")


                            if (producto_amount_dist_add >= productosParaObtenerBonus) {
                                if (hoy <= fechaexp) {
                                    Log.d(
                                        "PRODOBTE",
                                        productosParaObtenerBonus.toString() + ""
                                    )
                                    Log.d(
                                        "PRODDELBO",
                                        productosDelBonus.toString() + ""
                                    )
                                    Log.d(
                                        "PRODADD",
                                        producto_amount_dist_add.toString() + ""
                                    )

                                    val productos =
                                        producto_amount_dist_add / productosParaObtenerBonus

                                    val prod = String.format("%.0f", productos)
                                    val productoBonusTotal =
                                        prod.toDouble() * productosDelBonus


                                    Log.d("PROD DIV", productos.toString() + "")
                                    Log.d(
                                        "PROD TOTAL",
                                        productoBonusTotal.toString() + ""
                                    )

                                    producto_bonus_add =
                                        producto_amount_dist_add + productoBonusTotal
                                    Log.d(
                                        "PRODUCTODELBONUS",
                                        producto_bonus_add.toString() + ""
                                    )
                                    agregarBonificacion()
                                    Toast.makeText(
                                        context,
                                        "Se realizó una bonificación de $productoBonusTotal productos",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Fecha expirada para el bonus",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    agregar()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "No alcanza la cantidad deseada para el bonus",
                                    Toast.LENGTH_LONG
                                ).show()
                                agregar()
                            }
                        } else {
                            agregar()
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
                notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, id -> dialog.cancel() }

        val alertD = alertDialogBuilder.create()
        alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertD.show()
    }

    fun agregar() {
        val precioSeleccionado = spPrices!!.selectedItem as Double
        Log.d("precioSeleccionado", precioSeleccionado.toString() + "")

        //  CREDITO
        val metodoPagoCliente = invoiceDetallePreventa.getP_payment_method_id()
        val cred = activity.creditoLimiteClientePreventa!!.toDouble()
        if (metodoPagoCliente == "1") {
            creditoLimiteCliente = cred
            totalCredito = creditoLimiteCliente
            Log.d("ads", creditoLimiteCliente.toString() + "")
        } else if (metodoPagoCliente == "2") {
            val totalProducSlecc = precioSeleccionado * producto_amount_dist_add
            creditoLimiteCliente = cred
            totalCredito = creditoLimiteCliente - totalProducSlecc
            Log.d("ads", totalCredito.toString() + "")
        }

        // LIMITAR SEGUN EL LIMITE DEL CREDITO
        if (totalCredito >= 0) {
            val numero = session.datosPivotPreventa
            // increment index
            val currentIdNum1: Number = numero

            nextId = if (currentIdNum1 == null) {
                1
            } else {
                currentIdNum1.toInt() + 1
            }


            val pivotnuevo = Pivot() // unmanaged
            pivotnuevo.id = nextId
            pivotnuevo.invoice_id = idDetallesFactura.toString()
            pivotnuevo.product_id = idProducto
            pivotnuevo.price = precioSeleccionado.toString()
            pivotnuevo.amount = producto_amount_dist_add.toString()
            pivotnuevo.discount = producto_descuento_add.toString()
            pivotnuevo.delivered = producto_amount_dist_add.toString()
            pivotnuevo.devuelvo = 0
            pivotnuevo.bonus = 0
            pivotnuevo.amountSinBonus = 0.0

            activity.insertProduct(pivotnuevo)
            //   numero++;
            session.guardarDatosPivotPreventa(nextId)


            val ventaDetallePreventa = activity.currentVenta
            ventaDetallePreventa.invoice_id

            if (ventaDetallePreventa.invoice_id == idFacturaSeleccionada.toString()) {
                customer = ventaDetallePreventa.customer_id
            }

            // TRANSACCION PARA ACTUALIZAR EL CREDIT_LIMIT DEL CLIENTE
            val realm4 = Realm.getDefaultInstance()
            realm4.executeTransaction { realm4 -> //final sale ventas = realm4.where(sale.class).equalTo("invoice_id", idFacturaSeleccionada).findFirst();
                val clientes =
                    realm4.where(Clientes::class.java).equalTo("id", customer).findFirst()
                Log.d("ads", clientes.toString() + "")
                clientes!!.creditLimit = totalCredito.toString()

                realm4.insertOrUpdate(clientes) // using insert API

                realm4.close()
                activity.creditoLimiteClientePreventa = totalCredito.toString()

                fragment.updateData()
                val list = activity.allPivotDelegate
                activity.cleanTotalize()
                totalizeHelper = TotalizeHelperPreventa(activity)
                totalizeHelper.totalize(list)
                Log.d("listaResumenADD", list.toString() + "")
            }
            Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show()
        }
    }

    fun agregarBonificacion() {
        session.guardarDatosBonus(1)
        val precioSeleccionado = spPrices!!.selectedItem as Double
        Log.d("precioSeleccionado", precioSeleccionado.toString() + "")

        //  CREDITO
        val metodoPagoCliente = invoiceDetallePreventa.getP_payment_method_id()
        val cred = activity.creditoLimiteClientePreventa!!.toDouble()

        if (metodoPagoCliente == "1") {
            creditoLimiteCliente = cred
            totalCredito = creditoLimiteCliente
            Log.d("ads", creditoLimiteCliente.toString() + "")
        } else if (metodoPagoCliente == "2") {
            val totalProducSlecc = precioSeleccionado * producto_amount_dist_add
            creditoLimiteCliente = cred
            totalCredito = creditoLimiteCliente - totalProducSlecc
            Log.d("ads", totalCredito.toString() + "")
        }

        // LIMITAR SEGUN EL LIMITE DEL CREDITO
        if (totalCredito >= 0) {
            val numero = session.datosPivotPreventa
            // increment index
            val currentIdNum1: Number = numero

            nextId = if (currentIdNum1 == null) {
                1
            } else {
                currentIdNum1.toInt() + 1
            }


            val pivotnuevo = Pivot() // unmanaged
            pivotnuevo.id = nextId
            pivotnuevo.invoice_id = idDetallesFactura.toString()
            pivotnuevo.product_id = idProducto
            pivotnuevo.price = precioSeleccionado.toString()
            pivotnuevo.amount = producto_bonus_add.toString()
            pivotnuevo.discount = producto_descuento_add.toString()
            pivotnuevo.delivered = producto_bonus_add.toString()
            pivotnuevo.devuelvo = 0
            pivotnuevo.bonus = 1
            pivotnuevo.amountSinBonus = producto_amount_dist_add

            activity.insertProduct(pivotnuevo)

            session.guardarDatosPivotPreventa(nextId)

            val ventaDetallePreventa = activity.currentVenta
            ventaDetallePreventa.invoice_id

            if (ventaDetallePreventa.invoice_id == idFacturaSeleccionada.toString()) {
                customer = ventaDetallePreventa.customer_id
            }

            // TRANSACCION PARA ACTUALIZAR EL CREDIT_LIMIT DEL CLIENTE
            val realm4 = Realm.getDefaultInstance()
            realm4.executeTransaction { realm4 -> //final sale ventas = realm4.where(sale.class).equalTo("invoice_id", idFacturaSeleccionada).findFirst();
                val clientes =
                    realm4.where(Clientes::class.java).equalTo("id", customer).findFirst()
                Log.d("ads", clientes.toString() + "")
                clientes!!.creditLimit = totalCredito.toString()

                realm4.insertOrUpdate(clientes) // using insert API

                realm4.close()
                activity.creditoLimiteClientePreventa = totalCredito.toString()

                fragment.updateData()
                val list = activity.allPivotDelegate
                activity.cleanTotalize()
                totalizeHelper = TotalizeHelperPreventa(activity)
                totalizeHelper.totalize(list)
                Log.d("listaResumenADD", list.toString() + "")
            }
            Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show()
        }
    }

    private val boni: List<Bonuses>
        get() {
            val realm = Realm.getDefaultInstance()
            val query = realm.where(Bonuses::class.java)
            val result1 = query.findAll()
            Log.d("BONIFICACION", result1.toString() + "")
            return result1
        }


    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return productosList.size
    }


    /*Filtro*/
    inner class CustomFilter internal constructor(private val listAdapter: PrevSeleccionarProductoAdapter) :
        Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            countryModels.clear()
            val results = FilterResults()
            if (constraint.length == 0) {
                countryModels.addAll(productosList)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (person in productosList) {
                    if (person.description.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        countryModels.add(person)
                    }
                }
            }
            results.values = countryModels
            results.count = countryModels.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            listAdapter.notifyDataSetChanged()
        }
    }

    fun setFilter(countryModels: List<Productos>) {
        productosList = ArrayList()
        productosList.addAll(countryModels)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_producto_factura_nombre: TextView =
            view.findViewById<View>(R.id.txt_prev_producto_factura_nombre) as TextView
        val txt_producto_factura_marca: TextView =
            view.findViewById<View>(R.id.txt_prev_producto_factura_marca) as TextView
        val txt_producto_factura_tipo: TextView =
            view.findViewById<View>(R.id.txt_prev_producto_factura_tipo) as TextView
        val txt_producto_factura_precio: TextView =
            view.findViewById<View>(R.id.txt_prev_producto_factura_precio) as TextView
        private val txt_producto_factura_disponible: TextView? = null

        /* txt_producto_factura_disponible = (TextView) view.findViewById(R.id.txt_prev_producto_factura_disponible);*/
        private val txt_producto_factura_seleccionado =
            view.findViewById<View>(R.id.txt_prev_producto_factura_seleccionado) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewSeleccionarProductosPreventa) as CardView

        fun fillData(producto: Productos) {
            cardView.setOnClickListener(View.OnClickListener {
                session.guardarDatosBonus(0)
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) return@OnClickListener

                notifyItemChanged(selected_position)
                selected_position = adapterPosition
                notifyItemChanged(selected_position)

                val clickedDataItem = productosList[pos]
                val ProductoID = clickedDataItem.id

                // int InventarioID = clickedDataItem.getId();
                val precio = producto.sale_price
                val precio2 = producto.sale_price2
                val precio3 = producto.sale_price3
                val precio4 = producto.sale_price4
                val precio5 = producto.sale_price5
                val bonusProducto = producto.bonus

                /*  Double ProductoAmount = Double.valueOf(clickedDataItem.getAmount());*/
                val realm1 = Realm.getDefaultInstance()
                val producto =
                    realm1.where(Productos::class.java).equalTo("id", ProductoID)
                        .findFirst()

                val description = producto!!.description

                realm1.close()
                addProduct(
                    ProductoID,
                    description,
                    precio,
                    precio2,
                    precio3,
                    precio4,
                    precio5,
                    bonusProducto
                )
            })
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private var producto_amount_dist_add = 0.0
        private var producto_bonus_add = 0.0

        private var producto_descuento_add = 0.0
        private var productosParaObtenerBonus = 0.0
        private var productosDelBonus = 0.0
        var creditoLimiteCliente: Double = 0.0
    }
}

