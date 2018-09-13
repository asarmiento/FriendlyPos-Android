package com.friendlypos.Recibos.util;

import android.util.Log;

import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.Bonuses;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class TotalizeHelperRecibos {

    private static final double IVA = 13.0;
    String customer;
    RecibosActivity activity;
    private static double productosDelBonus = 0;
    SessionPrefes session;


    public TotalizeHelperRecibos(RecibosActivity activity) {
        this.activity = activity;
        session = new SessionPrefes(getApplicationContext());
    }

    public void destroy() {
        activity = null;
    }

    /*private String getProductTypeByPivotId(String id) {
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
    }*/

  /*  private Double getClienteFixedDescuentoByPivotId(String id) {

        sale ventaDetallePreventa = activity.getCurrentVenta();
        ventaDetallePreventa.getInvoice_id();
        customer = ventaDetallePreventa.getCustomer_id();

        Realm realm = Realm.getDefaultInstance();

        Double clienteFixedDescuento = Double.valueOf(realm.where(Clientes.class).equalTo("id", customer).findFirst().getFixedDiscount());

        realm.close();
        return clienteFixedDescuento;
    }*/

    public void totalizeRecibos(List<Recibos> pivotList) {
        for (Recibos p : pivotList) {
            totalize(p);
        }
    }

    private void totalize(final Recibos currentPivot) {

        Double totalPago = currentPivot.getTotal();
      /*  String tipo = getProductTypeByPivotId(currentPivot.getProduct_id());
        double agrego = currentPivot.getAmountSinBonus();
        String bonus = getProductBonusByPivotId(currentPivot.getProduct_id());*/
        Double total = 0.0;
        total = totalPago;

        Log.d("TOTALTODOS", total +"");

      /*  activity.setTotalizarSubGrabado(subGrab);
        activity.setTotalizarSubExento(subExen);
        activity.setTotalizarSubTotal(subt);
        activity.setTotalizarDescuento(discountBill);

        activity.setTotalizarImpuestoIVA(IvaT);*/
        activity.setTotalizarTotal(total);
        //  activity.setTotalizarTotalDouble(total);

    }
}
