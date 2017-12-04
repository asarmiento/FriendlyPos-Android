package com.friendlypos.reimpresion.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrClientesAdapter;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.reimpresion.activity.ReimprimirActivity;
import com.friendlypos.reimpresion.adapters.ReimprimirFacturaAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ReimprimirFacturaFragment extends BaseFragment {

    @Bind(R.id.recyclerViewReimprimirFactura)
    public RecyclerView recyclerView;

    private ReimprimirFacturaAdapter adapter;
    private DistrResumenAdapter adapter2;

    RealmResults<Venta> result1;
    Realm realm;
    public static ReimprimirFacturaFragment getInstance() {
        return new ReimprimirFacturaFragment();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter2.clearAll();
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter2.clearAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reimprimir_factura, container,
                false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ReimprimirFacturaAdapter(getContext(), ((ReimprimirActivity)getActivity()), getListClientes());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter2 = new DistrResumenAdapter();
        adapter2.clearAll();
    }

    private List<Venta> getListClientes(){
        realm = Realm.getDefaultInstance();
        RealmQuery<Venta> query = realm.where(Venta.class);
        RealmResults<Venta> result1 = query.findAll();

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

    }
}
