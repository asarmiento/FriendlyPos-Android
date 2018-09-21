package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;

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
        txtPagoCancelado = (TextView) rootView.findViewById(R.id.txtPagoCancelado);
        totalizeHelper = new TotalizeHelperRecibos(activity);
        applyBill = (Button) rootView.findViewById(R.id.btnPagoTotal);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewRecibosSeleccFactura);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        slecTAB = activity.getSelecClienteTabRecibos();
        if (adapter == null) {
            adapter = new RecibosSeleccionarFacturaAdapter(activity, this, getListProductos());

        }
        if (slecTAB == 1) {
            List<Recibos> list = getListProductos();
            activity.cleanTotalize();
            totalizeHelper.totalizeRecibos(list);
            Log.d("listaResumen", list + "");
        }

        recyclerView.setAdapter(adapter);

        applyBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        AlertDialog dialogReturnSale = new AlertDialog.Builder(activity)
                                .setTitle("Pago total")
                                .setMessage("¿Desea proceder con el pago total de las facturas?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {

                                            //double totalT = activity.getTotalizarTotal();
                                            double totalP = activity.getTotalizarCancelado();
                                            Log.d("totalRecibos", "" + totalP);

                                            String clienteId = activity.getClienteIdRecibos();
                                            Realm realm = Realm.getDefaultInstance();
                                            RealmResults<Recibos> result = realm.where(Recibos.class).equalTo("customer_id", clienteId).findAll();

                                            if (result.isEmpty()) {
                                                Toast.makeText(getActivity(), "No hay recibos emitidos", Toast.LENGTH_LONG).show();
                                            } else {
                                                for (int i = 0; i < result.size(); i++) {

                                                    List<Recibos> salesList1 = realm.where(Recibos.class).equalTo("customer_id", clienteId).findAll();
                                                    double totalFactura = salesList1.get(i).getTotal();
                                                    double totalPagado = salesList1.get(i).getPaid();
                                                    final String facturaId = salesList1.get(i).getInvoice_id();
                                                    Log.d("totalFactura", "" + totalFactura);
                                                    Log.d("totalPagado", "" + totalPagado);


                                                    double restante = totalFactura - totalPagado;

                                                    Log.d("restante", "" + String.format("%,.2f", restante));

                                                    final double irPagando = totalPagado + restante;

                                                    final Realm realm2 = Realm.getDefaultInstance();
                                                    realm2.executeTransaction(new Realm.Transaction() {

                                                        @Override
                                                        public void execute(Realm realm2) {
                                                            Recibos recibo_actualizado = realm2.where(Recibos.class).equalTo("invoice_id", facturaId).findFirst();

                                                            recibo_actualizado.setPaid(irPagando);
                                                            recibo_actualizado.setAbonado(1);
                                                            double cant = recibo_actualizado.getMontoCancelado();
                                                            if (cant == 0.0) {
                                                                recibo_actualizado.setMontoCancelado(irPagando);
                                                            } else {
                                                                recibo_actualizado.setMontoCancelado(cant + irPagando);
                                                            }

                                                            realm2.insertOrUpdate(recibo_actualizado);
                                                            realm2.close();

                                                            Log.d("NuevoPagando", recibo_actualizado + "");

                                                        }
                                                    });
                                                    updateData();
                                                    Log.d("irPagando", "" + String.format("%,.2f", irPagando));

                                                }

                                            }

                                            realm.close();
                                            Toast.makeText(activity, "Se realizó el pago total", Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            e.printStackTrace();

                                        }


                                    }

                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create();
                        dialogReturnSale.show();
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
