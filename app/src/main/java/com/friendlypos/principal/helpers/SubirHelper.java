package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.modelo.Facturas;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Delvo on 02/12/2017.
 */

public class SubirHelper {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private Activity activity;
    private Context mContext;
    private Realm realm, realm2, realmSysconfig, realmMarcas, realmTipoProducto,realmUsuarios, realmMetodoPago;
    private TextView mResponseTv;
    private RequestInterface mAPIService;

    public SubirHelper(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPost(String id, String branch_office_id, String numeration,String date, String times, String date_presale,String time_presale,
                         String due_date,String subtotal,String subtotal_taxed,String subtotal_exempt,String discount,String percent_discount,String tax,
                         String total,String changing,String note,String canceled,String paid_up,String paid,String created_at,String user_id,String user_id_applied,String invoice_type_id,
                         String payment_method_id, String venta, String productofacturas) {

        if (isOnline()) {

        mAPIService.savePost(id, branch_office_id, numeration,date,times,date_presale,time_presale,
                due_date,subtotal,subtotal_taxed,subtotal_exempt,discount,percent_discount,tax,
                total,changing,note,canceled,paid_up,paid,created_at,user_id,user_id_applied,invoice_type_id,
                payment_method_id, venta, productofacturas).enqueue(new Callback<Facturas>() {
            @Override
            public void onResponse(Call<Facturas> call, Response<Facturas> response) {

                if(response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                    Log.d("adasdasdasdasd",response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Facturas> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });}
    }

    public void showResponse(String response) {
        if(mResponseTv.getVisibility() == View.GONE) {
            mResponseTv.setVisibility(View.VISIBLE);
        }
        mResponseTv.setText(response);
    }


    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
