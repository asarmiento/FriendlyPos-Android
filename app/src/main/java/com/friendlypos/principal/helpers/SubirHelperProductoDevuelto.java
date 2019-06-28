package com.friendlypos.principal.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.EnviarClienteVisitado;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.activity.MenuPrincipal;
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
    private MenuPrincipal activity;
    private Context mContext;
    private RequestInterface mAPIService;
    int codigoServer;
    int codigo;
    String codigoS;
    String mensajeS;
    String resultS;
    public SubirHelperProductoDevuelto(MenuPrincipal activity) {
        this.activity = activity;
        this.mContext = activity;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
    }

    public void sendPostClienteProductoDevuelto(EnviarProductoDevuelto facturaQuery, final int cantidadFactura) {
        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token + " ");
        if (isOnline()) {
            Log.d("factura1", facturaQuery + " ");
            mAPIService.savePostProductoDevuelto(facturaQuery, token).enqueue(new Callback<Inventario>() {

                @Override
                public void onResponse(Call<Inventario> call, Response<Inventario> response) {


                    if(response.isSuccessful()) {
                        Log.d("respPreventa",response.body().toString());

                        codigo = response.code();
                        codigoS = response.body().getCode();
                        mensajeS = response.body().getMessages();
                        resultS= String.valueOf(response.body().isResult());

                        activity.codigoDeRespuestaProductoDevuelto(codigoS, mensajeS, resultS, codigo, cantidadFactura);
                        Log.d("codID",cantidadFactura+"");

                    }
                    else{
                        Log.d("respPreventa", "Error");
                    }

                }

                @Override
                public void onFailure(Call<Inventario> call, Throwable t) {
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
