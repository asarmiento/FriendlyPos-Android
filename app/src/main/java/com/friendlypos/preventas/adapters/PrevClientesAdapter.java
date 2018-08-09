package com.friendlypos.preventas.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.modelo.Clientes;
import java.util.List;
import io.realm.Realm;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class PrevClientesAdapter extends RecyclerView.Adapter<PrevClientesAdapter.CharacterViewHolder> {

    public List<Clientes> contentList;
    private PreventaActivity activity;
    private int selected_position = -1;
    private static Context QuickContext = null;
    double latitude;
    double longitude;
    int nextId;
    int tabCliente = 0;
    int activa = 0;
    String metodoPagoId;
    String fecha;
    String idCliente;
    String nombreCliente;
    String usuer;
    SessionPrefes session;
    private static EditText txtObservaciones;
    String seleccion;
    GPSTracker gps;
    String observ;
    private static Double creditolimite = 0.0;
    private static Double descuentoFixed = 0.0;
    private static Double cleintedue = 0.0;
    private static Double credittime = 0.0;

    public PrevClientesAdapter(Context context, PreventaActivity activity, List<Clientes> contentList) {
        this.contentList = contentList;
        this.activity = activity;
        this.QuickContext = context;
        session = new SessionPrefes(context);
    }
    @Override
    public PrevClientesAdapter.CharacterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.lista_preventa_clientes, parent, false);

        return new PrevClientesAdapter.CharacterViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final PrevClientesAdapter.CharacterViewHolder holder, final int position) {

        Clientes content = contentList.get(position);

        creditolimite = Double.parseDouble(content.getCreditLimit());
        descuentoFixed = Double.parseDouble(content.getFixedDiscount());
        cleintedue = Double.parseDouble(content.getDue());
        credittime = Double.parseDouble(content.getCreditTime());
        final String cardCliente = content.getCard();
        String companyCliente = content.getCompanyName();
        String fantasyCliente = content.getFantasyName();

        holder.txt_prev_card.setText(cardCliente);
        holder.txt_prev_fantasyname.setText(fantasyCliente);
        holder.txt_prev_companyname.setText(companyCliente);
        holder.txt_prev_creditlimit.setText(String.format("%,.2f", (creditolimite)));
        holder.txt_prev_fixeddescount.setText(String.format("%,.2f", (descuentoFixed)));
        holder.txt_prev_due.setText(String.format("%,.2f", (cleintedue)));
        holder.txt_prev_credittime.setText(String.format("%,.2f", (credittime)));

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                int pos = position;
                if (pos == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                Clientes clickedDataItem = contentList.get(pos);

                idCliente = clickedDataItem.getId();
                Toast.makeText(QuickContext,"ID CLIENTE" + idCliente, Toast.LENGTH_LONG).show();
                nombreCliente = clickedDataItem.getName();
                final int creditoTime = Integer.parseInt(clickedDataItem.getCreditTime());
                final String creditoLimiteClienteP = clickedDataItem.getCreditLimit();
                final String dueClienteP = clickedDataItem.getDue();
                LayoutInflater layoutInflater = LayoutInflater.from(QuickContext);


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuickContext);
                View promptView = layoutInflater.inflate(R.layout.promptvisitado_preventa, null);
                final RadioButton rbcomprado = (RadioButton) promptView.findViewById(R.id.compradoBillVisitado);
                final RadioButton rbvisitado = (RadioButton) promptView.findViewById(R.id.visitadoBillVisitado);
                txtObservaciones = (EditText) promptView.findViewById(R.id.txtObservaciones);
                alertDialogBuilder.setView(promptView);


                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            obtenerLocalización();
                            fecha = Functions.getDate() + " " + Functions.get24Time();


                            if (!rbcomprado.isChecked() && !rbvisitado.isChecked()) {
                                Functions.CreateMessage(QuickContext, " ", "Debe seleccionar una opción");

                            } else {

                                if (rbvisitado.isChecked()) {
                                    seleccion = "2";
                                    metodoPagoId = "1";


                                  /*  if(!txtObservaciones.getText().toString().equals("")){
                                        observ = txtObservaciones.getText().toString();
                                    Toast.makeText(getApplicationContext(), "Input Accepted", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                    //added soft keyboard code to make keyboard go away.

                                } else{ // here edittext is empty.. show user a toast and return,
                                    Toast.makeText(getApplicationContext(), "Input is empty", Toast.LENGTH_SHORT).show();

                                }*/

                                 /*   if(isValidName(observ)){
                                        txtObservaciones.setError(null);
                                        actualizarClienteVisitado();
                                    }
                                    else if(!isValidName(observ)){
                                        txtObservaciones.setError("Campo requerido");
                                        txtObservaciones.requestFocus();
                                    }*/
                                    //notifyDataSetChanged();
                                   // agregar();

                                    /*activity.setActivoColorPreventa(activa);
                                    tabCliente = 1;
                                    activity.setSelecClienteTabPreventa(tabCliente);
                                    activity.setCreditoLimiteClientePreventa(creditoLimiteClienteP);
                                    activity.setDueClientePreventa(dueClienteP);

                                    //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                    activity.setInvoiceIdPreventa(nextId);
                                    activity.setMetodoPagoClientePreventa(metodoPagoId);

                                    final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                                    progresRing.setCancelable(true);
                                    new Thread(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(5000);
                                            }
                                            catch (Exception e) {

                                            }
                                            progresRing.dismiss();
                                        }
                                    }).start();*/

                                }
                                else if (rbcomprado.isChecked()) {
                                    txtObservaciones.setText("_");
                                    seleccion = "1";
                                    LayoutInflater layoutInflater = LayoutInflater.from(QuickContext);
                                    View promptView = layoutInflater.inflate(R.layout.promptclient_preventa, null);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuickContext);
                                    alertDialogBuilder.setView(promptView);
                                    final RadioButton rbcontado = (RadioButton) promptView.findViewById(R.id.contadoBill);
                                    final RadioButton rbcredito = (RadioButton) promptView.findViewById(R.id.creditBill);

                                    if (creditoTime == 0) {
                                        rbcredito.setVisibility(View.GONE);
                                    }

                                    alertDialogBuilder
                                            .setCancelable(true)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int id) {

                                                    fecha = Functions.getDate() + " " + Functions.get24Time();


                                                    if(!rbcontado.isChecked() && !rbcredito.isChecked()){
                                                        Functions.CreateMessage(QuickContext, " ", "Debe seleccionar una opción");

                                                    }
                                                    else {
                                                        if (rbcredito.isChecked()) {

                                                                // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CREDITO DE LA FACTURA
                                                                metodoPagoId = "2";
                                                                //   Functions.CreateMessage(QuickContext, " ", "Se cambio la factura a crédito" + metodoPagoId);
                                                                notifyDataSetChanged();
                                                                agregar();
                                                                tabCliente = 1;
                                                                activity.setSelecClienteTabPreventa(tabCliente);
                                                                activity.setCreditoLimiteClientePreventa(creditoLimiteClienteP);
                                                                activity.setDueClientePreventa(dueClienteP);

                                                                //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                                                activity.setInvoiceIdPreventa(nextId);
                                                                activity.setMetodoPagoClientePreventa(metodoPagoId);

                                                                final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                                                                progresRing.setCancelable(true);
                                                                new Thread(new Runnable() {

                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            Thread.sleep(5000);
                                                                        }
                                                                        catch (Exception e) {

                                                                        }
                                                                        progresRing.dismiss();
                                                                    }
                                                                }).start();
                                                            actualizarClienteVisitado();

                                                        } else if (rbcontado.isChecked()) {
                                                            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CONTADO DE LA FACTURA
                                                            metodoPagoId = "1";
                                                            //Functions.CreateMessage(QuickContext, " ", "Se cambio la factura a contado" + metodoPagoId);
                                                            notifyDataSetChanged();
                                                            agregar();
                                                            tabCliente = 1;
                                                            activity.setSelecClienteTabPreventa(tabCliente);
                                                            activity.setCreditoLimiteClientePreventa(creditoLimiteClienteP);
                                                            activity.setDueClientePreventa(dueClienteP);

                                                            //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                                            activity.setInvoiceIdPreventa(nextId);
                                                            activity.setMetodoPagoClientePreventa(metodoPagoId);

                                                            final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando", "Seleccionando Cliente", true);
                                                            progresRing.setCancelable(true);
                                                            new Thread(new Runnable() {

                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        Thread.sleep(5000);
                                                                    }
                                                                    catch (Exception e) {

                                                                    }
                                                                    progresRing.dismiss();
                                                                }
                                                            }).start();
                                                            actualizarClienteVisitado();
                                                        }

                                                    }
                                                }
                                            })
                                            .setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });


                                    AlertDialog alertSeg = alertDialogBuilder.create();
                                    alertSeg.show();

                                }


                        }
                        }

                    })
                    .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alertPrimero = alertDialogBuilder.create();
                alertPrimero.show();
                alertPrimero.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbvisitado.isChecked()) {

                        if(!txtObservaciones.getText().toString().isEmpty()){
                            actualizarClienteVisitado();
                        }
                        else{
                            txtObservaciones.setError("Campo requerido");
                            txtObservaciones.requestFocus();}
                    }
                        else{
                            Toast.makeText(getApplicationContext(), "Contado", Toast.LENGTH_SHORT).show();

                        }
                    }

                });
            }
        });

        if (selected_position == position) {

            holder.cardView.setCardBackgroundColor(Color.parseColor("#607d8b"));
        }
        else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#009688"));
        }

    }
    public void updateData() {
      /*  ColorActivo = session.getDatosColorActivo();
        if (ColorActivo == 1 && selected_position == -1) {
        }*/

    }
    public void agregar(){

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String idUsuario = usuarios.getId();
        realm.close();

        final Realm realm2 = Realm.getDefaultInstance();

        realm2.executeTransaction(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                // increment index
                                  /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();

                                    if(numeracion.getId()){}
*/
                Number numero = realm.where(Numeracion.class).equalTo("sale_type", "3").max("numeracion_numero");

                if (numero == null) {
                    nextId = 1;
                }
                else {
                    nextId = numero.intValue() + 1;
                }


            }
        });

        //TODO MODIFICAR CON EL IDS CONSECUTIVOS (FACTURA Y NUMERACION)
        activity.initCurrentInvoice(String.valueOf(nextId), "3", idUsuario + "01-"+ "000000"+nextId, 0.0, 0.0, Functions.getDate(), Functions.get24Time(),
                Functions.getDate(), Functions.get24Time(), Functions.getDate(), "3", metodoPagoId, "", "", "", "", "", "", "", "", "", "", "", "", fecha,
                "", "");

        activity.initCurrentVenta(String.valueOf(nextId), String.valueOf(nextId), idCliente, nombreCliente, "6", "3", "0", "0", fecha, fecha, "0", 1, 1, 1);

        final Realm realm5 = Realm.getDefaultInstance();
        realm5.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm5) {
                Numeracion numNuevo= new Numeracion(); // unmanaged
                numNuevo.setSale_type("3");
                numNuevo.setNumeracion_numero(nextId);

                realm5.insertOrUpdate(numNuevo);
                Log.d("idinvNUEVOCREADO", numNuevo +"");


            }

        });
        realm5.close();
       // realm2.close();
    }
    public void obtenerLocalización() {

        gps = new GPSTracker(activity);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

           /* messageTextView2.setText("Mi direccion es: \n"
                    + latitude + "log "  + longitude );
            // \n is for new line
            Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();*/
        }
        else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();


        }

    }

    protected void actualizarClienteVisitado() {

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
        String idUsuario = usuarios.getId();

        String clienteid = activity.getCurrentVenta().getCustomer_id();

       /* sale sale = realm.where(sale.class).equalTo("invoice_id", String.valueOf(nextId)).findFirst();
        String clienteid = sale.getCustomer_id();*/
        Log.d("ClienteVisitadoFact", String.valueOf(nextId) + "");
        Log.d("ClienteVisitadoClient", clienteid + "");
        realm.close();

        final Realm realm5 = Realm.getDefaultInstance();

        realm5.beginTransaction();
        Number currentIdNum = realm5.where(visit.class).max("id");

        if (currentIdNum == null) {
            nextId = 1;
        }
        else {
            nextId = currentIdNum.intValue() + 1;
        }


        visit visitadonuevo = new visit();

        visitadonuevo.setId(nextId);
        visitadonuevo.setCustomer_id(clienteid);
        visitadonuevo.setVisit(seleccion);
        visitadonuevo.setObservation(observ);
        visitadonuevo.setDate(Functions.getDate());
        visitadonuevo.setLongitud(longitude);
        visitadonuevo.setLatitud(latitude);
        visitadonuevo.setUser_id(idUsuario);
        visitadonuevo.setSubida(1);


        realm5.copyToRealmOrUpdate(visitadonuevo);
        realm5.commitTransaction();
        Log.d("ClienteVisitado", visitadonuevo + "");
        realm5.close();
    }

    private boolean isValidName(String name) {
        boolean check = false;
        if (name.matches("^[\\p{L} .'-]+$") && name.length() > 2) {
            check = true;
        }
        return check;
    }
    @Override
    public int getItemCount() {
        return contentList.size();
    }
    public class CharacterViewHolder extends RecyclerView.ViewHolder {


        private TextView txt_prev_card, txt_prev_fantasyname, txt_prev_companyname, txt_prev_creditlimit,
            txt_prev_fixeddescount, txt_prev_due, txt_prev_credittime;
        public CardView cardView;


        public CharacterViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardViewPreventaClientes);

            txt_prev_card = (TextView) view.findViewById(R.id.txt_prev_card);
            txt_prev_fantasyname = (TextView) view.findViewById(R.id.txt_prev_fantasyname);
            txt_prev_companyname = (TextView) view.findViewById(R.id.txt_prev_companyname);

            txt_prev_creditlimit = (TextView) view.findViewById(R.id.txt_prev_creditlimit);
            txt_prev_fixeddescount = (TextView) view.findViewById(R.id.txt_prev_fixeddescount);
            txt_prev_due = (TextView) view.findViewById(R.id.txt_prev_due);
            txt_prev_credittime = (TextView) view.findViewById(R.id.txt_prev_credittime);
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
