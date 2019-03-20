package com.friendlypos.reenvio_email.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserError;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reenvio_email.activity.EmailActivity;
import com.friendlypos.reenvio_email.modelo.EmailResponse;
import com.friendlypos.reenvio_email.modelo.email_Id;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailClientesAdapter extends RecyclerView.Adapter<EmailClientesAdapter.CharacterViewHolder> {
    private EmailActivity activity;
    public List<Clientes> contentList;
    int activa = 0;
    private int selected_position = -1;
    private static Context QuickContext = null;
    String idCliente;
    private RequestInterface api;
    String token;

    public EmailClientesAdapter(Context context, EmailActivity activity, List<Clientes> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
    }

    @Override
    public EmailClientesAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_clientes_email, parent, false);

        return new EmailClientesAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmailClientesAdapter.CharacterViewHolder holder, final int position) {




        final Clientes content = contentList.get(position);


        holder.txt_cliente_card.setText(content.getCard());
        holder.txt_cliente_fantasyname.setText(content.getFantasyName());
        holder.txt_cliente_companyname.setText(content.getCompanyName());
        holder.txt_cliente_address.setText(content.getAddress());
        holder.txt_cliente_telefono.setText(content.getPhone());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                token = "Bearer " + SessionPrefes.get(QuickContext).getToken();
                Log.d("tokenC", token + " ");

                activa = 1;

                final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                progresRing.setCancelable(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {

                        }
                      //  progresRing.dismiss();
                    }
                }).start();

                int pos = position;
                if (pos == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                idCliente = content.getId();

                email_Id id = new email_Id(idCliente);
                String adswd = id.getCostumer();
                Log.d("EnviarEmaiasdasdl", adswd+ "");

                Toast.makeText(QuickContext, "idCliente "+ idCliente, Toast.LENGTH_LONG).show();

                api = BaseManager.getApi();
                Call<EmailResponse> call = api.savePostEmail(idCliente, token);


                call.enqueue(new Callback<EmailResponse>() {

                    @Override
                    public void onResponse(Call<EmailResponse> call, Response<EmailResponse> response) {
                        progresRing.dismiss();
                        // Procesar errores
                        if (!response.isSuccessful()) {
                            String error = "Ha ocurrido un error. Contacte al administrador";
                            if (response.errorBody()
                                    .contentType()
                                    .subtype()
                                    .equals("json")) {
                                UserError userError = UserError.fromResponseBody(response.errorBody());

                                error = userError.getMessage();
                                Log.d("LoginActivity", userError.getMessage());
                            }
                            else {
                                try {
                                    // Reportar causas de error no relacionado con la API
                                    Log.d("LoginActivity", response.errorBody().string());
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            showLoginError(error);}else {
                            Toast.makeText(QuickContext, "BIEN", Toast.LENGTH_LONG).show();

                            Log.d("EnviarEmail", response.body() + "");
                            Log.d("EnviarEmail", response.message() + "");
                            Log.d("EnviarEmail", response.code() + "");
                        }
                    /*    session.guardarDatosUsuarioas(userId, password);
                        download1.descargarUsuarios(context);
                        entrarMenuPrincipal();
*/
                        //showAppointmentsScreen();
                    }

                    @Override
                    public void onFailure(Call<EmailResponse> call, Throwable t) {
                        progresRing.dismiss();
                        showLoginError(t.getMessage());
                    }
                });



            }
        });
        if(selected_position==position){
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }

    }
    private void showLoginError(String error) {
        Toast.makeText(QuickContext, error, Toast.LENGTH_LONG).show();
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
        public CardView cardView;
        private TextView txt_cliente_card,txt_cliente_fantasyname,txt_cliente_companyname, txt_cliente_address,txt_cliente_creditlimit,
                txt_cliente_fixeddescount, txt_cliente_due,txt_cliente_credittime, txt_cliente_telefono;
        private ImageButton btnUbicacionCliente, btnEditarCliente;
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
            btnUbicacionCliente = (ImageButton)view.findViewById(R.id.btnUbicacionCliente);
            btnEditarCliente = (ImageButton)view.findViewById(R.id.btnEditarCliente);
            cardView = (CardView) view.findViewById(R.id.cardViewClientes);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

