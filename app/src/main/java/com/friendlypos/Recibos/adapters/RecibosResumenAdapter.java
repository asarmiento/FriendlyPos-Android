package com.friendlypos.Recibos.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.fragments.RecibosResumenFragment;
import com.friendlypos.Recibos.modelo.Recibos;

import java.util.List;


public class RecibosResumenAdapter extends RecyclerView.Adapter<RecibosResumenAdapter.CharacterViewHolder> {

    private Context context;
    public List<Recibos> productosList;
    private RecibosActivity activity;
    private RecibosResumenFragment fragment;
    private static double producto_amount_dist_add = 0;
    private static double producto_descuento_add = 0;
    private int selected_position = -1;
    static double creditoLimiteCliente = 0.0;
    double totalCredito = 0.0;

    int nextId;

    public RecibosResumenAdapter(RecibosActivity activity, RecibosResumenFragment fragment, List<Recibos> productosList) {
        this.activity = activity;
        this.fragment = fragment;
        this.productosList = productosList;
    }

    public void updateData(List<Recibos> productosList) {
        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public RecibosResumenAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_recibos_resumen, parent, false);
        context = parent.getContext();
        return new RecibosResumenAdapter.CharacterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecibosResumenAdapter.CharacterViewHolder holder, final int position) {
     //   List<Pivot> pivots = getListResumen();
        final Recibos inventario = productosList.get(position);

        final String id = inventario.getInvoice_id();
        String numeracion = inventario.getNumeration();
        double total = inventario.getTotal();
        double pago = inventario.getPaid();

        holder.txt_producto_factura_idRecibos.setText(id);
        holder.txt_producto_factura_numeracionRecibos.setText(numeracion);
        holder.txt_producto_factura_TotalRecibos.setText("Total: " + total);
        holder.txt_producto_factura_PagoRecibos.setText("Pago: " + pago);
        holder.fillData(inventario);

       /* for (Pivot pivot : pivots) {
            if (producto.getId().equals(pivot.getProduct_id())) {
                Log.d("jd", "seteando color x lista");
                holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
                return;
            } else {
                Log.d("jd", "se limpia");
                holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
            }
        }*/

    }

/*
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
                            } else if (metodoPagoCliente.equals("2")) {
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
                                        pivotnuevo.setBonus(0);

                                        realm2.insertOrUpdate(pivotnuevo); // using insert API



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


                                Toast.makeText(context, "Se agregó el producto", Toast.LENGTH_LONG).show();


                            } else {

                                Toast.makeText(context, "Has excedido el monto del crédito", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "El producto no se agrego, verifique la cantidad que esta ingresando", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(context, "El producto no se agrego, El descuento debe ser >0 <11", Toast.LENGTH_LONG).show();
                    }

                    //      notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();

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
    */
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

        private TextView txt_producto_factura_numeracionRecibos, txt_producto_factura_idRecibos, txt_producto_factura_TotalRecibos, txt_producto_factura_PagoRecibos;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumenRecibos);
            txt_producto_factura_numeracionRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_numeracionRecibos);
            txt_producto_factura_idRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_idRecibos);
            txt_producto_factura_TotalRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_TotalRecibos);
            txt_producto_factura_PagoRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_PagoRecibos);

        }

        void fillData(final Recibos producto) {
            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;

                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                    Recibos clickedDataItem = productosList.get(pos);

                    String ProductoID = clickedDataItem.getInvoice_id();
                    String InventarioID = clickedDataItem.getCustomer_id();

                  /*  String precio = producto.getSale_price();
                    String precio2 = producto.getSale_price2();
                    String precio3 = producto.getSale_price3();
                    String precio4 = producto.getSale_price4();
                    String precio5 = producto.getSale_price5();

                    Double ProductoAmount = Double.valueOf(clickedDataItem.getAmount());

                    Realm realm1 = Realm.getDefaultInstance();
                    Productos producto = realm1.where(Productos.class).equalTo("id", ProductoID).findFirst();


                    String description = producto.getDescription();

                    realm1.close();

                    addProduct(InventarioID, ProductoID, ProductoAmount, description, precio, precio2, precio3, precio4, precio5);*/

                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}