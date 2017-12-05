package com.friendlypos.distribucion.fragment;

import android.os.Bundle;
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

    String totalGrabado = "0";
    double totalExento = 0.0;
    String totalSubtotal = "0";
    String totalDescuento = "0";
    String totalImpuesto = "0";
    String totalTotal = "0";
    String totalVuelvo = "0";
    String totalPagoCon = "0";
    static double totalTotalDouble;
    static double pagoCon = 0.0;
    String facturaId;


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

        paid = (EditText) rootView.findViewById(R.id.txtPaid);

        change = (TextView) rootView.findViewById(R.id.txtChange);

        String metodoPagoCliente = ((DistribucionActivity) getActivity()).getMetodoPagoCliente();

        if (metodoPagoCliente.equals("1")) {
            //bill_type = 1;
            try {
                paid.setEnabled(true);

            }
            catch (Exception e) {
                Log.d("JD", "Error " + e.getMessage());
            }

        }
        else if (metodoPagoCliente.equals("2")) {
            //  bill_type = 2;
            paid.setEnabled(false);
        }


        notes = (EditText) rootView.findViewById(R.id.txtNotes);

        applyBill = (Button) rootView.findViewById(R.id.applyInvoice);
        printBill = (Button) rootView.findViewById(R.id.printInvoice);

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
                        //validateData();
                        // Log.d("total", String.valueOf(Functions.sGetDecimalStringAnyLocaleAsDouble(Total.getText().toString())));


                        pagoCon = Double.parseDouble(paid.getText().toString());
                        totalPagoCon = String.format("%,.2f", pagoCon);
                        double total = totalTotalDouble;

                        if (pagoCon >= total) {
                            double vuelto = pagoCon - total;
                            totalVuelvo = String.format("%,.2f", vuelto);

                            change.setText(totalVuelvo);

                            aplicarFactura();
                        }
                        else {
                            Toast.makeText(getActivity(), "Digite una cantidad mayor al total", Toast.LENGTH_LONG).show();
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
                        PrinterFunctions.imprimirFacturaDistrTotal(venta_actualizada, getActivity(), 1);
                    }
                    catch (Exception e) {
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
        Log.d("TotalExento", String.valueOf(((DistribucionActivity) getActivity()).getTotalizarSubExento()));
        totalSubtotal = ((DistribucionActivity) getActivity()).getTotalizarSubTotal();
        totalDescuento = ((DistribucionActivity) getActivity()).getTotalizarDescuento();
        totalImpuesto = ((DistribucionActivity) getActivity()).getTotalizarImpuestoIVA();
        totalTotal = ((DistribucionActivity) getActivity()).getTotalizarTotal();
        totalTotalDouble = ((DistribucionActivity) getActivity()).getTotalizarTotalDouble();
        facturaId = ((DistribucionActivity) getActivity()).getInvoiceId();

        subGra.setText(totalGrabado);
        subExe.setText(String.format("%,.2f", totalExento));

        subT.setText(totalSubtotal);
        discount.setText(totalDescuento);

        ivaSub.setText(totalImpuesto);
        Total.setText(totalTotal);

        Log.d("FACTURAIDTOTALIZAR", facturaId);


    }

    private void clearUI() {

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

                factura_actualizada.setSubtotal_taxed(totalGrabado);
                factura_actualizada.setSubtotal_exempt(String.valueOf(totalExento));
                factura_actualizada.setSubtotal(totalSubtotal);
                factura_actualizada.setDiscount(totalDescuento);
                factura_actualizada.setTax(totalImpuesto);
                factura_actualizada.setTotal(totalTotal);

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
        }
        catch (Exception e) {
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