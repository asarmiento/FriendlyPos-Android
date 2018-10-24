package com.friendlypos.principal.adapters;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.distribucion.util.TotalizeHelper;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.customer_location;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;


public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.CharacterViewHolder>{

    public List<Clientes> contentList;
    private static Double creditolimite = 0.0;
    private static Double descuentoFixed = 0.0;
    private static Double cleintedue = 0.0;
    private static Double credittime = 0.0;
    int activa = 0;
    private int selected_position = -1;
     double longitud, latitud;
    private static Context QuickContext = null;
    GPSTracker gps;
    double latitude;
    double longitude;
    int nextId;
   String idCliente;
    int obtenida = 0;
    public ClientesAdapter(Context context,List<Clientes> contentList) {
        this.contentList = contentList;
        this.QuickContext = context;
    }

    @Override
    public CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_clientes, parent, false);

        return new CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharacterViewHolder holder, final int position) {
        final Clientes content = contentList.get(position);

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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        progresRing.dismiss();
                    }
                }).start();

                int pos = position;
                if (pos == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                idCliente = content.getId();

             //   Toast.makeText(QuickContext, "idCliente "+ idCliente, Toast.LENGTH_LONG).show();

               longitud = content.getLongitud();
                latitud = content.getLatitud();

            }
        });
        if(selected_position==position){
            holder.cardView.setBackgroundColor(Color.parseColor("#607d8b"));
        }
        else
        {
            holder.cardView.setBackgroundColor(Color.parseColor("#009688"));
        }


        holder.btnUbicacionCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(activa == 1) {

                    if (longitud != 0.0 && latitud != 0.0) {

                        try {
                            String url = "https://waze.com/ul?ll="+ latitud + "," + longitud + "&navigate=yes";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            QuickContext.startActivity(intent);
                        } catch (ActivityNotFoundException ex) {

                            Uri gmmIntentUri = Uri.parse("geo:"+latitud + "," + longitud);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            QuickContext.startActivity(mapIntent);

                        }
                    } else {
                        Toast.makeText(QuickContext, "El cliente no cuenta con dirección GPS", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
                    Toast.makeText(QuickContext, "Selecciona un cliente primero", Toast.LENGTH_SHORT).show();

                }
            }
        });

        holder.btnEditarCliente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(activa == 1){
                    editarCliente();
                    }
                else{
                    Toast.makeText(QuickContext, "Selecciona una cliente primero", Toast.LENGTH_SHORT).show();

                }
            }
        });

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


    public void editarCliente() {

        LayoutInflater layoutInflater = LayoutInflater.from(QuickContext);
        View promptView = layoutInflater.inflate(R.layout.prompt_editar_clientes, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuickContext);
        alertDialogBuilder.setView(promptView);

        final TextView label = (TextView) promptView.findViewById(R.id.promtClabel);
        final Button btnObtenerGPS = (Button) promptView.findViewById(R.id.btnObtenerGPS);
        final TextView txtEditarLongitud = (TextView) promptView.findViewById(R.id.txtEditarLongitud);
        final TextView txtEditarLatitud = (TextView) promptView.findViewById(R.id.txtEditarLatitud);

        btnObtenerGPS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                obtenida = 1;
                obtenerLocalizacion();
                txtEditarLongitud.setText("Longitud: " + longitude);
                txtEditarLatitud.setText("Latitud: " + latitude);

            }
        });

        label.setText("Obtenga la nueva ubicación GPS");

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {


                if(obtenida == 1) {

                    try {

                        AlertDialog dialogReturnSale = new AlertDialog.Builder(QuickContext)
                                .setTitle("Guardar")
                                .setMessage("¿Desea cambiar la ubicación del cliente?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        final Realm realm5 = Realm.getDefaultInstance();

                                        realm5.beginTransaction();
                                        Number currentIdNum = realm5.where(customer_location.class).max("id_location");

                                        if (currentIdNum == null) {
                                            nextId = 1;
                                        } else {
                                            nextId = currentIdNum.intValue() + 1;
                                        }


                                        customer_location ubicacion = new customer_location();

                                        ubicacion.setId_location(nextId);
                                        ubicacion.setLatitud(latitude);
                                        ubicacion.setLongitud(longitude);
                                        ubicacion.setId_cliente(idCliente);
                                        ubicacion.setSubidaEdit(1);

                                        realm5.copyToRealmOrUpdate(ubicacion);
                                        realm5.commitTransaction();
                                        Log.d("UbicacionNueva", ubicacion + "");
                                        realm5.close();


                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                    }
                                }).create();
                        dialogReturnSale.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                       /* Functions.createSnackBar(QuickContext, coordinatorLayout, "Sucedio un error Revise que el producto y sus dependientes tengan existencias", 2, Snackbar.LENGTH_LONG);
                        Functions.CreateMessage(QuickContext, "Error", e.getMessage());*/
                    }
                }
                else{
                    Toast.makeText(QuickContext, "Obtenga la nueva dirección primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertD.show();
    }

    public void obtenerLocalizacion() {

        gps = new GPSTracker(QuickContext);
        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();



        }else{
            gps.showSettingsAlert();
        }

    }
}
