package com.friendlypos.distribucion.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrClientesAdapter;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class DistSelecClienteFragment extends BaseFragment{
    private Realm realm;

    @Bind(R.id.recyclerViewDistrCliente)
    public RecyclerView recyclerView;

    private DistrClientesAdapter adapter;

    public static DistSelecProductoFragment getInstance() {
        return new DistSelecProductoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_distribucion_cliente, container,
                false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new DistrClientesAdapter(getContext(), ((DistribucionActivity)getActivity()), getList());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private List<Venta> getList(){
        realm = Realm.getDefaultInstance();
        RealmQuery<Venta> query = realm.where(Venta.class);
        RealmResults<Venta> result1 = query.findAll();

        return result1;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public void updateData() {

    }
}
