package com.friendlypos.distribucion.adapters;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
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
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.lang.String.valueOf;

public class DistrClientesAdapter extends RecyclerView.Adapter<DistrClientesAdapter.CharacterViewHolder> {

    public List<Clientes> clientesList;
    public List<sale> contentList;
    private DistribucionActivity activity;
    //private boolean isSelected = false;
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

    public DistrClientesAdapter(Context context, DistribucionActivity activity, List<sale> contentList) {
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
        final sale sale = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();

        Clientes clientes = realm.where(Clientes.class).equalTo("id", sale.getCustomer_id()).findFirst();
        final invoice invoice = realm.where(invoice.class).equalTo("id", sale.getInvoice_id()).findFirst();

        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        final String fantasyCliente = clientes.getFantasyName();
        String numeracionFactura = invoice.getNumeration();
        String nombreVenta = sale.getCustomer_name();


        final double longitud = invoice.getLongitud();
        final double latitud = invoice.getLatitud();
        realm.close();

        final Realm realm3 = Realm.getDefaultInstance();

        try {
            realm3.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm3) {

                    sale.setNombreCliente(fantasyCliente);
                    realm3.copyToRealmOrUpdate(sale);
                    Log.d("invProdNombre", sale.getNombreCliente());
                }


            });

        } catch (Exception e) {
            Log.e("error", "error", e);
            Toast.makeText(QuickContext, "error", Toast.LENGTH_SHORT).show();


        }

        holder.txt_cliente_factura_card.setText(cardCliente);
        if (fantasyCliente.equals("Cliente Generico")){
            holder.txt_cliente_factura_fantasyname.setText(nombreVenta);
        }else{
            holder.txt_cliente_factura_fantasyname.setText(fantasyCliente);
        }
        holder.txt_cliente_factura_companyname.setText(companyCliente);
        holder.txt_cliente_factura_numeracion.setText("Factura: " + numeracionFactura);

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
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

        holder.btnUbicacionFacturaCliente.setOnClickListener(new View.OnClickListener() {

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

                /*    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                    startActivity(intent);*/
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
        });
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
            cardView = (CardView) view.findViewById(R.id.cardViewDistrClientes);
            txt_cliente_factura_card = (TextView) view.findViewById(R.id.txt_cliente_factura_card);
            txt_cliente_factura_fantasyname = (TextView) view.findViewById(R.id.txt_cliente_factura_fantasyname);
            txt_cliente_factura_companyname = (TextView) view.findViewById(R.id.txt_cliente_factura_companyname);
            txt_cliente_factura_numeracion = (TextView) view.findViewById(R.id.txt_cliente_factura_numeracion);
            btnDevolverFacturaCliente = (Button) view.findViewById(R.id.btnDevolverFacturaCliente);
            btnImprimirFacturaCliente = (ImageButton) view.findViewById(R.id.btnImprimirFacturaCliente);
            btnUbicacionFacturaCliente = (ImageButton) view.findViewById(R.id.btnUbicacionFacturaCliente);

        }
    }

    public void devolverFactura() {
        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                .setTitle("Devolución")
                .setMessage("¿Desea proceder con la devolución de la factura?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        tabCliente = 0;
                        activity.setSelecClienteTab(tabCliente);

                        Log.d("PRODUCTOSFACTURA1", facturaid1 + "");

                        for (int i = 0; i < facturaid1.size(); i++) {
                            final Pivot eventRealm = facturaid1.get(i);
                            final double cantidadDevolver = Double.parseDouble(eventRealm.getAmount());

                            Log.d("PRODUCTOSFACTURASEPA1", eventRealm + "");
                            Log.d("PRODUCTOSFACTURASEPA", cantidadDevolver + "");

                            final int resumenProductoId = eventRealm.getId();

                            // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                            Realm realm3 = Realm.getDefaultInstance();
                            realm3.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm3) {

                                    Inventario inventario = realm3.where(Inventario.class).equalTo("product_id", eventRealm.getProduct_id()).findFirst();

                                        if (inventario != null) {
                                            idInvetarioSelec = inventario.getId();
                                            amount_inventario = Double.valueOf(inventario.getAmount());
                                            Log.d("idinventario", idInvetarioSelec+"");

                                        } else {
                                            amount_inventario = 0.0;
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
                                            invnuevo.setInitial(String.valueOf("0"));
                                            invnuevo.setAmount(String.valueOf(cantidadDevolver));
                                            invnuevo.setAmount_dist(String.valueOf("0"));
                                            invnuevo.setDistributor(String.valueOf("0"));

                                            realm3.insertOrUpdate(invnuevo);
                                            Log.d("idinvNUEVOCREADO", invnuevo +"");
                                        }

                                    realm3.close();
                                }
                            });

                            final Realm realm5 = Realm.getDefaultInstance();
                            realm5.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm5) {
                                    Pivot inv_actualizado = realm5.where(Pivot.class).equalTo("id", resumenProductoId).findFirst();
                                    int dev = inv_actualizado.getDevuelvo();
                                    if(dev == 0){
                                        inv_actualizado.setDevuelvo(1);
                                        realm5.insertOrUpdate(inv_actualizado);
                                    }
                                    else{
                                        Log.d("devuelto", "ya esta 1");
                                    }


                                    realm5.close();
                                }
                            });


                            // OBTENER NUEVO AMOUNT_DIST
                            final Double nuevoAmountDevuelto =  cantidadDevolver + amount_inventario;
                            Log.d("nuevoAmount",nuevoAmountDevuelto+"");

                          // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT_DIST EN EL INVENTARIO
                            final Realm realm2 = Realm.getDefaultInstance();
                            realm2.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm2) {
                                    Inventario inv_actualizado = realm2.where(Inventario.class).equalTo("id", idInvetarioSelec).findFirst();
                                    inv_actualizado.setAmount(String.valueOf(nuevoAmountDevuelto));
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
                                RealmResults<sale> result = realm4.where(sale.class).equalTo("invoice_id", facturaID).findAll();
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

    public void setFilter(List<sale> countryModels){
        contentList = new ArrayList<>();
        contentList.addAll(countryModels);
        notifyDataSetChanged();
    }

}
