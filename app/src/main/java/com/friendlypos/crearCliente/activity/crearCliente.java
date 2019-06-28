package com.friendlypos.crearCliente.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.crearCliente.modelo.customer_new;
import com.friendlypos.distribucion.util.GPSTracker;
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.visit;
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

    @Bind(R.id.cliente_card_nuevo)
    EditText cliente_card_nuevo;

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

    @Bind(R.id.btnCrearCliente)
    Button btnCrearCliente;

    @Bind(R.id.cliente_credittime_nuevo)
    Spinner spinnerCreditTime;

    @Bind(R.id.cliente_idtype_nuevo)
    Spinner spinnerIdType;

    @Bind(R.id.cliente_fe_nuevo)
    Spinner spinnerFe;

    String idtype1, idtype, card, fe, fe1, placa, model, doors, email, fantasyname, companyname, phone, creditlimit, address, credittime1, credittime;
    double longitud, latitud;
    GPSTracker gps;
    double latitude = 0.0;
    double longitude = 0.0;
    int nextId;


    String array_spinnerIdType[] = { "Seleccione el tipo de cédula", "01: Cédula Fisica",
            "02: Cédula Juridica", "03: Dimex", "04: NITE"
          };

    String array_spinnerCreditTime[] = { "Seleccione los días de crédito", "8 Dias",
            "15 Dias", "30 Dias", "45 Dias"
    };

    String array_spinnerFe[] = { "Seleccione Fe", "Tiquete Electrónico",
            "Factura Electrónica"
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_crear_cliente);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.array_spinnerIdType1,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdType.setAdapter(adapter);
        spinnerIdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                idtype1 = array_spinnerIdType[position];

                if(idtype1.equals("01: Cédula Fisica")){
                    idtype = "01";
                }
                else if(idtype1.equals("02: Cédula Juridica")){
                    idtype = "02";
                }
                else if(idtype1.equals("03: Dimex")){
                    idtype = "03";
                }
                else if(idtype1.equals("04: NITE")){
                    idtype = "04";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                idtype = array_spinnerIdType[0];
            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.array_spinnerCreditTime1, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCreditTime.setAdapter(adapter1);
        spinnerCreditTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                credittime1 = array_spinnerCreditTime[position];

                if(credittime1.equals("8 Dias")){
                    credittime = "8";
                }
                else if(credittime1.equals("15 Dias")){
                    credittime = "15";
                }
                else if(credittime1.equals("30 Dias")){
                    credittime = "30";
                }
                else if(credittime1.equals("45 Dias")){
                    credittime = "45";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                credittime = array_spinnerCreditTime[0];
            }
        });


        ArrayAdapter<CharSequence> adapterFE = ArrayAdapter.createFromResource(
                this, R.array.array_spinnerFe1, android.R.layout.simple_spinner_item);
        adapterFE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFe.setAdapter(adapterFE);
        spinnerFe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                fe1 = array_spinnerFe[position];

                if(fe1.equals("Tiquete Electrónico")){
                    fe = "false";
                }
                else if(fe1.equals("Factura Electrónica")){
                    fe = "true";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fe = array_spinnerFe[0];
            }
        });

        if (!SessionPrefes.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar(toolbarCrearCliente);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("entro_a_crear", "entro_a_crear");
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
        if (name.matches("^[\\p{L} .'-]+$") && name.length() >= 1) {
            check = true;
        }
        return check;
    }

    private boolean isValidMobile(String phone2) {
        boolean check = false;
        if (phone2.length() == 8) {
            check = true;
        }
        return check;
    }

    private boolean isValidCard01(String phone2) {
        boolean check = false;
        if (phone2.length() == 9) {
            check = true;
        }
        return check;
    }

    private boolean isValidCard02(String phone2) {
        boolean check = false;
        if (phone2.length() == 10) {
            check = true;
        }
        return check;
    }

    private boolean isValidCard03(String phone2) {
        boolean check = false;
        if (phone2.length() == 12) {
            check = true;
        }
        return check;
    }
    private boolean isValidCard04(String phone2) {
        boolean check = false;
        if (phone2.length() > 9 && phone2.length() < 12) {
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
                Log.d("entro_a_boton_crear", "entro_a_boton_crear");
                placa = cliente_placa_nuevo.getText().toString();
                model = cliente_model_nuevo.getText().toString();
                doors = cliente_doors_nuevo.getText().toString();
                email = cliente_email_nuevo.getText().toString();

                fantasyname = cliente_fantasyname_nuevo.getText().toString();
                companyname = cliente_companyname_nuevo.getText().toString();
                phone = cliente_phone_nuevo.getText().toString();
                creditlimit = cliente_creditlimit_nuevo.getText().toString();

                address = cliente_address_nuevo.getText().toString();

                if (isValidEmail(email) && isValidName(companyname) ) {
                    if (latitude != 0.0) {
                        if (longitude != 0.0) {

                            if(idtype1.equals("Seleccione el tipo de cédula")){
                                Toast.makeText(crearCliente.this, "Seleccione un dato en tipo de cédula", Toast.LENGTH_LONG).show();
                            }else{
                                if(fe1.equals("Seleccione Fe")){
                                    Toast.makeText(crearCliente.this, "Seleccione un dato en Fe", Toast.LENGTH_LONG).show();
                                }else{

                            if (idtype.equals("01")) {

                                card = cliente_card_nuevo.getText().toString();
                                if (isValidCard01(card)) {
                                    enviarInfo();
                                } else {
                                    cliente_card_nuevo.setError("La cédula física debe ser de 9 dígitos");
                                    cliente_card_nuevo.requestFocus();
                                    Toast.makeText(crearCliente.this, "La cédula física debe ser de 9 dígitos", Toast.LENGTH_LONG).show();
                                }

                            } else if (idtype.equals("02")) {
                                card = cliente_card_nuevo.getText().toString();
                                if (isValidCard02(card)) {
                                    //  Toast.makeText(crearCliente.this, "Bien", Toast.LENGTH_LONG).show();
                                    enviarInfo();
                                } else {
                                    cliente_card_nuevo.setError("La cédula jurídica debe ser de 10 dígitos");
                                    cliente_card_nuevo.requestFocus();
                                    Toast.makeText(crearCliente.this, "La cédula jurídica debe ser de 10 dígitos", Toast.LENGTH_LONG).show();
                                }
                            } else if (idtype.equals("03")) {
                                card = cliente_card_nuevo.getText().toString();
                                if (isValidCard03(card)) {
                                    //   Toast.makeText(crearCliente.this, "Bien", Toast.LENGTH_LONG).show();
                                    enviarInfo();
                                } else {
                                    cliente_card_nuevo.setError("El DIMEX debe ser de 12 dígitos");
                                    cliente_card_nuevo.requestFocus();
                                    Toast.makeText(crearCliente.this, "El DIMEX debe ser de 12 dígitos", Toast.LENGTH_LONG).show();
                                }
                            } else if (idtype.equals("04")) {
                                card = cliente_card_nuevo.getText().toString();
                                if (isValidCard04(card)) {
                                    Toast.makeText(crearCliente.this, "Bien", Toast.LENGTH_LONG).show();
                                    enviarInfo();
                                } else {
                                    cliente_card_nuevo.setError("El NITE debe ser entre 10 u 11 dígitos");
                                    cliente_card_nuevo.requestFocus();
                                    Toast.makeText(crearCliente.this, "El NITE debe ser entre 10 u 11 dígitos", Toast.LENGTH_LONG).show();
                                }
                            }
                        }}
                        }
                    }else {
                        Toast.makeText(crearCliente.this, "Obtenga la ubicación del cliente", Toast.LENGTH_LONG).show();
                    }
                }
                else if (!isValidEmail(email)) {
                    cliente_email_nuevo.setError("Email inválido");
                    cliente_email_nuevo.requestFocus();
                }
                else if (!isValidName(companyname)) {
                    cliente_companyname_nuevo.setError("Company Name inválido");
                    cliente_companyname_nuevo.requestFocus();
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
        Log.d("entro_a_enviar", "entro_a_enviar");
        final Realm realm5 = Realm.getDefaultInstance();
        realm5.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm5) {

                Number currentIdNum = realm5.where(customer_new.class).max("id");

                if (currentIdNum == null) {
                    nextId = 1;
                }
                else {
                    nextId = currentIdNum.intValue() + 1;
                }

                customer_new clienteNuevo = new customer_new(); // unmanaged

                clienteNuevo.setId(nextId);

                clienteNuevo.setIdtype(idtype);
                clienteNuevo.setCard(card);
                clienteNuevo.setFe(fe);

                clienteNuevo.setLongitud(longitude);
                clienteNuevo.setLatitud(latitude);
                clienteNuevo.setPlaca(placa);

                clienteNuevo.setModel(model);
                clienteNuevo.setDoors(doors);
                clienteNuevo.setName(companyname);

                clienteNuevo.setEmail(email);
                clienteNuevo.setFantasy_name(fantasyname);
                clienteNuevo.setCompany_name(companyname);

                clienteNuevo.setPhone(phone);
                clienteNuevo.setCredit_limit(creditlimit);
                clienteNuevo.setAddress(address);

                clienteNuevo.setCredit_time(credittime);
                clienteNuevo.setSubidaNuevo(1);

                realm5.insertOrUpdate(clienteNuevo);
                Log.d("entro_a_creado", clienteNuevo + "");

            }

        });
        realm5.close();
        Toast.makeText(crearCliente.this, "El cliente se creo correctamente", Toast.LENGTH_LONG).show();
        limpiarCampos();
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

    public void limpiarCampos() {

        spinnerCreditTime.setSelection(0);
        spinnerFe.setSelection(0);
        spinnerIdType.setSelection(0);
        cliente_card_nuevo.setText("");
        cliente_placa_nuevo.setText("");
        cliente_model_nuevo.setText("");
        cliente_doors_nuevo.setText("");
        cliente_email_nuevo.setText("");
        cliente_fantasyname_nuevo.setText("");
        cliente_companyname_nuevo.setText("");
        cliente_phone_nuevo.setText("");
        cliente_creditlimit_nuevo.setText("");
        cliente_longitud_nuevo.setText("");
        cliente_latitud_nuevo.setText("");
    }

}
