package com.friendlypos.application.util

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.friendlypos.Recibos.modelo.receipts
import com.friendlypos.Recibos.modelo.recibos
import com.friendlypos.application.bluetooth.PrinterService
import com.friendlypos.application.util.Functions.doubleToString1
import com.friendlypos.application.util.Functions.paddigTabs
import com.friendlypos.distribucion.modelo.Inventario
import com.friendlypos.distribucion.modelo.Pivot
import com.friendlypos.distribucion.modelo.invoice
import com.friendlypos.distribucion.modelo.sale
import com.friendlypos.login.modelo.Usuarios
import com.friendlypos.login.util.SessionPrefes
import com.friendlypos.principal.activity.MenuPrincipal
import com.friendlypos.principal.modelo.Clientes
import com.friendlypos.principal.modelo.Productos
import com.friendlypos.principal.modelo.Sysconf
import io.realm.Realm
import io.realm.RealmResults
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date

object PrinterFunctions {
    var handler: Handler? = null
    var runnable: Runnable? = null
    private var printSalesCashTotal = 0.0
    private var printLiqContadoTotal = 0.0
    private var printLiqCreditoTotal = 0.0
    private var printLiqRecibosTotal = 0.0
    private var printRecibosTotal = 0.0

    var totalGrabado: String = ""
    var totalExento: String = ""
    var totalSubtotal: String = ""
    var totalDescuento: String = ""
    var totalImpuesto: String = ""
    var totalTotal: String = ""
    var totalCancelado: String = ""
    var totalVuelto: String = ""
    var totalNotas: String = ""
    var totalGrabado_: String = ""
    var totalExento_: String = ""
    var totalSubtotal_: String = ""
    var totalDescuento_: String = ""
    var totalImpuesto_: String = ""
    var totalTotal_: String = ""
    var totalCancelado_: String = ""
    var totalVuelto_: String = ""
    var totalNotas_: String = ""
    var totalNotasRecibos: String = ""
    var precio: Double = 0.0

    //TODO imprimir TOTALIZAR DISTRIBUCION
    fun datosImprimirDistrTotal(
        type: Int,
        sale: sale,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val impresiones1 = cantidadImpresiones.toInt()

        for (i in 1..impresiones1) {
            Log.d("impresiones", "cantidadImpresion$impresiones1")

            var payment = ""
            var messageC = ""
            var billptype = ""
            var metodoPagoNombre = ""
            var preview = ""

            val realm = Realm.getDefaultInstance()
            val sysconf = realm.where(Sysconf::class.java).findFirst()
            val clientes =
                realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
            val invoice =
                realm.where(invoice::class.java).equalTo("id", sale.invoice_id).findFirst()

            val result: RealmResults<Pivot> =
                realm.where(Pivot::class.java).equalTo("invoice_id", sale.invoice_id)
                    .equalTo("devuelvo", 0).findAll()
            Log.d("FACTPRODTOD", result.toString() + "")
            // VARIABLES VENTA
            val fechayhora = sale.updated_at

            // VARIABLES CLIENTES
            val cardCliente = clientes!!.card
            val companyCliente = clientes.companyName
            val fantasyCliente = clientes.fantasyName
            val telefonoCliente = clientes.phone
            val descuentoCliente = clientes.fixedDiscount.toDouble()

            // VARIABLES FACTURA
            val fechaFactura = invoice!!.due_date
            val numeracionFactura = invoice.numeration
            val metodoPago = invoice.payment_method_id
            val key = invoice.key
            val numConsecutivo = invoice.consecutive_number
            val tipoFactura = invoice.type

            totalGrabado = doubleToString1(
                invoice.subtotal_taxed.toDouble()
            )
            totalExento = doubleToString1(
                invoice.subtotal_exempt.toDouble()
            )
            totalSubtotal = doubleToString1(
                invoice.subtotal.toDouble()
            )
            totalDescuento = doubleToString1(
                invoice.discount.toDouble()
            )
            totalImpuesto = doubleToString1(
                invoice.tax.toDouble()
            )
            totalTotal = doubleToString1(
                invoice.total.toDouble()
            )
            totalCancelado = doubleToString1(
                invoice.paid.toDouble()
            )
            totalVuelto = doubleToString1(
                invoice.changing.toDouble()
            )
            totalNotas = invoice.note
            val idUsuario = invoice.user_id

            val usuarios = realm.where(Usuarios::class.java).equalTo("id", idUsuario).findFirst()

            val nombreUsuario = usuarios!!.username

            // VARIABLES SYSCONF
            val sysNombre = sysconf!!.name
            val sysNombreNegocio = sysconf.business_name
            val sysDireccion = sysconf.direction
            val sysIdentificacion = sysconf.identification
            val sysTelefono = sysconf.phone
            val sysCorreo = sysconf.email
            realm.close()

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
            val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!

            val condition =
                ("Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                        + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                        + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                        + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                        + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                        + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ")


            // TODO DISTRIBUCION
            if (tipoFactura == "01") {
                billptype = "Factura Electronica"
            } else if (tipoFactura == "04") {
                billptype = "Tiquete Electronico"
            }


            if (metodoPago == "1") {
                metodoPagoNombre = "Contado"
            } else if (metodoPago == "2") {
                metodoPagoNombre = "Credito"
            }


            if (prefList == "1") {
                val bill = ("""! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
! U1 LMARGIN 0
${String.format("%s", billptype)}
! U1 SETLP 7 0 14
------------------------------------------------


! U1 SETLP 5 1 35
$sysNombreNegocio
! U1 SETLP 7 0 14
------------------------------------------------

! U1 SETLP 7 0 14
$sysNombre
Cedula Juridica: $sysIdentificacion
$sysDireccion
Tel. $sysTelefono
Correo Electronico: $sysCorreo
------------------------------------------------

! U1 SETLP 7 0 14

# Factura: $numeracionFactura  $metodoPagoNombre  
Consec DGT: #$numConsecutivo
Clave DGT: #$key
------------------------------------------------

! U1 LMARGIN 0
""" +
                        (if (metodoPago === "2") ("""
     ! U1 LMARGIN 120
     Fecha Limite: ! U1 LMARGIN 350 $messageC
     
     """.trimIndent()) else "\r\n") +
                        "! U1 LMARGIN 0\r\n" +
                        "! U1 SETLP 7 0 14\r\n" +
                        "Fecha y hora: " + fechayhora + "\r\n" +
                        "Vendedor:  " + nombreUsuario + "\r\n" +
                        "Razon Social: " + companyCliente + "\r\n" +
                        "Nombre fantasia: " + fantasyCliente + "\r\n" +
                        "Cedula: " + cardCliente + " \r\n" +
                        "# Telefono: " + telefonoCliente + "\r\n" +
                        "------------------------------------------------\r\n" + "\r\n" +
                        "! U1 LMARGIN 0\r\n" +
                        "! U1 SETSP 0\r\n" +
                        "Descripcion           Codigo\r\n" +
                        "Cantidad      Precio       P.Sug       Total\r\n" +
                        "Tipo     \r\n" +
                        "- - - - - - - - - - - - - - - - - - - - - - - -\r\n" +
                        "! U1 SETLP 7 0 10\r\n" +
                        getPrintDistTotal(sale.invoice_id) +
                        "\r\n" + String.format(
                    "%20s %-20s",
                    "Subtotal Gravado",
                    totalGrabado
                ) + "\r\n" + String.format(
                    "%20s %-20s",
                    "Subtotal Exento",
                    totalExento
                ) + "\r\n" + String.format(
                    "%20s %-20s",
                    "Subtotal",
                    totalSubtotal
                ) + "\r\n" + String.format(
                    "%20s %-20s",
                    "Descuento",
                    totalDescuento
                ) + "\r\n" + String.format("%20s %-20s", "IVA", totalImpuesto) + "\r\n" +
                        "! U1 SETLP 5 3 70\r\n" + String.format(
                    "%20s %-20s",
                    "Total a pagar",
                    totalTotal
                ) +
                        "! U1 SETLP 7 0 10\r\n" + "\r\n" +
                        (if (metodoPago === "1") ("""
     ${
                            String.format(
                                "%20s %-20s",
                                "Cancelado con",
                                totalCancelado
                            )
                        }
     ${String.format("%20s %-20s", "Cambio", totalVuelto)}
     """.trimIndent()) else "\r\n"
                                ) +
                        "Notas: " + totalNotas_ + "\r\n" + "\r\n" +
                        (if (descuentoCliente > 0) "Se le aplico un $descuentoCliente%  de descuento" else "" + "\r\n") +
                        "! U1 SETLP 5 0 14\r\n" +
                        "Firma cliente ____________________________" + "\r\n" + "\r\n" + "\r\n" +
                        "Cedula ____________________________" + "\r\n" +
                        (if (metodoPago === "2") "! U1 SETLP 0 0 6\r\n\r\n$condition" else "\r\n"
                                ) + "\r\n" +
                        (if (ptype == 1)
                            ("""
                        
                        Este comprobante no puede ser utilizado para fines
                        tributarios, por lo cual no se permitira
                        su uso para respaldo de creditos o gastos
                        
                        
                        """.trimIndent())
                        else
                            "\r\n"
                                ) + "\r\n" +
                        "! U1 SETLP 5 0 14\r\n" +
                        " Autorizada mediante resolucion N DGT-R-48-2016\r\n" +
                        "del 7 de octubre de 2016.\r\n" + "\r\n" +
                        "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                        "Mantenga el documento para reclamos ." + "\r\n" +
                        " \n\n" +
                        " \n\n" +
                        " \n ")
                Log.d("IMPR DISTR ZEBRA", bill)
                val intent2 = Intent(PrinterService.BROADCAST_CLASS)
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
                intent2.putExtra("bill_to_print", bill)
                QuickContext.sendBroadcast(intent2)
            } else if (prefList == "2") {
                when (metodoPago) {
                    "1" -> {
                        payment = "Contado"
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%s",
                            billptype
                        ) + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombre + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysDireccion + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Clave DGT: #" + key + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + getPrintDistTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Gravado",
                            totalGrabado
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Exento",
                            totalExento
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal",
                            totalSubtotal
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Descuento",
                            totalDescuento
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "IVA",
                            totalImpuesto
                        ) + Html.fromHtml("</h1></center><br/><br/><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Total a pagar",
                            totalTotal
                        ) + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Cancelado con",
                            totalCancelado
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Cambio",
                            totalVuelto
                        ) + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Notas: " + totalNotas + Html.fromHtml("</h1></center><br/><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Firma cliente ____________________________" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>")

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "tributarios, por lo cual no se permitira" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "su uso para respaldo de creditos o gastos." + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Autorizada mediante resolucion Nº DGT-R-48-2016" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/><br/>")


                        preview += Html.fromHtml("<h1>")
                            .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/>")



                        Log.d("IMPR DISTR CONTADO", preview)
                    }

                    "2" -> {
                        payment = "Credito"
                        messageC = fechaFactura

                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%s",
                            billptype
                        ) + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombre + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysDireccion + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Clave DGT: #" + key + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Fecha Limite: " + messageC + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )

                        preview += Html.fromHtml("<h1>")
                            .toString() + "#  Descripcion      Codigo" + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + getPrintDistTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Gravado",
                            totalGrabado
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Exento",
                            totalExento
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal",
                            totalSubtotal
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Descuento",
                            totalDescuento
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "IVA",
                            totalImpuesto
                        ) + Html.fromHtml("</h1></center><br/><br/><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Total a pagar",
                            totalTotal
                        ) + Html.fromHtml("</h1></center><br/><br/>")


                        preview += Html.fromHtml("<h1>")
                            .toString() + "Notas: " + totalNotas + Html.fromHtml("</h1></center><br/><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Firma cliente ____________________________" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + condition + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "tributarios, por lo cual no se permitira" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "su uso para respaldo de creditos o gastos." + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Autorizada mediante resolucion N DGT-R-48-2016" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/><br/>")

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/>")


                        Log.d("IMPR DISTR CREDITO", preview)
                    }
                }
                val intent2 = Intent(PrinterService.BROADCAST_CLASS)
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
                intent2.putExtra("bill_to_print", preview)
                QuickContext.sendBroadcast(intent2)
            }
        }

        if (ptype == 1) {
            handler = Handler()
            runnable = Runnable {
                val intent = Intent(QuickContext, MenuPrincipal::class.java)
                QuickContext.startActivity(intent)
            }
        } else if (ptype == 3) {
            handler = Handler()
            runnable = Runnable {
                val intent = Intent(QuickContext, MenuPrincipal::class.java)
                QuickContext.startActivity(intent)
            }
        }

        handler!!.removeCallbacks(runnable!!)
        handler!!.postDelayed(runnable!!, 500)


        totalGrabado = ""
        totalExento = ""
        totalSubtotal = ""
        totalDescuento = ""
        totalImpuesto = ""
        totalTotal = ""
        totalCancelado = ""
        totalVuelto = ""
        totalNotas = ""
    }

    @JvmStatic
    fun imprimirFacturaDistrTotal(
        saleB: sale?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val dialogReturnSale = AlertDialog.Builder(QuickContext)
            .setTitle("Impresión")
            .setMessage("¿Desea realizar la impresión?")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                imprimirTotalizar(
                    1,
                    saleB,
                    QuickContext,
                    ptype,
                    cantidadImpresiones
                )
            }.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }.create()
        dialogReturnSale.show()
    }

    private fun imprimirTotalizar(
        type: Int,
        invoices: sale?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        try {
            if (invoices != null) {
                datosImprimirDistrTotal(type, invoices, QuickContext, ptype, cantidadImpresiones)
            } else {
                Toast.makeText(
                    QuickContext,
                    "Aun le falta terminar de hacer la factura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error PrinterFunctions", e.message!!)
        }
    }

    private fun getPrintDistTotal(idVenta: String): String {
        var send = ""

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime = sdf.format(Date())

        val realm = Realm.getDefaultInstance()
        val result: RealmResults<Pivot> =
            realm.where(Pivot::class.java).equalTo("invoice_id", idVenta).equalTo("devuelvo", 0)
                .findAll()

        if (result.isEmpty()) {
            send = "No hay invoice emitidas"
        } else {
            for (i in result.indices) {
                val salesList1: List<Pivot> =
                    realm.where(Pivot::class.java).equalTo("invoice_id", idVenta)
                        .equalTo("devuelvo", 0).findAll()
                val producto =
                    realm.where(Productos::class.java).equalTo("id", salesList1[i].product_id)
                        .findFirst()

                val precioSugerido = producto!!.suggested.toDouble()
                val description = producto.description
                val byteText = description.toByteArray(Charset.forName("UTF-8"))
                var description1: String? = null
                try {
                    description1 = String(byteText, charset("UTF-8"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                val barcode = producto.barcode

                val cant = salesList1[i].amount.toDouble()

                var sugerido = 0.0

                val impuesto = producto.iva
                var nombreTipo: String? = null

                if (impuesto == 0.0) {
                    nombreTipo = "Exento"
                    precio = salesList1[i].price.toDouble()
                } else {
                    nombreTipo = "Gravado"
                    precio = salesList1[i].price.toDouble() / 1.13
                }

                sugerido = (precio) * (precioSugerido / 100) + (precio)

                send += """
                    ${String.format("%s  %.24s ", description1, barcode)}
                    ${
                    String.format(
                        "%-12s %-10s %-12s %.10s",
                        cant,
                        doubleToString1(precio),
                        doubleToString1(sugerido),
                        doubleToString1(cant * precio)
                    )
                }
                    ${String.format("%.10s", nombreTipo)}
                    
                    """.trimIndent()
                send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n"
                Log.d("FACTPRODTODFAC", send + "")
            }
        }
        return send
    }

    // TODO IMPRIMIR RECIBOS
    fun datosImprimirRecibosTotal(
        type: Int,
        sale: recibos,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime = sdf.format(Date())

        val impresiones1 = cantidadImpresiones.toInt()

        for (i in 1..impresiones1) {
            Log.d("impresiones", "cantidadImpresion$impresiones1")

            var billptype = ""
            var preview = ""

            val realm = Realm.getDefaultInstance()
            val clientes =
                realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
            val sysconf = realm.where(Sysconf::class.java).findFirst()

            val receipts =
                realm.where(receipts::class.java).equalTo("customer_id", sale.customer_id)
                    .findFirst()


            val referencia = sale.referencia_receipts

            val sysNombre = sysconf!!.name
            val sysNombreNegocio = sysconf.business_name
            val sysDireccion = sysconf.direction
            val sysIdentificacion = sysconf.identification
            val sysTelefono = sysconf.phone
            val sysCorreo = sysconf.email

            val nombreCliente = clientes!!.fantasyName
            totalNotasRecibos = sale.observaciones
            val fecha =  /*sale.getDate()*/currentDateandTime
            val porPagar = sale.porPagar
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
            val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!


            if (ptype == 1) {
                billptype = "R e c i b o s"
            }

            if (prefList == "1") {
                val bill = """! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
! U1 LMARGIN 185
${String.format("%s", billptype)}
! U1 LMARGIN 0
! U1 SETLP 5 0 24
# $referencia
! U1 SETLP 5 0 24
$sysNombre
! U1 SETLP 5 1 35
$sysNombreNegocio
! U1 SETLP 5 0 24
$sysDireccion
Cedula $sysIdentificacion  Tel. $sysTelefono
E-mail $sysCorreo
! U1 SETLP 7 0 14

Cliente: $nombreCliente
Fecha: $fecha
! U1 LMARGIN 0
! U1 SETSP 0

------------------------------------------------
! U1 SETLP 7 0 10
${getPrintRecibosTotal(sale.customer_id)}

Saldo pendiente: ${doubleToString1(porPagar)}


Notas: $totalNotasRecibos


Muchas Gracias por preferirnos, un placer atenderlo
Mantenga el documento para reclamos .

 

 

 
 """
                val intent2 = Intent(PrinterService.BROADCAST_CLASS)
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
                intent2.putExtra("bill_to_print", bill)
                QuickContext.sendBroadcast(intent2)
                Log.d("imprimeZebraProf", bill)
            } else if (prefList == "2") {
                preview += Html.fromHtml("<h1>").toString() + String.format(
                    "%s",
                    billptype
                ) + Html.fromHtml("</h1><br/><br/><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "# " + referencia + Html.fromHtml("</h1><br/><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + sysNombre + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + sysDireccion + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + Html.fromHtml(
                    "</h1></center><br/>"
                )
                preview += Html.fromHtml("<h1>").toString() + "E-mail " + sysCorreo + Html.fromHtml(
                    "</h1><br/><br/>"
                )

                preview += Html.fromHtml("<h1>")
                    .toString() + "Cliente: " + nombreCliente + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Fecha: " + fecha + Html.fromHtml("</h1><br/><br/>")
                //   preview += Html.fromHtml("<h1>") +  "Numeracion     Monto Total     Monto Pagado" + Html.fromHtml("</h1></center><br/>");
                //   preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                preview += Html.fromHtml("<h1>")
                    .toString() + getPrintRecibosTotal(sale.customer_id) + Html.fromHtml("</h1></center><br/><br/>")

                preview += Html.fromHtml("<h1>").toString() + "Saldo pendiente: " + doubleToString1(
                    porPagar
                ) + Html.fromHtml("</h1></center><br/><br/><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Notas: " + totalNotasRecibos + Html.fromHtml("</h1></center><br/><br/><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                    "</h1></center><br/>"
                )
                preview += Html.fromHtml("<h1>")
                    .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/><br/>")
            }
            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
            Log.d("imprimeProf", preview)
        }

        val realm2 = Realm.getDefaultInstance()
        realm2.executeTransaction { realm2 ->
            val result: RealmResults<recibos> =
                realm2.where(recibos::class.java).equalTo("customer_id", sale.customer_id)
                    .equalTo("abonado", 1).findAll()
            if (result.isEmpty()) {
                Toast.makeText(QuickContext, "No hay recibos emitidos", Toast.LENGTH_LONG).show()
            } else {
                for (i in result.indices) {
                    val salesList1: List<recibos> = realm2.where(
                        recibos::class.java
                    ).equalTo("customer_id", sale.customer_id).equalTo("abonado", 1).findAll()

                    val facturaId1 = salesList1[i].invoice_id

                    val recibo_actualizado =
                        realm2.where(recibos::class.java).equalTo("invoice_id", facturaId1)
                            .findFirst()
                    recibo_actualizado!!.mostrar = 0

                    realm2.insertOrUpdate(recibo_actualizado)

                    Log.d("ACTMOSTRAR", recibo_actualizado.toString() + "")
                }
                realm2.close()
            }
        }
    }

    private fun getPrintRecibosTotal(idVenta: String): String {
        var send: String

        send = " "
        printRecibosTotal = 0.0
        val realm1 = Realm.getDefaultInstance()
        val result: RealmResults<recibos> =
            realm1.where(recibos::class.java).equalTo("customer_id", idVenta).equalTo("abonado", 1)
                .equalTo("mostrar", 1).findAll()

        if (result.isEmpty()) {
            send = "No hay recibos emitidos"
        } else {
            for (i in result.indices) {
                val salesList1: List<recibos> =
                    realm1.where(recibos::class.java).equalTo("customer_id", idVenta)
                        .equalTo("abonado", 1).equalTo("mostrar", 1).findAll()


                val numeracion = salesList1[i].numeration

                val total = salesList1[i].total
                val pagado = salesList1[i].montoCanceladoPorFactura
                val totalAbonos = salesList1[i].paid

                send += """
                    ${
                    padRight(
                        "# Factura: ",
                        30.0
                    )
                }${padRight(numeracion, 30.0)}
                    ${
                    padRight(
                        "Total Recibo: ",
                        30.0
                    )
                }${
                    padRight(
                        doubleToString1(
                            pagado
                        ), 30.0
                    )
                }
                    ${
                    padRight(
                        "Monto total: ",
                        30.0
                    )
                }${
                    padRight(
                        doubleToString1(
                            total
                        ), 30.0
                    )
                }
                    ${
                    padRight(
                        "Total en abonos: ",
                        30.0
                    )
                }${
                    padRight(
                        doubleToString1(
                            totalAbonos
                        ), 30.0
                    )
                }
                    
                    """.trimIndent()

                // TODO PRUEBA SUBIR
                // send += String.format("%-9s  %9s  %9s", numeracion, Functions.doubleToString1(total), Functions.doubleToString1(pagado) ) + "\r\n";
                // printRecibosTotal = printRecibosTotal + pagado;
                send += "------------------------------------------------\r\n"

                Log.d("FACTPRODTODFAC", send + "")
            }
            realm1.close()
        }
        return send
    }

    fun padRight(s: String, n: Double): String {
        val centeredString: String
        val pad = (n + 4) - s.length

        if (pad > 0) {
            val pd = paddigTabs((pad / 2.0).toInt().toLong())
            centeredString = "\t" + s + "\t" + pd
            println("pad: |$centeredString|")
        } else {
            centeredString = "\t" + s + "\t"
        }
        return centeredString
    }

    @JvmStatic
    fun imprimirFacturaRecibosTotal(
        recibo: recibos?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val dialogReturnSale = AlertDialog.Builder(QuickContext)
            .setTitle("Impresión")
            .setMessage("¿Desea realizar la impresión?")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                imprimirRecibosTotalizar(
                    1,
                    recibo,
                    QuickContext,
                    ptype,
                    cantidadImpresiones
                )
            }.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }.create()
        dialogReturnSale.show()
    }

    private fun imprimirRecibosTotalizar(
        type: Int,
        recibo: recibos?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        try {
            if (recibo != null) {
                datosImprimirRecibosTotal(type, recibo, QuickContext, ptype, cantidadImpresiones)
            } else {
                Toast.makeText(
                    QuickContext,
                    "Aun le falta terminar de hacer la factura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error PrinterFunctions", e.message!!)
        }
    }

    //TODO imprimir REIMPRIMIR RECIBOS
    private fun getPrintReimpReciboTotal(idVenta: String, referencia: String): String {
        var send = ""

        val realm1 = Realm.getDefaultInstance()
        val result = realm1.where(receipts::class.java).equalTo("customer_id", idVenta)
            .equalTo("reference", referencia).findAll()
        Log.d("recibosresult", result.toString() + "")
        if (result.isEmpty()) {
            send = "No hay recibos emitidos"
        } else {
            val salesList1: List<receipts> =
                realm1.where(receipts::class.java).equalTo("customer_id", idVenta)
                    .equalTo("reference", referencia).findAll()

            Log.d("getReference", salesList1[0].reference)

            val recibos =
                realm1.where(recibos::class.java).equalTo("numeration", salesList1[0].numeration)
                    .findFirst()

            Log.d("getNumeration", recibos!!.numeration + "")

            val numeroReferenciaReceipts = salesList1[0].reference
            val numeracionReceipts = salesList1[0].numeration
            val pagadoReceipts = salesList1[0].montoCanceladoPorFactura
            val pagadoSReceipts = String.format("%,.2f", pagadoReceipts)

            val total = recibos.total
            val totalS = String.format("%,.2f", total)

            val restante = salesList1[0].porPagarReceipts
            val restanteS = String.format("%,.2f", restante)

            val totalAbonos = salesList1[0].balance
            val totalAbonosS = String.format("%,.2f", totalAbonos)

            send += """
                ${
                padRight(
                    "# Referencia:",
                    20.0
                )
            }${padRight(numeroReferenciaReceipts, 20.0)}
                ${padRight("# Factura:", 30.0)}${
                padRight(
                    numeracionReceipts,
                    20.0
                )
            }
                ${
                padRight(
                    "Total Recibo:",
                    20.0
                )
            }${
                padRight(
                    doubleToString1(
                        pagadoReceipts
                    ), 20.0
                )
            }
                ${
                padRight(
                    "Monto total:",
                    30.0
                )
            }${
                padRight(
                    doubleToString1(
                        total
                    ), 20.0
                )
            }
                ${
                padRight(
                    "Total en abonos:",
                    20.0
                )
            }${
                padRight(
                    doubleToString1(
                        totalAbonos
                    ), 20.0
                )
            }
                ${
                padRight(
                    "Total restante:",
                    25.0
                )
            }${
                padRight(
                    doubleToString1(
                        restante
                    ), 20.0
                )
            }
                
                """.trimIndent()

            /*   send += String.format("%.50s  %20s ", numeroReferenciaReceipts, numeracionReceipts) + "\r\n" +
                    String.format("%-12s", Functions.doubleToString1(total)) + "\r\n" +
                    String.format("%s  %20s ", Functions.doubleToString1(pagadoReceipts), Functions.doubleToString1(restanteS)) + "\r\n";*/
            send += "------------------------------------------------\r\n"

            Log.d("FACTPRODTODFAC", send + "")


            realm1.close()
        }
        return send
    }

    fun datosReimpRecibosTotal(
        type: Int,
        sale: receipts,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime = sdf.format(Date())

        val impresiones1 = cantidadImpresiones.toInt()

        for (i in 1..impresiones1) {
            Log.d("impresiones", "cantidadImpresion$impresiones1")

            var billptype = ""
            var preview = ""

            val realm = Realm.getDefaultInstance()
            val clientes =
                realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
            val sysconf = realm.where(Sysconf::class.java).findFirst()


            // receipts receipts = realm.where(receipts.class).equalTo("customer_id", sale.getCustomer_id()).findFirst();
            val referencia = sale.reference

            val sysNombre = sysconf!!.name
            val sysNombreNegocio = sysconf.business_name
            val sysDireccion = sysconf.direction
            val sysIdentificacion = sysconf.identification
            val sysTelefono = sysconf.phone
            val sysCorreo = sysconf.email

            val nombreCliente = clientes!!.fantasyName
            val fecha =  /*sale.getDate()*/currentDateandTime
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
            val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!


            if (ptype == 1) {
                billptype = "R e c i b o s"
            }

            if (prefList == "1") {
                val bill = """! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
! U1 LMARGIN 185
${String.format("%s", billptype)}
! U1 LMARGIN 0
! U1 SETLP 5 0 24
$sysNombre
! U1 SETLP 5 1 35
$sysNombreNegocio
! U1 SETLP 5 0 24
$sysDireccion
Cedula $sysIdentificacion  Tel. $sysTelefono
E-mail $sysCorreo
! U1 SETLP 7 0 14

Cliente: $nombreCliente
Fecha: $fecha
! U1 LMARGIN 0
! U1 SETSP 0

------------------------------------------------
! U1 SETLP 7 0 10
${getPrintReimpReciboTotal(sale.customer_id, referencia)}


Notas: $totalNotasRecibos


Muchas Gracias por preferirnos, un placer atenderlo
Mantenga el documento para reclamos .

 

 

 
 """
                val intent2 = Intent(PrinterService.BROADCAST_CLASS)
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
                intent2.putExtra("bill_to_print", bill)
                QuickContext.sendBroadcast(intent2)
                Log.d("imprimeZebraProf", bill)
            } else if (prefList == "2") {
                preview += Html.fromHtml("<h1>").toString() + String.format(
                    "%s",
                    billptype
                ) + Html.fromHtml("</h1><br/><br/><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + sysNombre + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + sysDireccion + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + Html.fromHtml(
                    "</h1></center><br/>"
                )
                preview += Html.fromHtml("<h1>").toString() + "E-mail " + sysCorreo + Html.fromHtml(
                    "</h1><br/><br/>"
                )

                preview += Html.fromHtml("<h1>")
                    .toString() + "Cliente: " + nombreCliente + Html.fromHtml("</h1><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Fecha: " + fecha + Html.fromHtml("</h1><br/><br/>")
                //  preview += Html.fromHtml("<h1>") +  "# Referencia          # Factura" + Html.fromHtml("</h1></center><br/>");
                //  preview += Html.fromHtml("<h1>") +  "Monto total" + Html.fromHtml("</h1></center><br/>");
                //   preview += Html.fromHtml("<h1>") +  "Monto Pagado          Monto restante" + Html.fromHtml("</h1></center><br/>");
                preview += Html.fromHtml("<h1>")
                    .toString() + "------------------------------------------------" + Html.fromHtml(
                    "</h1></center><br/>"
                )
                preview += Html.fromHtml("<h1>").toString() + getPrintReimpReciboTotal(
                    sale.customer_id,
                    referencia
                ) + Html.fromHtml("</h1></center><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Notas: " + totalNotasRecibos + Html.fromHtml("</h1></center><br/><br/><br/>")
                preview += Html.fromHtml("<h1>")
                    .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                    "</h1></center><br/>"
                )
                preview += Html.fromHtml("<h1>")
                    .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/><br/>")
            }
            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
            Log.d("imprimeProf", preview)
        }
    }

    @JvmStatic
    fun imprimirReimpRecibosTotal(
        recibo: receipts?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val dialogReturnSale = AlertDialog.Builder(QuickContext)
            .setTitle("Impresión")
            .setMessage("¿Desea realizar la impresión?")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                imprimirReimpRecibosTotalizar(
                    1,
                    recibo,
                    QuickContext,
                    ptype,
                    cantidadImpresiones
                )
            }.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }.create()
        dialogReturnSale.show()
    }

    private fun imprimirReimpRecibosTotalizar(
        type: Int,
        recibo: receipts?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        try {
            if (recibo != null) {
                datosReimpRecibosTotal(type, recibo, QuickContext, ptype, cantidadImpresiones)
            } else {
                Toast.makeText(
                    QuickContext,
                    "Aun le falta terminar de hacer la factura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error PrinterFunctions", e.message!!)
        }
    }

    //TODO imprimir TOTALIZAR PREVENTA
    fun datosImprimirPrevTotal(type: Int, sale: sale, QuickContext: Context, ptype: Int) {
        var billptype = ""
        var preview = ""
        var metodoPagoNombre = ""
        var payment = ""

        val realm = Realm.getDefaultInstance()
        val sysconf = realm.where(Sysconf::class.java).findFirst()
        val clientes = realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
        val invoice = realm.where(invoice::class.java).equalTo("id", sale.invoice_id).findFirst()
        val result = realm.where(Pivot::class.java).equalTo("invoice_id", sale.invoice_id).findAll()
        Log.d("FACTPRODTOD", result.toString() + "")
        val fechayhora = sale.updated_at
        val nombreCliente = sale.customer_name
        val descuentoCliente = clientes!!.fixedDiscount.toDouble()

        val companyCliente = clientes.companyName
        val fantasyCliente = clientes.fantasyName

        val numeracionFactura = invoice!!.numeration
        val metodoPago = invoice.payment_method_id
        totalGrabado_ = doubleToString1(
            invoice.subtotal_taxed.toDouble()
        )
        totalExento_ = doubleToString1(
            invoice.subtotal_exempt.toDouble()
        )
        totalSubtotal_ = doubleToString1(
            invoice.subtotal.toDouble()
        )
        totalDescuento_ = doubleToString1(
            invoice.discount.toDouble()
        )
        totalImpuesto_ = doubleToString1(
            invoice.tax.toDouble()
        )
        totalTotal_ = doubleToString1(
            invoice.total.toDouble()
        )
        totalCancelado_ = doubleToString1(
            invoice.paid.toDouble()
        )
        totalVuelto_ = doubleToString1(
            invoice.changing.toDouble()
        )
        totalNotas_ = invoice.note
        val idUsuario = invoice.user_id

        val usuarios = realm.where(Usuarios::class.java).equalTo("id", idUsuario).findFirst()
        val nombreUsuario = usuarios!!.username

        // VARIABLES SYSCONF
        val sysNombreNegocio = sysconf!!.business_name
        realm.close()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
        val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!

        val condition =
            ("Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                    + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                    + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                    + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                    + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                    + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ")

        if (ptype == 1) {
            billptype = "Orden de Pedido"
        } else if (ptype == 3) {
            billptype = "Comprobante Provisional"
        }

        if (metodoPago == "1") {
            metodoPagoNombre = "Contado"
        } else if (metodoPago == "2") {
            metodoPagoNombre = "Credito"
        }

        if (prefList == "1") {
            val bill = ("""
    ! U1 JOURNAl
    ! U1 SETLP 0 0 0
    
    ! U1 SETLP 5 3 70
    ! U1 LMARGIN 0
    ${String.format("%s", billptype)}
    ! U1 SETLP 5 0 24
    
    N# Factura: $numeracionFactura
    Factura de: $metodoPagoNombre
    ! U1 SETLP 5 0 24
    
    
    $sysNombreNegocio
    Fecha y hora: $fechayhora
    Vendedor:  $nombreUsuario
    Razon Social: $companyCliente
    ${if (!nombreCliente.isEmpty()) "A nombre de: $nombreCliente\r\n" else ""}Nombre fantasia: $fantasyCliente
    ! U1 LMARGIN 0
    ! U1 SETSP 0
    
    Descripcion           Codigo
    Cantidad      Precio       P.Sug       Total
    Tipo     
    - - - - - - - - - - - - - - - - - - - - - - - -
    ! U1 SETLP 7 0 10
    ${getPrintPrevTotal(sale.invoice_id)}
    ${String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_)}
    ${String.format("%20s %-20s", "Subtotal Exento", totalExento_)}
    ${String.format("%20s %-20s", "Subtotal", totalSubtotal_)}
    ${String.format("%20s %-20s", "Descuento", totalDescuento_)}
    ${String.format("%20s %-20s", "IVA", totalImpuesto_)}
    ! U1 SETLP 5 3 70
    ${String.format("%20s %-20s", "Total", totalTotal_)}
    ! U1 SETLP 7 0 10
    
    """.trimIndent() +
                    (if (metodoPago === "1") ("""
     ${
                        String.format(
                            "%20s %-20s",
                            "Cancelado con",
                            totalCancelado
                        )
                    }
     ${String.format("%20s %-20s", "Cambio", totalVuelto)}
     
     """.trimIndent()) else "\r\n"
                            ) + "\r\n" +
                    "\r\n\n" + "Notas: " + totalNotas_ + "\r\n" +
                    (if (descuentoCliente > 0) "Se le aplico un $descuentoCliente%  de descuento\r\n" else "" + "\r\n") +
                    "! U1 SETLP 5 0 14\r\n" +
                    "\r\n\n" +
                    "Recibo conforme ____________________________" + "\r\n" + "\r\n" + "\r\n" +
                    "Cedula                  ____________________________" + "\r\n" +
                    "\r\n\n" +
                    (if (metodoPago === "2") ("""
     Firma y Cedula __________________________
     ! U1 SETLP 0 0 6
     
     
     $condition
     
     """.trimIndent()) else "\r\n"
                            ) + "\r\n" +
                    "\r\n\n" +
                    (if (ptype == 3)
                        ("""
                    
                    
                    Este comprobante no puede ser utilizado para fines
                    tributarios, por lo cual no se permitirá
                    su uso para respaldo de créditos o gastos
                    
                    
                    """.trimIndent())
                    else
                        "\r\n"
                            ) + "\r\n" +
                    "! U1 SETLP 5 0 14\r\n\n" +
                    "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                    "Mantenga el documento para reclamos ." + "\r\n" + "\r\n" +
                    " Autorizada mediante resolución N DGT-R-48-2016\r\n" +
                    "del 7 de octubre de 2016.\r\n" +
                    " \n\n" +
                    " \n\n" +
                    " \n ")
            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", bill)
            QuickContext.sendBroadcast(intent2)
            Log.d("imprimeZebra", bill)


            if (ptype == 1) {
                handler = Handler()
                runnable = Runnable {
                    val intent = Intent(QuickContext, MenuPrincipal::class.java)
                    QuickContext.startActivity(intent)
                }
            } else if (ptype == 3) {
                handler = Handler()
                runnable = Runnable {
                    val intent = Intent(QuickContext, MenuPrincipal::class.java)
                    QuickContext.startActivity(intent)
                }
            }

            handler!!.removeCallbacks(runnable!!)
            handler!!.postDelayed(runnable!!, 500)
        } else if (prefList == "2") {
            when (metodoPago) {
                "1" -> {
                    payment = "Contado"
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%s",
                        billptype
                    ) + Html.fromHtml("</h1><br/><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Factura de: " + payment + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + getPrintPrevTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Subtotal Gravado",
                        totalGrabado_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Subtotal Exento",
                        totalExento_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Subtotal",
                        totalSubtotal_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Descuento",
                        totalDescuento_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "IVA",
                        totalImpuesto_
                    ) + Html.fromHtml("</h1></center><br/><br/><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Total",
                        totalTotal_
                    ) + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Cancelado con",
                        totalCancelado_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Cambio",
                        totalVuelto_
                    ) + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Recibo conforme ____________________________" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/>")

                    if (ptype == 3) {
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "tributarios, por lo cual no se permitira" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "su uso para respaldo de créditos o gastos." + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                    }

                    preview += Html.fromHtml("<h1>")
                        .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Autorizada mediante resolución Nº DGT-R-48-2016" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/>")
                }

                "2" -> {
                    payment = "Credito"
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%s",
                        billptype
                    ) + Html.fromHtml("</h1><br/><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Factura de: " + payment + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>")

                    preview += Html.fromHtml("<h1>")
                        .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + getPrintPrevTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Subtotal Gravado",
                        totalGrabado_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Subtotal Exento",
                        totalExento_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Subtotal",
                        totalSubtotal_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Descuento",
                        totalDescuento_
                    ) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "IVA",
                        totalImpuesto_
                    ) + Html.fromHtml("</h1></center><br/><br/><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Total",
                        totalTotal_
                    ) + Html.fromHtml("</h1></center><br/><br/>")

                    /*  preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Cancelado con", totalCancelado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Cambio", totalVuelto_) + Html.fromHtml("</h1></center><br/><br/>");*/
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Recibo conforme ____________________________" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/>")

                    preview += Html.fromHtml("<h1>")
                        .toString() + condition + Html.fromHtml("</h1></center><br/>")

                    if (ptype == 3) {
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "tributarios, por lo cual no se permitira" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "su uso para respaldo de créditos o gastos." + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                    }

                    preview += Html.fromHtml("<h1>")
                        .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Autorizada mediante resolución Nº DGT-R-48-2016" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/>")
                }
            }
            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
            Log.d("imprime", preview)

            if (ptype == 1) {
                handler = Handler()
                runnable = Runnable {
                    val intent = Intent(QuickContext, MenuPrincipal::class.java)
                    QuickContext.startActivity(intent)
                }
            } else if (ptype == 3) {
                handler = Handler()
                runnable = Runnable {
                    val intent = Intent(QuickContext, MenuPrincipal::class.java)
                    QuickContext.startActivity(intent)
                }
            }

            handler!!.removeCallbacks(runnable!!)
            handler!!.postDelayed(runnable!!, 500)
        }
        totalGrabado_ = ""
        totalExento_ = ""
        totalSubtotal_ = ""
        totalDescuento_ = ""
        totalImpuesto_ = ""
        totalTotal_ = ""
        totalCancelado_ = ""
        totalVuelto_ = ""
        totalNotas_ = ""
    }

    fun datosImprimirProformaTotal(type: Int, sale: sale, QuickContext: Context, ptype: Int) {
        var preview = ""
        var billptype = ""
        val realm = Realm.getDefaultInstance()
        val sysconf = realm.where(Sysconf::class.java).findFirst()
        val clientes = realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
        val invoice = realm.where(invoice::class.java).equalTo("id", sale.invoice_id).findFirst()
        val result = realm.where(Pivot::class.java).equalTo("invoice_id", sale.invoice_id).findAll()
        Log.d("FACTPRODTOD", result.toString() + "")
        val fechayhora = sale.updated_at
        val nombreCliente = sale.customer_name
        val descuentoCliente = clientes!!.fixedDiscount.toDouble()

        val companyCliente = clientes.companyName
        val fantasyCliente = clientes.fantasyName

        val metodoPago = invoice!!.payment_method_id
        totalGrabado_ = doubleToString1(
            invoice.subtotal_taxed.toDouble()
        )
        totalExento_ = doubleToString1(
            invoice.subtotal_exempt.toDouble()
        )
        totalSubtotal_ = doubleToString1(
            invoice.subtotal.toDouble()
        )
        totalDescuento_ = doubleToString1(
            invoice.discount.toDouble()
        )
        totalImpuesto_ = doubleToString1(
            invoice.tax.toDouble()
        )
        totalTotal_ = doubleToString1(
            invoice.total.toDouble()
        )
        totalCancelado_ = doubleToString1(
            invoice.paid.toDouble()
        )
        totalVuelto_ = doubleToString1(
            invoice.changing.toDouble()
        )
        totalNotas_ = invoice.note
        val idUsuario = invoice.user_id

        val usuarios = realm.where(Usuarios::class.java).equalTo("id", idUsuario).findFirst()
        val nombreUsuario = usuarios!!.username

        // VARIABLES SYSCONF
        val sysNombreNegocio = sysconf!!.business_name
        realm.close()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
        val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!

        val condition =
            ("Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                    + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                    + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                    + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                    + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                    + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ")

        if (ptype == 1) {
            billptype = "P e d i d o"
        } else if (ptype == 2) {
            billptype = "P r o f o r m a"
        } else if (ptype == 3) {
            billptype = "F a c t u r a"
        }


        if (prefList == "1") {
            val bill = ("""
    ! U1 JOURNAl
    ! U1 SETLP 0 0 0
    
    ! U1 SETLP 5 0 24
    
    Cliente: $nombreCliente
    Razon Social: $companyCliente
    ${if (!nombreCliente.isEmpty()) "A nombre de: $nombreCliente\r\n" else ""}! U1 LMARGIN 0
    Nombre fantasia: $fantasyCliente
    ! U1 LMARGIN 0
    ! U1 SETSP 0
    
    Descripcion           Codigo
    Cantidad      Precio       P.Sug       Total
    Tipo     
    - - - - - - - - - - - - - - - - - - - - - - - -
    ! U1 SETLP 7 0 10
    ${getPrintPrevTotal(sale.invoice_id)}
    ! U1 SETLP 5 3 70
    ${String.format("%20s %-20s", "Total", totalTotal_)}
    
    
    Notas: $totalNotas_
    ${if (descuentoCliente > 0) "Se le aplico un $descuentoCliente%  de descuento\r\n" else "" + "\r\n"}! U1 SETLP 5 0 14
    
    
    Recibo conforme ____________________________
    
    
    Cedula                  ____________________________
    
    
    
    """.trimIndent() +
                    (if (metodoPago === "2") ("""
     Firma y Cedula __________________________
     ! U1 SETLP 0 0 6
     
     
     $condition
     
     """.trimIndent()) else "\r\n"
                            ) + "\r\n" +
                    "! U1 SETLP 5 0 14\r\n\n" +
                    "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                    "Mantenga el documento para reclamos ." + "\r\n" + "\r\n" +
                    " \n\n" +
                    " \n\n" +
                    " \n ")
            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", bill)
            QuickContext.sendBroadcast(intent2)
            Log.d("imprimeZebraProf", bill)

            if (ptype == 2) {
                handler = Handler()
                runnable = Runnable {
                    val intent = Intent(QuickContext, MenuPrincipal::class.java)
                    QuickContext.startActivity(intent)
                }
            }
            handler!!.removeCallbacks(runnable!!)
            handler!!.postDelayed(runnable!!, 500)
        } else if (prefList == "2") {
            when (metodoPago) {
                "1" -> {
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>")

                    preview += Html.fromHtml("<h1>")
                        .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + getPrintPrevTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Total",
                        totalTotal_
                    ) + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Recibo conforme ____________________________" + Html.fromHtml(
                        "</h1></center><br/><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/><br/>")
                }

                "2" -> {
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>")

                    preview += Html.fromHtml("<h1>")
                        .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + getPrintPrevTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                    preview += Html.fromHtml("<h1>").toString() + String.format(
                        "%20s %-20s",
                        "Total",
                        totalTotal_
                    ) + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Recibo conforme ____________________________" + Html.fromHtml(
                        "</h1></center><br/><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Firma y Cedula __________________________" + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + condition + Html.fromHtml("</h1></center><br/><br/>")
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                        "</h1></center><br/>"
                    )
                    preview += Html.fromHtml("<h1>")
                        .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/><br/>")
                }
            }
            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
            Log.d("imprimeProf", preview)

            if (ptype == 2) {
                handler = Handler()
                runnable = Runnable {
                    val intent = Intent(QuickContext, MenuPrincipal::class.java)
                    QuickContext.startActivity(intent)
                }
            }
            handler!!.removeCallbacks(runnable!!)
            handler!!.postDelayed(runnable!!, 500)
        }
        totalGrabado_ = ""
        totalExento_ = ""
        totalSubtotal_ = ""
        totalDescuento_ = ""
        totalImpuesto_ = ""
        totalTotal_ = ""
        totalCancelado_ = ""
        totalVuelto_ = ""
        totalNotas_ = ""
    }

    @JvmStatic
    fun imprimirFacturaPrevTotal(saleB: sale?, QuickContext: Context, ptype: Int) {
        val dialogReturnSale = AlertDialog.Builder(QuickContext)
            .setTitle("Impresión")
            .setMessage("¿Desea realizar la impresión?")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                imprimirTotalizarPrev(
                    1,
                    saleB,
                    QuickContext,
                    ptype
                )
            }.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }.create()
        dialogReturnSale.show()
    }

    //TODO imprimir TOTALIZAR PROFORMA
    @JvmStatic
    fun imprimirFacturaProformaTotal(saleB: sale?, QuickContext: Context, ptype: Int) {
        val dialogReturnSale = AlertDialog.Builder(QuickContext)
            .setTitle("Impresión")
            .setMessage("¿Desea realizar la impresión?")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                imprimirTotalizarProform(
                    1,
                    saleB,
                    QuickContext,
                    ptype
                )
            }.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }.create()
        dialogReturnSale.show()
    }

    private fun imprimirTotalizarPrev(
        type: Int,
        invoices: sale?,
        QuickContext: Context,
        ptype: Int
    ) {
        try {
            if (invoices != null) {
                datosImprimirPrevTotal(type, invoices, QuickContext, ptype)
            } else {
                Toast.makeText(
                    QuickContext,
                    "Aun le falta terminar de hacer la factura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error PrinterFunctions", e.message!!)
        }
    }

    private fun imprimirTotalizarProform(
        type: Int,
        invoices: sale?,
        QuickContext: Context,
        ptype: Int
    ) {
        try {
            if (invoices != null) {
                datosImprimirProformaTotal(type, invoices, QuickContext, ptype)
            } else {
                Toast.makeText(
                    QuickContext,
                    "Aun le falta terminar de hacer la factura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error PrinterFunctions", e.message!!)
        }
    }


    //TODO imprimir TOTALIZAR VENTA DIRECTA
    fun datosImprimirVentaDirectaTotal(
        type: Int,
        sale: sale,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val impresiones1 = cantidadImpresiones.toInt()

        for (i in 1..impresiones1) {
            Log.d("impresiones", "cantidadImpresion$impresiones1")

            var billptype = ""
            var preview = ""
            var metodoPagoNombre = ""
            var payment = ""

            val realm = Realm.getDefaultInstance()
            val sysconf = realm.where(Sysconf::class.java).findFirst()
            val clientes =
                realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
            val invoice =
                realm.where(invoice::class.java).equalTo("id", sale.invoice_id).findFirst()
            val result =
                realm.where(Pivot::class.java).equalTo("invoice_id", sale.invoice_id).findAll()
            Log.d("FACTPRODTOD", result.toString() + "")
            val fechayhora = sale.updated_at
            val nombreCliente = sale.customer_name
            val descuentoCliente = clientes!!.fixedDiscount.toDouble()

            val companyCliente = clientes.companyName
            val fantasyCliente = clientes.fantasyName
            val cardCliente = clientes.card
            val telefonoCliente = clientes.phone


            val key = invoice!!.key
            val numConsecutivo = invoice.consecutive_number
            val tipoFactura = invoice.type
            val numeracionFactura = invoice.numeration
            val metodoPago = invoice.payment_method_id
            totalGrabado_ = doubleToString1(
                invoice.subtotal_taxed.toDouble()
            )
            totalExento_ = doubleToString1(
                invoice.subtotal_exempt.toDouble()
            )
            totalSubtotal_ = doubleToString1(
                invoice.subtotal.toDouble()
            )
            totalDescuento_ = doubleToString1(
                invoice.discount.toDouble()
            )
            totalImpuesto_ = doubleToString1(
                invoice.tax.toDouble()
            )
            totalTotal_ = doubleToString1(
                invoice.total.toDouble()
            )
            totalCancelado_ = doubleToString1(
                invoice.paid.toDouble()
            )
            totalVuelto_ = doubleToString1(
                invoice.changing.toDouble()
            )
            totalNotas_ = invoice.note
            val idUsuario = invoice.user_id

            val usuarios = realm.where(Usuarios::class.java).equalTo("id", idUsuario).findFirst()
            val nombreUsuario = usuarios!!.username

            // VARIABLES SYSCONF
            val sysNombre = sysconf!!.name
            val sysNombreNegocio = sysconf.business_name
            val sysDireccion = sysconf.direction
            val sysIdentificacion = sysconf.identification
            val sysTelefono = sysconf.phone
            val sysCorreo = sysconf.email

            realm.close()

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
            val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!

            val condition =
                ("Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                        + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                        + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                        + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                        + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                        + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ")
            // TODO VENTA DIRECTA
            if (tipoFactura == "01") {
                billptype = "Factura electronica"
            } else if (tipoFactura == "04") {
                billptype = "Tiquete electronico"
            }



            if (metodoPago == "1") {
                metodoPagoNombre = "Contado"
            } else if (metodoPago == "2") {
                metodoPagoNombre = "Credito"
            }

            if (prefList == "1") {
                val bill = ("""! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
! U1 LMARGIN 0
${String.format("%s", billptype)}
! U1 SETLP 7 0 14
------------------------------------------------


! U1 SETLP 5 1 35
$sysNombreNegocio
! U1 SETLP 7 0 14
------------------------------------------------

! U1 SETLP 7 0 14
$sysNombre
Cedula Juridica: $sysIdentificacion
$sysDireccion
Tel. $sysTelefono
Correo Electronico: $sysCorreo
------------------------------------------------

! U1 SETLP 7 0 14

# Factura: $numeracionFactura  $metodoPagoNombre
Consec DGT: #$numConsecutivo
Clave DGT: #$key
------------------------------------------------

! U1 LMARGIN 0
! U1 SETLP 7 0 14
Fecha y hora: $fechayhora
Vendedor:  $nombreUsuario
Razon Social: $companyCliente
Nombre fantasia: $fantasyCliente
Cedula: $cardCliente 
# Telefono: $telefonoCliente
------------------------------------------------

! U1 LMARGIN 0
! U1 SETSP 0

Descripcion           Codigo
Cantidad      Precio       P.Sug       Total
Tipo     
- - - - - - - - - - - - - - - - - - - - - - - -
! U1 SETLP 7 0 10
${getPrintPrevTotal(sale.invoice_id)}
${String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_)}
${String.format("%20s %-20s", "Subtotal Exento", totalExento_)}
${String.format("%20s %-20s", "Subtotal", totalSubtotal_)}
${String.format("%20s %-20s", "Descuento", totalDescuento_)}
${String.format("%20s %-20s", "IVA", totalImpuesto_)}
! U1 SETLP 5 3 70
${
                    String.format(
                        "%20s %-20s",
                        "Total a pagar",
                        totalTotal_
                    )
                }! U1 SETLP 7 0 10

""" +
                        (if (metodoPago === "1") ("""
     ${
                            String.format(
                                "%20s %-20s",
                                "Cancelado con",
                                totalCancelado
                            )
                        }
     ${String.format("%20s %-20s", "Cambio", totalVuelto)}
     """.trimIndent()) else "\r\n"
                                ) +
                        "Notas: " + totalNotas_ + "\r\n" + "\r\n" +
                        (if (descuentoCliente > 0) "Se le aplico un $descuentoCliente%  de descuento" else "" + "\r\n") +
                        "! U1 SETLP 5 0 14\r\n" +
                        "Firma cliente ____________________________" + "\r\n" + "\r\n" + "\r\n" +
                        "Cedula ____________________________" + "\r\n" +
                        (if (metodoPago === "2") "! U1 SETLP 0 0 6\r\n\r\n$condition" else "\r\n"
                                ) + "\r\n" +
                        (if (ptype == 3)
                            ("""
                        
                        Este comprobante no puede ser utilizado para fines
                        tributarios, por lo cual no se permitira
                        su uso para respaldo de creditos o gastos
                        
                        
                        """.trimIndent())
                        else
                            "\r\n"
                                ) + "\r\n" +
                        "! U1 SETLP 5 0 14\r\n" +
                        " Autorizada mediante resolucion N DGT-R-48-2016\r\n" +
                        "del 7 de octubre de 2016.\r\n" + "\r\n" +
                        "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                        "Mantenga el documento para reclamos ." + "\r\n" +
                        " \n\n" +
                        " \n\n" +
                        " \n ")
                val intent2 = Intent(PrinterService.BROADCAST_CLASS)
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
                intent2.putExtra("bill_to_print", bill)
                QuickContext.sendBroadcast(intent2)
                Log.d("imprimeZebra", bill)
            } else if (prefList == "2") {
                when (metodoPago) {
                    "1" -> {
                        payment = "Contado"
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%s",
                            billptype
                        ) + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombre + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysDireccion + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Clave DGT: #" + key + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + getPrintPrevTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Gravado",
                            totalGrabado_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Exento",
                            totalExento_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal",
                            totalSubtotal_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Descuento",
                            totalDescuento_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "IVA",
                            totalImpuesto_
                        ) + Html.fromHtml("</h1></center><br/><br/><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Total a pagar",
                            totalTotal_
                        ) + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Cancelado con",
                            totalCancelado_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Cambio",
                            totalVuelto_
                        ) + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Firma cliente ____________________________" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>")

                        if (ptype == 3) {
                            preview += Html.fromHtml("<h1>")
                                .toString() + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml(
                                "</h1></center><br/>"
                            )
                            preview += Html.fromHtml("<h1>")
                                .toString() + "tributarios, por lo cual no se permitira" + Html.fromHtml(
                                "</h1></center><br/>"
                            )
                            preview += Html.fromHtml("<h1>")
                                .toString() + "su uso para respaldo de creditos o gastos." + Html.fromHtml(
                                "</h1></center><br/><br/>"
                            )
                        }

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Autorizada mediante resolucion Nº DGT-R-48-2016" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/><br/>")

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/>")
                    }

                    "2" -> {
                        payment = "Credito"

                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%s",
                            billptype
                        ) + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysNombre + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + sysDireccion + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Clave DGT: #" + key + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "------------------------------------------------" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Tipo " + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + getPrintPrevTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Gravado",
                            totalGrabado_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal Exento",
                            totalExento_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Subtotal",
                            totalSubtotal_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Descuento",
                            totalDescuento_
                        ) + Html.fromHtml("</h1></center><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "IVA",
                            totalImpuesto_
                        ) + Html.fromHtml("</h1></center><br/><br/><br/>")
                        preview += Html.fromHtml("<h1>").toString() + String.format(
                            "%20s %-20s",
                            "Total a pagar",
                            totalTotal_
                        ) + Html.fromHtml("</h1></center><br/><br/>")

                        preview += Html.fromHtml("<h1>")
                            .toString() + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Firma cliente ____________________________" + Html.fromHtml(
                            "</h1></center><br/><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>")

                        preview += Html.fromHtml("<h1>")
                            .toString() + condition + Html.fromHtml("</h1></center><br/>")

                        if (ptype == 3) {
                            preview += Html.fromHtml("<h1>")
                                .toString() + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml(
                                "</h1></center><br/>"
                            )
                            preview += Html.fromHtml("<h1>")
                                .toString() + "tributarios, por lo cual no se permitira" + Html.fromHtml(
                                "</h1></center><br/>"
                            )
                            preview += Html.fromHtml("<h1>")
                                .toString() + "su uso para respaldo de creditos o gastos." + Html.fromHtml(
                                "</h1></center><br/><br/>"
                            )
                        }
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Autorizada mediante resolucion Nº DGT-R-48-2016" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/><br/>")
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml(
                            "</h1></center><br/>"
                        )
                        preview += Html.fromHtml("<h1>")
                            .toString() + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/>")
                    }
                }
                val intent2 = Intent(PrinterService.BROADCAST_CLASS)
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
                intent2.putExtra("bill_to_print", preview)
                QuickContext.sendBroadcast(intent2)
                Log.d("imprime", preview)
            }
        }

        if (ptype == 1) {
            handler = Handler()
            runnable = Runnable {
                val intent = Intent(QuickContext, MenuPrincipal::class.java)
                QuickContext.startActivity(intent)
            }
        } else if (ptype == 3) {
            handler = Handler()
            runnable = Runnable {
                val intent = Intent(QuickContext, MenuPrincipal::class.java)
                QuickContext.startActivity(intent)
            }
        }

        handler!!.removeCallbacks(runnable!!)
        handler!!.postDelayed(runnable!!, 500)



        totalGrabado_ = ""
        totalExento_ = ""
        totalSubtotal_ = ""
        totalDescuento_ = ""
        totalImpuesto_ = ""
        totalTotal_ = ""
        totalCancelado_ = ""
        totalVuelto_ = ""
        totalNotas_ = ""
    }

    private fun imprimirTotalizarVentaDirect(
        type: Int,
        invoices: sale?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        try {
            if (invoices != null) {
                datosImprimirVentaDirectaTotal(
                    type,
                    invoices,
                    QuickContext,
                    ptype,
                    cantidadImpresiones
                )
            } else {
                Toast.makeText(
                    QuickContext,
                    "Aun le falta terminar de hacer la factura",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error PrinterFunctions", e.message!!)
        }
    }

    @JvmStatic
    fun imprimirFacturaVentaDirectaTotal(
        saleB: sale?,
        QuickContext: Context,
        ptype: Int,
        cantidadImpresiones: String
    ) {
        val dialogReturnSale = AlertDialog.Builder(QuickContext)
            .setTitle("Impresión")
            .setMessage("¿Desea realizar la impresión?")
            .setPositiveButton(
                "OK"
            ) { dialog, which ->
                imprimirTotalizarVentaDirect(
                    1,
                    saleB,
                    QuickContext,
                    ptype,
                    cantidadImpresiones
                )
            }.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }.create()
        dialogReturnSale.show()
    }

    private fun getPrintPrevTotal(idVenta: String): String {
        var send = " "

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var amountsinbonus = 0.0
        var amountConBonus = 0.0
        val realm = Realm.getDefaultInstance()
        val result: RealmResults<Pivot> =
            realm.where(Pivot::class.java).equalTo("invoice_id", idVenta).equalTo("devuelvo", 0)
                .findAll()

        if (result.isEmpty()) {
            send = "No hay invoice emitidas"
        } else {
            // printSalesCashTotal= 0.0;
            for (i in result.indices) {
                val salesList1: List<Pivot> =
                    realm.where(Pivot::class.java).equalTo("invoice_id", idVenta).findAll()

                val esBonus = salesList1[i].bonus

                val producto =
                    realm.where(Productos::class.java).equalTo("id", salesList1[i].product_id)
                        .findFirst()
                val precioSugerido = producto!!.suggested.toDouble()
                val description = producto.description
                val byteText = description.toByteArray(Charset.forName("UTF-8"))

                var description1: String? = null
                try {
                    description1 = String(byteText, charset("UTF-8"))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                val barcode = producto.barcode
                val cant = salesList1[i].amount.toDouble()
                var sugerido = 0.0


                val impuesto = producto.iva

                var nombreTipo: String? = null

                if (impuesto == 0.0) {
                    nombreTipo = "Exento"
                    precio = salesList1[i].price.toDouble()
                } else {
                    nombreTipo = "Gravado"
                    precio = salesList1[i].price.toDouble() / 1.13
                }


                sugerido = (precio) * (precioSugerido / 100) + (precio)

                if (esBonus == 1) {
                    amountsinbonus = salesList1[i].amountSinBonus

                    val amount = salesList1[i].amount
                    amountConBonus = amount.toDouble()

                    val totalAmountBonus = amountConBonus - amountsinbonus

                    send += """
                        ${String.format("%s  %.24s ", description1, barcode)}
                        ${
                        String.format(
                            "%-12s %-10s %-12s %.10s",
                            totalAmountBonus,
                            "0.0",
                            "0.0",
                            "0.0"
                        )
                    }
                        ${String.format("%.10s", nombreTipo)}
                        
                        """.trimIndent()
                    send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n"

                    send += """
                        ${String.format("%s  %.24s ", description1, barcode)}
                        ${
                        String.format(
                            "%-12s %-10s %-12s %.10s",
                            amountsinbonus,
                            doubleToString1(
                                precio
                            ),
                            doubleToString1(sugerido),
                            doubleToString1(
                                amountsinbonus * precio
                            )
                        )
                    }
                        ${String.format("%.10s", nombreTipo)}
                        
                        """.trimIndent()
                    send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n"
                } else {
                    send += """
                        ${String.format("%s  %.24s ", description1, barcode)}
                        ${
                        String.format(
                            "%-12s %-10s %-12s %.10s",
                            cant,
                            doubleToString1(
                                precio
                            ),
                            doubleToString1(sugerido),
                            doubleToString1(cant * precio)
                        )
                    }
                        ${String.format("%.10s", nombreTipo)}
                        
                        """.trimIndent()
                    send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n"
                }

                Log.d("FACTPRODTODFAC", send + "")
            }
        }
        return send
    }


    //TODO imprimir PRODUCTOS SELECC
    fun datosImprimirProductosSelecClientes(sale: sale, QuickContext: Context) {
        val realm = Realm.getDefaultInstance()
        val sysconf = realm.where(Sysconf::class.java).findFirst()
        val clientes = realm.where(Clientes::class.java).equalTo("id", sale.customer_id).findFirst()
        val invoice = realm.where(invoice::class.java).equalTo("id", sale.invoice_id).findFirst()
        val result: RealmResults<Pivot> =
            realm.where(Pivot::class.java).equalTo("invoice_id", sale.invoice_id)
                .equalTo("devuelvo", 0).findAll()

        Log.d("FACTPRODTOD", result.toString() + "")

        // VARIABLES CLIENTES
        val companyCliente = clientes!!.companyName

        // VARIABLES FACTURA
        val numeracionFactura = invoice!!.numeration


        // VARIABLES SYSCONF
        val sysNombre = sysconf!!.name
        val sysNombreNegocio = sysconf.business_name
        val sysDireccion = sysconf.direction
        realm.close()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
        val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!
        var preview = ""
        val billptype = "O r d e n  d e  C o m p r a"

        if (prefList == "1") {
            val bill = """! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
${String.format("%s", billptype)}
! U1 SETLP 7 0 14

N# Factura: $numeracionFactura
! U1 SETLP 5 1 35
$sysNombre
! U1 SETLP 5 1 35
$sysNombreNegocio
! U1 SETLP 5 0 24
$sysDireccion
! U1 SETLP 7 0 14

Razon Social: $companyCliente
! U1 LMARGIN 0
! U1 SETSP 0


Descripcion           Codigo
Cantidad      Precio       P.Sug       Total
Tipo     
------------------------------------------------
! U1 SETLP 7 0 10
${getPrintDistTotal(sale.invoice_id)}
! U1 SETLP 5 3 70
 

 

 
 """

            Log.d("JD", bill)
            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", bill)
            QuickContext.sendBroadcast(intent2)
        } else if (prefList == "2") {
            preview += Html.fromHtml("<h1>").toString() + String.format(
                "%s",
                billptype
            ) + Html.fromHtml("</h1><br/><br/><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/>")
            preview += Html.fromHtml("<h1>").toString() + sysNombre + Html.fromHtml("</h1><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + sysNombreNegocio + Html.fromHtml("</h1><br/>")
            preview += Html.fromHtml("<h1>").toString() + sysDireccion + Html.fromHtml("</h1><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Tipo" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "------------------------------------------------" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + getPrintDistTotal(sale.invoice_id) + Html.fromHtml("</h1></center><br/>")

            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
        }
    }

    @JvmStatic
    fun imprimirProductosDistrSelecCliente(sale: sale, QuickContext: Context) {
        val builder = AlertDialog.Builder(QuickContext)
        builder.setTitle("Impresión de Productos?")
        builder.setMessage("¿Desea imprimir la lista de productos para esta factura?")
        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            datosImprimirProductosSelecClientes(
                sale,
                QuickContext
            )
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    //TODO imprimir ORDEN DE CARGA
    fun datosImprimirOrdenCarga(QuickContext: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
        val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!
        var preview = ""
        val salesCredit = printProductInvoicesPreventa
        val billptype = "Orden de Carga"

        if (prefList == "1") {
            val bill = """! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
${String.format("%s", billptype)}
! U1 SETLP 7 0 10

Usuario: ${getUserName(QuickContext)}
! U1 LMARGIN 0
! U1 SETSP 0


Descripcion     
Precio         Cantidad          Total
------------------------------------------------
! U1 SETLP 7 0 10


$salesCredit

------------------------------------------------


 
 
 
 
 """

            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", bill)
            QuickContext.sendBroadcast(intent2)
        } else if (prefList == "2") {
            preview += Html.fromHtml("<h1>").toString() + String.format(
                "%s",
                billptype
            ) + Html.fromHtml("</h1><br/><br/><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Descripcion " + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Precio         Cantidad          Total" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "------------------------------------------------" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + salesCredit + Html.fromHtml("</h1></center><br/>")

            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
        }
    }

    @JvmStatic
    fun imprimirOrdenCarga(QuickContext: Context) {
        val builder = AlertDialog.Builder(QuickContext)
        builder.setTitle("¿Desea imprimir la lista de orden de carga?")
        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            datosImprimirOrdenCarga(
                QuickContext
            )
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
        //}
    }

    //TODO imprimir LIQUIDACION
    fun datosImprimirLiquidacion(QuickContext: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
        val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!
        var preview = ""
        val ventaContado = printProductInvoicesLiqContado
        val ventaCredito = printProductInvoicesLiqCredito
        val ventaRecibos = printProductRecibosLiq

        val billptype = "L i q u i d a c i o n"

        if (prefList == "1") {
            val bill = """! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
${String.format("%s", billptype)}
! U1 SETLP 7 0 10

Usuario: ${getUserName(QuickContext)}
! U1 LMARGIN 0
! U1 SETLP 7 0 10
Facturas de Contado
! U1 LMARGIN 0
! U1 SETLP 7 0 10
#     Factura           Fecha         Monto
------------------------------------------------
! U1 SETLP 7 0 10


$ventaContado

------------------------------------------------
#     Total ${doubleToString1(printLiqContadoTotal)}


! U1 LMARGIN 0
! U1 SETLP 7 0 10
Facturas de Credito
! U1 LMARGIN 0
! U1 SETLP 7 0 10
#     Factura           Fecha         Monto
------------------------------------------------
! U1 SETLP 7 0 10


$ventaCredito

------------------------------------------------
#     Total ${doubleToString1(printLiqCreditoTotal)}


! U1 LMARGIN 0
! U1 SETLP 7 0 10
Recibos
! U1 LMARGIN 0
! U1 SETLP 7 0 10
#     Recibo           Fecha         Monto
------------------------------------------------
! U1 SETLP 7 0 10


$ventaRecibos

------------------------------------------------
#     Total ${doubleToString1(printLiqRecibosTotal)}


------------------------------------------------
#     Total ${doubleToString1(printLiqContadoTotal + printLiqRecibosTotal /*+ printReceiptsTotal*/)}
 
 
 
 
 """

            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", bill)
            QuickContext.sendBroadcast(intent2)
            Log.d("imprimeLiquidacion", bill)
        } else if (prefList == "2") {
            preview += Html.fromHtml("<h1>").toString() + String.format(
                "%s",
                billptype
            ) + Html.fromHtml("</h1><br/><br/><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Usuario: " + getUserName(QuickContext) + Html.fromHtml("</h1><br/><br/>")

            preview += Html.fromHtml("<h1>")
                .toString() + "Facturas de Contado" + Html.fromHtml("</h1><br/><br/>")

            preview += Html.fromHtml("<h1>")
                .toString() + "#     Factura           Fecha         Monto" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "------------------------------------------------" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + ventaContado + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>").toString() + "#     Total " + doubleToString1(
                printLiqContadoTotal
            ) + Html.fromHtml("</h1><br/><br/><br/>")

            preview += Html.fromHtml("<h1>")
                .toString() + "Facturas de Credito" + Html.fromHtml("</h1><br/><br/>")

            preview += Html.fromHtml("<h1>")
                .toString() + "#     Factura           Fecha         Monto" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "------------------------------------------------" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + ventaCredito + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>").toString() + "#     Total " + doubleToString1(
                printLiqCreditoTotal
            ) + Html.fromHtml("</h1><br/><br/><br/>")

            preview += Html.fromHtml("<h1>")
                .toString() + "Recibos" + Html.fromHtml("</h1><br/><br/>")

            preview += Html.fromHtml("<h1>")
                .toString() + "#     Recibo           Fecha         Monto" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "------------------------------------------------" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + ventaRecibos + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>").toString() + "#     Total " + doubleToString1(
                printLiqRecibosTotal
            ) + Html.fromHtml("</h1><br/><br/><br/>")


            preview += Html.fromHtml("<h1>").toString() + "#     Total " + doubleToString1(
                printLiqContadoTotal + printLiqRecibosTotal /*+ printReceiptsTotal*/
            ) + Html.fromHtml("</h1><br/><br/><br/>")

            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
        }
    }

    @JvmStatic
    fun imprimirLiquidacionMenu(QuickContext: Context) {
        val builder = AlertDialog.Builder(QuickContext)
        builder.setTitle("¿Desea imprimir la lista de liquidación?")
        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            datosImprimirLiquidacion(
                QuickContext
            )
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
        //}
    }

    //TODO imprimir DEVOLUCION
    fun datosImprimirDevolucion(QuickContext: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext)
        val prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra")!!
        var preview = ""

        val salesCredit = printDevolcionProductInvoices
        val billptype = "D e v o l u c i o n"

        if (prefList == "1") {
            val bill = """! U1 JOURNAl
! U1 SETLP 0 0 0

! U1 SETLP 5 3 70
${String.format("%s", billptype)}
! U1 SETLP 7 0 10

Usuario: ${getUserName(QuickContext)}
! U1 LMARGIN 0
! U1 SETSP 0
#        Producto                  
Cantidad         Precio           Total
------------------------------------------------
! U1 SETLP 7 0 10
$salesCredit

------------------------------------------------


------------------------------------------------
 
 
 
 
 """

            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", bill)
            QuickContext.sendBroadcast(intent2)
        } else if (prefList == "2") {
            preview += Html.fromHtml("<h1>").toString() + String.format(
                "%s",
                billptype
            ) + Html.fromHtml("</h1><br/><br/><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "#        Producto" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "Cantidad         Precio           Total" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + "------------------------------------------------" + Html.fromHtml("</h1></center><br/>")
            preview += Html.fromHtml("<h1>")
                .toString() + salesCredit + Html.fromHtml("</h1></center><br/>")

            val intent2 = Intent(PrinterService.BROADCAST_CLASS)
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true")
            intent2.putExtra("bill_to_print", preview)
            QuickContext.sendBroadcast(intent2)
        }
    }

    @JvmStatic
    fun imprimirDevoluciónMenu(QuickContext: Context) {
        val builder = AlertDialog.Builder(QuickContext)
        builder.setTitle("¿Desea imprimir la lista de devolución?")
        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            datosImprimirDevolucion(
                QuickContext
            )
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
        //}
    }

    private val printDevolcionProductInvoices: String
        get() {
            var send = ""

            val sdf = SimpleDateFormat("yyyy-MM-dd")

            val realm = Realm.getDefaultInstance()

            val result = realm.where(Inventario::class.java).notEqualTo("amount", "0")
                .notEqualTo("amount", "0.0").findAll()

            if (result.isEmpty()) {
                send = "No hay devoluciones emitidas"
            } else {
                printSalesCashTotal = 0.0
                for (i in result.indices) {
                    val salesList1: List<Inventario> = realm.where(
                        Inventario::class.java
                    ).notEqualTo("amount", "0").notEqualTo("amount", "0.0").findAll()


                    val factProductoId = salesList1[i].product_id
                    val factAmount = salesList1[i].amount.toDouble()

                    val realm1 = Realm.getDefaultInstance()
                    val producto = realm1.where(Productos::class.java).equalTo("id", factProductoId)
                        .findFirst()
                    val description = producto!!.description
                    val precio = producto.sale_price.toDouble()

                    realm1.close()

                    send += """
                        ${
                        String.format(
                            "%-1s      %.60s",
                            factProductoId,
                            description
                        )
                    }
                        ${
                        String.format(
                            "%-14s   %-12s    %.10s",
                            factAmount,
                            precio,
                            doubleToString1(factAmount * precio)
                        )
                    }
                        
                        """.trimIndent()
                    send += "------------------------------------------------\r\n"
                    Log.d("FACTPRODTODFAC", send + "")
                }
            }
            return send
        }

    private val printProductInvoicesLiqContado: String
        get() {
            var send = ""

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDateandTime = sdf.format(Date())

            val realm = Realm.getDefaultInstance()
            val result =
                realm.where(invoice::class.java).equalTo("payment_method_id", "1")
                    .equalTo("date", currentDateandTime)
                    .equalTo("facturaDePreventa", "VentaDirecta").or()
                    .equalTo("facturaDePreventa", "Distribucion").findAll()

            if (result.isEmpty()) {
                send = "No hay facturas emitidas"
            } else {
                printLiqContadoTotal = 0.0
                for (i in result.indices) {
                    val salesList1: List<invoice> =
                        realm.where(invoice::class.java).equalTo("payment_method_id", "1")
                            .equalTo("date", currentDateandTime)
                            .equalTo("facturaDePreventa", "VentaDirecta").or()
                            .equalTo("facturaDePreventa", "Distribucion").findAll()
                    val pago = salesList1[i].payment_method_id
                    val factNum = salesList1[i].numeration
                    val factFecha = salesList1[i].date


                    val factTotal = salesList1[i].total.toDouble()

                    if (pago == "1") {
                        if (factFecha == currentDateandTime) {
                            send += String.format(
                                "%-5s      %.20s      %-6s",
                                factNum,
                                factFecha,
                                doubleToString1(factTotal)
                            ) + "\r\n"
                            printLiqContadoTotal =
                                printLiqContadoTotal + factTotal

                            Log.d("LiqContado", send + "")
                        }
                    } else {
                    }
                }
            }
            return send
        }

    private val printProductInvoicesLiqCredito: String
        get() {
            var send = ""

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDateandTime = sdf.format(Date())

            val realm = Realm.getDefaultInstance()
            val result =
                realm.where(invoice::class.java).equalTo("payment_method_id", "2")
                    .equalTo("date", currentDateandTime)
                    .equalTo("facturaDePreventa", "VentaDirecta").or()
                    .equalTo("facturaDePreventa", "Distribucion").findAll()

            if (result.isEmpty()) {
                send = "No hay facturas emitidas"
            } else {
                printLiqCreditoTotal = 0.0
                for (i in result.indices) {
                    val salesList1: List<invoice> =
                        realm.where(invoice::class.java).equalTo("payment_method_id", "2")
                            .equalTo("date", currentDateandTime)
                            .equalTo("facturaDePreventa", "VentaDirecta").or()
                            .equalTo("facturaDePreventa", "Distribucion").findAll()

                    val pago = salesList1[i].payment_method_id
                    val factNum = salesList1[i].numeration
                    val factFecha = salesList1[i].date

                    val factTotal = salesList1[i].total.toDouble()

                    if (pago == "2") {
                        if (factFecha == currentDateandTime) {
                            send += String.format(
                                "%-5s      %.20s      %-6s",
                                factNum,
                                factFecha,
                                doubleToString1(factTotal)
                            ) + "\r\n"
                            printLiqCreditoTotal =
                                printLiqCreditoTotal + factTotal

                            Log.d("LiqCredito", send + "")
                        }
                    } else {
                    }
                }
            }
            return send
        }

    private val printProductRecibosLiq: String
        get() {
            var send = ""

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDateandTime = sdf.format(Date())

            val realm = Realm.getDefaultInstance()
            val result =
                realm.where(receipts::class.java).equalTo("date", currentDateandTime)
                    .findAll()

            if (result.isEmpty()) {
                send = "No hay facturas emitidas"
            } else {
                printLiqRecibosTotal = 0.0
                for (i in result.indices) {
                    Log.d("tamaño", result.size.toString() + "")
                    val salesList1: List<receipts> =
                        realm.where(receipts::class.java)
                            .equalTo("date", currentDateandTime).findAll()
                    Log.d("salesList1", salesList1.toString() + "")

                    val numeracion = salesList1[i].numeration
                    val fecha = salesList1[i].date

                    val pagado = salesList1[i].montoCanceladoPorFactura

                    send += String.format(
                        "%-9s  %9s  %9s",
                        numeracion,
                        fecha,
                        doubleToString1(pagado)
                    ) + "\r\n"
                    printLiqRecibosTotal =
                        printLiqRecibosTotal + pagado
                    Log.d(
                        "LiqRecibosTotal",
                        printLiqRecibosTotal.toString() + ""
                    )
                    send += "------------------------------------------------\r\n"
                    Log.d("LiqRecibos", send + "")
                }
            }

            return send
        }

    private val printProductInvoicesPreventa: String
        get() {
            var send = ""

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val currentDateandTime = sdf.format(Date())

            val realm = Realm.getDefaultInstance()
            val result =
                realm.where(invoice::class.java).equalTo("date", currentDateandTime)
                    .equalTo("facturaDePreventa", "Preventa").findAll()

            if (result.isEmpty()) {
                send = "No hay facturas emitidas"
            } else {
                printSalesCashTotal = 0.0
                for (i in result.indices) {
                    val salesList1: List<invoice> =
                        realm.where(invoice::class.java)
                            .equalTo("date", currentDateandTime)
                            .equalTo("facturaDePreventa", "Preventa").findAll()
                    val idFactura = salesList1[i].id

                    val resultPivot =
                        realm.where(Pivot::class.java).equalTo("invoice_id", idFactura)
                            .findAll()

                    for (a in resultPivot.indices) {
                        val pivotList1: List<Pivot> =
                            realm.where(Pivot::class.java).equalTo("invoice_id", idFactura)
                                .findAll()
                        val idPivot = pivotList1[a].product_id
                        val precioPivot = pivotList1[a].price.toDouble()
                        val cantidadPivot = pivotList1[a].amount.toDouble()
                        /*  RealmResults<Productos> resultProducto = realm.where(Productos.class).equalTo("id", idProducto).findAll();
      for (int t = 0; t < resultProducto.size(); t++) {
*/
                        val productoList1 =
                            realm.where(Productos::class.java).equalTo("id", idPivot)
                                .findFirst()
                        val descripcion = productoList1!!.description

                        send += String.format(
                            "%.60s",
                            descripcion
                        ) + "\r\n" + String.format(
                            "%-14s   %-12s    %.10s",
                            precioPivot,
                            cantidadPivot,
                            doubleToString1(cantidadPivot * precioPivot)
                        ) + "\r\n"
                        send += "------------------------------------------------\r\n"

                        /*  }*/
                    }
                }
            }
            Log.d("OrdenCarga", send + "")
            return send
        }

    private fun getUserName(context: Context): String {
        val session = SessionPrefes(context)

        val usuer = session.usuarioPrefs
        Log.d("usuer", usuer)
        val realm = Realm.getDefaultInstance()
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        val nombreUsuario = usuarios!!.username
        realm.close()
        return nombreUsuario
    }
}
