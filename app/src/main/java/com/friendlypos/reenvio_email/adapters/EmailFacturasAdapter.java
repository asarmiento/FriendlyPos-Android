package com.friendlypos.reenvio_email.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.reenvio_email.activity.EmailActivity;
import com.friendlypos.reenvio_email.fragment.EmailSelecFacturaFragment;
import com.friendlypos.reenvio_email.modelo.invoices;

import java.util.ArrayList;
import java.util.List;

public class EmailFacturasAdapter extends RecyclerView.Adapter<EmailFacturasAdapter.CharacterViewHolder> {

    private List<invoices> productosList;
    private EmailActivity activity;
    private static ArrayList<invoices> aListdata = new ArrayList<invoices>();
    private int selected_position1 = -1;
    Double amount_inventario = 0.0;
    int idInvetarioSelec;
    int nextId;
    private EmailSelecFacturaFragment fragment;

    public EmailFacturasAdapter(EmailActivity activity, EmailSelecFacturaFragment fragment, List<invoices> productosList) {
        this.productosList = productosList;
        this.activity = activity;
        this.fragment = fragment;
    }

    public EmailFacturasAdapter() {

    }

    public void updateData(List<invoices> productosList) {
        this.productosList = productosList;
        notifyDataSetChanged();
    }

    @Override
    public EmailFacturasAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_email_facturas, parent, false);
        return new EmailFacturasAdapter.CharacterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(EmailFacturasAdapter.CharacterViewHolder holder, final int position) {

        final invoices pivot = productosList.get(position);

        holder.txt_email_factura_numero.setText("Factura: " + pivot.getNumeration());
        holder.txt_email_factura_fecha.setText("Fecha: " + pivot.getDate());
        String pivotTotal = String.format("%,.2f", (Double.valueOf( pivot.getTotal_voucher())));
        holder.txt_email_factura_total.setText("Total: " +pivotTotal);

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

        private TextView txt_email_factura_numero, txt_email_factura_fecha, txt_email_factura_total, txt_resumen_factura_cantidad, txt_resumen_factura_total;
        protected CardView cardView;
        ImageButton btnReenviarFactura;

        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewEmailFactura);
            txt_email_factura_numero = (TextView) view.findViewById(R.id.txt_email_factura_numero);
            txt_email_factura_fecha = (TextView) view.findViewById(R.id.txt_email_factura_fecha);
            txt_email_factura_total = (TextView) view.findViewById(R.id.txt_email_factura_total);

            btnReenviarFactura = (ImageButton) view.findViewById(R.id.btnReenviarFactura);

            btnReenviarFactura.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                }
            });
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
