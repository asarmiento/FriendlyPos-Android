package com.friendlypos.distribucion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.runModelo.RunSale;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.ProductoFactura;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Sysconf;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmList;

import static java.lang.String.valueOf;


public class DistTotalizarFragment extends BaseFragment {

    private static TextView subGra;
    private static TextView subExe;
    private static TextView subT;
    private static TextView ivaSub;
    private static TextView discount;
    private static TextView Total;
    private static EditText paid;
    private static TextView change;
    private static EditText notes;
    private static EditText client_name;
    private static EditText client_card;
    private static EditText client_contact;

    String totalGrabado;
    String totalExento;
    String totalSubtotal;
    String totalDescuento;
    String totalImpuesto;
    String totalTotal;
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
    public void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_distribucion_totalizar, container, false);

        client_card = (EditText) rootView.findViewById(R.id.client_card);
        client_contact = (EditText) rootView.findViewById(R.id.client_contact);
        client_name = (EditText) rootView.findViewById(R.id.client_name);

        subExe = (TextView) rootView.findViewById(R.id.subExento);
        subGra = (TextView) rootView.findViewById(R.id.subGrabado);
        ivaSub = (TextView) rootView.findViewById(R.id.IvaFact);
        subT = (TextView) rootView.findViewById(R.id.subTotal);
        discount = (TextView) rootView.findViewById(R.id.Discount);
        Total = (TextView) rootView.findViewById(R.id.Total);

        paid = (EditText) rootView.findViewById(R.id.txtPaid);
        change = (TextView) rootView.findViewById(R.id.txtChange);

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
                            aplicarFactura();
                            //saveDataToSqlite();
                        }
                        catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }
                } );

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
        totalGrabado = ((DistribucionActivity) getActivity()).getTotalizarSubGrabado();
        totalExento = ((DistribucionActivity) getActivity()).getTotalizarSubExento();
        totalSubtotal = ((DistribucionActivity) getActivity()).getTotalizarSubTotal();
        totalDescuento = ((DistribucionActivity) getActivity()).getTotalizarDescuento();
        totalImpuesto = ((DistribucionActivity) getActivity()).getTotalizarImpuestoIVA();
        totalTotal = ((DistribucionActivity) getActivity()).getTotalizarTotal();
        facturaId = ((DistribucionActivity) getActivity()).getInvoiceId();

        subGra.setText(totalGrabado);
        subExe.setText(totalExento);

        subT.setText(totalSubtotal);
        discount.setText(totalDescuento);

        ivaSub.setText(totalImpuesto);
        Total.setText(totalTotal);

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

                factura_actualizada.setSubtotal_taxed(totalGrabado);
                factura_actualizada.setSubtotal_exempt(totalExento);
                factura_actualizada.setSubtotal(totalSubtotal);
                factura_actualizada.setDiscount(totalDescuento);
                factura_actualizada.setTax(totalImpuesto);
                factura_actualizada.setTotal(totalTotal);

                factura_actualizada.setChanging(change.getText().toString());
                factura_actualizada.setNote(notes.getText().toString());
                factura_actualizada.setCanceled("1");
                factura_actualizada.setPaid((paid.getText().toString().isEmpty()) ? "0" : paid.getText().toString());


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

                realm3.insertOrUpdate(venta_actualizada);
                realm3.close();
            }
        });

    }

    protected void aplicarFactura() {

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