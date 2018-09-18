package com.friendlypos.Recibos.adapters;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.adapters.DistrClientesAdapter;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by DelvoM on 04/09/2018.
 */

public class RecibosClientesAdapter extends RecyclerView.Adapter<RecibosClientesAdapter.CharacterViewHolder> {

    public List<Recibos> contentList;
    private RecibosActivity activity;
    private int selected_position = -1;
    private static Context QuickContext = null;
    String facturaID, clienteID;
    int tabCliente;


    public RecibosClientesAdapter(Context context, RecibosActivity activity, List<Recibos> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
    }

    @Override
    public RecibosClientesAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_recibos_clientes, parent, false);

        return new RecibosClientesAdapter.CharacterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecibosClientesAdapter.CharacterViewHolder holder, final int position) {
        final Recibos recibo = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();

        Clientes clientes = realm.where(Clientes.class).equalTo("id", recibo.getCustomer_id()).findFirst();
       // final invoice invoice = realm.where(invoice.class).equalTo("id", recibo.getInvoice_id()).findFirst();

        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();
      //  String numeracionFactura = recibo.getNumeration();


        holder.txt_cliente_factura_card.setText(cardCliente);
      /*  if (fantasyCliente.equals("Cliente Generico")){
            holder.txt_cliente_factura_fantasyname.setText(nombreVenta);
        }else{*/
            holder.txt_cliente_factura_fantasyname.setText(fantasyCliente);
       /* }*/
        holder.txt_cliente_factura_companyname.setText(companyCliente);
      //  holder.txt_cliente_factura_numeracion.setText("Factura: " + numeracionFactura);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   activa = 1;

                final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                progresRing.setCancelable(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {

                        }
                        progresRing.dismiss();
                    }
                }).start();

                int pos = position;
                if (pos == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                Recibos clickedDataItem = contentList.get(pos);


                facturaID = clickedDataItem.getInvoice_id();
                clienteID = clickedDataItem.getCustomer_id();

                Toast.makeText(activity, facturaID + " " + clienteID + " ", Toast.LENGTH_LONG).show();

                tabCliente = 1;
                activity.setSelecClienteTabRecibos(tabCliente);

                activity.setClienteIdRecibos(clienteID);

            }
        });
        if(selected_position==position){
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }

       /* holder.btnUbicacionFacturaCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(activa == 1) {

                    if (longitud != 0.0 && latitud != 0.0) {

                        try {
                            // Launch Waze to look for Hawaii:
                            //   String url = "https://waze.com/ul?ll=9.9261253,-84.0889091&navigate=yes";

                            String url = "https://waze.com/ul?ll="+ latitud + "," + longitud + "&navigate=yes";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            QuickContext.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {

                            Uri gmmIntentUri = Uri.parse("geo:"+latitud + "," + longitud);
                            // Uri gmmIntentUri = Uri.parse("geo:9.9261253,-84.0889091");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            QuickContext.startActivity(mapIntent);


                        }
                    } else {
                        Toast.makeText(QuickContext, "El cliente no cuenta con dirección GPS", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(QuickContext, "Selecciona una factura primero", Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.btnDevolverFacturaCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(activa == 1){
                    devolverFactura();}
                else{
                    Toast.makeText(QuickContext, "Selecciona una factura primero", Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.btnImprimirFacturaCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(activa == 1){
                    try {
                        PrinterFunctions.imprimirProductosDistrSelecCliente(sale, QuickContext);
                    }
                    catch (Exception e) {
                        Functions.CreateMessage(QuickContext, "Error", e.getMessage() + "\n" + e.getStackTrace().toString());
                    }}
                else{
                    Toast.makeText(QuickContext, "Selecciona una factura primero", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_cliente_factura_card, txt_cliente_factura_fantasyname, txt_cliente_factura_companyname;
        public CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewRecibosClientes);
            txt_cliente_factura_card = (TextView) view.findViewById(R.id.txt_cliente_factura_cardRecibos);
            txt_cliente_factura_fantasyname = (TextView) view.findViewById(R.id.txt_cliente_factura_fantasynameRecibos);
            txt_cliente_factura_companyname = (TextView) view.findViewById(R.id.txt_cliente_factura_companynameRecibos);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

