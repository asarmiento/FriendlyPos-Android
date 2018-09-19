package com.friendlypos.Recibos.adapters;

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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.fragments.RecibosResumenFragment;
import com.friendlypos.Recibos.fragments.RecibosSeleccionarFacturaFragment;
import com.friendlypos.Recibos.modelo.Recibos;
import com.friendlypos.preventas.modelo.Bonuses;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

/**
 * Created by DelvoM on 13/09/2018.
 */

public class RecibosSeleccionarFacturaAdapter extends RecyclerView.Adapter<RecibosSeleccionarFacturaAdapter.CharacterViewHolder> {

    private Context context;
    public List<Recibos> productosList;
    private RecibosActivity activity;
    private RecibosSeleccionarFacturaFragment fragment;
    private static double producto_amount_dist_add = 0;
    private static double producto_descuento_add = 0;
    private int selected_position = -1;
    static double creditoLimiteCliente = 0.0;
    double totalPago = 0.0;
    double totalPagado = 0.0;
    double montoPagar;
    double debePagar = 0.0;
    double montoFaltante = 0.0;
    String facturaID, clienteID;
    int tabFactura;

    public RecibosSeleccionarFacturaAdapter(RecibosActivity activity, RecibosSeleccionarFacturaFragment fragment, List<Recibos> productosList) {
        this.activity = activity;
        this.fragment = fragment;
        this.productosList = productosList;
    }

    public void updateData(List<Recibos> productosList) {
        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public RecibosSeleccionarFacturaAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_recibos_seleccionar_factura, parent, false);
        context = parent.getContext();
        return new RecibosSeleccionarFacturaAdapter.CharacterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecibosSeleccionarFacturaAdapter.CharacterViewHolder holder, final int position) {
        final Recibos inventario = productosList.get(position);

        final String id = inventario.getInvoice_id();
        String numeracion = inventario.getNumeration();
        double total = inventario.getTotal();
        double pago = inventario.getPaid();


        holder.txt_producto_factura_numeracionRecibos.setText( "# de factura: " + numeracion);

        debePagar = total - pago;

        holder.txt_producto_factura_FaltanteRecibos.setText("Restante: " + String.format("%,.2f", debePagar));
        holder.txt_producto_factura_TotalRecibos.setText("Total: " + String.format("%,.2f", total));
        holder.txt_producto_factura_PagoRecibos.setText("Pagado: " + String.format("%,.2f", pago));
        holder.fillData(inventario);


        if(selected_position==position){
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }


    }


    public void addProduct(final String facturaID, final double totalPago, final double totalPagado, final double debePagar) {

      /*  final invoiceDetallePreventa invoiceDetallePreventa = activity.getCurrentInvoice();

        idDetallesFactura =  invoiceDetallePreventa.getP_id();
        Log.d("FACTURAIDDELEG", idDetallesFactura + "");
        // invoiceDetallePreventa.setP_code(weqweq);

        idFacturaSeleccionada = (activity).getInvoiceIdPreventa();
        Log.d("idFacturaSeleccionada", idFacturaSeleccionada + "");
        idProducto = producto_id;
        Log.d("idProductoSeleccionado", producto_id + "");
*/

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.promptrecibos, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);


        final TextView label = (TextView) promptView.findViewById(R.id.promtClabelRecibos);
        label.setText("Escriba un pago maximo de " + String.format("%,.2f", debePagar) + " minima de 1");
       // label.setText("Escriba la cantidad requerida del producto");


        final EditText input = (EditText) promptView.findViewById(R.id.promtCtextRecibos);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                try {
                    montoPagar = Double.parseDouble(((input.getText().toString().isEmpty()) ? "0" : input.getText().toString()));

                    final String facturaId = activity.getInvoiceIdRecibos();

                    if (montoPagar <= debePagar) {
                        Toast.makeText(context, "Pago " + montoPagar + " " + debePagar + " ", Toast.LENGTH_LONG).show();
                        montoFaltante = totalPagado + montoPagar;

                        final Realm realm2 = Realm.getDefaultInstance();
                        realm2.executeTransaction(new Realm.Transaction() {

                            @Override
                            public void execute(Realm realm2) {
                                Recibos recibo_actualizado = realm2.where(Recibos.class).equalTo("invoice_id", facturaId).findFirst();

                                recibo_actualizado.setPaid(montoFaltante);

                                realm2.insertOrUpdate(recibo_actualizado);
                                realm2.close();

                                Log.d("ACT RECIBO", recibo_actualizado + "");

                            }
                        });
                        activity.setTotalizarPagado(montoFaltante);
                        input.setText(" ");
                        notifyDataSetChanged();

                    }else{
                        Toast.makeText(context, "El monto agregado es mayor al monto de la factura", Toast.LENGTH_LONG).show();
                    }


                }
                catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }



             /*    try {

                   // TODO obtiene la cantidad del producto
                    producto_amount_dist_add = Double.parseDouble(((input.getText().toString().isEmpty()) ? "0" : input.getText().toString()));

                    // TODO obtiene el descuento del producto
                    producto_descuento_add = Double.parseDouble(((desc.getText().toString().isEmpty()) ? "0" : desc.getText().toString()));


                    if (producto_descuento_add >= 0 && producto_descuento_add <= 10) {

                        if (bonusProducto.equals("1")){
                            Log.d("idProductoBONIF", producto_id + "");

                            long fechaexp = fechaExpiracionBonus.getTime();
                            Log.d("fechaExpBONIF", fechaexp + "");

                            Calendar cal = Calendar.getInstance();
                            long hoy = cal.getTimeInMillis();
                            Log.d("fechaBONIF", hoy + "");


                            if(producto_amount_dist_add >= productosParaObtenerBonus ){

                                if(hoy <= fechaexp){
                                    Log.d("PRODOBTE", productosParaObtenerBonus + "");
                                    Log.d("PRODDELBO", productosDelBonus + "");
                                    Log.d("PRODADD", producto_amount_dist_add + "");

                                    double productos = producto_amount_dist_add / productosParaObtenerBonus;

                                    String prod = String.format("%.0f", productos);
                                    double productoBonusTotal = Double.parseDouble(prod) * productosDelBonus;


                                    Log.d("PROD DIV", productos + "");
                                    Log.d("PROD TOTAL", productoBonusTotal + "");

                                    producto_bonus_add =  producto_amount_dist_add + productoBonusTotal;
                                    Log.d("PRODUCTODELBONUS", producto_bonus_add + "");
                                    agregarBonificacion();
                                    Toast.makeText(context, "Se realizó una bonificación de " + productoBonusTotal + " productos", Toast.LENGTH_LONG).show();

                                }
                                else{
                                    Toast.makeText(context, "Fecha expirada para el bonus", Toast.LENGTH_LONG).show();
                                    agregar();
                                }
                            }
                            else{
                                Toast.makeText(context, "No alcanza la cantidad deseada para el bonus", Toast.LENGTH_LONG).show();
                                agregar();
                            }

                        }

                        else{
                            agregar();
                        }

                    }
                    else {
                        Toast.makeText(context, "El producto no se agrego, El descuento debe ser >0 <11", Toast.LENGTH_LONG).show();
                    }
                    notifyDataSetChanged();
                }
                catch (Exception e) {
                    e.printStackTrace();

                }*/

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

        private TextView txt_producto_factura_numeracionRecibos, txt_producto_factura_FaltanteRecibos, txt_producto_factura_TotalRecibos, txt_producto_factura_PagoRecibos;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewSelecFacRecibos);
            txt_producto_factura_numeracionRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_numeracionRecibos);
            txt_producto_factura_FaltanteRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_FaltanteRecibos);
            txt_producto_factura_TotalRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_TotalRecibos);
            txt_producto_factura_PagoRecibos = (TextView) view.findViewById(R.id.txt_producto_factura_PagoRecibos);

        }

       void fillData(final Recibos producto) {
            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                final ProgressDialog progresRing = ProgressDialog.show(context, "Cargando", "Seleccionando Factura", true);
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
                if (pos == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = getAdapterPosition();
                notifyItemChanged(selected_position);

                Recibos clickedDataItem = productosList.get(pos);


                facturaID = clickedDataItem.getInvoice_id();
                clienteID = clickedDataItem.getCustomer_id();

                totalPago = clickedDataItem.getTotal();
                totalPagado = clickedDataItem.getPaid();

                    debePagar = totalPago - totalPagado;
                    Log.d("debePagar",  debePagar + "");

                Toast.makeText(activity, facturaID + " " + clienteID + " " + totalPago, Toast.LENGTH_LONG).show();
                activity.setTotalFacturaSelec(totalPago);
                activity.setTotalizarPagado(totalPagado);

                tabFactura = 1;
                activity.setSelecFacturaTabRecibos(tabFactura);
                activity.setInvoiceIdRecibos(facturaID);


                    addProduct(facturaID, totalPago, totalPagado, debePagar);

            }
        });

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}