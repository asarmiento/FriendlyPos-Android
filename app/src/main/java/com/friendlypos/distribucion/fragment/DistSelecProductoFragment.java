package com.friendlypos.distribucion.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.adapters.DistrSeleccionarProductosAdapter;
import com.friendlypos.distribucion.modelo.Inventario;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class DistSelecProductoFragment extends BaseFragment {
    private Realm realm;
    RecyclerView recyclerView;
    private DistrSeleccionarProductosAdapter adapter;
    private DistrResumenAdapter adapter2;
    private static int bill_type = 1;
    static TextView creditoLimite;
    static double creditoLimiteCliente = 0.0;

    public static DistSelecProductoFragment getInstance() {
        return new DistSelecProductoFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter2.clearAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter2.clearAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_distribucion_selecproduct, container,
            false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewDistrSeleccProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new DistrSeleccionarProductosAdapter(((DistribucionActivity) getActivity()), this, getListProductos());
        recyclerView.setAdapter(adapter);
        creditoLimite = (TextView) rootView.findViewById(R.id.restCredit);

        Log.d("listaProducto", getListProductos() + "");

        adapter2 = new DistrResumenAdapter();
        adapter2.clearAll();

        creditoDisponible();

        return rootView;

    }

    private List<Inventario> getListProductos() {
        realm = Realm.getDefaultInstance();
        RealmQuery<Inventario> query = realm.where(Inventario.class);
        RealmResults<Inventario> result1 = query.findAll();

        return result1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter2.clearAll();
        realm.close();
    }

    public void creditoDisponible() {

        // creditoLimiteCliente = Double.parseDouble(((DistribucionActivity) getActivity()).getCreditoLimiteClienteSlecc());

        String metodoPagoCliente = ((DistribucionActivity) getActivity()).getMetodoPagoCliente();
        creditoLimiteCliente = Double.parseDouble(((DistribucionActivity) getActivity()).getCreditoLimiteClienteSlecc());
        String dueCliente = ((DistribucionActivity) getActivity()).getDueCliente();

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
        }

    }

    @Override
    public void updateData() {
        adapter.updateData(getListProductos());
        creditoLimiteCliente = Double.parseDouble(((DistribucionActivity) getActivity()).getCreditoLimiteClienteSlecc());
        creditoLimite.setText("C.Disponible: " + String.format("%,.2f", creditoLimiteCliente));

    }


}
