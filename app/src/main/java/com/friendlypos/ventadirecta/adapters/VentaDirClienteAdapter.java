package com.friendlypos.ventadirecta.adapters;

/**
 * Created by DelvoM on 13/08/2018.
 */

import android.app.ProgressDialog;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;

import java.util.List;

import io.realm.Realm;

public class VentaDirClienteAdapter extends RecyclerView.Adapter<VentaDirClienteAdapter.CharacterViewHolder> {

    public List<Clientes> contentList;
    private VentaDirectaActivity activity;
    private int selected_position = -1;
    private static Context QuickContext = null;
    int tabCliente, nextId;
    String metodoPagoId, idCliente, nombreCliente, fecha, usuer;
    SessionPrefes session;

    private static Double creditolimite = 0.0;
    private static Double descuentoFixed = 0.0;
    private static Double cleintedue = 0.0;
    private static Double credittime = 0.0;


    public VentaDirClienteAdapter(Context context, VentaDirectaActivity activity, List<Clientes> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
        session = new SessionPrefes(context);
    }

    @Override
    public VentaDirClienteAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_ventadirecta_clientes, parent, false);

        return new VentaDirClienteAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VentaDirClienteAdapter.CharacterViewHolder holder, final int position) {
        Clientes content = contentList.get(position);

        creditolimite = Double.parseDouble(content.getCreditLimit());
        descuentoFixed = Double.parseDouble(content.getFixedDiscount());
        cleintedue = Double.parseDouble(content.getDue());
        credittime = Double.parseDouble(content.getCreditTime());
        final String cardCliente = content.getCard();
        String companyCliente = content.getCompanyName();
        String fantasyCliente = content.getFantasyName();

        holder.txt_prev_card.setText(cardCliente);
        holder.txt_prev_fantasyname.setText(fantasyCliente);
        holder.txt_prev_companyname.setText(companyCliente);
        holder.txt_prev_creditlimit.setText(String.format("%,.2f", (creditolimite)));
        holder.txt_prev_fixeddescount.setText(String.format("%,.2f", (descuentoFixed)));
        holder.txt_prev_due.setText(String.format("%,.2f", (cleintedue)));
        holder.txt_prev_credittime.setText(String.format("%,.2f", (credittime)));

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int pos = position;
                if (pos == RecyclerView.NO_POSITION) return;

                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                Clientes clickedDataItem = contentList.get(pos);

                idCliente = clickedDataItem.getId();
                Toast.makeText(QuickContext,"ID CLIENTE" + idCliente, Toast.LENGTH_LONG).show();
                nombreCliente = clickedDataItem.getName();
                final int creditoTime = Integer.parseInt(clickedDataItem.getCreditTime());
                final String creditoLimiteClienteP = clickedDataItem.getCreditLimit();
                final String dueClienteP = clickedDataItem.getDue();

                LayoutInflater layoutInflater = LayoutInflater.from(QuickContext);
                View promptView = layoutInflater.inflate(R.layout.promptclient_preventa, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuickContext);
                alertDialogBuilder.setView(promptView);
                final RadioButton rbcontado = (RadioButton) promptView.findViewById(R.id.contadoBill);
                final RadioButton rbcredito = (RadioButton) promptView.findViewById(R.id.creditBill);

                if (creditoTime == 0) {
                    rbcredito.setVisibility(View.GONE);
                }

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                fecha = Functions.getDate() + " " + Functions.get24Time();

                                if(!rbcontado.isChecked() && !rbcredito.isChecked()){
                                    Functions.CreateMessage(QuickContext, " ", "Debe seleccionar una opción");

                                } else {
                                    if (rbcredito.isChecked()) {
                                            metodoPagoId = "2";
                                            notifyDataSetChanged();
                                            agregar();
                                             tabCliente = 1;
                                            activity.setSelecClienteTabVentaDirecta(tabCliente);
                                            activity.setCreditoLimiteClienteVentaDirecta(creditoLimiteClienteP);
                                            activity.setDueClienteVentaDirecta(dueClienteP);
                                            activity.setInvoiceIdPreventa(nextId);
                                            activity.setMetodoPagoClienteVentaDirecta(metodoPagoId);

                                        final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando",
                                                "Seleccionando Cliente", true);
                                        progresRing.setCancelable(true);
                                        new Thread(new Runnable() {

                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(5000);
                                                }
                                                catch (Exception e) {

                                                }
                                                progresRing.dismiss();
                                            }
                                        }).start();

                                    }
                                    else if (rbcontado.isChecked()) {
                                        metodoPagoId = "1";
                                        notifyDataSetChanged();
                                        agregar();
                                        tabCliente = 1;
                                        activity.setSelecClienteTabVentaDirecta(tabCliente);
                                        activity.setCreditoLimiteClienteVentaDirecta(creditoLimiteClienteP);
                                        activity.setDueClienteVentaDirecta(dueClienteP);
                                        activity.setInvoiceIdPreventa(nextId);
                                        activity.setMetodoPagoClienteVentaDirecta(metodoPagoId);

                                        final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando",
                                                "Seleccionando Cliente", true);
                                        progresRing.setCancelable(true);
                                        new Thread(new Runnable() {

                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(5000);
                                                }
                                                catch (Exception e) {

                                                }
                                                progresRing.dismiss();
                                            }
                                        }).start();
                                    }



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

            }
        });
        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }

    }

    public void agregar(){

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String idUsuario = usuarios.getId();
        realm.close();

        final Realm realm2 = Realm.getDefaultInstance();

        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                // increment index
                                  /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();

                                    if(numeracion.getId()){}
*/
                Number numero = realm.where(Numeracion.class).equalTo("sale_type", "2").max("numeracion_numero");

                if (numero == null) {
                    nextId = 1;
                }
                else {
                    nextId = numero.intValue() + 1;
                }


            }
        });

        //TODO MODIFICAR CON EL IDS CONSECUTIVOS (FACTURA Y NUMERACION)
        activity.initCurrentInvoice(String.valueOf(nextId), "3", idUsuario + "01-"+ "000000"+nextId, 0.0, 0.0, Functions.getDate(), Functions.get24Time(),
                Functions.getDate(), Functions.get24Time(), Functions.getDate(), "2", metodoPagoId, "", "", "", "", "", "", "", "", "", "", "", "", fecha,
                "", "");

        activity.initCurrentVenta(String.valueOf(nextId), String.valueOf(nextId), idCliente, nombreCliente, "6", "2", "0", "0", fecha, fecha, "0", 1, 1, 2);

        final Realm realm5 = Realm.getDefaultInstance();
        realm5.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm5) {
                Numeracion numNuevo= new Numeracion(); // unmanaged
                numNuevo.setSale_type("3");
                numNuevo.setNumeracion_numero(nextId);

                realm5.insertOrUpdate(numNuevo);
                Log.d("idinvNUEVOCREADO", numNuevo +"");


            }

        });
        realm5.close();
        // realm2.close();
    }


    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {


        private TextView txt_prev_card, txt_prev_fantasyname, txt_prev_companyname, txt_prev_creditlimit,
                txt_prev_fixeddescount, txt_prev_due, txt_prev_credittime;
        public CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewVentaDirectaClientes);

            txt_prev_card = (TextView) view.findViewById(R.id.txt_ventadir_card);
            txt_prev_fantasyname = (TextView) view.findViewById(R.id.txt_ventadir_fantasyname);
            txt_prev_companyname = (TextView) view.findViewById(R.id.txt_ventadir_companyname);

            txt_prev_creditlimit = (TextView) view.findViewById(R.id.txt_ventadir_creditlimit);
            txt_prev_fixeddescount = (TextView) view.findViewById(R.id.txt_ventadir_fixeddescount);
            txt_prev_due = (TextView) view.findViewById(R.id.txt_ventadir_due);
            txt_prev_credittime = (TextView) view.findViewById(R.id.txt_ventadir_credittime);
        }
    }

    /* private int getContador() {
         Realm realm = Realm.getDefaultInstance();
         realmResultado = realm.where(InvoiceDetallePreventa.class).findAll();
         realmResultado.sort("p_id");

 //        InvoiceDetallePreventa invoiceDetallePreventa = activity.getCurrentInvoice();
 //        invoiceDetallePreventa.setP_code(weqweq);
 //
         return realmResultado.size();
     }
 */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

