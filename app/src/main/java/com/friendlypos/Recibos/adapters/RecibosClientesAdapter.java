package com.friendlypos.Recibos.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.application.util.Functions;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by DelvoM on 04/09/2018.
 */

public class RecibosClientesAdapter extends RecyclerView.Adapter<RecibosClientesAdapter.CharacterViewHolder> {

    public List<recibos> contentList;
    private RecibosActivity activity;
    private int selected_position = -1;
    private static Context QuickContext = null;
    String facturaID, clienteID;
    int tabCliente;
    String usuer;
    SessionPrefes session;
    String idUsuario;
    int nextId;
    String numFactura;

    public RecibosClientesAdapter(Context context, RecibosActivity activity, List<recibos> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
        session = new SessionPrefes(context);
    }

    @Override
    public RecibosClientesAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_recibos_clientes, parent, false);

        return new RecibosClientesAdapter.CharacterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecibosClientesAdapter.CharacterViewHolder holder, final int position) {
        final recibos recibo = contentList.get(position);
        activity.cleanTotalizeFinal();
        Realm realm = Realm.getDefaultInstance();

        String clienteId = activity.getClienteIdRecibos();


        Clientes clientes = realm.where(Clientes.class).equalTo("id", recibo.getCustomer_id()).findFirst();
        // final invoice invoice = realm.where(invoice.class).equalTo("id", recibo.getInvoice_id()).findFirst();

        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();
        String numeracionFactura = recibo.getNumeration();
        double tot = 0.0;
        RealmResults<recibos> result1 = realm.where(recibos.class).equalTo("customer_id", recibo.getCustomer_id()).findAllSorted("date", Sort.DESCENDING);

        int cant = result1.size();
        Log.d("RECIBOSCLIENTE", cant+ "");

        for(int i = 0; i<cant; i++)
        {
            int abonado1 =  result1.get(i).getAbonado();
            double total1 =  result1.get(i).getTotal();
            double pago1 =  result1.get(i).getPaid();
            double totalPagado1 = total1 - pago1;

            Log.d("PAGOSFOR2", totalPagado1 + "   " + abonado1 + "");
            activity.setTotalizarFinalCliente(totalPagado1);

            // double porPagar = recibo.getPorPagar();
            int abonado = recibo.getAbonado();
            double total = recibo.getTotal();
            double pago = recibo.getPaid();
            double totalPagado = total - pago;
            Log.d("PAGOSFOR1", totalPagado + "   " + abonado + "");

        }

        tot = activity.getTotalizarFinalCliente();
        Log.d("PAGOSFORTOT", tot + "");

        if(tot == 0.0){
            holder.cardView.setVisibility(View.GONE);
            holder.cardView.getLayoutParams().height = 0;
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(0, 0,0, 0);
            holder.cardView.requestLayout();
            Log.d("inactivo", "inactivo");

        }
        else{
            holder.txt_cliente_factura_card.setText(cardCliente);
            holder.txt_cliente_factura_fantasyname.setText(fantasyCliente);
            holder.txt_cliente_factura_companyname.setText(companyCliente);
        }



        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int tabCliente1 = activity.getSelecClienteTabRecibos();
                if (tabCliente1 == 1) {

                    AlertDialog dialogReturnSale = new AlertDialog.Builder(activity)
                            .setTitle("Salir")
                            .setMessage("¿Desea cancelar la factura en proceso y empezar otra?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final Realm realm2 = Realm.getDefaultInstance();

                                    realm2.executeTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm) {

                                            Number numero = realm.where(Numeracion.class).equalTo("sale_type", "4").max("number");

                                            if (numero == null) {
                                                nextId = 1;
                                            } else {
                                                nextId = numero.intValue() - 1;
                                            }

                                        }
                                    });
                                    final Realm realm5 = Realm.getDefaultInstance();
                                    realm5.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm5) {
                                            Numeracion numNuevo = new Numeracion(); // unmanaged
                                            numNuevo.setSale_type("4");
                                            numNuevo.setNumeracion_numero(nextId);

                                            realm5.insertOrUpdate(numNuevo);
                                            Log.d("RecNumNuevaAtras", numNuevo + "");


                                        }

                                    });

                                    int id = nextId + 1;
                                    final String idRecipiente = String.valueOf(id);
                                    final Realm realm6 = Realm.getDefaultInstance();
                                    realm6.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm5) {

                                            RealmResults<receipts> result = realm6.where(receipts.class).equalTo("receipts_id",idRecipiente).findAll();
                                            result.deleteAllFromRealm();
                                            Log.d("ReciboBorrado", result + "");
                                        }

                                    });
                                    realm5.close();
                                    realm6.close();

                                    //   activa = 1;
                                    activity.cleanTotalizeFinal();
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

                                    notifyItemChanged(selected_position);
                                    selected_position = position;
                                    notifyItemChanged(selected_position);
                                    recibos clickedDataItem = contentList.get(pos);

                                    facturaID = clickedDataItem.getInvoice_id();
                                    clienteID = clickedDataItem.getCustomer_id();
                                    double totalP = clickedDataItem.getPorPagar();

                                    Log.d("totalP", totalP + "");

                                    //  Toast.makeText(activity, facturaID + " " + clienteID + " ", Toast.LENGTH_LONG).show();

                                    tabCliente = 1;
                                    activity.setSelecClienteTabRecibos(tabCliente);
                                    activity.setClienteIdRecibos(clienteID);
                                    crearRecibo();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create();
                    dialogReturnSale.show();

                }else{
                    //   activa = 1;
                    activity.cleanTotalizeFinal();
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

                    notifyItemChanged(selected_position);
                    selected_position = position;
                    notifyItemChanged(selected_position);
                    recibos clickedDataItem = contentList.get(pos);

                    facturaID = clickedDataItem.getInvoice_id();
                    clienteID = clickedDataItem.getCustomer_id();
                    double totalP = clickedDataItem.getPorPagar();

                    Log.d("totalP", totalP + "");

                    //  Toast.makeText(activity, facturaID + " " + clienteID + " ", Toast.LENGTH_LONG).show();

                    tabCliente = 1;
                    activity.setSelecClienteTabRecibos(tabCliente);
                    activity.setClienteIdRecibos(clienteID);
                    crearRecibo();
                }
            }
        });
        if(selected_position==position){
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }

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

    public void crearRecibo(){

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        idUsuario = usuarios.getId();
        realm.close();

        final Realm realm2 = Realm.getDefaultInstance();

        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                Number numero = realm.where(Numeracion.class).equalTo("sale_type", "4").max("number");

                if (numero == null) {
                    nextId = 1;
                } else {
                    nextId = numero.intValue() + 1;
                }
                int valor = numero.intValue();

                int length = String.valueOf(valor).length();

                if(length == 1){
                    numFactura = idUsuario + "04-" +  "000000" + nextId;
                }
                else if(length == 2){
                    numFactura = idUsuario + "04-" +  "00000" + nextId;
                }
                else if(length == 3){
                    numFactura = idUsuario + "04-" +  "0000" + nextId;
                }
                else if(length == 4){
                    numFactura = idUsuario + "04-" +  "000" + nextId;
                }
                else if(length == 5){
                    numFactura = idUsuario + "04-" +  "00" + nextId;
                }
                else if(length == 6){
                    numFactura = idUsuario + "04-" +  "0" + nextId;
                }
                else if(length == 7){
                    numFactura = idUsuario + "04-" +  nextId;
                }
            }
        });


        final Realm realmRecibo = Realm.getDefaultInstance();
        realmRecibo.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realmRecibo) {
                receipts receipt = new receipts(); // unmanaged

                receipt.setReceipts_id(String.valueOf(nextId));
                receipt.setCustomer_id(clienteID);
                receipt.setCustomer_id(clienteID);
                receipt.setReference(numFactura);
                receipt.setDate(Functions.getDate());

                realmRecibo.insertOrUpdate(receipt);
                Log.d("ReciboNuevo", receipt + "");
                activity.setReceipts_id_num(String.valueOf(nextId));


            }

        });
        realmRecibo.close();


        final Realm realm5 = Realm.getDefaultInstance();
        realm5.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm5) {
                Numeracion numNuevo = new Numeracion(); // unmanaged
                numNuevo.setSale_type("4");
                numNuevo.setNumeracion_numero(nextId);
                realm5.insertOrUpdate(numNuevo);
                Log.d("NumRecibosNueva", numNuevo + "");


            }

        });
        realm5.close();


    }

}

