package com.friendlypos.preventas.delegate;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.preventas.modelo.saleDetallePreventa;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class PreSellInvoiceDelegate {

    PreventaActivity preventaActivity;

    invoiceDetallePreventa newInvoice;
    saleDetallePreventa newSale;
    List<Pivot> productofacturas;

    public PreSellInvoiceDelegate(PreventaActivity preventaActivity) {
        this.preventaActivity = preventaActivity;
    }

    public void initInvoiceDetallePreventa(int counter, String payMethodId) {
        newInvoice = new invoiceDetallePreventa();
        newInvoice.setP_id(counter);
        newInvoice.setP_payment_method_id(payMethodId);

        productofacturas = new ArrayList<>();
     //   newInvoice.setP_productofacturas(new RealmList<Pivot>((Pivot) productofacturas));

        Log.d("invoice1", newInvoice + "");

    }

    public void initVentaDetallePreventa(String p_id, String p_invoice_id, String p_customer_id, String p_customer_name,
                                         String p_cash_desk_id, String p_sale_type, String p_viewed, String p_applied,
                                         String p_created_at, String p_updated_at, String p_reserved, int aplicada, int subida) {
        newSale = new saleDetallePreventa();
        newSale.setP_id(p_id);
        newSale.setP_invoice_id(p_invoice_id);
        newSale.setP_customer_id(p_customer_id);
        newSale.setP_customer_name(p_customer_name);
        newSale.setP_cash_desk_id(p_cash_desk_id);
        newSale.setP_sale_type(p_sale_type);
        newSale.setP_viewed(p_viewed);
        newSale.setP_applied(p_applied);
        newSale.setP_created_at(p_created_at);
        newSale.setP_updated_at(p_updated_at);
        newSale.setP_reserved(p_reserved);
        newSale.setAplicada(aplicada);
        newSale.setSubida(subida);


        Log.d("invoiceSale", newSale + "");

    }

    public invoiceDetallePreventa getCurrentInvoice() {
        return newInvoice;
    }

    public saleDetallePreventa getCurrentVenta() {
        return newSale;
    }

   public void insertProduct(Pivot pivot) {
        productofacturas.add(pivot);

        Log.d("invoice2", productofacturas + "");
    }

   /* public void initProduct(int pos, int devuelto) {

        productofacturas = new ArrayList<>();

        productofacturas.get(pos).setDevuelvo(devuelto);
        //   newInvoice.setP_productofacturas(new RealmList<Pivot>((Pivot) productofacturas));

        Log.d("invoice5", productofacturas + "");

    }*/

    public List<Pivot> initProduct(int pos) {

        if (productofacturas.isEmpty()) {
            Toast.makeText(preventaActivity,"vacio",Toast.LENGTH_LONG).show();
            // send = "No hay invoice emitidas";
        }
        else {
                int devolver = productofacturas.get(pos).getDevuelvo();

                Log.d("invoiceDev", devolver + "");
                if( devolver == 0) {

                    productofacturas.get(pos).setDevuelvo(1);

                }else{
                  //  productofacturas.get(pos).removeAllChangeListeners();
                    Toast.makeText(preventaActivity,"no hay",Toast.LENGTH_LONG).show();
                }
        }
        Log.d("invoiceremover", productofacturas + "");
        return productofacturas;
    }


    public void borrarProducto(Pivot pivot) {

        productofacturas.get(0).setDevuelvo(1);

        Log.d("invoiceBorrar", productofacturas + "");
    }


    public List<Pivot> getAllPivot() {

        if (productofacturas.isEmpty()) {
            Toast.makeText(preventaActivity,"vacio",Toast.LENGTH_LONG).show();
           // send = "No hay invoice emitidas";
            }
         else {

            for (int i = 0; i < productofacturas.size(); i++) {

                int devolver = productofacturas.get(i).getDevuelvo();
                Log.d("invoiceDev", devolver + "");
                if( devolver == 0) {

                    productofacturas.get(i);

                }else{
                    productofacturas.remove(i);
                    Toast.makeText(preventaActivity,"no hay",Toast.LENGTH_LONG).show();
                }

            }
        }
        Log.d("invoice4", productofacturas + "");
        return productofacturas;
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
