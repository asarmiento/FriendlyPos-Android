package com.friendlypos.distribucion.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.lang.String.valueOf;

public class DistrClientesAdapter extends RecyclerView.Adapter<DistrClientesAdapter.CharacterViewHolder> {

    public List<Venta> contentList;
    private DistribucionActivity activity;
    //private boolean isSelected = false;
    private int selected_position = -1;
    private static Context QuickContext = null;



    public DistrClientesAdapter(Context context, DistribucionActivity activity, List<Venta> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.lista_distribucion_clientes, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, final int position) {
        final Venta venta = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();


        Clientes clientes = realm.where(Clientes.class).equalTo("id", venta.getCustomer_id()).findFirst();
        final Facturas facturas = realm.where(Facturas.class).equalTo("id", venta.getInvoice_id()).findFirst();

        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();
        String numeracionFactura = facturas.getNumeration();

        holder.txt_cliente_factura_card.setText(cardCliente);
        holder.txt_cliente_factura_fantasyname.setText(fantasyCliente);
        holder.txt_cliente_factura_companyname.setText(companyCliente);
        holder.txt_cliente_factura_numeracion.setText("Factura: " + numeracionFactura);
        holder.cardView.setBackgroundColor(selected_position == position ? Color.parseColor("#607d8b") : Color.parseColor("#009688"));

        holder.btnDevolverFacturaCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                devolverFactura(facturas);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_cliente_factura_card, txt_cliente_factura_fantasyname, txt_cliente_factura_companyname, txt_cliente_factura_numeracion;
        protected CardView cardView;
        Button btnDevolverFacturaCliente;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewDistrClientes);
            txt_cliente_factura_card = (TextView) view.findViewById(R.id.txt_cliente_factura_card);
            txt_cliente_factura_fantasyname = (TextView) view.findViewById(R.id.txt_cliente_factura_fantasyname);
            txt_cliente_factura_companyname = (TextView) view.findViewById(R.id.txt_cliente_factura_companyname);
            txt_cliente_factura_numeracion = (TextView) view.findViewById(R.id.txt_cliente_factura_numeracion);
            btnDevolverFacturaCliente = (Button) view.findViewById(R.id.btnDevolverFacturaCliente);

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
                        String facturaID = clickedDataItem.getInvoice_id();

                        Realm realm = Realm.getDefaultInstance();
                        //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());
                        RealmResults<Pivot> facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaID).findAll();

                        realm.close();



                        Toast.makeText(view.getContext(), "You clicked " + facturaID, Toast.LENGTH_SHORT).show();
                        Log.d("PRODUCTOSFACTURA", facturaid1 + "");
                        activity.setInvoiceId(facturaID);

                }
            });

        }
    }

    public static void devolverFactura(final Facturas facturas) {
        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Devolución")
                .setMessage("¿Desea proceder con la devolución de la factura?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                      /*  sendReturnSales(rSale);
                        refreshSalesList();*/
                        Functions.CreateMessage(QuickContext, "Devolución", " Se devolvió la factura " + facturas.getNumeration() + " .");
                       /* System.out.print("FACTURACARGA");
                        getDataInventories getdata1 = new getDataInventories();
                        getdata1.execute();*/


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialogReturnSale.show();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
