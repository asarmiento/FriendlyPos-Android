package com.friendlypos.ventadirecta.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
import com.friendlypos.ventadirecta.adapters.VentaDirResumenAdapter;
import com.friendlypos.ventadirecta.util.TotalizeHelperVentaDirecta;
import java.util.List;


public class VentaDirResumenFragment extends BaseFragment {

    RecyclerView recyclerView;
    private VentaDirResumenAdapter adapter;
    int slecTAB;

    TotalizeHelperVentaDirecta totalizeHelper;

    VentaDirectaActivity activity;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ventadir_resumen, container,
                false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewVentaDirectaResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        slecTAB = activity.getSelecClienteTabVentaDirecta();

        totalizeHelper = new TotalizeHelperVentaDirecta(activity);

        if (slecTAB == 1) {
            List<Pivot> list = activity.getAllPivotDelegate();
            adapter = new VentaDirResumenAdapter(activity, this,  list);
            recyclerView.setAdapter(adapter);

            activity.cleanTotalize();

            totalizeHelper.totalize(list);
            Log.d("listaResumen",  list + "");
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        totalizeHelper.destroy();
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
    public void updateData() {
        slecTAB = activity.getSelecClienteTabVentaDirecta();
        if (slecTAB == 1) {
            activity.cleanTotalize();
            List<Pivot> list = activity.getAllPivotDelegate();

            adapter.updateData(list);
            totalizeHelper.totalize(list);
        }
        else {
            Log.d("SelecUpdateResumenVD", "No hay productos");
        }
    }
}
