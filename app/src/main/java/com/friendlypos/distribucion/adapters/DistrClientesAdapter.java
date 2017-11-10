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
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.distribucion.modelo.Venta;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

import io.realm.Realm;

public class DistrClientesAdapter extends RecyclerView.Adapter<DistrClientesAdapter.CharacterViewHolder> {

    public List<Venta> contentList;

    public DistrClientesAdapter(List<Venta> contentList) {
        this.contentList = contentList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_distribucion_clientes, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, final int position) {
        Venta venta = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();
        final String cardCliente = realm.where(Clientes.class).equalTo("id",venta.getCustomer_id()).findFirst().getCard();
        String companyCliente = realm.where(Clientes.class).equalTo("id", venta.getCustomer_id()).findFirst().getCompanyName();
        String fantasyCliente = realm.where(Clientes.class).equalTo("id", venta.getCustomer_id()).findFirst().getFantasyName();
        String numeracionFactura = realm.where(Facturas.class).equalTo("id", venta.getInvoice_id()).findFirst().getNumeration();

        holder.txt_cliente_factura_card.setText(cardCliente);
       holder.txt_cliente_factura_fantasyname.setText(fantasyCliente);
        holder.txt_cliente_factura_companyname.setText(companyCliente);
        holder.txt_cliente_factura_numeracion.setText("Factura: " + numeracionFactura);

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public void animateTo(List<Venta> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Venta> newModels) {
        for (int i = contentList.size() - 1; i >= 0; i--) {
            final Venta model = contentList.get(i);
            if (!newModels.contains(model)) {
                //removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Venta> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Venta model = newModels.get(i);
            if (!contentList.contains(model)) {
               // addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Venta> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Venta model = newModels.get(toPosition);
            final int fromPosition = contentList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
              //  moveItem(fromPosition, toPosition);
            }
        }

    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_cliente_factura_card,txt_cliente_factura_fantasyname,txt_cliente_factura_companyname, txt_cliente_factura_numeracion;
        protected CardView cardView;
        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewDistrClientes);
            txt_cliente_factura_card = (TextView)view.findViewById(R.id.txt_cliente_factura_card);
            txt_cliente_factura_fantasyname = (TextView)view.findViewById(R.id.txt_cliente_factura_fantasyname);
            txt_cliente_factura_companyname = (TextView)view.findViewById(R.id.txt_cliente_factura_companyname);
            txt_cliente_factura_numeracion = (TextView)view.findViewById(R.id.txt_cliente_factura_numeracion);

            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){

                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION){
                        view.setBackgroundColor(Color.parseColor("#607d8b"));
                        Venta clickedDataItem = contentList.get(pos);
                        String facturaID =  clickedDataItem.getId();
                        Toast.makeText(view.getContext(), "You clicked " + facturaID, Toast.LENGTH_SHORT).show();
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
