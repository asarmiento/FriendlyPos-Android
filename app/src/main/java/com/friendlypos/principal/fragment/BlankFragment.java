package com.friendlypos.principal.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.principal.adapters.ProductosAdapter;
import com.friendlypos.principal.interfaces.RequestInterface;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlankFragment extends Fragment {

    public RecyclerView recyclerView;
    public ProductosAdapter adapter;
    public String mBaseUrl = "http://friendlyaccount.com/";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
      /*  RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);*/
        getClient();
     /*   recList.setLayoutManager(llm);*/
        return rootView;
    }

    private void getClient() {

        RequestInterface request = BaseManager.getClient(mBaseUrl).create(RequestInterface.class);

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
