package com.friendlypos.ventadirecta.delegate;

import android.util.Log;

import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
import com.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class PreSellInvoiceDelegateVD {

    VentaDirectaActivity preventaActivity;

    invoiceDetalleVentaDirecta newInvoice;
    sale newSale;
    List<Pivot> productofacturas;

    public PreSellInvoiceDelegateVD(VentaDirectaActivity preventaActivity) {
        this.preventaActivity = preventaActivity;
        productofacturas = new ArrayList<>();
    }

    public void initInvoiceDetalleVentaDirecta(String id, String type, String branch_office_id, String numeration,String key,
            String consecutive_number, double latitude, double longitude,
                                           String date, String times, String date_presale, String times_presale, String due_data,
                                           String invoice_type_id, String payment_method_id, String totalSubtotal, String totalGrabado,
                                           String totalExento, String totalDescuento, String percent_discount, String totalImpuesto,
                                           String totalTotal, String changing, String notes, String canceled,
                                           String paid_up, String paid, String created_at, String idUsuario,
                                           String idUsuarioAplicado, int creada, int aplicada) {

        newInvoice = new invoiceDetalleVentaDirecta();

        newInvoice.setP_id(Integer.parseInt(id));
        newInvoice.setP_type(type);
        newInvoice.setP_branch_office_id(branch_office_id);
        newInvoice.setP_numeration(numeration);
        newInvoice.setP_key(key);
        newInvoice.setP_consecutive_number(consecutive_number);
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
        newInvoice.setP_Creada(creada);
        newInvoice.setP_Aplicada(aplicada);

        Log.d("invoice1", newInvoice + "");

    }

    public void initVentaDetallesVentaDirecta(String p_id, String p_invoice_id, String p_customer_id, String p_customer_name,
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


        newSale.id = p_id;
        newSale.invoice_id = p_invoice_id;
        newSale.customer_id = p_customer_id;
        newSale.customer_name = p_customer_name;
        newSale.cash_desk_id = p_cash_desk_id;
        newSale.sale_type = p_sale_type;
        newSale.viewed = p_viewed;
        newSale.applied = p_applied;
        newSale.created_at = p_created_at;
        newSale.updated_at = p_updated_at;
        newSale.reserved = p_reserved;
        newSale.aplicada = aplicada;
        newSale.subida = subida;
        newSale.facturaDePreventa = facturaDePreventa;


        Log.d("invoiceSale", newSale + "");

    }

    public invoiceDetalleVentaDirecta getCurrentInvoiceVentaDirecta() {
        return newInvoice;
    }

    public sale getCurrentVentaVentaDirecta() {
        return newSale;
    }

    public void insertProductVentaDirecta(Pivot pivot) {
        productofacturas.add(pivot);

        Log.d("invoice2", productofacturas + "");
    }


    public List<Pivot> initProductVentaDirecta(int pos) {

        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio");
            // send = "No hay invoice emitidas";
        }
        else {
            int devolver = productofacturas.get(pos).devuelvo;

            Log.d("invoiceDev", devolver + "");
            if (devolver == 0) {

                productofacturas.get(pos).devuelvo = 1;

            }
            else {
                //  productofacturas.get(pos).removeAllChangeListeners();
                Log.d("vacio", "No hay productos");            }
        }
        Log.d("invoiceremover", productofacturas + "");
        return productofacturas;
    }


    public void borrarProductoVentaDirecta(Pivot pivot) {

        for (int i = 0; i < productofacturas.size(); i++) {
            Log.d("invoiceBorrarSize", productofacturas.size() + "");
            productofacturas.get(i).devuelvo = 1;
        }
        Log.d("invoiceBorrar", productofacturas + "");
    }


    public List<Pivot> getAllPivotVentaDirecta() {

        if (productofacturas.isEmpty()) {
            Log.d("vacio", "vacio");
            // send = "No hay invoice emitidas";
        }
        else {

            for (int i = 0; i < productofacturas.size(); i++) {

                int devolver = productofacturas.get(i).devuelvo;
                Log.d("invoiceDev", devolver + "");
                if (devolver == 0) {

                    productofacturas.get(i);
                    Log.d("productofacturasGet",  productofacturas.get(i)+"");
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

    public invoice getInvoiceByInvoiceDetalleVentaDirecta() {

        invoice invoice = new invoice();

        invoice.id = String.valueOf(newInvoice.getP_id());
        invoice.type = newInvoice.getP_type();
        invoice.branch_office_id = newInvoice.getP_branch_office_id();
        invoice.numeration = newInvoice.getP_numeration();
        invoice.key = newInvoice.getP_key();
        invoice.consecutive_number = newInvoice.getP_consecutive_number();
        invoice.latitud = newInvoice.getP_latitud();
        invoice.longitud = newInvoice.getP_longitud();
        invoice.date = newInvoice.getP_date();
        invoice.times = newInvoice.getP_times();
        invoice.date_presale = newInvoice.getP_date_presale();
        invoice.time_presale = newInvoice.getP_time_presale();
        invoice.due_date = newInvoice.getP_due_date();
        invoice.invoice_type_id = newInvoice.getP_invoice_type_id();
        invoice.payment_method_id = newInvoice.getP_payment_method_id();
        invoice.subtotal = String.valueOf(newInvoice.getP_subtotal());
        invoice.subtotal_taxed = String.valueOf(newInvoice.getP_subtotal_taxed());
        invoice.subtotal_exempt = String.valueOf(newInvoice.getP_subtotal_exempt());
        invoice.discount = String.valueOf(newInvoice.getP_discount());
        invoice.percent_discount = newInvoice.getP_percent_discount();
        invoice.tax = String.valueOf(newInvoice.getP_tax());
        invoice.total = String.valueOf(newInvoice.getP_total());
        invoice.changing = newInvoice.getP_changing();
        invoice.note = newInvoice.getP_note();
        invoice.canceled = newInvoice.getP_canceled();
        invoice.paid_up = newInvoice.getP_paid_up();
        invoice.paid = newInvoice.getP_paid();
        invoice.created_at = newInvoice.getP_created_at();
        invoice.user_id = newInvoice.getP_user_id();
        invoice.user_id_applied = newInvoice.getP_user_id_applied();
        invoice.sale = newSale;

        RealmList<Pivot> results = new RealmList<>();

        results.addAll(productofacturas);
        invoice.setProductofactura(results);

        invoice.aplicada = 1;
        invoice.subida = 1;
        invoice.facturaDePreventa = newInvoice.getFacturaDePreventa();

        Log.d("CREAR VENTA DIR", invoice + "");

        return invoice;
    }
}
