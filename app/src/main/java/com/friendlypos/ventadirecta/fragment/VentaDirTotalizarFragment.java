package com.friendlypos.ventadirecta.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.principal.modelo.ConsecutivosNumberFe;
import com.friendlypos.principal.modelo.Sysconf;
import com.friendlypos.principal.modelo.datosTotales;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
import com.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta;

import java.util.Random;

import io.realm.Realm;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;


public class VentaDirTotalizarFragment extends BaseFragment  {

    private static TextView subGra;
    private static TextView subExe;
    private static TextView subT;
    private static TextView ivaSub;
    private static TextView discount;
    private static TextView Total;
    private static TextView change;


    private static EditText notes;
    private static EditText client_name;
    private static EditText paid;

    double totalGrabado = 0.0;
    double totalExento = 0.0;
    double totalSubtotal = 0.0;
    double totalDescuento = 0.0;
    double totalImpuesto = 0.0;
    double totalTotal = 0.0;
    String totalVuelvo = "0";
    String totalPagoCon = "0";
    static double pagoCon = 0.0;
    static double vuelto = 0.0;
    String facturaId;
    String usuer;
    SessionPrefes session;
    double latitude;
    double longitude;
    String metodoPagoCliente, consConsecutivo, consConsecutivoATV, numeroConsecutivo, keyElectronica;
    private static Button applyBill;
    private static Button printBill;

    private static int apply_done = 0;
    int slecTAB;
    sale sale_actualizada;

    Numeracion num_actualizada;
    VentaDirectaActivity activity;
    int nextId;
    GPSTracker gps;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;

    String seleccion;


    datosTotales datos_actualizados;
    double totalDatosTotal = 0.0;
    double totalDatosTotal2 = 0.0;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (VentaDirectaActivity) activity;
    }
    @Override
    public void onPause() {
        super.onPause();


    }
    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAll();
        getActivity().unregisterReceiver(bluetoothStateChangeReceiver);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothStateChangeReceiver = new BluetoothStateChangeReceiver();
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(getContext());
        session = new SessionPrefes(getApplicationContext());
        Log.d("applydone", apply_done +"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ventadir_totalizar, container, false);


        client_name = (EditText) rootView.findViewById(R.id.client_name);

        subExe = (TextView) rootView.findViewById(R.id.subExento);
        subGra = (TextView) rootView.findViewById(R.id.subGrabado);
        ivaSub = (TextView) rootView.findViewById(R.id.IvaFact);
        subT = (TextView) rootView.findViewById(R.id.subTotal);
        discount = (TextView) rootView.findViewById(R.id.Discount);
        Total = (TextView) rootView.findViewById(R.id.Total);

        paid = (EditText) rootView.findViewById(R.id.txtPaid);

        change = (TextView) rootView.findViewById(R.id.txtChange);
        slecTAB = ((VentaDirectaActivity) getActivity()).getSelecClienteTabVentaDirecta();

        if (slecTAB == 1) {

            metodoPagoCliente = ((VentaDirectaActivity) getActivity()).getMetodoPagoClienteVentaDirecta();


            if (metodoPagoCliente.equals("1")) {
                //bill_type = 1;
                try {
                    paid.setEnabled(true);

                } catch (Exception e) {
                    Log.d("JD", "Error " + e.getMessage());
                }

            } else if (metodoPagoCliente.equals("2")) {
                //  bill_type = 2;
                paid.setEnabled(false);
            }
        }
        else{
            Log.d("nadaTotalizar", "nadaTotalizar");
        }
        notes = (EditText) rootView.findViewById(R.id.txtNotes);

        applyBill = (Button) rootView.findViewById(R.id.applyInvoice);
        printBill = (Button) rootView.findViewById(R.id.printInvoice);

        if (apply_done == 1) {
            applyBill.setVisibility(View.GONE);
            printBill.setVisibility(View.VISIBLE);
        } else {
            applyBill.setVisibility(View.VISIBLE);
            printBill.setVisibility(View.GONE);
        }

        applyBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {

                            if (metodoPagoCliente.equals("1")) {

                                if(paid.getText().toString().isEmpty()){
                                    pagoCon = 0;
                                    vuelto = 0;
                                    totalVuelvo = String.format("%,.2f", vuelto);
                                    change.setText(totalVuelvo);
                                }
                                else{
                                    pagoCon = Double.parseDouble(paid.getText().toString());
                                    totalPagoCon = String.format("%,.2f", pagoCon);
                                    double total = totalTotal;

                                    if (pagoCon >= total) {
                                        vuelto = pagoCon - total;
                                        totalVuelvo = String.format("%,.2f", vuelto);
                                        change.setText(totalVuelvo);
                                    }else {
                                        Toast.makeText(getActivity(), "Digite una cantidad mayor al total", Toast.LENGTH_LONG).show();
                                    }
                                }
                                    int tabCliente = 0;
                                    ((VentaDirectaActivity) getActivity()).setSelecClienteTabVentaDirecta(tabCliente);
                                    obtenerLocalización();
                                    aplicarFactura();

                            }
                            else if (metodoPagoCliente.equals("2")) {
                                Toast.makeText(getActivity(), "Crédito", Toast.LENGTH_LONG).show();
                                obtenerLocalización();
                                aplicarFactura();
                                int tabCliente = 0;
                                ((VentaDirectaActivity) getActivity()).setSelecClienteTabVentaDirecta(tabCliente);
                            }
                            actualizarFactura();
                            actualizarNumeracion();
                        }
                        catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                        session.guardarDatosBloquearBotonesDevolver(0);
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

                                PrinterFunctions.imprimirFacturaVentaDirectaTotal(sale_actualizada, getActivity(), 3, cantidadImpresiones);
                                clearAll();
                                Log.d("applydoneImp", apply_done +"");

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

    @Override
    public void updateData() {
        if (slecTAB == 1) {
            paid.getText().clear();

            totalGrabado = ((VentaDirectaActivity) getActivity()).getTotalizarSubGrabado();
            totalExento = ((VentaDirectaActivity) getActivity()).getTotalizarSubExento();
            totalSubtotal = ((VentaDirectaActivity) getActivity()).getTotalizarSubTotal();
            totalDescuento = ((VentaDirectaActivity) getActivity()).getTotalizarDescuento();
            totalImpuesto = ((VentaDirectaActivity) getActivity()).getTotalizarImpuestoIVA();
            totalTotal = ((VentaDirectaActivity) getActivity()).getTotalizarTotal();
            facturaId = String.valueOf(((VentaDirectaActivity) getActivity()).getInvoiceIdVentaDirecta());

            subGra.setText(String.format("%,.2f", totalGrabado));
            subExe.setText(String.format("%,.2f", totalExento));

            subT.setText(String.format("%,.2f", totalSubtotal));
            discount.setText(String.format("%,.2f", totalDescuento));

            ivaSub.setText(String.format("%,.2f", totalImpuesto));
            Total.setText(String.format("%,.2f", totalTotal));

            Log.d("FACTURAIDTOTALIZAR", facturaId);
        }
        else{
            Log.d("nadaTotalizarupdate", "nadaTotalizarupdate");
        }

    }

    public void obtenerLocalización() {

        gps = new GPSTracker(getActivity());

        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        }else{

            gps.showSettingsAlert();


        }

    }



    protected void actualizarNumeracion() {
        final int id = Integer.parseInt(facturaId);
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                num_actualizada = realm3.where(Numeracion.class).equalTo("number", id).equalTo("sale_type","1").findFirst();

                num_actualizada.setRec_aplicada(1);
                realm3.insertOrUpdate(num_actualizada);
                realm3.close();

                Log.d("Numeracion", num_actualizada + "" );
            }
        });

    }



    public void actualizarFacturaDetalles() {

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String idUsuario = usuarios.getId();
        realm.close();

        final invoiceDetalleVentaDirecta invoiceDetallePreventa1 = activity.getCurrentInvoice();

        invoiceDetallePreventa1.setP_key(keyElectronica);
        invoiceDetallePreventa1.setP_consecutive_number(numeroConsecutivo);

        invoiceDetallePreventa1.setP_longitud(longitude);
        invoiceDetallePreventa1.setP_latitud(latitude);

        invoiceDetallePreventa1.setP_subtotal(String.valueOf(totalSubtotal));
        invoiceDetallePreventa1.setP_subtotal_taxed(String.valueOf(totalGrabado));
        invoiceDetallePreventa1.setP_subtotal_exempt(String.valueOf(totalExento));
        invoiceDetallePreventa1.setP_discount(String.valueOf(totalDescuento));
        invoiceDetallePreventa1.setP_tax(String.valueOf(totalImpuesto));
        invoiceDetallePreventa1.setP_total(String.valueOf(totalTotal));

        invoiceDetallePreventa1.setP_changing(String.valueOf(vuelto));

        String nota;
        if(notes.getText().toString().isEmpty()){
            nota = "ninguna";
        }else{
            nota = notes.getText().toString();
        }

        invoiceDetallePreventa1.setP_note(nota);
        invoiceDetallePreventa1.setP_canceled("1");
        invoiceDetallePreventa1.setP_paid(String.valueOf(pagoCon));
        invoiceDetallePreventa1.setP_user_id(idUsuario);
        invoiceDetallePreventa1.setP_user_id_applied(idUsuario);
        invoiceDetallePreventa1.setP_sale(activity.getCurrentVenta());
        invoiceDetallePreventa1.setFacturaDePreventa("VentaDirecta");
        invoiceDetallePreventa1.setP_Aplicada(1);

        Log.d("actFactDetVD", invoiceDetallePreventa1 + "");

    }

    protected void actualizarFactura() {

        final invoice invoice =  activity.getInvoiceByInvoiceDetalles();

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(invoice);
            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                actualizarVenta();
                actualizarDatosTotales();
            }
        }, new Realm.Transaction.OnError() {

            @Override
            public void onError(Throwable error) {
                Log.e("actualizarFactura ", error.getMessage());
            }
        });

        Log.d("invoicetotal", invoice + "");

    }

    protected void actualizarVenta() {

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                sale_actualizada = realm3.where(sale.class).equalTo("invoice_id", facturaId).findFirst();
                Log.d("dadasdad", client_name.getText().toString());

                String nombreEscrito = client_name.getText().toString();

                if (nombreEscrito.matches(" ")) {
                    Log.d("nombreCam",  "Nombre no cambio");
                }else{
                    Log.d("nombreCam",  "Nombre cambio");
                    sale_actualizada.setCustomer_name(client_name.getText().toString());
                }

                sale_actualizada.setApplied("1");
                sale_actualizada.setUpdated_at(Functions.getDate() + " " + Functions.get24Time());
                sale_actualizada.setAplicada(1);
                sale_actualizada.setSubida(1);

                realm3.insertOrUpdate(sale_actualizada);
                realm3.close();

                Log.d("ENVIADOSALE", sale_actualizada + "" );
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
        datos_actualizados.setIdTotal(2);
        datos_actualizados.setNombreTotal("VentaDirecta");
        datos_actualizados.setTotalVentaDirecta(totalTotal);
        datos_actualizados.setDate(Functions.getDate());
    //    datos_actualizados.setDate("2018-12-04");
        realm5.copyToRealmOrUpdate(datos_actualizados);
        realm5.commitTransaction();
        Log.d("datosTotalesVD", datos_actualizados + "");
        realm5.close();
    }

/*

    protected void actualizarDatosTotales() {

        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                datos_actualizados = realm3.where(datosTotales.class).equalTo("nombreTotal", "VentaDirecta").findFirst();

                totalDatosTotal = datos_actualizados.getTotalVentaDirecta();

                totalDatosTotal2 = totalDatosTotal + totalTotal;

                datos_actualizados.setTotalVentaDirecta(totalDatosTotal2);
                datos_actualizados.setDate(Functions.getDate());

                realm3.insertOrUpdate(datos_actualizados);
                realm3.close();

                Log.d("TotalDatos", datos_actualizados + "" );
            }
        });

    }*/


    protected void aplicarFactura() {
        paid.setEnabled(false);
        crearFacturaElectronica();
        actualizarFacturaDetalles();

        Toast.makeText(getActivity(), "Venta realizada correctamente", Toast.LENGTH_LONG).show();

        applyBill.setVisibility(View.GONE);
        printBill.setVisibility(View.VISIBLE);
        apply_done = 1;


    }


    public void crearFacturaElectronica(){

        Realm realm = Realm.getDefaultInstance();

        Sysconf sysconf = realm.where(Sysconf.class).findFirst();

        String sysSucursal = sysconf.getSucursal();

        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String userTerminal = usuarios.getTerminal();
        final String userId = usuarios.getId();

        final invoiceDetalleVentaDirecta invoiceDetallePreventa1 = activity.getCurrentInvoice();


     //   invoice factura_actualizada = realm.where(invoice.class).equalTo("id", facturaId).findFirst();
        final String invType = invoiceDetallePreventa1.getP_type();

       // ConsecutivosNumberFe consecutivosNumberFe = realm.where(ConsecutivosNumberFe.class).equalTo("user_id", "1").findFirst();
        //   String consConsecutivo = consecutivosNumberFe.getNumber_consecutive();
        Log.d("sysSucursal", sysSucursal);
        Log.d("userTerminal", userTerminal);
        Log.d("invType", invType);

        final Realm realm2 = Realm.getDefaultInstance();

        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                Number numero = realm.where(ConsecutivosNumberFe.class).equalTo("user_id", userId).equalTo("type_doc", invType).max("number_consecutive");

                if (numero == null) {
                    nextId = 1;
                }
                else {
                    nextId = numero.intValue() + 1;
                }
                int valor = numero.intValue();

                int length = String.valueOf(nextId).length();

                if(length == 1){
                    consConsecutivo = "000000000" + nextId;
                }
                else if(length == 2){
                    consConsecutivo = "00000000" + nextId;
                }
                else if(length == 3){
                    consConsecutivo = "0000000" + nextId;
                }
                else if(length == 4){
                    consConsecutivo = "000000" + nextId;
                }
                else if(length == 5){
                    consConsecutivo = "00000" + nextId;
                }
                else if(length == 6){
                    consConsecutivo = "0000" + nextId;
                }
                else if(length == 7){
                    consConsecutivo = "000" + nextId;
                }
                else if(length == 8){
                    consConsecutivo = "00" + nextId;
                }
                else if(length == 9){
                    consConsecutivo = "0" + nextId;
                }
                else if(length == 10){
                    consConsecutivo = "" + nextId;
                }

            }
        });




        Log.d("consConsecutivo", consConsecutivo);

        numeroConsecutivo = sysSucursal + userTerminal + invType + consConsecutivo;

        Log.d("numeroConsecutivo", numeroConsecutivo);



        final Realm realm5 = Realm.getDefaultInstance();
        realm5.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm5) {
                ConsecutivosNumberFe numNuevo = realm5.where(ConsecutivosNumberFe.class).equalTo("user_id", userId).equalTo("type_doc", invType).findFirst();

                numNuevo.setNumber_consecutive(nextId);

                realm5.insertOrUpdate(numNuevo);
                Log.d("ActConsecutivo", numNuevo + "");
                realm5.close();

            }

        });
        realm5.close();


        String sysIdNumberAtv = sysconf.getId_number_atv();

        int lengthAtv = String.valueOf(sysIdNumberAtv).length();

        if(lengthAtv == 1){
            consConsecutivoATV = "00000000000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 2){
            consConsecutivoATV = "0000000000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 3){
            consConsecutivoATV = "000000000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 4){
            consConsecutivoATV = "00000000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 5){
            consConsecutivoATV = "0000000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 6){
            consConsecutivoATV = "000000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 7){
            consConsecutivoATV = "00000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 8){
            consConsecutivoATV = "0000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 9){
            consConsecutivoATV = "000" + sysIdNumberAtv;
        }
        else if(lengthAtv == 10){
            consConsecutivoATV = "00" + sysIdNumberAtv;
        }
        else if(lengthAtv == 11){
            consConsecutivoATV = "0" + sysIdNumberAtv;
        }
        else if(lengthAtv == 12){
            consConsecutivoATV = "" + sysIdNumberAtv;
        }

        int min = 10000001;
        int max = 99999999;
        int codSeguridad = new Random().nextInt((max - min)+ 1) + min;


        Log.d("consConsecutivoATV", consConsecutivoATV);
        Log.d("userTerminal", userTerminal);
        Log.d("invType", invType);
        Log.d("codSeguridad", codSeguridad + "");

        keyElectronica = "506" + Functions.getDateConsecutivo() + consConsecutivoATV + numeroConsecutivo + "3" + codSeguridad;
        Log.d("keyElectronica", keyElectronica + "");



        realm.close();
    }


    public static void clearAll() {
        if (apply_done == 1) {

            apply_done = 0;
            paid.getText().clear();
            Log.d("applydoneClear", apply_done +"");
        }
        try {
            System.gc();
        } catch (Exception e) {
        }


    }



}