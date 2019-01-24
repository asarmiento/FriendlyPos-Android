package com.friendlypos.Recibos.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.adapters.RecibosClientesAdapter;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.distribucion.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class RecibosClientesFragment extends BaseFragment {
    private Realm realm;

    @Bind(R.id.recyclerViewRecibosCliente)
    public RecyclerView recyclerView;

    private RecibosClientesAdapter adapter;


    public static RecibosClientesFragment getInstance() {
        return new RecibosClientesFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_recibos_clientes, container,
                false);
        ButterKnife.bind(this, rootView);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(adapter == null) {
            adapter = new RecibosClientesAdapter(getContext(), ((RecibosActivity) getActivity()), /* removeDuplicates(*/getListClientes()/*)*/);

        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    private List<recibos> getListClientes(){
        realm = Realm.getDefaultInstance();
        RealmQuery<recibos> query = realm.where(recibos.class);

        RealmResults<recibos> result1 = query.distinct("customer_id");

        if(result1.size() == 0){
            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();
        }

        Log.d("Resultado", String.valueOf(result1));
        return result1;


    }


/*
    public ArrayList<recibos> removeDuplicates(List<recibos> list){
        Set set = new TreeSet(new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                if(((recibos)o1).getCustomer_id().equalsIgnoreCase(((recibos)o2).getCustomer_id())){
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);

        final ArrayList newList = new ArrayList(set);
        Log.d("dupli", newList + "");
        return newList;

    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }


    public void updateData() {
    }
}
