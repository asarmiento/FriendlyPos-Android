package com.friendlypos.distribucion.fragment;

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
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.modelo.ConsecutivosNumberFe;
import com.friendlypos.principal.modelo.Sysconf;
import com.friendlypos.principal.modelo.datosTotales;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;


public class DistTotalizarFragment extends BaseFragment  {

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
    double totalDatosTotal = 0.0;
    double totalDatosTotal2 = 0.0;
    String totalVuelvo = "0";
    String totalPagoCon = "0";
    static double pagoCon = 0.0;
    static double vuelto = 0.0;
    String facturaId,usuer, numeroConsecutivo, keyElectronica,  metodoPagoCliente, consConsecutivo, consConsecutivoATV;
    SessionPrefes session;
    double latitude;
    double longitude;
    private static Button applyBill;
    private static Button printBill;

    private static int apply_done = 0;
    int slecTAB, nextId;
    sale sale_actualizada;
    datosTotales datos_actualizados;
    GPSTracker gps;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;
    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAll();
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
        bluetoothStateChangeReceiver.setBluetoothStateChangeReceiver(getContext());
        session = new SessionPrefes(getApplicationContext());
        Log.d("applydone", apply_done +"");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_distribucion_totalizar, container, false);


        client_name = (EditText) rootView.findViewById(R.id.client_name);

        subExe = (TextView) rootView.findViewById(R.id.subExento);
        subGra = (TextView) rootView.findViewById(R.id.subGrabado);
        ivaSub = (TextView) rootView.findViewById(R.id.IvaFact);
        subT = (TextView) rootView.findViewById(R.id.subTotal);
        discount = (TextView) rootView.findViewById(R.id.Discount);
        Total = (TextView) rootView.findViewById(R.id.Total);

        paid = (EditText) rootView.findViewById(R.id.txtPaid);

        change = (TextView) rootView.findViewById(R.id.txtChange);
        slecTAB = ((DistribucionActivity) getActivity()).getSelecClienteTab();

        if (slecTAB == 1) {

            metodoPagoCliente = ((DistribucionActivity) getActivity()).getMetodoPagoCliente();


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
            Toast.makeText(getActivity(),"nadaTotalizar",Toast.LENGTH_LONG).show();
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
                            //validateData();
                            // Log.d("total", String.valueOf(Functions.sGetDecimalStringAnyLocaleAsDouble(Total.getText().toString())));

                            if (metodoPagoCliente.equals("1")) {

                                if(paid.getText().toString().isEmpty()){
                                    pagoCon = 0;
                                    vuelto = 0;
                                    totalVuelvo = String.format("%,.2f", vuelto);
                                    change.setText(totalVuelvo);
                                }
                                else {
                                    pagoCon = Double.parseDouble(paid.getText().toString());
                                    totalPagoCon = String.format("%,.2f", pagoCon);
                                    double total = totalTotal;

                                    if (pagoCon >= total) {
                                        vuelto = pagoCon - total;
                                        totalVuelvo = String.format("%,.2f", vuelto);
                                        change.setText(totalVuelvo);}
                                    else {
                                        Toast.makeText(getActivity(), "Digite una cantidad mayor al total", Toast.LENGTH_LONG).show();
                                    }
                                }

                                    int tabCliente = 0;
                                    ((DistribucionActivity) getActivity()).setSelecClienteTab(tabCliente);
                                    obtenerLocalización();
                                    aplicarFactura();

                            }
                           else if (metodoPagoCliente.equals("2")) {
                                    Toast.makeText(getActivity(), "Crédito", Toast.LENGTH_LONG).show();
                                int tabCliente = 0;
                                ((DistribucionActivity) getActivity()).setSelecClienteTab(tabCliente);
                                obtenerLocalización();
                                aplicarFactura();
                            }


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

                                PrinterFunctions.imprimirFacturaDistrTotal(sale_actualizada, getActivity(), 1, cantidadImpresiones);
                                Toast.makeText(getActivity(), "Imprimiendo", Toast.LENGTH_SHORT).show();
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

            totalGrabado = ((DistribucionActivity) getActivity()).getTotalizarSubGrabado();
            totalExento = ((DistribucionActivity) getActivity()).getTotalizarSubExento();
            totalSubtotal = ((DistribucionActivity) getActivity()).getTotalizarSubTotal();
            totalDescuento = ((DistribucionActivity) getActivity()).getTotalizarDescuento();
            totalImpuesto = ((DistribucionActivity) getActivity()).getTotalizarImpuestoIVA();
            totalTotal = ((DistribucionActivity) getActivity()).getTotalizarTotal();
            facturaId = ((DistribucionActivity) getActivity()).getInvoiceId();

            subGra.setText(String.format("%,.2f", totalGrabado));
            subExe.setText(String.format("%,.2f", totalExento));

            subT.setText(String.format("%,.2f", totalSubtotal));
            discount.setText(String.format("%,.2f", totalDescuento));

            ivaSub.setText(String.format("%,.2f", totalImpuesto));
            Total.setText(String.format("%,.2f", totalTotal));

            Log.d("FACTURAIDTOTALIZAR", facturaId);
        }
        else{
            Toast.makeText(getActivity(),"nadaTotalizarupdate",Toast.LENGTH_LONG).show();
        }

    }


    public void crearFacturaElectronica(){

        Realm realm = Realm.getDefaultInstance();

        Sysconf sysconf = realm.where(Sysconf.class).findFirst();

        String sysSucursal = sysconf.getSucursal();

        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String userTerminal = usuarios.getTerminal();
        final String userId = usuarios.getId();

        invoice factura_actualizada = realm.where(invoice.class).equalTo("id", facturaId).findFirst();
        final String invType = factura_actualizada.getType();

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

    public void obtenerLocalización() {

        gps = new GPSTracker(getActivity());
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            gps.showSettingsAlert();
        }

    }

    protected void actualizarFactura() {

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA FACTURAS
        final Realm realm2 = Realm.getDefaultInstance();
        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm2) {
                invoice factura_actualizada = realm2.where(invoice.class).equalTo("id", facturaId).findFirst();
                Realm realm = Realm.getDefaultInstance();
                usuer = session.getUsuarioPrefs();
                Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
                String idUsuario = usuarios.getId();
                realm.close();

                factura_actualizada.setDate(Functions.getDate());
                factura_actualizada.setTimes(Functions.get24Time());

                factura_actualizada.setKey(keyElectronica);
                factura_actualizada.setConsecutive_number(numeroConsecutivo);

                factura_actualizada.setLatitud(latitude);
                factura_actualizada.setLongitud(longitude);

                factura_actualizada.setSubtotal_taxed(String.valueOf(totalGrabado));
                factura_actualizada.setSubtotal_exempt(String.valueOf(totalExento));
                factura_actualizada.setSubtotal(String.valueOf(totalSubtotal));
                factura_actualizada.setDiscount(String.valueOf(totalDescuento));
                factura_actualizada.setTax(String.valueOf(totalImpuesto));
                factura_actualizada.setTotal(String.valueOf(totalTotal));

                factura_actualizada.setPaid(String.valueOf(pagoCon));
                factura_actualizada.setChanging(String.valueOf(vuelto));
                factura_actualizada.setUser_id_applied(idUsuario);
                factura_actualizada.setNote(notes.getText().toString());
                factura_actualizada.setCanceled("1");
                factura_actualizada.setAplicada(1);
                factura_actualizada.setSubida(1);
                factura_actualizada.setFacturaDePreventa("Distribucion");



                RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", facturaId)/*.equalTo("devuelvo", 0)*/.findAll();
                Log.d("FACTURANUEVA", result + "");


                RealmList <Pivot> results = new RealmList<Pivot>();

                results.addAll(result.subList(0, result.size()));
                factura_actualizada.setProductofactura(results);

                Log.d("CREAR DISTRIBUCION", factura_actualizada + "");

                realm2.insertOrUpdate(factura_actualizada);
                realm2.close();
              //  Pivot pivot  = realm5.where(Pivot.class).equalTo("invoice_id", facturaId).findAll();
            }
        });
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

                if (nombreEscrito.matches("")) {
                    Toast.makeText(getActivity(), "Nombre no cambio", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getActivity(), "Nombre cambio", Toast.LENGTH_SHORT).show();
                    sale_actualizada.setCustomer_name(client_name.getText().toString());
                }

                sale_actualizada.setSale_type("1");
                sale_actualizada.setApplied("1");
                sale_actualizada.setUpdated_at(Functions.getDate() + " " + Functions.get24Time());
                sale_actualizada.setAplicada(1);
                sale_actualizada.setSubida(1);
                sale_actualizada.setFacturaDePreventa("Distribucion");

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
        datos_actualizados.setIdTotal(1);
        datos_actualizados.setNombreTotal("Distribucion");
        datos_actualizados.setTotalDistribucion(totalTotal);
        datos_actualizados.setDate(Functions.getDate());

        realm5.copyToRealmOrUpdate(datos_actualizados);
        realm5.commitTransaction();
        Log.d("datosTotalesDist", datos_actualizados + "");
        realm5.close();


        /*
        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                datos_actualizados = realm3.where(datosTotales.class).equalTo("nombreTotal", "Distribucion").findFirst();

                totalDatosTotal = datos_actualizados.getTotalDistribucion();

                totalDatosTotal2 = totalDatosTotal + totalTotal;

                datos_actualizados.setTotalDistribucion(totalDatosTotal2);
                datos_actualizados.setDate(Functions.getDate());

                realm3.insertOrUpdate(datos_actualizados);
                realm3.close();

                Log.d("TotalDatos", datos_actualizados + "" );
            }
        });*/

    }



    protected void aplicarFactura() {
        paid.setEnabled(false);
        crearFacturaElectronica();
        actualizarFactura();
        actualizarVenta();
        actualizarDatosTotales();


        Toast.makeText(getActivity(), "Venta realizada correctamente", Toast.LENGTH_LONG).show();

        applyBill.setVisibility(View.GONE);
        printBill.setVisibility(View.VISIBLE);
        apply_done = 1;

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