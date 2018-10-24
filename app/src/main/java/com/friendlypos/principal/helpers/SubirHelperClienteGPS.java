package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.EnviarClienteVisitado;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.modelo.EnviarClienteGPS;
import com.friendlypos.principal.modelo.customer_location;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by DelvoM on 24/10/2018.
 */

public class SubirHelperClienteGPS {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private MenuPrincipal activity;
    private Context mContext;
    private RequestInterface mAPIService;

    int codigo;
    public String respuestaServer;
    String codigoS;
    String mensajeS;
    String resultS;

    public SubirHelperClienteGPS(MenuPrincipal activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostClienteGPS(EnviarClienteGPS facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");
            mAPIService.savePostClienteGPS(facturaQuery, token).enqueue(new Callback<customer_location>() {

                @Override
                public void onResponse(Call<customer_location> call, Response<customer_location> response) {

                        if(response.isSuccessful()) {
                            // showResponse(response.body().toString());
                            Log.d("respVentaDirecta",response.body().toString());
                            codigo = response.code();
                            codigoS = response.body().getCode();
                            mensajeS = response.body().getMessage();
                            resultS= String.valueOf(response.body().isResult());

                            activity.codigoDeRespuestaClienteGPS(codigoS, mensajeS, resultS, codigo);
                        }
                        else{
                        }
                }

                @Override
                public void onFailure(Call<customer_location> call, Throwable t) {
                    Log.e(TAG, "Unable to submit post to API.");
                }
            });}
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
