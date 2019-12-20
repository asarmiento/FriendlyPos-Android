package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.modelo.EnviarFactura;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.activity.MenuPrincipal;

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
    private MenuPrincipal activity;
    private Context mContext;
    private RequestInterface mAPIService;
    int codigo;
    public String respuestaServer;
    String codigoS;
    String mensajeS;
    String resultS;
    int codigoServer;

    public int getCodigoServer() {
        return codigoServer;
    }

    public void setCodigoServer(int codigoServer) {
        this.codigoServer = codigoServer;
    }

    public String getRespuestaServer() {
        return respuestaServer;
    }

    public void setRespuestaServer(String respuestaServer) {
        this.respuestaServer = respuestaServer;
    }

    public SubirHelper(MenuPrincipal activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPost(EnviarFactura facturaQuery, final String cantidadFactura) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");
        mAPIService.savePost(facturaQuery, token).enqueue(new Callback<invoice>() {

            public void onResponse(Call<invoice> call, Response<invoice> response) {

                if(response.isSuccessful()) {
                    Log.d("respuestaFactura",response.body().toString());
                    codigo = response.code();
                    codigoS = response.body().getCode();
                    mensajeS = response.body().getMessage();
                    resultS= String.valueOf(response.body().isResult());
                    activity.codigoDeRespuestaDistr(codigoS, mensajeS, resultS, codigo, cantidadFactura);
                }
                else{

                }

            }


            @Override
            public void onFailure(Call<invoice> call, Throwable t) {
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
