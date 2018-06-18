package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.preventas.modelo.EnviarClienteVisitado;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Delvo on 02/12/2017.
 */

public class SubirHelperClienteVisitado {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private Activity activity;
    private Context mContext;
    private RequestInterface mAPIService;

    public SubirHelperClienteVisitado(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostClienteVisitado(EnviarClienteVisitado facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");
        mAPIService.savePostClienteVisitado(facturaQuery, token).enqueue(new Callback<visit>() {

            @Override
            public void onResponse(Call<visit> call, Response<visit> response) {
                if(response.isSuccessful()) {
                   // showResponse(response.body().toString());
                    Log.d("respClienteVisitado",response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<visit> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });}
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
