package com.friendlypos.reimpresion.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.Sysconf;
import com.friendlypos.reimpresion.activity.ReimprimirActivity;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ReimprimirResumenFragment extends BaseFragment {
    @Bind(R.id.html_text)
    public HtmlTextView text;

    @Bind(R.id.btnReimprimirFactura)
    public ImageButton btnReimprimirFactura;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    VentaDirectaActivity activity;
    sale sale_actualizada = null;
    String facturaId = "";
    String nombreMetodoPago;
    int slecTAB;
    String tipoFacturacion;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(getContext());
        activity = new VentaDirectaActivity();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reimprimir_resumen, container, false);
        text = (HtmlTextView) rootView.findViewById(R.id.html_text);
        ButterKnife.bind(this, rootView);


        btnReimprimirFactura.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String a = "1";
                if (sale_actualizada.getSale_type() == "2") {
                    a = "2";
                }
                if (bluetoothStateChangeReceiver.isBluetoothAvailable() == true) {

                    tipoFacturacion = sale_actualizada.getFacturaDePreventa();

                    if (tipoFacturacion.equals("Distribucion")) {

                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        View promptView = layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null);

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setView(promptView);
                        final CheckBox checkbox = (CheckBox) promptView.findViewById(R.id.checkbox);

                        final TextView label = (TextView) promptView.findViewById(R.id.promtClabelRecibosImp);
                        label.setText("Escriba el número de impresiones requeridas");

                        final EditText input = (EditText) promptView.findViewById(R.id.promtCtextRecibosImp);

                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                String cantidadImpresiones = input.getText().toString();

                        PrinterFunctions.imprimirFacturaDistrTotal(sale_actualizada, getActivity(), 1, cantidadImpresiones);
                        Toast.makeText(getActivity(), "imprimir Totalizar Dist", Toast.LENGTH_SHORT).show();

                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertD = alertDialogBuilder.create();
                        alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        alertD.show();


                    } else if (tipoFacturacion.equals("VentaDirecta")) {

                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        View promptView = layoutInflater.inflate(R.layout.prompt_imprimir_recibos, null);

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setView(promptView);
                        final CheckBox checkbox = (CheckBox) promptView.findViewById(R.id.checkbox);

                        final TextView label = (TextView) promptView.findViewById(R.id.promtClabelRecibosImp);
                        label.setText("Escriba el número de impresiones requeridas");

                        final EditText input = (EditText) promptView.findViewById(R.id.promtCtextRecibosImp);

                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                String cantidadImpresiones = input.getText().toString();


                        PrinterFunctions.imprimirFacturaVentaDirectaTotal(sale_actualizada, getActivity(), 3, cantidadImpresiones);
                        Toast.makeText(getActivity(), "imprimir Totalizar VentaD", Toast.LENGTH_SHORT).show();

                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertD = alertDialogBuilder.create();
                        alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        alertD.show();

                    }
                }

                else if(bluetoothStateChangeReceiver.isBluetoothAvailable() == false){
                    Functions.CreateMessage(getActivity(), "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                }
            }
        });

        return rootView;
    }


    private void getHtmlPreview() {
        try {

            Realm realm = Realm.getDefaultInstance();
            Sysconf sysconf = realm.where(Sysconf.class).findFirst();
            Clientes clientes = realm.where(Clientes.class).equalTo("id", sale_actualizada.getCustomer_id()).findFirst();
            invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale_actualizada.getInvoice_id()).findFirst();
            RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", sale_actualizada.getInvoice_id()).findAll();

            String idUsuario = invoice.getUser_id();

            Usuarios usuarios = realm.where(Usuarios.class).equalTo("id", idUsuario).findFirst();

            String nombreUsuario = usuarios.getUsername();

            // VARIABLES VENTA
            String fechayhora = sale_actualizada.getUpdated_at();
            String nombreCliente = sale_actualizada.getCustomer_name();

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

            if (metodoPago.equals("1")){
                nombreMetodoPago = "Contado";
            }
            else if(metodoPago.equals("2")){
                nombreMetodoPago = "Crédito";
            }

            String totalGrabado= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_taxed()));
            String totalExento= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal_exempt()));
            String totalSubtotal= Functions.doubleToString1(Double.parseDouble(invoice.getSubtotal()));
            String totalDescuento= Functions.doubleToString1(Double.parseDouble(invoice.getDiscount()));
            String totalImpuesto= Functions.doubleToString1(Double.parseDouble(invoice.getTax()));
            String totalTotal= Functions.doubleToString1(Double.parseDouble(invoice.getTotal()));
            String totalCancelado= Functions.doubleToString1(Double.parseDouble(invoice.getPaid()));
            String totalVuelto= Functions.doubleToString1(Double.parseDouble(invoice.getChanging()));
            String totalNotas= invoice.getNote();


            // VARIABLES SYSCONF
            String sysNombre = sysconf.getName();
            String sysNombreNegocio = sysconf.getBusiness_name();
            String sysDireccion = sysconf.getDirection();
            String sysIdentificacion = sysconf.getIdentification();
            String sysTelefono = sysconf.getPhone();
            String sysCorreo = sysconf.getEmail();
            realm.close();

            String preview = "";
            String condition = "Esta factura constituye titulo ejecutivo al tenor del articulo 460 del codigo de comercio. "
                + "El deudor renuncia a los requerimientos de pago, domicilio y tramites del juicio ejecutivo. "
                + "El suscrito da fe, bajo la gravedad de juramento que se encuentra facultado y autorizado para firmar esta factura, "
                + "por su representada, conforme al articulo supracitado. Si realiza pago mediante transferencia electronica de "
                + "fondos o cualquier otro medio que no sea efectivo, la validez del pago queda sujeto a su acreditacion en las cuentas "
                + "bancarias de " + sysNombre + ", Por lo cual la factura original le sera entregada una vez confirme dicha acreditacion ";

            if (sale_actualizada != null) {


                String billString = "";
                if (sale_actualizada.getSale_type().equals("1")) {
                    billString = "Factura";
                }
                else if (sale_actualizada.getSale_type().equals("2")) {
                    billString = "Factura";
                }
                else if (sale_actualizada.getSale_type().equals("3")) {
                    billString = "Proforma";
                }


                preview += "<center><h2>" + billString + " a " + nombreMetodoPago + "</h2>";
                preview += "<h5>" + billString + " #" + numeracionFactura + "</h3>";
                preview += "<center><h2>" + sysNombre + "</h2></center>";
                preview += "<center><h4>" + sysNombreNegocio + "</h4></center>";
                preview += "<h6>" + sysDireccion + "</h2></center>";
                preview += "<a><b>Tel:</b> " + sysTelefono + "</a><br>";
                preview += "<a><b>E-mail:</b> " + sysCorreo + "</a><br>";
                preview += "<a><b>Cedula Juridica:</b> " + sysIdentificacion + "</a><br>";
                preview += "<a><b>Fecha:</b> " + fechayhora + "</a><br>";
                preview += "<a><b>Fecha de impresión:</b> " + Functions.getDate() + " " + Functions.get24Time() + "</a><br><br>";
                preview += "<a><b>Vendedor:</b> " + nombreUsuario + "</a><br>";
                preview += "<a><b>ID Cliente:</b> " + cardCliente + "</a><br>";
                preview += "<a><b>Cliente:</b> " + companyCliente + "</a><br>";
                preview += "<a><b>A nombre de:</b> " + nombreCliente + "</a><br><br>";
             /*   preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
*/
                preview += "<a><b>" + padRight("Descripcion", 10) + "\t\t" + padRight("Codigo", 10) + "</b></a><br>";
                preview += "<a><b>" + padRight("Cantidad", 10) + padRight("Precio", 10) + padRight("P.Sug", 10) + padRight("Total", 10)+ "</b></a><br>";
                preview += "<a><b>" + padRight("Tipo", 10)+"</b></a><br>";
                preview += "<a>------------------------------------------------<a><br>";

                preview += getPrintDistTotal(sale_actualizada.getInvoice_id());
                preview += "<center><a>" + String.format("%20s %-20s", "Subtotal Gravado", totalGrabado) + "</a><br>";
                preview += "<a> " + String.format("%20s %-20s", "Subtotal Exento", totalExento) + "</a><br>";
                preview += "<a> " + String.format("%20s %-20s", "Subtotal", totalSubtotal) + "</a><br>";
                preview += "<a> " + String.format("%20s %-20s", "IVA", totalImpuesto) + "</a><br>";
                preview += "<a> " + String.format("%20s %-20s", "Descuento", totalDescuento) + "</a><br>";
                preview += "<a> " + String.format("%20s %-20s", "Total", totalTotal) + "</a><br><br></center>";
                preview += "<a><b>Notas:</b> " + totalNotas + "</a><br>";
                preview += "<a><b>Firma y Cedula:_______________________________</b></a><br>";
                if (metodoPago == "2") {
                    preview += "<br><br><font size=\"7\"><p>" + condition + "</p></font>";
                }

                preview += "<a>" +
                    " Autorizado mediante oficio <br>" +
                    "N° : 11-1997 de la D.G.T.D  </a>";

            }
            else {
                preview += "<center><h2>Seleccione la factura a ver</h2></center>";
            }
            //HtmlTextView.
            text.setHtmlFromString(preview, new HtmlTextView.LocalImageGetter());
        }
        catch (Exception e) {
            String preview = "<center><h2>Seleccione la factura a ver cath</h2></center>";
            text.setHtmlFromString(preview, new HtmlTextView.LocalImageGetter());
            Log.d("adsdad", e.getMessage());
        }
    }

    public static String padRight(String s, double n) {
        String centeredString;
        double pad = (n + 4) - s.length();

        if (pad > 0) {
            String pd = Functions.paddigTabs((int) (pad / 2.0));
            centeredString = "\t" + s + "\t" + pd;
            System.out.println("pad: " + "|" + centeredString + "|");
        }
        else {
            centeredString = "\t" + s + "\t";
        }
        return centeredString;
    }

    private static String getPrintDistTotal(String idVenta) {
        String send = "";

        Realm realm1 = Realm.getDefaultInstance();
        RealmResults<Pivot> result = realm1.where(Pivot.class).equalTo("invoice_id", idVenta).equalTo("devuelvo", 0).findAll();

        if (result.isEmpty()) {
            send = "No hay invoice emitidas";
        }
        else {
            // printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<Pivot> salesList1 = realm1.where(Pivot.class).equalTo("invoice_id", idVenta).equalTo("devuelvo", 0).findAll();
                Productos producto = realm1.where(Productos.class).equalTo("id", salesList1.get(i).getProduct_id()).findFirst();

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
                double precio = Double.parseDouble(salesList1.get(i).getPrice());

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

              /*  String factFecha = salesList1.get(i).getDate();
                double factTotal = Functions.sGetDecimalStringAnyLocaleAsDouble(salesList1.get(i).getTotal());*/
                send += String.format("%s  %.24s ", description1, barcode) + "<br>" +
                        String.format("%-12s %-10s %-12s %.10s", cant, Functions.doubleToString1(precio), Functions.doubleToString1(sugerido),Functions.doubleToString1(cant * precio)) + "<br>" +
                        String.format("%.10s", nombreTipo) + "<br>";
                send += "<a>------------------------------------------------<a><br>";


              /*  send += String.format("%s  %.24s ", description, barcode) + "<br>" +
                    String.format("%-5s %-10s %-10s %-15s %.1s", cant /*bill.amount, precio, precio, Functions.doubleToString(cant * precio), typeId) + "<br>";
                send += "<a>------------------------------------------------<a><br>";*/
                Log.d("FACTPRODTODFAC", send + "");

            }
            realm1.close();
        }
        return send;
    }

    @Override
    public void updateData() {
        slecTAB = ((ReimprimirActivity) getActivity()).getSelecFacturaTab();

        if (slecTAB == 1){

        facturaId = ((ReimprimirActivity) getActivity()).getInvoiceIdReimprimir();
        if (facturaId != null) {
            Log.d("FACTURAIDReim", facturaId);
        }

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {

                sale_actualizada = realm3.where(sale.class).equalTo("invoice_id", facturaId).findFirst();

                realm3.close();
            }
        });

        String a = "1";
        if (sale_actualizada.getSale_type() == "2") {
            a = "2";
        }
        getHtmlPreview();
        // PrinterFunctions.imprimirFacturaDistrTotal(sale_actualizada, getActivity(), Integer.parseInt(a));
    }
        else{
        Toast.makeText(getActivity(),"nadaSelecProducto",Toast.LENGTH_LONG).show();
    }}
}


