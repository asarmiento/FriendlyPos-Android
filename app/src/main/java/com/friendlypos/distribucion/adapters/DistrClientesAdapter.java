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
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

public class DistrClientesAdapter extends RecyclerView.Adapter<DistrClientesAdapter.CharacterViewHolder> {

    private List<Clientes> contentList;

    public DistrClientesAdapter(List<Clientes> contentList) {
        this.contentList = contentList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_clientes, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, final int position) {
        Clientes content = contentList.get(position);

        holder.txt_cliente_card.setText(content.getCard());
        holder.txt_cliente_fantasyname.setText(content.getFantasyName());
        holder.txt_cliente_companyname.setText(content.getCompanyName());
        holder.txt_cliente_address.setText(content.getAddress());
        holder.txt_cliente_creditlimit.setText(content.getCreditLimit());
        holder.txt_cliente_fixeddescount.setText(content.getFixedDiscount());
        holder.txt_cliente_due.setText(content.getDue());
        holder.txt_cliente_credittime.setText(content.getCreditTime());

   /*     holder.cardView.setOnClickListener(new View.OnClickListener(){
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
    public int getItemCount() {
        return contentList.size();
    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_cliente_card,txt_cliente_fantasyname,txt_cliente_companyname, txt_cliente_address,txt_cliente_creditlimit,
                txt_cliente_fixeddescount, txt_cliente_due,txt_cliente_credittime;
        protected CardView cardView;
        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewDistrClientes);
            txt_cliente_card = (TextView)view.findViewById(R.id.txt_cliente_card);
            txt_cliente_fantasyname = (TextView)view.findViewById(R.id.txt_cliente_fantasyname);
            txt_cliente_companyname = (TextView)view.findViewById(R.id.txt_cliente_companyname);
            txt_cliente_address = (TextView)view.findViewById(R.id.txt_cliente_address);

            txt_cliente_creditlimit = (TextView)view.findViewById(R.id.txt_cliente_creditlimit);
            txt_cliente_fixeddescount = (TextView)view.findViewById(R.id.txt_cliente_fixeddescount);
            txt_cliente_due = (TextView)view.findViewById(R.id.txt_cliente_due);
            txt_cliente_credittime = (TextView)view.findViewById(R.id.txt_cliente_credittime);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
