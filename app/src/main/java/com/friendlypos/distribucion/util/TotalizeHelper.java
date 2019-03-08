package com.friendlypos.distribucion.util;

import android.util.Log;

import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;

public class TotalizeHelper {

    private static final double IVA = 13.0;

    DistribucionActivity activity;

    public TotalizeHelper(DistribucionActivity activity) {
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
        Realm realm = Realm.getDefaultInstance();

        sale venta = realm.where(sale.class).equalTo("invoice_id", id).findFirst();
        Double clienteFixedDescuento = Double.valueOf(realm.where(Clientes.class).equalTo("id", venta.getCustomer_id()).findFirst().getFixedDiscount());

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
        Double subGrabConImp = 0.0;
        Double discountBill = 0.0;
        Double subGrabm = 0.0;
        Double subExen = 0.0;
        Double IvaT = 0.0;
        Double subt = 0.0;
        Double total = 0.0;
        Double cantidad = Double.valueOf(currentPivot.getAmount());
        Double precio = Double.valueOf(currentPivot.getPrice());
        Double descuento = Double.valueOf(currentPivot.getDiscount());
        Double subGrabDesc = 0.0;

        if (tipo.equals("1")) {
            subGrabConImp = subGrab + (precio) * (cantidad);
            subGrab = (subGrab + (precio) * (cantidad))/1.13;


            subGrabm = subGrabm + ((precio) * (cantidad) - ((descuento / 100) * (precio) * (cantidad)));
            discountBill += ((descuento / 100) * subGrab);
            Log.d("discountBillGr", discountBill + "");

            subGrabDesc = subGrab - discountBill;
            Log.d("subGrabDesc", subGrabDesc + "");

        }
        else {
            subExen = subExen + ((precio) * (cantidad));
            discountBill += ((descuento / 100) * subExen);
            Log.d("discountBillEx", discountBill + "");
        }

        discountBill += ((subExen * (clienteFixedDescuento / 100.00)) + (subGrabm * (clienteFixedDescuento / 100.00)));


        if (subGrab > 0) {
            //  IvaT = (subGrabm - (subGrabm * (clienteFixedDescuento / 100.00))) * (IVA / 100);
           // IvaT = subGrabConImp - subGrab;
            IvaT = subGrabDesc * 0.13;
            Log.d("IvaT", IvaT + "");
        }
        else {
            IvaT = 0.0;
        }

        subt = subGrab + subExen;

        Log.d("subtotal", subt + "");
        total = (subt + IvaT) - discountBill;

        activity.setTotalizarSubGrabado(subGrab);
        activity.setTotalizarSubExento(subExen);
        activity.setTotalizarSubTotal(subt);
        activity.setTotalizarDescuento(discountBill);

        activity.setTotalizarImpuestoIVA(IvaT);
        activity.setTotalizarTotal(total);

    }
}
