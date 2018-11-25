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
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

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
        Log.d("listaProducto", getListClientes() + "");
    }

    private List<sale> getListClientes(){

        realm = Realm.getDefaultInstance();
        final RealmQuery<sale> query = realm.where(sale.class).equalTo("aplicada", 0);
        final RealmResults<sale> result1 = query.findAll();


        if (result1.isEmpty()) {

            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();
        }

   /*     else {
            for (int i = 0; i < result1.size(); i++) {

                List<sale> salesList1 = realm.where(sale.class).equalTo("aplicada", 0).findAll();
                String nombre = salesList1.get(i).getNombreCliente();
                if (nombre == null) {
                    String facturaId1 = salesList1.get(i).getCustomer_id();

                    Clientes salesList2 = realm.where(Clientes.class).equalTo("id", facturaId1).findFirst();

                    final String facturaId2 = salesList2.getId();
                    final String desc = salesList2.getFantasyName();

                    final Realm realm3 = Realm.getDefaultInstance();

                    realm3.executeTransaction(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm3) {

                            sale inv_actualizado = realm3.where(sale.class).equalTo("customer_id", facturaId2).findFirst();
                            //  inv_actualizado.setProducto(new RealmList<Productos>(salesList2.toArray(new Productos[salesList2.size()])));
                            inv_actualizado.setNombreCliente(desc);
                            realm3.insertOrUpdate(inv_actualizado); // using insert API
                        }
                    });
                }
            }
        }*/
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

            if(model.getNombreCliente() == null){

            }else{

            final String text = model.getNombreCliente().toLowerCase();
            Log.d("FiltroPreventa", text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
            }
        }
        return filteredModelList;
    }

}
