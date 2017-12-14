package com.friendlypos.principal.activity;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.Facturas;
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.fragment.ConfiguracionFragment;
import com.friendlypos.principal.helpers.DescargasHelper;
import com.friendlypos.principal.helpers.SubirHelper;
import com.friendlypos.reimpresion.activity.ReimprimirActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.friendlypos.R.id.btn_descargar_datosempresa;

public class MenuPrincipal extends BluetoothActivity implements PopupMenu.OnMenuItemClickListener
       /* implements NavigationView.OnNavigationItemSelectedListener*/ {
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    int bloquear = 0;
    @Bind(R.id.clickClientes)
    LinearLayout clickClientes;

    @Bind(R.id.clickProductos)
    LinearLayout clickProductos;

    @Bind(R.id.clickDistribucion)
    LinearLayout clickDistribucion;

    @Bind(R.id.clickVentaDirecta)
    LinearLayout clickVentaDirecta;

    @Bind(R.id.clickPreventa)
    LinearLayout clickPreventa;

    @Bind(R.id.clickReimprimirVentas)
    LinearLayout clickReimprimirVentas;

    @Bind(R.id.clickConfig)
    LinearLayout clickConfig;

    DrawerLayout drawer;

    @Bind(R.id.txtNombreUsuario)
    TextView txtNombreUsuario;

    SessionPrefes session;
    DescargasHelper download1;
    SubirHelper subir1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        networkStateChangeReceiver = new NetworkStateChangeReceiver();

        session = new SessionPrefes(getApplicationContext());
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        download1 = new DescargasHelper(MenuPrincipal.this);
        subir1 = new SubirHelper(MenuPrincipal.this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        connectToPrinter();

        // Redirección al Login
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else {
            Realm realm = Realm.getDefaultInstance();
            String usuer = session.getUsuarioPrefs();
            Log.d("user", usuer);

            Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();

            String nombreUsuario = usuarios.getUsername();

            Log.d("user", nombreUsuario);

            txtNombreUsuario.setText(nombreUsuario);
            //
            realm.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    private void connectToPrinter() {
        //if(bluetoothStateChangeReceiver.isBluetoothAvailable()) {
            getPreferences();
            if (printer_enabled) {
                if (printer == null || printer.equals("")) {
//                AlertDialog d = new AlertDialog.Builder(context)
//                        .setTitle(getResources().getString(R.string.printer_alert))
//                        .setMessage(getResources().getString(R.string.message_printer_not_found))
//                        .setNegativeButton(getString(android.R.string.ok), null)
//                        .show();
                }
                else {
                    if (!isServiceRunning(PrinterService.CLASS_NAME)) {
                        PrinterService.startRDService(getApplicationContext(), printer);
                    }
                }
            }
        }/*else{
          //  return null;
            Toast.makeText(getApplicationContext(),"dasda", Toast.LENGTH_LONG).show();
            Log.i("adsdsda", "Bluetooth not supported");
            // Show proper message here
            //finish();

            //TODO MUESTRA UN DIALOG DE ERROR
        }
    }*/

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public boolean onMenuItemSelect(MenuItem item) {
        showPopup(findViewById(item.getItemId()));
        return true;
    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(MenuPrincipal.this, view);
        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        Menu popupMenu = popup.getMenu();

        if (bloquear == 0) {
            //bloqueados
            popupMenu.findItem(R.id.btn_descargar_catalogo).setEnabled(false);
            popupMenu.findItem(R.id.btn_descargar_inventario).setEnabled(false);
            Toast.makeText(MenuPrincipal.this, "Descargar datos de la empresa primero", Toast.LENGTH_LONG).show();
        }
        else if (bloquear == 1) {
            //desbloqueados
            popupMenu.findItem(R.id.btn_descargar_catalogo).setEnabled(true);
            popupMenu.findItem(R.id.btn_descargar_inventario).setEnabled(true);
        }
        popup.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cerrarsesion:

                AlertDialog alertDialog = new AlertDialog.Builder(
                    MenuPrincipal.this).setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        session.cerrarSesion();
                        finish();
                    }

                }).setNegativeButton(
                    "No", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            //customer = cust;
                            dialog.cancel();
                            //new getDataClient().execute();
                        }

                    }
                ).create();

                alertDialog.setMessage("Seguro que quiere cerrar Sesion?");

                // Showing Alert Message
                alertDialog.show();
                break;

            case btn_descargar_datosempresa:
                bloquear = 1;
                Toast.makeText(MenuPrincipal.this, "descargar_datosEmpresa", Toast.LENGTH_SHORT).show();
                download1.descargarDatosEmpresa(MenuPrincipal.this);
                break;

            case R.id.btn_descargar_catalogo:
                Toast.makeText(MenuPrincipal.this, "descargar_catalogo", Toast.LENGTH_SHORT).show();
                download1.descargarCatalogo(MenuPrincipal.this);
                break;

            case R.id.btn_descargar_inventario:
                Toast.makeText(MenuPrincipal.this, "descargar_inventario", Toast.LENGTH_SHORT).show();
                download1.descargarInventario(MenuPrincipal.this);
                break;

            case R.id.btn_descargar_deudas:
                Toast.makeText(this, "Botón no disponible por el momento", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_subir_recibos:


                Toast.makeText(MenuPrincipal.this, "subir_recibos", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_subir_ventas:

                //TODO SABER COMO TENER QUE SUBIR LOS DATOS, SI UNO X UNO O TODOS DE UN SOLO.
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<Facturas> facturas1 = realm.where(Facturas.class).findAll();
                for (int i = 0; i < facturas1.size(); i++) {
                    subir1.sendPost(facturas1.get(i));
                }

                Toast.makeText(MenuPrincipal.this, "subir_ventas", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_devolver_inventario:
                Toast.makeText(MenuPrincipal.this, "devolver_inventario", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_imprimir_liquidacion:
                if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {
                    PrinterFunctions.imprimirLiquidacionMenu(MenuPrincipal.this);
                    Toast.makeText(MenuPrincipal.this, "imprimir liquidacion", Toast.LENGTH_SHORT).show();
                }
                else if(bluetoothStateChangeReceiver.isBluetoothAvailable() == false){
                    Functions.CreateMessage(MenuPrincipal.this, "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                }
                break;


        }
        return false;
    }



    public void ClickNavigation(View view) {
        Fragment fragment = null;
        Class fragmentClass = ConfiguracionFragment.class;

        switch (view.getId()) {

            case R.id.clickConfig:
                fragmentClass = ConfiguracionFragment.class;

                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }// Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.frame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void onClickGo(View component) {

        switch (component.getId()) {

            case R.id.clickClientes:
                Intent clientes;
                clientes = new Intent(MenuPrincipal.this, ClientesActivity.class);
                startActivity(clientes);
                finish();

                break;
            case R.id.clickProductos:
                Intent productos;
                productos = new Intent(MenuPrincipal.this, ProductosActivity.class);
                startActivity(productos);
                finish();
                break;

            case R.id.clickDistribucion:
                Intent dist;
                dist = new Intent(MenuPrincipal.this, DistribucionActivity.class);
                startActivity(dist);
                finish();

                break;

            case R.id.clickVentaDirecta:
                Toast.makeText(this, "Botón no disponible por el momento", Toast.LENGTH_SHORT).show();
                break;

            case R.id.clickPreventa:
                Toast.makeText(this, "Botón no disponible por el momento", Toast.LENGTH_SHORT).show();
                break;

            case R.id.clickReimprimirVentas:
                Intent reimprimir;
                reimprimir = new Intent(MenuPrincipal.this, ReimprimirActivity.class);
                startActivity(reimprimir);
                finish();
                break;

        }
    }

    //Check if the printing service is running
    public boolean isServiceRunning(String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(this);
    }

}
