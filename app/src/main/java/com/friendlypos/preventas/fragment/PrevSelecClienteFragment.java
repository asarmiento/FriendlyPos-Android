package com.friendlypos.preventas.fragment;

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
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.adapters.PrevClientesAdapter;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class PrevSelecClienteFragment extends BaseFragment {
    private Realm realm;

    @Bind(R.id.recyclerViewPrevCliente)
    public RecyclerView recyclerView;

    private PrevClientesAdapter adapter;
   // private DistrResumenAdapter adapter2;

   /* public static DistSelecProductoFragment getInstance() {
        return new DistSelecProductoFragment();
    }*/
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
        View rootView = inflater.inflate(R.layout.fragment_prev_cliente, container,
                false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PrevClientesAdapter(getContext(), ((PreventaActivity) getActivity()), getList());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

      //  adapter2 = new DistrResumenAdapter();
    }

    private List<Clientes> getList(){
        realm = Realm.getDefaultInstance();
        RealmQuery<Clientes> query = realm.where(Clientes.class);
        RealmResults<Clientes> result1 = query.findAll();
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