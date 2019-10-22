package com.friendlypos.reimprimirRecibos.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.reimprimirRecibos.activity.ReimprimirRecibosActivity;
import com.friendlypos.reimprimirRecibos.adapters.ReimprimirReciboFacturaAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ReimprimirReciboFacturaFragment extends BaseFragment {

    @Bind(R.id.recyclerViewReimprimirReciboFactura)
    public RecyclerView recyclerView;

    private ReimprimirReciboFacturaAdapter adapter;

    RealmResults<receipts> result1;
    Realm realm;

    public static ReimprimirReciboFacturaFragment getInstance() {
        return new ReimprimirReciboFacturaFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reimprimir_recibo_factura, container,
                false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ReimprimirReciboFacturaAdapter(getContext(), ((ReimprimirRecibosActivity)getActivity()), getListClientes());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


    }

    private List<receipts> getListClientes(){
        realm = Realm.getDefaultInstance();
      //  RealmQuery<recibos> query = realm.where(recibos.class).equalTo("aplicada", 1).equalTo("facturaDePreventa", "Distribucion").or().equalTo("facturaDePreventa", "VentaDirecta");

        RealmQuery<receipts> query = realm.where(receipts.class);
        RealmResults<receipts> result1 = query.findAll();

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
