package com.friendlypos.distribucion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.friendlypos.R;

import static java.lang.String.valueOf;


public class DistTotalizarFragment extends Fragment {

    private static TextView subGra;
    private static TextView subExe;
    private static TextView subT;
    private static TextView ivaSub;
    private static TextView discount;
    private static TextView Total;
    private static EditText paid;
    private static TextView change;
    private static EditText notes;
    private static Button applyBill;
    private static Button printBill;
    private static EditText client_name;
    private static EditText client_card;
    private static EditText client_contact;
    private static int apply_done = 0;
    private static Context QuickContext = null;
    private static CoordinatorLayout coordinatorLayout;


    @Override
    public void onResume() {
        super.onResume();
     /*   update_fields();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //container.removeAllViews();

        //ViewGroup mRootView= (ViewGroup)getActivity().findViewById(R.id.container);
        //android.transitions.everywhere.TransitionManager.beginDelayedTransition(mRootView, new Explode());
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
     /*   paid.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                setChange();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
*/
        change = (TextView) rootView.findViewById(R.id.txtChange);
        notes = (EditText) rootView.findViewById(R.id.txtNotes);

      /*  applyBill = (Button) rootView.findViewById(R.id.applyInvoice);
        printBill = (Button) rootView.findViewById(R.id.printInvoice);
        if (apply_done == 1) {
            applyBill.setVisibility(View.GONE);
            printBill.setVisibility(View.VISIBLE);
        }
        else {
            applyBill.setVisibility(View.VISIBLE);
            printBill.setVisibility(View.GONE);
        }
*/
       /* printBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            PrinterFunctions.dialogPrintBill(saleB, QuickContext, sysconf, coordinatorLayout, 1);
                        }
                        catch (Exception e) {
                            Functions.CreateMessage(QuickContext, "Error", e.getMessage() + "\n" + e.getStackTrace().toString());
                        }
                    }
                }

        );*/


       /* applyBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            validateData();
                            Log.d("total", String.valueOf(Functions.sGetDecimalStringAnyLocaleAsDouble(Total.getText().toString())));
                            applyBill();
                            //saveDataToSqlite();
                        }
                        catch (Exception e) {
                            Functions.createSnackBar(QuickContext, coordinatorLayout, e.getMessage(), 2, Snackbar.LENGTH_LONG);
                            e.printStackTrace();
                        }
                    }
                }


        );*/

        //getDataCustomer getcustomers = new getDataCustomer();
        // getcustomers.execute();
        /*update_fields();*/

     /*   if (currentSale != null && currentSale.customer_name.length() > 0) {
            Log.d("total customer_name", currentSale.customer_name);
            client_name.setText(currentSale.customer_name);
        }*/

        return rootView;
    }

   /* public void update_fields() {
        setTotalFields();
        if (bill_type == 1) {
            paid.setEnabled(true);
        }
        else {
            paid.setEnabled(false);
            paid.setText("");
            change.setText(valueOf("0"));
        }
    }*/

    /*protected void applyBill() {

        Sales sale = updateSale();
        Invoices invoice = updateInvoice();
        updateProductsByInvoice(invoice);

        Functions.createSnackBar(QuickContext, coordinatorLayout, "Venta realizada Correctamente", 1, Snackbar.LENGTH_LONG);

        applyBill.setVisibility(View.GONE);
        printBill.setVisibility(View.VISIBLE);

        apply_done = 1;

        saleB = new RunSale(sale);

        refreshSalesList();

        ProductsBill.clear();
        aListdata.clear();
        CurrentSale = null;
    }
*/
   /* protected void validateData() throws Exception {

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



       /* ClimDataValidator.validateEditTextHasText(txtRtime, "No ha ingresado una hora ");
        ClimDataValidator.validateEditTextHasText(txtRocName, "No ha ingresado OC cliente");
        ClimDataValidator.validateEditTextHasText(txtRdeliver, "No ha ingresado fecha de entrega");
        if (aListCantidades.isEmpty())
        {
            throw new TecValidatorException("No ha agregado ningun producto a el listado!");
        }
    }
*/
  /*  protected Invoices updateInvoice() {


        Invoices invoice = currentInvoice;
        invoice.user = users;
        invoice.canceled = true;
        invoice.date = Functions.getDate();
        invoice.times = Functions.get24Time();
        invoice.paid = Functions.sGetDecimalStringAnyLocaleAsDouble((paid.getText().toString().isEmpty()) ? "0" : paid.getText().toString());
        invoice.changing = Functions.sGetDecimalStringAnyLocaleAsDouble(change.getText().toString());
        invoice.subtotal = Functions.sGetDecimalStringAnyLocaleAsDouble(subT.getText().toString());
        invoice.subtotal_exempt = Functions.sGetDecimalStringAnyLocaleAsDouble(subExe.getText().toString());
        invoice.subtotal_taxed = Functions.sGetDecimalStringAnyLocaleAsDouble(subGra.getText().toString());
        invoice.tax = Functions.sGetDecimalStringAnyLocaleAsDouble(ivaSub.getText().toString());
        invoice.discount = Functions.sGetDecimalStringAnyLocaleAsDouble(discount.getText().toString());
        invoice.total = Functions.sGetDecimalStringAnyLocaleAsDouble(Total.getText().toString());
        invoice.notes = notes.getText().toString();
        invoice.updated_at = Functions.getDate() + " " + Functions.getTime();
        invoice.data_sync = 1;
        invoice.save();
        return invoice;
    }

    protected Sales updateSale() {
        Sales sale = currentSale;
        sale.date= Functions.getDateTime();

        sale.customer_name = client_name.getText().toString();
        sale.data_sync = 1;
        sale.sale_type = 1;
        sale.distributor = Boolean.FALSE;
        sale.save();
        return sale;
    }

    protected void updateProductsByInvoice(Invoices invoice) {

        ProductByInvoices productByI;

        for (DetailBill billd : mAdapterBill.getData()) {
            productByI = new Select().all().from(ProductByInvoices.class)
                    .where(Condition.column(ProductByInvoices$Table.INVOICE_INVOICE).eq(currentInvoice.id))
                    .and(Condition.column(ProductByInvoices$Table.PRODUCT_PRODUCT).eq(billd.inventories.product.id))
                    .querySingle();

            Boolean isNew = false;

            if (productByI == null) {
                productByI = new ProductByInvoices();
                ProductByInvoices _pBI = new Select().all().from(ProductByInvoices.class)
                        .orderBy(false, "id")
                        .querySingle();
                productByI.id = _pBI.id + 1;
                isNew = true;
            }

            double prevAmount = productByI.amount;

            productByI.product = billd.inventories.product;
            productByI.amount = Functions.sGetDecimalStringAnyLocaleAsDouble(billd.quantity);
            productByI.price = Functions.sGetDecimalStringAnyLocaleAsDouble(billd.price);
            productByI.invoice = invoice;
            productByI.discount = Functions.sGetDecimalStringAnyLocaleAsDouble(billd.descount);
            productByI.data_sync = 1;
            productByI.isSon = billd.isSon;
            if (isNew) {
                productByI.insert();
            }
            else {
                productByI.save();
            }
            Log.d("isNew", isNew.toString());
            Log.d("productByI id ", String.valueOf(productByI.id));
            Log.d("productByI product_id ", String.valueOf(productByI.product.id));

            for (Inventories inv : mInventories) {
                if (inv.id == billd.id) {
                    inv.amount_dist = (inv.amount_dist - prevAmount) + Functions.sGetDecimalStringAnyLocaleAsDouble(billd.quantity);
                    inv.save();
                }
            }
        }

    }*/

    /*private void setChange() {
        try {
            Double paidwith = Functions.sGetDecimalStringAnyLocaleAsDouble(paid.getText().toString());
            Double total = Functions.sGetDecimalStringAnyLocaleAsDouble(Total.getText().toString());
            if (paidwith != 0)
                change.setText(valueOf(paidwith - total));
        }
        catch (Exception e) {
        }
    }
*/

}