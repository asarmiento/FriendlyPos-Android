package com.friendlypos.preventas.fragment;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.friendlypos.preventas.modelo.ClienteVisitado;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;

import io.realm.Realm;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;


public class PrevTotalizarFragment extends BaseFragment  {

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
    PreventaActivity activity;
    int nextId;
    GPSTracker gps;
    BluetoothStateChangeReceiver bluetoothStateChangeReceiver;

    String seleccion;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (PreventaActivity) activity;
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

        View rootView = inflater.inflate(R.layout.fragment_prev_totalizar, container, false);


        client_name = (EditText) rootView.findViewById(R.id.client_name);

        subExe = (TextView) rootView.findViewById(R.id.subExento);
        subGra = (TextView) rootView.findViewById(R.id.subGrabado);
        ivaSub = (TextView) rootView.findViewById(R.id.IvaFact);
        subT = (TextView) rootView.findViewById(R.id.subTotal);
        discount = (TextView) rootView.findViewById(R.id.Discount);
        Total = (TextView) rootView.findViewById(R.id.Total);

        paid = (EditText) rootView.findViewById(R.id.txtPaid);

        change = (TextView) rootView.findViewById(R.id.txtChange);
        slecTAB = ((PreventaActivity) getActivity()).getSelecClienteTabPreventa();

        if (slecTAB == 1) {

            metodoPagoCliente = ((PreventaActivity) getActivity()).getMetodoPagoClientePreventa();


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
                                pagoCon = Double.parseDouble(paid.getText().toString());
                                totalPagoCon = String.format("%,.2f", pagoCon);
                                double total = totalTotal;

                                if (pagoCon >= total) {
                                    vuelto = pagoCon - total;
                                    totalVuelvo = String.format("%,.2f", vuelto);

                                    int tabCliente = 0;
                                    ((PreventaActivity) getActivity()).setSelecClienteTabPreventa(tabCliente);

                                    change.setText(totalVuelvo);
                                    obtenerLocalización();
                                    aplicarFactura();
                            }
                                else {
                                    Toast.makeText(getActivity(), "Digite una cantidad mayor al total", Toast.LENGTH_LONG).show();
                                }
                            }
                           else if (metodoPagoCliente.equals("2")) {
                                    Toast.makeText(getActivity(), "Crédito", Toast.LENGTH_LONG).show();
                                obtenerLocalización();
                                aplicarFactura();
                            }
                            actualizarFactura();
                            alertVisitado();

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
                                PrinterFunctions.imprimirFacturaPrevTotal(sale_actualizada, getActivity(), 1);
                                Toast.makeText(getActivity(), "imprimir Totalizar Preventa", Toast.LENGTH_SHORT).show();
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
        else{
            Toast.makeText(getActivity(),"nadaTotalizarupdate",Toast.LENGTH_LONG).show();
        }

    }

    public void obtenerLocalización() {

        gps = new GPSTracker(getActivity());

        // check if GPS enabled
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

           /* messageTextView2.setText("Mi direccion es: \n"
                    + latitude + "log "  + longitude );
            // \n is for new line
            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();*/
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();


        }

    }

    public void alertVisitado(){

    LayoutInflater layoutInflater = LayoutInflater.from(activity);
    View promptView = layoutInflater.inflate(R.layout.promptvisitado_preventa, null);

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setView(promptView);
    final RadioButton rbcomprado = (RadioButton) promptView.findViewById(R.id.compradoBillVisitado);
    final RadioButton rbvisitado = (RadioButton) promptView.findViewById(R.id.visitadoBillVisitado);


                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int id) {

            if(rbcomprado.isChecked()) {seleccion = "1";}

            else if(rbvisitado.isChecked()){seleccion = "2";}

            actualizarClienteVisitado();

        }


                        })
            .setNegativeButton("Cancel",
                                       new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
        }
    });



    AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();

    }


    public void actualizarFacturaDetalles() {

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String idUsuario = usuarios.getId();
        realm.close();

        final invoiceDetallePreventa invoiceDetallePreventa1 = activity.getCurrentInvoice();
        invoiceDetallePreventa1.setP_longitud(longitude);
        invoiceDetallePreventa1.setP_latitud(latitude);

        invoiceDetallePreventa1.setP_subtotal(String.valueOf(totalSubtotal));
        invoiceDetallePreventa1.setP_subtotal_taxed(String.valueOf(totalGrabado));
        invoiceDetallePreventa1.setP_subtotal_exempt(String.valueOf(totalExento));
        invoiceDetallePreventa1.setP_discount(String.valueOf(totalDescuento));
        invoiceDetallePreventa1.setP_tax(String.valueOf(totalImpuesto));
        invoiceDetallePreventa1.setP_total(String.valueOf(totalTotal));

        invoiceDetallePreventa1.setP_changing(String.valueOf(vuelto));
        invoiceDetallePreventa1.setP_note(notes.getText().toString());
        invoiceDetallePreventa1.setP_canceled("1");
        invoiceDetallePreventa1.setP_paid(String.valueOf(pagoCon));
        invoiceDetallePreventa1.setP_user_id(idUsuario);
        invoiceDetallePreventa1.setP_user_id_applied(idUsuario);
        invoiceDetallePreventa1.setP_sale(activity.getCurrentVenta());
/*
        invoiceDetallePreventa1.setAplicada(1);
        invoiceDetallePreventa1.setSubida(1);*/


        Log.d("codigoooo", invoiceDetallePreventa1 + "");

    }

    protected void actualizarFactura() {

        activity.getInvoiceByInvoiceDetalles();

     /*   // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA FACTURAS
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

                realm2.insertOrUpdate(factura_actualizada);
                realm2.close();
            }
        });*/
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

                realm3.insertOrUpdate(sale_actualizada);
                realm3.close();

                Log.d("ENVIADOSALE", sale_actualizada + "" );
            }
        });

    }

    protected void actualizarClienteVisitado() {
        final Realm realm5 = Realm.getDefaultInstance();

        realm5.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm5) {

                Number currentIdNum = realm5.where(ClienteVisitado.class).max("id");

                if (currentIdNum == null) {
                    nextId = 1;
                }
                else {
                    nextId = currentIdNum.intValue() + 1;
                }

                ClienteVisitado visitadonuevo = new ClienteVisitado();
                visitadonuevo.setId(nextId);
                visitadonuevo.setId_invoice(facturaId);
                visitadonuevo.setPedido(seleccion);
                visitadonuevo.setLongitud(longitude);
                visitadonuevo.setLatitud(latitude);

                realm5.insertOrUpdate(visitadonuevo);
                Log.d("ClienteVisitado", visitadonuevo + "" );
            }
        });

    }


    protected void aplicarFactura() {
        paid.setEnabled(false);

        actualizarFacturaDetalles();

    /*    actualizarFactura();
        actualizarVenta();*/

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



/*
    protected void validateData() throws Exception {

        if (CurrentSale == null) {
            client_name.requestFocus();
            throw new IOException("Debe Seleccionar un cliente de la primera vista");
        }

        if (CurrentSale.costumer.company_name.equals("Cliente Generico") && client_name.getText().toString().isEmpty()) {
            throw new IOException("Para el Cliente a Contado, debe ingresar un nombre");
        }


        //ClimDataValidator.validateSpinnerHasNotDefaultText("Seleccione condición compra",  spinnerPaymentMethods, "Debe seleccionar una condición de compra");

        if (!(mAdapterBill.getItemCount() > 0)) {
            throw new IOException("Debe ingresar al menos un producto");
        }

        if (bill_type == 1) {
            if (paid.getText().toString().isEmpty()) {
                throw new IOException("Aun no ha cobrado !!");
            }
            if (Functions.sGetDecimalStringAnyLocaleAsDouble(paid.getText().toString()) < Functions.sGetDecimalStringAnyLocaleAsDouble(Total.getText().toString())) {
                throw new IOException("El cobro no es correcto");

            }
        }
    }*/

}