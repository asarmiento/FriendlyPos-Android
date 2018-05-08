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
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.Sysconf;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class PrinterFunctions {

    private static double printSalesCashTotal = 0.0;


    //TODO imprimir TOTALIZAR DISTRIBUCION

    public static void datosImprimirDistrTotal(int type, sale sale, Context QuickContext, int ptype) {
        String stype = "";
        String payment = "";
        String messageC = "";
        String billptype = "";
        String preview = "";

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();

        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).equalTo("devuelvo", 0).findAll();
        Log.d("FACTPRODTOD", result + "");
        // VARIABLES VENTA
        String fechayhora = sale.getUpdated_at();
        String nombreCliente = sale.getCustomer_name();

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
        String totalGrabado= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_taxed()));
        String totalExento= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_exempt()));
        String totalSubtotal= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal()));
        String totalDescuento= Functions.doubleToString1(Double.parseDouble(invoice.getDiscount()));
        String totalImpuesto= Functions.doubleToString1(Double.parseDouble(invoice.getTax()));
        String totalTotal= Functions.doubleToString1(Double.parseDouble(invoice.getTotal()));
        String totalCancelado= Functions.doubleToString1(Double.parseDouble(invoice.getPaid()));
        String totalVuelto= Functions.doubleToString1(Double.parseDouble(invoice.getChanging()));
        String totalNotas= invoice.getNote();
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
                    "Vendedor:  " + nombreUsuario + "\r\n" +
                    "\r\n" +
                    "! U1 LMARGIN 50\r\n" +
                    "Cedula: " + cardCliente + " \r\n" +
                    "Razon Social: " + companyCliente + "\r\n" +
                    ((!nombreCliente.isEmpty()) ? "A nombre de: " + nombreCliente + "\r\n" : "") +
                    "Nombre fantasia: " + fantasyCliente + "\r\n" +
                    "# Telefono: " + telefonoCliente + "\r\n" +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "Descripcion           Codigo\r\n" +
                    "Cantidad      Precio       P.Sug       Total\r\n" +
                    "Tipo     \r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +

                    getPrintDistTotal(sale.getInvoice_id()) +
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
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintDistTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
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
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "#  Descripcion      Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cant     Precio       P.Sug        Total      I" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintDistTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
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

    public static void imprimirFacturaDistrTotal (final sale saleB, final Context QuickContext, final int ptype){

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

    private static void imprimirTotalizar(int type, sale invoices, Context QuickContext, int ptype) {
        try {
            if (invoices != null) {
                PrinterFunctions.datosImprimirDistrTotal(type, invoices, QuickContext, ptype);
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
//To get original string from byte.
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
                double precio = Double.parseDouble(salesList1.get(i).getPrice());

             //   String total = Functions.doubleToString1(cant * precio);
           //     double totalD = Double.parseDouble(total);

                double sugerido=0.0;

                // gravado Sugerido =( (preciode venta/1.13)*(suggested /100) )+ (preciode venta* 0.13)+(preciode venta/1.13);
                // en caso de exento Sugerido =( (preciode venta)*(suggested /100)) + (preciode venta);

                if (typeId.equals("1")){
                    nombreTipo = "Gravado";
                    sugerido = (precio/1.13)*(precioSugerido /100) + (precio * 0.13)+(precio/1.13);
                }
                else if (typeId.equals("2")){
                    nombreTipo = "Exento";
                    sugerido = (precio)*(precioSugerido /100) + (precio);
                }


                send += String.format("%s  %.24s ", description1, barcode) + "\r\n" +
                        String.format("%-12s %-10s %-12s %.10s", cant, Functions.doubleToString1(precio), Functions.doubleToString1(sugerido),Functions.doubleToString1(cant * precio)) + "\r\n" +
                        String.format("%.10s", nombreTipo) + "\r\n";
                send += "------------------------------------------------\r\n";
                Log.d("FACTPRODTODFAC", send + "");
            }
        }
        return send;
    }

    //TODO imprimir TOTALIZAR PREVENTA

    public static void datosImprimirPrevTotal(int type, sale sale, Context QuickContext, int ptype) {
        String stype = "";
        String payment = "";
        String messageC = "";
        String billptype = "";
        String preview = "";

        Realm realm = Realm.getDefaultInstance();
        Sysconf sysconf = realm.where(Sysconf.class).findFirst();
        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).findAll();
        Log.d("FACTPRODTOD", result + "");
        // VARIABLES VENTA
        String fechayhora = sale.getUpdated_at();
        String nombreCliente = sale.getCustomer_name();

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
        String totalGrabado= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_taxed()));
        String totalExento= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_exempt()));
        String totalSubtotal= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal()));
        String totalDescuento= Functions.doubleToString1(Double.parseDouble(invoice.getDiscount()));
        String totalImpuesto= Functions.doubleToString1(Double.parseDouble(invoice.getTax()));
        String totalTotal= Functions.doubleToString1(Double.parseDouble(invoice.getTotal()));
        String totalCancelado= Functions.doubleToString1(Double.parseDouble(invoice.getPaid()));
        String totalVuelto= Functions.doubleToString1(Double.parseDouble(invoice.getChanging()));
        String totalNotas= invoice.getNote();
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

        if (ptype == 1) {
            billptype = "P e d i d o";
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
                    "Factura de: ! U1 LMARGIN 350 " + metodoPago + "\r\n" +
                    ((metodoPago == "2") ? "! U1 LMARGIN 120\r\n" +
                            "Fecha Limite: ! U1 LMARGIN 350 " + messageC + "\r\n" : "\r\n") +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
                    sysNombreNegocio + "\r\n" +
                    "! U1 SETLP 5 0 24\r\n" +
                    "Fecha y hora: " + fechayhora + "\r\n" +
                    "Vendedor:  " + nombreUsuario + "\r\n" +
                  /*  "\r\n" +
                    "! U1 LMARGIN 50\r\n" +
                    "Cedula: " + cardCliente + " \r\n" +
                    "Razon Social: " + companyCliente + "\r\n" +
                    ((!nombreCliente.isEmpty()) ? "A nombre de: " + nombreCliente + "\r\n" : "") +
                    "Nombre fantasia: " + fantasyCliente + "\r\n" +
                    "# Telefono: " + telefonoCliente + "\r\n" +*/
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETSP 0\r\n" +
                    "#  Descripcion               Codigo\r\n" +
                    "Cant     Precio       P.Sug        Total      I\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +

                    getPrintPrevTotal(sale.getInvoice_id()) +
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
                 /*   preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/><br/>");
                  /*  preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "E-mail " + sysCorreo + Html.fromHtml("</h1></center><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/><br/><br/>");
                  /*  preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") +  "#  Descripcion               Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cant     Precio       P.Sug        Total      I" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
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
                 /*   preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/><br/>");
                  /*  preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Cedula " + sysIdentificacion + "  Tel. " + sysTelefono + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "E-mail " + sysCorreo + Html.fromHtml("</h1></center><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") +  "Fecha y hora: " + fechayhora + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Vendedor:  " + nombreUsuario + Html.fromHtml("</h1></center><br/><br/><br/>");
                  /*  preview += Html.fromHtml("<h1>") +  "Cedula: " + cardCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "Razon Social: " + companyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") + "A nombre de: " + nombreCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "Nombre fantasia: " + fantasyCliente + Html.fromHtml("</h1></center><br/><br/>");
                    preview += Html.fromHtml("<h1>") +  "# Telefono: " + telefonoCliente + Html.fromHtml("</h1></center><br/><br/><br/>");*/
                    preview += Html.fromHtml("<h1>") +  "#  Descripcion      Codigo" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "Cant     Precio       P.Sug        Total      I" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
                    preview += Html.fromHtml("<h1>") +    getPrintPrevTotal(sale.getInvoice_id()) + Html.fromHtml("</h1></center><br/>");
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

    public static void imprimirFacturaPrevTotal (final sale saleB, final Context QuickContext, final int ptype){

        CharSequence colors[] = new CharSequence[]{"Copia Cliente", "Copia Contabilidad", "Copia Archivo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("Seleccione la copia a imprimir?");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which) {
                    case 0:
                        imprimirTotalizarPrev(1, saleB, QuickContext, ptype);
                        break;
                    case 1:
                        imprimirTotalizarPrev(2, saleB, QuickContext, ptype);
                        break;
                    case 2:
                        imprimirTotalizarPrev(3, saleB, QuickContext, ptype);
                        break;
                }
            }
        });
        builder.show();
        //}
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

    private static String getPrintPrevTotal(String idVenta) {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", idVenta).findAll();

        if (result.isEmpty()) {
            send = "No hay invoice emitidas";
        } else {
            // printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<Pivot> salesList1 = realm.where(Pivot.class).equalTo("invoice_id", idVenta).findAll();
                Productos producto = realm.where(Productos.class).equalTo("id", salesList1.get(i).getProduct_id()).findFirst();
                //   sale ventas = realm.where(sale.class).equalTo("invoice_id", salesList1.get(i).getInvoice_id()).findFirst();
                //    Clientes clientes = realm.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();



                String description = producto.getDescription();
                byte[] byteText = description.getBytes(Charset.forName("UTF-8"));
//To get original string from byte.
                String description1 = null;
                try {
                    description1 = new String(byteText, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String barcode = producto.getBarcode();
                String typeId = producto.getProduct_type_id();
                String nombreTipo = null;
                if (typeId.equals("1")){
                    nombreTipo = "Gravado";
                }
                else if (typeId.equals("2")){
                    nombreTipo = "Exento";

                }
                double cant = Double.parseDouble(salesList1.get(i).getAmount());
                double precio = Double.parseDouble(salesList1.get(i).getPrice());


                String total = String.valueOf(cant * precio);
                Log.d("r", total + "");
                String total2 = String.format("%,.2f", cant * precio);
                Log.d("r", total2 + "");
                //String total3 = String.format("%.,2f", cant * precio);
                //  Log.d("r", total3 + "");
                String total4 = String.format(Locale.FRANCE, "%1$,.2f", cant * precio);
                Log.d("r", total4 + "");
              /*  String factFecha = salesList1.get(i).getDate();
                double factTotal = Functions.sGetDecimalStringAnyLocaleAsDouble(salesList1.get(i).getTotal());*/

                send += String.format("%s  %.24s ", description1, barcode) + "\r\n" +
                        String.format("%-5s %-10s %-15s %-12s %.1s", cant /*bill.amount*/, precio, precio, Functions.doubleToString1(cant * precio), nombreTipo) + "\r\n";
                send += "------------------------------------------------\r\n";
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
            preview += Html.fromHtml("<h1>") + "N# Factura: " + numeracionFactura + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") + sysNombre + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") + sysNombreNegocio + Html.fromHtml("</h1><br/><br/>");
            preview += Html.fromHtml("<h1>") + sysDireccion + Html.fromHtml("</h1><br/><br/>");
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
                   /* "Recibos \r\n" +
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
        String salesCredit = getPrintProductInvoices();
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
                    "! U1 SETSP 0\r\n" +
                    "#     Factura           Fecha         Monto\r\n" +
                    "------------------------------------------------\r\n" +
                    "! U1 SETLP 7 0 10\r\n" +
                    "\r\n" +
                    "\r\n" +
                    salesCredit +
                    "\r\n" +
                    "\r\n" +
                    "------------------------------------------------\r\n" +
                    "#     Total " + Functions.doubleToString1(printSalesCashTotal) + "\r\n" +
                    "\r\n" +
                    "\r\n" +
                   /* "Recibos \r\n" +
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
                    "#     Total " + Functions.doubleToString1(printSalesCashTotal /*+ printSalesCreditTotal + printReceiptsTotal*/) + "\r\n" +
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
            preview += Html.fromHtml("<h1>") +  "#     Factura           Fecha         Monto" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "------------------------------------------------" + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +   salesCredit + Html.fromHtml("</h1></center><br/>");
            preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printSalesCashTotal) + Html.fromHtml("</h1><br/><br/><br/>");
            preview += Html.fromHtml("<h1>") +  "#     Total " + Functions.doubleToString1(printSalesCashTotal /*+ printSalesCreditTotal + printReceiptsTotal*/) + Html.fromHtml("</h1><br/><br/><br/>");

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
                   /* "Recibos \r\n" +
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

       // RealmResults<invoice> result = realm.where(invoice.class).equalTo("date", currentDateandTime).findAll();

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

    private static String getPrintProductInvoices() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<invoice> result = realm.where(invoice.class).equalTo("date", currentDateandTime).findAll();

        if (result.isEmpty()) {
            send = "No hay facturas emitidas";
        } else {
            printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<invoice> salesList1 = realm.where(invoice.class).equalTo("date", currentDateandTime).findAll();

                String factNum = salesList1.get(i).getNumeration();
                String factFecha = salesList1.get(i).getDate();

                double factTotal = Double.parseDouble(salesList1.get(i).getTotal());


                send += String.format("%-5s      %.20s      %-6s", factNum, factFecha, Functions.doubleToString1(factTotal)) + "\r\n";
                printSalesCashTotal = printSalesCashTotal + factTotal;

                Log.d("FACTPRODTODFACSI", send + "");
            }
        }
        Log.d("FACTPRODTODFAC", send + "");
        return send;
    }

    private static String getPrintProductInvoicesPreventa() {
        String send = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        Realm realm = Realm.getDefaultInstance();
        RealmResults<invoice> result = realm.where(invoice.class).equalTo("date", currentDateandTime).equalTo("facturaDePreventa", 1).findAll();

        if (result.isEmpty()) {
            send = "No hay facturas emitidas";
        } else {
            printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<invoice> salesList1 = realm.where(invoice.class).equalTo("date", currentDateandTime).equalTo("facturaDePreventa", 1).findAll();
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
