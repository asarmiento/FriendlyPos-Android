package com.friendlypos.distribucion.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.adapters.ProductosAdapter;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrSeleccionarProductosAdapter extends RecyclerView.Adapter<DistrSeleccionarProductosAdapter.CharacterViewHolder> {

    private Context context;
    public List<Inventario> productosList;

    public DistrSeleccionarProductosAdapter(List<Inventario> productosList) {

        this.productosList = productosList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_distribucion_productos, parent, false);
        context = parent.getContext();
        //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
        // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistrSeleccionarProductosAdapter.CharacterViewHolder holder, final int position) {
        Inventario inventario = productosList.get(position);
/*
        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addProduct(inventario);
            }
        });
*/
        //todo repasar esto
        Realm realm = Realm.getDefaultInstance();
        String description = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst().getDescription();
        String marca = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst().getBrand_id();
        String tipo = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst().getProduct_type_id();
        String precio = realm.where(Productos.class).equalTo("id", inventario.getProduct_id()).findFirst().getSale_price();

        String marca2 = realm.where(Marcas.class).equalTo("id", marca).findFirst().getName();
        String tipoProducto = realm.where(TipoProducto.class).equalTo("id", tipo).findFirst().getName();

        realm.close();

        holder.txt_producto_factura_nombre.setText(description);
        holder.txt_producto_factura_marca.setText("Marca: " + marca2);
        holder.txt_producto_factura_tipo.setText("Tipo: " + tipoProducto);
        holder.txt_producto_factura_precio.setText(precio);
        holder.txt_producto_factura_disponible.setText("Disp: " + inventario.getAmount_dist());
        holder.txt_producto_factura_seleccionado.setText("Selec: " + "0.0");


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

    public void addProduct(String posicion) {

        final Inventario inv = new Inventario();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.promptamount, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);

        final TextView label = (TextView) promptView.findViewById(R.id.promtClabel);
        label.setText("Escriba una cantidad maxima de " + posicion + " minima de 1");
        final EditText input = (EditText) promptView.findViewById(R.id.promtCtext);
        final EditText desc = (EditText) promptView.findViewById(R.id.promtCDesc);

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

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_producto_factura_nombre, txt_producto_factura_marca,txt_producto_factura_tipo, txt_producto_factura_precio, txt_producto_factura_disponible, txt_producto_factura_seleccionado;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewSeleccionarProductos);
            txt_producto_factura_nombre = (TextView) view.findViewById(R.id.txt_producto_factura_nombre);
            txt_producto_factura_marca = (TextView) view.findViewById(R.id.txt_producto_factura_marca);
            txt_producto_factura_tipo = (TextView) view.findViewById(R.id.txt_producto_factura_tipo);
            txt_producto_factura_precio = (TextView) view.findViewById(R.id.txt_producto_factura_precio);
            txt_producto_factura_disponible = (TextView) view.findViewById(R.id.txt_producto_factura_disponible);
            txt_producto_factura_seleccionado = (TextView) view.findViewById(R.id.txt_producto_factura_seleccionado);
            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        view.setBackgroundColor(Color.parseColor("#607d8b"));
                        Inventario clickedDataItem = productosList.get(pos);
                        String ProductoID =  clickedDataItem.getId();
                        String ProductoID2 =  clickedDataItem.getProduct_id();
                        String ProductoAmount =  clickedDataItem.getAmount_dist();
                        Toast.makeText(view.getContext(), "You clicked " + ProductoID + ProductoID2, Toast.LENGTH_SHORT).show();
                        addProduct(ProductoAmount);
                    }

                    //Here goes your desired onClick behaviour. Like:

                    //     Toast.makeText(view.getContext(), "You have clicked " + cardCliente + position, Toast.LENGTH_SHORT).show(); //you can add data to the tag of your cardview in onBind... and retrieve it here with with.getTag().toString()..
                    //You can change the fragment, something like this, not tested, please correct for your desired output:
                    //    Activity activity = view.getContext();
                    //   Fragment CityName = new CityName();
                    //Create a bundle to pass data, add data, set the bundle to your fragment and:
                    //   activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, cityName).addToBackStack(null).commit();     //Here m getting error
                }
            });

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

