package com.friendlypos.distribucion.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    int slecTAB;
    @Override
    public void onResume() {
        super.onResume();
        adapter.clearAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       /* FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.detach(DistResumenFragment.this).attach(DistResumenFragment.this).commit();
*/
        View rootView = inflater.inflate(R.layout.fragment_distribucion_resumen, container,
            false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDistrResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            adapter = new DistrResumenAdapter(getContext(), ((DistribucionActivity) getActivity()), this, getListResumen());
            recyclerView.setAdapter(adapter);

            Log.d("listaResumen", getListResumen() + "");

        return rootView;
    }

    private List<Pivot> getListResumen() {
        String facturaId = ((DistribucionActivity) getActivity()).getInvoiceId();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pivot> facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaId).findAll();
        realm.close();
        return facturaid1;
    }

    @Override
    public void updateData() {
        slecTAB = ((DistribucionActivity) getActivity()).getSelecClienteTab();
        if (slecTAB == 1) {
            adapter.clearAll();
            ((DistribucionActivity) getActivity()).cleanTotalize();
            adapter.updateData(getListResumen());
        }
        else{
            Toast.makeText(getActivity(),"nadaresumenUpdate",Toast.LENGTH_LONG).show();
        }
}}
