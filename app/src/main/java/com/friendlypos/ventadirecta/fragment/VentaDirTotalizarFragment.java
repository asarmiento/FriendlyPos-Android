package com.friendlypos.ventadirecta.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
import com.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta;

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
    String metodoPagoCliente;
    private static Button applyBill;
    private static Button printBill;

    private static int apply_done = 0;
    int slecTAB;
    sale sale_actualizada;
    VentaDirectaActivity activity;
    int nextId;
    GPSTracker gps;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;

    String seleccion;

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
                            }
                            actualizarFactura();

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
                                PrinterFunctions.imprimirFacturaPrevTotal(sale_actualizada, getActivity(), 3);
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

    public void actualizarFacturaDetalles() {

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String idUsuario = usuarios.getId();
        realm.close();

        final invoiceDetalleVentaDirecta invoiceDetallePreventa1 = activity.getCurrentInvoice();
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

                if (nombreEscrito.matches("")) {
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


    protected void aplicarFactura() {
        paid.setEnabled(false);

        actualizarFacturaDetalles();

        Toast.makeText(getActivity(), "Venta realizada correctamente", Toast.LENGTH_LONG).show();

        applyBill.setVisibility(View.GONE);
        printBill.setVisibility(View.VISIBLE);
        apply_done = 1;


    }


    public static void clearAll() {
        if (apply_done == 1) {

            apply_done = 0;
            paid.getText().clear();
        }
        try {
            System.gc();
        } catch (Exception e) {
        }


    }
}