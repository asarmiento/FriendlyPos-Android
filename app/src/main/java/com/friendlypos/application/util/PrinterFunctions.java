package com.friendlypos.application.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.Sysconf;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class PrinterFunctions {

    static Handler handler;
    static Runnable runnable;
    private static double printSalesCashTotal = 0.0;
    private static double printLiqContadoTotal = 0.0;
    private static double printLiqCreditoTotal = 0.0;
    private static double printLiqRecibosTotal = 0.0;
    private static double printRecibosTotal = 0.0;

    static String totalGrabado= "";
    static String totalExento= "";
    static String totalSubtotal= "";
    static String totalDescuento= "";
    static String totalImpuesto= "";
    static String totalTotal = "";
    static String totalCancelado= "";
    static String totalVuelto= "";
    static String totalNotas= "";
    static String totalGrabado_= "";
    static String totalExento_= "";
    static  String totalSubtotal_= "";
    static  String totalDescuento_= "";
    static  String totalImpuesto_= "";
    static  String totalTotal_ = "";
    static  String totalCancelado_= "";
    static  String totalVuelto_= "";
    static  String totalNotas_= "";
    static  String totalNotasRecibos= "";
   static double precio=0.0;

    //TODO imprimir TOTALIZAR DISTRIBUCION

    public static void datosImprimirDistrTotal(int type, sale sale,final Context QuickContext, int ptype, String cantidadImpresiones) {

        int impresiones1 = Integer.parseInt(cantidadImpresiones);

        for(int i =1; i<=impresiones1; i++){

            Log.d("impresiones", "cantidadImpresion" + impresiones1);

        String payment = "";
        String messageC = "";
        String billptype = "";
        String metodoPagoNombre = "";
        String preview = "";

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();

        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).equalTo("devuelvo", 0).findAll();
        Log.d("FACTPRODTOD", result + "");
        // VARIABLES VENTA
        String fechayhora = sale.getUpdated_at();

        // VARIABLES CLIENTES
        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();
        String telefonoCliente = clientes.getPhone();
        double descuentoCliente = Double.parseDouble(clientes.getFixedDiscount());

        // VARIABLES FACTURA
        String fechaFactura = invoice.getDue_date();
        String numeracionFactura = invoice.getNumeration();
        String metodoPago = invoice.getPayment_method_id();
        String key = invoice.getKey();
        String numConsecutivo = invoice.getConsecutive_number();
        String tipoFactura = invoice.getType();

        totalGrabado= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_taxed()));
        totalExento= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_exempt()));
        totalSubtotal= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal()));
        totalDescuento= Functions.doubleToString1(Double.parseDouble(invoice.getDiscount()));
        totalImpuesto= Functions.doubleToString1(Double.parseDouble(invoice.getTax()));
        totalTotal= Functions.doubleToString1(Double.parseDouble(invoice.getTotal()));
        totalCancelado= Functions.doubleToString1(Double.parseDouble(invoice.getPaid()));
        totalVuelto= Functions.doubleToString1(Double.parseDouble(invoice.getChanging()));
        totalNotas= invoice.getNote();
        String idUsuario = invoice.getUser_id();

        Usuarios usuarios = realm.where(Usuarios.class).equalTo("id", idUsuario).findFirst();

        String nombreUsuario = usuarios.getUsername();

        // VARIABLES SYSCONF
        String sysNombre = sysconf.getName();
        String sysNombreNegocio = sysconf.getBusiness_name();
        String sysDireccion = sysconf.getDirection();
        String sysIdentificacion = sysconf.getIdentification();
        String sysTelefono = sysconf.getPhone();
        String sysCorreo = sysconf.getEmail();
        realm.close();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");

        String condition = "Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ";

        if (tipoFactura.equals("01")) {
            billptype = "Factura Electronica";
        }else if(tipoFactura.equals("04")){
            billptype = "Tiquete Electronico";
        }


        if (metodoPago.equals("1")) {
            metodoPagoNombre = "Contado";
        } else if (metodoPago.equals("2")) {
            metodoPagoNombre = "Credito";
        }


        if (prefList.equals("1")){
            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    String.format("%s", billptype) + "\r\n" +
                    "! U1 SETLP 7 0 14\r\n" +
                    "------------------------------------------------\r\n" + "\r\n" + "\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
                    sysNombreNegocio + "\r\n" +
                    "! U1 SETLP 7 0 14\r\n" +
                    "------------------------------------------------\r\n" + "\r\n" +
                    "! U1 SETLP 7 0 14\r\n" +
                    sysNombre + "\r\n" +
                    "Cedula Juridica: " + sysIdentificacion + "\r\n" +
                    sysDireccion + "\r\n" +
                    "Tel. " + sysTelefono + "\r\n" +
                    "Correo Electronico: " + sysCorreo + "\r\n" +
                    "------------------------------------------------\r\n" + "\r\n" +
                    "! U1 SETLP 7 0 14\r\n" + "\r\n" +
                    "# Factura: " + numeracionFactura + "  " + metodoPagoNombre + "  " + "\r\n" +
                    "Consec DGT: #" + numConsecutivo + "\r\n" +
                    "Clave DGT: #" + key + "\r\n" +
                    "------------------------------------------------\r\n" + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    ((metodoPago == "2") ? "! U1 LMARGIN 120\r\n" +
                            "Fecha Limite: ! U1 LMARGIN 350 " + messageC + "\r\n" : "\r\n") +
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

                    getPrintDistTotal(sale.getInvoice_id()) +
                    "\r\n" +

                    String.format("%20s %-20s", "Subtotal Gravado", totalGrabado) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal Exento", totalExento) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal", totalSubtotal) + "\r\n" +
                    String.format("%20s %-20s", "Descuento", totalDescuento) + "\r\n" +
                    String.format("%20s %-20s", "IVA", totalImpuesto) + "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +



                    String.format("%20s %-20s", "Total a pagar", totalTotal) +
                    "! U1 SETLP 7 0 10\r\n" + "\r\n" +
                    ((metodoPago == "1") ?
                            String.format("%20s %-20s", "Cancelado con", totalCancelado) + "\r\n" +
                                    String.format("%20s %-20s", "Cambio", totalVuelto) : "\r\n"
                    ) +
                    "Notas: " + totalNotas_ + "\r\n" + "\r\n" +
                    ((descuentoCliente > 0) ? "Se le aplico un " + descuentoCliente + "%  de descuento" : "" + "\r\n") +
                    "! U1 SETLP 5 0 14\r\n" +
                    "Firma cliente ____________________________" + "\r\n" + "\r\n" + "\r\n" +
                    "Cedula ____________________________" + "\r\n" +
                    ((metodoPago == "2") ?
                            "! U1 SETLP 0 0 6\r\n" + "\r\n" + condition: "\r\n"
                    ) + "\r\n" +
                    ((ptype == 1) ?
                            "\r\n" + "Este comprobante no puede ser utilizado para fines\r\n" +
                                    "tributarios, por lo cual no se permitira" + "\r\n" +
                                    "su uso para respaldo de creditos o gastos" + "\r\n" + "\r\n"
                            : "\r\n"
                    ) + "\r\n" +
                    "! U1 SETLP 5 0 14\r\n" +

                    " Autorizada mediante resolucion N DGT-R-48-2016\r\n" +
                    "del 7 de octubre de 2016.\r\n" + "\r\n" +

                    "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                    "Mantenga el documento para reclamos ." + "\r\n" +


                    " \n\n" +
                    " \n\n" +
                    " \n ";
            Log.d("IMPR DISTR ZEBRA", bill);
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
        }
        else if(prefList.equals("2")){
            switch (metodoPago) {
                case "1":
                    payment = "Contado";
                    preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Clave DGT: #" + key + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintDistTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto) + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total a pagar", totalTotal) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Cancelado con", totalCancelado) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Cambio", totalVuelto) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Firma cliente ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/><br/>");

                    preview += Html.fromHtml("<h1>")   + "Este comprobante no puede ser utilizado para fines" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "tributarios, por lo cual no se permitira" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "su uso para respaldo de creditos o gastos." +  Html.fromHtml("</h1></center><br/><br/>");

                    preview += Html.fromHtml("<h1>")   + "Autorizada mediante resolucion Nº DGT-R-48-2016" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "del 7 de octubre de 2016." +  Html.fromHtml("</h1></center><br/><br/>");


                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/>");



                    Log.d("IMPR DISTR CONTADO", preview);
                    break;
                case "2":
                    payment = "Credito";
                    messageC = fechaFactura;

                    preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Clave DGT: #" + key + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Fecha Limite: " + messageC + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");

                    preview += Html.fromHtml("<h1>") +  "#  Descripcion      Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintDistTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto) + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total a pagar", totalTotal) + Html.fromHtml("</h1></center><br/><br/>");


                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Firma cliente ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  condition +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Este comprobante no puede ser utilizado para fines" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "tributarios, por lo cual no se permitira" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "su uso para respaldo de creditos o gastos." +  Html.fromHtml("</h1></center><br/><br/>");

                    preview += Html.fromHtml("<h1>")   + "Autorizada mediante resolucion N DGT-R-48-2016" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "del 7 de octubre de 2016." +  Html.fromHtml("</h1></center><br/><br/>");

                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/>");


                    Log.d("IMPR DISTR CREDITO", preview);
                    break;
            }
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
        }
    }

        if (ptype == 1) {
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                    QuickContext.startActivity(intent);
                }
            };
        } else if (ptype == 3) {
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                    QuickContext.startActivity(intent);
                }
            };
        }

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 500);


        totalGrabado= "";
        totalExento= "";
        totalSubtotal= "";
        totalDescuento= "";
        totalImpuesto= "";
        totalTotal = "";
        totalCancelado= "";
        totalVuelto= "";
        totalNotas= "";
    }

    public static void imprimirFacturaDistrTotal (final sale saleB, final Context QuickContext, final int ptype, final String cantidadImpresiones) {

        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Impresión")
                .setMessage("¿Desea realizar la impresión?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imprimirTotalizar(1, saleB, QuickContext, ptype, cantidadImpresiones);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }).create();
        dialogReturnSale.show();

    }

    private static void imprimirTotalizar(int type, sale invoices, Context QuickContext, int ptype, String cantidadImpresiones) {
        try {
            if (invoices != null) {
                PrinterFunctions.datosImprimirDistrTotal(type, invoices, QuickContext, ptype, cantidadImpresiones);
            } else {
                Toast.makeText(QuickContext, "Aun le falta terminar de hacer la factura" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error PrinterFunctions", e.getMessage());
        }
    }

    private static String getPrintDistTotal(String idVenta) {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", idVenta).equalTo("devuelvo", 0).findAll();

        if (result.isEmpty()) {
            send = "No hay invoice emitidas";
        } else {
            // printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<Pivot> salesList1 = realm.where(Pivot.class).equalTo("invoice_id", idVenta).equalTo("devuelvo", 0).findAll();
                Productos producto = realm.where(Productos.class).equalTo("id", salesList1.get(i).getProduct_id()).findFirst();
                //   sale ventas = realm.where(sale.class).equalTo("invoice_id", salesList1.get(i).getInvoice_id()).findFirst();
                //    Clientes clientes = realm.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();


                double precioSugerido = Double.parseDouble(producto.getSuggested());
                String description = producto.getDescription();
                byte[] byteText = description.getBytes(Charset.forName("UTF-8"));
                String description1 = null;
                try {
                    description1 = new String(byteText, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String barcode = producto.getBarcode();
                String typeId = producto.getProduct_type_id();
                String nombreTipo = null;

                double cant = Double.parseDouble(salesList1.get(i).getAmount());


                //   String total = Functions.doubleToString1(cant * precio);
                //     double totalD = Double.parseDouble(total);

                double sugerido=0.0;

                // gravado Sugerido =( (preciode venta/1.13)*(suggested /100) )+ (preciode venta* 0.13)+(preciode venta/1.13);
                // en caso de exento Sugerido =( (preciode venta)*(suggested /100)) + (preciode venta);

                if (typeId.equals("1")){
                    nombreTipo = "Gravado";
                    precio = Double.parseDouble(salesList1.get(i).getPrice()) / 1.13;
                }
                else if (typeId.equals("2")){
                    nombreTipo = "Exento";
                    precio = Double.parseDouble(salesList1.get(i).getPrice());

                }
                sugerido = (precio)*(precioSugerido /100) + (precio);

                send += String.format("%s  %.24s ", description1, barcode) + "\r\n" +
                        String.format("%-12s %-10s %-12s %.10s", cant, Functions.doubleToString1(precio), Functions.doubleToString1(sugerido),Functions.doubleToString1(cant * precio)) + "\r\n" +
                        String.format("%.10s", nombreTipo) + "\r\n";
                send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n";
                Log.d("FACTPRODTODFAC", send + "");
            }
        }
        return send;
    }
    // TODO IMPRIMIR RECIBOS

    public static void datosImprimirRecibosTotal(int type, final recibos sale, final Context QuickContext, int ptype, String cantidadImpresiones) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        int impresiones1 = Integer.parseInt(cantidadImpresiones);

        for(int i =1; i<=impresiones1; i++){

            Log.d("impresiones", "cantidadImpresion" + impresiones1);

        String billptype = "";
        String preview = "";

        Realm realm = Realm.getDefaultInstance();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();

        receipts receipts = realm.where(receipts.class).equalTo("customer_id", sale.getCustomer_id()).findFirst();


       String referencia =  receipts.getReference();

        String sysNombre = sysconf.getName();
        String sysNombreNegocio = sysconf.getBusiness_name();
        String sysDireccion = sysconf.getDirection();
        String sysIdentificacion = sysconf.getIdentification();
        String sysTelefono = sysconf.getPhone();
        String sysCorreo = sysconf.getEmail();

        String nombreCliente = clientes.getFantasyName();
        totalNotasRecibos = sale.getObservaciones();
        final String fecha = /*sale.getDate()*/currentDateandTime;
        Double porPagar = sale.getPorPagar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");


        if (ptype == 1) {
            billptype = "R e c i b o s";
        }

        if (prefList.equals("1")){

            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    "! U1 LMARGIN 185\r\n" +
                    String.format("%s", billptype) + "\r\n" +

                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 5 0 24\r\n" +
                    "# " + referencia + "\r\n" +
                    "! U1 SETLP 5 0 24\r\n" +
                    sysNombre + "\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
                    sysNombreNegocio + "\r\n" +
                    "! U1 SETLP 5 0 24\r\n" +
                    sysDireccion + "\r\n" +
                    "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + "\r\n" +
                    "E-mail " + sysCorreo + "\r\n" +

                    "! U1 SETLP 7 0 14\r\n" + "\r\n" +
                    "Cliente: " + nombreCliente + "\r\n" +
                    "Fecha: " + fecha + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "\r\n" +
                    "Numeracion     Monto Total     Monto Pagado\r\n" +
                    "          \r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +

                    getPrintRecibosTotal(sale.getCustomer_id()) +
                    "\r\n" +
                    "\r\n\n" + "Total: " +  Functions.doubleToString1(printRecibosTotal) + "\r\n" +
                    "\r\n\n" + "Saldo pendiente: " + porPagar + "\r\n" +
                    "\r\n\n" + "Notas: " + totalNotasRecibos + "\r\n" +
                    "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                    "Mantenga el documento para reclamos ." + "\r\n" + "\r\n" +
                    " \n\n" +
                    " \n\n" +
                    " \n ";
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
            Log.d("imprimeZebraProf", bill);
        }
        else if(prefList.equals("2")){
                    preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "# " + referencia + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "E-mail " + sysCorreo + Html.fromHtml("</h1><br/><br/>");

                    preview += Html.fromHtml("<h1>") + "Cliente: " + nombreCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Fecha: " + fecha + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Numeracion     Monto Total     Monto Pagado" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   getPrintRecibosTotal(sale.getCustomer_id()) + Html.fromHtml("</h1></center><br/>");

                    preview += Html.fromHtml("<h1>") +  "Total: " +  Functions.doubleToString1(printRecibosTotal) + Html.fromHtml("</h1></center><br/><br/><br/>");

                    preview += Html.fromHtml("<h1>") +  "Saldo pendiente: " + porPagar + Html.fromHtml("</h1></center><br/><br/><br/>");
            preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotasRecibos + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/><br/>");
            }
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
            Log.d("imprimeProf", preview);

        }

        final Realm realm2 = Realm.getDefaultInstance();
        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm2) {

                RealmResults<recibos> result = realm2.where(recibos.class).equalTo("customer_id",  sale.getCustomer_id()).equalTo("abonado", 1).findAll();

                if (result.isEmpty()) {

                    Toast.makeText(QuickContext, "No hay recibos emitidos", Toast.LENGTH_LONG).show();}

                else{
                    for (int i = 0; i < result.size(); i++) {

                        List<recibos> salesList1 = realm2.where(recibos.class).equalTo("customer_id", sale.getCustomer_id()).equalTo("abonado", 1).findAll();

                        String facturaId1 = salesList1.get(i).getInvoice_id();

                        recibos recibo_actualizado = realm2.where(recibos.class).equalTo("invoice_id", facturaId1).findFirst();
                        recibo_actualizado.setMostrar(0);

                        realm2.insertOrUpdate(recibo_actualizado);

                        Log.d("ACTMOSTRAR", recibo_actualizado + "");
                    }
                    realm2.close();
                }

            }
        });


        }

    private static String getPrintRecibosTotal(String idVenta) {
        String send;

        send = " ";
        printRecibosTotal= 0.0;
        Realm realm1 = Realm.getDefaultInstance();
        RealmResults<recibos> result = realm1.where(recibos.class).equalTo("customer_id", idVenta).equalTo("abonado", 1).equalTo("mostrar",1).findAll();

        if (result.isEmpty()) {
            send = "No hay recibos emitidos";
        }
        else {
            for (int i = 0; i < result.size(); i++) {

                List<recibos> salesList1 = realm1.where(recibos.class).equalTo("customer_id", idVenta).equalTo("abonado", 1).equalTo("mostrar",1).findAll();


                String numeracion = salesList1.get(i).getNumeration();
                double total = salesList1.get(i).getTotal();
                //String totalS = String.format("%,.2f", total);

                double pagado = salesList1.get(i).getMontoCanceladoPorFactura();
               // String pagadoS = String.format("%,.2f", pagado);
                double restante = salesList1.get(i).getMontoCancelado();
               // String restanteS = String.format("%,.2f", restante);

                send += String.format("%-9s  %9s  %9s", numeracion, Functions.doubleToString1(total), Functions.doubleToString1(pagado) ) + "\r\n";
                printRecibosTotal = printRecibosTotal + pagado;
                send += "------------------------------------------------\r\n";

                Log.d("FACTPRODTODFAC", send + "");

            }
            realm1.close();
        }
        return send;

    }

    public static void imprimirFacturaRecibosTotal(final recibos recibo, final Context QuickContext, final int ptype, final String cantidadImpresiones){

        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Impresión")
                .setMessage("¿Desea realizar la impresión?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imprimirRecibosTotalizar(1, recibo, QuickContext, ptype, cantidadImpresiones);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }).create();
        dialogReturnSale.show();

    }

    private static void imprimirRecibosTotalizar(int type, recibos recibo, Context QuickContext, int ptype, String cantidadImpresiones) {
        try {
            if (recibo != null) {
                PrinterFunctions.datosImprimirRecibosTotal(type, recibo, QuickContext, ptype, cantidadImpresiones);
            } else {
                Toast.makeText(QuickContext, "Aun le falta terminar de hacer la factura" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error PrinterFunctions", e.getMessage());
        }
    }


    //TODO imprimir TOTALIZAR PREVENTA

    public static void datosImprimirPrevTotal(int type, sale sale, final Context QuickContext, int ptype) {
        String billptype = "";
        String preview = "";
        String metodoPagoNombre = "";
        String payment = "";

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).findAll();
        Log.d("FACTPRODTOD", result + "");
        String fechayhora = sale.getUpdated_at();
        String nombreCliente = sale.getCustomer_name();
        double descuentoCliente = Double.parseDouble(clientes.getFixedDiscount());

        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();

        String numeracionFactura = invoice.getNumeration();
        String metodoPago = invoice.getPayment_method_id();
        totalGrabado_= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_taxed()));
        totalExento_= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_exempt()));
        totalSubtotal_= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal()));
        totalDescuento_= Functions.doubleToString1(Double.parseDouble(invoice.getDiscount()));
        totalImpuesto_= Functions.doubleToString1(Double.parseDouble(invoice.getTax()));
        totalTotal_= Functions.doubleToString1(Double.parseDouble(invoice.getTotal()));
        totalCancelado_= Functions.doubleToString1(Double.parseDouble(invoice.getPaid()));
        totalVuelto_= Functions.doubleToString1(Double.parseDouble(invoice.getChanging()));
        totalNotas_= invoice.getNote();
        String idUsuario = invoice.getUser_id();

        Usuarios usuarios = realm.where(Usuarios.class).equalTo("id", idUsuario).findFirst();
        String nombreUsuario = usuarios.getUsername();

        // VARIABLES SYSCONF
        String sysNombreNegocio = sysconf.getBusiness_name();
        realm.close();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");

        String condition = "Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ";

        if (ptype == 1) {
            billptype = "Orden de Pedido";
        } else if (ptype == 3) {
            billptype = "Comprobante Provisional";
        }

        if (metodoPago.equals("1")) {
            metodoPagoNombre = "Contado";
        } else if (metodoPago.equals("2")) {
            metodoPagoNombre = "Credito";
        }

        if (prefList.equals("1")){

            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    String.format("%s", billptype) + "\r\n" +

                    "! U1 SETLP 5 0 24\r\n" + "\r\n" +
                    "N# Factura: " + numeracionFactura + "\r\n" +
                    "Factura de: " + metodoPagoNombre + "\r\n" +

                    "! U1 SETLP 5 0 24\r\n\n" +
                    "\r\n" +
                    sysNombreNegocio + "\r\n" +

                    "Fecha y hora: " + fechayhora + "\r\n" +
                    "Vendedor:  " + nombreUsuario + "\r\n" +
                    "Razon Social: " + companyCliente + "\r\n" +
                    ((!nombreCliente.isEmpty()) ? "A nombre de: " + nombreCliente + "\r\n" : "") +
                    "Nombre fantasia: " + fantasyCliente + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "\r\n" +
                    "Descripcion           Codigo\r\n" +
                    "Cantidad      Precio       P.Sug       Total\r\n" +
                    "Tipo     \r\n" +
                    "- - - - - - - - - - - - - - - - - - - - - - - -\r\n"+
                    "! U1 SETLP 7 0 10\r\n" +

                    getPrintPrevTotal(sale.getInvoice_id()) +
                    "\r\n" +

                    String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal Exento", totalExento_) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal", totalSubtotal_) + "\r\n" +
                    String.format("%20s %-20s", "Descuento", totalDescuento_) + "\r\n" +
                    String.format("%20s %-20s", "IVA", totalImpuesto_) + "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    String.format("%20s %-20s", "Total", totalTotal_) + "\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    ((metodoPago == "1") ?
                            String.format("%20s %-20s", "Cancelado con", totalCancelado) + "\r\n" +
                                    String.format("%20s %-20s", "Cambio", totalVuelto) + "\r\n" : "\r\n"
                    ) + "\r\n" +
                    "\r\n\n" + "Notas: " + totalNotas_ + "\r\n" +
                    ((descuentoCliente > 0) ? "Se le aplico un " + descuentoCliente + "%  de descuento" + "\r\n" : "" + "\r\n") +
                    "! U1 SETLP 5 0 14\r\n" +
                    "\r\n\n" +
                    "Recibo conforme ____________________________" + "\r\n" + "\r\n" + "\r\n" +
                    "Cedula                  ____________________________" + "\r\n" +
                    "\r\n\n" +
                    ((metodoPago == "2") ?
                            "Firma y Cedula __________________________" + "\r\n"
                                    + "! U1 SETLP 0 0 6\r\n" + "\r\n\n" + condition + "\r\n" : "\r\n"
                    ) + "\r\n" +
                    "\r\n\n" +
                    ((ptype == 3) ?
                            "\r\n\n" + "Este comprobante no puede ser utilizado para fines\r\n" +
                                    "tributarios, por lo cual no se permitirá" + "\r\n" +
                                    "su uso para respaldo de créditos o gastos" + "\r\n" + "\r\n"
                                   : "\r\n"
                    ) + "\r\n" +

                    "! U1 SETLP 5 0 14\r\n\n" +
                    "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                    "Mantenga el documento para reclamos ." + "\r\n" + "\r\n" +

                    " Autorizada mediante resolución N DGT-R-48-2016\r\n" +
                    "del 7 de octubre de 2016.\r\n" +
                    " \n\n" +
                    " \n\n" +
                    " \n ";
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
            Log.d("imprimeZebra", bill);


            if (ptype == 1) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };
            } else if (ptype == 3) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };
            }

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 500);


        }
        else if(prefList.equals("2")){
            switch (metodoPago) {
                case "1":

                 payment = "Contado";
                    preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Factura de: " + payment + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto_) + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total", totalTotal_) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Cancelado con", totalCancelado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Cambio", totalVuelto_) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Recibo conforme ____________________________" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/>");

                    if(ptype == 3){

                    preview += Html.fromHtml("<h1>")   + "Este comprobante no puede ser utilizado para fines" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "tributarios, por lo cual no se permitira" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "su uso para respaldo de créditos o gastos." +  Html.fromHtml("</h1></center><br/><br/>");
                    }

                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Autorizada mediante resolución Nº DGT-R-48-2016" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "del 7 de octubre de 2016." +  Html.fromHtml("</h1></center><br/>");
                    break;
                case "2":
                    payment = "Credito";
                    preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Factura de: " + payment + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>");

                    preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto_) + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total", totalTotal_) + Html.fromHtml("</h1></center><br/><br/>");
                  /*  preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Cancelado con", totalCancelado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Cambio", totalVuelto_) + Html.fromHtml("</h1></center><br/><br/>");*/

                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Recibo conforme ____________________________" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/>");

                    preview += Html.fromHtml("<h1>") +  condition +  Html.fromHtml("</h1></center><br/>");

                    if(ptype == 3){

                        preview += Html.fromHtml("<h1>")   + "Este comprobante no puede ser utilizado para fines" +  Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>")   + "tributarios, por lo cual no se permitira" +  Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>")   + "su uso para respaldo de créditos o gastos." +  Html.fromHtml("</h1></center><br/><br/>");
                    }

                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Autorizada mediante resolución Nº DGT-R-48-2016" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "del 7 de octubre de 2016." +  Html.fromHtml("</h1></center><br/>");
                    break;
            }
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
            Log.d("imprime", preview);

            if (ptype == 1) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };
            } else if (ptype == 3) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };
            }

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 500);


        }
        totalGrabado_= "";
        totalExento_= "";
        totalSubtotal_= "";
        totalDescuento_= "";
        totalImpuesto_= "";
        totalTotal_ = "";
        totalCancelado_= "";
        totalVuelto_= "";
        totalNotas_ = "";
    }

    public static void datosImprimirProformaTotal(int type, sale sale, final Context QuickContext, int ptype) {
        String billptype = "";
        String preview = "";

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).findAll();
        Log.d("FACTPRODTOD", result + "");
        String fechayhora = sale.getUpdated_at();
        String nombreCliente = sale.getCustomer_name();
        double descuentoCliente = Double.parseDouble(clientes.getFixedDiscount());

        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();

        String numeracionFactura = invoice.getNumeration();
        String metodoPago = invoice.getPayment_method_id();
        totalGrabado_= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_taxed()));
        totalExento_= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_exempt()));
        totalSubtotal_= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal()));
        totalDescuento_= Functions.doubleToString1(Double.parseDouble(invoice.getDiscount()));
        totalImpuesto_= Functions.doubleToString1(Double.parseDouble(invoice.getTax()));
        totalTotal_= Functions.doubleToString1(Double.parseDouble(invoice.getTotal()));
        totalCancelado_= Functions.doubleToString1(Double.parseDouble(invoice.getPaid()));
        totalVuelto_= Functions.doubleToString1(Double.parseDouble(invoice.getChanging()));
        totalNotas_= invoice.getNote();
        String idUsuario = invoice.getUser_id();

        Usuarios usuarios = realm.where(Usuarios.class).equalTo("id", idUsuario).findFirst();
        String nombreUsuario = usuarios.getUsername();

        // VARIABLES SYSCONF
        String sysNombreNegocio = sysconf.getBusiness_name();
        realm.close();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");

        String condition = "Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ";

        if (ptype == 1) {
            billptype = "P e d i d o";
        } else if (ptype == 2) {
            billptype = "P r o f o r m a";
        } else if (ptype == 3) {
            billptype = "F a c t u r a";
        }


        if (prefList.equals("1")){

            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 0 24\r\n" + "\r\n" +
                    "Cliente: " + nombreCliente + "\r\n" +
                    "Razon Social: " + companyCliente + "\r\n" +
                    ((!nombreCliente.isEmpty()) ? "A nombre de: " + nombreCliente + "\r\n" : "") +
                    "! U1 LMARGIN 0\r\n" +
                    "Nombre fantasia: " + fantasyCliente + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "\r\n" +
                    "Descripcion           Codigo\r\n" +
                    "Cantidad      Precio       P.Sug       Total\r\n" +
                    "Tipo     \r\n" +
                    "- - - - - - - - - - - - - - - - - - - - - - - -\r\n"+
                    "! U1 SETLP 7 0 10\r\n" +

                    getPrintPrevTotal(sale.getInvoice_id()) +
                    "\r\n" +

                   /* String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal Exento", totalExento_) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal", totalSubtotal_) + "\r\n" +
                    String.format("%20s %-20s", "Descuento", totalDescuento_) + "\r\n" +
                    String.format("%20s %-20s", "IVA", totalImpuesto_) + "\r\n" +*/
                    "! U1 SETLP 5 3 70\r\n" +
                    String.format("%20s %-20s", "Total", totalTotal_) + "\r\n" +
                   /* "! U1 SETLP 7 0 10\r\n" +
                    ((metodoPago == "1") ?
                            String.format("%20s %-20s", "Cancelado con", totalCancelado) + "\r\n" +
                                    String.format("%20s %-20s", "Cambio", totalVuelto) + "\r\n" : "\r\n"
                    ) + "\r\n" +*/
                    "\r\n\n" + "Notas: " + totalNotas_ + "\r\n" +
                    ((descuentoCliente > 0) ? "Se le aplico un " + descuentoCliente + "%  de descuento" + "\r\n" : "" + "\r\n") +
                    "! U1 SETLP 5 0 14\r\n" +
                    "\r\n\n" +
                    "Recibo conforme ____________________________" + "\r\n" + "\r\n" + "\r\n" +
                    "Cedula                  ____________________________" + "\r\n" +
                    "\r\n\n" +
                    ((metodoPago == "2") ?
                            "Firma y Cedula __________________________" + "\r\n"
                                    + "! U1 SETLP 0 0 6\r\n" + "\r\n\n" + condition + "\r\n" : "\r\n"
                    ) + "\r\n" +
                    "! U1 SETLP 5 0 14\r\n\n" +
                    "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                    "Mantenga el documento para reclamos ." + "\r\n" + "\r\n" +
                    " \n\n" +
                    " \n\n" +
                    " \n ";
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
            Log.d("imprimeZebraProf", bill);

            if (ptype == 2) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };


            }
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 500);
        }
        else if(prefList.equals("2")){
            switch (metodoPago) {
                case "1":

                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>");

                    preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                   /* preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto_) + Html.fromHtml("</h1></center><br/><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total", totalTotal_) + Html.fromHtml("</h1></center><br/><br/>");
                  /* preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Cancelado con", totalCancelado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Cambio", totalVuelto_) + Html.fromHtml("</h1></center><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Recibo conforme ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/><br/>");
                    break;
                case "2":
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/><br/>");

                    preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                /*    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento_) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto_) + Html.fromHtml("</h1></center><br/><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total", totalTotal_) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Recibo conforme ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Firma y Cedula __________________________" +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  condition +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/><br/>");
                    break;
            }
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
            Log.d("imprimeProf", preview);

            if (ptype == 2) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };


            }
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 500);
        }
        totalGrabado_= "";
        totalExento_= "";
        totalSubtotal_= "";
        totalDescuento_= "";
        totalImpuesto_= "";
        totalTotal_ = "";
        totalCancelado_= "";
        totalVuelto_= "";
        totalNotas_ = "";
    }

    public static void imprimirFacturaPrevTotal (final sale saleB, final Context QuickContext, final int ptype){

        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Impresión")
                .setMessage("¿Desea realizar la impresión?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imprimirTotalizarPrev(1, saleB, QuickContext, ptype);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }).create();
        dialogReturnSale.show();

    }

    //TODO imprimir TOTALIZAR PROFORMA

    public static void imprimirFacturaProformaTotal (final sale saleB, final Context QuickContext, final int ptype){

        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Impresión")
                .setMessage("¿Desea realizar la impresión?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imprimirTotalizarProform(1, saleB, QuickContext, ptype);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }).create();
        dialogReturnSale.show();


    }

    private static void imprimirTotalizarPrev(int type, sale invoices, Context QuickContext, int ptype) {
        try {
            if (invoices != null) {
                PrinterFunctions.datosImprimirPrevTotal(type, invoices, QuickContext, ptype);
            } else {
                Toast.makeText(QuickContext, "Aun le falta terminar de hacer la factura" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error PrinterFunctions", e.getMessage());
        }
    }

    private static void imprimirTotalizarProform(int type, sale invoices, Context QuickContext, int ptype) {
        try {
            if (invoices != null) {
                PrinterFunctions.datosImprimirProformaTotal(type, invoices, QuickContext, ptype);
            } else {
                Toast.makeText(QuickContext, "Aun le falta terminar de hacer la factura" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error PrinterFunctions", e.getMessage());
        }
    }


    //TODO imprimir TOTALIZAR VENTA DIRECTA

    public static void datosImprimirVentaDirectaTotal(int type, sale sale, final Context QuickContext, int ptype, String cantidadImpresiones) {

        int impresiones1 = Integer.parseInt(cantidadImpresiones);

        for(int i =1; i<=impresiones1; i++) {

            Log.d("impresiones", "cantidadImpresion" + impresiones1);

            String billptype = "";
            String preview = "";
            String metodoPagoNombre = "";
            String payment = "";

            Realm realm = Realm.getDefaultInstance();
            Sysconf sysconf = realm.where(Sysconf.class).findFirst();
            Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
            invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();
            RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).findAll();
            Log.d("FACTPRODTOD", result + "");
            String fechayhora = sale.getUpdated_at();
            String nombreCliente = sale.getCustomer_name();
            double descuentoCliente = Double.parseDouble(clientes.getFixedDiscount());

            String companyCliente = clientes.getCompanyName();
            String fantasyCliente = clientes.getFantasyName();
            String cardCliente = clientes.getCard();
            String telefonoCliente = clientes.getPhone();


            String key = invoice.getKey();
            String numConsecutivo = invoice.getConsecutive_number();
            String tipoFactura = invoice.getType();
            String numeracionFactura = invoice.getNumeration();
            String metodoPago = invoice.getPayment_method_id();
            totalGrabado_ = Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_taxed()));
            totalExento_ = Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_exempt()));
            totalSubtotal_ = Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal()));
            totalDescuento_ = Functions.doubleToString1(Double.parseDouble(invoice.getDiscount()));
            totalImpuesto_ = Functions.doubleToString1(Double.parseDouble(invoice.getTax()));
            totalTotal_ = Functions.doubleToString1(Double.parseDouble(invoice.getTotal()));
            totalCancelado_ = Functions.doubleToString1(Double.parseDouble(invoice.getPaid()));
            totalVuelto_ = Functions.doubleToString1(Double.parseDouble(invoice.getChanging()));
            totalNotas_ = invoice.getNote();
            String idUsuario = invoice.getUser_id();

            Usuarios usuarios = realm.where(Usuarios.class).equalTo("id", idUsuario).findFirst();
            String nombreUsuario = usuarios.getUsername();

            // VARIABLES SYSCONF
            String sysNombre = sysconf.getName();
            String sysNombreNegocio = sysconf.getBusiness_name();
            String sysDireccion = sysconf.getDirection();
            String sysIdentificacion = sysconf.getIdentification();
            String sysTelefono = sysconf.getPhone();
            String sysCorreo = sysconf.getEmail();

            realm.close();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
            String prefList = sharedPreferences.getString("pref_selec_impresora", "Impresora Zebra");

            String condition = "Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                    + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                    + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                    + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                    + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                    + "bancarias de " + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ";

            if (tipoFactura.equals("01")) {
                billptype = "Factura Electronica";
            } else if (tipoFactura.equals("04")) {
                billptype = "Tiquete Electronico";
            }



            if (metodoPago.equals("1")) {
                metodoPagoNombre = "Contado";
            } else if (metodoPago.equals("2")) {
                metodoPagoNombre = "Credito";
            }

            if (prefList.equals("1")) {

                String bill = "! U1 JOURNAl\r\n" +
                        "! U1 SETLP 0 0 0\r\n" +
                        "\r\n" +
                        "! U1 SETLP 5 3 70\r\n" +
                        "! U1 LMARGIN 0\r\n" +
                        String.format("%s", billptype) + "\r\n" +
                        "! U1 SETLP 7 0 14\r\n" +
                        "------------------------------------------------\r\n" + "\r\n" + "\r\n" +
                        "! U1 SETLP 5 1 35\r\n" +
                        sysNombreNegocio + "\r\n" +
                        "! U1 SETLP 7 0 14\r\n" +
                        "------------------------------------------------\r\n" + "\r\n" +
                        "! U1 SETLP 7 0 14\r\n" +
                        sysNombre + "\r\n" +
                        "Cedula Juridica: " + sysIdentificacion + "\r\n" +
                        sysDireccion + "\r\n" +
                        "Tel. " + sysTelefono + "\r\n" +
                        "Correo Electronico: " + sysCorreo + "\r\n" +
                        "------------------------------------------------\r\n" + "\r\n" +
                        "! U1 SETLP 7 0 14\r\n" + "\r\n" +
                        "# Factura: " + numeracionFactura + "  " + metodoPagoNombre + "\r\n" +
                        "Consec DGT: #" + numConsecutivo + "\r\n" +
                        "Clave DGT: #" + key + "\r\n" +
                        "------------------------------------------------\r\n" + "\r\n" +
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
                        "\r\n" +
                        "Descripcion           Codigo\r\n" +
                        "Cantidad      Precio       P.Sug       Total\r\n" +
                        "Tipo     \r\n" +
                        "- - - - - - - - - - - - - - - - - - - - - - - -\r\n" +
                        "! U1 SETLP 7 0 10\r\n" +

                        getPrintPrevTotal(sale.getInvoice_id()) +
                        "\r\n" +

                        String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + "\r\n" +
                        String.format("%20s %-20s", "Subtotal Exento", totalExento_) + "\r\n" +
                        String.format("%20s %-20s", "Subtotal", totalSubtotal_) + "\r\n" +
                        String.format("%20s %-20s", "Descuento", totalDescuento_) + "\r\n" +
                        String.format("%20s %-20s", "IVA", totalImpuesto_) + "\r\n" +
                        "! U1 SETLP 5 3 70\r\n" +
                        String.format("%20s %-20s", "Total a pagar", totalTotal_) +
                        "! U1 SETLP 7 0 10\r\n" + "\r\n" +
                        ((metodoPago == "1") ?
                                String.format("%20s %-20s", "Cancelado con", totalCancelado) + "\r\n" +
                                        String.format("%20s %-20s", "Cambio", totalVuelto) : "\r\n"
                        ) +
                        "Notas: " + totalNotas_ + "\r\n" + "\r\n" +
                        ((descuentoCliente > 0) ? "Se le aplico un " + descuentoCliente + "%  de descuento" : "" + "\r\n") +
                        "! U1 SETLP 5 0 14\r\n" +
                        "Firma cliente ____________________________" + "\r\n" + "\r\n" + "\r\n" +
                        "Cedula ____________________________" + "\r\n" +
                        ((metodoPago == "2") ?
                                "! U1 SETLP 0 0 6\r\n" + "\r\n" + condition : "\r\n"
                        ) + "\r\n" +
                        ((ptype == 3) ?
                                "\r\n" + "Este comprobante no puede ser utilizado para fines\r\n" +
                                        "tributarios, por lo cual no se permitira" + "\r\n" +
                                        "su uso para respaldo de creditos o gastos" + "\r\n" + "\r\n"
                                : "\r\n"
                        ) + "\r\n" +

                        "! U1 SETLP 5 0 14\r\n" +
                        " Autorizada mediante resolucion N DGT-R-48-2016\r\n" +
                        "del 7 de octubre de 2016.\r\n" + "\r\n" +

                        "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                        "Mantenga el documento para reclamos ." + "\r\n" +
                        " \n\n" +
                        " \n\n" +
                        " \n ";
                Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
                intent2.putExtra("bill_to_print", bill);
                QuickContext.sendBroadcast(intent2);
                Log.d("imprimeZebra", bill);

            } else if (prefList.equals("2")) {
                switch (metodoPago) {
                    case "1":

                        payment = "Contado";
                        preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Clave DGT: #" + key + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");

                        preview += Html.fromHtml("<h1>") + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");

                        preview += Html.fromHtml("<h1>") + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Tipo " + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Subtotal Exento", totalExento_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Subtotal", totalSubtotal_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Descuento", totalDescuento_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "IVA", totalImpuesto_) + Html.fromHtml("</h1></center><br/><br/><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Total a pagar", totalTotal_) + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Cancelado con", totalCancelado_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Cambio", totalVuelto_) + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "Firma cliente ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>");

                        if (ptype == 3) {

                            preview += Html.fromHtml("<h1>") + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml("</h1></center><br/>");
                            preview += Html.fromHtml("<h1>") + "tributarios, por lo cual no se permitira" + Html.fromHtml("</h1></center><br/>");
                            preview += Html.fromHtml("<h1>") + "su uso para respaldo de creditos o gastos." + Html.fromHtml("</h1></center><br/><br/>");
                        }

                        preview += Html.fromHtml("<h1>") + "Autorizada mediante resolucion Nº DGT-R-48-2016" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/><br/>");

                        preview += Html.fromHtml("<h1>") + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/>");
                        break;
                    case "2":
                        payment = "Credito";

                        preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Cedula Juridica: " + sysIdentificacion + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Correo Electronico: " + sysCorreo + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "# Factura: " + numeracionFactura + "  " + payment + "  " + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Consec DGT: #" + numConsecutivo + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Clave DGT: #" + key + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");

                        preview += Html.fromHtml("<h1>") + "Fecha y hora: " + fechayhora + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "Cedula: " + cardCliente + Html.fromHtml("</h1><br/>");
                        preview += Html.fromHtml("<h1>") + "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "------------------------------------------------" + Html.fromHtml("</h1></center><br/><br/>");

                        preview += Html.fromHtml("<h1>") + "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Tipo " + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "- - - - - - - - - - - - - - - - - - - - - - - -" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Subtotal Gravado", totalGrabado_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Subtotal Exento", totalExento_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Subtotal", totalSubtotal_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Descuento", totalDescuento_) + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "IVA", totalImpuesto_) + Html.fromHtml("</h1></center><br/><br/><br/>");
                        preview += Html.fromHtml("<h1>") + String.format("%20s %-20s", "Total a pagar", totalTotal_) + Html.fromHtml("</h1></center><br/><br/>");

                        preview += Html.fromHtml("<h1>") + "Notas: " + totalNotas_ + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "Firma cliente ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "Cedula ____________________________" + Html.fromHtml("</h1></center><br/><br/>");

                        preview += Html.fromHtml("<h1>") + condition + Html.fromHtml("</h1></center><br/>");

                        if (ptype == 3) {

                            preview += Html.fromHtml("<h1>") + "Este comprobante no puede ser utilizado para fines" + Html.fromHtml("</h1></center><br/>");
                            preview += Html.fromHtml("<h1>") + "tributarios, por lo cual no se permitira" + Html.fromHtml("</h1></center><br/>");
                            preview += Html.fromHtml("<h1>") + "su uso para respaldo de creditos o gastos." + Html.fromHtml("</h1></center><br/><br/>");
                        }
                        preview += Html.fromHtml("<h1>") + "Autorizada mediante resolucion Nº DGT-R-48-2016" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "del 7 de octubre de 2016." + Html.fromHtml("</h1></center><br/><br/>");
                        preview += Html.fromHtml("<h1>") + "Muchas gracias por preferirnos un placer atenderlo" + Html.fromHtml("</h1></center><br/>");
                        preview += Html.fromHtml("<h1>") + "Mantenga el documento para reclamos." + Html.fromHtml("</h1></center><br/>");
                        break;
                }
                Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
                intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
                intent2.putExtra("bill_to_print", preview);
                QuickContext.sendBroadcast(intent2);
                Log.d("imprime", preview);


            }
        }

            if (ptype == 1) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };
            } else if (ptype == 3) {
                handler = new Handler();
                runnable = new Runnable() {
                    public void run() {
                        Intent intent = new Intent(QuickContext, MenuPrincipal.class);
                        QuickContext.startActivity(intent);
                    }
                };
            }

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 500);



        totalGrabado_= "";
        totalExento_= "";
        totalSubtotal_= "";
        totalDescuento_= "";
        totalImpuesto_= "";
        totalTotal_ = "";
        totalCancelado_= "";
        totalVuelto_= "";
        totalNotas_ = "";
    }

    private static void imprimirTotalizarVentaDirect(int type, sale invoices, Context QuickContext, int ptype, String cantidadImpresiones) {
        try {
            if (invoices != null) {
                PrinterFunctions.datosImprimirVentaDirectaTotal(type, invoices, QuickContext, ptype, cantidadImpresiones);
            } else {
                Toast.makeText(QuickContext, "Aun le falta terminar de hacer la factura" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error PrinterFunctions", e.getMessage());
        }
    }

    public static void imprimirFacturaVentaDirectaTotal (final sale saleB, final Context QuickContext, final int ptype, final String cantidadImpresiones){

        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Impresión")
                .setMessage("¿Desea realizar la impresión?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imprimirTotalizarVentaDirect(1, saleB, QuickContext, ptype,cantidadImpresiones);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                }).create();
        dialogReturnSale.show();

    }

    private static String getPrintPrevTotal(String idVenta) {
        String send = " ";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        double amountsinbonus = 0.0;
        double amountConBonus = 0.0;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", idVenta).equalTo("devuelvo", 0).findAll();

        if (result.isEmpty()) {
            send = "No hay invoice emitidas";
        } else {
            // printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<Pivot> salesList1 = realm.where(Pivot.class).equalTo("invoice_id", idVenta).findAll();

                int esBonus = salesList1.get(i).getBonus();

                Productos producto = realm.where(Productos.class).equalTo("id", salesList1.get(i).getProduct_id()).findFirst();

                double precioSugerido = Double.parseDouble(producto.getSuggested());
                String description = producto.getDescription();
                byte[] byteText = description.getBytes(Charset.forName("UTF-8"));

                String description1 = null;
                try {
                    description1 = new String(byteText, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String barcode = producto.getBarcode();
                String typeId = producto.getProduct_type_id();
                String nombreTipo = null;

                double cant = Double.parseDouble(salesList1.get(i).getAmount());


                double sugerido=0.0;

                // gravado Sugerido =( (preciode venta/1.13)*(suggested /100) )+ (preciode venta* 0.13)+(preciode venta/1.13);
                // en caso de exento Sugerido =( (preciode venta)*(suggested /100)) + (preciode venta);

                if (typeId.equals("1")){
                    nombreTipo = "Gravado";
                    precio = Double.parseDouble(salesList1.get(i).getPrice()) / 1.13;
                }
                else if (typeId.equals("2")){
                    nombreTipo = "Exento";
                    precio = Double.parseDouble(salesList1.get(i).getPrice());
                }
                sugerido = (precio)*(precioSugerido /100) + (precio);

                if(esBonus == 1) {

                    amountsinbonus = salesList1.get(i).getAmountSinBonus();

                    String amount = salesList1.get(i).getAmount();
                    amountConBonus = Double.parseDouble(amount);

                    double totalAmountBonus = amountConBonus - amountsinbonus;

                    send += String.format("%s  %.24s ", description1, barcode) + "\r\n" +
                            String.format("%-12s %-10s %-12s %.10s", totalAmountBonus, "0.0", "0.0", "0.0") + "\r\n" +
                            String.format("%.10s", nombreTipo) + "\r\n";
                    send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n";

                    send += String.format("%s  %.24s ", description1, barcode) + "\r\n" +
                            String.format("%-12s %-10s %-12s %.10s", amountsinbonus, Functions.doubleToString1(precio), Functions.doubleToString1(sugerido), Functions.doubleToString1(amountsinbonus * precio)) + "\r\n" +
                            String.format("%.10s", nombreTipo) + "\r\n";
                    send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n";
                }else{
                    send += String.format("%s  %.24s ", description1, barcode) + "\r\n" +
                            String.format("%-12s %-10s %-12s %.10s", cant, Functions.doubleToString1(precio), Functions.doubleToString1(sugerido), Functions.doubleToString1(cant * precio)) + "\r\n" +
                            String.format("%.10s", nombreTipo) + "\r\n";
                    send += "- - - - - - - - - - - - - - - - - - - - - - - -\r\n";
                }

                Log.d("FACTPRODTODFAC", send + "");
            }
        }
        return send;
    }


    //TODO imprimir PRODUCTOS SELECC

    public static void datosImprimirProductosSelecClientes(sale sale, Context QuickContext) {

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();
        //RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).findAll();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).equalTo("devuelvo", 0).findAll();

        Log.d("FACTPRODTOD", result + "");

        // VARIABLES CLIENTES
        String companyCliente = clientes.getCompanyName();

        // VARIABLES FACTURA
        String numeracionFactura = invoice.getNumeration();


        // VARIABLES SYSCONF
        String sysNombre = sysconf.getName();
        String sysNombreNegocio = sysconf.getBusiness_name();
        String sysDireccion = sysconf.getDirection();
        realm.close();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");
        String preview = "";
        String billptype = "O r d e n  d e  C o m p r a";

        if (prefList.equals("1")){

            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    //"! U1 LMARGIN 185\r\n" +
                    String.format("%s", billptype) + "\r\n" +
                    //"! U1 LMARGIN 150\r\n" +
                    "! U1 SETLP 7 0 14\r\n" + "\r\n" +
                    "N# Factura: " + numeracionFactura + "\r\n" +
                    //"! U1 LMARGIN 120\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
                    sysNombre + "\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
                    sysNombreNegocio + "\r\n" +
                    "! U1 SETLP 5 0 24\r\n" +
                    sysDireccion + "\r\n" +
                    "! U1 SETLP 7 0 14\r\n" +
                    "\r\n" +
                    //"! U1 LMARGIN 50\r\n" +
                    "Razon Social: " + companyCliente + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "\r\n" +
                    "\r\n" +
                    "Descripcion           Codigo\r\n" +
                    "Cantidad      Precio       P.Sug       Total\r\n" +
                    "Tipo     \r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    getPrintDistTotal(sale.getInvoice_id()) +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    " \n\n" +
                    " \n\n" +
                    " \n ";

            Log.d("JD", bill);
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
        }
        else if(prefList.equals("2")){

            preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
            preview += Html.fromHtml("<h1>") + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/>");
            preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/>");
            preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/>");
            preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/>");
            preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
            preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "Tipo" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +    getPrintDistTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
        }
    }

    public static void imprimirProductosDistrSelecCliente(final sale sale, final Context QuickContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("Impresión de Productos?");
        builder.setMessage("¿Desea imprimir la lista de productos para esta factura?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                datosImprimirProductosSelecClientes(sale, QuickContext);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    //TODO imprimir ORDEN DE CARGA

    public static void datosImprimirOrdenCarga(Context QuickContext) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");
        String preview = "";

        //String salesCash = getPrintSalesCash();
            String salesCredit = getPrintProductInvoicesPreventa();
       // String receipts = getPrintReceipts();
        String billptype = "Orden de Carga";

        if (prefList.equals("1")){

            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    //"! U1 LMARGIN 185\r\n" +
                    String.format("%s", billptype) + "\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "Usuario: " + getUserName(QuickContext) + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "\r\n" +
                    "\r\n" +
                    "Descripcion     \r\n" +
                    "Precio         Cantidad          Total\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "\r\n" +
                    salesCredit +
                    "\r\n" +
                    "\r\n" +
                    "------------------------------------------------\r\n" +
                //    "#     Total " + Functions.doubleToString1(printSalesCashTotal) + "\r\n" +
                    "\r\n" +
                    "\r\n" +
                   /* "recibos \r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    " \n\n" +
                    "#     Recibo           Fecha         Monto\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                 //   receipts +
                    " \r\n" +
                    " \r\n" +
                    "------------------------------------------------\r\n" +

                    "\r\n" +
                    "\r\n" +*/

                //   "#     Total " + Functions.doubleToString1(printSalesCashTotal /*+ printSalesCreditTotal + printReceiptsTotal*/) + "\r\n" +
                    " \r\n" +
                    " \r\n" +
                    " \r\n" +
                    " \n ";

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
        }

        else if(prefList.equals("2")){

            preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
          //  preview += Html.fromHtml("<h1>") + "Usuario: " + getUserName(QuickContext) + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") +  "Descripcion " + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "Precio         Cantidad          Total" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +   salesCredit + Html.fromHtml("</h1></center><br/>");
   //         preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printSalesCashTotal) + Html.fromHtml("</h1><br/><br/><br/>");
       //     preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printSalesCashTotal /*+ printSalesCreditTotal + printReceiptsTotal*/) + Html.fromHtml("</h1><br/><br/><br/>");

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
        }
    }

    public static void imprimirOrdenCarga(final Context QuickContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("¿Desea imprimir la lista de orden de carga?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                datosImprimirOrdenCarga(QuickContext);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        //}
    }

    //TODO imprimir LIQUIDACION

    public static void datosImprimirLiquidacion(Context QuickContext) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");
        String preview = "";

        //String salesCash = getPrintSalesCash();
        String ventaContado = getPrintProductInvoicesLiqContado();
        String ventaCredito = getPrintProductInvoicesLiqCredito();
        String ventaRecibos = getPrintProductRecibosLiq();

        // String receipts = getPrintReceipts();
        String billptype = "L i q u i d a c i o n";

        if (prefList.equals("1")){

            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    //"! U1 LMARGIN 185\r\n" +
                    String.format("%s", billptype) + "\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "Usuario: " + getUserName(QuickContext) + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +

                    "Facturas de Contado" + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "#     Factura           Fecha         Monto\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "\r\n" +
                    ventaContado +
                    "\r\n" +
                    "\r\n" +
                    "------------------------------------------------\r\n" +
                    "#     Total " + Functions.doubleToString1(printLiqContadoTotal) + "\r\n" +
                    "\r\n" +
                    "\r\n" +

                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "Facturas de Credito" + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "#     Factura           Fecha         Monto\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "\r\n" +
                    ventaCredito +
                    "\r\n" +
                    "\r\n" +
                    "------------------------------------------------\r\n" +
                    "#     Total " + Functions.doubleToString1(printLiqCreditoTotal) + "\r\n" +
                    "\r\n" +
                    "\r\n" +

                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "Recibos" + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "#     Recibo           Fecha         Monto\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "\r\n" +
                    ventaRecibos +
                    "\r\n" +
                    "\r\n" +
                    "------------------------------------------------\r\n" +
                    "#     Total " + Functions.doubleToString1(printLiqRecibosTotal) + "\r\n" +
                    "\r\n" +
                    "\r\n" +

                   /* "recibos \r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    " \n\n" +
                    "#     Recibo           Fecha         Monto\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                 //   receipts +
                    " \r\n" +
                    " \r\n" +
                    "------------------------------------------------\r\n" +

                    "\r\n" +
                    "\r\n" +*/
                    "------------------------------------------------\r\n" +
                    "#     Total " + Functions.doubleToString1(printLiqContadoTotal + printLiqRecibosTotal /*+ printReceiptsTotal*/) + "\r\n" +
                    " \r\n" +
                    " \r\n" +
                    " \r\n" +
                    " \n ";

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
            Log.d("imprimeLiquidacion", bill);
        }

        else if(prefList.equals("2")){

            preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
            preview += Html.fromHtml("<h1>") + "Usuario: " + getUserName(QuickContext) + Html.fromHtml("</h1><br/><br/>");

            preview += Html.fromHtml("<h1>") + "Facturas de Contado" + Html.fromHtml("</h1><br/><br/>");

            preview += Html.fromHtml("<h1>") +  "#     Factura           Fecha         Monto" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +   ventaContado + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printLiqContadoTotal) + Html.fromHtml("</h1><br/><br/><br/>");

            preview += Html.fromHtml("<h1>") + "Facturas de Credito" + Html.fromHtml("</h1><br/><br/>");

            preview += Html.fromHtml("<h1>") +  "#     Factura           Fecha         Monto" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +   ventaCredito + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printLiqCreditoTotal) + Html.fromHtml("</h1><br/><br/><br/>");

            preview += Html.fromHtml("<h1>") + "Recibos" + Html.fromHtml("</h1><br/><br/>");

            preview += Html.fromHtml("<h1>") +   "#     Recibo           Fecha         Monto" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +   ventaRecibos + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printLiqRecibosTotal) + Html.fromHtml("</h1><br/><br/><br/>");


            preview += Html.fromHtml("<h1>") +  "#     Total " +  Functions.doubleToString1(printLiqContadoTotal + printLiqRecibosTotal /*+ printReceiptsTotal*/) + Html.fromHtml("</h1><br/><br/><br/>");

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
        }
    }

    public static void imprimirLiquidacionMenu(final Context QuickContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("¿Desea imprimir la lista de liquidación?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                datosImprimirLiquidacion(QuickContext);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        //}
    }

    //TODO imprimir DEVOLUCION

    public static void datosImprimirDevolucion(Context QuickContext) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickContext);
        String prefList = sharedPreferences.getString("pref_selec_impresora","Impresora Zebra");
        String preview = "";

        //String salesCash = getPrintSalesCash();
        String salesCredit = getPrintDevolcionProductInvoices();
        // String receipts = getPrintReceipts();
        String billptype = "D e v o l u c i o n";

        if (prefList.equals("1")){

            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    //"! U1 LMARGIN 185\r\n" +
                    String.format("%s", billptype) + "\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "Usuario: " + getUserName(QuickContext) + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "#        Producto                  \r\n" +
                    "Cantidad         Precio           Total\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    salesCredit +
                    "\r\n" +
                    "\r\n" +
                    "------------------------------------------------\r\n" +
                  //  "#     Total " + Functions.doubleToString1(printSalesCashTotal) + "\r\n" +
                    "\r\n" +
                    "\r\n" +
                   /* "recibos \r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    " \n\n" +
                    "#     Recibo           Fecha         Monto\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                 //   receipts +
                    " \r\n" +
                    " \r\n" +
                    "------------------------------------------------\r\n" +

                    "\r\n" +
                    "\r\n" +*/
                    "------------------------------------------------\r\n" +
                  //  "#     Total " + Functions.doubleToString1(printSalesCashTotal /*+ printSalesCreditTotal + printReceiptsTotal*/) + "\r\n" +
                    " \r\n" +
                    " \r\n" +
                    " \r\n" +
                    " \n ";

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
        }

        else if(prefList.equals("2")){

            preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
            //  preview += Html.fromHtml("<h1>") + "Usuario: " + getUserName(QuickContext) + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") +   "#        Producto" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +   "Cantidad         Precio           Total" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +   salesCredit + Html.fromHtml("</h1></center><br/>");
        //    preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printSalesCashTotal) + Html.fromHtml("</h1><br/><br/><br/>");
        //    preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printSalesCashTotal /*+ printSalesCreditTotal + printReceiptsTotal*/) + Html.fromHtml("</h1><br/><br/><br/>");

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
        }
    }

    public static void imprimirDevoluciónMenu(final Context QuickContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("¿Desea imprimir la lista de devolución?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                datosImprimirDevolucion(QuickContext);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        //}
    }

    private static String getPrintDevolcionProductInvoices() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();

        RealmResults<Inventario> result = realm.where(Inventario.class).notEqualTo("amount", "0").notEqualTo("amount", "0.0").findAll();

        if (result.isEmpty()) {
            send = "No hay devoluciones emitidas";
        } else {
            printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<Inventario> salesList1 = realm.where(Inventario.class).notEqualTo("amount", "0").notEqualTo("amount", "0.0").findAll();


                String factProductoId = salesList1.get(i).getProduct_id();
                double factAmount = Double.parseDouble(salesList1.get(i).getAmount());

                Realm realm1 = Realm.getDefaultInstance();
                Productos producto = realm1.where(Productos.class).equalTo("id", factProductoId).findFirst();
                String description = producto.getDescription();
                double precio = Double.parseDouble(producto.getSale_price());

                realm1.close();
             //   double factTotal = Double.parseDouble(salesList1.get(i).getTotal());

              //  String total = String.format("%,.2f",factTotal);

                send += String.format("%-1s      %.60s", factProductoId, description) + "\r\n" +
                        String.format("%-14s   %-12s    %.10s", factAmount, precio, Functions.doubleToString1(factAmount * precio)) + "\r\n";
                send += "------------------------------------------------\r\n";
                //   printSalesCashTotal = printSalesCashTotal + factTotal;

                Log.d("FACTPRODTODFAC", send + "");
            }
        }
        return send;
    }

    private static String getPrintProductInvoicesLiqContado() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<invoice> result = realm.where(invoice.class).equalTo("payment_method_id", "1").equalTo("date", currentDateandTime).equalTo("facturaDePreventa", "VentaDirecta").or().equalTo("facturaDePreventa", "Distribucion").findAll();

        if (result.isEmpty()) {
            send = "No hay facturas emitidas";
        } else {
            printLiqContadoTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<invoice> salesList1 = realm.where(invoice.class).equalTo("payment_method_id", "1").equalTo("date", currentDateandTime).equalTo("facturaDePreventa", "VentaDirecta").or().equalTo("facturaDePreventa", "Distribucion").findAll();
                String pago = salesList1.get(i).getPayment_method_id();
                String factNum = salesList1.get(i).getNumeration();
                String factFecha = salesList1.get(i).getDate();


                double factTotal = Double.parseDouble(salesList1.get(i).getTotal());

                if(pago.equals("1")){

                    if(factFecha.equals(currentDateandTime)){
                send += String.format("%-5s      %.20s      %-6s", factNum, factFecha, Functions.doubleToString1(factTotal)) + "\r\n";
                printLiqContadoTotal = printLiqContadoTotal + factTotal;

                Log.d("LiqContado", send + "");}}
                else{

                }
            }
        }
        return send;
    }

    private static String getPrintProductInvoicesLiqCredito() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<invoice> result = realm.where(invoice.class).equalTo("payment_method_id", "2").equalTo("date", currentDateandTime).equalTo("facturaDePreventa", "VentaDirecta").or().equalTo("facturaDePreventa", "Distribucion").findAll();

        if (result.isEmpty()) {
            send = "No hay facturas emitidas";
        } else {
            printLiqCreditoTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<invoice> salesList1 = realm.where(invoice.class).equalTo("payment_method_id", "2").equalTo("date", currentDateandTime).equalTo("facturaDePreventa", "VentaDirecta").or().equalTo("facturaDePreventa", "Distribucion").findAll();

                String pago = salesList1.get(i).getPayment_method_id();
                String factNum = salesList1.get(i).getNumeration();
                String factFecha = salesList1.get(i).getDate();

                double factTotal = Double.parseDouble(salesList1.get(i).getTotal());

                if(pago.equals("2")){
                    if(factFecha.equals(currentDateandTime)){

                send += String.format("%-5s      %.20s      %-6s", factNum, factFecha, Functions.doubleToString1(factTotal)) + "\r\n";
                printLiqCreditoTotal = printLiqCreditoTotal + factTotal;

                Log.d("LiqCredito", send + "");}
                }
                else{

                }
            }
        }
        return send;
    }

/*
    private static String getPrintProductRecibosLiq() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<receipts> result = realm.where(receipts.class).equalTo("date", currentDateandTime).findAll();

        if (result.isEmpty()) {
            send = "No hay facturas emitidas";
        } else {
            printLiqRecibosTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {
                List<receipts> salesList1 = realm.where(receipts.class).equalTo("date", currentDateandTime).findAll();
                Log.d("salesList1", salesList1 + "");

                String factNum = salesList1.get(i).getReference();
                String factFecha = salesList1.get(i).getDate();
                String customerID = salesList1.get(i).getCustomer_id();
                double montoPagado = salesList1.get(i).getMontoPagado();


/*
                RealmList<recibos> factTotal1 = salesList1.get(i).getListaRecibos();
                Log.d("FACTURANUEVA", factNum + factFecha + customerID + montoPagado + " ");
                Log.d("FACTURANUEVA", factTotal1 + "");
                double factTotal11 = 0;
                if (factTotal1.isEmpty()) {
                    Log.d("rep", "rep");
                } else {


                    factTotal11 = factTotal1.get(0).getMontoCanceladoPorFactura();
                    Log.d("FACTURANUEVA", factTotal11 + "");
                }



               RealmResults<recibos> result2 = realm.where(recibos.class).equalTo("customer_id", customerID).findAll();
                Log.d("FACTURANUEVA", result2 + "");


                double factTotal1 = result2.get(i).getMontoCanceladoPorFactura();
                Log.d("factTotal12", factTotal1 + "");


                double factTotal1 = salesList1.get(i).getListaRecibos().get(0).getMontoCanceladoPorFactura();
                Log.d("salesList11", salesList1.get(i).getListaRecibos() + "");

                Log.d("factTotal1", factTotal1 + "");

                // FIN COMENT

                if (montoPagado == 0.0) {
                    Log.d("es0", "es 0");
                } else {

                    send += String.format("%-5s      %.20s      %-6s", factNum, factFecha, Functions.doubleToString1(montoPagado)) + "\r\n";
                    printLiqRecibosTotal = printLiqRecibosTotal + montoPagado;
                    Log.d("LiqRecibos", send + "");
                }

            }
        }
        return send;
    }*/

    private static String getPrintProductRecibosLiq() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

       Realm realm = Realm.getDefaultInstance();
        RealmResults<receipts> result = realm.where(receipts.class).equalTo("date", currentDateandTime).findAll();

        if (result.isEmpty()) {
            send = "No hay facturas emitidas";
        } else {
            printLiqRecibosTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                Log.d("tamaño", result.size()+"");
                List<receipts> salesList1 = realm.where(receipts.class).equalTo("date", currentDateandTime).findAll();
                Log.d("salesList1", salesList1 + "");

                String numeracion = salesList1.get(i).getNumeration();
                String fecha = salesList1.get(i).getDate();
                //String totalS = String.format("%,.2f", total);

                double pagado = salesList1.get(i).getMontoCanceladoPorFactura();
                // String pagadoS = String.format("%,.2f", pagado);
              //  double restante = salesList1.get(i).getMontoCancelado();
                // String restanteS = String.format("%,.2f", restante);

                send += String.format("%-9s  %9s  %9s", numeracion, fecha, Functions.doubleToString1(pagado) ) + "\r\n";
                printLiqRecibosTotal = printLiqRecibosTotal + pagado;
                Log.d("LiqRecibosTotal", printLiqRecibosTotal + "");
                send += "------------------------------------------------\r\n";
                Log.d("LiqRecibos", send + "");
                }

            }

        return send;
    }

    private static String getPrintProductInvoicesPreventa() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<invoice> result = realm.where(invoice.class).equalTo("date", currentDateandTime).equalTo("facturaDePreventa", "Preventa").findAll();

        if (result.isEmpty()) {
            send = "No hay facturas emitidas";
        } else {
            printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<invoice> salesList1 = realm.where(invoice.class).equalTo("date", currentDateandTime).equalTo("facturaDePreventa", "Preventa").findAll();
                String idFactura = salesList1.get(i).getId();

                RealmResults<Pivot> resultPivot = realm.where(Pivot.class).equalTo("invoice_id", idFactura).findAll();

                for (int a = 0; a < resultPivot.size(); a++) {

                    List<Pivot> pivotList1 = realm.where(Pivot.class).equalTo("invoice_id", idFactura).findAll();
                    String idPivot = pivotList1.get(a).getProduct_id();
                    double precioPivot = Double.parseDouble(pivotList1.get(a).getPrice());
                    double cantidadPivot = Double.parseDouble(pivotList1.get(a).getAmount());
                  /*  RealmResults<Productos> resultProducto = realm.where(Productos.class).equalTo("id", idProducto).findAll();
                    for (int t = 0; t < resultProducto.size(); t++) {
*/
                        Productos productoList1 = realm.where(Productos.class).equalTo("id", idPivot).findFirst();
                        String descripcion = productoList1.getDescription();

                    send += String.format("%.60s", descripcion) + "\r\n" +
                            String.format("%-14s   %-12s    %.10s", precioPivot, cantidadPivot, Functions.doubleToString1(cantidadPivot * precioPivot)) + "\r\n";
                    send += "------------------------------------------------\r\n";

                  /*  }*/
                }
            }
        }
        Log.d("OrdenCarga", send + "");
        return send;

    }

    private static String getUserName(Context context) {
        SessionPrefes session = new SessionPrefes(context);

        String usuer = session.getUsuarioPrefs();
        Log.d("usuer", usuer);
        Realm realm = Realm.getDefaultInstance();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String nombreUsuario = usuarios.getUsername();
        realm.close();


     //   Session session = new Session(context);
      //  Users users = new Select().all().from(Users.class).where(Condition.column(Users$Table.ID).eq(session.getUserDetails().get(session.KEY_USERINFO).userId)).querySingle();
        return nombreUsuario;
    }


}
