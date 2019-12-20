package com.friendlypos.ventadirecta.util;

import android.util.Log;

import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.Bonuses;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;

import java.util.List;

import io.realm.Realm;

public class TotalizeHelperVentaDirecta {

    private static final double IVA = 13.0;
    String customer;
    VentaDirectaActivity activity;
    private static double productosDelBonus = 0;
    private static double productosParaObtenerBonus = 0;
    public TotalizeHelperVentaDirecta(VentaDirectaActivity activity) {
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

    private String getProductBonusByPivotId(String id) {
        Realm realm = Realm.getDefaultInstance();
        String bonus = realm.where(Productos.class).equalTo("id", id).findFirst().getBonus();
        realm.close();
        return bonus;
    }
    private Double getProductIVAByPivotId(String id) {
        Realm realm = Realm.getDefaultInstance();
        Double iva = realm.where(Productos.class).equalTo("id", id).findFirst().getIva();
        realm.close();
        return iva;
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

    private void totalize(final Pivot currentPivot) {
        Double cantidad;

        Double clienteFixedDescuento = getClienteFixedDescuentoByPivotId(currentPivot.getInvoice_id());
        Double iva = getProductIVAByPivotId(currentPivot.getProduct_id());
        double agrego = currentPivot.getAmountSinBonus();
        String bonus = getProductBonusByPivotId(currentPivot.getProduct_id());

        if (bonus.equals("1") && currentPivot.getBonus() == 1){

            final Realm realmBonus = Realm.getDefaultInstance();

            realmBonus.executeTransactionAsync(new Realm.Transaction() {

                @Override
                public void execute(Realm realmBonus) {

                    Bonuses productoConBonus = realmBonus.where(Bonuses.class).equalTo("product_id", Integer.valueOf(currentPivot.getProduct_id())).findFirst();
                    productosDelBonus = Double.parseDouble(productoConBonus.getProduct_bonus());
                    productosParaObtenerBonus = Double.parseDouble(productoConBonus.getProduct_sale());

                    Log.d("BONIFTOTAL", productoConBonus.getProduct_id() +  " " + productosDelBonus);

                }
            });

            cantidad = agrego;
        }
        else{
            cantidad = Double.valueOf(currentPivot.getAmount());
        }

        Double subGrab = 0.0;
        Double subGrabConImp = 0.0;
        Double discountBill = 0.0;
        Double subGrabm = 0.0;
        Double subExen = 0.0;
        Double IvaT = 0.0;
        Double subt = 0.0;
        Double total = 0.0;


        Double subGrabDesc = 0.0;

        Double precio = Double.valueOf(currentPivot.getPrice());
        Double descuento = Double.valueOf(currentPivot.getDiscount());

        //if (tipo.equals("1")) {
        if (iva > 0.0) {
            subGrabConImp = subGrab + (precio) * (cantidad);
            Log.d("subGrabConImp", subGrabConImp + "");

            Log.d("IVA", iva + "");
            Double ivaConvertido = (iva / 100)+1;
            Log.d("ivaa", ivaConvertido + "");

            subGrab = (subGrab + (precio) * (cantidad))/ivaConvertido;
            Log.d("subGrab", subGrab + "");

            subGrabm = subGrabm + ((precio) * (cantidad) - ((descuento / 100) * (precio) * (cantidad)));
            Log.d("subGrabm", subGrabm + "");
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
            //IvaT = subGrabConImp - subGrab;
            Double impuesto = iva / 100;
            Log.d("IvaT", iva + "");
            IvaT = subGrabDesc * impuesto;
            Log.d("IvaT", IvaT + "");
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
