package com.friendlypos.principal.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.crearCliente.modelo.customer_new;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.modelo.EnviarClienteNuevo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by DelvoM on 24/10/2018.
 */

public class SubirHelperClienteNuevo {

    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private MenuPrincipal activity;
    private Context mContext;
    private RequestInterface mAPIService;

    int codigo;
    public String respuestaServer;
    String codigoS;
    String mensajeS;
    String resultS;

    public SubirHelperClienteNuevo(MenuPrincipal activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostClienteNuevo(EnviarClienteNuevo facturaQuery) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");
            mAPIService.savePostClienteNuevo(facturaQuery, token).enqueue(new Callback<customer_new>() {

                @Override
                public void onResponse(Call<customer_new> call, Response<customer_new> response) {

                        if(response.isSuccessful()) {
                            Log.d("respClienteNuevo",response.body().toString());
                            codigo = response.code();
                            codigoS = response.body().getCode();
                            mensajeS = response.body().getMessages();
                            resultS= String.valueOf(response.body().isResult());

                            activity.codigoDeRespuestaClienteNuevo(codigoS, mensajeS, resultS, codigo);
                        }
                        else{
                            Toast.makeText(mContext, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                }

                @Override
                public void onFailure(Call<customer_new> call, Throwable t) {
                    Log.e(TAG, "Unable to submit post to API.");
                }
            });}
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(mContext);
    }
}
