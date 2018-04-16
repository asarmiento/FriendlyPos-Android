package com.friendlypos.preventas.util;

import android.util.Log;

import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.saleDetallePreventa;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;

public class TotalizeHelperPreventa {

    private static final double IVA = 13.0;
    String customer;
    PreventaActivity activity;

    public TotalizeHelperPreventa(PreventaActivity activity) {
        this.activity = activity;
    }

    public void destroy() {
        activity = null;
    }

    private String getProductTypeByPivotId(String id) {
        Realm realm = Realm.getDefaultInstance();
        String tipo = realm.where(Productos.class).equalTo("id", id).findFirst().getProduct_type_id();
        realm.close();
        return tipo;
    }

    private Double getClienteFixedDescuentoByPivotId(String id) {

        sale ventaDetallePreventa = activity.getCurrentVenta();
        ventaDetallePreventa.getInvoice_id();
        customer = ventaDetallePreventa.getCustomer_id();
       /* if(ventaDetallePreventa.getP_invoice_id() == id){



        }*/

        Realm realm = Realm.getDefaultInstance();

        //sale venta = realm.where(sale.class).equalTo("invoice_id", id).findFirst();
        Double clienteFixedDescuento = Double.valueOf(realm.where(Clientes.class).equalTo("id", customer).findFirst().getFixedDiscount());

        realm.close();
        return clienteFixedDescuento;
    }

    public void totalize(List<Pivot> pivotList) {
        for (Pivot p : pivotList) {
            totalize(p);
        }
    }

    private void totalize(Pivot currentPivot) {
        Double clienteFixedDescuento = getClienteFixedDescuentoByPivotId(currentPivot.getInvoice_id());
        String tipo = getProductTypeByPivotId(currentPivot.getProduct_id());

        Double subGrab = 0.0;
        Double discountBill = 0.0;
        Double subGrabm = 0.0;
        Double subExen = 0.0;
        Double IvaT = 0.0;
        Double subt = 0.0;
        Double total = 0.0;

        Double precio = Double.valueOf(currentPivot.getPrice());
        Double cantidad = Double.valueOf(currentPivot.getAmount());
        Double descuento = Double.valueOf(currentPivot.getDiscount());


        if (tipo.equals("1")) {
            subGrab = subGrab + (precio) * (cantidad);

            subGrabm = subGrabm + ((precio) * (cantidad) - ((descuento / 100) * (precio) * (cantidad)));
        }
        else {
            subExen = subExen + ((precio) * (cantidad));

        }
        discountBill += ((descuento / 100) * (precio) * (cantidad));

        discountBill += ((subExen * (clienteFixedDescuento / 100.00)) + (subGrabm * (clienteFixedDescuento / 100.00)));


        if (subGrab > 0) {
            IvaT = (subGrabm - (subGrabm * (clienteFixedDescuento / 100.00))) * (IVA / 100);
        }
        else {
            IvaT = 0.0;
        }

        subt = subGrab + subExen;
        // subTotal = String.format("%,.2f", subt);
        Log.d("subtotal", subt + "");
        total = (subt + IvaT) - discountBill;
        // Total = String.format("%,.2f", total);
        // Log.d("total", total + "");

        activity.setTotalizarSubGrabado(subGrab);
        activity.setTotalizarSubExento(subExen);
        activity.setTotalizarSubTotal(subt);
        activity.setTotalizarDescuento(discountBill);

        activity.setTotalizarImpuestoIVA(IvaT);
        activity.setTotalizarTotal(total);
        //  activity.setTotalizarTotalDouble(total);

    }
}
