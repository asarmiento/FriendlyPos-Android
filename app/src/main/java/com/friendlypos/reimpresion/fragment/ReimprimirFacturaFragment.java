package com.friendlypos.reimpresion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.sale;
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

    RealmResults<sale> result1;
    Realm realm;

    public static ReimprimirFacturaFragment getInstance() {
        return new ReimprimirFacturaFragment();
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


    }

    private List<sale> getListClientes(){
        realm = Realm.getDefaultInstance();
        RealmQuery<sale> query = realm.where(sale.class).equalTo("aplicada", 1).equalTo("facturaDePreventa", "Distribucion").or().equalTo("facturaDePreventa", "VentaDirecta");
        RealmResults<sale> result1 = query.findAll();

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
