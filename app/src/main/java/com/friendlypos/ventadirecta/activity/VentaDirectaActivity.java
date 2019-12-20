package com.friendlypos.ventadirecta.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;

import com.friendlypos.R;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Inventario;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.Adapter;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.ventadirecta.delegate.PreSellInvoiceDelegateVD;
import com.friendlypos.ventadirecta.fragment.VentaDirResumenFragment;
import com.friendlypos.ventadirecta.fragment.VentaDirSelecClienteFragment;
import com.friendlypos.ventadirecta.fragment.VentaDirSelecProductoFragment;
import com.friendlypos.ventadirecta.fragment.VentaDirTotalizarFragment;
import com.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class VentaDirectaActivity extends BluetoothActivity {
    SessionPrefes session;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int invoiceIdPreventa;
    private String metodoPagoClienteVentaDirecta;
    private String creditoLimiteClienteVentaDirecta = null;
    private int selecClienteTabVentaDirecta;
    private String dueClienteVentaDirecta;
    int nextId;
    private double totalizarSubGrabado;
    private double totalizarSubExento;
    private double totalizarSubTotal;
    private double totalizarDescuento;
    private double totalizarImpuestoIVA;
    private double totalizarTotal;
    private double totalizarTotalDouble;
    List<Pivot> facturaid1;
    String idFacturaSeleccionada;
    int idInvetarioSelec;
    Double amount_inventario = 0.0;

    private PreSellInvoiceDelegateVD preSellInvoiceDelegate;

    public int getInvoiceIdVentaDirecta() {
        return invoiceIdPreventa;
    }

    public void setInvoiceIdPreventa(int invoiceIdPreventa) {
        this.invoiceIdPreventa = invoiceIdPreventa;
    }


    public double getTotalizarSubGrabado() {
        return totalizarSubGrabado;
    }

    public void setTotalizarSubGrabado(double totalizarSubGrabado) {
        this.totalizarSubGrabado = this.totalizarSubGrabado + totalizarSubGrabado;
    }

    public double getTotalizarSubExento() {
        return totalizarSubExento;
    }

    public void setTotalizarSubExento(double totalizarSubExento) {
        this.totalizarSubExento = this.totalizarSubExento + totalizarSubExento;
    }

    public double getTotalizarSubTotal() {
        return totalizarSubTotal;
    }

    public void setTotalizarSubTotal(double totalizarSubTotal) {
        this.totalizarSubTotal = this.totalizarSubTotal + totalizarSubTotal;
    }

    public double getTotalizarDescuento() {
        return totalizarDescuento;
    }

    public void setTotalizarDescuento(double totalizarDescuento) {
        this.totalizarDescuento = this.totalizarDescuento + totalizarDescuento;
    }

    public double getTotalizarImpuestoIVA() {
        return totalizarImpuestoIVA;
    }

    public void setTotalizarImpuestoIVA(double totalizarImpuestoIVA) {
        this.totalizarImpuestoIVA = this.totalizarImpuestoIVA + totalizarImpuestoIVA;
    }

    public double getTotalizarTotal() {
        return totalizarTotal;
    }

    public void setTotalizarTotal(double totalizarTotal) {
        this.totalizarTotal = this.totalizarTotal + totalizarTotal;
    }


    public String getMetodoPagoClienteVentaDirecta() {
        return metodoPagoClienteVentaDirecta;
    }

    public void setMetodoPagoClienteVentaDirecta(String metodoPagoClienteVentaDirecta) {
        this.metodoPagoClienteVentaDirecta = metodoPagoClienteVentaDirecta;
    }

    public String getCreditoLimiteClienteVentaDirecta() {
        return creditoLimiteClienteVentaDirecta;
    }

    public void setCreditoLimiteClienteVentaDirecta(String creditoLimiteClienteVentaDirecta) {
        this.creditoLimiteClienteVentaDirecta = creditoLimiteClienteVentaDirecta;
    }

    public int getSelecClienteTabVentaDirecta() {
        return selecClienteTabVentaDirecta;
    }

    public void setSelecClienteTabVentaDirecta(int selecClienteTabVentaDirecta) {
        this.selecClienteTabVentaDirecta = selecClienteTabVentaDirecta;
    }

    public String getDueClienteVentaDirecta() {
        return dueClienteVentaDirecta;
    }

    public void setDueClienteVentaDirecta(String dueClienteVentaDirecta) {
        this.dueClienteVentaDirecta = dueClienteVentaDirecta;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ventadirecta);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarVentaDirecta);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        session = new SessionPrefes(getApplicationContext());
        actionBar.setDisplayHomeAsUpEnabled(true);
        preSellInvoiceDelegate = new PreSellInvoiceDelegateVD(this);
        connectToPrinter();
        /*toolbar = (Toolbar) findViewById(R.id.toolbarDistribucion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerVentaDirecta);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabsVentaDirecta);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabCliente = getSelecClienteTabVentaDirecta();
                if (tabCliente == 0 && tab.getPosition() != 0) {

                    Functions.CreateMessage(VentaDirectaActivity.this, "Venta Directa", "Seleccione una factura.");

                    new Handler().postDelayed(
                            new Runnable() {

                                @Override
                                public void run() {
                                    tabLayout.getTabAt(0).select();
                                }
                            }, 100);
                }
                else {
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        getList();
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

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getSupportFragmentManager());
        final List<BaseFragment> list = new ArrayList<>();
        list.add(new VentaDirSelecClienteFragment());
       list.add(new VentaDirSelecProductoFragment());
        list.add(new VentaDirResumenFragment());
         list.add(new VentaDirTotalizarFragment());
        adapter.addFragment(list.get(0), "Seleccionar Cliente");
      adapter.addFragment(list.get(1), "Seleccionar Productos");
         adapter.addFragment(list.get(2), "Resumen");
         adapter.addFragment(list.get(3), "Totalizar");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                list.get(position).updateData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                int tabCliente = getSelecClienteTabVentaDirecta();
                if (tabCliente == 1) {



                    String message = "¿Desea cancelar la factura en proceso?";
                    String titulo = "Salir";
                    SpannableString spannableString =  new SpannableString(message);
                    SpannableString spannableStringTitulo =  new SpannableString(titulo);

                    CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getApplicationContext().getAssets(), "font/monse.otf"));
                    spannableString.setSpan(typefaceSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length(), Spanned.SPAN_PRIORITY);

                    AlertDialog dialogReturnSale = new AlertDialog.Builder(VentaDirectaActivity.this)

                            .setTitle(spannableStringTitulo)
                            .setMessage(spannableString)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    final Realm realm2 = Realm.getDefaultInstance();

                                    realm2.executeTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm) {

                                            // increment index
                                  /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();

                                    if(numeracion.getId()){}
*/

                                            // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                                            Number numero = realm.where(Numeracion.class).equalTo("sale_type", "1").max("number");

                                            if (numero == null) {
                                                nextId = 1;
                                            } else {
                                                nextId = numero.intValue() - 1;
                                            }

                                        }
                                    });


                                    final Realm realm5 = Realm.getDefaultInstance();
                                    realm5.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm5) {
                                            Numeracion numNuevo = new Numeracion(); // unmanaged
                                            numNuevo.setSale_type("1");
                                            numNuevo.setNumeracion_numero(nextId);

                                            realm5.insertOrUpdate(numNuevo);
                                            Log.d("VDNumNuevaAtras", numNuevo + "");


                                        }

                                    });
                                    realm5.close();

                                    devolverTodo();


                                    Intent intent = new Intent(VentaDirectaActivity.this, MenuPrincipal.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            }).create();
                    dialogReturnSale.show();



                }else{
                    Intent intent = new Intent(VentaDirectaActivity.this, MenuPrincipal.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }



                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

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

    public invoiceDetalleVentaDirecta getCurrentInvoice() {
        return preSellInvoiceDelegate.getCurrentInvoiceVentaDirecta();
    }

    public sale getCurrentVenta() {
        return preSellInvoiceDelegate.getCurrentVentaVentaDirecta();
    }

    public invoice getInvoiceByInvoiceDetalles() {
        return preSellInvoiceDelegate.getInvoiceByInvoiceDetalleVentaDirecta();
    }

    public List<Pivot> getAllPivotDelegate() {
        return preSellInvoiceDelegate.getAllPivotVentaDirecta();
    }

    public void insertProduct(Pivot pivot) {
        preSellInvoiceDelegate.insertProductVentaDirecta(pivot);
    }

    public void borrarProduct(Pivot pivot) {
        preSellInvoiceDelegate.borrarProductoVentaDirecta(pivot);
    }

    public void initCurrentInvoice(String id, String type, String branch_office_id, String numeration,String key,
                                   String consecutive_number, double latitude, double longitude,
                                   String date, String times, String date_presale, String times_presale, String due_data,
                                   String invoice_type_id, String payment_method_id, String totalSubtotal, String totalGrabado,
                                   String totalExento, String totalDescuento, String percent_discount, String totalImpuesto,
                                   String totalTotal, String changing, String notes, String canceled,
                                   String paid_up, String paid, String created_at, String idUsuario,
                                   String idUsuarioAplicado, int creada, int aplicada) {
        preSellInvoiceDelegate.initInvoiceDetalleVentaDirecta(id, type, branch_office_id, numeration,key, consecutive_number, latitude, longitude,
                date, times, date_presale, times_presale, due_data,
                invoice_type_id, payment_method_id, totalSubtotal, totalGrabado,
                totalExento, totalDescuento, percent_discount, totalImpuesto,
                totalTotal, changing, notes, canceled,
                paid_up, paid, created_at, idUsuario,
                idUsuarioAplicado, creada, aplicada);
    }

    public void initCurrentVenta(String p_id, String p_invoice_id, String p_customer_id, String p_customer_name,
                                 String p_cash_desk_id, String p_sale_type, String p_viewed, String p_applied,
                                 String p_created_at, String p_updated_at, String p_reserved, int aplicada, int subida, String facturaDePreventa) {
        preSellInvoiceDelegate.initVentaDetallesVentaDirecta(p_id, p_invoice_id, p_customer_id, p_customer_name,
                p_cash_desk_id, p_sale_type, p_viewed, p_applied,
                p_created_at, p_updated_at, p_reserved, aplicada, subida, facturaDePreventa);
    }

    public void initProducto(int pos) {
        preSellInvoiceDelegate.initProductVentaDirecta(pos);
    }

    public void cleanTotalize() {
        totalizarSubGrabado = 0.0;
        totalizarSubExento = 0.0;
        totalizarSubTotal = 0.0;
        totalizarDescuento = 0.0;
        totalizarImpuestoIVA = 0.0;
        totalizarTotal = 0.0;
        totalizarTotalDouble = 0.0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preSellInvoiceDelegate.destroy();
        preSellInvoiceDelegate = null;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {


            int tabCliente = getSelecClienteTabVentaDirecta();
            if (tabCliente == 1) {

                String message = "¿Desea cancelar la factura en proceso?";
                String titulo = "Salir";
                SpannableString spannableString =  new SpannableString(message);
                SpannableString spannableStringTitulo =  new SpannableString(titulo);

                CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getApplicationContext().getAssets(), "font/monse.otf"));
                spannableString.setSpan(typefaceSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length(), Spanned.SPAN_PRIORITY);

                AlertDialog dialogReturnSale = new AlertDialog.Builder(VentaDirectaActivity.this)

                        .setTitle(spannableStringTitulo)
                        .setMessage(spannableString)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                final Realm realm2 = Realm.getDefaultInstance();

                                realm2.executeTransaction(new Realm.Transaction() {

                                    @Override
                                    public void execute(Realm realm) {

                                        // increment index
                                  /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();

                                    if(numeracion.getId()){}
*/

                                        // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                                        Number numero = realm.where(Numeracion.class).equalTo("sale_type", "1").max("number");

                                        if (numero == null) {
                                            nextId = 1;
                                        } else {
                                            nextId = numero.intValue() - 1;
                                        }

                                    }
                                });


                                final Realm realm5 = Realm.getDefaultInstance();
                                realm5.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm5) {
                                        Numeracion numNuevo = new Numeracion(); // unmanaged
                                        numNuevo.setSale_type("1");
                                        numNuevo.setNumeracion_numero(nextId);

                                        realm5.insertOrUpdate(numNuevo);
                                        Log.d("VDNumNuevaAtras", numNuevo + "");


                                    }

                                });
                                realm5.close();
                                devolverTodo();
                                Intent intent = new Intent(VentaDirectaActivity.this, MenuPrincipal.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        }).create();
                dialogReturnSale.show();

            }else{
                Intent intent = new Intent(VentaDirectaActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        }
        return super.onKeyDown(keyCode, event);
    }



    private List<Numeracion> getList(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Numeracion> query = realm.where(Numeracion.class).equalTo("sale_type", "1").equalTo("rec_creada", 1).equalTo("rec_aplicada", 0);
        RealmResults<Numeracion> result1 = query.findAll();


        if(result1.size() == 0){
            Log.d("nadaCreados", "nada" + "");
           // Toast.makeText(getApplicationContext(),"Nada" ,Toast.LENGTH_LONG).show();
        }
        else{
            for (int i = 0; i < result1.size(); i++) {
                List<Numeracion> salesList1 = realm.where(Numeracion.class).equalTo("sale_type", "1").equalTo("rec_creada", 1).equalTo("rec_aplicada", 0).findAll();
                int numero = salesList1.get(i).getNumeracion_numero();

                nextId = numero - 1;

                final Realm realm5 = Realm.getDefaultInstance();
                realm5.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm5) {
                        Numeracion numNuevo = new Numeracion(); // unmanaged
                        numNuevo.setSale_type("1");
                        numNuevo.setNumeracion_numero(nextId);

                        realm5.insertOrUpdate(numNuevo);
                        Log.d("VDNumNuevaAtras", numNuevo + "");


                    }

                });
                realm5.close();
            }
          //  devolverTodo();
        }
        return result1;
    }


    public void devolverTodo(){

        facturaid1 = getAllPivotDelegate();//realm.where(Pivot.class).equalTo("invoice_id", idFacturaSeleccionada).findAll();
        Log.d("PRODUCTOSFACTURAS", facturaid1 + "");

        for (int i = 0; i < facturaid1.size(); i++) {
            final Pivot eventRealm = facturaid1.get(i);
            final double cantidadDevolver = Double.parseDouble(eventRealm.getAmount());

            Log.d("PRODUCTOSFACTURASEPA1", eventRealm + "");
            Log.d("PRODUCTOSFACTURASEPA", cantidadDevolver + "");

            final int resumenProductoId = eventRealm.getId();
            Log.d("resumenProductoId", resumenProductoId + "");

            // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
            Realm realm3 = Realm.getDefaultInstance();
            realm3.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm3) {

                    Inventario inventario = realm3.where(Inventario.class).equalTo("product_id", eventRealm.getProduct_id()).findFirst();

                    if (inventario != null) {
                        idInvetarioSelec = inventario.getId();
                        amount_inventario = Double.valueOf(inventario.getAmount());
                        Log.d("idinventario", idInvetarioSelec+"");

                    } else {
                        amount_inventario = 0.0;
                        // increment index
                        Number currentIdNum = realm3.where(Inventario.class).max("id");

                        if (currentIdNum == null) {
                            nextId = 1;
                        } else {
                            nextId = currentIdNum.intValue() + 1;
                        }

                        Inventario invnuevo= new Inventario(); // unmanaged
                        invnuevo.setId(nextId);
                        invnuevo.setProduct_id(eventRealm.getProduct_id());
                        invnuevo.setInitial(String.valueOf("0"));
                        invnuevo.setAmount(String.valueOf(cantidadDevolver));
                        invnuevo.setAmount_dist(String.valueOf("0"));
                        invnuevo.setDistributor(String.valueOf("0"));

                        realm3.insertOrUpdate(invnuevo);
                        Log.d("idinvNUEVOCREADO", invnuevo +"");
                    }

                    realm3.close();
                }
            });


            // OBTENER NUEVO AMOUNT_DIST
            final Double nuevoAmountDevuelto =  cantidadDevolver + amount_inventario;
            Log.d("nuevoAmount",nuevoAmountDevuelto+"");

            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT_DIST EN EL INVENTARIO
            final Realm realm2 = Realm.getDefaultInstance();
            realm2.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm2) {
                    Inventario inv_actualizado = realm2.where(Inventario.class).equalTo("id", idInvetarioSelec).findFirst();
                    inv_actualizado.setAmount(String.valueOf(nuevoAmountDevuelto));
                    realm2.insertOrUpdate(inv_actualizado);
                    realm2.close();
                }
            });

        }
        session.guardarDatosBloquearBotonesDevolver(0);
        }

    }


