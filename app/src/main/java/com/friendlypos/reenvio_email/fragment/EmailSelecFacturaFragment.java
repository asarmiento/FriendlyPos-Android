package com.friendlypos.reenvio_email.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.reenvio_email.activity.EmailActivity;
import com.friendlypos.reenvio_email.adapters.EmailFacturasAdapter;
import com.friendlypos.reenvio_email.modelo.invoices;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class EmailSelecFacturaFragment extends BaseFragment{

    private Realm realm;
    @Bind(R.id.recyclerViewEmailFactura)
    public RecyclerView recyclerView;

    private EmailFacturasAdapter adapter;

    int slecTAB;
    EmailActivity activity;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_email_selec_factura, container,
                false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //List<invoices> list = ;
        adapter = new EmailFacturasAdapter(activity, this, getListClientes());
            //adapter2 = new DistrResumenAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Log.d("listaProducto", getListClientes() + "");
    }

    private List<invoices> getListClientes(){

        realm = Realm.getDefaultInstance();
        RealmQuery<invoices> query = realm.where(invoices.class);
        RealmResults<invoices> result1 = query.findAll();
        if(result1.size() == 0){
            Toast.makeText(getApplicationContext(),"Favor descargar datos primero",Toast.LENGTH_LONG).show();
        }
        return result1;

    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = (EmailActivity) activity;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activity = null;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // realm.close();
    }

    @Override
    public void updateData() {
        slecTAB = activity.getSelecClienteTabEmail();
        if (slecTAB == 1) {
           // List<invoices> list = ;

            adapter.updateData(getListClientes());
        }else {
            Toast.makeText(getActivity(), "nadaresumenUpdate", Toast.LENGTH_LONG).show();
        }
    }
}

