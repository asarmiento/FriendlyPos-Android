package com.friendlypos.distribucion.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.distribucion.adapters.DistrClientesAdapter;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class DistSelecClienteFragment extends Fragment  implements SearchView.OnQueryTextListener{
    private Realm realm;

    @Bind(R.id.recyclerViewDistrCliente)
    public RecyclerView recyclerView;

    private DistrClientesAdapter adapter;
    private static List<Venta> mSales = new ArrayList<>();

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        System.out.println("mSales size = " + mSales.size());
        final List<Venta> filteredModelList = filter(mSales, query);
        adapter.animateTo(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private List<Venta> filter(List<Venta> models, String query) {
        query = query.toLowerCase();

        final List<Venta> filteredModelList = new ArrayList<>();
        for (Venta model : models) {
            Clientes custumers = model.clientes;
            String text = custumers.getCompanyName().toLowerCase();
            String text2 = custumers.getFantasyName().toLowerCase();
            String text3 = custumers.getName().toLowerCase();
            String text4 = custumers.getCard().toLowerCase();
            if (text.contains(query) || text2.contains(query) || text3.contains(query) || text4.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    }
