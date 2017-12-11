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
import com.friendlypos.login.util.SessionPrefes;

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

    public void sendPost(Facturas facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {

        mAPIService.savePost(facturaQuery, token).enqueue(new Callback<Facturas>() {
            @Override
            public void onResponse(Call<Facturas> call, Response<Facturas> response) {
                Log.i(TAG, "mamon " + response.body());
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
