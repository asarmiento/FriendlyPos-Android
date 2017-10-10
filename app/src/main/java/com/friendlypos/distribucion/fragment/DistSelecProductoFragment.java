package com.friendlypos.distribucion.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.principal.modelo.Clientes;

import io.realm.Realm;
import io.realm.RealmResults;


public class DistSelecProductoFragment extends Fragment {
    private RealmResults<Clientes> listaPedidos;
    private Realm realm;
    TextView textView3;

    public DistSelecProductoFragment() {
        // Required empty public constructor
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

      /*  recyclerView = (RealmRecyclerView) rootView.findViewById(R.id.rrvListaUsuario);*/

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                listaPedidos = realm.where(Clientes.class).findAll();
                textView3.setText((CharSequence) listaPedidos);
            }

        });




     /*   adapter = new RecyclerAdapter(getActivity(), listaPedidos, true, true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();*/

        return rootView;
    }

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
