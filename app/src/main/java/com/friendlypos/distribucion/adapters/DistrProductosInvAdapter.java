package com.friendlypos.distribucion.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.modelo.Inventario;

import java.util.List;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrProductosInvAdapter extends RecyclerView.Adapter<DistrProductosInvAdapter.CharacterViewHolder> {

    private Context context;
    private List<Inventario> productosList;

    public DistrProductosInvAdapter(List<Inventario> productosList) {

        this.productosList = productosList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_productos, parent, false);

        context = parent.getContext();
      //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
       // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new DistrProductosInvAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistrProductosInvAdapter.CharacterViewHolder holder, final  int position) {
        Inventario productos = productosList.get(position);

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addProduct();
            }
        });

        holder.tv_name.setText(productos.getProduct_id());
        holder.tv_version.setText(productos.getAmount());
        holder.tv_api_level.setText(productos.getAmount_dist());

     /*   holder.cardView.setOnClickListener(new View.OnClickListener(){
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
        });*/
    }


    public void addProduct() {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View promptView = layoutInflater.inflate(R.layout.promptamount, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);


        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertD.show();
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

        private TextView tv_name,tv_version,tv_api_level;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_version = (TextView)view.findViewById(R.id.tv_version);
            tv_api_level = (TextView)view.findViewById(R.id.tv_api_level);
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

