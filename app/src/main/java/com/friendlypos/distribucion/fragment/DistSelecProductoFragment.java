package com.friendlypos.distribucion.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.distribucion.adapters.DistrProductosAdapter;
import com.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static java.lang.String.valueOf;


public class DistSelecProductoFragment extends Fragment {
    private Realm realm;
    RecyclerView recyclerView;
    private DistrSeleccionarProductosAdapter adapter;

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
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_distribucion_selecproduct, container,
                false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDistrSeleccProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DistrSeleccionarProductosAdapter(getList());
        recyclerView.setAdapter(adapter);

        Log.d("lista", getList() + "");

        return rootView;
    }

    private List<Inventario> getList(){
        realm = Realm.getDefaultInstance();
        RealmQuery<Inventario> query = realm.where(Inventario.class);
        RealmResults<Inventario> result1 = query.findAll();

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
