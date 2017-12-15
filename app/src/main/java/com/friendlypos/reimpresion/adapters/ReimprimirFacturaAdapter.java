package com.friendlypos.reimpresion.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrClientesAdapter;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reimpresion.activity.ReimprimirActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Delvo on 03/12/2017.
 */

public class ReimprimirFacturaAdapter extends RecyclerView.Adapter<ReimprimirFacturaAdapter.CharacterViewHolder> {

    public List<Venta> contentList;
    private ReimprimirActivity activity;
    //private boolean isSelected = false;
    private int selected_position = -1;
    private static Context QuickContext = null;
    RealmResults<Pivot> facturaid1;
    int idInvetarioSelec;
    Double amount_dist_inventario = 0.0;
    String facturaID, clienteID;
    int nextId;

    public ReimprimirFacturaAdapter(Context context, ReimprimirActivity activity, List<Venta> contentList) {
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

        final Venta venta = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();

        long cantidadPivot = 0;

        Clientes clientes = realm.where(Clientes.class).equalTo("id", venta.getCustomer_id()).findFirst();
        final Facturas facturas = realm.where(Facturas.class).equalTo("id", venta.getInvoice_id()).findFirst();

        cantidadPivot = realm.where(Pivot.class).equalTo("invoice_id", venta.getInvoice_id()).count();
        String numeracionFactura = facturas.getNumeration();
        String fantasyCliente = clientes.getFantasyName();
        String aNombreDe = venta.getCustomer_name();
        double totalFactura = Double.parseDouble(facturas.getTotal());

        holder.txt_reimprimir_factura_numeracion.setText(numeracionFactura);
        holder.txt_reimprimir_factura_fechahora.setText(Functions.getDate() + " " + Functions.get24Time());
        holder.txt_reimprimir_factura_fantasyname.setText(fantasyCliente);
        holder.txt_reimprimir_factura_anombrede.setText(aNombreDe);

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
                txt_reimprimir_factura_total, txt_reimprimir_factura_cantidad;
        protected CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewReimprimirFactura);
            txt_reimprimir_factura_numeracion = (TextView) view.findViewById(R.id.txt_reimprimir_factura_numeracion);
            txt_reimprimir_factura_fechahora = (TextView) view.findViewById(R.id.txt_reimprimir_factura_fechahora);
            txt_reimprimir_factura_fantasyname = (TextView) view.findViewById(R.id.txt_reimprimir_factura_fantasyname);
            txt_reimprimir_factura_anombrede = (TextView) view.findViewById(R.id.txt_reimprimir_factura_anombrede);

            txt_reimprimir_factura_total = (TextView) view.findViewById(R.id.txt_reimprimir_factura_total);
            txt_reimprimir_factura_cantidad = (TextView) view.findViewById(R.id.txt_reimprimir_factura_cantidad);


            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;

                    // Updating old as well as new positions
                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                    Venta clickedDataItem = contentList.get(pos);
                    facturaID = clickedDataItem.getInvoice_id();
                    /*clienteID = clickedDataItem.getCustomer_id();

                    Realm realm = Realm.getDefaultInstance();
                    Facturas facturas = realm.where(Facturas.class).equalTo("id", facturaID).findFirst();
                    Clientes clientes = realm.where(Clientes.class).equalTo("id", clienteID).findFirst();
                    //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());
                    facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaID).findAll();
                    String metodoPago = facturas.getPayment_method_id();
                    String creditoLimiteCliente = clientes.getCreditLimit();
                    String dueCliente = clientes.getDue();
                    realm.close();*/


                    Toast.makeText(view.getContext(), "You clicked " + facturaID, Toast.LENGTH_SHORT).show();
                   /* Log.d("PRODUCTOSFACTURATO", facturaid1 + "");
                    Log.d("metodoPago", metodoPago + "");*/
                    activity.setInvoiceIdReimprimir(facturaID);
                    Log.d("metodoPago", facturaID + "");
                 /*   activity.setInvoiceId(facturaID);
                    activity.setMetodoPagoCliente(metodoPago);
                    activity.setCreditoLimiteCliente(creditoLimiteCliente);
                    activity.setDueCliente(dueCliente);*/

                }
            });

        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
