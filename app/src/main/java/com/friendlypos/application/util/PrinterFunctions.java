package com.friendlypos.application.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Sysconf;

import java.util.List;
import java.util.Locale;
import io.realm.Realm;
import io.realm.RealmResults;

public class PrinterFunctions {

    public static void datosImprimir(int type, Venta venta, Context QuickContext, int ptype) {
        String stype = "";
        String payment = "";
        String messageC = "";
        String billptype = "";
        String preview = "";

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", venta.getCustomer_id()).findFirst();
        Facturas facturas = realm.where(Facturas.class).equalTo("id", venta.getInvoice_id()).findFirst();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", venta.getInvoice_id()).findAll();
        Log.d("FACTPRODTOD", result + "");
        // VARIABLES VENTA
        String fechayhora = venta.getUpdated_at();
        String nombreCliente = venta.getCustomer_name();

        // VARIABLES CLIENTES
        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();
        String telefonoCliente = clientes.getPhone();
        double descuentoCliente = Double.parseDouble(clientes.getFixedDiscount());

        // VARIABLES FACTURA
        String fechaFactura =facturas.getDue_date();
        String numeracionFactura = facturas.getNumeration();
        String metodoPago = facturas.getPayment_method_id();
        String totalGrabado= facturas.getSubtotal_taxed();
        String totalExento= facturas.getSubtotal_exempt();
        String totalSubtotal= facturas.getSubtotal();
        String totalDescuento= facturas.getDiscount();
        String totalImpuesto= facturas.getTax();
        String totalTotal= facturas.getTotal();
        String totalCancelado= facturas.getPaid();
        String totalVuelto= facturas.getChanging();
        String totalNotas= facturas.getNote();

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

        if (ptype == 1) {
            billptype = "F a c t u r a";
        } else if (ptype == 2) {
            billptype = "P r o f o r m a";
        } else if (ptype == 3) {
            billptype = "Orden de pedido";
        }

        if (type == 1) {
            stype = "Original";
        } else if (type == 2) {
            stype = "Contabilidad";
        } else if (type == 3) {
            stype = "Archivo";
        }
        if (prefList.equals("1")){
            String bill = "! U1 JOURNAl\r\n" +
                    "! U1 SETLP 0 0 0\r\n" +
                    "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    "! U1 LMARGIN 185\r\n" +
                    String.format("%s", billptype) + "\r\n" +
                    "! U1 LMARGIN 150\r\n" +
                    "! U1 SETLP 7 0 14\r\n" + "\r\n" +
                    "N# Factura: " + numeracionFactura + "\r\n" +
                    "! U1 LMARGIN 120\r\n" +
                    "Factura de: ! U1 LMARGIN 350 " + payment + "\r\n" +
                    ((metodoPago == "2") ? "! U1 LMARGIN 120\r\n" +
                            "Fecha Limite: ! U1 LMARGIN 350 " + messageC + "\r\n" : "\r\n") +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
                    sysNombre + "\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
                    sysNombreNegocio + "\r\n" +
                    "! U1 SETLP 5 0 24\r\n" +
                    sysDireccion + "\r\n" +
                    "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + "\r\n" +
                    "E-mail " + sysCorreo + "\r\n" +
                    "! U1 SETLP 7 0 14\r\n" +
                    "Fecha y hora: " + fechayhora + "\r\n" +
                    // TODO REVISAR LOS USUARIOS
              //      "Vendedor:  " + sale.users.name + "\r\n" +
                    "\r\n" +
                    "! U1 LMARGIN 50\r\n" +
                    "Cedula: " + cardCliente + " \r\n" +
                    "Razon Social: " + companyCliente + "\r\n" +
                    ((!nombreCliente.isEmpty()) ? "A nombre de: " + nombreCliente + "\r\n" : "") +
                    "Nombre fantasia: " + fantasyCliente + "\r\n" +
                    "# Telefono: " + telefonoCliente + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "#  Descripcion               Codigo\r\n" +
                    "Cant     Precio       P.Sug        Total      I\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    // TODO REVISAR TODOS LOS PRODUCTOS
                  // getPrintProducts(Functions.getProducsByBillForPrinting(sale.invoices.id)) +
                    "\r\n" +

                    String.format("%20s %-20s", "Subtotal Gravado", totalGrabado) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal Exento", totalExento) + "\r\n" +
                    String.format("%20s %-20s", "Subtotal", totalSubtotal) + "\r\n" +
                    String.format("%20s %-20s", "Descuento", totalDescuento) + "\r\n" +
                    String.format("%20s %-20s", "IVA", totalImpuesto) + "\r\n" +
                    "! U1 SETLP 5 3 70\r\n" +
                    String.format("%20s %-20s", "Total", totalTotal) + "\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    String.format("%20s %-20s", "Cancelado con", totalCancelado) + "\r\n" +
                    String.format("%20s %-20s", "Cambio", totalVuelto) + "\r\n" +
                    "\r\n\n" + "Notas: " + totalNotas + "\r\n" +
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
                    "! U1 SETLP 5 0 14\r\n" +
                    "\r\n" + String.format("Factura %s", stype) + "\r\n\n" +
                    "\r\n\n" + "Muchas Gracias por preferirnos, un placer atenderlo\r\n" +
                    "Mantenga el documento para reclamos ." + "\r\n" + "\r\n" +
                    " Autorizado mediante oficio\r\n" +
                    "N° : 11-1997 de la D.G.T.D\r\n" +
                    " \n\n" +
                    " \n\n" +
                    " \n ";
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", bill);
            QuickContext.sendBroadcast(intent2);
        }
        else if(prefList.equals("2")){
            switch (metodoPago) {
                case "1":
                    payment = "Contado";
                    preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Factura de: " + payment + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "E-mail " + sysCorreo + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1></center><br/><br/>");
                    //preview += Html.fromHtml("<h1>") +  "Vendedor:  " + sale.users.name + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "#  Descripcion               Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cant     Precio       P.Sug        Total      I" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                //    preview += Html.fromHtml("<h1>") +  getPrintProducts(Functions.getProducsByBillForPrinting(sale.invoices.id)) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto) + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total", totalTotal) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Cancelado con", totalCancelado) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Cambio", totalVuelto) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Recibo conforme ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + String.format("Factura %s", stype) +  Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Autorizado mediante oficio" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "N : 11-1997 de la D.G.T.D" +  Html.fromHtml("</h1></center><br/>");
                    break;
                case "2":
                    payment = "Credito";
                    messageC = fechaFactura;
                    preview += Html.fromHtml("<h1>") + String.format("%s", billptype) + Html.fromHtml("</h1><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Factura de: " + payment + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Fecha Limite: " + messageC + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "E-mail " + sysCorreo + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1></center><br/><br/>");
                    //preview += Html.fromHtml("<h1>") +  "Vendedor:  " + sale.users.name + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "#  Descripcion               Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cant     Precio       P.Sug        Total      I" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                   // preview += Html.fromHtml("<h1>") +  getPrintProducts(Functions.getProducsByBillForPrinting(sale.invoices.id)) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Gravado", totalGrabado) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal Exento", totalExento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Subtotal", totalSubtotal) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Descuento", totalDescuento) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "IVA", totalImpuesto) + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Total", totalTotal) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  String.format("%20s %-20s", "Cancelado con", totalCancelado) + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   String.format("%20s %-20s", "Cambio", totalVuelto) + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Notas: " + totalNotas + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Recibo conforme ____________________________" + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula ____________________________" +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Firma y Cedula __________________________" +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  condition +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + String.format("Factura %s", stype) +  Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Muchas gracias por preferirnos un placer atenderlo" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "Mantenga el documento para reclamos." +  Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>")   + "Autorizado mediante oficio" +  Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>")   + "N : 11-1997 de la D.G.T.D" +  Html.fromHtml("</h1></center><br/>");
                    break;
            }
            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
        }
    }

    public static void imprimirFacturaDistrTotal (final Venta saleB, final Context QuickContext, final int ptype){

        CharSequence colors[] = new CharSequence[]{"Copia Cliente", "Copia Contabilidad", "Copia Archivo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("Seleccione la copia a imprimir?");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which) {
                    case 0:
                        imprimirTotalizar(1, saleB, QuickContext, ptype);
                        break;
                    case 1:
                        imprimirTotalizar(2, saleB, QuickContext, ptype);
                        break;
                    case 2:
                        imprimirTotalizar(3, saleB, QuickContext, ptype);
                        break;
                }
            }
        });
        builder.show();
        //}
        }

    private static void imprimirTotalizar(int type, Venta invoices, Context QuickContext, int ptype) {
        try {
            if (invoices != null) {
                PrinterFunctions.datosImprimir(type, invoices, QuickContext, ptype);
            } else {
                Toast.makeText(QuickContext, "Aun le falta terminar de hacer la factura" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error PrinterFunctions", e.getMessage());
        }
    }

    public static void imprimirProductosSelecClientes(Venta venta, Context QuickContext) {

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", venta.getCustomer_id()).findFirst();
        Facturas facturas = realm.where(Facturas.class).equalTo("id", venta.getInvoice_id()).findFirst();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", venta.getInvoice_id()).findAll();
        Log.d("FACTPRODTOD", result + "");
        // VARIABLES VENTA
        String fechayhora = venta.getUpdated_at();
        String nombreCliente = venta.getCustomer_name();

        // VARIABLES CLIENTES
        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();

        // VARIABLES FACTURA
        String numeracionFactura = facturas.getNumeration();

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
                    "#     Descripcion           Codigo\r\n" +
                    "Cant I\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
             //       getPrintProducts(Functions.getProducsByBillForPrinting(sale.invoice.id)) +
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
            preview += Html.fromHtml("<h1>") + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
            preview += Html.fromHtml("<h1>") +  "#  Descripcion               Codigo" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "Cant     I" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
         //   preview += Html.fromHtml("<h1>") +   getPrintProducts(Functions.getProducsByBillForPrinting(sale.invoice.id)) + Html.fromHtml("</h1></center><br/>");

            Intent intent2 = new Intent(PrinterService.BROADCAST_CLASS);
            intent2.putExtra(PrinterService.BROADCAST_CLASS + "TO_PRINT", "true");
            intent2.putExtra("bill_to_print", preview);
            QuickContext.sendBroadcast(intent2);
        }
    }

    public static void imprimirProductosDistrSelecCliente(final Venta sale, final Context QuickContext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("Impresión de Productos?");
        builder.setMessage("¿Desea imprimir la lista de productos para esta factura?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imprimirProductosSelecClientes(sale, QuickContext);
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


}
