package com.friendlypos.reimpresion_pedidos.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity;
import com.friendlypos.reimpresion_pedidos.adapters.ReimPedidoResumenAdapter;
import com.friendlypos.reimpresion_pedidos.util.TotalizeHelperReimPedido;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReimPedidoResumenFragment extends BaseFragment {

    RecyclerView recyclerView;
    private ReimPedidoResumenAdapter adapter;
    int slecTAB;

    TotalizeHelperReimPedido totalizeHelper;

    ReimprimirPedidosActivity activity;

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
        View rootView = inflater.inflate(R.layout.fragment_reim_pedido_resumen, container,
                false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewReimPedidoResumen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Pivot> list = getListResumen();

        adapter = new ReimPedidoResumenAdapter(activity, this, list);
        recyclerView.setAdapter(adapter);

        activity.cleanTotalize();
        totalizeHelper = new TotalizeHelperReimPedido(activity);
        totalizeHelper.totalize(list);
        Log.d("listaResumen", list + "");

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
        this.activity = (ReimprimirPedidosActivity) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;
    }


    public List<Pivot> getListResumen() {
        String facturaId = activity.getInvoiceId();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pivot> facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaId).equalTo("devuelvo", 0).findAll();
        realm.close();
        return facturaid1;
    }

    @Override
    public void updateData() {
        slecTAB = activity.getSelecClienteTab();
        if (slecTAB == 1) {
            activity.cleanTotalize();
            List<Pivot> list = getListResumen();

            adapter.updateData(list);
            totalizeHelper.totalize(list);
        }
        else {
            Toast.makeText(getActivity(), "nadaresumenUpdate", Toast.LENGTH_LONG).show();
        }
    }
}
