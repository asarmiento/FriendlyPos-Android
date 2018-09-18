package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.Recibos.util.TotalizeHelperRecibos;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.login.modelo.Usuarios;

import io.realm.Realm;
import io.realm.RealmResults;


public class RecibosResumenFragment extends BaseFragment {

    TotalizeHelperRecibos totalizeHelper;
    int slecTAB;
    RecibosActivity activity;

    private static TextView txt_resumen_factura_TotalTodosRecibos;
    private static TextView txt_resumen_factura_totalUnaRecibos;
    Button btnPagarFacturaRecibos;
    private static EditText txtMontoPagar;

    double totalFacturaSelec = 0.0;
    double totalFacturaTodas = 0.0;
    double totalFacturaPagado = 0.0;
    double debePagar = 0.0;
    double montoFaltante = 0.0;

    public static RecibosResumenFragment getInstance() {
        return new RecibosResumenFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_recibos_resumen, container,
                false);

            txtMontoPagar = (EditText) rootView.findViewById(R.id.txtMontoPagar);
            btnPagarFacturaRecibos = (Button) rootView.findViewById(R.id.btnPagarFacturaRecibos);
            txt_resumen_factura_TotalTodosRecibos = (TextView) rootView.findViewById(R.id.txt_resumen_factura_TotalTodosRecibos);
            txt_resumen_factura_totalUnaRecibos = (TextView) rootView.findViewById(R.id.txt_resumen_factura_totalUnaRecibos);

            //totalizeHelper = new TotalizeHelperRecibos(activity);

        btnPagarFacturaRecibos.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {

                            String monto = txtMontoPagar.getText().toString();
                            double montoPagar = Double.parseDouble(monto);
                            final String facturaId = activity.getInvoiceIdRecibos();

                            if (montoPagar <= debePagar) {
                                Toast.makeText(getActivity(), "Pago " + montoPagar + " " + debePagar + " " +totalFacturaPagado, Toast.LENGTH_LONG).show();
                                montoFaltante = totalFacturaPagado + montoPagar;

                                final Realm realm2 = Realm.getDefaultInstance();
                                realm2.executeTransaction(new Realm.Transaction() {

                                    @Override
                                    public void execute(Realm realm2) {
                                        Recibos recibo_actualizado = realm2.where(Recibos.class).equalTo("invoice_id", facturaId).findFirst();

                                        recibo_actualizado.setPaid(montoFaltante);

                                        realm2.insertOrUpdate(recibo_actualizado);
                                        realm2.close();


                                        Log.d("ACT RECIBO", recibo_actualizado + "");

                                    }
                                });
                                activity.setTotalizarPagado(montoFaltante);
                                txtMontoPagar.setText(" ");

                            }else{
                                Toast.makeText(getActivity(), "El monto agregado es mayor al monto de la factura", Toast.LENGTH_LONG).show();
                            }


                        }
                        catch (Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                        }
                    }

                });

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // realm.close();
    }

    @Override
    public void updateData() {slecTAB = activity.getSelecClienteTabRecibos();
        if (slecTAB == 1) {

           // totalFacturaTodas = activity.getTotalizarTotal();
          //  Log.d("totalFacturaTodas",  totalFacturaTodas + "");


            totalFacturaSelec = activity.getTotalFacturaSelec();
            Log.d("totalFacturaSelec",  totalFacturaSelec + "");
            totalFacturaPagado = activity.getTotalizarPagado();
            Log.d("totalFacturaPagado",  totalFacturaPagado + "");

            debePagar = totalFacturaSelec - totalFacturaPagado;
            Log.d("debePagar",  debePagar + "");


            txt_resumen_factura_TotalTodosRecibos.setText("Total de la factura: " + String.format("%,.2f", totalFacturaSelec));
            txt_resumen_factura_totalUnaRecibos.setText("Total por pagar de esta factura: " + String.format("%,.2f", debePagar));

        }
        else {
            Log.d("nadaTotalizarupdate",  "nadaTotalizarupdate");
        }

    }


}
