package com.friendlypos.Recibos.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.Recibos.fragments.RecibosResumenFragment;
import com.friendlypos.Recibos.modelo.recibos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delvo on 19/09/2018.
 */

public class RecibosResumenAdapter extends RecyclerView.Adapter<RecibosResumenAdapter.CharacterViewHolder> {
    private List<recibos> productosList;
    private RecibosActivity activity;
    private static ArrayList<recibos> aListdata = new ArrayList<recibos>();
    private int selected_position1 = -1;
    Double amount_inventario = 0.0;
    int idInvetarioSelec;
    int nextId;
    private RecibosResumenFragment fragment;

    public RecibosResumenAdapter(RecibosActivity activity, RecibosResumenFragment fragment, List<recibos> productosList) {
        this.productosList = productosList;
        this.activity = activity;
        this.fragment = fragment;
    }

    public RecibosResumenAdapter() {

    }

    public void updateData(List<recibos> productosList) {
        activity.cleanTotalize();
        this.productosList = productosList;
        notifyDataSetChanged();

    }

    @Override
    public RecibosResumenAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_recibos_resumen, parent, false);
        return new RecibosResumenAdapter.CharacterViewHolder(view);

    }


    @Override
    public void onBindViewHolder(RecibosResumenAdapter.CharacterViewHolder holder, final int position) {

        final recibos recibo = productosList.get(position);
        String numeracion = recibo.getNumeration();
        double montoPagado = recibo.getMontoCancelado();

        holder.txt_resumen_numeracionRecibos.setText("# de factura: " + numeracion);
        holder.txt_resumen_abonoRecibos.setText("Cantidad pagada: " + montoPagado);

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

        private TextView txt_resumen_numeracionRecibos, txt_resumen_abonoRecibos;
        protected CardView cardView;
        ImageButton btnEliminarResumenRecibos;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumenRecibos);
            txt_resumen_numeracionRecibos = (TextView) view.findViewById(R.id.txt_resumen_numeracionRecibos);
            txt_resumen_abonoRecibos = (TextView) view.findViewById(R.id.txt_resumen_abonoRecibos);

            btnEliminarResumenRecibos = (ImageButton) view.findViewById(R.id.btnEliminarResumenRecibos);

            btnEliminarResumenRecibos.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
/*
                   activity.cleanTotalize();
                    int pos = getAdapterPosition();

                    notifyItemChanged(selected_position1);
                    selected_position1 = getAdapterPosition();
                    notifyItemChanged(selected_position1);

                    final Pivot clickedDataItem = productosList.get(pos);

                    final int resumenProductoId = clickedDataItem.getId();
                    final double cantidadProducto = Double.parseDouble(clickedDataItem.getAmount());

                    Toast.makeText(view.getContext(), "Se borró el producto" , Toast.LENGTH_SHORT).show();

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


                    // TRANSACCIÓN BD PARA BORRAR EL CAMPO

                    final Realm realm5 = Realm.getDefaultInstance();
                    realm5.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm5) {
                            Pivot inv_actualizado = realm5.where(Pivot.class).equalTo("id", resumenProductoId).findFirst();
                            inv_actualizado.setDevuelvo(1);
                            realm5.insertOrUpdate(inv_actualizado);
                            realm5.close();
                        }
                    });

                    notifyDataSetChanged();
                    fragment.updateData();
*/
                    notifyDataSetChanged();

                }
            });
        }

    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}