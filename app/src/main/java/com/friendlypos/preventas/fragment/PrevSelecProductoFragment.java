package com.friendlypos.preventas.fragment;

import android.app.Activity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.adapters.PrevResumenAdapter;
import com.friendlypos.preventas.adapters.PrevSeleccionarProductoAdapter;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.preventas.util.TotalizeHelperPreventa;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class PrevSelecProductoFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private Realm realm;
    RecyclerView recyclerView;
    private PrevSeleccionarProductoAdapter adapter;
    private PrevResumenAdapter adapter2;
    private static int bill_type = 1;
    static TextView creditoLimite;
    static double creditoLimiteCliente = 0.0;
    int slecTAB;
    PreventaActivity activity;
    TotalizeHelperPreventa totalizeHelper;

    public static PrevSelecProductoFragment getInstance() {

        return new PrevSelecProductoFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (PreventaActivity) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;

    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_prev_selecproducto, container,
                false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewPrevSeleccProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if(adapter == null) {
            adapter = new PrevSeleccionarProductoAdapter(activity, this, getListProductos());
        }
        recyclerView.setAdapter(adapter);

        creditoLimite = (TextView) rootView.findViewById(R.id.restCreditPreventa);

        Log.d("listaProducto", getListProductos() + "");
        adapter2 = new PrevResumenAdapter();
        creditoDisponible();

        return rootView;

    }



    private List<Inventario> getListProductos() {
        realm = Realm.getDefaultInstance();
        RealmQuery<Inventario> query = realm.where(Inventario.class)/*.notEqualTo("amount", "0").notEqualTo("amount", "0.0").notEqualTo("amount", "0.000")*/;
        RealmResults<Inventario> result1 = query.findAll();

        return result1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    public void creditoDisponible() {
        slecTAB = activity.getSelecClienteTabPreventa();

        if (slecTAB == 1){
            // creditoLimiteCliente = Double.parseDouble(((DistribucionActivity) getActivity()).getCreditoLimiteClienteSlecc());
            //    creditoLimiteCliente = 0.0;
            final invoiceDetallePreventa invoiceDetallePreventa = activity.getCurrentInvoice();

            //String metodoPagoCliente = invoiceDetallePreventa.getP_payment_method_id();


            String metodoPagoCliente  = activity.getMetodoPagoClientePreventa();

            String limite = activity.getCreditoLimiteClientePreventa();
            // creditoLimiteCliente = Double.parseDouble(limite);
            String dueCliente = activity.getDueClientePreventa();

            Log.d("PagoProductoSelec", metodoPagoCliente + "");
            Log.d("PagoProductoSelec", creditoLimiteCliente + "");
            Log.d("PagoProductoSelec", dueCliente + "");

            if (metodoPagoCliente.equals("1")) {
                bill_type = 1;
                creditoLimite.setVisibility(View.GONE);
            }
            else if (metodoPagoCliente.equals("2")) {
                bill_type = 2;
                try {
                    creditoLimite.setVisibility(View.VISIBLE);
                    creditoLimite.setText("C.Disponible: " + String.format("%,.2f", creditoLimiteCliente));
                }
                catch (Exception e) {
                    Log.d("JD", "Error " + e.getMessage());
                }
            }}
        else{
            Log.d("Selec", "No hay productos");
        }

    }

    @Override
    public void updateData() {
        adapter.updateData(getListProductos());
        adapter2.notifyDataSetChanged();
        if (slecTAB == 1) {
            creditoLimiteCliente = Double.parseDouble(((PreventaActivity) getActivity()).getCreditoLimiteClientePreventa());
            creditoLimite.setText("C.Disponible: " + String.format("%,.2f", creditoLimiteCliente));
        }
        else{
            Log.d("SelecUpdate", "No hay productos");
        }
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
                        adapter.setFilter(getListProductos());
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
        final List<Inventario> filteredModelList = filter(getListProductos(), newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<Inventario> filter(List<Inventario> models, String query) {
        query = query.toLowerCase();

        Log.d("listaProductoFiltro", getListProductos() + "");
        final List<Inventario> filteredModelList = new ArrayList<>();
        for (Inventario model : models) {
            final String text = model.getNombre_producto().toLowerCase();
            Log.d("FiltroPreventa", text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


}
