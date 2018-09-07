package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.adapters.RecibosResumenAdapter;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.util.TotalizeHelper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class RecibosResumenFragment extends BaseFragment {
    private Realm realm;
    RecyclerView recyclerView;
    private RecibosResumenAdapter adapter;

    int slecTAB;
    RecibosActivity activity;

    public static RecibosResumenFragment getInstance() {
        return new RecibosResumenFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (RecibosActivity) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_recibos_resumen, container,
                false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewRecibosResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if(adapter == null) {
            adapter = new RecibosResumenAdapter(activity, this, getListProductos());
        }
        recyclerView.setAdapter(adapter);

        return rootView;

    }

    private List<Recibos> getListProductos() {
        String clienteId = activity.getClienteIdRecibos();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Recibos> result1 = realm.where(Recibos.class).equalTo("customer_id", clienteId).findAll();
        realm.close();

        return result1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // realm.close();
    }

    @Override
    public void updateData() {
        adapter.updateData(getListProductos());

    }

}
