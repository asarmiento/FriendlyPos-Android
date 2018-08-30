package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.modelo.EnviarFactura;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.login.util.SessionPrefes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Delvo on 02/12/2017.
 */

public class SubirHelperProforma {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private Activity activity;
    private Context mContext;
    private RequestInterface mAPIService;

    public SubirHelperProforma(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostProforma(EnviarFactura facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");
        mAPIService.savePostProforma(facturaQuery, token).enqueue(new Callback<invoice>() {

            @Override
            public void onResponse(Call<invoice> call, Response<invoice> response) {
                if(response.isSuccessful()) {
                   // showResponse(response.body().toString());
                    Log.d("respProforma",response.body().toString());
                    Toast.makeText(activity, response.body().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<invoice> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });}
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
