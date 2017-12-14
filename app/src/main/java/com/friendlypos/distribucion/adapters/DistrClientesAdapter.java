package com.friendlypos.distribucion.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static android.R.attr.handle;
import static java.lang.String.valueOf;

public class DistrClientesAdapter extends RecyclerView.Adapter<DistrClientesAdapter.CharacterViewHolder> {

    public List<Venta> contentList;
    private DistribucionActivity activity;
    //private boolean isSelected = false;
    private int selected_position = -1;
    private static Context QuickContext = null;
    RealmResults<Pivot> facturaid1;
    int idInvetarioSelec;
    Double amount_dist_inventario = 0.0;
    String facturaID, clienteID;
    int nextId;
    int tabCliente;
    ProgressDialog progressDoalog;

    public DistrClientesAdapter(Context context, DistribucionActivity activity, List<Venta> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
        activity.setSelecClienteTab(tabCliente);
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
                devolverFactura();
            }
        });

        holder.btnImprimirFacturaCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    PrinterFunctions.imprimirProductosDistrSelecCliente(venta, QuickContext);
                }
                catch (Exception e) {
                    Functions.CreateMessage(QuickContext, "Error", e.getMessage() + "\n" + e.getStackTrace().toString());
                }
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
        ImageButton btnImprimirFacturaCliente;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewDistrClientes);
            txt_cliente_factura_card = (TextView) view.findViewById(R.id.txt_cliente_factura_card);
            txt_cliente_factura_fantasyname = (TextView) view.findViewById(R.id.txt_cliente_factura_fantasyname);
            txt_cliente_factura_companyname = (TextView) view.findViewById(R.id.txt_cliente_factura_companyname);
            txt_cliente_factura_numeracion = (TextView) view.findViewById(R.id.txt_cliente_factura_numeracion);
            btnDevolverFacturaCliente = (Button) view.findViewById(R.id.btnDevolverFacturaCliente);
            btnImprimirFacturaCliente = (ImageButton) view.findViewById(R.id.btnImprimirFacturaCliente);

            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

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

                                int pos = getAdapterPosition();
                                //if (pos == RecyclerView.NO_POSITION) return;

                                // Updating old as well as new positions
                                notifyItemChanged(selected_position);
                                selected_position = getAdapterPosition();
                                notifyItemChanged(selected_position);

                                Venta clickedDataItem = contentList.get(pos);
                                facturaID = clickedDataItem.getInvoice_id();
                                clienteID = clickedDataItem.getCustomer_id();

                                Realm realm = Realm.getDefaultInstance();
                                Facturas facturas = realm.where(Facturas.class).equalTo("id", facturaID).findFirst();
                                Clientes clientes = realm.where(Clientes.class).equalTo("id", clienteID).findFirst();
                                //String facturaid = String.valueOf(realm.where(ProductoFactura.class).equalTo("id", facturaID).findFirst().getId());
                                facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaID).findAll();
                                String metodoPago = facturas.getPayment_method_id();
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
                                activity.setDueCliente(dueCliente);

                }
            });

        }
    }

    public void devolverFactura() {
        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Devolución")
                .setMessage("¿Desea proceder con la devolución de la factura?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.d("PRODUCTOSFACTURA1", facturaid1 + "");

                        for (int i = 0; i < facturaid1.size(); i++) {
                            final Pivot eventRealm = facturaid1.get(i);
                            final double cantidadDevolver = Double.parseDouble(eventRealm.getAmount());

                            Log.d("PRODUCTOSFACTURASEPA1", eventRealm + "");
                            Log.d("PRODUCTOSFACTURASEPA", cantidadDevolver + "");

                            // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                            Realm realm3 = Realm.getDefaultInstance();
                            realm3.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm3) {

                                    Inventario inventario = realm3.where(Inventario.class).equalTo("product_id", eventRealm.getProduct_id()).findFirst();

                                        if (inventario != null) {
                                            idInvetarioSelec = inventario.getId();
                                            amount_dist_inventario = Double.valueOf(inventario.getAmount_dist());
                                            Log.d("idinventario", idInvetarioSelec+"");

                                        } else {
                                            amount_dist_inventario = 0.0;
                                            // increment index
                                            Number currentIdNum = realm3.where(Inventario.class).max("id");

                                            if (currentIdNum == null) {
                                                nextId = 1;
                                            } else {
                                                nextId = currentIdNum.intValue() + 1;
                                            }

                                            Inventario invnuevo= new Inventario(); // unmanaged
                                            invnuevo.setId(nextId);
                                            invnuevo.setProduct_id(eventRealm.getProduct_id());
                                            invnuevo.setInitial(String.valueOf(cantidadDevolver));
                                            invnuevo.setAmount(String.valueOf("0"));
                                            invnuevo.setAmount_dist(String.valueOf(cantidadDevolver));
                                            invnuevo.setDistributor(String.valueOf("0"));

                                            realm3.insertOrUpdate(invnuevo);
                                            Log.d("idinvNUEVOCREADO", invnuevo +"");
                                        }

                                    realm3.close();
                                }
                            });


                            // OBTENER NUEVO AMOUNT_DIST
                            final Double nuevoAmountDevuelto =  cantidadDevolver + amount_dist_inventario;
                            Log.d("nuevoAmount",nuevoAmountDevuelto+"");

                          // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT_DIST EN EL INVENTARIO
                            final Realm realm2 = Realm.getDefaultInstance();
                            realm2.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm2) {
                                    Inventario inv_actualizado = realm2.where(Inventario.class).equalTo("id", idInvetarioSelec).findFirst();
                                    inv_actualizado.setAmount_dist(String.valueOf(nuevoAmountDevuelto));
                                    realm2.insertOrUpdate(inv_actualizado);
                                    realm2.close();
                                }
                            });




                        }
                        // TRANSACCIÓN BD PARA BORRAR LA FACTURA
                        final Realm realm4 = Realm.getDefaultInstance();
                        realm4.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm4) {
                                RealmResults<Venta> result = realm4.where(Venta.class).equalTo("invoice_id", facturaID).findAll();
                                result.deleteAllFromRealm();
                               // Log.d("RealmResultsVenta", result + "");
                                realm4.close();
                            }

                        });
                        notifyDataSetChanged();

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
