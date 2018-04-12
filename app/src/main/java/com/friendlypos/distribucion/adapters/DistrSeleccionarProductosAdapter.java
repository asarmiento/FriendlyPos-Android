package com.friendlypos.distribucion.adapters;


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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.TotalizeHelper;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrSeleccionarProductosAdapter extends RecyclerView.Adapter<DistrSeleccionarProductosAdapter.CharacterViewHolder> {

    private Context context;
    public List<Inventario> productosList;
    private DistribucionActivity activity;
    private DistSelecProductoFragment fragment;
    private DistResumenFragment fragment1;
    private static double producto_amount_dist_add = 0;
    private static double producto_descuento_add = 0;
    private int selected_position = -1;
    static double creditoLimiteCliente = 0.0;
    double totalCredito = 0.0;;
    TotalizeHelper totalizeHelper;

    int nextId;

    public DistrSeleccionarProductosAdapter(DistribucionActivity activity, DistSelecProductoFragment fragment, List<Inventario> productosList) {
        this.activity = activity;
        this.fragment = fragment;
        this.productosList = productosList;
        totalizeHelper = new TotalizeHelper(activity);
    }

    public void updateData(List<Inventario> productosList) {

        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.lista_distribucion_productos, parent, false);
        context = parent.getContext();
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DistrSeleccionarProductosAdapter.CharacterViewHolder holder, final int position) {

        final Inventario inventario = productosList.get(position);

        Realm realm = Realm.getDefaultInstance();
        Productos producto = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst();


        final String description = producto.getDescription();
        String marca = producto.getBrand_id();
        String tipo = producto.getProduct_type_id();
        String precio = producto.getSale_price();

        String marca2 = realm.where(Marcas.class).equalTo("id", marca).findFirst().getName();
        String tipoProducto = realm.where(TipoProducto.class).equalTo("id", tipo).findFirst().getName();

        realm.close();

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        final Realm realm3 = Realm.getDefaultInstance();

        try {
            realm3.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm3) {

                  //  Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("id", inventario_id).findFirst();
                  //  inv_actualizado.setAmount_dist(String.valueOf(nuevoAmount));
                    inventario.setNombre_producto(description);
                    realm3.insertOrUpdate(inventario); // using insert API

                    Log.d("asda", inventario.getNombre_producto());
                }

            });

        } catch (Exception e) {
            Log.e("error", "error", e);
            Toast.makeText(context,"error", Toast.LENGTH_SHORT).show();

        }
        realm3.close();
/*
        if(inventario.getAmount().equals("0")){

            holder.cardView.setVisibility(View.GONE);
        }else{
*/
        holder.fillData(producto);

        holder.txt_producto_factura_nombre.setText(description);
        holder.txt_producto_factura_marca.setText("Marca: " + marca2);
        holder.txt_producto_factura_tipo.setText("Tipo: " + tipoProducto);
        holder.txt_producto_factura_precio.setText(precio);
        holder.txt_producto_factura_disponible.setText("Disp: " + inventario.getAmount());
        holder.cardView.setBackgroundColor(selected_position == position ? Color.parseColor("#607d8b") : Color.parseColor("#009688"));
    }
    //}


    public void addProduct(final int inventario_id, final String producto_id, final
    Double cantidadDisponible, final String description, String Precio1, String Precio2,
                           String Precio3, String Precio4, String Precio5) {

        final String idFacturaSeleccionada = (activity).getInvoiceId();
        Log.d("idFacturaSeleccionada", idFacturaSeleccionada + "");
        Log.d("idProductoSeleccionado", producto_id + "");


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.promptamount, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final TextView txtNombreProducto = (TextView) promptView.findViewById(R.id.txtNombreProducto);
        txtNombreProducto.setText(description);

        final TextView label = (TextView) promptView.findViewById(R.id.promtClabel);
        label.setText("Escriba una cantidad maxima de " + cantidadDisponible + " minima de 1");
        final EditText input = (EditText) promptView.findViewById(R.id.promtCtext);
        final EditText desc = (EditText) promptView.findViewById(R.id.promtCDesc);


        final Spinner spPrices = (Spinner) promptView.findViewById(R.id.spPrices);
        ArrayList<Double> pricesList = new ArrayList<>();
        Double precio1 = Double.valueOf(Precio1);
        Double precio2 = Double.valueOf(Precio2);
        Double precio3 = Double.valueOf(Precio3);
        Double precio4 = Double.valueOf(Precio4);
        Double precio5 = Double.valueOf(Precio5);

        pricesList.add(precio1);

        if (precio2 != 0)
            pricesList.add(precio2);

        if (precio3 != 0)
            pricesList.add(precio3);

        if (precio4 != 0)
            pricesList.add(precio4);

        if (precio5 != 0)
            pricesList.add(precio5);

        ArrayAdapter<Double> pricesAdapter = new ArrayAdapter<Double>(label.getContext(), android.R.layout.simple_spinner_item, pricesList);
        spPrices.setAdapter(pricesAdapter);
        spPrices.setSelection(0);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                try {


                    producto_amount_dist_add = Double.parseDouble(((input.getText().toString().isEmpty()) ? "0" : input.getText().toString()));
                    Log.d("productoAmountDistAdd", producto_amount_dist_add + "");
                    //  productoAmountDistAdd = String.format("%,.2f", producto_amount_dist_add);

                    producto_descuento_add = Double.parseDouble(((desc.getText().toString().isEmpty()) ? "0" : desc.getText().toString()));
                    // productoDescuentoAdd = String.format("%,.2f", producto_descuento_add);
                    Log.d("productoDescuentoAdd", producto_descuento_add + "");

                    if (producto_descuento_add >= 0 && producto_descuento_add <= 10) {
                        Toast.makeText(context, "agregodesc", Toast.LENGTH_LONG).show();
                        if (producto_amount_dist_add > 0 && producto_amount_dist_add <= cantidadDisponible) {


                            final double precioSeleccionado = (double) spPrices.getSelectedItem();
                            Log.d("precioSeleccionado", precioSeleccionado + "");

                            //  CREDITO
                            String metodoPagoCliente = activity.getMetodoPagoCliente();
                            Double cred = Double.parseDouble(activity.getCreditoLimiteCliente());
                            if (metodoPagoCliente.equals("1")) {
                                creditoLimiteCliente = cred;
                                totalCredito = creditoLimiteCliente;
                                Log.d("ads", creditoLimiteCliente + "");
                            }
                            else if (metodoPagoCliente.equals("2")) {
                                double totalProducSlecc = precioSeleccionado * producto_amount_dist_add;
                                creditoLimiteCliente = cred;
                                totalCredito = creditoLimiteCliente - totalProducSlecc;
                                Log.d("ads", totalCredito + "");
                            }

                            // LIMITAR SEGUN EL LIMITE DEL CREDITO
                            if (totalCredito >= 0) {


                                final Realm realm2 = Realm.getDefaultInstance();

                                realm2.executeTransaction(new Realm.Transaction() {

                                    @Override
                                    public void execute(Realm realm) {

                                        // increment index
                                        Number currentIdNum = realm.where(Pivot.class).max("id");

                                        if (currentIdNum == null) {
                                            nextId = 1;
                                        }
                                        else {
                                            nextId = currentIdNum.intValue() + 1;
                                        }

                                        Pivot pivotnuevo = new Pivot(); // unmanaged
                                        pivotnuevo.setId(nextId);
                                        pivotnuevo.setInvoice_id(idFacturaSeleccionada);
                                        pivotnuevo.setProduct_id(producto_id);
                                        pivotnuevo.setPrice(String.valueOf(precioSeleccionado));
                                        pivotnuevo.setAmount(String.valueOf(producto_amount_dist_add));
                                        pivotnuevo.setDiscount(String.valueOf(producto_descuento_add));
                                        pivotnuevo.setDelivered(String.valueOf(producto_amount_dist_add));

                                        realm2.insertOrUpdate(pivotnuevo); // using insert API

                                    /*Pivot pivotnuevo = realm2.createObject(Pivot.class, nextId);

                                    */

                                    }
                                });

                                final Double nuevoAmount = cantidadDisponible - producto_amount_dist_add;
                                Log.d("nuevoAmount", nuevoAmount + "");


                                final Realm realm3 = Realm.getDefaultInstance();

                                realm3.executeTransaction(new Realm.Transaction() {

                                    @Override
                                    public void execute(Realm realm3) {

                                        Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("id", inventario_id).findFirst();
                                        inv_actualizado.setAmount(String.valueOf(nuevoAmount));

                                        realm3.insertOrUpdate(inv_actualizado); // using insert API
                                    }
                                });


                                // TRANSACCION PARA ACTUALIZAR EL CREDIT_LIMIT DEL CLIENTE
                                final Realm realm4 = Realm.getDefaultInstance();
                                realm4.executeTransaction(new Realm.Transaction() {

                                    @Override
                                    public void execute(Realm realm4) {

                                        final sale ventas = realm4.where(sale.class).equalTo("invoice_id", idFacturaSeleccionada).findFirst();
                                        Clientes clientes = realm4.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();
                                        Log.d("ads", clientes + "");
                                        clientes.setCreditLimit(String.valueOf(totalCredito));

                                        realm4.insertOrUpdate(clientes); // using insert API

                                        realm4.close();
                                        activity.setCreditoLimiteCliente(String.valueOf(totalCredito));

                                        fragment.updateData();
                                        List<Pivot> list = getListResumen();
                                        activity.cleanTotalize();
                                        totalizeHelper = new TotalizeHelper(activity);
                                        totalizeHelper.totalize(list);
                                        Log.d("listaResumenADD", list + "");

                                    }
                                });


                                Toast.makeText(context, nextId + "agregocanti ", Toast.LENGTH_LONG).show();


                            }
                            else {

                                Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(context, "El producto no se agrego, verifique la cantidad que esta ingresando", Toast.LENGTH_LONG).show();
                        }

                    }
                    else {
                        Toast.makeText(context, "El producto no se agrego, El descuento debe ser >0 <11", Toast.LENGTH_LONG).show();
                    }
                    notifyDataSetChanged();

                }
                catch (Exception e) {
                    e.printStackTrace();
                       /* Functions.createSnackBar(QuickContext, coordinatorLayout, "Sucedio un error Revise que el producto y sus dependientes tengan existencias", 2, Snackbar.LENGTH_LONG);
                        Functions.CreateMessage(QuickContext, "Error", e.getMessage());*/
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertD.show();
    }

    public List<Pivot> getListResumen() {
        String facturaId = activity.getInvoiceId();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Pivot> facturaid1 = realm.where(Pivot.class).equalTo("invoice_id", facturaId).findAll();
        realm.close();
        return facturaid1;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public void setFilter(List<Inventario> countryModels){

        productosList = new ArrayList<>();
        productosList.addAll(countryModels);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_producto_factura_nombre, txt_producto_factura_marca, txt_producto_factura_tipo, txt_producto_factura_precio, txt_producto_factura_disponible, txt_producto_factura_seleccionado;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewSeleccionarProductos);
            txt_producto_factura_nombre = (TextView) view.findViewById(R.id.txt_producto_factura_nombre);
            txt_producto_factura_marca = (TextView) view.findViewById(R.id.txt_producto_factura_marca);
            txt_producto_factura_tipo = (TextView) view.findViewById(R.id.txt_producto_factura_tipo);
            txt_producto_factura_precio = (TextView) view.findViewById(R.id.txt_producto_factura_precio);
            txt_producto_factura_disponible = (TextView) view.findViewById(R.id.txt_producto_factura_disponible);
            txt_producto_factura_seleccionado = (TextView) view.findViewById(R.id.txt_producto_factura_seleccionado);
        }

        void fillData(final Productos producto) {
            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;

                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                    Inventario clickedDataItem = productosList.get(pos);
                    String ProductoID = clickedDataItem.getProduct_id();
                    int InventarioID = clickedDataItem.getId();

                    String precio = producto.getSale_price();
                    String precio2 = producto.getSale_price2();
                    String precio3 = producto.getSale_price3();
                    String precio4 = producto.getSale_price4();
                    String precio5 = producto.getSale_price5();

                    Double ProductoAmount = Double.valueOf(clickedDataItem.getAmount());

                    Realm realm1 = Realm.getDefaultInstance();
                    Productos producto = realm1.where(Productos.class).equalTo("id", ProductoID).findFirst();


                    String description = producto.getDescription();

                    realm1.close();

                    Toast.makeText(view.getContext(), "You clicked " + ProductoID, Toast.LENGTH_SHORT).show();
                    addProduct(InventarioID, ProductoID, ProductoAmount, description, precio, precio2, precio3, precio4, precio5);

                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

