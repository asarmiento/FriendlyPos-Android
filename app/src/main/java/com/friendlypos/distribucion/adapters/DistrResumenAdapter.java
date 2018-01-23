package com.friendlypos.distribucion.adapters;


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
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrResumenAdapter extends RecyclerView.Adapter<DistrResumenAdapter.CharacterViewHolder> {
    private List<Pivot> productosList;
    private DistribucionActivity activity;
    private static ArrayList<Pivot> aListdata = new ArrayList<Pivot>();
    private int selected_position1 = -1;
    Double amount_inventario = 0.0;
    int idInvetarioSelec;

    private DistResumenFragment fragment;

    public DistrResumenAdapter(DistribucionActivity activity, DistResumenFragment fragment, List<Pivot> productosList) {
        this.productosList = productosList;
        this.activity = activity;
        this.fragment = fragment;
    }

    public DistrResumenAdapter() {

    }

    public void updateData(List<Pivot> productosList) {
        activity.cleanTotalize();
        this.productosList = productosList;
        notifyDataSetChanged();

    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_distribucion_resumen, parent, false);
        return new DistrResumenAdapter.CharacterViewHolder(view);

    }

    private String getProductDescriptionByPivotId(String id) {
        Realm realm = Realm.getDefaultInstance();
        String description = realm.where(Productos.class).equalTo("id", id).findFirst().getDescription();
        realm.close();
        return description;
    }

    @Override
    public void onBindViewHolder(DistrResumenAdapter.CharacterViewHolder holder, final int position) {

        final Pivot pivot = productosList.get(position);

        holder.txt_resumen_factura_nombre.setText(getProductDescriptionByPivotId(pivot.getProduct_id()));
        holder.txt_resumen_factura_precio.setText("P: " + Double.valueOf(pivot.getPrice()));
        holder.txt_resumen_factura_descuento.setText("Descuento de: " + Double.valueOf(pivot.getDiscount()));
        holder.txt_resumen_factura_cantidad.setText("C: " + Double.parseDouble(pivot.getAmount()));

        String pivotTotal = String.format("%,.2f", (Double.valueOf(pivot.getPrice()) * Double.parseDouble(pivot.getAmount())));

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

                    // Updating old as well as new positions
                    notifyItemChanged(selected_position1);
                    selected_position1 = getAdapterPosition();
                    notifyItemChanged(selected_position1);

                    final Pivot clickedDataItem = productosList.get(pos);

                    final int resumenProductoId = clickedDataItem.getId();
                    final double cantidadProducto = Double.parseDouble(clickedDataItem.getAmount());

                    Toast.makeText(view.getContext(), "You clicked " + resumenProductoId, Toast.LENGTH_SHORT).show();

                    // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
                    Realm realm3 = Realm.getDefaultInstance();
                    realm3.executeTransaction(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm3) {

                            Inventario inventario = realm3.where(Inventario.class).equalTo("product_id", clickedDataItem.getProduct_id()).findFirst();
                            idInvetarioSelec = inventario.getId();
                            amount_inventario = Double.valueOf(inventario.getAmount());
                            realm3.close();
                            Log.d("idinventario", idInvetarioSelec + "");
                        }
                    });

                    // OBTENER NUEVO AMOUNT
                    final Double nuevoAmountDevuelto = cantidadProducto + amount_inventario;
                    Log.d("nuevoAmount", nuevoAmountDevuelto + "");

                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT EN EL INVENTARIO
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

                    // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO CREDIT_LIMIT EN LA FACTURA
                /*    final Realm realm5 = Realm.getDefaultInstance();
                    realm5.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm5) {
                            Clientes inv_actualizado = realm5.where(Clientes.class).equalTo("id", ventas.getCustomer_id()).findFirst();
                            inv_actualizado.setCreditLimit(String.valueOf(credi));
                            realm5.insertOrUpdate(inv_actualizado);
                            realm5.close();
                        }
                    });*/

                    // TRANSACCIÓN BD PARA BORRAR EL CAMPO
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {

                        @Override
                        public void execute(Realm realm) {
                            RealmResults<Pivot> result = realm.where(Pivot.class).equalTo("id", resumenProductoId).findAll();
                            result.deleteAllFromRealm();
                            realm.close();
                        }

                    });
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

