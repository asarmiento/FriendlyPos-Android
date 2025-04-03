package com.friendlypos.distribucion.adapters

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.application.util.Functions.CreateMessage
import com.friendlypos.application.util.PrinterFunctions.imprimirProductosDistrSelecCliente
import com.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlypos.distribucion.modelo.Inventario
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.invoice
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.login.util.SessionPrefes
import com.friendlypos.principal.modelo.Clientes
import io.realm.Realm
import io.realm.RealmResults
import io.realm.internal.SyncObjectServerFacade
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class DistrClientesAdapter(
    context: Context?,
    private val activity: DistribucionActivity,
    var contentList: MutableList<sale>
) :
    RecyclerView.Adapter<DistrClientesAdapter.CharacterViewHolder>() {
    var clientesList: List<Clientes>? = null

    //private boolean isSelected = false;
    private var selected_position = -1
    var facturaid1: RealmResults<Pivot>? = null
    var idInvetarioSelec: Int = 0
    var amount_inventario: Double = 0.0
    var amount_dist_inventario: Double = 0.0
    var facturaID: String? = null
    var clienteID: String? = null
    var nextId: Int = 0
    var tabCliente: Int = 0
    var activa: Int = 0
    var nombreMetodoPago: String? = null
    var session: SessionPrefes

    init {
        QuickContext = context
        session = SessionPrefes(SyncObjectServerFacade.getApplicationContext())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_distribucion_clientes, parent, false)

        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val sale = contentList[position]

        val realm = Realm.getDefaultInstance()

        val clientes = realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
        val invoice = realm.where(invoice::class.java).equalTo("id", sale.invoice_id).findFirst()

        val cardCliente = clientes!!.card
        val companyCliente = clientes.companyName
        val fantasyCliente = clientes.fantasyName

        val numeracionFactura = invoice!!.numeration
        val nombreVenta = sale.customer_name
        val longitud = invoice.longitud
        val latitud = invoice.latitud

        holder.txt_cliente_factura_card.text = cardCliente
        if (fantasyCliente == "Cliente Generico") {
            holder.txt_cliente_factura_fantasyname.text = nombreVenta
        } else {
            holder.txt_cliente_factura_fantasyname.text = fantasyCliente
        }
        holder.txt_cliente_factura_companyname.text = companyCliente
        holder.txt_cliente_factura_numeracion.text = "Factura: $numeracionFactura"

        holder.cardView.setOnLongClickListener {
            val pos = position
            val clickedDataItem = contentList[pos]

            val realm6 = Realm.getDefaultInstance()
            val invoice1 =
                realm6.where<invoice>(invoice::class.java).equalTo("id", clickedDataItem.invoice_id)
                    .findFirst()
            val clientes = realm6.where(Clientes::class.java)
                .equalTo("id", clickedDataItem.customer_id).findFirst()
            realm6.close()

            val metodoPago = invoice1!!.payment_method_id
            val numeracionFactura1 = invoice1.numeration
            val creditoTime = clientes!!.creditTime.toInt()
            if (metodoPago == "1") {
                nombreMetodoPago = "Contado"
            } else if (metodoPago == "2") {
                nombreMetodoPago = "Crédito"
            }


            val layoutInflater = LayoutInflater.from(QuickContext)

            val promptView = layoutInflater.inflate(R.layout.promptclient, null)

            val alertDialogBuilder = AlertDialog.Builder(
                QuickContext!!
            )
            alertDialogBuilder.setView(promptView)
            val txtTipoFacturaEs =
                promptView.findViewById<View>(R.id.txtTipoFacturaEs) as TextView
            val rbcontado =
                promptView.findViewById<View>(R.id.contadoBill) as RadioButton
            val rbcredito =
                promptView.findViewById<View>(R.id.creditBill) as RadioButton
            val rgTipo = promptView.findViewById<View>(R.id.rgTipo) as RadioGroup

            txtTipoFacturaEs.text = "La factura #$numeracionFactura1 es de: $nombreMetodoPago"

            if (nombreMetodoPago == "Contado") {
                rgTipo.check(R.id.contadoBill)
            } else if (nombreMetodoPago == "Crédito") {
                rgTipo.check(R.id.creditBill)
            }

            alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(
                    "OK"
                ) { dialog, id ->
                    if (rbcredito.isChecked) {
                        if (creditoTime == 0) {
                            CreateMessage(
                                QuickContext!!,
                                " ",
                                "Este cliente no cuenta con crédito"
                            )
                        } else if (nombreMetodoPago == "Crédito") {
                            CreateMessage(
                                QuickContext!!,
                                " ",
                                "Esta factura ya es de crédito"
                            )
                        } else {
                            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CREDITO DE LA FACTURA
                            val realm2 = Realm.getDefaultInstance()
                            realm2.executeTransaction { realm2 ->
                                val factura_actualizado = realm2.where<invoice>(
                                    invoice::class.java
                                ).equalTo("id", sale.invoice_id).findFirst()
                                factura_actualizado!!.payment_method_id = "2".toString()
                                realm2.insertOrUpdate(factura_actualizado)
                                realm2.close()
                            }
                            CreateMessage(
                                QuickContext!!,
                                " ",
                                "Se cambio la factura a crédito"
                            )
                            notifyDataSetChanged()
                        }
                    }
                    if (nombreMetodoPago == "Contado" && rbcontado.isChecked) {
                        CreateMessage(
                            QuickContext!!,
                            " ",
                            "Esta factura ya es de contado"
                        )
                    } else if (rbcontado.isChecked) {
                        // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CONTADO DE LA FACTURA
                        val realm2 = Realm.getDefaultInstance()
                        realm2.executeTransaction { realm2 ->
                            val factura_actualizado = realm2.where<invoice>(
                                invoice::class.java
                            ).equalTo("id", sale.invoice_id).findFirst()
                            factura_actualizado!!.payment_method_id = "1".toString()
                            realm2.insertOrUpdate(factura_actualizado)
                            realm2.close()
                        }

                        CreateMessage(
                            QuickContext!!,
                            " ",
                            "Se cambio la factura a contado"
                        )
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, id -> dialog.cancel() }

            val alertD = alertDialogBuilder.create()
            alertD.show()
            true
        }
        holder.cardView.setOnClickListener(View.OnClickListener {
            activa = 1
            /*final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                               progresRing.setCancelable(true);*/
            val progresRing = ProgressDialog(QuickContext) /* = ProgressDialog.show(QuickContext, "Cargando",
                                                            "Seleccionando Cliente", true);*/
            val message = "Seleccionando Cliente"
            val titulo = "Cargando"
            val spannableString = SpannableString(message)
            val spannableStringTitulo = SpannableString(titulo)

            val typefaceSpan = CalligraphyTypefaceSpan(
                TypefaceUtils.load(
                    QuickContext!!.assets, "font/monse.otf"
                )
            )
            spannableString.setSpan(
                typefaceSpan,
                0,
                message.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length, Spanned.SPAN_PRIORITY)

            progresRing.setTitle(spannableStringTitulo)
            progresRing.setMessage(spannableString)
            progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progresRing.isIndeterminate = true
            progresRing.setCancelable(true)
            progresRing.show()

            Thread {
                try {
                    Thread.sleep(5000)
                } catch (e: Exception) {
                }
                progresRing.dismiss()
            }.start()

            val pos = position
            if (pos == RecyclerView.NO_POSITION) return@OnClickListener

            // Updating old as well as new positions
            notifyItemChanged(selected_position)
            selected_position = position
            notifyItemChanged(selected_position)

            val clickedDataItem = contentList[pos]


            facturaID = clickedDataItem.invoice_id
            clienteID = clickedDataItem.customer_id

            val realm = Realm.getDefaultInstance()
            val invoice =
                realm.where<invoice>(invoice::class.java).equalTo("id", facturaID).findFirst()
            val clientes = realm.where(Clientes::class.java).equalTo("id", clienteID).findFirst()
            facturaid1 = realm.where(Pivot::class.java).equalTo("invoice_id", facturaID).findAll()
            val metodoPago = invoice!!.payment_method_id
            val creditoLimiteCliente = clientes!!.creditLimit
            val dueCliente = clientes.due
            val feCliente = clientes.fe
            val tipoDoc = invoice.type
            realm.close()

            Log.d("PRODUCTOSFACTURATO", facturaid1.toString() + "")
            Log.d("metodoPago", metodoPago + "")
            Log.d("feCliente", feCliente + "")
            Log.d("tipoDoc", tipoDoc + "")
            tabCliente = 1
            activity.selecClienteTab = tabCliente
            activity.invoiceId = facturaID
            activity.metodoPagoCliente = metodoPago
            activity.creditoLimiteCliente = creditoLimiteCliente
            activity.dueCliente = dueCliente
        })


        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"))
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        holder.btnUbicacionFacturaCliente.setOnClickListener {
            if (activa == 1) {
                if (longitud != 0.0 && latitud != 0.0) {
                    try {
                        val url =
                            "https://waze.com/ul?ll=$latitud,$longitud&navigate=yes"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        QuickContext!!.startActivity(intent)
                    } catch (ex: ActivityNotFoundException) {
                        val gmmIntentUri = Uri.parse("geo:$latitud,$longitud")

                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        QuickContext!!.startActivity(mapIntent)
                    }
                } else {
                    Toast.makeText(
                        QuickContext,
                        "El cliente no cuenta con dirección GPS",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    QuickContext,
                    "Selecciona una factura primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        holder.btnDevolverFacturaCliente.setOnClickListener {
            if (activa == 1) {
                devolverFactura()
            } else {
                Toast.makeText(
                    QuickContext,
                    "Selecciona una factura primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        holder.btnImprimirFacturaCliente.setOnClickListener {
            if (activa == 1) {
                try {
                    imprimirProductosDistrSelecCliente(
                        sale,
                        QuickContext!!
                    )
                } catch (e: Exception) {
                    CreateMessage(
                        QuickContext!!,
                        "Error",
                        """
                            ${e.message}
                            ${e.stackTrace}
                            """.trimIndent()
                    )
                }
            } else {
                Toast.makeText(
                    QuickContext,
                    "Selecciona una factura primero",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    inner class CharacterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_cliente_factura_card: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_card) as TextView
        val txt_cliente_factura_fantasyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_fantasyname) as TextView
        val txt_cliente_factura_companyname: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_companyname) as TextView
        val txt_cliente_factura_numeracion: TextView =
            view.findViewById<View>(R.id.txt_cliente_factura_numeracion) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewDistrClientes) as CardView
        var btnDevolverFacturaCliente: Button =
            view.findViewById<View>(R.id.btnDevolverFacturaCliente) as Button
        var btnImprimirFacturaCliente: ImageButton =
            view.findViewById<View>(R.id.btnImprimirFacturaCliente) as ImageButton
        var btnUbicacionFacturaCliente: ImageButton =
            view.findViewById<View>(R.id.btnUbicacionFacturaCliente) as ImageButton
    }

    fun devolverFactura() {
        val dialogReturnSale = AlertDialog.Builder(
            QuickContext!!
        )
            .setTitle("Devolución")
            .setMessage("¿Desea proceder con la devolución de la factura?")
            .setPositiveButton("OK") { dialog, which ->
                tabCliente = 0
                activity.selecClienteTab = tabCliente

                Log.d("PRODUCTOSFACTURA1", facturaid1.toString() + "")

                for (i in facturaid1!!.indices) {
                    val eventRealm = facturaid1!![i]
                    val cantidadDevolver = eventRealm!!.amount.toDouble()

                    Log.d("PRODUCTOSFACTURASEPA1", eventRealm.toString() + "")
                    Log.d("PRODUCTOSFACTURASEPA", cantidadDevolver.toString() + "")

                    val resumenProductoId = eventRealm.id

                    // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                    val realm3 = Realm.getDefaultInstance()
                    realm3.executeTransaction { realm3 ->
                        val inventario = realm3.where(Inventario::class.java)
                            .equalTo("product_id", eventRealm.product_id).findFirst()
                        if (inventario != null) {
                            idInvetarioSelec = inventario.id
                            amount_inventario = inventario.amount.toDouble()
                            amount_dist_inventario = inventario.amount_dist.toDouble()
                            Log.d("idinventario", idInvetarioSelec.toString() + "")
                        } else {
                            amount_inventario = 0.0
                            // increment index
                            val currentIdNum = realm3.where(
                                Inventario::class.java
                            ).max("id")

                            nextId = if (currentIdNum == null) {
                                1
                            } else {
                                currentIdNum.toInt() + 1
                            }

                            val invnuevo = Inventario() // unmanaged
                            invnuevo.id = nextId
                            invnuevo.product_id = eventRealm.product_id
                            invnuevo.initial = "0".toString()
                            invnuevo.amount = cantidadDevolver.toString()
                            invnuevo.amount_dist = "0".toString()
                            invnuevo.distributor = "0".toString()

                            realm3.insertOrUpdate(invnuevo)
                            Log.d("idinvNUEVOCREADO", invnuevo.toString() + "")
                        }
                        realm3.close()
                    }

                    val realm5 = Realm.getDefaultInstance()
                    realm5.executeTransaction { realm5 ->
                        val inv_actualizado =
                            realm5.where(Pivot::class.java).equalTo("id", resumenProductoId)
                                .findFirst()
                        val dev = inv_actualizado!!.devuelvo
                        if (dev == 0) {
                            inv_actualizado.devuelvo = 1
                            realm5.insertOrUpdate(inv_actualizado)
                        } else {
                            Log.d("devuelto", "ya esta 1")
                        }
                        realm5.close()
                    }


                    // OBTENER NUEVO AMOUNT_DIST
                    val nuevoAmountDevuelto = cantidadDevolver + amount_inventario
                    Log.d("nuevoAmount", nuevoAmountDevuelto.toString() + "")

                    val nuevoAmountDistDevuelto = amount_dist_inventario - cantidadDevolver
                    Log.d("nuevoAmountDist", nuevoAmountDistDevuelto.toString() + "")
                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT_DIST EN EL INVENTARIO
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
                }
                // TRANSACCIÓN BD PARA BORRAR LA FACTURA
                val realm4 = Realm.getDefaultInstance()
                realm4.executeTransaction { realm4 ->
                    val inv_actualizado =
                        realm4.where(sale::class.java).equalTo("invoice_id", facturaID)
                            .findFirst()
                    inv_actualizado!!.devolucion = 1
                    realm4.insertOrUpdate(inv_actualizado)
                    Log.d("DevolucionSaleTotal", inv_actualizado.toString() + "")
                    realm4.close()
                }

                val realmInvoice = Realm.getDefaultInstance()
                realmInvoice.executeTransaction { realmInvoice ->
                    val inv_actualizado =
                        realmInvoice.where(invoice::class.java).equalTo("id", facturaID)
                            .findFirst()
                    inv_actualizado!!.devolucionInvoice = 1
                    realmInvoice.insertOrUpdate(inv_actualizado)
                    Log.d("DevolucionInvoiceTotal", inv_actualizado.toString() + "")
                    realmInvoice.close()
                }
                notifyDataSetChanged()
            }.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }.create()
        dialogReturnSale.show()
        session.guardarDatosBloquearBotonesDevolver(0)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    fun setFilter(countryModels: List<sale>) {
        contentList = ArrayList()
        contentList.addAll(countryModels)
        notifyDataSetChanged()
    }

    companion object {
        private var QuickContext: Context? = null
    }
}
