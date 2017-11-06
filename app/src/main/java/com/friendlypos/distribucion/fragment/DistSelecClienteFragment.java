package com.friendlypos.distribucion.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.distribucion.adapters.DistrClientesAdapter;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class DistSelecClienteFragment extends Fragment {
    private Realm realm;

    @Bind(R.id.recyclerViewDistrCliente)
    public RecyclerView recyclerView;

    private DistrClientesAdapter adapter;

    public static DistSelecProductoFragment getInstance() {
        return new DistSelecProductoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_distribucion_cliente, container,
                false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new DistrClientesAdapter(getList());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter);

    /*    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                view.setBackgroundColor(Color.parseColor("#607d8b"));
                Clientes movie = getList().get(position);
                Toast.makeText(getApplicationContext(), movie.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        Log.d("lista", getList() + "");*/

    }

    private List<Venta> getList(){
        realm = Realm.getDefaultInstance();
        RealmQuery<Venta> query = realm.where(Venta.class);
        RealmResults<Venta> result1 = query.findAll();

        return result1;
    }
/*
    private void readStories(){
        RealmQuery<StoryRealm> query = realm.where(StoryRealm.class);
        RealmResults<StoryRealm> resultAllStories = query.findAll();
        arrayListStories = new ArrayList<>();
        for(StoryRealm storyRealm : resultAllStories){
            arrayListStories.add(new Story(storyRealm));
        }
        adapter = new AdapterComment(arrayListStories);
        commentsRecList.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }*/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
/*


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);


        String[] dataset = new String[100];
        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = "item" + i;
        }

       adapter = new ClientesAdapter(mContentsArray);
        mRecyclerView.setAdapter(adapter);

        adapter = new RecyclerAdapter(dataset, getActivity());
        mRecyclerView.setAdapter(adapter);

        super.onViewCreated(view, savedInstanceState);
    }*/

}