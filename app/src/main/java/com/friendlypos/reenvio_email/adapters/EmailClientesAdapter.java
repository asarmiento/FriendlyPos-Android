package com.friendlypos.reenvio_email.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
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
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.modelo.EnviarFactura;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.login.modelo.UserError;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.helpers.DescargasHelper;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.reenvio_email.activity.EmailActivity;
import com.friendlypos.reenvio_email.modelo.EmailResponse;
import com.friendlypos.reenvio_email.modelo.customer;
import com.friendlypos.reenvio_email.modelo.email_Id;
import com.friendlypos.reenvio_email.modelo.invoices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

import static android.content.ContentValues.TAG;

public class EmailClientesAdapter extends RecyclerView.Adapter<EmailClientesAdapter.CharacterViewHolder> {
    private EmailActivity activity;
    public List<Clientes> contentList;
    int activa = 0;
    private int selected_position = -1;
    private static Context QuickContext = null;
    String idCliente;
    private RequestInterface mAPIService;
    String token;
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    int codigo;
    public String respuestaServer;
    customer codigoS;
    List<invoices> mensajeS;
    String resultS;
    int codigoServer;
    ArrayList<invoices> mContentsArray;
    ArrayList<customer> mContentsArray2;
    private Realm realm;
    int tabCliente;

    public EmailClientesAdapter(Context context, EmailActivity activity, List<Clientes> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        mAPIService = BaseManager.getApi();
        mContentsArray = new ArrayList<>();
        mContentsArray2 = new ArrayList<>();
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
                realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<invoices> result = realm.where(invoices.class).findAll();
                        result.deleteAllFromRealm();
                    }
                });

                token = "Bearer " + SessionPrefes.get(QuickContext).getToken();
                Log.d("tokenC", token + " ");

                activa = 1;

                /*final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                progresRing.setCancelable(true);*/

                final ProgressDialog progresRing;/* = ProgressDialog.show(QuickContext, "Cargando",
                                                        "Seleccionando Cliente", true);*/


                progresRing = new ProgressDialog(QuickContext);
                String message = "Seleccionando Cliente";
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



                if (isOnline()) {
                    Log.d("factura1", idCliente + " ");
                    tabCliente = 1;
                    activity.setSelecClienteTabEmail(tabCliente);
                    email_Id obj = new email_Id(idCliente);
                    Log.d("obj", obj + " ");
                    mAPIService.savePostEmail(obj, token).enqueue(new Callback<EmailResponse>() {

                        public void onResponse(Call<EmailResponse> call, Response<EmailResponse> response) {
                            mContentsArray.clear();


                            if(response.isSuccessful()) {

                                mContentsArray.addAll(response.body().getFacturas());

                                try {


                                    // Work with Realm
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(mContentsArray);
                                    realm.commitTransaction();
                                    //realm.close();
                                }
                                finally {
                                    realm.close();
                                }
                                Log.d("GuardarFacturas", mContentsArray.toString());

                              /*  Log.d("respuestaFactura",response.body().toString());
                                codigo = response.code();
                                codigoS = response.body().getCustomer();
                                mensajeS = response.body().getFacturas();*/
                              //  resultS= String.valueOf(response.body().isResult());

                            }
                            else{
                           /*     RealmResults<invoices> results = realm.where(invoices.class).findAll();
                                if (results.isEmpty()){
                                }else{
                                    mContentsArray.addAll(results);
                                }*/
                            }
                            progresRing.dismiss();
                        }


                        @Override
                        public void onFailure(Call<EmailResponse> call, Throwable t) {
                            progresRing.dismiss();
                            Log.e(TAG, "Unable to submit post to API.");
                        }
                    });}
                else{
                    tabCliente = 0;
                    activity.setSelecClienteTabEmail(tabCliente);
                    Functions.CreateMessage(activity, "Email", "Por favor revisar conexión de Internet antes de continuar");
                    progresRing.dismiss();
                }


/*
                 email_Id obj = new email_Id(idCliente);
                    Log.d("obj", obj + " ");

                Call<EmailResponse> call = mAPIService.savePostEmail(obj, token);

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

                            Log.d("EnviarEmail", response.body() + "");
                            Log.d("EnviarEmail", response.message() + "");
                            Log.d("EnviarEmail", response.code() + "");
                        }
                        session.guardarDatosUsuarioas(userId, password);
                        download1.descargarUsuarios(context);
                        entrarMenuPrincipal();

                        //showAppointmentsScreen();
                    }

                    @Override
                    public void onFailure(Call<EmailResponse> call, Throwable t) {
                        progresRing.dismiss();
                        showLoginError(t.getMessage());
                    }
                });
*/


            }
        });
        if(selected_position==position){
            holder.cardView.setBackgroundColor(Color.parseColor("#d1d3d4"));
        }
        else
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
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
    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(QuickContext);
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

