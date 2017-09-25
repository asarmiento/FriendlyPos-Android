package com.friendlypos.principal.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.CharacterViewHolder> {

    private List<Productos> productosList;

    public ProductosAdapter(List<Productos> productosList) {
        this.productosList = productosList;
    }

    @Override
    public ProductosAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_productos, parent, false);

        return new ProductosAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosAdapter.CharacterViewHolder holder, int position) {
        Productos productos = productosList.get(position);
        holder.tv_name.setText(productos.getDescription());
        holder.tv_version.setText(productos.getBarcode());
        holder.tv_api_level.setText(productos.getSale_price());
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name,tv_version,tv_api_level;

        public CharacterViewHolder(View view) {
            super(view);
            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_version = (TextView)view.findViewById(R.id.tv_version);
            tv_api_level = (TextView)view.findViewById(R.id.tv_api_level);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

