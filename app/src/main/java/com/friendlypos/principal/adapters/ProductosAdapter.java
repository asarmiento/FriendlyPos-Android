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
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_productos, parent, false);

      //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
       // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new ProductosAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductosAdapter.CharacterViewHolder holder, int position) {
        Productos productos = productosList.get(position);
        holder.tv_name.setText(productos.getDescription());
        holder.tv_version.setText(productos.getBarcode());
        holder.tv_api_level.setText(productos.getSale_price());

        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Here goes your desired onClick behaviour. Like:
                Toast.makeText(view.getContext(), "You have clicked " + view.getId(), Toast.LENGTH_SHORT).show(); //you can add data to the tag of your cardview in onBind... and retrieve it here with with.getTag().toString()..
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

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name,tv_version,tv_api_level;
        protected CardView cardView;
        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
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

