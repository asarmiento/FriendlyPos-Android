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
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
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
    int nextId, numeration;
    int tabCliente;
    int activa = 0;
    String nombreMetodoPago;
    String metodoPagoId;
    int contador = 0;
  //  private RealmResults<InvoiceDetallePreventa> realmResultado;
    invoiceDetallePreventa factura_nueva = new invoiceDetallePreventa();

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
                activa = 1;

                final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
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

                int pos = position;
                if (pos == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                Clientes clickedDataItem = contentList.get(pos);

                final String idCliente = clickedDataItem.getId();
                final String nombreCliente = clickedDataItem.getName();
                final int creditoTime = Integer.parseInt(clickedDataItem.getCreditTime());
                final String creditoLimiteClienteP = clickedDataItem.getCreditLimit();
                final String dueClienteP = clickedDataItem.getDue();

                LayoutInflater layoutInflater = LayoutInflater.from(QuickContext);
                View promptView = layoutInflater.inflate(R.layout.promptclient_preventa, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuickContext);
                alertDialogBuilder.setView(promptView);
                final RadioButton rbcontado = (RadioButton) promptView.findViewById(R.id.contadoBill);
                final RadioButton rbcredito = (RadioButton) promptView.findViewById(R.id.creditBill);

                alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            String fecha = Functions.getDate() + " " + Functions.get24Time();
                            if (rbcredito.isChecked()) {
                                if (creditoTime == 0) {
                                    Functions.CreateMessage(QuickContext, " ", "Este cliente no cuenta con crédito");
                                }
                                else {
                                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CREDITO DE LA FACTURA
                                    metodoPagoId = "2";
                                    Functions.CreateMessage(QuickContext, " ", "Se cambio la factura a crédito" + metodoPagoId);
                                    notifyDataSetChanged();
                                }
                            }
                            else if (rbcontado.isChecked()) {
                                // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CONTADO DE LA FACTURA
                                metodoPagoId = "1";
                                Functions.CreateMessage(QuickContext, " ", "Se cambio la factura a contado" + metodoPagoId);
                                notifyDataSetChanged();
                            }

                       /*    final Realm realm2 = Realm.getDefaultInstance();

                            realm2.executeTransaction(new Realm.Transaction() {

                                @Override
                                public void execute(Realm realm) {

                                    // increment index

                                    int numero = (int) realm.where(invoice.class).max("id");

                                    Number currentIdNum = numero;


                                    if (currentIdNum == null) {
                                        nextId = 1;
                                    }
                                    else {
                                        nextId = currentIdNum.intValue() + 1;
                                    }

                                    // increment index
                                    Number NumFactura = realm.where(invoice.class).max("numeration");

                                    if (NumFactura == null) {
                                        numeration = 1;
                                    }
                                    else {
                                        numeration = NumFactura.intValue() + 1;
                                    }

                                }
                           });
*/
                            activity.initCurrentInvoice("1", "3", "00001", 0.0, 0.0, Functions.getDate(), Functions.get24Time(),
                                    Functions.getDate(), Functions.get24Time(), Functions.getDate(), "2", metodoPagoId, "", "", "", "", "", "","","","","","","",fecha,
                                    "","");

                            activity.initCurrentVenta("1", "1", idCliente, nombreCliente, "6", "2", "0", "0", fecha, fecha, "0", 1 , 0);
                            tabCliente = 1;
                            activity.setSelecClienteTabPreventa(tabCliente);
                            activity.setCreditoLimiteClientePreventa(creditoLimiteClienteP);
                            activity.setDueClientePreventa(dueClienteP);
                            activity.setInvoiceIdPreventa(1);
                            activity.setMetodoPagoClientePreventa(metodoPagoId);
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


              //  Toast.makeText(QuickContext, "You clicked FACTURA NUEVA " + factura_nueva.getP_id(), Toast.LENGTH_SHORT).show();


            }
        });
        if (selected_position == position) {
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }

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
            cardView = (CardView) view.findViewById(R.id.cardViewPreventaClientes);

            txt_prev_card = (TextView) view.findViewById(R.id.txt_prev_card);
            txt_prev_fantasyname = (TextView) view.findViewById(R.id.txt_prev_fantasyname);
            txt_prev_companyname = (TextView) view.findViewById(R.id.txt_prev_companyname);

            txt_prev_creditlimit = (TextView) view.findViewById(R.id.txt_prev_creditlimit);
            txt_prev_fixeddescount = (TextView) view.findViewById(R.id.txt_prev_fixeddescount);
            txt_prev_due = (TextView) view.findViewById(R.id.txt_prev_due);
            txt_prev_credittime = (TextView) view.findViewById(R.id.txt_prev_credittime);
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
