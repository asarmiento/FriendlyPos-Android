package com.friendlypos.distribucion.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter;
import com.friendlypos.distribucion.modelo.Inventario;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class DistSelecProductoFragment extends BaseFragment {
    private Realm realm;
    RecyclerView recyclerView;
    private DistrSeleccionarProductosAdapter adapter;
    private DistrResumenAdapter adapter2;
    public static DistSelecProductoFragment getInstance() {
        return new DistSelecProductoFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter2.clearAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_distribucion_selecproduct, container,
                false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDistrSeleccProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DistrSeleccionarProductosAdapter(((DistribucionActivity)getActivity()),getListProductos());
        recyclerView.setAdapter(adapter);

        Log.d("listaProducto", getListProductos() + "");

        adapter2 = new DistrResumenAdapter();
        adapter2.clearAll();
        return rootView;
    }

    private List<Inventario> getListProductos(){
        realm = Realm.getDefaultInstance();
        RealmQuery<Inventario> query = realm.where(Inventario.class);
        RealmResults<Inventario> result1 = query.findAll();

        return result1;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter2.clearAll();
        realm.close();
    }
    @Override
    public void updateData() {
        adapter.updateData(getListProductos());
    }
}
