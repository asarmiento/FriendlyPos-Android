package com.friendlypos.reenvio_email.adapters;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.reenvio_email.activity.EmailActivity;
import com.friendlypos.reenvio_email.fragment.EmailSelecFacturaFragment;
import com.friendlypos.reenvio_email.modelo.SendEmailResponse;
import com.friendlypos.reenvio_email.modelo.invoices;
import com.friendlypos.reenvio_email.modelo.send_email_id;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class EmailFacturasAdapter extends RecyclerView.Adapter<EmailFacturasAdapter.CharacterViewHolder> {

    private List<invoices> productosList;
    private EmailActivity activity;
    private static ArrayList<invoices> aListdata = new ArrayList<invoices>();
    private int selected_position1 = -1;
    Double amount_inventario = 0.0;
    int idInvetarioSelec;
    int nextId;
    String token;
    private EmailSelecFacturaFragment fragment;
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private RequestInterface mAPIService;
    String codigoS;
    String mensajeS;
    String resultS;
    int codigo;
    public EmailFacturasAdapter(EmailActivity activity, EmailSelecFacturaFragment fragment, List<invoices> productosList) {
        this.productosList = productosList;
        this.activity = activity;
        this.fragment = fragment;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
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



                    AlertDialog dialogReturnSale = new AlertDialog.Builder(activity)
                            .setTitle("Reenviar Email")
                            .setMessage("¿Desea reenviar un email?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int pos = getAdapterPosition();
                                    // Updating old as well as new positions
                                    notifyItemChanged(selected_position1);
                                    selected_position1 = getAdapterPosition();
                                    notifyItemChanged(selected_position1);

                                    final invoices clickedDataItem = productosList.get(pos);
                                    final String facturaId = clickedDataItem.getId();
                                    Toast.makeText(activity, "Factura #" + facturaId, Toast.LENGTH_SHORT).show();

                                    token = "Bearer " + SessionPrefes.get(activity).getToken();
                                    Log.d("tokenC", token + " ");


                       if (isOnline()) {
                        Log.d("factura1", facturaId + " ");

                        send_email_id obj = new send_email_id(facturaId);
                        Log.d("obj", obj + " ");
                        mAPIService.savePostSendEmail(obj, token).enqueue(new Callback<SendEmailResponse>() {

                            public void onResponse(Call<SendEmailResponse> call, Response<SendEmailResponse> response) {

                                if(response.isSuccessful()) {

                                    Log.d("respuestaFactura",response.body().toString());
                                    codigo = response.code();
                                    Log.d("codigo",codigo+"");
                                    codigoS = response.body().getCode();
                                    Log.d("codigoS",codigoS+"");
                                    mensajeS = response.body().getMessage();
                                    Log.d("mensajeS",mensajeS+"");
                                    resultS= String.valueOf(response.body().isResult());
                                    Log.d("resultS",resultS+"");
                                    Toast.makeText(activity, mensajeS, Toast.LENGTH_LONG).show();

                                }
                                else{

                                }
                            }


                            @Override
                            public void onFailure(Call<SendEmailResponse> call, Throwable t) {
                                Log.e(TAG, "Unable to submit post to API.");
                            }
                        });}
                    else{
                        Toast.makeText(activity, "Error, por favor revisar conexión de Internet", Toast.LENGTH_SHORT).show();
                    }


                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            }).create();
                    dialogReturnSale.show();







                }
            });
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(activity);
    }
}
