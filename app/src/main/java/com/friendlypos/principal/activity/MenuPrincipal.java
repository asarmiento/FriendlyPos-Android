package com.friendlypos.principal.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.friendlypos.Recibos.activity.RecibosActivity;
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.application.util.PrinterFunctions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.modelo.EnviarFactura;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.modelo.Usuarios;
import com.friendlypos.login.util.Properties;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.activity.PreventaActivity;
import com.friendlypos.preventas.modelo.EnviarClienteVisitado;
import com.friendlypos.preventas.modelo.visit;
import com.friendlypos.principal.fragment.ConfiguracionFragment;
import com.friendlypos.principal.helpers.DescargasHelper;
import com.friendlypos.principal.helpers.SubirHelper;
import com.friendlypos.principal.helpers.SubirHelperClienteVisitado;
import com.friendlypos.principal.helpers.SubirHelperPreventa;
import com.friendlypos.principal.helpers.SubirHelperProforma;
import com.friendlypos.principal.helpers.SubirHelperVentaDirecta;
import com.friendlypos.reimpresion.activity.ReimprimirActivity;
import com.friendlypos.reimpresion_pedidos.activity.ReimprimirPedidosActivity;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;
import com.github.clans.fab.FloatingActionButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.friendlypos.R.id.btn_descargar_datosempresa;
import static com.friendlypos.R.id.btn_descargar_recibos;

public class MenuPrincipal extends BluetoothActivity implements PopupMenu.OnMenuItemClickListener
       /* implements NavigationView.OnNavigationItemSelectedListener*/ {
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    int bloquear = 0;
    private FloatingActionButton but1 = null;
    private FloatingActionButton but2 = null;
    private FloatingActionButton but3 = null;
    private FloatingActionButton but4 = null;
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

    @Bind(R.id.clickRecibos)
    LinearLayout clickRecibos;

    @Bind(R.id.clickReimprimirVentas)
    LinearLayout clickReimprimirVentas;

    @Bind(R.id.clickReimprimirPedidos)
    LinearLayout clickReimprimirPedidos;

    @Bind(R.id.clickConfig)
    LinearLayout clickConfig;

    DrawerLayout drawer;

    @Bind(R.id.txtNombreUsuario)
    TextView txtNombreUsuario;

    SessionPrefes session;
    DescargasHelper download1;
    SubirHelper subir1;
    SubirHelperPreventa subirPreventa;
    SubirHelperVentaDirecta subirVentaDirecta;
    SubirHelperProforma subirProforma;
    SubirHelperClienteVisitado subirClienteVisitado;
    String usuer;
    String idUsuario;
    String facturaId;
    int facturaIdCV;
    private Properties properties;
    private int descargaDatosEmpresa;
    private int cambioDatosEmpresa = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        ActivityCompat.requestPermissions(MenuPrincipal.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        session = new SessionPrefes(getApplicationContext());
        properties = new Properties(getApplicationContext());
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        download1 = new DescargasHelper(MenuPrincipal.this);
        subir1 = new SubirHelper(MenuPrincipal.this);
        subirPreventa = new SubirHelperPreventa(MenuPrincipal.this);
        subirVentaDirecta = new SubirHelperVentaDirecta(MenuPrincipal.this);
        subirProforma = new SubirHelperProforma(MenuPrincipal.this);
        subirClienteVisitado = new SubirHelperClienteVisitado(MenuPrincipal.this);
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
            usuer = session.getUsuarioPrefs();
            Log.d("userasd", usuer);


           Realm realm = Realm.getDefaultInstance();
            Usuarios usuarios = realm.where(Usuarios.class).equalTo("email", usuer).findFirst();
            if(usuarios == null){
                txtNombreUsuario.setText(usuer);
            }else{
            String nombreUsuario = usuarios.getUsername();
            idUsuario = usuarios.getId();
            Log.d("userasd", nombreUsuario);
            txtNombreUsuario.setText(nombreUsuario);
            }
            realm.close();
        }

        but1 = (FloatingActionButton) findViewById(R.id.nav_distribucion);

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!properties.getBlockedApp()) {
                    Intent intent = new Intent(getApplication(), DistribucionActivity.class);
                    startActivity(intent);
                }
            }
        });

        but2 = (FloatingActionButton) findViewById(R.id.nav_ventadirecta);

        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!properties.getBlockedApp()) {
                    Intent intent = new Intent(getApplication(), VentaDirectaActivity.class);
                    startActivity(intent);
                }
            }
        });

        but3 = (FloatingActionButton) findViewById(R.id.nav_preventa);

        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!properties.getBlockedApp()) {
                    Intent intent = new Intent(getApplication(), PreventaActivity.class);
                    startActivity(intent);
                }
            }
        });

        but4 = (FloatingActionButton) findViewById(R.id.nav_recibos);

        but4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!properties.getBlockedApp()) {
                    Intent intent = new Intent(getApplication(), RecibosActivity.class);
                    startActivity(intent);
                }
            }
        });

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
        }

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
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cerrarsesion:

                AlertDialog alertDialog = new AlertDialog.Builder(
                    MenuPrincipal.this).setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        cambioDatosEmpresa = 0;
                        session.setPrefDescargaDatos(cambioDatosEmpresa);
                        session.cerrarSesion();
                        finish();
                    }

                }).setNegativeButton(
                    "No", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }

                    }
                ).create();

                alertDialog.setMessage("Seguro que quiere cerrar Sesion?");
                alertDialog.show();
                break;
/*
            case btn_descargar_datosempresa:
                bloquear = 1;
                Toast.makeText(MenuPrincipal.this, "descargar_datosEmpresa", Toast.LENGTH_SHORT).show();
                download1.descargarDatosEmpresa(MenuPrincipal.this);
                break;

            case R.id.btn_descargar_catalogo:
                if (bloquear == 0){
                    Toast.makeText(MenuPrincipal.this, "Descargar datos de la empresa primero", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MenuPrincipal.this, "descargar_catalogo", Toast.LENGTH_SHORT).show();
                    download1.descargarCatalogo(MenuPrincipal.this);}
                break;

            case R.id.btn_descargar_inventario:
                if (bloquear == 0){
                    Toast.makeText(MenuPrincipal.this, "Descargar datos de la empresa primero", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MenuPrincipal.this, "descargar_inventario", Toast.LENGTH_SHORT).show();
                    download1.descargarInventario(MenuPrincipal.this);}
                break;
*/
            case btn_descargar_datosempresa:
                cambioDatosEmpresa = session.getPrefDescargaDatos();
                //cambioDatosEmpresa = getDescargaDatosEmpresa();

                if (cambioDatosEmpresa == 0) {

                    download1.descargarDatosEmpresa(MenuPrincipal.this);
                    download1.descargarUsuarios(MenuPrincipal.this);
                    cambioDatosEmpresa = 1;
                    session.setPrefDescargaDatos(cambioDatosEmpresa);
                }else{
                    Toast.makeText(MenuPrincipal.this, "Ya los datos están descargados", Toast.LENGTH_LONG).show();
                }

            case btn_descargar_recibos:
                cambioDatosEmpresa = session.getPrefDescargaDatos();

                if (cambioDatosEmpresa == 1) {
                    download1.descargarRecibos(MenuPrincipal.this);

                } else {
                    Toast.makeText(MenuPrincipal.this, "Descargar datos de la empresa primero", Toast.LENGTH_LONG).show();

                }

                break;

            case R.id.btn_descargar_catalogo:
                //  cambioDatosEmpresa = getDescargaDatosEmpresa();
                cambioDatosEmpresa = session.getPrefDescargaDatos();

                    if (cambioDatosEmpresa == 1) {
                        download1.descargarCatalogo(MenuPrincipal.this);

                    } else {
                        Toast.makeText(MenuPrincipal.this, "Descargar datos de la empresa primero", Toast.LENGTH_LONG).show();
                    }
                break;

            case R.id.btn_descargar_inventario:

                Realm realm4 = Realm.getDefaultInstance();

                RealmQuery<invoice> query4 = realm4.where(invoice.class).equalTo("subida", 1);
                final RealmResults<invoice> invoice4 = query4.findAll();
                Log.d("qweqweq", invoice4.toString());



                if(invoice4.size()== 0){
                    cambioDatosEmpresa = session.getPrefDescargaDatos();
                    // cambioDatosEmpresa = getDescargaDatosEmpresa();

                    if (cambioDatosEmpresa == 1) {
                        download1.descargarInventario(MenuPrincipal.this);

                    } else {
                        Toast.makeText(MenuPrincipal.this, "Descargar datos de la empresa primero", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(MenuPrincipal.this,"Existen facturas pendientes de subir", Toast.LENGTH_LONG).show();
                }
                realm4.close();
                break;

            case R.id.btn_subir_ventas:
                //TODO SABER COMO TENER QUE SUBIR LOS DATOS, SI UNO X UNO O TODOS DE UN SOLO.

                Realm realm = Realm.getDefaultInstance();

                RealmQuery<invoice> query = realm.where(invoice.class).equalTo("subida", 1).equalTo("facturaDePreventa", "Distribucion");
                final RealmResults<invoice> invoice1 = query.findAll();
                Log.d("qweqweq", invoice1.toString());
                List<invoice> listaFacturas = realm.copyFromRealm(invoice1);
                Log.d("qweqweq1", listaFacturas + "");
                realm.close();

                if(listaFacturas.size()== 0){
                    Toast.makeText(MenuPrincipal.this,"No hay facturas para subir", Toast.LENGTH_LONG).show();
                }else {

                    for (int i = 0; i < listaFacturas.size(); i++) {

                        facturaId = String.valueOf(listaFacturas.get(i).getId());
                        Log.d("facturaId", facturaId + "");
                        EnviarFactura obj = new EnviarFactura(listaFacturas.get(i));
                        Log.d("My App", obj + "");
                        subir1.sendPost(obj);

                        String resp = subir1.getRespuestaServer();

                        if(resp.equals("200 OK")){
                        actualizarVenta();
                        actualizarFactura();
                        }
                        else{
                            Toast.makeText(MenuPrincipal.this, "no se subio", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                Toast.makeText(MenuPrincipal.this, "subir_ventas", Toast.LENGTH_SHORT).show();

                break;

            case R.id.btn_subir_pedidos:

                Realm realmPedidos = Realm.getDefaultInstance();

                RealmQuery<invoice> queryPedidos = realmPedidos.where(invoice.class).equalTo("subida", 1).equalTo("facturaDePreventa", "Preventa");
                final RealmResults<invoice> invoicePedidos = queryPedidos.findAll();
                Log.d("SubFacturaInvP", invoicePedidos.toString());
                List<invoice> listaFacturasPedidos = realmPedidos.copyFromRealm(invoicePedidos);
                Log.d("SubFacturaListaP", listaFacturasPedidos + "");
                realmPedidos.close();

                if(listaFacturasPedidos.size()== 0){
                    Toast.makeText(MenuPrincipal.this,"No hay facturas para subir", Toast.LENGTH_LONG).show();
                }else {

                    for (int i = 0; i < listaFacturasPedidos.size(); i++) {
                        Toast.makeText(MenuPrincipal.this, "hay", Toast.LENGTH_SHORT).show();

                        facturaId = String.valueOf(listaFacturasPedidos.get(i).getId());
                        Log.d("facturaId", facturaId + "");
                        EnviarFactura obj = new EnviarFactura(listaFacturasPedidos.get(i));
                        Log.d("My App", obj + "");
                        subirPreventa.sendPostPreventa(obj);

                        actualizarVenta();
                        actualizarFactura();
                    }
                }

                Toast.makeText(MenuPrincipal.this, "subir_Pedidos", Toast.LENGTH_SHORT).show();

                break;

            case R.id.btn_subir_ventadirecta:

                Realm realmVentaDirecta = Realm.getDefaultInstance();

                RealmQuery<invoice> queryVentaDirecta = realmVentaDirecta.where(invoice.class).equalTo("subida", 1).equalTo("facturaDePreventa", "VentaDirecta");
                final RealmResults<invoice> invoiceVentaDirecta = queryVentaDirecta.findAll();
                Log.d("SubFacturaInvV", invoiceVentaDirecta.toString());
                List<invoice> listaFacturasVentaDirecta = realmVentaDirecta.copyFromRealm(invoiceVentaDirecta);
                Log.d("SubFacturaListaV", listaFacturasVentaDirecta + "");
                realmVentaDirecta.close();

                if(listaFacturasVentaDirecta.size()== 0){
                    Toast.makeText(MenuPrincipal.this,"No hay facturas para subir", Toast.LENGTH_LONG).show();
                }else {

                    for (int i = 0; i < listaFacturasVentaDirecta.size(); i++) {
                        Toast.makeText(MenuPrincipal.this, "hay", Toast.LENGTH_SHORT).show();

                        facturaId = String.valueOf(listaFacturasVentaDirecta.get(i).getId());
                        Log.d("facturaId", facturaId + "");
                        EnviarFactura obj = new EnviarFactura(listaFacturasVentaDirecta.get(i));
                        Log.d("My App", obj + "");
                        subirVentaDirecta.sendPostVentaDirecta(obj);

                        actualizarVenta();
                        actualizarFactura();
                    }
                }

                Toast.makeText(MenuPrincipal.this, "subir_Venta Directa", Toast.LENGTH_SHORT).show();

                break;

            case R.id.btn_subir_proforma:

                Realm realmProforma = Realm.getDefaultInstance();

                RealmQuery<invoice> queryProforma = realmProforma.where(invoice.class).equalTo("subida", 1).equalTo("facturaDePreventa", "Proforma");
                final RealmResults<invoice> invoiceProforma = queryProforma.findAll();
                Log.d("SubFacturaInvPRO", invoiceProforma.toString());
                List<invoice> listaFacturasProforma = realmProforma.copyFromRealm(invoiceProforma);
                Log.d("SubFacturaListaPROV", listaFacturasProforma + "");
                realmProforma.close();

                if(listaFacturasProforma.size()== 0){
                    Toast.makeText(MenuPrincipal.this,"No hay facturas para subir", Toast.LENGTH_LONG).show();
                }else {

                    for (int i = 0; i < listaFacturasProforma.size(); i++) {
                        Toast.makeText(MenuPrincipal.this, "hay", Toast.LENGTH_SHORT).show();

                        facturaId = String.valueOf(listaFacturasProforma.get(i).getId());
                        Log.d("facturaId", facturaId + "");
                        EnviarFactura obj = new EnviarFactura(listaFacturasProforma.get(i));
                        Log.d("My App", obj + "");
                        subirProforma.sendPostProforma(obj);

                        actualizarVenta();
                        actualizarFactura();
                    }
                }

                Toast.makeText(MenuPrincipal.this, "subir Proforma", Toast.LENGTH_SHORT).show();

                break;

            case R.id.btn_subir_clienteVisitados:

                Realm realmClienteVisitados = Realm.getDefaultInstance();

                RealmQuery<visit> queryClienteVisitados = realmClienteVisitados.where(visit.class).equalTo("subida", 1);
                final RealmResults<visit> invoiceVisits = queryClienteVisitados.findAll();
                Log.d("qweqweq", invoiceVisits.toString());
                List<visit> listaVisits = realmClienteVisitados.copyFromRealm(invoiceVisits);
                Log.d("qweqweq1", listaVisits + "");
                realmClienteVisitados.close();

                if(listaVisits.size()== 0){
                    Toast.makeText(MenuPrincipal.this,"No hay facturas para subir", Toast.LENGTH_LONG).show();
                }else {

                    for (int i = 0; i < listaVisits.size(); i++) {
                        Toast.makeText(MenuPrincipal.this, "hay", Toast.LENGTH_SHORT).show();

                        facturaIdCV = listaVisits.get(i).getId();
                        Log.d("facturaIdCV", facturaIdCV + "");
                        EnviarClienteVisitado obj = new EnviarClienteVisitado(listaVisits.get(i));
                        Log.d("My App", obj + "");
                        subirClienteVisitado.sendPostClienteVisitado(obj);

                        actualizarClienteVisitado();
                    }
                }

                Toast.makeText(MenuPrincipal.this, "subir_clienteVisitados", Toast.LENGTH_SHORT).show();

                break;


            case R.id.btn_devolver_inventario:
                Toast.makeText(MenuPrincipal.this, "devolver_inventario", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_imprimir_liquidacion:
               /* if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {*/
                    PrinterFunctions.imprimirLiquidacionMenu(MenuPrincipal.this);
                    Toast.makeText(MenuPrincipal.this, "imprimir liquidacion", Toast.LENGTH_SHORT).show();
            /*    }
                else if(bluetoothStateChangeReceiver.isBluetoothAvailable() == false){
                    Functions.CreateMessage(MenuPrincipal.this, "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                }*/
                break;

            case R.id.btn_imprimir_orden_carga:
               /* if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {*/
                PrinterFunctions.imprimirOrdenCarga(MenuPrincipal.this);
                Toast.makeText(MenuPrincipal.this, "imprimir liquidacion", Toast.LENGTH_SHORT).show();
            /*    }
                else if(bluetoothStateChangeReceiver.isBluetoothAvailable() == false){
                    Functions.CreateMessage(MenuPrincipal.this, "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                }*/
                break;

            case R.id.btn_imprimir_devolucion:
                if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {
                    PrinterFunctions.imprimirDevoluciónMenu(MenuPrincipal.this);
                    Toast.makeText(MenuPrincipal.this, "imprimir devolucion", Toast.LENGTH_SHORT).show();
                }
                else if(bluetoothStateChangeReceiver.isBluetoothAvailable() == false){
                    Functions.CreateMessage(MenuPrincipal.this, "Error", "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo");
                }
                break;


        }
        return false;
    }

    public void actualizarFactura() {

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA FACTURAS
        final Realm realm2 = Realm.getDefaultInstance();

        try {
            realm2.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    invoice factura_actualizada = realm2.where(invoice.class).equalTo("id", facturaId).findFirst();
                    factura_actualizada.setSubida(0);
                    realm2.insertOrUpdate(factura_actualizada);

                }

            });

        } catch (Exception e) {
            Log.e("error", "error", e);
            Toast.makeText(MenuPrincipal.this,"error", Toast.LENGTH_SHORT).show();

        }
        realm2.close();

    }

    protected void actualizarVenta() {

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        final Realm realm3 = Realm.getDefaultInstance();

        try {
            realm3.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    sale sale_actualizada = realm3.where(sale.class).equalTo("id", facturaId).findFirst();
                    sale_actualizada.setSubida(0);
                    realm3.insertOrUpdate(sale_actualizada);

                }

            });

        } catch (Exception e) {
            Log.e("error", "error", e);
            Toast.makeText(MenuPrincipal.this,"error", Toast.LENGTH_SHORT).show();

        }
        realm3.close();
    }

    protected void actualizarClienteVisitado() {

        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS
        final Realm realm3 = Realm.getDefaultInstance();

        try {
            realm3.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    visit sale_actualizada = realm3.where(visit.class).equalTo("id", facturaIdCV).findFirst();
                    sale_actualizada.setSubida(0);
                    realm3.insertOrUpdate(sale_actualizada);

                }

            });

        } catch (Exception e) {
            Log.e("error", "error", e);
            Toast.makeText(MenuPrincipal.this,"error", Toast.LENGTH_SHORT).show();

        }
        realm3.close();
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
                Intent vd;
                vd = new Intent(MenuPrincipal.this, VentaDirectaActivity.class);
                startActivity(vd);
                finish();
                break;

            case R.id.clickPreventa:
                Intent preventa;
                preventa = new Intent(MenuPrincipal.this, PreventaActivity.class);
                startActivity(preventa);
                finish();
                break;

            case R.id.clickRecibos:
                Intent recibos;
                recibos = new Intent(MenuPrincipal.this, RecibosActivity.class);
                startActivity(recibos);
                finish();
                break;

            case R.id.clickReimprimirVentas:
                Intent reimprimir;
                reimprimir = new Intent(MenuPrincipal.this, ReimprimirActivity.class);
                startActivity(reimprimir);
                finish();
                break;

            case R.id.clickReimprimirPedidos:
                Intent reimprimirpedidos;
                reimprimirpedidos = new Intent(MenuPrincipal.this, ReimprimirPedidosActivity.class);
                startActivity(reimprimirpedidos);
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
