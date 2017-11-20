package com.friendlypos.distribucion.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.ProductoFactura;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

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
        String description = realm.where(Productos.class).equalTo("id", pivot.getProduct_id()).findFirst().getDescription();
        Double cantidad = Double.parseDouble(pivot.getAmount());
        Double precio = Double.valueOf(pivot.getPrice());
        Double descuento = Double.valueOf(pivot.getDiscount());

        holder.txt_resumen_factura_nombre.setText(description);
        holder.txt_resumen_factura_precio.setText("P: " + precio);
        holder.txt_resumen_factura_descuento.setText("Descuento de: " + descuento);
        holder.txt_resumen_factura_cantidad.setText("C: " + cantidad);

        String pivotTotal = String.format("%,.2f", (precio * cantidad));
        holder.txt_resumen_factura_total.setText("T: " + pivotTotal);

        realm.close();
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

  /*  public static void totalize() {
        if (mAdapterBill.getData() != null && !mAdapterBill.getData().isEmpty()) {
            subGrab = 0.0;
            subGrabm = 0.0;
            subExen = 0.0;
            discountBill = 0.0;
            for (Pivot dbill : mAdapterBill.getData()) {
                //DecimalFormat twoDForm = new DecimalFormat("%,.2f");
                //System.out.println("Producto a ingresar " + product.getDescription() + " "+ product.getProductTypeId());
                if (dbill.inventories.product.producttype.name.equals("Gravado")) {
                    subGrab = subGrab + (Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity));
                    subGrabm = subGrabm + ((Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity) - ((((Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.descount) / 100) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price))) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity))));
                }
                else {
                    subExen = subExen + (Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity));
                }

                discountBill += ((((Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.descount) / 100) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.price))) * Functions.sGetDecimalStringAnyLocaleAsDouble(dbill.quantity));

            }
            discountBill += ((subExen * (CurrentSale.costumer.fixed_discount / 100.00)) + (subGrabm * (CurrentSale.costumer.fixed_discount / 100.00)));

            if (subGrab > 0)
                IvaT = Functions.sGetDecimalStringAnyLocaleAsDouble(String.format("%.2f", (subGrabm - (subGrabm * (CurrentSale.costumer.fixed_discount / 100.00))) * (iva / 100)));
            else
                IvaT = 0.0;

            subt = subGrab + subExen;
            total = (subt + IvaT) - discountBill;
        }
        else {
            subGrab = 0.0;
            subExen = 0.0;
            IvaT = 0.0;
            discountBill = 0.0;
            subt = subGrab + subExen;
            total = subt + IvaT;
        }
        //if (mAdapterBill.getData().size()>0 && spinnerPaymentMethods.getSelectedItemId()==1)
        //    paid.setEnabled(true);
    }
*/
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

