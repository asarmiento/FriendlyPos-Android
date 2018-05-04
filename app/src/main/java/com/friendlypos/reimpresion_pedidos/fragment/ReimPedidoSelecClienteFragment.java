package com.friendlypos.reimpresion_pedidos.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity;
import com.friendlypos.reimpresion_pedidos.adapters.ReimPedidoClientesAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class ReimPedidoSelecClienteFragment extends BaseFragment {
    private Realm realm;

    @Bind(R.id.recyclerViewReimPedidoCliente)
    public RecyclerView recyclerView;

    private ReimPedidoClientesAdapter adapter;

    public static DistSelecProductoFragment getInstance() {
        return new DistSelecProductoFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_reim_pedido_selec_cliente, container,
                false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ReimPedidoClientesAdapter(getContext(), ((ReimprimirPedidosActivity) getActivity()), getListClientes());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }


    private List<sale> getListClientes(){
        realm = Realm.getDefaultInstance();
        RealmQuery<sale> query = realm.where(sale.class).equalTo("aplicada", 1).equalTo("facturaDePreventa", 1).equalTo("subida", 1);
        RealmResults<sale> result1 = query.findAll();
        if(result1.size() == 0){
            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();
        }
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
