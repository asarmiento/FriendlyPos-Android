package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.Sysconf;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;


public class RecibosAplicarFragment extends BaseFragment {


    private static Button applyBill;
    private static Button printBill;
    RecibosActivity activity;
    private static int apply_done = 0;
    GPSTracker gps;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    SessionPrefes session;
    double latitude;
    double longitude;
    int slecTAB;
    EditText txtFecha;
    String facturaId;
    Recibos recibo_actualizado;
    public HtmlTextView text;
    private static EditText observaciones;
    String observ;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (RecibosActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(activity);
        session = new SessionPrefes(getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recibos_aplicar, container, false);

        observaciones = (EditText) rootView.findViewById(R.id.txtRecibosObservaciones);
        applyBill = (Button) rootView.findViewById(R.id.aplicarRecibo);
        printBill = (Button) rootView.findViewById(R.id.imprimirRecibo);
        text = (HtmlTextView) rootView.findViewById(R.id.html_textRecibos);
        txtFecha = (EditText) rootView.findViewById(R.id.txtFecha);
        txtFecha.setText(getCurrentDate());
        txtFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                showDatePickerDialog();
            }
        });

        if (apply_done == 1) {
            applyBill.setVisibility(View.GONE);
            printBill.setVisibility(View.VISIBLE);
        }
        else {
            applyBill.setVisibility(View.VISIBLE);
            printBill.setVisibility(View.GONE);
        }


        applyBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            aplicarFactura() ;
                          /*  if (metodoPagoCliente.equals("1")) {

                                int tabCliente = 0;
                                ((PreventaActivity) getActivity()).setSelecClienteTabPreventa(tabCliente);

                                Toast.makeText(getActivity(), "Contado", Toast.LENGTH_LONG).show();
                                obtenerLocalización();
                                aplicarFactura();
                            }

                            else if (metodoPagoCliente.equals("2")) {
                                Toast.makeText(getActivity(), "Crédito", Toast.LENGTH_LONG).show();
                                obtenerLocalización();
                                aplicarFactura();

                            }
                            actualizarFactura();*/

                        }
                        catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                });


        printBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        try {

                            if (bluetoothStateChangeReceiver.isBluetoothAvailable() == true) {

                            /*    sale ventaDetallePreventa = activity.getCurrentVenta();
                                ventaDetallePreventa.getInvoice_id();
                                tipoFacturacionImpr = ventaDetallePreventa.getFacturaDePreventa();

                                if(tipoFacturacionImpr.equals("Preventa")){
                                    PrinterFunctions.imprimirFacturaPrevTotal(sale_actualizada, getActivity(), 1);

                                }
                                else if(tipoFacturacionImpr.equals("Proforma")){
                                    PrinterFunctions.imprimirFacturaProformaTotal(sale_actualizada, getActivity(), 1);

                                }
*/

                            }
                            else if (bluetoothStateChangeReceiver.isBluetoothAvailable() == false) {
                                Functions.CreateMessage(getActivity(), "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                            }


                        }
                        catch (Exception e) {
                            Functions.CreateMessage(getActivity(), "Error", e.getMessage() + "\n" + e.getStackTrace().toString());
                        }
                    }
                }

        );
        return rootView;




    }

    public void updateData() {
        slecTAB = activity.getSelecClienteTabRecibos();
        if (slecTAB == 1) {
            //  paid.getText().clear();

           /* totalGrabado = ((PreventaActivity) getActivity()).getTotalizarSubGrabado();
            totalExento = ((PreventaActivity) getActivity()).getTotalizarSubExento();
            totalSubtotal = ((PreventaActivity) getActivity()).getTotalizarSubTotal();
            totalDescuento = ((PreventaActivity) getActivity()).getTotalizarDescuento();
            totalImpuesto = ((PreventaActivity) getActivity()).getTotalizarImpuestoIVA();
            totalTotal = ((PreventaActivity) getActivity()).getTotalizarTotal();*/
            facturaId = activity.getInvoiceIdRecibos();
            final String clienteId = activity.getClienteIdRecibos();
            // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
            final Realm realm3 = Realm.getDefaultInstance();
            realm3.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm3) {

                    recibo_actualizado = realm3.where(Recibos.class).equalTo("customer_id", clienteId).findFirst();




                //    recibo_actualizado = realm3.where(Recibos.class).equalTo("invoice_id", facturaId).findFirst();

                    realm3.close();
                }
            });


            getHtmlPreview();
          /*  subGra.setText(String.format("%,.2f", totalGrabado));
            subExe.setText(String.format("%,.2f", totalExento));

            subT.setText(String.format("%,.2f", totalSubtotal));
            discount.setText(String.format("%,.2f", totalDescuento));

            ivaSub.setText(String.format("%,.2f", totalImpuesto));
            Total.setText(String.format("%,.2f", totalTotal));*/

            Log.d("FACTURAIDTOTALIZAR", clienteId);
        }
        else {
            Log.d("nadaTotalizarupdate",  "nadaTotalizarupdate");
        }

    }

    public void obtenerLocalización() {

        gps = new GPSTracker(getActivity());

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        }
        else {
            gps.showSettingsAlert();


        }

    }

    private void getHtmlPreview() {
        try {

            Realm realm = Realm.getDefaultInstance();
            Clientes clientes = realm.where(Clientes.class).equalTo("id", recibo_actualizado.getCustomer_id()).findFirst();
            //invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale_actualizada.getInvoice_id()).findFirst();
            RealmResults<Recibos> result = realm.where(Recibos.class).equalTo("invoice_id", recibo_actualizado.getInvoice_id()).findAll();

           // String idUsuario = invoice.getUser_id();

          //  Usuarios usuarios = realm.where(Usuarios.class).equalTo("id", idUsuario).findFirst();

           // String nombreUsuario = usuarios.getUsername();

            // VARIABLES VENTA
            String fechayhora = recibo_actualizado.getDate();
            String nombreCliente = clientes.getFantasyName();

            String preview = "";


            if (recibo_actualizado != null) {


                preview += "<h5>" + "Recibos" + "</h5>";

                preview += "<a><b>A nombre de:</b> " + nombreCliente + "</a><br><br>";
             /*   preview += Html.fromHtml("<h1>") +  "Descripcion           Codigo" + Html.fromHtml("</h1></center><br/>");
                preview += Html.fromHtml("<h1>") +   "Cantidad      Precio       P.Sug       Total" + Html.fromHtml("</h1></center><br/>");
                preview += Html.fromHtml("<h1>") +   "Tipo " + Html.fromHtml("</h1></center><br/>");
*/
                preview += "<a><b>" + padRight("# Factura", 10) + "\t\t" + padRight("Monto total", 10) + "</b></a><br>";
                preview += "<a><b>" + padRight("Monto Pagado", 10) + padRight("Monto restante", 10) + "</b></a><br>";
                preview += "<a>------------------------------------------------<a><br>";

                preview += getPrintDistTotal(recibo_actualizado.getCustomer_id());

            }
            else {
                preview += "<center><h2>Seleccione la factura a ver</h2></center>";
            }
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
        RealmResults<Recibos> result = realm1.where(Recibos.class).equalTo("customer_id", idVenta).equalTo("abonado", 1).findAll();

        if (result.isEmpty()) {
            send = "No hay recibos emitidos";
        }
        else {
            // printSalesCashTotal= 0.0;
            for (int i = 0; i < result.size(); i++) {

                List<Recibos> salesList1 = realm1.where(Recibos.class).equalTo("customer_id", idVenta).equalTo("abonado", 1).findAll();
               // Productos producto = realm1.where(Productos.class).equalTo("id", salesList1.get(i).getProduct_id()).findFirst();


                String numeracion = salesList1.get(i).getNumeration();
                double total = salesList1.get(i).getTotal();
                String totalS = String.format("%,.2f", total);

                double pagado = salesList1.get(i).getPaid();
                String pagadoS = String.format("%,.2f", pagado);
                double restante = salesList1.get(i).getMontoCancelado();
                String restanteS = String.format("%,.2f", restante);

                send += String.format("|%-600s|  |%600s|", numeracion, totalS) + "<br>" +
                        String.format("|%-1000s|  |%1000s|",pagadoS ,restanteS) + "<br>";
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



    public void actualizarFacturaDetalles() {


        txtFecha.getText().toString();
        observaciones.getText().toString();

        if(!observaciones.getText().toString().isEmpty()){
            observ = observaciones.getText().toString();

        }
        else{
            observaciones.setError("Campo requerido");
            observaciones.requestFocus();
        }


    /*    Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String idUsuario = usuarios.getId();
        realm.close();

        sale ventaDetallePreventa = activity.getCurrentVenta();
        ventaDetallePreventa.getInvoice_id();
        tipoFacturacion = ventaDetallePreventa.getFacturaDePreventa();

        final invoiceDetallePreventa invoiceDetallePreventa1 = activity.getCurrentInvoice();
        invoiceDetallePreventa1.setP_longitud(longitude);
        invoiceDetallePreventa1.setP_latitud(latitude);

        invoiceDetallePreventa1.setP_subtotal(String.valueOf(totalSubtotal));
        invoiceDetallePreventa1.setP_subtotal_taxed(String.valueOf(totalGrabado));
        invoiceDetallePreventa1.setP_subtotal_exempt(String.valueOf(totalExento));
        invoiceDetallePreventa1.setP_discount(String.valueOf(totalDescuento));
        invoiceDetallePreventa1.setP_tax(String.valueOf(totalImpuesto));
        invoiceDetallePreventa1.setP_total(String.valueOf(totalTotal));

        invoiceDetallePreventa1.setP_changing("0");
        invoiceDetallePreventa1.setP_note(notes.getText().toString());
        invoiceDetallePreventa1.setP_canceled("1");
        invoiceDetallePreventa1.setP_paid("0");
        invoiceDetallePreventa1.setP_user_id(idUsuario);
        invoiceDetallePreventa1.setP_user_id_applied(idUsuario);
        invoiceDetallePreventa1.setP_sale(activity.getCurrentVenta());

        if(tipoFacturacion.equals("Preventa")){
            invoiceDetallePreventa1.setFacturaDePreventa("Preventa");
        }
        else if(tipoFacturacion.equals("Proforma")){
            invoiceDetallePreventa1.setFacturaDePreventa("Proforma");
        }


        Log.d("actFactDetPrev", invoiceDetallePreventa1 + "");*/

    }

    protected void aplicarFactura() {

    /*    actualizarFacturaDetalles();*/

        Toast.makeText(getActivity(), "Venta realizada correctamente", Toast.LENGTH_LONG).show();

        applyBill.setVisibility(View.GONE);
        printBill.setVisibility(View.VISIBLE);
        apply_done = 1;


    }

    public static void clearAll() {
        if (apply_done == 1) {

            apply_done = 0;
            // paid.getText().clear();
        }
        try {
            System.gc();
        }
        catch (Exception e) {
        }


    }

    public static  class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        public void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }

    }

    public String getCurrentDate()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("d / M / yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = day + " / " + (month+1) + " / " + year;
                txtFecha.setText(selectedDate);
            }
        });
        newFragment.show(getFragmentManager(), "datePicker");
    }



}
