package com.friendlypos.reimpresion_pedidos.fragment;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.util.TotalizeHelper;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity;
import com.friendlypos.reimpresion_pedidos.adapters.ReimPedidoSeleccionarProductosAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ReimPedidoSelecProductoFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private Realm realm;
    RecyclerView recyclerView;
    private ReimPedidoSeleccionarProductosAdapter adapter;
    private DistrResumenAdapter adapter2;
    private static int bill_type = 1;
    static TextView creditoLimite;
    static double creditoLimiteCliente = 0.0;
    int slecTAB;
    ReimprimirPedidosActivity activity;
    TotalizeHelper totalizeHelper;

    public static ReimPedidoSelecProductoFragment getInstance() {
        return new ReimPedidoSelecProductoFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (ReimprimirPedidosActivity) activity;
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
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_reim_pedido_selec_producto, container,
                false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewReimPedidoSeleccProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new ReimPedidoSeleccionarProductosAdapter(activity, this, getListProductos());
        recyclerView.setAdapter(adapter);
        creditoLimite = (TextView) rootView.findViewById(R.id.restCredit);

        Log.d("listaProducto", getListProductos() + "");
        adapter2 = new DistrResumenAdapter();
        creditoDisponible();

        return rootView;

    }


    private List<Productos> getListProductos() {
        realm = Realm.getDefaultInstance();
        RealmQuery<Productos> query = realm.where(Productos.class);
        RealmResults<Productos> result1 = query.findAll();

        return result1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    public void creditoDisponible() {
        slecTAB = activity.getSelecClienteTab();

        if (slecTAB == 1){
            // creditoLimiteCliente = Double.parseDouble(((ReimprimirPedidosActivity) getActivity()).getCreditoLimiteClienteSlecc());
            //    creditoLimiteCliente = 0.0;
            String metodoPagoCliente = activity.getMetodoPagoCliente();
            String limite = activity.getCreditoLimiteCliente();
            // creditoLimiteCliente = Double.parseDouble(limite);
            String dueCliente = activity.getDueCliente();

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
            Toast.makeText(getActivity(),"nadaSelecProducto",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void updateData() {
        adapter.updateData(getListProductos());
        adapter2.notifyDataSetChanged();
       /* totalizeHelper = new TotalizeHelper(activity);
        totalizeHelper.totalize(resumenFrag1.getListResumen());*/

        if (slecTAB == 1) {
            creditoLimiteCliente = Double.parseDouble(((ReimprimirPedidosActivity) getActivity()).getCreditoLimiteCliente());
            creditoLimite.setText("C.Disponible: " + String.format("%,.2f", creditoLimiteCliente));
        }
        else{
            Toast.makeText(getActivity(),"nadaSelecProducto",Toast.LENGTH_LONG).show();
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
        final List<Productos> filteredModelList = filter(getListProductos(), newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<Productos> filter(List<Productos> models, String query) {
        query = query.toLowerCase();

        final List<Productos> filteredModelList = new ArrayList<>();
        for (Productos model : models) {
            final String text = model.getDescription().toLowerCase();
            Log.d("dasdad", text);
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


}
