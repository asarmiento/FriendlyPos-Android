package com.friendlypos.reimprimirRecibos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reimpresion.activity.ReimprimirActivity;
import com.friendlypos.reimprimirRecibos.activity.ReimprimirRecibosActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Delvo on 03/12/2017.
 */

public class ReimprimirReciboFacturaAdapter extends RecyclerView.Adapter<ReimprimirReciboFacturaAdapter.CharacterViewHolder> {

    public List<receipts> contentList;
    private ReimprimirRecibosActivity activity;
    private int selected_position = -1;
    private static Context QuickContext = null;
    String facturaID;
    int tabCliente;

    public ReimprimirReciboFacturaAdapter(Context context, ReimprimirRecibosActivity activity, List<receipts> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_reimprimir_recibos_facturas, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, final int position) {

        final receipts sale = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();

        long cantidadPivot = 0;

      /*  Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        final invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();

        cantidadPivot = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).equalTo("devuelvo", 0).count();

        RealmQuery<Pivot> query1 = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id());
        RealmResults<Pivot> result1 = query1.findAll();
        Log.d("pivot", result1 + "");*/

        String numeracionFactura = sale.getNumeration();
       /* String fantasyCliente = clientes.getFantasyName();
        String fecha1 = invoice.getDate();
        String hora1 = invoice.getTimes();
        int subida = invoice.getSubida();
        String aNombreDe = sale.getCustomer_name();*/
        double totalFactura = sale.getMontoPagado();

        holder.txt_resumen_numeracionRecibosFactura.setText(numeracionFactura);
        holder.txt_resumen_abonoRecibosFactura.setText(String.format("%,.2f",totalFactura));

        holder.cardView.setBackgroundColor(selected_position == position ? Color.parseColor("#d1d3d4") : Color.parseColor("#FFFFFF"));

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_resumen_numeracionRecibosFactura, txt_resumen_abonoRecibosFactura;
        protected CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumenRecibosFactura);
            txt_resumen_numeracionRecibosFactura = (TextView) view.findViewById(R.id.txt_resumen_numeracionRecibosFactura);
            txt_resumen_abonoRecibosFactura = (TextView) view.findViewById(R.id.txt_resumen_abonoRecibosFactura);


            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                    receipts clickedDataItem = contentList.get(pos);
                    facturaID = clickedDataItem.getReceipts_id();

                    Toast.makeText(view.getContext(), "You clicked " + facturaID, Toast.LENGTH_SHORT).show();

                    tabCliente = 1;
                    activity.setSelecReciboTab(tabCliente);
                   // activity.setInvoiceIdReimprimir(facturaID);
                    Log.d("metodoPago", facturaID + "");


                }
            });

        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
