package com.friendlypos.principal.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.Recibos.modelo.EnviarRecibos;
import com.friendlypos.Recibos.modelo.receipts;
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

public class SubirHelperRecibos {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private MenuPrincipal activity;
    private Context mContext;
    private RequestInterface mAPIService;
    int codigo;
    String codigoS;
    String mensajeS;
    String resultS;

    public SubirHelperRecibos(MenuPrincipal activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostRecibos(EnviarRecibos facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");

        mAPIService.savePostRecibos(facturaQuery, token).enqueue(new Callback<receipts>() {

            @Override
            public void onResponse(Call<receipts> call, Response<receipts> response) {

                if(response.isSuccessful()) {
                    Log.d("respRecibos",response.body().toString());

                    codigo = response.code();
                    codigoS = response.body().getCode();
                    mensajeS = response.body().getMessage();
                    resultS= String.valueOf(response.body().isResult());

                    activity.codigoDeRespuestaRecibos(codigoS, mensajeS, resultS, codigo);
                }
                else{
                }
            }

            @Override
            public void onFailure(Call<receipts> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API.");
            }
        });}
        else{
            Toast.makeText(activity, "Error, por favor revisar conexión de Internet", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
