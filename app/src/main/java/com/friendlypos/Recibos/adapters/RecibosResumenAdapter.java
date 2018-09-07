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