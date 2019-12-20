package com.friendlypos.preventas.fragment;

import android.app.Activity;
import androidx.core.view.MenuItemCompat;

import android.os.Bundle;
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
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.adapters.PrevResumenAdapter;
import com.friendlypos.preventas.adapters.PrevSeleccionarProductoAdapter;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.preventas.util.TotalizeHelperPreventa;
import com.friendlypos.principal.modelo.Productos;

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
    int datosEnFiltro=0;

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

    private List<Productos> getListProductos() {
        realm = Realm.getDefaultInstance();
        RealmQuery<Productos> query = realm.where(Productos.class).equalTo("status", "Activo");
        RealmResults<Productos> result1 = query.findAll();

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

        if(datosEnFiltro == 1){
            Log.d("OSCARUpdate", "No actualiza xq esta en " + datosEnFiltro);
        }
        else{
            datosEnFiltro = 0;
            adapter.updateData(getListProductos());
            adapter2.notifyDataSetChanged();
            Log.d("OSCARUpdate1", "Actualiza xq esta en " + datosEnFiltro);
        }


        if (slecTAB == 1) {
            creditoLimiteCliente = Double.parseDouble(((PreventaActivity) getActivity()).getCreditoLimiteClientePreventa());
            creditoLimite.setText("C.Disponible: " + String.format("%,.2f", creditoLimiteCliente));
        }
        else{
            Log.d("SelecUpdate", "No hay productos");
        }
      //  adapter.updateData(getListProductos());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Productos> filteredModelList = filter(getListProductos(), newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<Productos> filter(List<Productos> models, String query) {

        if(query.isEmpty()){
            Log.d("OSCARVAC", "esta vacio la consulta");
            datosEnFiltro = 0;

        }else{

            datosEnFiltro = 1;

            Log.d("OSCARLLE", "esta llena la consulta");
        }

        query = query.toLowerCase();

        Log.d("listaProductoFiltro", getListProductos() + "");
        final List<Productos> filteredModelList = new ArrayList<>();
        for (Productos model : models) {
            final String text = model.getDescription().toLowerCase();
            Log.d("FiltroPreventa", text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


}
