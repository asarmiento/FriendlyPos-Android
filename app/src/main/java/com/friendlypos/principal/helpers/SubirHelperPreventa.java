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
import com.friendlypos.principal.activity.MenuPrincipal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Delvo on 02/12/2017.
 */

public class SubirHelperPreventa {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private Activity activity;
    private Context mContext;
    private RequestInterface mAPIService;
    int codigoServer;

    public int getCodigoServer() {
        return codigoServer;
    }

    public void setCodigoServer(int codigoServer) {
        this.codigoServer = codigoServer;
    }
    public SubirHelperPreventa(Activity activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostPreventa(EnviarFactura facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");

        mAPIService.savePostPreventa(facturaQuery, token).enqueue(new Callback<invoice>() {

            @Override
            public void onResponse(Call<invoice> call, Response<invoice> response) {

                if(response.isSuccessful()) {
                    Log.d("respPreventa",response.body().toString());
                    Toast.makeText(activity, response.message().toString(), Toast.LENGTH_SHORT).show();
                    int codigo = response.code();
                    setCodigoServer(codigo);
                }
                else{
                    Toast.makeText(activity, response.message().toString(), Toast.LENGTH_SHORT).show();
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
