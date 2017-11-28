package com.friendlypos.application.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.runModelo.RunSale;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Sysconf;

import io.realm.Realm;


public class PrinterFunctions {
/*
    public static void printBillPrinter(Context QuickContext, RunSale sale, Sysconf company, int type, int ptype) {
        String stype = "";
        String payment = "";
        String messageC = "";
        String billptype = "";
        String preview = "";

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
                    "N# Factura: " + sale.invoices.getNumeration() + "\r\n" +
                    "! U1 LMARGIN 120\r\n" +
                    "Factura de: ! U1 LMARGIN 350 " + payment + "\r\n" +
                    ((sale.invoices.getPayment_method_id() == "2") ? "! U1 LMARGIN 120\r\n" +
                            "Fecha Limite: ! U1 LMARGIN 350 " + messageC + "\r\n" : "\r\n") +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
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
    }*/

    public static void datosImprimir(int type, Venta invoices, Context QuickContext, int ptype) {

        Realm realm = Realm.getDefaultInstance();


        Clientes clientes = realm.where(Clientes.class).equalTo("id", invoices.getCustomer_id()).findFirst();
        final Facturas facturas = realm.where(Facturas.class).equalTo("id", invoices.getInvoice_id()).findFirst();

        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();
        String numeracionFactura = facturas.getNumeration();

        String stype = "";
        String payment = "";
        String messageC = "";
        String billptype = "";
        String preview = "";

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
                    ((facturas.getPayment_method_id() == "2") ? "! U1 LMARGIN 120\r\n" +
                            "Fecha Limite: ! U1 LMARGIN 350 " + messageC + "\r\n" : "\r\n") +
                    "! U1 LMARGIN 0\r\n" +
                    "! U1 SETLP 5 1 35\r\n" +
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



    }

    public static void imprimirFacturaDistrTotal (final Venta saleB, final Context QuickContext, final int ptype)

    {

        CharSequence colors[] = new CharSequence[]{"Copia Cliente", "Copia Contabilidad", "Copia Archivo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("Seleccione la copia a imprimir?");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which) {
                    case 0:
                        imprimir(1, saleB, QuickContext, ptype);
                        break;
                    case 1:
                        imprimir(2, saleB, QuickContext, ptype);
                        break;
                    case 2:
                        imprimir(3, saleB, QuickContext, ptype);
                        break;
                }
            }
        });
        builder.show();
        //}
        }

    private static void imprimir(int type, Venta invoices, Context QuickContext, int ptype) {
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

   /* public static void dialogPrintBill(final RunSale saleB, final Context QuickContext, final Sysconf sysconf, final CoordinatorLayout coordinatorLayout, final int ptype) {

        /*if(!Functions.blueToothDevicePair(QuickContext)){
            Functions.CreateMessage(QuickContext,"Impresión","Porfavor conecte una impresora para imprimir.");
        }else {
        CharSequence colors[] = new CharSequence[]{"Copia Cliente", "Copia Contabilidad", "Copia Archivo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(QuickContext);
        builder.setTitle("Seleccione la copia a imprimir?");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch (which) {
                    case 0:
                        print(1, saleB, QuickContext, sysconf, coordinatorLayout, ptype);
                        break;
                    case 1:
                        print(2, saleB, QuickContext, sysconf, coordinatorLayout, ptype);
                        break;
                    case 2:
                        print(3, saleB, QuickContext, sysconf, coordinatorLayout, ptype);
                        break;
                }
            }
        });
        builder.show();
        //}
    }
    private static void print(int type, RunSale saleB, Context QuickContext, Sysconf sysconf, CoordinatorLayout coordinatorLayout, int ptype) {
        try {
            if (saleB != null) {
                PrinterFunctions.printBillPrinter(QuickContext, saleB, sysconf, type, ptype);
            } else {
                Toast.makeText(QuickContext, "Aun le falta terminar de hacer la factura" , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error PrinterFunctions", e.getMessage());
        }
    }*/
}
