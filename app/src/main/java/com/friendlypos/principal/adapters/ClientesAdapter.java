package com.friendlypos.principal.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.principal.modelo.Clientes;

import java.util.List;

/**
 * Created by Podisto on 15/05/2016.
 */
public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.CharacterViewHolder> {

    private List<Clientes> contentList;

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

        holder.txt_cliente_card.setText(content.getCard());
        holder.txt_cliente_fantasyname.setText(content.getFantasyName());
        holder.txt_cliente_companyname.setText(content.getCompanyName());
        holder.txt_cliente_address.setText(content.getAddress());
        holder.txt_cliente_creditlimit.setText(content.getCreditLimit());
        holder.txt_cliente_fixeddescount.setText(content.getFixedDiscount());
        holder.txt_cliente_due.setText(content.getDue());
        holder.txt_cliente_credittime.setText(content.getCreditTime());

        Log.d("nombre", content.getFantasyName()+ "");
        Log.d("idasd", content.getId()+ "");
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public static class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_cliente_card,txt_cliente_fantasyname,txt_cliente_companyname, txt_cliente_address,txt_cliente_creditlimit,
                txt_cliente_fixeddescount, txt_cliente_due,txt_cliente_credittime;

        public CharacterViewHolder(View view) {
            super(view);
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
