package com.friendlypos.preventas.delegate;

import android.util.Log;

import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class PreSellInvoiceDelegate {

    PreventaActivity preventaActivity;

    invoiceDetallePreventa newInvoice;

    List<Pivot> productofacturas;

    public PreSellInvoiceDelegate(PreventaActivity preventaActivity) {
        this.preventaActivity = preventaActivity;
    }

    public void initInvoiceDetallePreventa(int counter, String payMethodId) {
        newInvoice = new invoiceDetallePreventa();
        newInvoice.setP_id(counter);
        newInvoice.setP_payment_method_id(payMethodId);
        productofacturas = new ArrayList<>();
        //newInvoice.setP_productofacturas(new RealmList<Pivot>((Pivot) productofacturas));

        Log.d("invoice1", newInvoice + "");

    }

    public invoiceDetallePreventa getCurrentInvoice() {
        return newInvoice;
    }

   public void insertProduct(Pivot pivot) {
        productofacturas.add(pivot);
        Log.d("invoice2", productofacturas + "");
       Log.d("invoice3", newInvoice + "");
    }


    public void destroy() {
        this.preventaActivity = null;
        this.newInvoice = null;
    }

    public invoice getInvoiceByInvoiceDetalle() {

        invoice invoice = new invoice();

        invoice.setCode(newInvoice.getP_code());
        invoice.setCode(newInvoice.getP_code());
        invoice.setCode(newInvoice.getP_code());
        invoice.setCode(newInvoice.getP_code());
        invoice.setCode(newInvoice.getP_code());
        invoice.setCode(newInvoice.getP_code());
        invoice.setProductofactura(new RealmList<Pivot>((Pivot) productofacturas));
        return invoice;
    }
}
