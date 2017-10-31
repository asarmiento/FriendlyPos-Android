package com.friendlypos.principal.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.adapters.ClientesAdapter;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.ClientesResponse;

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

public class DescargarCatalogo extends AppCompatActivity {

        private ProgressDialog progress;
        private ArrayList<Clientes> mContentsArray = new ArrayList<>();

        private Realm realm;
        private RequestInterface api;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_clientes);
            ButterKnife.bind(this);

            progress = new ProgressDialog(this);
            progress.setMessage("Cargando catálogo");
            progress.setCanceledOnTouchOutside(false);
            progress.show();

            // Obtener token de usuario
            String token = "Bearer " + SessionPrefes.get(this).getToken();
            Log.d("tokenCliente", token +" ");

            realm = Realm.getDefaultInstance();

            api = BaseManager.getApi();
            Call<ClientesResponse> call = api.getJSON(token);

            call.enqueue(new Callback<ClientesResponse>() {
                @Override
                public void onResponse(Call<ClientesResponse> call, Response<ClientesResponse> response) {
                    mContentsArray.clear();

                    if(response.isSuccessful()) {
                        progress.dismiss();

                        mContentsArray.addAll(response.body().getContents());

                        // Add content to the realm DB
                        // Open a transaction to store items into the realm
                        // Use copyToRealm() to convert the objects into proper RealmObjects managed by Realm.

                        realm.beginTransaction();
                        realm.copyToRealm(mContentsArray);
                        realm.commitTransaction();
                        realm.close();

                        Toast.makeText(DescargarCatalogo.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        progress.dismiss();
                        Toast.makeText(DescargarCatalogo.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                        RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                        mContentsArray.addAll(results);
                    }
                }

                @Override
                public void onFailure(Call<ClientesResponse> call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(DescargarCatalogo.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                    RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                    mContentsArray.addAll(results);
                }
            });

        }
    }

