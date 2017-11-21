package com.friendlypos.distribucion.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrResumenAdapter extends RecyclerView.Adapter<DistrResumenAdapter.CharacterViewHolder> {
    private ArrayList<Pivot> data;
    private Context context;
    private List<Pivot> productosList;

    private static double iva = 13.0;
    private static int apply_done = 0;
    //IVA
    private static Double subExen = 0.0;
    private static Double subGrab = 0.0;
    private static Double subGrabm = 0.0;
    private static Double IvaT = 0.0;
    private static Double subt = 0.0;
    private static Double discountBill = 0.0;
    private static Double total = 0.0;

    private static String description;
    private static String tipo;
    private static String impuestoIVA;
    private static Double cantidad = 0.0;
    private static Double precio = 0.0;
    private static Double descuento= 0.0;
    private static Double clienteFixedDescuento = 0.0;

    public DistrResumenAdapter(List<Pivot> productosList) {

        this.productosList = productosList;
    }

    public ArrayList<Pivot> getData() {
        return data;
    }

    public void updateData(List<Pivot> productosList){
        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_distribucion_resumen, parent, false);

        context = parent.getContext();
        //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
        // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new DistrResumenAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistrResumenAdapter.CharacterViewHolder holder, final int position) {
        Pivot pivot = productosList.get(position);

        //todo repasar esto
        Realm realm = Realm.getDefaultInstance();
        Productos producto = realm.where(Productos.class).equalTo("id", pivot.getProduct_id()).findFirst();


        Venta ventas = realm.where(Venta.class).equalTo("invoice_id", pivot.getInvoice_id()).findFirst();

        Clientes clientes = realm.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();

        description = producto.getDescription();
        tipo = producto.getProduct_type_id();

        cantidad = Double.parseDouble(pivot.getAmount());
        precio = Double.valueOf(pivot.getPrice());
        descuento = Double.valueOf(pivot.getDiscount());
        clienteFixedDescuento = Double.valueOf(clientes.getFixedDiscount());

        holder.txt_resumen_factura_nombre.setText(description);
        holder.txt_resumen_factura_precio.setText("P: " + precio);
        holder.txt_resumen_factura_descuento.setText("Descuento de: " + descuento);
        holder.txt_resumen_factura_cantidad.setText("C: " + cantidad);

        String pivotTotal = String.format("%,.2f", (precio * cantidad));
        holder.txt_resumen_factura_total.setText("T: " + pivotTotal);

        realm.close();

        totalize();
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

    public static class CharacterViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_resumen_factura_nombre, txt_resumen_factura_descuento,txt_resumen_factura_precio, txt_resumen_factura_cantidad, txt_resumen_factura_total;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumen);
            txt_resumen_factura_nombre = (TextView) view.findViewById(R.id.txt_resumen_factura_nombre);
            txt_resumen_factura_descuento = (TextView) view.findViewById(R.id.txt_resumen_factura_descuento);
            txt_resumen_factura_precio = (TextView) view.findViewById(R.id.txt_resumen_factura_precio);
            txt_resumen_factura_cantidad = (TextView) view.findViewById(R.id.txt_resumen_factura_cantidad);
            txt_resumen_factura_total = (TextView) view.findViewById(R.id.txt_resumen_factura_total);
        }

    }

   public static void totalize() {

      if (tipo.equals("1")) {
            // subGrab = subGrab + (Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity));
            Log.d("tipoProdcuto", tipo + "gr");
            subGrab = subGrab + (precio) * (cantidad);
            String subTotalGrabado = String.format("%,.2f", subGrab);
            Log.d("subGrab", subGrab + "");
            Log.d("subTotalGrabado", subTotalGrabado);

            // subGrabm = subGrabm + ((Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity) - ((((Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.descount) / 100) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price))) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity))));
            subGrabm = subGrabm + ((precio) * (cantidad) - ((descuento / 100) * (precio) * (cantidad)));
            String subTotalGrabadoM = String.format("%,.2f", subGrabm);
            Log.d("subGrabm", subGrabm + "");
            Log.d("subTotalGrabadoM", subTotalGrabadoM);
        }

        else {
            Log.d("tipoProdcuto", tipo + "ex");
            subExen = subExen + ((precio) * (cantidad));
            String subTotalExento= String.format("%,.2f", subExen);

            Log.d("subExen", subExen + "");
            Log.d("subTotalExento", subTotalExento);
        }

       /*discountBill += ((((Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.descount) / 100) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price))) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity));

   }
    discountBill += ((subExen * (CurrentSale.costumer.fixed_discount / 100.00)) + (subGrabm * (CurrentSale.costumer.fixed_discount / 100.00)));
*/
       discountBill += ((descuento / 100) * (precio) * (cantidad));


        //  TODO REVISAR ANDROIDPOS EL IF QUE REVISA SI ESTA LLENO O NO
      // discountBill += ((subExen * (clienteFixedDescuento / 100.00)) + (subGrabm * (clienteFixedDescuento / 100.00)));
        String descuentoCliente= String.format("%,.2f", discountBill);
        Log.d("discountBill", discountBill + "");
        Log.d("descuentoCliente", descuentoCliente);


       if (subGrab > 0){
           IvaT = (subGrabm - (subGrabm * (clienteFixedDescuento / 100.00))) * (iva / 100);
            impuestoIVA = String.format("%,.2f", IvaT);

       Log.d("IvaT", IvaT + "mayor");
       Log.d("impuestoIVA", impuestoIVA);}
       else {
           IvaT = 0.0;
           impuestoIVA = String.format("%,.2f", IvaT);
           Log.d("IvaT", IvaT + "else");
           Log.d("impuestoIVA", impuestoIVA);
       }

       subt = subGrab + subExen;
       Log.d("subt", subt + "");
       total = (subt + IvaT) - discountBill;
       Log.d("total", total + "");

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

