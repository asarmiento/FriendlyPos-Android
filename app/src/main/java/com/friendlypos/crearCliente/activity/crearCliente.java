package com.friendlypos.crearCliente.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.crearCliente.modelo.customer_new;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class crearCliente extends BluetoothActivity {

    @Bind(R.id.toolbarCrearCliente)
    Toolbar toolbarCrearCliente;

    @Bind(R.id.cliente_idtype_nuevo)
    EditText cliente_idtype_nuevo;

    @Bind(R.id.cliente_card_nuevo)
    EditText cliente_card_nuevo;

    @Bind(R.id.cliente_fe_nuevo)
    EditText cliente_fe_nuevo;

    @Bind(R.id.cliente_longitud_nuevo)
    TextView cliente_longitud_nuevo;

    @Bind(R.id.cliente_latitud_nuevo)
    TextView cliente_latitud_nuevo;

    @Bind(R.id.cliente_placa_nuevo)
    EditText cliente_placa_nuevo;

    @Bind(R.id.cliente_model_nuevo)
    EditText cliente_model_nuevo;

    @Bind(R.id.cliente_doors_nuevo)
    EditText cliente_doors_nuevo;

    @Bind(R.id.cliente_name_nuevo)
    EditText cliente_name_nuevo;

    @Bind(R.id.cliente_email_nuevo)
    EditText cliente_email_nuevo;

    @Bind(R.id.cliente_fantasyname_nuevo)
    EditText cliente_fantasyname_nuevo;

    @Bind(R.id.cliente_companyname_nuevo)
    EditText cliente_companyname_nuevo;

    @Bind(R.id.cliente_phone_nuevo)
    EditText cliente_phone_nuevo;

    @Bind(R.id.cliente_creditlimit_nuevo)
    EditText cliente_creditlimit_nuevo;

    @Bind(R.id.cliente_address_nuevo)
    EditText cliente_address_nuevo;

    @Bind(R.id.cliente_credittime_nuevo)
    EditText cliente_credittime_nuevo;

    @Bind(R.id.btnCrearCliente)
    Button btnCrearCliente;

    String idtype, card, fe, placa, model, doors, name, email, fantasyname, companyname, phone, creditlimit, address, credittime;
    double longitud, latitud;
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_crear_cliente);
        ButterKnife.bind(this);

        // Redirección al Login
        if (!SessionPrefes.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar(toolbarCrearCliente);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(crearCliente.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(crearCliente.this, MenuPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Log.d("ATRAS", "Atras");
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    private boolean isValidName(String name) {
        boolean check = false;
        if (name.matches("^[\\p{L} .'-]+$") && name.length() > 2) {
            check = true;
        }
        return check;
    }

    private boolean isValidMobile(String phone2) {
        boolean check = false;
        if (phone2.length() >= 8 && phone2.length() <= 11) {
            check = true;
        }
        return check;
    }

    public void onClickGo(View component) {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        switch (component.getId()) {
            case R.id.btnCrearCliente:

                idtype = cliente_idtype_nuevo.getText().toString();
                card = cliente_card_nuevo.getText().toString();
                fe = cliente_fe_nuevo.getText().toString();
                placa = cliente_placa_nuevo.getText().toString();

                model = cliente_model_nuevo.getText().toString();
                doors = cliente_doors_nuevo.getText().toString();
                name = cliente_name_nuevo.getText().toString();
                email = cliente_email_nuevo.getText().toString();

                fantasyname = cliente_fantasyname_nuevo.getText().toString();
                companyname = cliente_companyname_nuevo.getText().toString();
                phone = cliente_phone_nuevo.getText().toString();
                creditlimit = cliente_creditlimit_nuevo.getText().toString();

                address = cliente_address_nuevo.getText().toString();
                credittime = cliente_credittime_nuevo.getText().toString();


                if (
                        isValidEmail(email) && isValidMobile(phone)
                        && isValidName(name))
                {

                    if(latitude == 0.0){
                        if(longitude == 0.0){
                        enviarInfo();
                    }
                        Toast.makeText(crearCliente.this, "Obtenga la ubicación del cliente", Toast.LENGTH_LONG).show();
                    }

                } else if (!isValidName(name)) {
                    cliente_name_nuevo.setError("Nombre Invalido");
                    cliente_name_nuevo.requestFocus();
                } else if (!isValidEmail(email)) {
                    cliente_email_nuevo.setError("Correo Invalido");
                    cliente_email_nuevo.requestFocus();
                } else if (!isValidMobile(phone)) {
                    cliente_phone_nuevo.setError("Telefono Invalido");
                    cliente_phone_nuevo.requestFocus();
                }

                break;
            case R.id.btnUbicacionCliente:
                obtenerLocalización();
                cliente_longitud_nuevo.setText("Longitud: " +longitude );
                cliente_latitud_nuevo.setText("Latitud: " +latitude);
                break;

        }


    }

    public void enviarInfo(){

        final Realm realm5 = Realm.getDefaultInstance();
        realm5.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm5) {
                customer_new clienteNuevo = new customer_new(); // unmanaged

                clienteNuevo.setIdtype(idtype);
                clienteNuevo.setCard(card);
                clienteNuevo.setFe(fe);

                clienteNuevo.setLongitud(longitude);
                clienteNuevo.setLatitud(latitude);
                clienteNuevo.setPlaca(placa);

                clienteNuevo.setModel(model);
                clienteNuevo.setDoors(doors);
                clienteNuevo.setName(name);

                clienteNuevo.setEmail(email);
                clienteNuevo.setFantasy_name(fantasyname);
                clienteNuevo.setCompany_name(companyname);

                clienteNuevo.setPhone(phone);
                clienteNuevo.setCredit_limit(creditlimit);
                clienteNuevo.setAddress(address);

                clienteNuevo.setCredit_time(credittime);
                clienteNuevo.setSubidaNuevo(1);
                realm5.insertOrUpdate(clienteNuevo);
                Log.d("idinvNUEVOCREADO", clienteNuevo + "");


            }

        });
        realm5.close();
        Toast.makeText(crearCliente.this, "El cliente se creo correctamente", Toast.LENGTH_LONG).show();
    }

    public void obtenerLocalización() {

        gps = new GPSTracker(crearCliente.this);

        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        }
        else {
            gps.showSettingsAlert();


        }

    }

}
