package com.friendlypos.distribucion.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
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
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;

import static java.lang.String.valueOf;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrSeleccionarProductosAdapter extends RecyclerView.Adapter<DistrSeleccionarProductosAdapter.CharacterViewHolder> {

    private Context context;
    public List<Inventario> productosList;
    private DistribucionActivity activity;
    private static double producto_amount_dist_add = 0;
    private static double producto_descuento_add = 0;
    private int selected_position = -1;
    // private static String productoAmountDistAdd, productoDescuentoAdd, subTotalExento, descuentoCliente, subTotal, Total;
    String idProducto;
    int nextId;

    public DistrSeleccionarProductosAdapter(DistribucionActivity activity, List<Inventario> productosList) {
        this.activity = activity;
        this.productosList = productosList;
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
        //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
        // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DistrSeleccionarProductosAdapter.CharacterViewHolder holder, final int position) {
        final Inventario inventario = productosList.get(position);

        //todo repasar esto

        Realm realm = Realm.getDefaultInstance();
        Productos producto = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst();


        String description = producto.getDescription();
        String marca = producto.getBrand_id();
        String tipo = producto.getProduct_type_id();
        String precio = producto.getSale_price();


        String marca2 = realm.where(Marcas.class).equalTo("id", marca).findFirst().getName();
        String tipoProducto = realm.where(TipoProducto.class).equalTo("id", tipo).findFirst().getName();

        realm.close();

        holder.fillData(producto);
        holder.txt_producto_factura_nombre.setText(description);
        holder.txt_producto_factura_marca.setText("Marca: " + marca2);
        holder.txt_producto_factura_tipo.setText("Tipo: " + tipoProducto);
        holder.txt_producto_factura_precio.setText(precio);
        holder.txt_producto_factura_disponible.setText("Disp: " + inventario.getAmount_dist());
        holder.txt_producto_factura_seleccionado.setText("Selec: " + "0.0");
        holder.cardView.setBackgroundColor(selected_position == position ? Color.parseColor("#607d8b") : Color.parseColor("#009688"));


    }

    public void addProduct(final int inventario_id, final String producto_id, final Double cantidadDisponible, String Precio1, String Precio2, String Precio3, String Precio4, String Precio5) {

        final String idFacturaSeleccionada = (activity).getInvoiceId();
        Log.d("idFacturaSeleccionada", idFacturaSeleccionada + "");
        Log.d("idProductoSeleccionado", producto_id + "");


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.promptamount, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final TextView label = (TextView) promptView.findViewById(R.id.promtClabel);
        label.setText("Escriba una cantidad maxima de " + cantidadDisponible + " minima de 1");
        final EditText input = (EditText) promptView.findViewById(R.id.promtCtext);
        final EditText desc = (EditText) promptView.findViewById(R.id.promtCDesc);

       /* if (inv.product.salemethod.id == 2) {
            input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setRawInputType(Configuration.KEYBOARD_QWERTY);
        }*/

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

                            final Realm realm2 = Realm.getDefaultInstance();

                            realm2.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {

                                    // increment index
                                    Number currentIdNum = realm.where(Pivot.class).max("id");

                                    if (currentIdNum == null) {
                                        nextId = 1;
                                    } else {
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
                                public void execute(Realm realm) {

                                    Inventario inv_actualizado = realm3.where(Inventario.class).equalTo("id", inventario_id).findFirst();
                                    inv_actualizado.setAmount_dist(String.valueOf(nuevoAmount));

                                    realm3.insertOrUpdate(inv_actualizado); // using insert API


                                }
                            });
                            Toast.makeText(context, nextId + "agregocanti ", Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(context, "El producto no se agrego, verifique la cantidad que esta ingresando", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(context, "El producto no se agrego, El descuento debe ser >0 <11", Toast.LENGTH_LONG).show();
                    }

                    notifyDataSetChanged();

                } catch (Exception e) {
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

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return productosList.size();
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

                    Double ProductoAmount = Double.valueOf(clickedDataItem.getAmount_dist());

                    Toast.makeText(view.getContext(), "You clicked " + ProductoID, Toast.LENGTH_SHORT).show();
                    addProduct(InventarioID, ProductoID, ProductoAmount, precio, precio2, precio3, precio4, precio5);

                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

