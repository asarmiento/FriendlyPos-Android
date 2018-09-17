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

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.util.TotalizeHelperRecibos;
import com.friendlypos.distribucion.fragment.BaseFragment;


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

            totalizeHelper = new TotalizeHelperRecibos(activity);


        return rootView;

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
            //  paid.getText().clear();
            totalFacturaTodas = activity.getTotalizarTotal();
            Log.d("totalFacturaTodas",  totalFacturaTodas + "");
            totalFacturaSelec = activity.getTotalFacturaSelec();
            Log.d("totalFacturaSelec",  totalFacturaSelec + "");
            totalFacturaPagado = activity.getTotalizarPagado();
            Log.d("totalFacturaPagado",  totalFacturaPagado + "");
            double debePagar = totalFacturaSelec - totalFacturaPagado;
            Log.d("debePagar",  debePagar + "");


            txt_resumen_factura_TotalTodosRecibos.setText("Total de todas las facturas: " + String.format("%,.2f", totalFacturaTodas));
            txt_resumen_factura_totalUnaRecibos.setText("Total por pagar de esta factura: " + String.format("%,.2f", debePagar));

        }
        else {
            Log.d("nadaTotalizarupdate",  "nadaTotalizarupdate");
        }

    }


}
