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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.adapters.RecibosSeleccionarFacturaAdapter;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.Recibos.util.TotalizeHelperRecibos;
import com.friendlypos.distribucion.fragment.BaseFragment;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RecibosSeleccionarFacturaFragment extends BaseFragment {
    private Realm realm;
    RecyclerView recyclerView;
    private RecibosSeleccionarFacturaAdapter adapter;
    TotalizeHelperRecibos totalizeHelper;
    private static Button applyBill;
    int slecTAB;
    RecibosActivity activity;
    TextView txtPagoTotal, txtPagoCancelado;
    double debePagar = 0.0;
StringBuffer sb= null;
    double cantidadPagar;

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
            List<recibos> list = getListProductos();
            activity.cleanTotalize();
            totalizeHelper.totalizeRecibos(list);
            Log.d("listaResumen", list + "");
        }

        recyclerView.setAdapter(adapter);

        applyBill.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {


                        sb= new StringBuffer();

                        for(recibos r : adapter.checked){

                            sb.append(r.getNumeration());

                            double total = r.getTotal();
                            double pago = r.getPaid();
                            debePagar = total - pago;
                         activity.setTotalizarTotalCheck(debePagar);

                        }

                        if(adapter.checked.size()>0){
                            final double totalCheck = activity.getTotalizarTotalCheck();

                            Toast.makeText(activity ,totalCheck + "", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(activity ,"no hay", Toast.LENGTH_LONG).show();
                        }

                        //double totalT = activity.getTotalizarTotal();
                        final double totalP = activity.getTotalizarCancelado();
                        Log.d("totalRecibos", "" + totalP);

                        LayoutInflater layoutInflater = LayoutInflater.from(activity);
                        View promptView = layoutInflater.inflate(R.layout.promptrecibospagototal, null);

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setView(promptView);

                        final TextView label = (TextView) promptView.findViewById(R.id.promtClabelRecibosPagoTotal);
                        label.setText("Escriba un pago maximo de " + String.format("%,.2f", totalP) + " minima de 1");

                        final EditText input = (EditText) promptView.findViewById(R.id.promtCtextRecibosPagoTotal);

                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                double numIngresado = Double.parseDouble(input.getText().toString());



                                if(!input.getText().toString().isEmpty()){

                                    if(numIngresado >= totalP){
                                        Toast.makeText(getActivity(), "Ingrese una cantidad menor al total", Toast.LENGTH_LONG).show();
                                    }else{
                                    cantidadPagar = numIngresado;
                                    Log.d("cantidadPagar1", "" + String.format("%,.2f", cantidadPagar));
                                    AlertDialog dialogReturnSale = new AlertDialog.Builder(activity)
                                            .setTitle("Pago total")
                                            .setMessage("¿Desea proceder con el pago de las facturas?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    try {

                                                        String clienteId = activity.getClienteIdRecibos();
                                                        Realm realm = Realm.getDefaultInstance();
                                                        RealmResults<recibos> result = realm.where(recibos.class).equalTo("customer_id", clienteId).findAllSorted("date", Sort.DESCENDING);

                                                        if (result.isEmpty()) {
                                                            Toast.makeText(getActivity(), "No hay recibos emitidos", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            for (int i = 0; i < result.size(); i++) {
                                                                Log.d("cantidadPagarfor", "" + String.format("%,.2f", cantidadPagar));
                                                                List<recibos> salesList1 = realm.where(recibos.class).equalTo("customer_id", clienteId).findAllSorted("date", Sort.DESCENDING);
                                                                double totalFactura = salesList1.get(i).getTotal();
                                                                double totalPagado = salesList1.get(i).getPaid();
                                                                final String facturaId = salesList1.get(i).getInvoice_id();
                                                                Log.d("totalFactura", "" + totalFactura);
                                                                Log.d("totalPagado", "" + totalPagado);


                                                                if(totalFactura == totalPagado){
                                                                    Log.d("ya", "" + "ya");
                                                                }
                                                        else {
                                                                    double restante = totalFactura - totalPagado;

                                                                    Log.d("restante", "" + String.format("%,.2f", restante));

                                                                    double cantidadPagarRestante = 0.0;
                                                                    if (cantidadPagar > restante) {

                                                                        cantidadPagarRestante = cantidadPagar - restante;
                                                                        cantidadPagar = cantidadPagarRestante;

                                                                        Log.d("cantidadPagar2", "" + String.format("%,.2f", cantidadPagar));


                                                                        //  double cantidadPagarRestanteS = activity.getMontoAgregadoRestante();
                                                                        final double irPagando = restante + totalPagado;

                                                                        final Realm realm2 = Realm.getDefaultInstance();
                                                                        realm2.executeTransaction(new Realm.Transaction() {

                                                                            @Override
                                                                            public void execute(Realm realm2) {
                                                                                recibos recibo_actualizado = realm2.where(recibos.class).equalTo("invoice_id", facturaId).findFirst();

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


                                                                    } else {

                                                                        if(cantidadPagar < 0.0){
                                                                            Log.d("nohay","no hay");
                                                                        }
                                                                        else{
                                                                        //  double cantidadPagarRestanteS = activity.getMontoAgregadoRestante();
                                                                        final double irPagando = cantidadPagar + totalPagado;

                                                                        final Realm realm2 = Realm.getDefaultInstance();
                                                                        realm2.executeTransaction(new Realm.Transaction() {

                                                                            @Override
                                                                            public void execute(Realm realm2) {
                                                                                recibos recibo_actualizado = realm2.where(recibos.class).equalTo("invoice_id", facturaId).findFirst();

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
                                                                        cantidadPagarRestante = cantidadPagar - restante;
                                                                        cantidadPagar = cantidadPagarRestante;
                                                                        updateData();

                                                                        Log.d("irPagando", "" + String.format("%,.2f", irPagando));

                                                                    }
                                                                    }
                                                                }
                                                        }

                                                        realm.close();
                                                        Toast.makeText(activity, "Se realizó el pago total", Toast.LENGTH_LONG).show();
                                                        }
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
                                }
                                else{
                                    input.setError("Campo requerido");
                                    input.requestFocus();
                                }

                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        activity.cleanTotalizeCkeck();
                                    }
                                });

                        AlertDialog alertD = alertDialogBuilder.create();
                        alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        alertD.show();
                    }
                });
        return rootView;

    }


    private List<recibos> getListProductos() {
        String clienteId = activity.getClienteIdRecibos();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<recibos> result1 = realm.where(recibos.class).equalTo("customer_id", clienteId).findAllSorted("date", Sort.DESCENDING);
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
            List<recibos> list = getListProductos();

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
