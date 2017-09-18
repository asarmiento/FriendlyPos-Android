package com.friendlypos.principal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.principal.adapters.ProductosAdapter;
import com.friendlypos.principal.interfaces.RequestInterface;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductosAdapter adapter;
    public String mBaseUrl = "http://friendlyaccount.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        initViews();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.productos_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getClient();
    }


    private void getClient() {
      /*  Retrofit retrofit = null;
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request.Builder ongoing = chain.request().newBuilder();
                        ongoing.addHeader("Accept", "application/json");
                        ongoing.addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImNmNzhkZjlkY2MwMjI2ZjdkNTkzMDNiZjY0NTM0YTYxZWJjMDE5NzkzYjdmN2JhOWE3MDBhMjVkYjc2ODkyNTAwNDU0YzViMWVhNzZlMmI1In0.eyJhdWQiOiIyIiwianRpIjoiY2Y3OGRmOWRjYzAyMjZmN2Q1OTMwM2JmNjQ1MzRhNjFlYmMwMTk3OTNiN2Y3YmE5YTcwMGEyNWRiNzY4OTI1MDA0NTRjNWIxZWE3NmUyYjUiLCJpYXQiOjE1MDUzMzIwMDcsIm5iZiI6MTUwNTMzMjAwNywiZXhwIjoxNTM2ODY4MDA3LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.Bwl6qiipcwS8tWlzAwb-pw3-tPXLr54fmv3lx7jrwnCUWzyTKO9B9cGzNRw3C9mPejqw2PnOTtsCr3fVBy_3CB_wgEqWtITXYq5iiBvylJ7SjEugkgOy9bJqKomkROtmk1zC7E88g8OvZi6trgHxluLG8pVGf4VuQr89arFnAEkYB1T-P0xPnS3idX9mni5KSydxtWCvdXJmFg61Tbgs9X_KHZ64vAZWFWFbVzmEMtdL_S0Zu-u_hoksG6TA1cCa7qnY6nq_ByGMyT1RhvlVI_AA2RhtYXs6y4EbAT6XRrj39EM7kfonI9Vs1Q7vw-fY-vFdm1BC-V5ek5n7YfslcmsWfNvEW1iLAP8ezBuHdo9DHEK5Kz9Jm2DmV90Fq2JlP2bkhf78MxlhbQjCZbiOxouvhC8DuiUGvZqKJTZn-N_tOSVZAmhdT5UuikwLvZqkAZ4puvc-oNECwxyDJrcc_Q4Ll2amV9YmeOZikxXEvwc5TtCXjnvITYlvObqfmCv6ajQlH4L4OS056tDsopDPJ570DLTWbTJNLtoukiSJ4dQ5dPj7vRhjjgU4tB4o8PA9DXx2uLoKJOFYtkbYK-xxYe5pCSc-cfa586lS85GSSXBUzuoMWlRyWCFtdxeh4TWtE-aU2zEVpZzbGjy1iGR2VrvjpNPWeVowaFi4cIbQq_w"
                        );
                        return chain.proceed(ongoing.build());
                    }
                })
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://friendlyaccount.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
*/
        RequestInterface request = BaseManager.getClient(mBaseUrl).create(RequestInterface.class);

        /**
         GET List Resources
         **/
        Call<Productos> call = request.getJSON1();
        call.enqueue(new Callback<Productos>() {
            @Override
            public void onResponse(Call<Productos> call, Response<Productos> response) {


                Log.d("TAG", response.code() + "");

                Productos resource = response.body();

                List<Productos.Product> datumList = resource.datosProductos;

                adapter = new ProductosAdapter(datumList);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<Productos> call, Throwable t) {
                call.cancel();
            }
        });

    }
}
