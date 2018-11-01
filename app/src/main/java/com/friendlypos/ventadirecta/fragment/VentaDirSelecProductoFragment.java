package com.friendlypos.ventadirecta.fragment;

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
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.preventas.util.TotalizeHelperPreventa;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
import com.friendlypos.ventadirecta.adapters.VentaDirSeleccionarProductoAdapter;
import com.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class VentaDirSelecProductoFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private Realm realm;
    RecyclerView recyclerView;
    private VentaDirSeleccionarProductoAdapter adapter;

    private static int bill_type = 1;
    static TextView creditoLimite;
    static double creditoLimiteCliente = 0.0;
    int slecTAB;
    VentaDirectaActivity activity;
    TotalizeHelperPreventa totalizeHelper;
    int datosEnFiltro=0;
    List<Inventario> listaInventario;

    public static VentaDirSelecProductoFragment getInstance() {
        return new VentaDirSelecProductoFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (VentaDirectaActivity) activity;
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

        View rootView = inflater.inflate(R.layout.fragment_ventadir_selecproducto, container,
                false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewVentaDirectaSeleccProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if(adapter == null) {
            adapter = new VentaDirSeleccionarProductoAdapter(activity, this, getListProductos());
        }

        recyclerView.setAdapter(adapter);

        creditoLimite = (TextView) rootView.findViewById(R.id.restCreditVentaDirecta);
        listaInventario = getListProductos();
        Log.d("listaProducto", getListProductos() + "");
        creditoDisponible();

        return rootView;
    }



    private List<Inventario> getListProductos() {
        realm = Realm.getDefaultInstance();
        RealmQuery<Inventario> query = realm.where(Inventario.class).notEqualTo("amount", "0").notEqualTo("amount", "0.0").notEqualTo("amount", "0.000");
        RealmResults<Inventario> result1 = query.findAll();

        if (result1.isEmpty()) {

            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();}

        else{
            for (int i = 0; i < result1.size(); i++) {

                List<Inventario> salesList1 = realm.where(Inventario.class).notEqualTo("amount", "0").notEqualTo("amount", "0.0").notEqualTo("amount", "0.000").findAll();
                String nombre = salesList1.get(i).getNombre_producto();
                if (nombre == null){
                    String facturaId1 = salesList1.get(i).getProduct_id();

                    Productos salesList2 = realm.where(Productos.class).equalTo("id", facturaId1).findFirst();

                    final String facturaId2 = salesList2.getId();
                    final String desc = salesList2.getDescription();

                    final Realm realm3 = Realm.getDefaultInstance();

                    realm3.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm bgRealm) {
                            Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("product_id", facturaId2).findFirst();
                            //  inv_actualizado.setProducto(new RealmList<Productos>(salesList2.toArray(new Productos[salesList2.size()])));
                            inv_actualizado.setNombre_producto(desc);
                            realm3.insertOrUpdate(inv_actualizado); // using insert API
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            realm3.close();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            realm3.close();
                        }
                    });
                }

            }

        }

        return result1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }

    public void creditoDisponible() {
        slecTAB = activity.getSelecClienteTabVentaDirecta();

        if (slecTAB == 1){
            // creditoLimiteCliente = Double.parseDouble(((DistribucionActivity) getActivity()).getCreditoLimiteClienteSlecc());
            //    creditoLimiteCliente = 0.0;
            final invoiceDetalleVentaDirecta invoiceDetallePreventa = activity.getCurrentInvoice();

            //String metodoPagoCliente = invoiceDetallePreventa.getP_payment_method_id();


            String metodoPagoCliente  = activity.getMetodoPagoClienteVentaDirecta();

            String limite = activity.getCreditoLimiteClienteVentaDirecta();
            // creditoLimiteCliente = Double.parseDouble(limite);
            String dueCliente = activity.getDueClienteVentaDirecta();

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
            Log.d("OSCARUpdate1", "Actualiza xq esta en " + datosEnFiltro);
        }

        if (slecTAB == 1) {
            creditoLimiteCliente = Double.parseDouble(((VentaDirectaActivity) getActivity()).getCreditoLimiteClienteVentaDirecta());
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
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Inventario> filteredModelList = filter(listaInventario, newText);
        adapter.setFilter(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private List<Inventario> filter(List<Inventario> models, String query) {


        if(query.isEmpty()){
            Log.d("OSCARVAC", "esta vacio la consulta");
            datosEnFiltro = 0;

        }else{

            datosEnFiltro = 1;

            Log.d("OSCARLLE", "esta llena la consulta");
        }

        query = query.toLowerCase();
        final List<Inventario> filteredModelList = new ArrayList<>();

        for (Inventario model : models) {

            if(model.getNombre_producto() == null){

            }else{

            String text = model.getNombre_producto().toLowerCase();
                Log.d("FiltroVentaDirecta", text);
                if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        }
        return filteredModelList;
    }


}
