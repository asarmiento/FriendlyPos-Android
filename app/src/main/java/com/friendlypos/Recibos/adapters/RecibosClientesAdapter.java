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
        final invoice invoice = realm.where(invoice.class).equalTo("id", recibo.getInvoice_id()).findFirst();

        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();
        String numeracionFactura = recibo.getNumeration();
        //String nombreVenta = recibo.getCustomer_name();

       // final double longitud = invoice.getLongitud();
      //  final double latitud = invoice.getLatitud();



        holder.txt_cliente_factura_card.setText(cardCliente);
      /*  if (fantasyCliente.equals("Cliente Generico")){
            holder.txt_cliente_factura_fantasyname.setText(nombreVenta);
        }else{*/
            holder.txt_cliente_factura_fantasyname.setText(fantasyCliente);
       /* }*/
        holder.txt_cliente_factura_companyname.setText(companyCliente);
        holder.txt_cliente_factura_numeracion.setText("Factura: " + numeracionFactura);

    /*    holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int pos = position;
                sale clickedDataItem = contentList.get(pos);

                Realm realm6 = Realm.getDefaultInstance();
                invoice invoice1 = realm6.where(invoice.class).equalTo("id", clickedDataItem.getInvoice_id()).findFirst();
                Clientes clientes = realm6.where(Clientes.class).equalTo("id", clickedDataItem.getCustomer_id()).findFirst();
                realm6.close();

                String metodoPago = invoice1.getPayment_method_id();
                String numeracionFactura1 = invoice1.getNumeration();
                final int creditoTime = Integer.parseInt(clientes.getCreditTime());
                if (metodoPago.equals("1")){
                    nombreMetodoPago = "Contado";
                }
                else if(metodoPago.equals("2")){
                    nombreMetodoPago = "Crédito";
                }


                LayoutInflater layoutInflater = LayoutInflater.from(QuickContext);

                View promptView = layoutInflater.inflate(R.layout.promptclient, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuickContext);
                alertDialogBuilder.setView(promptView);
                final TextView txtTipoFacturaEs = (TextView) promptView.findViewById(R.id.txtTipoFacturaEs);
                final RadioButton rbcontado = (RadioButton) promptView.findViewById(R.id.contadoBill);
                final RadioButton rbcredito = (RadioButton) promptView.findViewById(R.id.creditBill);
                final RadioGroup rgTipo = (RadioGroup) promptView.findViewById(R.id.rgTipo);

                txtTipoFacturaEs.setText("La factura #" + numeracionFactura1 + " es de: " + nombreMetodoPago);

                if (nombreMetodoPago.equals("Contado")){
                    rgTipo.check(R.id.contadoBill);
                }else if (nombreMetodoPago.equals("Crédito")){
                    rgTipo.check(R.id.creditBill);
                }

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if(rbcredito.isChecked()) {
                                    if (creditoTime == 0) {
                                        Functions.CreateMessage(QuickContext, " ", "Este cliente no cuenta con crédito");
                                    }
                                    else if (nombreMetodoPago.equals("Crédito")) {
                                        Functions.CreateMessage(QuickContext, " ", "Esta factura ya es de crédito");
                                    }
                                    else {
                                        // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CREDITO DE LA FACTURA
                                        final Realm realm2 = Realm.getDefaultInstance();
                                        realm2.executeTransaction(new Realm.Transaction() {

                                            @Override
                                            public void execute(Realm realm2) {
                                                invoice factura_actualizado = realm2.where(invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();
                                                factura_actualizado.setPayment_method_id(String.valueOf("2"));
                                                realm2.insertOrUpdate(factura_actualizado);
                                                realm2.close();
                                            }
                                        });
                                        Functions.CreateMessage(QuickContext, " ", "Se cambio la factura a crédito");
                                        notifyDataSetChanged();
                                    }
                                }

                                if(nombreMetodoPago.equals("Contado") && rbcontado.isChecked() ){
                                    Functions.CreateMessage(QuickContext, " ", "Esta factura ya es de contado");
                                }
                                else if(rbcontado.isChecked()){
                                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CONTADO DE LA FACTURA
                                    final Realm realm2 = Realm.getDefaultInstance();
                                    realm2.executeTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm2) {
                                            invoice factura_actualizado = realm2.where(invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();
                                            factura_actualizado.setPayment_method_id(String.valueOf("1"));
                                            realm2.insertOrUpdate(factura_actualizado);
                                            realm2.close();
                                        }
                                    });

                                    Functions.CreateMessage(QuickContext, " ", "Se cambio la factura a contado");
                                    notifyDataSetChanged();
                                }

                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
                return true;
            }
        });*/
     /*   holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activa = 1;

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

                sale clickedDataItem = contentList.get(pos);


                facturaID = clickedDataItem.getInvoice_id();
                clienteID = clickedDataItem.getCustomer_id();

                Realm realm = Realm.getDefaultInstance();
                invoice invoice = realm.where(invoice.class).equalTo("id", facturaID).findFirst();
                Clientes clientes = realm.where(Clientes.class).equalTo("id", clienteID).findFirst();
                //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());
                facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaID).findAll();
                String metodoPago = invoice.getPayment_method_id();
                String creditoLimiteCliente = clientes.getCreditLimit();
                String dueCliente = clientes.getDue();
                realm.close();

                Log.d("PRODUCTOSFACTURATO", facturaid1 + "");
                Log.d("metodoPago", metodoPago + "");
                tabCliente = 1;
                activity.setSelecClienteTab(tabCliente);
                activity.setInvoiceId(facturaID);
                activity.setMetodoPagoCliente(metodoPago);
                activity.setCreditoLimiteCliente(creditoLimiteCliente);
                activity.setDueCliente(dueCliente);
            }
        });
        if(selected_position==position){
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }
*/
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

        private TextView txt_cliente_factura_card, txt_cliente_factura_fantasyname, txt_cliente_factura_companyname, txt_cliente_factura_numeracion;
        public CardView cardView;
        Button btnDevolverFacturaCliente;
        ImageButton btnImprimirFacturaCliente, btnUbicacionFacturaCliente;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewRecibosClientes);
            txt_cliente_factura_card = (TextView) view.findViewById(R.id.txt_cliente_factura_cardRecibos);
            txt_cliente_factura_fantasyname = (TextView) view.findViewById(R.id.txt_cliente_factura_fantasynameRecibos);
            txt_cliente_factura_companyname = (TextView) view.findViewById(R.id.txt_cliente_factura_companynameRecibos);
            txt_cliente_factura_numeracion = (TextView) view.findViewById(R.id.txt_cliente_factura_numeracionRecibos);
            btnDevolverFacturaCliente = (Button) view.findViewById(R.id.btnDevolverFacturaClienteRecibos);
            btnImprimirFacturaCliente = (ImageButton) view.findViewById(R.id.btnImprimirFacturaClienteRecibos);
            btnUbicacionFacturaCliente = (ImageButton) view.findViewById(R.id.btnUbicacionFacturaClienteRecibos);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

