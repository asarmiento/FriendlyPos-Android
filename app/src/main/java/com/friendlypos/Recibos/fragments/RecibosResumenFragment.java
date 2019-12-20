package com.friendlypos.Recibos.fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.adapters.RecibosResumenAdapter;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.distribucion.fragment.BaseFragment;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class RecibosResumenFragment extends BaseFragment {

    RecyclerView recyclerView;
    private RecibosResumenAdapter adapter;
    int slecTAB;

    //TotalizeHelperPreventa totalizeHelper;

    RecibosActivity activity;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (RecibosActivity) activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       /* FragmentTransaction ftr = getFragmentManager().beginTransaction();
        ftr.detach(DistResumenFragment.this).attach(DistResumenFragment.this).commit();
*/
        View rootView = inflater.inflate(R.layout.fragment_recibos_resumen, container,
                false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewRecibosResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<recibos> list = getListResumen();

        adapter = new RecibosResumenAdapter(activity, this, list);
        recyclerView.setAdapter(adapter);

        /*activity.cleanTotalize();
        totalizeHelper = new TotalizeHelper(activity);
        totalizeHelper.totalize(list);
        Log.d("listaResumen", list + "");*/

        return rootView;


   /*     recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewPreventaResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        slecTAB = activity.getSelecClienteTabPreventa();

        totalizeHelper = new TotalizeHelperPreventa(activity);
        if (slecTAB == 1) {
           // List<Pivot> list = activity.getAllPivotDelegate();
            adapter = new RecibosResumenAdapter(activity, this,  list);
            recyclerView.setAdapter(adapter);


            activity.cleanTotalize();

            totalizeHelper.totalize(list);
            Log.d("listaResumen",  list + "");
        }
        return rootView;*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //totalizeHelper.destroy();
    }



    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;
    }

    private List<recibos> getListResumen() {
        String clienteId = activity.getClienteIdRecibos();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<recibos> result1 = realm.where(recibos.class).equalTo("customer_id", clienteId).equalTo("abonado", 1).equalTo("mostrar",1).findAll();
        realm.close();

        return result1;

    }

    @Override
    public void updateData() {
        slecTAB = activity.getSelecClienteTabRecibos();
        if (slecTAB == 1) {
            activity.cleanTotalize();
            List<recibos> list = getListResumen();

            adapter.updateData(list);
           // totalizeHelper.totalize(list);
        }
        else {
            Log.d("SelecUpdateResumen", "No hay productos");
        }
    }
}

