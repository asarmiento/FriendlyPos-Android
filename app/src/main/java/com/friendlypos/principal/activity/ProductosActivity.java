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
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.principal.adapters.ProductosAdapter;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.principal.modelo.ProductosResponse;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private RecyclerView recyclerView;
    private TextView mTvTitle;
    private ArrayList<Productos> mContentsArray = new ArrayList<>();
    private ProductosAdapter adapter;

    private Realm realm;
    private RequestInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = new ProgressDialog(this);
        progress.setMessage("Cargando lista de productos");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        // Init Realm
        realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("Productos1.realm")
                .build();
        // Create a new empty instance of Realm
        realm = Realm.getInstance(realmConfiguration);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new ProductosAdapter(mContentsArray);
        recyclerView.setAdapter(adapter);

        api = BaseManager.getApi();
        Call<ProductosResponse> call = api.getProducts();

        call.enqueue(new Callback<ProductosResponse>() {
            @Override
            public void onResponse(Call<ProductosResponse> call, Response<ProductosResponse> response) {
                mContentsArray.clear();

                if(response.isSuccessful()) {
                    progress.dismiss();

                    // Set title
                    String title = response.body().getCode();
                    Log.d("asdasdasda", title + "");
                    mTvTitle.setText(title);
                    mContentsArray.addAll(response.body().getProductos());

                    // Add content to the realm DB
                    // Open a transaction to store items into the realm
                    // Use copyToRealm() to convert the objects into proper RealmObjects managed by Realm.
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(mContentsArray);
                    realm.commitTransaction();

                    Toast.makeText(ProductosActivity.this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                } else {
                    progress.dismiss();
                    Toast.makeText(ProductosActivity.this, getString(R.string.error) + " CODE: " +response.code(), Toast.LENGTH_LONG).show();
                    RealmResults<Productos> results = realm.where(Productos.class).findAll();
                    mContentsArray.addAll(results);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ProductosResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(ProductosActivity.this, getString(R.string.failed), Toast.LENGTH_LONG).show();
                RealmResults<Productos> results = realm.where(Productos.class).findAll();
                mContentsArray.addAll(results);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(ProductosActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
