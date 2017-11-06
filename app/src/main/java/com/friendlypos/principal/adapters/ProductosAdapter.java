package com.friendlypos.principal.adapters;


import android.app.Activity;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.CharacterViewHolder> {

    private List<Productos> productosList;

    public ProductosAdapter(List<Productos> productosList) {

        this.productosList = productosList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_productos, parent, false);

      //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
       // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new ProductosAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosAdapter.CharacterViewHolder holder, final  int position) {
        Productos producto = productosList.get(position);
        //todo repasar esto
        Realm realm = Realm.getDefaultInstance();
        String marca = realm.where(Marcas.class).equalTo("id", producto.getBrand_id()).findFirst().getName();
        String tipoProducto = realm.where(TipoProducto.class).equalTo("id", producto.getProduct_type_id()).findFirst().getName();

        // TODO Revisar como poner el inventario,ya que el campo 1 es null y se cae la app
        String inventario = realm.where(Inventario.class).equalTo("product_id", "5").findFirst().getInitial();


        //String inventario2 = realm.where(Inventario.class).equalTo(producto.getId(), "product_id").findFirst().getInitial();

/*
        {
            "id": 359,
                "product_id": "5",
                "initial": "30",
                "amount": "0",
                "amount_dist": "30",
                "distributor": "0"
        },*/
        realm.close();

        holder.txt_producto_nombre.setText(producto.getDescription());
        holder.txt_producto_codbarras.setText(producto.getBarcode());
        holder.txt_producto_marca.setText(marca);
        holder.txt_producto_tipo.setText(tipoProducto);
        holder.txt_producto_stock.setText(producto.getStock_max());
        holder.txt_producto_inventario.setText(inventario);
        holder.txt_producto_precio.setText(producto.getSale_price());

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

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_producto_nombre,txt_producto_codbarras,txt_producto_marca, txt_producto_tipo, txt_producto_stock, txt_producto_inventario, txt_producto_precio;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewProductos);
            txt_producto_nombre = (TextView)view.findViewById(R.id.txt_producto_nombre);
            txt_producto_codbarras = (TextView)view.findViewById(R.id.txt_producto_codbarras);
            txt_producto_marca = (TextView)view.findViewById(R.id.txt_producto_marca);
            txt_producto_tipo = (TextView)view.findViewById(R.id.txt_producto_tipo);
            txt_producto_stock = (TextView)view.findViewById(R.id.txt_producto_stock);
            txt_producto_inventario = (TextView)view.findViewById(R.id.txt_producto_inventario);
            txt_producto_precio = (TextView)view.findViewById(R.id.txt_producto_precio);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

