package com.friendlypos.principal.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.principal.adapters.ClientesAdapter;
import com.friendlypos.principal.interfaces.RequestInterface;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.friendlypos.principal.modelo.ClientesResponse;

public class ClientesActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private RecyclerView recyclerView;
    private TextView mTvTitle;
    private ArrayList<Clientes> mContentsArray = new ArrayList<>();
    private ClientesAdapter adapter;

    private Realm realm;
    private RequestInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(this);
        progress.setMessage("Cargando lista de clientes");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Fresco.initialize(this);

        // Init Realm
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("Clientes.realm")
                .build();
        // Create a new empty instance of Realm
        realm = Realm.getInstance(realmConfiguration);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ClientesAdapter(mContentsArray);
        recyclerView.setAdapter(adapter);

        api = BaseManager.getApi();
        Call<ClientesResponse> call = api.getJSON();

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

                    Toast.makeText(ClientesActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                } else {
                    progress.dismiss();
                    Toast.makeText(ClientesActivity.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                    RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                    mContentsArray.addAll(results);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ClientesResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(ClientesActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                RealmResults<Clientes> results = realm.where(Clientes.class).findAll();
                mContentsArray.addAll(results);
                adapter.notifyDataSetChanged();
            }
        });

    }
}
