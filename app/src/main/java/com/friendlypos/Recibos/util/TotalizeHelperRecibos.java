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


    public void totalizeRecibos(List<Recibos> pivotList) {
        for (Recibos p : pivotList) {
            totalize(p);
        }
    }

    private void totalize(final Recibos currentPivot) {

        Double totalPago = currentPivot.getTotal();

        Double total = 0.0;
        total = totalPago;


        Double totalPagado = currentPivot.getPaid();

        Double totalPagar = 0.0;
        totalPagar = totalPago - totalPagado;

        Log.d("TOTALTODOS", total +"");

        activity.setTotalizarTotal(total);
        activity.setTotalizarCancelado(totalPagar);


    }
}
