package com.friendlypos.preventas.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.adapters.PrevClientesAdapter;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class PrevSelecClienteFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private Realm realm;

    @Bind(R.id.recyclerViewPrevCliente)
    public RecyclerView recyclerView;

    private PrevClientesAdapter adapter;

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
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(adapter == null) {
            adapter = new PrevClientesAdapter(getContext(), ((PreventaActivity) getActivity()), getList());
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
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
        adapter.updateData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(getList());
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
        final List<Clientes> filteredModelList = filter(getList(), newText);
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
            Log.d("FiltroPreventa", text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}