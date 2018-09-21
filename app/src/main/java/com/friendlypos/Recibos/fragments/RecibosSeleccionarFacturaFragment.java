package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.adapters.RecibosResumenAdapter;
import com.friendlypos.Recibos.adapters.RecibosSeleccionarFacturaAdapter;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.Recibos.util.TotalizeHelperRecibos;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.fragment.BaseFragment;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RecibosSeleccionarFacturaFragment extends BaseFragment {
    private Realm realm;
    RecyclerView recyclerView;
    private RecibosSeleccionarFacturaAdapter adapter;
    TotalizeHelperRecibos totalizeHelper;
    private static Button applyBill;
    int slecTAB;
    RecibosActivity activity;
    TextView txtPagoTotal, txtPagoCancelado;
    public static RecibosSeleccionarFacturaFragment getInstance() {
        return new RecibosSeleccionarFacturaFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (RecibosActivity) activity;
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

        View rootView = inflater.inflate(R.layout.fragment_recibos_seleccionar_factura, container,
                false);
        setHasOptionsMenu(true);

        txtPagoTotal = (TextView) rootView.findViewById(R.id.txtPagoTotal);
        txtPagoCancelado  = (TextView) rootView.findViewById(R.id.txtPagoCancelado);
        totalizeHelper = new TotalizeHelperRecibos(activity);
        applyBill = (Button) rootView.findViewById(R.id.btnPagoTotal);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewRecibosSeleccFactura);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        slecTAB = activity.getSelecClienteTabRecibos();
        if(adapter == null) {
            adapter = new RecibosSeleccionarFacturaAdapter(activity, this, getListProductos());

        }
        if (slecTAB == 1) {
            List<Recibos> list = getListProductos();
            activity.cleanTotalize();
            totalizeHelper.totalizeRecibos(list);
            Log.d("listaResumen",  list + "");
        }

        recyclerView.setAdapter(adapter);

        applyBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            //double totalT = activity.getTotalizarTotal();
                           double totalP = activity.getTotalizarCancelado();
                            Log.d("totalRecibos", "" + totalP);

                            String clienteId = activity.getClienteIdRecibos();

                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<Recibos> result = realm.where(Recibos.class).equalTo("customer_id", clienteId).findAll();

                            if (result.isEmpty()) {
                                Toast.makeText(getActivity(), "No hay recibos emitidos", Toast.LENGTH_LONG).show();
                            }
                            else {
                                for (int i = 0; i < result.size(); i++) {

                                    List<Recibos> salesList1 = realm.where(Recibos.class).equalTo("customer_id", clienteId).findAll();
                                    double totalFactura = salesList1.get(i).getTotal();
                                    double totalPagado = salesList1.get(i).getPaid();
                                    Log.d("totalFactura", "" + totalFactura);
                                    Log.d("totalPagado", "" + totalFactura);

                                    double restante = totalFactura - totalPagado;

                                    Log.d("restante", "" + restante);

                                    double irPagando = totalP - restante;
                                    Log.d("irPagando", "" + irPagando);

                                }
                            }

                            realm.close();
                        }
                        catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                });

        return rootView;

    }

    private List<Recibos> getListProductos() {
        String clienteId = activity.getClienteIdRecibos();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Recibos> result1 = realm.where(Recibos.class).equalTo("customer_id", clienteId).findAll();
        realm.close();

        return result1;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // realm.close();
    }


    @Override
    public void updateData() {
        slecTAB = activity.getSelecClienteTabRecibos();
        if (slecTAB == 1) {
            activity.cleanTotalize();
            List<Recibos> list = getListProductos();

            adapter.updateData(list);
           // adapter2.notifyDataSetChanged();
            totalizeHelper.totalizeRecibos(list);

            double totalT = activity.getTotalizarTotal();
            double totalP = activity.getTotalizarCancelado();

            txtPagoTotal.setText("Total de todas: " +  String.format("%,.2f", totalT));
            txtPagoCancelado.setText("Total por pagar: " +  String.format("%,.2f", totalP));
            Log.d("totalFull", totalT + "");
        }
        else {
            Log.d("SelecUpdateResumen", "No hay productos");
        }
    }

}
