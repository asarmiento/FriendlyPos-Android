package com.friendlypos.principal.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.principal.modelo.Clientes;

import java.util.ArrayList;
import java.util.List;


public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.CharacterViewHolder>{

    public List<Clientes> contentList;
    private static Double creditolimite = 0.0;
    private static Double descuentoFixed = 0.0;
    private static Double cleintedue = 0.0;
    private static Double credittime = 0.0;

    public ClientesAdapter(List<Clientes> contentList) {
        this.contentList = contentList;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_clientes, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, int position) {
        Clientes content = contentList.get(position);

        creditolimite = Double.parseDouble(content.getCreditLimit());
        descuentoFixed =   Double.parseDouble(content.getFixedDiscount());
        cleintedue = Double.parseDouble(content.getDue());
        credittime =   Double.parseDouble(content.getCreditTime());

        holder.txt_cliente_card.setText(content.getCard());
        holder.txt_cliente_fantasyname.setText(content.getFantasyName());
        holder.txt_cliente_companyname.setText(content.getCompanyName());
        holder.txt_cliente_address.setText(content.getAddress());
        holder.txt_cliente_telefono.setText(content.getPhone());
        holder.txt_cliente_creditlimit.setText(String.format("%,.2f", (creditolimite)));
        holder.txt_cliente_fixeddescount.setText(String.format("%,.2f", (descuentoFixed)));
        holder.txt_cliente_due.setText(String.format("%,.2f", (cleintedue)));
        holder.txt_cliente_credittime.setText(String.format("%,.2f", (credittime)));
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public void setFilter(List<Clientes> countryModels){
        contentList = new ArrayList<>();
        contentList.addAll(countryModels);
        notifyDataSetChanged();
    }


    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_cliente_card,txt_cliente_fantasyname,txt_cliente_companyname, txt_cliente_address,txt_cliente_creditlimit,
                txt_cliente_fixeddescount, txt_cliente_due,txt_cliente_credittime, txt_cliente_telefono;

        public CharacterViewHolder(View view) {
            super(view);
            txt_cliente_card = (TextView)view.findViewById(R.id.txt_cliente_card);
            txt_cliente_fantasyname = (TextView)view.findViewById(R.id.txt_cliente_fantasyname);
            txt_cliente_companyname = (TextView)view.findViewById(R.id.txt_cliente_companyname);
            txt_cliente_address = (TextView)view.findViewById(R.id.txt_cliente_address);
            txt_cliente_telefono = (TextView)view.findViewById(R.id.txt_cliente_telefono);
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
