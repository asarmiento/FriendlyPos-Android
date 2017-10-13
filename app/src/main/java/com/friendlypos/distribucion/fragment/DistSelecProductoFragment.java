package com.friendlypos.distribucion.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class DistSelecProductoFragment extends Fragment {
    private RealmResults<Clientes> listaPedidos;
    private Realm realm;
    TextView textView3;

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
        textView3 = (TextView) rootView.findViewById(R.id.textView3);
        Log.d("lista", getList() + "");
textView3.setText(getList() + "");



     /*   adapter = new RecyclerAdapter(getActivity(), listaPedidos, true, true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();*/

        return rootView;
    }

    private List<Clientes> getList(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Clientes> query = realm.where(Clientes.class);
        RealmResults<Clientes> result1 = query.findAll();

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
