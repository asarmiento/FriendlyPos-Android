package com.friendlypos.distribucion.adapters;


import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrProductosAdapter extends RecyclerView.Adapter<DistrProductosAdapter.CharacterViewHolder> {

    private List<Productos> productosList;

    public DistrProductosAdapter(List<Productos> productosList) {

        this.productosList = productosList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_productos, parent, false);

      //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
       // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new DistrProductosAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistrProductosAdapter.CharacterViewHolder holder, final  int position) {
        Productos productos = productosList.get(position);
        holder.txt_producto_nombre.setText(productos.getDescription());
        holder.txt_producto_codbarras.setText(productos.getBarcode());
        holder.txt_producto_marca.setText(productos.getSale_price());
        holder.txt_producto_tipo.setText(productos.getDescription());
        holder.txt_producto_stock.setText(productos.getStock_max());
        holder.txt_producto_inventario.setText(productos.getSale_price());
        holder.txt_producto_precio.setText(productos.getSale_price());

        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Here goes your desired onClick behaviour. Like:
                view.setBackgroundColor(Color.parseColor("#607d8b"));
                Toast.makeText(view.getContext(), "You have clicked " + position, Toast.LENGTH_SHORT).show(); //you can add data to the tag of your cardview in onBind... and retrieve it here with with.getTag().toString()..
                //You can change the fragment, something like this, not tested, please correct for your desired output:
            //    Activity activity = view.getContext();
             //   Fragment CityName = new CityName();
                //Create a bundle to pass data, add data, set the bundle to your fragment and:
             //   activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, cityName).addToBackStack(null).commit();     //Here m getting error
            }
        });
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

    public static class CharacterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

