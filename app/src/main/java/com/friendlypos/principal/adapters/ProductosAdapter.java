package com.friendlypos.principal.adapters;

/**
 * Created by DelvoM on 18/09/2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.friendlypos.R;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    private List<Productos.Product> android;

    public ProductosAdapter(List<Productos.Product> android) {
        this.android = android;
    }

    @Override
    public ProductosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_productos, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tv_name.setText(android.get(i).getDescription());
        viewHolder.tv_version.setText(android.get(i).getBarcode());
        viewHolder.tv_api_level.setText(android.get(i).getSalePrice());
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name,tv_version,tv_api_level;
        public ViewHolder(View view) {
            super(view);

            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_version = (TextView)view.findViewById(R.id.tv_version);
            tv_api_level = (TextView)view.findViewById(R.id.tv_api_level);

        }
    }

}