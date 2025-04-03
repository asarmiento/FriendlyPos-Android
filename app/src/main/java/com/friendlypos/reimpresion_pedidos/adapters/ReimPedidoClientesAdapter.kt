package com.friendlypos.reimpresion_pedidos.adapters

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
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.friendlypos.R
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver
import com.friendlypos.application.util.Functions.CreateMessage
import com.friendlypos.application.util.PrinterFunctions.imprimirFacturaPrevTotal
import com.friendlypos.application.util.PrinterFunctions.imprimirFacturaProformaTotal
import com.friendlypos.application.util.PrinterFunctions.imprimirProductosDistrSelecCliente
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.invoice
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity
import io.realm.Realm
import io.realm.RealmResults
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class ReimPedidoClientesAdapter(
    context: Context?,
    private val activity: ReimprimirPedidosActivity,
    var contentList: List<sale>
) :
    RecyclerView.Adapter<ReimPedidoClientesAdapter.CharacterViewHolder>() {
    //private boolean isSelected = false;
    private var selected_position = -1
    private val selected_position1 = -1
    var facturaid1: RealmResults<Pivot>? = null
    var idInvetarioSelec: Int = 0
    var amount_inventario: Double = 0.0
    var facturaID: String? = null
    var clienteID: String? = null
    var nextId: Int = 0
    var tabCliente: Int = 0
    var activa: Int = 0
    var activaSoloImprimir: Int = 0
    var nombreMetodoPago: String? = null
    var subida: Int = 0
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    var sale_actualizada: sale? = null
    var tipoFacturacionImpr: String? = null

    init {
        QuickContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_reimpedido_clientes, parent, false)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver!!.setBluetoothStateChangeReceiver(QuickContext!!)
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
        subida = invoice.subida
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

        if (subida == 1) {
            holder.txtSubida.setBackgroundColor(Color.parseColor("#FF0000"))
        } else {
            holder.txtSubida.setBackgroundColor(Color.parseColor("#607d8b"))
        }

        holder.cardView.setOnLongClickListener {
            val pos = position
            val clickedDataItem = contentList[pos]
            val subida1 = clickedDataItem.subida
            if (subida1 == 1) {
                val realm6 = Realm.getDefaultInstance()
                val invoice1 = realm6.where<invoice>(invoice::class.java)
                    .equalTo("id", clickedDataItem.invoice_id).findFirst()
                val clientes = realm6.where(Clientes::class.java)
                    .equalTo("id", clickedDataItem.customer_id).findFirst()
                realm6.close()

                val metodoPago = invoice1!!.payment_method_id
                val numeracionFactura1 = invoice1.numeration
                val creditoTime = clientes!!.creditTime!!.toInt()
                if (metodoPago == "1") {
                    nombreMetodoPago = "Contado"
                } else if (metodoPago == "2") {
                    nombreMetodoPago = "Crédito"
                }


                val layoutInflater =
                    LayoutInflater.from(QuickContext)

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
            }
            true
        }



        holder.cardView.setOnClickListener(View.OnClickListener {
            val pos = position
            if (pos == RecyclerView.NO_POSITION) return@OnClickListener

            // Updating old as well as new positions
            notifyItemChanged(selected_position)
            selected_position = position
            notifyItemChanged(selected_position)

            val clickedDataItem = contentList[pos]
            val subida1 = clickedDataItem.subida
            if (subida1 == 1) {
                activa = 1
                activaSoloImprimir = 0


                /*  final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
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




                facturaID = clickedDataItem.invoice_id
                clienteID = clickedDataItem.customer_id

                val realm = Realm.getDefaultInstance()
                val invoice =
                    realm.where<invoice>(invoice::class.java).equalTo("id", facturaID).findFirst()
                val clientes =
                    realm.where(Clientes::class.java).equalTo("id", clienteID).findFirst()
                //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());
                facturaid1 =
                    realm.where(Pivot::class.java).equalTo("invoice_id", facturaID).findAll()
                val metodoPago = invoice!!.payment_method_id
                val creditoLimiteCliente = clientes!!.creditLimit
                val dueCliente = clientes.due
                realm.close()

                Log.d("PRODUCTOSFACTURATO", facturaid1.toString() + "")
                Log.d("metodoPago", metodoPago + "")
                tabCliente = 1
                activity.selecClienteTab = tabCliente
                activity.invoiceId = facturaID
                activity.metodoPagoCliente = metodoPago
                activity.creditoLimiteCliente = creditoLimiteCliente
                activity.dueCliente = dueCliente
            } else if (subida1 == 0) {
                activa = 1
                activaSoloImprimir = 1


                /*   final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente Impresion", true);
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

                facturaID = clickedDataItem.invoice_id
                clienteID = clickedDataItem.customer_id

                val realm = Realm.getDefaultInstance()

                facturaid1 =
                    realm.where(Pivot::class.java).equalTo("invoice_id", facturaID).findAll()
                realm.close()

                Log.d("PRODUCTOSIMPRIMIR", facturaid1.toString() + "")


                tabCliente = 0
                activity.selecClienteTab = tabCliente
            }
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

        holder.btnImprimirFacturaCliente.setOnClickListener {
            if (activa == 1 && activaSoloImprimir == 0) {
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
            }
            if (activa == 1 && activaSoloImprimir == 1) {
                try {
                    if (bluetoothStateChangeReceiver!!.isBluetoothAvailable == true) {
                        val realm3 = Realm.getDefaultInstance()
                        realm3.executeTransaction { realm3 ->
                            sale_actualizada = realm3.where<sale>(sale::class.java)
                                .equalTo("invoice_id", facturaID).findFirst()
                            Log.d("ENVIADOSALE", sale_actualizada.toString() + "")
                        }

                        tipoFacturacionImpr = sale_actualizada!!.facturaDePreventa

                        if (tipoFacturacionImpr == "Preventa") {
                            imprimirFacturaPrevTotal(
                                sale_actualizada,
                                QuickContext!!, 1
                            )
                            Toast.makeText(
                                QuickContext,
                                "imprimir Totalizar Preventa",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (tipoFacturacionImpr == "Proforma") {
                            imprimirFacturaProformaTotal(
                                sale_actualizada,
                                QuickContext!!, 1
                            )
                            Toast.makeText(
                                QuickContext,
                                "imprimir Totalizar Preventa",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (bluetoothStateChangeReceiver!!.isBluetoothAvailable == false) {
                        CreateMessage(
                            QuickContext!!,
                            "Error",
                            "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                        )
                    }
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
        val txtSubida: TextView =
            view.findViewById<View>(R.id.txtSubidaReimpPedidos) as TextView
        var cardView: CardView =
            view.findViewById<View>(R.id.cardViewDistrClientes) as CardView

        // Button btnDevolverFacturaCliente;
        //  btnDevolverFacturaCliente = (Button) view.findViewById(R.id.btnDevolverFacturaCliente);
        var btnImprimirFacturaCliente: ImageButton =
            view.findViewById<View>(R.id.btnImprimirFacturaCliente) as ImageButton
        var btnUbicacionFacturaCliente: ImageButton =
            view.findViewById<View>(R.id.btnUbicacionFacturaCliente) as ImageButton
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    companion object {
        private var QuickContext: Context? = null
    }
}
