package com.friendlypos.principal.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.adapters.DistrProductosInvAdapter;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.FacturasResponse;
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.InventarioResponse;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.ProductosResponse;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by DelvoM on 31/10/2017.
 */

public class DescargarInventario extends AsyncTask<Void, Integer, Boolean> {

    private Activity activity;

    private ArrayList<Inventario> mContentsArray = new ArrayList<>();
    private ArrayList<Facturas> mContentsArray2 = new ArrayList<>();

    private ProgressDialog pDialog;
    private Context mContext;

    private Realm realm;
    private RequestInterface api;

    public DescargarInventario(Activity activity){
        this.activity = activity;
        this.mContext = activity;

    }
    @Override
    protected Boolean doInBackground(Void... params) {

        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token +" ");

        api = BaseManager.getApi();

        Call<InventarioResponse> call = api.getInventory(token);

        call.enqueue(new Callback<InventarioResponse>() {
            @Override
            public void onResponse(Call<InventarioResponse> call, Response<InventarioResponse> response) {
                mContentsArray.clear();

                if(response.isSuccessful()) {

                    mContentsArray.addAll(response.body().getInventarios());

                    try {
                        // Work with Realm
                        realm.beginTransaction();
                        realm.copyToRealm(mContentsArray);
                        realm.commitTransaction();
                        //realm.close();
                    } finally {
                        realm.close();
                    }

                  //  Toast.makeText(DescargarInventario.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                } else {
                  //  Toast.makeText(DescargarInventario.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                    RealmResults<Inventario> results = realm.where(Inventario.class).findAll();
                    mContentsArray.addAll(results);
                }

            }

            @Override
            public void onFailure(Call<InventarioResponse> call, Throwable t) {
               // Toast.makeText(DescargarInventario.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                RealmResults<Inventario> results = realm.where(Inventario.class).findAll();
                mContentsArray.addAll(results);
            }
        });


        Call<FacturasResponse> call2 = api.getFacturas(token);

        call2.enqueue(new Callback<FacturasResponse>() {
            @Override
            public void onResponse(Call<FacturasResponse> call2, Response<FacturasResponse> response2) {
                mContentsArray2.clear();

                if(response2.isSuccessful()) {

                    mContentsArray2.addAll(response2.body().getFacturas());
                    realm = Realm.getDefaultInstance();
                    try {
                        realm.beginTransaction();
                        realm.copyToRealm(mContentsArray2);
                        realm.commitTransaction();
                    } finally {
                        realm.close();
                    }

                    Log.d("finish", mContentsArray2 +" ");

                    //Toast.makeText(ProductosActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                } else {
                    // Toast.makeText(ProductosActivity.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                    RealmResults<Facturas> results2 = realm.where(Facturas.class).findAll();
                    mContentsArray2.addAll(results2);
                }
            }

            @Override
            public void onFailure(Call<FacturasResponse> call2, Throwable t) {
                //  Toast.makeText(ProductosActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                RealmResults<Facturas> results2 = realm.where(Facturas.class).findAll();
                mContentsArray2.addAll(results2);
            }
        });

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progreso = values[0].intValue();

        pDialog.setProgress(progreso);
    }

    @Override
    protected void onPreExecute() {
        this.pDialog = new ProgressDialog(activity);
        this.pDialog.setMessage("Cargando lista de inventarios");
        if(!this.pDialog.isShowing()){
            this.pDialog.show();
        }
     /*  pDialog = new ProgressDialog(get);
        pDialog.setMessage("Cargando lista de inventarios");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setProgress(0);
        pDialog.show();*/
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result)
        {
            pDialog.dismiss();
            // Toast.makeText(DescargarCatalogo.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        //Toast.makeText(DescargarCatalogo.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
    }
}



