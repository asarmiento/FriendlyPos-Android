package com.friendlypos.principal.activity;
/**
 * Created by DelvoM on 31/10/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.ClientesResponse;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.ProductosResponse;

import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescargarCatalogo extends AsyncTask<Void, Integer, Boolean> {

    private Activity activity;

    private ProgressDialog pDialog;
    private ArrayList<Clientes> mContentsArray = new ArrayList<>();
    private ArrayList<Productos> mContentsArray2 = new ArrayList<>();
    private Context mContext;

    private Realm realm;
    private RequestInterface api;

    public DescargarCatalogo(Activity activity){
        this.activity = activity;
        this.mContext = activity;

    }
    @Override
    protected Boolean doInBackground(Void... params) {

        String token = "Bearer " + SessionPrefes.get(mContext).getToken();
        Log.d("tokenCliente", token +" ");


        api = BaseManager.getApi();
        Call<ClientesResponse> call = api.getJSON(token);

        call.enqueue(new Callback<ClientesResponse>() {
            @Override
            public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {
                mContentsArray.clear();

                if(response.isSuccessful()) {

                    mContentsArray.addAll(response.body().getContents());
                    realm = Realm.getDefaultInstance();
                    try {
                        // Work with Realm
                        realm.beginTransaction();
                        realm.copyToRealm(mContentsArray);
                        realm.commitTransaction();
                        //realm.close();
                    } finally {
                        realm.close();
                    }

                    Log.d("finish", mContentsArray +" ");
                //    Toast.makeText(DescargarCatalogo.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                } else {
               //     Toast.makeText(DescargarCatalogo.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                    RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                    mContentsArray.addAll(results);
                }
            }

            @Override
            public void onFailure(Call<ClientesResponse> call, Throwable t) {
                //Toast.makeText(DescargarCatalogo.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                mContentsArray.addAll(results);
            }
        });

        Call<ProductosResponse> call2 = api.getProducts(token);

        call2.enqueue(new Callback<ProductosResponse>() {
            @Override
            public void onResponse(Call<ProductosResponse> call2, Response<ProductosResponse> response2) {
                mContentsArray2.clear();

                if(response2.isSuccessful()) {

                    mContentsArray2.addAll(response2.body().getProductos());
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
                    RealmResults<Productos> results2 = realm.where(Productos.class).findAll();
                    mContentsArray2.addAll(results2);
                }
            }

            @Override
            public void onFailure(Call<ProductosResponse> call2, Throwable t) {
              //  Toast.makeText(ProductosActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                RealmResults<Productos> results2 = realm.where(Productos.class).findAll();
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


