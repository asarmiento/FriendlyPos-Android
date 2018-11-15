package com.friendlypos.distribucion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.friendlypos.principal.modelo.datosTotales;

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
                                obtenerLocalización();
                                aplicarFactura();
                            }


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
                                PrinterFunctions.imprimirFacturaDistrTotal(sale_actualizada, getActivity(), 1);
                                Toast.makeText(getActivity(), "imprimir liquidacion", Toast.LENGTH_SHORT).show();
                                clearAll();
                                Log.d("applydoneImp", apply_done +"");
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


                realm2.insertOrUpdate(factura_actualizada);
                realm2.close();



                Realm realm5 = Realm.getDefaultInstance();
                RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("invoice_id", facturaId)/*.equalTo("devuelvo", 0)*/.findAll();
                Log.d("FACTURANUEVA", result + "");


                RealmList <Pivot> results = new RealmList<Pivot>();

                results.addAll(result.subList(0, result.size()));
                factura_actualizada.setProductofactura(results);

                Log.d("CREAR DISTRIBUCION", factura_actualizada + "");
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

        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                datos_actualizados = realm3.where(datosTotales.class).equalTo("nombreTotal", "Distribucion").findFirst();

                totalDatosTotal = datos_actualizados.getTotalDistribucion();

                totalDatosTotal2 = totalDatosTotal + totalTotal;

                datos_actualizados.setTotalDistribucion(totalDatosTotal2);

                realm3.insertOrUpdate(datos_actualizados);
                realm3.close();

                Log.d("TotalDatos", datos_actualizados + "" );
            }
        });

    }



    protected void aplicarFactura() {
        paid.setEnabled(false);

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