package com.friendlypos.reenvio_email.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrClientesAdapter;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reenvio_email.activity.EmailActivity;
import com.friendlypos.reenvio_email.adapters.EmailClientesAdapter;
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class EmailSelecClienteFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private Realm realm;
    @Bind(R.id.recyclerViewEmailCliente)
    public RecyclerView recyclerView;
    private EmailClientesAdapter adapter;

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

        View rootView = inflater.inflate(R.layout.fragment_email_selec_cliente, container,
                false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        return rootView;
        }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(adapter == null) {
            adapter = new EmailClientesAdapter(getContext(), ((EmailActivity) getActivity()), getListClientes());
            //adapter2 = new DistrResumenAdapter();
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        Log.d("listadeClientes", getListClientes() + "");
    }

    private List<Clientes> getListClientes(){

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_email, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(getListClientes());
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Clientes> filteredModelList = filter(getListClientes(), newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<Clientes> filter(List<Clientes> models, String query) {
        query = query.toLowerCase();

        final List<Clientes> filteredModelList = new ArrayList<>();
        for (Clientes model : models) {
            final String text = model.getFantasyName().toLowerCase();
            Log.d("FiltroEmail", text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


}
