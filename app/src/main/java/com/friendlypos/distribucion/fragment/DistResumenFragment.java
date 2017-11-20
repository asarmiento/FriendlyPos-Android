package com.friendlypos.distribucion.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.modelo.Pivot;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class DistResumenFragment extends BaseFragment {

    RecyclerView recyclerView;
    private DistrResumenAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_distribucion_resumen, container,
            false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDistrResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DistrResumenAdapter(getList1());
        recyclerView.setAdapter(adapter);

        Log.d("listaResumen", getList1() + "");

        return rootView;
    }

    private List<Pivot> getList1() {
        String facturaId = ((DistribucionActivity) getActivity()).getInvoiceId();
        Realm realm = Realm.getDefaultInstance();
        //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());

        RealmResults<Pivot> facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaId).findAll();

        realm.close();

        // RealmQuery<ProductoFactura> query = realm.where(ProductoFactura.class);
        // RealmResults<ProductoFactura> result1 = query.findAll();

        //SECUNDARIO
        //((DistribucionActivity) getActivity()).setInvoiceId(null);
        return facturaid1;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void updateData() {
        adapter.updateData(getList1());
    }
}
