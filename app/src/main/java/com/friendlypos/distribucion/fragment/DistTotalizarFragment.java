package com.friendlypos.distribucion.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.distribucion.util.Localizacion;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;


public class DistTotalizarFragment extends BaseFragment {

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
    String facturaId;

    TextView messageTextView;
    TextView messageTextView2;



    private static Button applyBill;
    private static Button printBill;

    private static int apply_done = 0;

    Venta venta_actualizada;

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearAll();
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

        messageTextView = (TextView) rootView.findViewById(R.id.message_id);
        messageTextView2 = (TextView) rootView.findViewById(R.id.message_id2);

        paid = (EditText) rootView.findViewById(R.id.txtPaid);

        change = (TextView) rootView.findViewById(R.id.txtChange);

        String metodoPagoCliente = ((DistribucionActivity) getActivity()).getMetodoPagoCliente();

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


                            pagoCon = Double.parseDouble(paid.getText().toString());
                            totalPagoCon = String.format("%,.2f", pagoCon);
                            double total = totalTotal;

                            if (pagoCon >= total) {
                                double vuelto = pagoCon - total;
                                totalVuelvo = String.format("%,.2f", vuelto);

                                change.setText(totalVuelvo);

                                // aplicarFactura();
                                obtenerLocalización();
                            } else {
                                Toast.makeText(getActivity(), "Digite una cantidad mayor al total", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
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
                            PrinterFunctions.imprimirFacturaDistrTotal(venta_actualizada, getActivity(), 1);
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

    protected void actualizarFactura() {

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA FACTURAS
        final Realm realm2 = Realm.getDefaultInstance();
        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm2) {
                Facturas factura_actualizada = realm2.where(Facturas.class).equalTo("id", facturaId).findFirst();


                factura_actualizada.setDate(Functions.getDate());
                factura_actualizada.setTimes(Functions.get24Time());

                factura_actualizada.setSubtotal_taxed(String.valueOf(totalGrabado));
                factura_actualizada.setSubtotal_exempt(String.valueOf(totalExento));
                factura_actualizada.setSubtotal(String.valueOf(totalSubtotal));
                factura_actualizada.setDiscount(String.valueOf(totalDescuento));
                factura_actualizada.setTax(String.valueOf(totalImpuesto));
                factura_actualizada.setTotal(String.valueOf(totalTotal));

                factura_actualizada.setPaid(totalPagoCon);
                factura_actualizada.setChanging(totalVuelvo);

                factura_actualizada.setNote(notes.getText().toString());
                factura_actualizada.setCanceled("1");
                factura_actualizada.setAplicada(1);


                realm2.insertOrUpdate(factura_actualizada);
                realm2.close();
            }
        });
    }

    protected void actualizarVenta() {

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        final Realm realm3 = Realm.getDefaultInstance();
        realm3.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm3) {
                venta_actualizada = realm3.where(Venta.class).equalTo("id", facturaId).findFirst();

                venta_actualizada.setCustomer_name(client_name.getText().toString());
                venta_actualizada.setSale_type("1");
                venta_actualizada.setUpdated_at(Functions.getDate() + " " + Functions.get24Time());
                venta_actualizada.setAplicada(1);

                realm3.insertOrUpdate(venta_actualizada);
                realm3.close();
            }
        });

    }

    protected void aplicarFactura() {
        paid.setEnabled(false);

        actualizarFactura();
        actualizarVenta();

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

    public void obtenerLocalización() {

        MyLocationListener mLocationListener = new MyLocationListener();
        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000L, 0F, mLocationListener);

        /* Use the LocationManager class to obtain GPS locations */
 /*       LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                (LocationListener) mlocListener);*/

    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    messageTextView2.setText("Mi direccion es: \n"
                            + address.getAddressLine(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Class My Location Listener */
    public class MyLocationListener implements LocationListener {
        DistTotalizarFragment mainActivity;

        public DistTotalizarFragment getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(DistTotalizarFragment mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            messageTextView.setText(Text);
            this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            messageTextView.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            messageTextView.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Este metodo se ejecuta cada vez que se detecta un cambio en el
            // status del proveedor de localizacion (GPS)
            // Los diferentes Status son:
            // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
            // TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
            // espera que este disponible en breve
            // AVAILABLE -> Disponible
        }

    }/* End of Class MyLocationListener */




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