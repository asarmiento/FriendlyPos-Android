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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.adapters.RecibosResumenAdapter;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.Recibos.util.TotalizeHelperRecibos;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.adapters.DistrResumenAdapter;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.util.TotalizeHelper;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.adapters.PrevResumenAdapter;
import com.friendlypos.preventas.util.TotalizeHelperPreventa;

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

        List<Recibos> list = getListResumen();

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

    private List<Recibos> getListResumen() {
        String clienteId = activity.getClienteIdRecibos();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Recibos> result1 = realm.where(Recibos.class).equalTo("customer_id", clienteId).equalTo("abonado", 1).findAll();
        realm.close();

        return result1;

    }

    @Override
    public void updateData() {
        slecTAB = activity.getSelecClienteTabRecibos();
        if (slecTAB == 1) {
            activity.cleanTotalize();
            List<Recibos> list = getListResumen();

            adapter.updateData(list);
           // totalizeHelper.totalize(list);
        }
        else {
            Log.d("SelecUpdateResumen", "No hay productos");
        }
    }
}

