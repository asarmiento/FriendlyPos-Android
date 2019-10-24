package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.receiptsDetalle;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.datosTotales;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;


public class RecibosAplicarFragment extends BaseFragment {


    private static Button applyBill;
    private static Button printBill;
    RecibosActivity activity;
    private static int apply_done = 0;
    double pagado= 0.0;
    double montoCancelado= 0.0;
    String numeracion;
    static String pagadoMostrarS;
    GPSTracker gps;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    SessionPrefes session;
    double latitude;
    double longitude;
    int slecTAB, nextId;
    EditText txtFecha;
    String facturaId;
    String clienteId, receipts_ref;
    recibos recibo_actualizado;
    public HtmlTextView text;
    private static EditText observaciones;
    String observ;
    String fecha;
    double totalP;
    double totalDatosTotal = 0.0;
    double totalDatosTotal2 = 0.0;
    double totalTotal = 0.0;
    datosTotales datos_actualizados;
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
        pagado= 0.0;
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
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
      /*  txtFecha = (EditText) rootView.findViewById(R.id.txtFecha);
        txtFecha.setText(getCurrentDate());
        txtFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                showDatePickerDialog();
            }
        });
*/
        applyBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            int tabCliente = 0;
                            ((RecibosActivity) getActivity()).setSelecClienteTabRecibos(tabCliente);
                            aplicarFactura() ;
                          //  actualizarRecibo();
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

                            if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {

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

                                        PrinterFunctions.imprimirFacturaRecibosTotal(recibo_actualizado, getActivity(), 1, cantidadImpresiones);
                                        Toast.makeText(getActivity(), "imprimir liquidacion", Toast.LENGTH_SHORT).show();
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
                            else if(bluetoothStateChangeReceiver.isBluetoothAvailable() == false){
                                Functions.CreateMessage(getActivity(), "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                            }

                        } catch (Exception e) {
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

            facturaId = activity.getInvoiceIdRecibos();
            clienteId = activity.getClienteIdRecibos();
            totalP = activity.getTotalizarCancelado();

            final Realm realm3 = Realm.getDefaultInstance();
            realm3.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm3) {

                    recibo_actualizado = realm3.where(recibos.class).equalTo("customer_id", clienteId).findFirst();

                    realm3.close();
                }
            });


            getHtmlPreview();

            Log.d("FACTURAIDTOTALIZAR", clienteId);
        }
        else {
            Log.d("nadaTotalizarupdate",  "nadaTotalizarupdate");
        }

    }

    private void getHtmlPreview() {
        try {

            Realm realm = Realm.getDefaultInstance();
            Clientes clientes = realm.where(Clientes.class).equalTo("id", recibo_actualizado.getCustomer_id()).findFirst();

            String nombreCliente = clientes.getFantasyName();

            String preview = "";


            if (recibo_actualizado != null) {


                preview += "<h5>" + "recibos" + "</h5>";

                preview += "<a><b>A nombre de:</b> " + nombreCliente + "</a><br><br>";
                preview += "<a><b>" + padRight("# Factura", 10) + padRight("Monto total", 10)+ "</b></a><br>";
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
        RealmResults<recibos> result = realm1.where(recibos.class).equalTo("customer_id", idVenta).equalTo("abonado", 1).equalTo("mostrar",1).findAll();

        if (result.isEmpty()) {
            send = "No hay recibos emitidos";
        }
        else {
            for (int i = 0; i < result.size(); i++) {

                List<recibos> salesList1 = realm1.where(recibos.class).equalTo("customer_id", idVenta).equalTo("abonado", 1).equalTo("mostrar",1).findAll();


                String numeracion = salesList1.get(i).getNumeration();
                double total = salesList1.get(i).getTotal();
                String totalS = String.format("%,.2f", total);

                double pagado = salesList1.get(i).getMontoCanceladoPorFactura();
                String pagadoS = String.format("%,.2f", pagado);

                double restante = salesList1.get(i).getPorPagar();
                String restanteS = String.format("%,.2f", restante);




                send += String.format("|%-5000s|  |%10s|", numeracion, totalS) + "<br>" +
                        String.format("|%-5000s| |%10s|",pagadoS ,restanteS) + "<br>";
                send += "<a>------------------------------------------------<a><br>";

                Log.d("FACTPRODTODFAC", send + "");

            }
            realm1.close();
        }
        return send;
    }



    public void actualizarFacturaDetalles() {


        final Realm realm2 = Realm.getDefaultInstance();
        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm2) {

                RealmResults<recibos> result = realm2.where(recibos.class).equalTo("customer_id", clienteId).equalTo("abonado", 1).findAll();

                if (result.isEmpty()) {

                    Toast.makeText(getActivity(), "No hay recibos emitidos", Toast.LENGTH_LONG).show();}

                else{
                for (int i = 0; i < result.size(); i++) {

                    List<recibos> salesList1 = realm2.where(recibos.class).equalTo("customer_id", clienteId).equalTo("abonado", 1).findAll();

                    String facturaId1 = salesList1.get(i).getInvoice_id();

                    recibos recibo_actualizado = realm2.where(recibos.class).equalTo("invoice_id", facturaId1).findFirst();
                 //   recibo_actualizado.setMostrar(0);
                    recibo_actualizado.setDate(Functions.getDate());
                    recibo_actualizado.setObservaciones(observ);
                    recibo_actualizado.setReferencia_receipts(receipts_ref);

                    realm2.insertOrUpdate(recibo_actualizado);

                    Log.d("ACT RECIBO", recibo_actualizado + "");
                }
                    realm2.close();
                }

            }
        });
    }


    public void actualizarReceiptsDetalles() {

        final Realm realm2 = Realm.getDefaultInstance();
        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm2) {

                RealmResults<recibos> result = realm2.where(recibos.class).equalTo("customer_id", clienteId).equalTo("abonado", 1).findAll();

                if (result.isEmpty()) {

                    Toast.makeText(getActivity(), "No hay recibos emitidos", Toast.LENGTH_LONG).show();}

                else{
                    for (int i = 0; i < result.size(); i++) {

                        List<recibos> salesList1 = realm2.where(recibos.class).equalTo("customer_id", clienteId).equalTo("abonado", 1).findAll();
                        numeracion = salesList1.get(i).getNumeration();
                        pagado = salesList1.get(i).getPaid();
                        activity.setTotalizarFinal(pagado);

                       String receipts_id = activity.getReceipts_id_num();
                        montoCancelado = salesList1.get(i).getMontoCanceladoPorFactura();
                        activity.setCanceladoPorFactura(montoCancelado);


                        receipts recibo_actua = realm2.where(receipts.class).equalTo("receipts_id", receipts_id).equalTo("customer_id", clienteId).findFirst();



                        receipts_ref = recibo_actua.getReference();
                        recibo_actua.setListaRecibos(new RealmList<recibos>(salesList1.toArray(new recibos[salesList1.size()])));
                        recibo_actua.setBalance(activity.getTotalizarFinal());
                        recibo_actua.setAplicado(1);
                        recibo_actua.setSum(observ);
                        recibo_actua.setNotes(observ);
                        recibo_actua.setMontoPagado(montoCancelado);
                        recibo_actua.setNumeration(numeracion);
                        recibo_actua.setMontoCanceladoPorFactura(montoCancelado);
                        realm2.insertOrUpdate(recibo_actua);

                        Log.d("ACTRECIBO", recibo_actua + "");
                    }
                   realm2.close();
                    pagado= 0.0;
                }

            }
        });
    }

    protected void actualizarDatosTotales() {

        final Realm realm5 = Realm.getDefaultInstance();

        realm5.beginTransaction();
        Number currentIdNum = realm5.where(datosTotales.class).max("id");

        if (currentIdNum == null) {
            nextId = 1;
        }
        else {
            nextId = currentIdNum.intValue() + 1;
        }


        datosTotales datos_actualizados = new datosTotales();

        datos_actualizados.setId(nextId);
        datos_actualizados.setIdTotal(5);
        datos_actualizados.setNombreTotal("Recibo");
        datos_actualizados.setTotalRecibos(activity.getCanceladoPorFactura());
        datos_actualizados.setDate(Functions.getDate());

        realm5.copyToRealmOrUpdate(datos_actualizados);
        realm5.commitTransaction();
        Log.d("datosTotalesRec", datos_actualizados + "");
        realm5.close();
    }

   /* public void actualizarDatosTotales() {

        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                datos_actualizados = realm3.where(datosTotales.class).equalTo("nombreTotal", "Recibo").findFirst();

                totalDatosTotal = datos_actualizados.getTotalRecibos();

                totalDatosTotal2 = totalDatosTotal +  activity.getCanceladoPorFactura();

                datos_actualizados.setTotalRecibos(totalDatosTotal2);
                datos_actualizados.setDate(Functions.getDate());

                realm3.insertOrUpdate(datos_actualizados);
                realm3.close();

                Log.d("TotalDatos", datos_actualizados + "" );
            }
        });

    }*/

    protected void aplicarFactura() {

      //  fecha = txtFecha.getText().toString();
        observaciones.getText().toString();

        if(!observaciones.getText().toString().isEmpty()){
            observ = observaciones.getText().toString();
            actualizarReceiptsDetalles();
            actualizarFacturaDetalles();

            actualizarDatosTotales();
            Toast.makeText(getActivity(), "Recibo realizado correctamente", Toast.LENGTH_LONG).show();

            applyBill.setVisibility(View.GONE);
            printBill.setVisibility(View.VISIBLE);
            apply_done = 1;

        }
        else{
            observaciones.setError("Campo requerido");
            observaciones.requestFocus();
        }

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



/*

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
    }*/
/*
    public void actualizarRecibosDetalles() {


        final receiptsDetalle invoiceDetallePreventa1 = activity.getCurrentRecibos();

        invoiceDetallePreventa1.setD_sum(observ);



        Log.d("actFactDetPrev", invoiceDetallePreventa1 + "");

    }


    protected void actualizarRecibo() {

        final receipts receipt = activity.getReceiptsByReceiptsDetalle();

        Log.d("invoicetotal", receipt + "");

    }*/

}
