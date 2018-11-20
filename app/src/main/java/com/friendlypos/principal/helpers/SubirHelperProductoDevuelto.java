package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.EnviarClienteVisitado;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.modelo.EnviarProductoDevuelto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Delvo on 02/12/2017.
 */

public class SubirHelperProductoDevuelto {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private Activity activity;
    private Context mContext;
    private RequestInterface mAPIService;

    public SubirHelperProductoDevuelto(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostClienteProductoDevuelto(EnviarProductoDevuelto facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");
        mAPIService.savePostProductoDevuelto(facturaQuery, token).enqueue(new Callback<Pivot>() {

            @Override
            public void onResponse(Call<Pivot> call, Response<Pivot> response) {
                if(response.isSuccessful()) {
                   // showResponse(response.body().toString());
                    Log.d("respClienteVisitado",response.body().toString());

                    if (response.code() == 200) {
                        Log.d("respClViMens",  "OK");
                        Toast.makeText(mContext, "Se subio con exito", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("respClViMens",  "ERROR");
                    }
                }
            }

            @Override
            public void onFailure(Call<Pivot> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });}
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
