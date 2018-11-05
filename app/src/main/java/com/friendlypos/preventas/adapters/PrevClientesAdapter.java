package com.friendlypos.preventas.adapters;

import android.app.Dialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.modelo.Clientes;
import com.friendlypos.principal.modelo.Productos;

import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;

public class PrevClientesAdapter extends RecyclerView.Adapter<PrevClientesAdapter.CharacterViewHolder> {

    public List<Clientes> contentList;
    private PreventaActivity activity;
    private int selected_position = -1;
    private static Context QuickContext = null;
    double latitude;
    double longitude;
    int nextId;
    int tabCliente = 0;
    String metodoPagoId;
    String fecha;
    String idCliente;
    String nombreCliente;
    String usuer;
    SessionPrefes session;
    private static EditText txtObservaciones;
    String seleccion, tipoFacturacion;
    GPSTracker gps;
    String observ;
    RadioButton rbcomprado, rbvisitado, rbfactPrev, rbfactProf;
    static Double creditolimite = 0.0;
    static Double descuentoFixed = 0.0;
    static Double cleintedue = 0.0;
    static Double credittime = 0.0;
    int creditoTime;
    String creditoLimiteClienteP, dueClienteP;
    String idUsuario;
    String numFactura;

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

                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);

                Clientes clickedDataItem = contentList.get(pos);

                idCliente = clickedDataItem.getId();
                nombreCliente = clickedDataItem.getName();
                final int creditoTime = Integer.parseInt(clickedDataItem.getCreditTime());
                final String creditoLimiteClienteP = clickedDataItem.getCreditLimit();
                final String dueClienteP = clickedDataItem.getDue();

                final Dialog dialogInicial = new Dialog(QuickContext);
                dialogInicial.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogInicial.setContentView(R.layout.promptvisitado_preventa);


                final Dialog dialogInicial1 = new Dialog(QuickContext);
                dialogInicial1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogInicial1.setContentView(R.layout.promptclient_proforma);

                dialogInicial1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                rbfactPrev = (RadioButton) dialogInicial1.findViewById(R.id.rbfactPrev);
                rbfactProf = (RadioButton) dialogInicial1.findViewById(R.id.rbfactProf);
                final Button btnOkProforma = (Button) dialogInicial1.findViewById(R.id.btnOKProforma);
                final Button btnOkPreventa = (Button) dialogInicial1.findViewById(R.id.btnOKPreventa);
                final Button btnCancelProforma = (Button) dialogInicial1.findViewById(R.id.btnCancelProforma);
                dialogInicial1.show();

                dialogInicial.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                rbcomprado = (RadioButton) dialogInicial.findViewById(R.id.compradoBillVisitado);
                rbvisitado = (RadioButton) dialogInicial.findViewById(R.id.visitadoBillVisitado);
                final Button btnOkVisitado = (Button) dialogInicial.findViewById(R.id.btnOKV);
                final Button btnOkComprado = (Button) dialogInicial.findViewById(R.id.btnOKC);
                final Button btnCancel = (Button) dialogInicial.findViewById(R.id.btnCancel);
                txtObservaciones = (EditText) dialogInicial.findViewById(R.id.txtObservaciones);

                RadioGroup yourRadioGroup2 = (RadioGroup) dialogInicial1.findViewById(R.id.rgTipo);
                yourRadioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        switch(checkedId)
                        {
                            case R.id.rbfactPrev:
                                btnOkPreventa.setVisibility(View.VISIBLE);
                                btnOkProforma.setVisibility(View.INVISIBLE);
                                break;
                            case R.id.rbfactProf:
                                btnOkPreventa.setVisibility(View.INVISIBLE);
                                btnOkProforma.setVisibility(View.VISIBLE);
                                break;

                        }
                    }
                });

                btnOkProforma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        tipoFacturacion = "Proforma";
                        activity.setSelecClienteFacturacionPreventa(tipoFacturacion);
                        dialogInicial1.dismiss();
                        rbcomprado.setText("Generar Proforma");
                        dialogInicial.show();


                    }
                });

                btnOkPreventa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        tipoFacturacion = "Preventa";
                        activity.setSelecClienteFacturacionPreventa(tipoFacturacion);
                        dialogInicial1.dismiss();
                        rbcomprado.setText("Generar Pedido");
                        dialogInicial.show();

                    }
                });

                btnCancelProforma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogInicial1.dismiss();
                    }
                });








                RadioGroup yourRadioGroup = (RadioGroup) dialogInicial.findViewById(R.id.rgTipoVisitado);
                yourRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        switch(checkedId)
                        {
                            case R.id.visitadoBillVisitado:
                                btnOkVisitado.setVisibility(View.VISIBLE);
                                btnOkComprado.setVisibility(View.INVISIBLE);
                                break;
                            case R.id.compradoBillVisitado:
                                btnOkVisitado.setVisibility(View.INVISIBLE);
                                btnOkComprado.setVisibility(View.VISIBLE);
                                break;

                        }
                    }
                });

                btnOkVisitado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        obtenerLocalización();


                        if(!txtObservaciones.getText().toString().isEmpty()){
                            observ = txtObservaciones.getText().toString();
                            seleccion = "2";

                            actualizarClienteVisitado();
                            dialogInicial.dismiss();
                        }
                        else{
                            txtObservaciones.setError("Campo requerido");
                            txtObservaciones.requestFocus();
                            }
                    }
                });

                btnOkComprado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogInicial.dismiss();
                        obtenerLocalización();
                        fecha = Functions.getDate() + " " + Functions.get24Time();
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
                                                txtObservaciones.setText(" ");
                                                observ = txtObservaciones.getText().toString();
                                                // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO METODO DE PAGO CREDITO DE LA FACTURA
                                                metodoPagoId = "2";
                                                notifyDataSetChanged();
                                                agregar();
                                                tabCliente = 1;
                                                activity.setSelecClienteTabPreventa(tabCliente);
                                                activity.setCreditoLimiteClientePreventa(creditoLimiteClienteP);
                                                activity.setDueClientePreventa(dueClienteP);

                                                //TODO MODIFICAR CON EL ID CONSECUTIVOS
                                                activity.setInvoiceIdPreventa(nextId);
                                                activity.setMetodoPagoClientePreventa(metodoPagoId);

                                                final ProgressDialog progresRing = ProgressDialog.show(QuickContext, "Cargando",
                                                        "Seleccionando Cliente", true);
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
                                                txtObservaciones.setText(" ");
                                                observ = txtObservaciones.getText().toString();
                                                metodoPagoId = "1";
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
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogInicial.dismiss();
                    }
                });
            }
        });


        if (selected_position == position) {
                if(rbfactProf.isChecked() || rbfactPrev.isChecked()){
                    if(rbcomprado.isChecked()){

            holder.cardView.setCardBackgroundColor(Color.parseColor("#607d8b"));
            }
}
        }
        else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#009688"));
        }

    }

    public void updateData() {

    }

    public void setFilter(List<Clientes> countryModels){

        contentList = new ArrayList<>();
        contentList.addAll(countryModels);
        notifyDataSetChanged();
    }

    public void agregar(){

        Realm realm = Realm.getDefaultInstance();
        usuer = session.getUsuarioPrefs();
        Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
     idUsuario = usuarios.getId();
        realm.close();

        if(tipoFacturacion.equals("Preventa")) {
            final Realm realm2 = Realm.getDefaultInstance();

            realm2.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                    Number numero = realm.where(Numeracion.class).equalTo("sale_type", "2").max("number");

                    if (numero == null) {
                        nextId = 1;
                    } else {
                        nextId = numero.intValue() + 1;
                    }
                    int valor = numero.intValue();

                    int length = String.valueOf(valor).length();

                    if(length == 1){
                        numFactura = idUsuario + "02-" + "000000" + nextId;
                    }
                    else if(length == 2){
                        numFactura = idUsuario + "02-" + "00000" + nextId;
                    }
                    else if(length == 3){
                        numFactura = idUsuario + "02-" + "0000" + nextId;
                    }
                    else if(length == 4){
                        numFactura = idUsuario + "02-" + "000" + nextId;
                    }
                    else if(length == 5){
                        numFactura = idUsuario + "02-" + "00" + nextId;
                    }
                    else if(length == 6){
                        numFactura = idUsuario + "02-" + "0" + nextId;
                    }
                    else if(length == 7){
                        numFactura = idUsuario + "02-" + nextId;
                    }



                }
            });

            activity.initCurrentInvoice(String.valueOf(nextId), "3", numFactura, 0.0, 0.0, Functions.getDate(), Functions.get24Time(),
                    Functions.getDate(), Functions.get24Time(), Functions.getDate(), "2", metodoPagoId, "", "", "", "", "", "", "", "", "", "", "", "", fecha,
                    "", "");

            activity.initCurrentVenta(String.valueOf(nextId), String.valueOf(nextId), idCliente, nombreCliente, "6", "3", "0", "0", fecha, fecha, "0", 1, 1, "Preventa");

            final Realm realm5 = Realm.getDefaultInstance();
            realm5.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm5) {
                    Numeracion numNuevo = new Numeracion(); // unmanaged
                    numNuevo.setSale_type("2");
                    numNuevo.setNumeracion_numero(nextId);
                    numNuevo.setRec_creada(1);
                    realm5.insertOrUpdate(numNuevo);
                    Log.d("idinvNUEVOCREADO", numNuevo + "");


                }

            });
            realm5.close();
        }
        else if (tipoFacturacion.equals("Proforma")){
            final Realm realm2 = Realm.getDefaultInstance();

            // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

            realm2.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {

                    Number numero = realm.where(Numeracion.class).equalTo("sale_type", "3").max("number");
                    if (numero == null) {
                        nextId = 1;
                    } else {
                        nextId = numero.intValue() + 1;
                    }

                    int valor = numero.intValue();

                    int length = String.valueOf(valor).length();

                    if(length == 1){
                        numFactura = idUsuario + "03-" + "000000" + nextId;
                    }
                    else if(length == 2){
                        numFactura = idUsuario + "03-" + "00000" + nextId;
                    }
                    else if(length == 3){
                        numFactura = idUsuario + "03-" + "0000" + nextId;
                    }
                    else if(length == 4){
                        numFactura = idUsuario + "03-" + "000" + nextId;
                    }
                    else if(length == 5){
                        numFactura = idUsuario + "03-" + "00" + nextId;
                    }
                    else if(length == 6){
                        numFactura = idUsuario + "03-" + "0" + nextId;
                    }
                    else if(length == 7){
                        numFactura = idUsuario + "03-" + nextId;
                    }
                }
            });

            //TODO MODIFICAR CON EL IDS CONSECUTIVOS (FACTURA Y NUMERACION)
            activity.initCurrentInvoice(String.valueOf(nextId), "3", numFactura, 0.0, 0.0, Functions.getDate(), Functions.get24Time(),
                    Functions.getDate(), Functions.get24Time(), Functions.getDate(), "3", metodoPagoId, "", "", "", "", "", "", "", "", "", "", "", "", fecha,
                    "", "");

            activity.initCurrentVenta(String.valueOf(nextId), String.valueOf(nextId), idCliente, nombreCliente, "6", "3", "0", "0", fecha, fecha, "0", 1, 1, "Proforma");

            final Realm realm5 = Realm.getDefaultInstance();
            realm5.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm5) {
                    Numeracion numNuevo = new Numeracion(); // unmanaged
                    numNuevo.setSale_type("3");
                    numNuevo.setNumeracion_numero(nextId);
                    numNuevo.setRec_creada(1);
                    realm5.insertOrUpdate(numNuevo);
                    Log.d("idinvNUEVOCREADO", numNuevo + "");


                }

            });
            realm5.close();
        }

    }
    public void obtenerLocalización() {

        gps = new GPSTracker(activity);

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

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

    //    String clienteid = activity.getCurrentVenta().getCustomer_id();

       /* sale sale = realm.where(sale.class).equalTo("invoice_id", String.valueOf(nextId)).findFirst();
        String clienteid = sale.getCustomer_id();*/
        Log.d("ClienteVisitadoFact", String.valueOf(nextId) + "");
        Log.d("ClienteVisitadoClient", idCliente + "");
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
        visitadonuevo.setCustomer_id(idCliente);
        visitadonuevo.setVisit(seleccion);
        visitadonuevo.setObservation(observ);
        visitadonuevo.setDate(Functions.getDate());
        visitadonuevo.setLongitud(longitude);
        visitadonuevo.setLatitud(latitude);
        visitadonuevo.setUser_id(idUsuario);
        visitadonuevo.setSubida(1);
        visitadonuevo.setTipoVisitado("PREV");

        realm5.copyToRealmOrUpdate(visitadonuevo);
        realm5.commitTransaction();
        Log.d("ClienteVisitado", visitadonuevo + "");
        realm5.close();
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
