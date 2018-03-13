package com.friendlypos.preventas.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PrevClientesAdapter extends RecyclerView.Adapter<PrevClientesAdapter.CharacterViewHolder> {

    public List<Clientes> contentList;
    private PreventaActivity activity;
    private int selected_position = -1;
    private int selected_position1 = -1;
    private static Context QuickContext = null;
    RealmResults<Pivot> facturaid1;
    int idInvetarioSelec;
    Double amount_inventario = 0.0;
    String facturaID, clienteID;
    int nextId;
    int tabCliente;
    int activa = 0;
    String nombreMetodoPago;

    private static Double creditolimite = 0.0;
    private static Double descuentoFixed = 0.0;
    private static Double cleintedue = 0.0;
    private static Double credittime = 0.0;


    public PrevClientesAdapter(Context context, PreventaActivity activity, List<Clientes> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
    }

    @Override
    public PrevClientesAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_preventa_clientes, parent, false);

        return new PrevClientesAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PrevClientesAdapter.CharacterViewHolder holder, final int position) {
        Clientes content = contentList.get(position);

        creditolimite = Double.parseDouble(content.getCreditLimit());
        descuentoFixed =   Double.parseDouble(content.getFixedDiscount());
        cleintedue = Double.parseDouble(content.getDue());
        credittime =   Double.parseDouble(content.getCreditTime());
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


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int pos = position;

                // Updating old as well as new positions
                notifyItemChanged(selected_position1);
                selected_position1 = position;
                notifyItemChanged(selected_position1);

                Clientes clickedDataItem = contentList.get(pos);


                Realm realm6 = Realm.getDefaultInstance();
                //invoice invoice1 = realm6.where(invoice.class).equalTo("id", clickedDataItem.getInvoice_id()).findFirst();
               // Clientes clientes = realm6.where(Clientes.class).equalTo("id", clickedDataItem.getCustomer_id()).findFirst();
                realm6.close();

              //  String metodoPago = invoice1.getPayment_method_id();
              //  String numeracionFactura1 = invoice1.getNumeration();
                final int creditoTime = Integer.parseInt(clickedDataItem.getCreditTime());

             /*   if (metodoPago.equals("1")){
                    nombreMetodoPago = "Contado";
                }
                else if(metodoPago.equals("2")){
                    nombreMetodoPago = "Crédito";
                }
*/

                LayoutInflater layoutInflater = LayoutInflater.from(QuickContext);

                View promptView = layoutInflater.inflate(R.layout.promptclient_preventa, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuickContext);
                alertDialogBuilder.setView(promptView);
                final RadioButton rbcontado = (RadioButton) promptView.findViewById(R.id.contadoBill);
                final RadioButton rbcredito = (RadioButton) promptView.findViewById(R.id.creditBill);
                final RadioGroup rgTipo = (RadioGroup) promptView.findViewById(R.id.rgTipo);

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
                                        Toast.makeText(QuickContext, "Este cliente no cuenta con crédito", Toast.LENGTH_LONG).show();
                                    }
                                    else if (nombreMetodoPago.equals("Crédito")) {
                                        Toast.makeText(QuickContext, "Esta factura ya es de crédito", Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(QuickContext, "Se cambio la factura a crédito", Toast.LENGTH_LONG).show();
                                        notifyDataSetChanged();
                                    }
                                }

                                if(nombreMetodoPago.equals("Contado") && rbcontado.isChecked() ){
                                    Toast.makeText(QuickContext, "Esta factura ya es de contado", Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(QuickContext,"Se cambio la factura a contado", Toast.LENGTH_LONG).show();
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
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
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

                Clientes clickedDataItem = contentList.get(pos);


               // facturaID = clickedDataItem.getInvoice_id();
                clienteID = clickedDataItem.getId();

                Realm realm = Realm.getDefaultInstance();
                invoice invoice = realm.where(com.friendlypos.distribucion.modelo.invoice.class).equalTo("id", facturaID).findFirst();
                Clientes clientes = realm.where(Clientes.class).equalTo("id", clienteID).findFirst();
                //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());
                facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaID).findAll();
                String metodoPago = invoice.getPayment_method_id();
                String creditoLimiteCliente = clientes.getCreditLimit();
                String dueCliente = clientes.getDue();
                realm.close();

                Toast.makeText(QuickContext, "You clicked " + facturaID, Toast.LENGTH_SHORT).show();
                Log.d("PRODUCTOSFACTURATO", facturaid1 + "");
                Log.d("metodoPago", metodoPago + "");
                tabCliente = 1;
                activity.setSelecClienteTab(tabCliente);
                activity.setInvoiceId(facturaID);
                activity.setMetodoPagoCliente(metodoPago);
                activity.setCreditoLimiteCliente(creditoLimiteCliente);
                activity.setDueCliente(dueCliente);*/
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


        private TextView txt_prev_card,txt_prev_fantasyname,txt_prev_companyname,txt_prev_creditlimit,
                txt_prev_fixeddescount, txt_prev_due,txt_prev_credittime;
        public CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewPreventaClientes);

            txt_prev_card = (TextView)view.findViewById(R.id.txt_prev_card);
            txt_prev_fantasyname = (TextView)view.findViewById(R.id.txt_prev_fantasyname);
            txt_prev_companyname = (TextView)view.findViewById(R.id.txt_prev_companyname);

            txt_prev_creditlimit = (TextView)view.findViewById(R.id.txt_prev_creditlimit);
            txt_prev_fixeddescount = (TextView)view.findViewById(R.id.txt_prev_fixeddescount);
            txt_prev_due = (TextView)view.findViewById(R.id.txt_prev_due);
            txt_prev_credittime = (TextView)view.findViewById(R.id.txt_prev_credittime);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
