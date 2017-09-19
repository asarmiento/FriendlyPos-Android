package com.friendlypos.principal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.principal.adapters.ClientesAdapter;
import com.friendlypos.principal.adapters.ProductosAdapter;
import com.friendlypos.principal.interfaces.RequestInterface;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientesActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private ClientesAdapter adapter;
    public String mBaseUrl = "http://friendlyaccount.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_volver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent clientes;
            clientes = new Intent(ClientesActivity.this, MenuPrincipal.class);
            startActivity(clientes);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.clientes_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getClient();
    }


    private void getClient() {

        RequestInterface request = BaseManager.getClient(mBaseUrl).create(RequestInterface.class);

        /**
         GET List Resources
         **/
        Call<Clientes> call = request.getJSON();
        call.enqueue(new Callback<Clientes>() {
            @Override
            public void onResponse(Call<Clientes> call, Response<Clientes> response) {


                Log.d("TAG", response.code() + "");

                Clientes resource = response.body();

                List<Clientes.Client> datumList = resource.datosClientes;

                adapter = new ClientesAdapter(datumList);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<Clientes> call, Throwable t) {
                call.cancel();
            }
        });

    }
}
