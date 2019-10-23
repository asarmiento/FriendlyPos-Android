package com.friendlypos.preventas.adapters;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.fragment.PrevResumenFragment;
import com.friendlypos.preventas.modelo.Bonuses;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class PrevResumenAdapter extends RecyclerView.Adapter<PrevResumenAdapter.CharacterViewHolder> {
    private List<Pivot> productosList;
    private PreventaActivity activity;
    private static ArrayList<Pivot> aListdata = new ArrayList<Pivot>();
    private int selected_position1 = -1;
    Double amount_inventario = 0.0;
    int idInvetarioSelec;
    int nextId;
    private PrevResumenFragment fragment;
    private static double productosDelBonus = 0;

    public PrevResumenAdapter(PreventaActivity activity, PrevResumenFragment fragment, List<Pivot> productosList) {
        this.productosList = productosList;
        this.activity = activity;
        this.fragment = fragment;
    }

    public PrevResumenAdapter() {

    }

    public void updateData(List<Pivot> productosList) {
        activity.cleanTotalize();
        this.productosList = productosList;
        notifyDataSetChanged();

    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_distribucion_resumen, parent, false);
        return new PrevResumenAdapter.CharacterViewHolder(view);

    }

    private String getProductDescriptionByPivotId(String id) {
        Realm realm = Realm.getDefaultInstance();
        String description = realm.where(Productos.class).equalTo("id", id).findFirst().getDescription();
        realm.close();
        return description;
    }

    @Override
    public void onBindViewHolder(PrevResumenAdapter.CharacterViewHolder holder, final int position) {

        final Pivot pivot = productosList.get(position);
        String pivotTotal;
        double amountBonif;
        holder.txt_resumen_factura_nombre.setText(getProductDescriptionByPivotId(pivot.getProduct_id()));
        holder.txt_resumen_factura_precio.setText("P: " + Double.valueOf(pivot.getPrice()));
        holder.txt_resumen_factura_descuento.setText("Descuento de: " + Double.valueOf(pivot.getDiscount()));
        holder.txt_resumen_factura_cantidad.setText("C: " + Double.parseDouble(pivot.getAmount()));

            Realm realm0 = Realm.getDefaultInstance();
            String bonus = realm0.where(Productos.class).equalTo("id", pivot.getProduct_id()).findFirst().getBonus();
            realm0.close();

        if (bonus.equals("1")){

            final Realm realmBonus = Realm.getDefaultInstance();

            realmBonus.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realmBonus) {

                    Bonuses productoConBonus = realmBonus.where(Bonuses.class).equalTo("product_id", Integer.valueOf(pivot.getProduct_id())).findFirst();
                    productosDelBonus = Double.parseDouble(productoConBonus.getProduct_bonus());

                    Log.d("BONIFTOTAL", productoConBonus.getProduct_id() +  " " + productosDelBonus);

                }
            });
            amountBonif = Double.parseDouble(pivot.getAmount()) - productosDelBonus;
            pivotTotal = String.format("%,.2f", (Double.valueOf(pivot.getPrice()))* amountBonif);
            Log.d("BONIFTOTALPIVOT", pivotTotal +  " ");
        }
        else{
            pivotTotal = String.format("%,.2f", (Double.valueOf(pivot.getPrice()) * Double.parseDouble(pivot.getAmount())));
            Log.d("TOTALPIVOT", pivotTotal +  " ");
        }

        holder.txt_resumen_factura_total.setText("T: " + pivotTotal);

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

        private TextView txt_resumen_factura_nombre, txt_resumen_factura_descuento, txt_resumen_factura_precio, txt_resumen_factura_cantidad, txt_resumen_factura_total;
        protected CardView cardView;
        ImageButton btnEliminarResumen;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumen);
            txt_resumen_factura_nombre = (TextView) view.findViewById(R.id.txt_resumen_factura_nombre);
            txt_resumen_factura_descuento = (TextView) view.findViewById(R.id.txt_resumen_factura_descuento);
            txt_resumen_factura_precio = (TextView) view.findViewById(R.id.txt_resumen_factura_precio);
            txt_resumen_factura_cantidad = (TextView) view.findViewById(R.id.txt_resumen_factura_cantidad);
            txt_resumen_factura_total = (TextView) view.findViewById(R.id.txt_resumen_factura_total);
            btnEliminarResumen = (ImageButton) view.findViewById(R.id.btnEliminarResumen);

            btnEliminarResumen.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    activity.cleanTotalize();
                    int pos = getAdapterPosition();

                    notifyItemChanged(selected_position1);
                    selected_position1 = getAdapterPosition();
                    notifyItemChanged(selected_position1);

                    final Pivot clickedDataItem = productosList.get(pos);

                    final int resumenProductoId = clickedDataItem.getId();
                    final double cantidadProducto = Double.parseDouble(clickedDataItem.getAmount());

                    Toast.makeText(view.getContext(), "Se borró el producto" , Toast.LENGTH_SHORT).show();

                    // TRANSACCIÓN BD PARA BORRAR EL CAMPO

                    activity.initProducto(pos);

                    notifyDataSetChanged();
                    fragment.updateData();

                }
            });
        }

    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}

