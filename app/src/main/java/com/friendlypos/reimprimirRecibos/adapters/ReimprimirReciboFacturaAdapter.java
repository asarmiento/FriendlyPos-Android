package com.friendlypos.reimprimirRecibos.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reimprimirRecibos.activity.ReimprimirRecibosActivity;

import java.util.List;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by Delvo on 03/12/2017.
 */

public class ReimprimirReciboFacturaAdapter extends RecyclerView.Adapter<ReimprimirReciboFacturaAdapter.CharacterViewHolder> {

    public List<receipts> contentList;
    private ReimprimirRecibosActivity activity;
    private int selected_position = -1;
    private static Context QuickContext = null;
    String facturaID;
    int tabCliente;

    public ReimprimirReciboFacturaAdapter(Context context, ReimprimirRecibosActivity activity, List<receipts> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_reimprimir_recibos_facturas, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, final int position) {

        final receipts receipt = contentList.get(position);

        Realm realm = Realm.getDefaultInstance();

        long cantidadPivot = 0;

        recibos nuevoRecibo = realm.where(recibos.class).equalTo("customer_id", receipt.getCustomer_id()).findFirst();


        Clientes clientes = realm.where(Clientes.class).equalTo("id", receipt.getCustomer_id()).findFirst();
        Log.d("nuevoRecibo", nuevoRecibo + "");

        final String cardCliente = clientes.getCard();
        String companyCliente = clientes.getCompanyName();
        String fantasyCliente = clientes.getFantasyName();

        String numeracionFactura = receipt.getNumeration();
        double totalFactura = receipt.getMontoPagado();

        holder.txt_resumen_numeracionRecibosFactura.setText("Número del Recibo: " +numeracionFactura);
        holder.txt_cliente_factura_referenciaRecibosFactura.setText("Referencia del Recibo: " +receipt.getReference());

        holder.txt_cliente_factura_fantasynameRecibosFactura.setText(fantasyCliente);
        holder.txt_cliente_factura_companynameRecibosFactura.setText(companyCliente);

        holder.cardView.setBackgroundColor(selected_position == position ? Color.parseColor("#d1d3d4") : Color.parseColor("#FFFFFF"));

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_resumen_numeracionRecibosFactura, txt_cliente_factura_referenciaRecibosFactura, txt_cliente_factura_fantasynameRecibosFactura,
                txt_cliente_factura_companynameRecibosFactura;
        protected CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewResumenRecibosFactura);
            txt_resumen_numeracionRecibosFactura = (TextView) view.findViewById(R.id.txt_resumen_numeracionRecibosFactura);
            txt_cliente_factura_referenciaRecibosFactura = (TextView) view.findViewById(R.id.txt_cliente_factura_referenciaRecibosFactura);
            txt_cliente_factura_fantasynameRecibosFactura= (TextView) view.findViewById(R.id.txt_cliente_factura_fantasynameRecibosFactura);
            txt_cliente_factura_companynameRecibosFactura= (TextView) view.findViewById(R.id.txt_cliente_factura_companynameRecibosFactura);

            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;

                    final ProgressDialog progresRing;/* = ProgressDialog.show(QuickContext, "Cargando",
                                                        "Seleccionando Cliente", true);*/


                    progresRing = new ProgressDialog(QuickContext);
                    String message = "Seleccionando Recibo";
                    String titulo = "Cargando";
                    SpannableString spannableString =  new SpannableString(message);
                    SpannableString spannableStringTitulo =  new SpannableString(titulo);

                    CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(QuickContext.getAssets(), "font/monse.otf"));
                    spannableString.setSpan(typefaceSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length(), Spanned.SPAN_PRIORITY);

                    progresRing.setTitle(spannableStringTitulo);
                    progresRing.setMessage(spannableString);
                    progresRing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progresRing.setIndeterminate(true);
                    progresRing.setCancelable(true);
                    progresRing.show();


                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            }
                            catch (Exception e) {

                            }
                            progresRing.dismiss();
                        }
                    }).start();



                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);

                    receipts clickedDataItem = contentList.get(pos);
                    facturaID = clickedDataItem.getReference();

                    Toast.makeText(view.getContext(), "You clicked " + facturaID, Toast.LENGTH_SHORT).show();

                    tabCliente = 1;
                    activity.setSelecReciboTab(tabCliente);
                    activity.setInvoiceIdReimprimirRecibo(facturaID);
                    Log.d("metodoPago", facturaID + "");


                }
            });

        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
