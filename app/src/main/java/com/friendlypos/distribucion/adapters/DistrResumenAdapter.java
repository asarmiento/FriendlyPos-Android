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
import com.friendlypos.distribucion.modelo.Marcas;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.ProductoFactura;
import com.friendlypos.distribucion.modelo.TipoProducto;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by DelvoM on 21/09/2017.
 */

public class DistrResumenAdapter extends RecyclerView.Adapter<DistrResumenAdapter.CharacterViewHolder> {

    private Context context;
    private List<ProductoFactura> productosList;

    public DistrResumenAdapter(List<ProductoFactura> productosList) {

        this.productosList = productosList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_distribucion_resumen, parent, false);

        context = parent.getContext();
        //  CharacterViewHolder placeViewHolder = new CharacterViewHolder(view);
        // placeViewHolder.cardView.setOnClickListener(new ProductosAdapter(placeViewHolder, parent));
        return new DistrResumenAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistrResumenAdapter.CharacterViewHolder holder, final int position) {
        ProductoFactura pivot = productosList.get(position);

        //todo repasar esto
        Realm realm = Realm.getDefaultInstance();
        String description = realm.where(Productos.class).equalTo("id", pivot.getPivot().getProduct_id()).findFirst().getDescription();
        realm.close();

        holder.txt_resumen_factura_nombre.setText(description);
      /*  holder.txt_resumen_factura_descuento.setText("Descuento de: " + pivot.getPivot().getDiscount());
        holder.txt_resumen_factura_precio.setText("P: " + pivot.getPivot().getPrice());
        holder.txt_resumen_factura_cantidad.setText("C: " + pivot.getPivot().getAmount());

        String pivotTotal = pivot.getPivot().getAmount() + pivot.getPivot().getPrice();

        holder.txt_resumen_factura_total.setText("T: " + pivotTotal);
*/

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

        private TextView txt_resumen_factura_nombre, txt_resumen_factura_descuento,txt_resumen_factura_precio, txt_resumen_factura_cantidad, txt_resumen_factura_total;
        protected CardView cardView;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumen);
            txt_resumen_factura_nombre = (TextView) view.findViewById(R.id.txt_resumen_factura_nombre);
            txt_resumen_factura_descuento = (TextView) view.findViewById(R.id.txt_producto_factura_marca);
            txt_resumen_factura_precio = (TextView) view.findViewById(R.id.txt_producto_factura_tipo);
            txt_resumen_factura_cantidad = (TextView) view.findViewById(R.id.txt_producto_factura_precio);
            txt_resumen_factura_total = (TextView) view.findViewById(R.id.txt_producto_factura_disponible);
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

