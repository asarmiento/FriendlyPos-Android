package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.app.broadcastreceiver.BluetoothStateChangeReceiver;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;

import io.realm.Realm;

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
    private static EditText observaciones;
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
      /*  if (slecTAB == 1) {
            //  paid.getText().clear();

            totalGrabado = ((PreventaActivity) getActivity()).getTotalizarSubGrabado();
            totalExento = ((PreventaActivity) getActivity()).getTotalizarSubExento();
            totalSubtotal = ((PreventaActivity) getActivity()).getTotalizarSubTotal();
            totalDescuento = ((PreventaActivity) getActivity()).getTotalizarDescuento();
            totalImpuesto = ((PreventaActivity) getActivity()).getTotalizarImpuestoIVA();
            totalTotal = ((PreventaActivity) getActivity()).getTotalizarTotal();
            facturaId = String.valueOf(((PreventaActivity) getActivity()).getInvoiceIdPreventa());

            subGra.setText(String.format("%,.2f", totalGrabado));
            subExe.setText(String.format("%,.2f", totalExento));

            subT.setText(String.format("%,.2f", totalSubtotal));
            discount.setText(String.format("%,.2f", totalDescuento));

            ivaSub.setText(String.format("%,.2f", totalImpuesto));
            Total.setText(String.format("%,.2f", totalTotal));

            Log.d("FACTURAIDTOTALIZAR", facturaId);
        }
        else {
            Log.d("nadaTotalizarupdate",  "nadaTotalizarupdate");
        }*/

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

    public void actualizarFacturaDetalles() {

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
}
