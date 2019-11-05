package com.friendlypos.preventas.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.adapters.PrevResumenAdapter;
import com.friendlypos.preventas.util.TotalizeHelperPreventa;

import java.util.List;


public class PrevResumenFragment extends BaseFragment {

    RecyclerView recyclerView;
    private PrevResumenAdapter adapter;
    int slecTAB;

    TotalizeHelperPreventa totalizeHelper;

    PreventaActivity activity;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       /* FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.detach(DistResumenFragment.this).attach(DistResumenFragment.this).commit();
*/
        View rootView = inflater.inflate(R.layout.fragment_prev_resumen, container,
            false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewPreventaResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        slecTAB = activity.getSelecClienteTabPreventa();

        totalizeHelper = new TotalizeHelperPreventa(activity);
        if (slecTAB == 1) {
            List<Pivot> list = activity.getAllPivotDelegate();
        adapter = new PrevResumenAdapter(activity, this,  list);
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
        this.activity = (PreventaActivity) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;
    }


    @Override
    public void updateData() {
        slecTAB = activity.getSelecClienteTabPreventa();
        if (slecTAB == 1) {
            activity.cleanTotalize();
          List<Pivot> list = activity.getAllPivotDelegate();

            adapter.updateData(list);
            totalizeHelper.totalize(list);
        }
        else {
            Log.d("SelecUpdateResumen", "No hay productos");
        }
    }
}
