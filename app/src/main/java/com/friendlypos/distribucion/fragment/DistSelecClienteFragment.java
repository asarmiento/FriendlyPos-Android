package com.friendlypos.distribucion.fragment;

import android.support.annotation.Nullable;
import android.os.Bundle;
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
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;


public class DistSelecClienteFragment extends BaseFragment  implements SearchView.OnQueryTextListener{
    private Realm realm;

    @Bind(R.id.recyclerViewDistrCliente)
    public RecyclerView recyclerView;

    private DistrClientesAdapter adapter;
    private DistrResumenAdapter adapter2;
    int i;
    String fantasyCliente;
    String idCliente;



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
        View rootView = inflater.inflate(R.layout.fragment_distribucion_cliente, container,
                false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(adapter == null) {
            adapter = new DistrClientesAdapter(getContext(), ((DistribucionActivity) getActivity()), getListClientes());

            adapter2 = new DistrResumenAdapter();
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private List<sale> getListClientes(){
        realm = Realm.getDefaultInstance();
        final RealmQuery<sale> query = realm.where(sale.class).equalTo("aplicada", 0);
        final RealmResults<sale> result1 = query.findAll();

        if(result1.size() == 0){
            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();
        }


        for(i=0; i< result1.size();i++){

          final Realm realm3 = Realm.getDefaultInstance();

            try {
                realm3.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm3) {

                        Clientes query2 = realm3.where(Clientes.class).equalTo("id", result1.get(i).getCustomer_id()).findFirst();
                        fantasyCliente = query2.getFantasyName();
                        idCliente = query2.getId();
                        Log.e("fantasyCliente", fantasyCliente);

                    }


                });

            } catch (Exception e) {
                Log.e("error", "error", e);
                //Toast.makeText(, "error", Toast.LENGTH_SHORT).show();
            }

            final Realm realm4 = Realm.getDefaultInstance();

            try {
                realm4.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm3) {

                        sale query3 = realm4.where(sale.class).equalTo("customer_id", idCliente).findFirst();

                        query3.setNombreCliente(fantasyCliente);
                        realm4.copyToRealmOrUpdate(query3);
                        Log.d("invProdNombre", query3.getNombreCliente());
                    }


                });

            } catch (Exception e) {
                Log.e("error", "error", e);
               // Toast.makeText(QuickContext, "error", Toast.LENGTH_SHORT).show();
            }

        }
        Log.d("SALE", result1+"");
        return result1;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // realm.close();
    }

    @Override
    public void updateData() {
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
        final List<sale> filteredModelList = filter(getListClientes(), newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<sale> filter(List<sale> models, String query) {
        query = query.toLowerCase();

        final List<sale> filteredModelList = new ArrayList<>();
        for (sale model : models) {
            final String text = model.getNombreCliente().toLowerCase();
            Log.d("FiltroPreventa", text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

}
