package com.friendlysystemgroup.friendlypos.reimpresion.fragment

import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.friendlysystemgroup.friendlypos.application.util.Functions
import io.realm.Realm
import com.friendlypos.util.HtmlTextView
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


import com.friendlysystemgroup.friendlypos.preventas.fragment.BaseFragment
import com.friendlypos.util.LocalImageGetter

class ReimprimirResumenFragment : BaseFragment() {
    @BindView(R.id.html_text)
    lateinit var text: HtmlTextView

    @BindView(R.id.btnReimprimirFactura)
    lateinit var btnReimprimirFactura: ImageButton
    var bluetoothStateChangeReceiver: BluetoothStateChangeReceiver? = null
    var activity: VentaDirectaActivity? = null
    var sale_actualizada: sale? = null
    var facturaId: String? = ""
    var nombreMetodoPago: String? = null
    var slecTAB: Int = 0
    var tipoFacturacion: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothStateChangeReceiver = BluetoothStateChangeReceiver()
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(getContext())
        activity = VentaDirectaActivity()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View =
            inflater.inflate(R.layout.fragment_reimprimir_resumen, container, false)
        text = rootView.findViewById<View>(R.id.html_text) as HtmlTextView
        //ButterKnife.bind(this, rootView)


        btnReimprimirFactura.setOnClickListener(View.OnClickListener {
            var a = "1"
            if (sale_actualizada.sale_type === "2") {
                a = "2"
            }
            if (bluetoothStateChangeReceiver.isBluetoothAvailable == true) {
                tipoFacturacion = sale_actualizada.facturaDePreventa

                if (tipoFacturacion == "Distribucion") {
                    val layoutInflater: LayoutInflater = LayoutInflater.from(getActivity())
                    val promptView: View =
                        layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null)

                    val alertDialogBuilder =
                        AlertDialog.Builder(getActivity())
                    alertDialogBuilder.setView(promptView)
                    val checkbox: CheckBox =
                        promptView.findViewById<View>(R.id.checkbox) as CheckBox

                    val label: TextView =
                        promptView.findViewById<View>(R.id.promtClabelRecibosImp) as TextView
                    label.setText("Escriba el número de impresiones requeridas")

                    val input: EditText =
                        promptView.findViewById<View>(R.id.promtCtextRecibosImp) as EditText

                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton(
                        "OK",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                val cantidadImpresiones: String = input.getText().toString()

                                PrinterFunctions.imprimirFacturaDistrTotal(
                                    sale_actualizada,
                                    getActivity(),
                                    1,
                                    cantidadImpresiones
                                )
                                Toast.makeText(
                                    getActivity(),
                                    "imprimir Totalizar Dist",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    alertDialogBuilder.setNegativeButton(
                        "Cancel",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                dialog.cancel()
                            }
                        })

                    val alertD = alertDialogBuilder.create()
                    alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    alertD.show()
                } else if (tipoFacturacion == "VentaDirecta") {
                    val layoutInflater: LayoutInflater = LayoutInflater.from(getActivity())
                    val promptView: View =
                        layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null)

                    val alertDialogBuilder =
                        AlertDialog.Builder(getActivity())
                    alertDialogBuilder.setView(promptView)
                    val checkbox: CheckBox =
                        promptView.findViewById<View>(R.id.checkbox) as CheckBox

                    val label: TextView =
                        promptView.findViewById<View>(R.id.promtClabelRecibosImp) as TextView
                    label.setText("Escriba el número de impresiones requeridas")

                    val input: EditText =
                        promptView.findViewById<View>(R.id.promtCtextRecibosImp) as EditText

                    alertDialogBuilder.setCancelable(false)
                    alertDialogBuilder.setPositiveButton(
                        "OK",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                val cantidadImpresiones: String = input.getText().toString()


                                PrinterFunctions.imprimirFacturaVentaDirectaTotal(
                                    sale_actualizada,
                                    getActivity(),
                                    3,
                                    cantidadImpresiones
                                )
                                Toast.makeText(
                                    getActivity(),
                                    "imprimir Totalizar VentaD",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    alertDialogBuilder.setNegativeButton(
                        "Cancel",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                dialog.cancel()
                            }
                        })

                    val alertD = alertDialogBuilder.create()
                    alertD.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    alertD.show()
                }
            } else if (bluetoothStateChangeReceiver.isBluetoothAvailable == false) {
                Functions.CreateMessage(
                    getActivity(),
                    "Error",
                    "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                )
            }
        })

        return rootView
    }


    private val htmlPreview: Unit
        get() {
            try {
                val realm = Realm.getDefaultInstance()
                val sysconf: Sysconf = realm.where<Sysconf>(Sysconf::class.java).findFirst()
                val clientes: Clientes = realm.where<Clientes>(Clientes::class.java)
                    .equalTo("id", sale_actualizada.customer_id).findFirst()
                val invoice: invoice = realm.where<invoice>(invoice::class.java)
                    .equalTo("id", sale_actualizada.invoice_id).findFirst()
                val result: RealmResults<Pivot> = realm.where<Pivot>(Pivot::class.java)
                    .equalTo("invoice_id", sale_actualizada.invoice_id).findAll()

                val idUsuario: String = invoice.user_id

                val usuarios: Usuarios =
                    realm.where<Usuarios>(Usuarios::class.java).equalTo("id", idUsuario).findFirst()

                val nombreUsuario: String = usuarios.username

                // VARIABLES VENTA
                val fechayhora: String = sale_actualizada.updated_at
                val nombreCliente: String = sale_actualizada.customer_name

                // VARIABLES CLIENTES
                val cardCliente: String = clientes.card
                val companyCliente: String = clientes.companyName
                val fantasyCliente: String = clientes.fantasyName
                val telefonoCliente: String = clientes.phone
                val descuentoCliente: Double = clientes.fixedDiscount.toDouble()

                // VARIABLES FACTURA
                val fechaFactura: String = invoice.due_date
                val numeracionFactura: String = invoice.numeration
                val metodoPago: String = invoice.payment_method_id

                if (metodoPago == "1") {
                    nombreMetodoPago = "Contado"
                } else if (metodoPago == "2") {
                    nombreMetodoPago = "Crédito"
                }

                val totalGrabado =
                    Functions.doubleToString1(invoice.subtotal_taxed.toDouble())
                val totalExento =
                    Functions.doubleToString1(invoice.subtotal_exempt.toDouble())
                val totalSubtotal =
                    Functions.doubleToString1(invoice.subtotal.toDouble())
                val totalDescuento =
                    Functions.doubleToString1(invoice.discount.toDouble())
                val totalImpuesto =
                    Functions.doubleToString1(invoice.tax.toDouble())
                val totalTotal =
                    Functions.doubleToString1(invoice.total.toDouble())
                val totalCancelado =
                    Functions.doubleToString1(invoice.paid.toDouble())
                val totalVuelto =
                    Functions.doubleToString1(invoice.changing.toDouble())
                val totalNotas: String = invoice.note


                // VARIABLES SYSCONF
                val sysNombre: String = sysconf.name
                val sysNombreNegocio: String = sysconf.business_name
                val sysDireccion: String = sysconf.direction
                val sysIdentificacion: String = sysconf.identification
                val sysTelefono: String = sysconf.phone
                val sysCorreo: String = sysconf.email
                realm.close()

                var preview = ""
                val condition =
                    ("Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                            + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                            + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                            + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                            + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                            + "bancarias de " + sysNombre + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ")

                if (sale_actualizada != null) {
                    var billString = ""
                    if (sale_actualizada.sale_type == "1") {
                        billString = "Factura"
                    } else if (sale_actualizada.sale_type == "2") {
                        billString = "Factura"
                    } else if (sale_actualizada.sale_type == "3") {
                        billString = "Proforma"
                    }


                    preview += "<center><h2>$billString a $nombreMetodoPago</h2>"
                    preview += "<h5>$billString #$numeracionFactura</h3>"
                    preview += "<center><h2>$sysNombre</h2></center>"
                    preview += "<center><h4>$sysNombreNegocio</h4></center>"
                    preview += "<h6>$sysDireccion</h2></center>"
                    preview += "<a><b>Tel:</b> $sysTelefono</a><br>"
                    preview += "<a><b>E-mail:</b> $sysCorreo</a><br>"
                    preview += "<a><b>Cedula Juridica:</b> $sysIdentificacion</a><br>"
                    preview += "<a><b>Fecha:</b> $fechayhora</a><br>"
                    preview += "<a><b>Fecha de impresión:</b> " + Functions.date + " " + Functions.get24Time() + "</a><br><br>"
                    preview += "<a><b>Vendedor:</b> $nombreUsuario</a><br>"
                    preview += "<a><b>ID Cliente:</b> $cardCliente</a><br>"
                    preview += "<a><b>Cliente:</b> $companyCliente</a><br>"
                    preview += "<a><b>A nombre de:</b> $nombreCliente</a><br><br>"
                    /*   preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
       preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
       preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
*/
                    preview += "<a><b>" + padRight(
                        "Descripcion",
                        10.0
                    ) + "\t\t" + padRight(
                        "Codigo",
                        10.0
                    ) + "</b></a><br>"
                    preview += "<a><b>" + padRight(
                        "Cantidad",
                        10.0
                    ) + padRight(
                        "Precio",
                        10.0
                    ) + padRight(
                        "P.Sug",
                        10.0
                    ) + padRight("Total", 10.0) + "</b></a><br>"
                    preview += "<a><b>" + padRight(
                        "Tipo",
                        10.0
                    ) + "</b></a><br>"
                    preview += "<a>------------------------------------------------<a><br>"

                    preview += getPrintDistTotal(
                        sale_actualizada.invoice_id
                    )
                    preview += "<center><a>" + String.format(
                        "%20s %-20s",
                        "Subtotal Gravado",
                        totalGrabado
                    ) + "</a><br>"
                    preview += "<a> " + String.format(
                        "%20s %-20s",
                        "Subtotal Exento",
                        totalExento
                    ) + "</a><br>"
                    preview += "<a> " + String.format(
                        "%20s %-20s",
                        "Subtotal",
                        totalSubtotal
                    ) + "</a><br>"
                    preview += "<a> " + String.format(
                        "%20s %-20s",
                        "IVA",
                        totalImpuesto
                    ) + "</a><br>"
                    preview += "<a> " + String.format(
                        "%20s %-20s",
                        "Descuento",
                        totalDescuento
                    ) + "</a><br>"
                    preview += "<a> " + String.format(
                        "%20s %-20s",
                        "Total",
                        totalTotal
                    ) + "</a><br><br></center>"
                    preview += "<a><b>Notas:</b> $totalNotas</a><br>"
                    preview += "<a><b>Firma y Cedula:_______________________________</b></a><br>"
                    if (metodoPago === "2") {
                        preview += "<br><br><font size=\"7\"><p>$condition</p></font>"
                    }

                    preview += "<a>" +
                            " Autorizado mediante oficio <br>" +
                            "N° : 11-1997 de la D.G.T.D  </a>"
                } else {
                    preview += "<center><h2>Seleccione la factura a ver</h2></center>"
                }
                //HtmlTextView.
                text.setHtmlFromString(preview, LocalImageGetter())
            } catch (e: Exception) {
                val preview =
                    "<center><h2>Seleccione la factura a ver cath</h2></center>"
                text.setHtmlFromString(preview, LocalImageGetter())
                Log.d("adsdad", e.message!!)
            }
        }

    override fun updateData() {
        slecTAB = (getActivity() as ReimprimirActivity).selecFacturaTab

        if (slecTAB == 1) {
            facturaId = (getActivity() as ReimprimirActivity).invoiceIdReimprimir
            if (facturaId != null) {
                Log.d("FACTURAIDReim", facturaId!!)
            }

            // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
            val realm3 = Realm.getDefaultInstance()
            realm3.executeTransaction { realm3 ->
                sale_actualizada =
                    realm3.where<sale>(sale::class.java).equalTo("invoice_id", facturaId)
                        .findFirst()
                realm3.close()
            }

            var a = "1"
            if (sale_actualizada.sale_type === "2") {
                a = "2"
            }
            htmlPreview
            // PrinterFunctions.imprimirFacturaDistrTotal(sale_actualizada, getActivity(), Integer.parseInt(a));
        } else {
            Toast.makeText(getActivity(), "nadaSelecProducto", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun padRight(s: String, n: Double): String {
            val centeredString: String
            val pad = (n + 4) - s.length

            if (pad > 0) {
                val pd = Functions.paddigTabs((pad / 2.0).toInt().toLong())
                centeredString = "\t" + s + "\t" + pd
                println("pad: |$centeredString|")
            } else {
                centeredString = "\t" + s + "\t"
            }
            return centeredString
        }

        private fun getPrintDistTotal(idVenta: String): String {
            var send = ""

            val realm1 = Realm.getDefaultInstance()
            val result: RealmResults<Pivot> =
                realm1.where<Pivot>(Pivot::class.java).equalTo("invoice_id", idVenta)
                    .equalTo("devuelvo", 0).findAll()

            if (result.isEmpty()) {
                send = "No hay invoice emitidas"
            } else {
                // printSalesCashTotal= 0.0;
                for (i in result.indices) {
                    val salesList1: List<Pivot> =
                        realm1.where<Pivot>(Pivot::class.java).equalTo("invoice_id", idVenta)
                            .equalTo("devuelvo", 0).findAll()
                    val producto: Productos = realm1.where<Productos>(Productos::class.java)
                        .equalTo("id", salesList1[i].product_id).findFirst()

                    val precioSugerido: Double = producto.suggested.toDouble()
                    val description: String = producto.description
                    val byteText = description.toByteArray(Charset.forName("UTF-8"))
                    var description1: String? = null
                    try {
                        description1 = String(byteText, charset("UTF-8"))
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    val barcode: String = producto.barcode
                    val typeId: String = producto.product_type_id
                    var nombreTipo: String? = null

                    val cant: Double = salesList1[i].amount.toDouble()
                    val precio: Double = salesList1[i].price.toDouble()

                    var sugerido = 0.0

                    // gravado Sugerido =( (preciode venta/1.13)*(suggested /100) )+ (preciode venta* 0.13)+(preciode venta/1.13);
                    // en caso de exento Sugerido =( (preciode venta)*(suggested /100)) + (preciode venta);
                    if (typeId == "1") {
                        nombreTipo = "Gravado"
                        sugerido =
                            (precio / 1.13) * (precioSugerido / 100) + (precio * 0.13) + (precio / 1.13)
                    } else if (typeId == "2") {
                        nombreTipo = "Exento"
                        sugerido = (precio) * (precioSugerido / 100) + (precio)
                    }

                    /*  String factFecha = salesList1.get(i).getDate();
                double factTotal = Functions.sGetDecimalStringAnyLocaleAsDouble(salesList1.get(i).getTotal());*/
                    send += String.format(
                        "%s  %.24s ",
                        description1,
                        barcode
                    ) + "<br>" + String.format(
                        "%-12s %-10s %-12s %.10s",
                        cant,
                        Functions.doubleToString1(precio),
                        Functions.doubleToString1(sugerido),
                        Functions.doubleToString1(cant * precio)
                    ) + "<br>" + String.format("%.10s", nombreTipo) + "<br>"
                    send += "<a>------------------------------------------------<a><br>"


                    /*  send += String.format("%s  %.24s ", description, barcode) + "<br>" +
                    String.format("%-5s %-10s %-10s %-15s %.1s", cant / *bill.amount, precio, precio, Functions.doubleToString(cant * precio), typeId) + "<br>";
                send += "<a>------------------------------------------------<a><br>";*/
                    Log.d("FACTPRODTODFAC", send + "")
                }
                realm1.close()
            }
            return send
        }
    }
}


