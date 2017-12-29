package com.friendlypos.reimpresion.adapters;

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
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reimpresion.activity.ReimprimirActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Delvo on 03/12/2017.
 */

public class ReimprimirFacturaAdapter extends RecyclerView.Adapter<ReimprimirFacturaAdapter.CharacterViewHolder> {

    public List<sale> contentList;
    private ReimprimirActivity activity;
    //private boolean isSelected = false;
    private int selected_position = -1;
    private static Context QuickContext = null;
    RealmResults<Pivot> facturaid1;
    int idInvetarioSelec;
    Double amount_dist_inventario = 0.0;
    String facturaID, clienteID;
    int nextId;
    int tabCliente;

    public ReimprimirFacturaAdapter(Context context, ReimprimirActivity activity, List<sale> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_reimprimir_facturas, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, final int position) {

        final sale sale = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();

        long cantidadPivot = 0;

        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        final invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();

        cantidadPivot = realm.where(Pivot.class).equalTo("invoice_id", sale.getInvoice_id()).count();
        String numeracionFactura = invoice.getNumeration();
        String fantasyCliente = clientes.getFantasyName();
        String fecha1 = invoice.getDate();
        String hora1 = invoice.getTimes();
        int subida = invoice.getSubida();
        String aNombreDe = sale.getCustomer_name();
        double totalFactura = Double.parseDouble(invoice.getTotal());

        holder.txt_reimprimir_factura_numeracion.setText(numeracionFactura);
        holder.txt_reimprimir_factura_fechahora.setText(fecha1 + " " + hora1);
        holder.txt_reimprimir_factura_fantasyname.setText(fantasyCliente);
        holder.txt_reimprimir_factura_anombrede.setText(aNombreDe);

        if(subida == 1){
            holder.txtSubida.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        else{
            holder.txtSubida.setBackgroundColor(Color.parseColor("#607d8b"));
        }

        holder.txt_reimprimir_factura_total.setText(String.format("%,.2f",totalFactura));
        holder.txt_reimprimir_factura_cantidad.setText((int) cantidadPivot + "");

        holder.cardView.setBackgroundColor(selected_position == position ? Color.parseColor("#607d8b") : Color.parseColor("#009688"));

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_reimprimir_factura_numeracion, txt_reimprimir_factura_fechahora, txt_reimprimir_factura_fantasyname, txt_reimprimir_factura_anombrede,
                txt_reimprimir_factura_total, txt_reimprimir_factura_cantidad, txtSubida;
        protected CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewReimprimirFactura);
            txt_reimprimir_factura_numeracion = (TextView) view.findViewById(R.id.txt_reimprimir_factura_numeracion);
            txt_reimprimir_factura_fechahora = (TextView) view.findViewById(R.id.txt_reimprimir_factura_fechahora);
            txt_reimprimir_factura_fantasyname = (TextView) view.findViewById(R.id.txt_reimprimir_factura_fantasyname);
            txt_reimprimir_factura_anombrede = (TextView) view.findViewById(R.id.txt_reimprimir_factura_anombrede);
            txtSubida = (TextView) view.findViewById(R.id.txtSubida);
            txt_reimprimir_factura_total = (TextView) view.findViewById(R.id.txt_reimprimir_factura_total);
            txt_reimprimir_factura_cantidad = (TextView) view.findViewById(R.id.txt_reimprimir_factura_cantidad);


            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                    sale clickedDataItem = contentList.get(pos);
                    facturaID = clickedDataItem.getInvoice_id();

                    Toast.makeText(view.getContext(), "You clicked " + facturaID, Toast.LENGTH_SHORT).show();

                    tabCliente = 1;
                    activity.setSelecFacturaTab(tabCliente);
                    activity.setInvoiceIdReimprimir(facturaID);
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
