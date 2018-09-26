package com.friendlypos.preventas.delegate;

import android.util.Log;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class PreSellInvoiceDelegate {

    PreventaActivity preventaActivity;

    invoiceDetallePreventa newInvoice;
    sale newSale;
    List<Pivot> productofacturas;

    public PreSellInvoiceDelegate(PreventaActivity preventaActivity) {
        this.preventaActivity = preventaActivity;
        productofacturas = new ArrayList<>();
    }

    public void initInvoiceDetallePreventa(String id, String branch_office_id, String numeration, double latitude, double longitude,
                                           String date, String times, String date_presale, String times_presale, String due_data,
                                           String invoice_type_id, String payment_method_id, String totalSubtotal, String totalGrabado,
                                           String totalExento, String totalDescuento, String percent_discount, String totalImpuesto,
                                           String totalTotal, String changing, String notes, String canceled,
                                           String paid_up, String paid, String created_at, String idUsuario,
                                           String idUsuarioAplicado) {

        newInvoice = new invoiceDetallePreventa();

        newInvoice.setP_id(Integer.parseInt(id));
        newInvoice.setP_branch_office_id(branch_office_id);
        newInvoice.setP_numeration(numeration);
        newInvoice.setP_latitud(latitude);
        newInvoice.setP_longitud(longitude);
        newInvoice.setP_date(date);
        newInvoice.setP_times(times);
        newInvoice.setP_date_presale(date_presale);
        newInvoice.setP_time_presale(times_presale);
        newInvoice.setP_due_date(due_data);
        newInvoice.setP_invoice_type_id(invoice_type_id);
        newInvoice.setP_payment_method_id(payment_method_id);
        newInvoice.setP_subtotal(String.valueOf(totalSubtotal));
        newInvoice.setP_subtotal_taxed(String.valueOf(totalGrabado));
        newInvoice.setP_subtotal_exempt(String.valueOf(totalExento));
        newInvoice.setP_discount(String.valueOf(totalDescuento));
        newInvoice.setP_percent_discount(percent_discount);
        newInvoice.setP_tax(String.valueOf(totalImpuesto));
        newInvoice.setP_total(String.valueOf(totalTotal));
        newInvoice.setP_changing(changing);
        newInvoice.setP_note(notes);
        newInvoice.setP_canceled(canceled);
        newInvoice.setP_paid_up(paid_up);
        newInvoice.setP_paid(paid);
        newInvoice.setP_created_at(created_at);
        newInvoice.setP_user_id(idUsuario);
        newInvoice.setP_user_id_applied(idUsuarioAplicado);
        // newInvoice.setP_sale(newSale);

        newInvoice.setP_productofacturas(productofacturas);


        Log.d("invoice1", newInvoice + "");

    }

    public void initVentaDetallesPreventa(String p_id, String p_invoice_id, String p_customer_id, String p_customer_name,
                                          String p_cash_desk_id, String p_sale_type, String p_viewed, String p_applied,
                                          String p_created_at, String p_updated_at, String p_reserved, int aplicada, int subida, String facturaDePreventa) {
        newSale = new sale();

      /*  newSale.setP_id(p_id);
        newSale.setP_invoice_id(p_invoice_id);
        newSale.setP_customer_id(p_customer_id);
        newSale.setP_customer_name(p_customer_name);
        newSale.setP_cash_desk_id(p_cash_desk_id);
        newSale.setP_sale_type(p_sale_type);
        newSale.setP_viewed(p_viewed);
        newSale.setP_applied(p_applied);
        newSale.setP_created_at(p_created_at);
        newSale.setP_updated_at(p_updated_at);
        newSale.setP_reserved(p_reserved);*/


        newSale.setId(p_id);
        newSale.setInvoice_id(p_invoice_id);
        newSale.setCustomer_id(p_customer_id);
        newSale.setCustomer_name(p_customer_name);
        newSale.setCash_desk_id(p_cash_desk_id);
        newSale.setSale_type(p_sale_type);
        newSale.setViewed(p_viewed);
        newSale.setApplied(p_applied);
        newSale.setCreated_at(p_created_at);
        newSale.setUpdated_at(p_updated_at);
        newSale.setReserved(p_reserved);
        newSale.setAplicada(aplicada);
        newSale.setSubida(subida);
        newSale.setFacturaDePreventa(facturaDePreventa);


        Log.d("invoiceSale", newSale + "");

    }

    public invoiceDetallePreventa getCurrentInvoice() {
        return newInvoice;
    }

    public sale getCurrentVenta() {
        return newSale;
    }

    public void insertProduct(Pivot pivot) {
        productofacturas.add(pivot);

        Log.d("invoice2", productofacturas + "");
    }


    public List<Pivot> initProduct(int pos) {

        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio");
            // send = "No hay invoice emitidas";
        }
        else {
            int devolver = productofacturas.get(pos).getDevuelvo();

            Log.d("invoiceDev", devolver + "");
            if (devolver == 0) {

                productofacturas.get(pos).setDevuelvo(1);

            }
            else {
                //  productofacturas.get(pos).removeAllChangeListeners();
                Log.d("vacio", "No hay productos");            }
        }
        Log.d("invoiceremover", productofacturas + "");
        return productofacturas;
    }


    public List<Pivot> getAllPivot() {

        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio");
            // send = "No hay invoice emitidas";
        }
        else {

            for (int i = 0; i < productofacturas.size(); i++) {

                int devolver = productofacturas.get(i).getDevuelvo();
                Log.d("invoiceDev", devolver + "");
                if (devolver == 0) {

                    productofacturas.get(i);

                }
                else {
                    productofacturas.remove(i);
                    Log.d("vacio", "No hay productos");
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

        invoice.setId(String.valueOf(newInvoice.getP_id()));
        invoice.setBranch_office_id(newInvoice.getP_branch_office_id());
        invoice.setNumeration(newInvoice.getP_numeration());
        invoice.setLatitud(newInvoice.getP_latitud());
        invoice.setLongitud(newInvoice.getP_longitud());
        invoice.setDate(newInvoice.getP_date());
        invoice.setTimes(newInvoice.getP_times());
        invoice.setDate_presale(newInvoice.getP_date_presale());
        invoice.setTime_presale(newInvoice.getP_time_presale());
        invoice.setDue_date(newInvoice.getP_due_date());
        invoice.setInvoice_type_id(newInvoice.getP_invoice_type_id());
        invoice.setPayment_method_id(newInvoice.getP_payment_method_id());
        invoice.setSubtotal(String.valueOf(newInvoice.getP_subtotal()));
        invoice.setSubtotal_taxed(String.valueOf(newInvoice.getP_subtotal_taxed()));
        invoice.setSubtotal_exempt(String.valueOf(newInvoice.getP_subtotal_exempt()));
        invoice.setDiscount(String.valueOf(newInvoice.getP_discount()));
        invoice.setPercent_discount(newInvoice.getP_percent_discount());
        invoice.setTax(String.valueOf(newInvoice.getP_tax()));
        invoice.setTotal(String.valueOf(newInvoice.getP_total()));
        invoice.setChanging(newInvoice.getP_changing());
        invoice.setNote(newInvoice.getP_note());
        invoice.setCanceled(newInvoice.getP_canceled());
        invoice.setPaid_up(newInvoice.getP_paid_up());
        invoice.setPaid(newInvoice.getP_paid());
        invoice.setCreated_at(newInvoice.getP_created_at());
        invoice.setUser_id(newInvoice.getP_user_id());
        invoice.setUser_id_applied(newInvoice.getP_user_id_applied());
        invoice.setSale(newSale);

        RealmList<Pivot> results = new RealmList<>();

        results.addAll(productofacturas);
        invoice.setProductofactura(results);

        invoice.setAplicada(1);
        invoice.setSubida(1);

        invoice.setFacturaDePreventa(newInvoice.getFacturaDePreventa());

        Log.d("CREAR PREVENTA", invoice + "");

        return invoice;
    }
}
