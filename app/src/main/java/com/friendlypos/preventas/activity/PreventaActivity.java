package com.friendlypos.preventas.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.Adapter;
import com.friendlypos.preventas.delegate.PreSellInvoiceDelegate;
import com.friendlypos.preventas.fragment.PrevResumenFragment;
import com.friendlypos.preventas.fragment.PrevSelecClienteFragment;
import com.friendlypos.preventas.fragment.PrevSelecProductoFragment;
import com.friendlypos.preventas.fragment.PrevTotalizarFragment;
import com.friendlypos.preventas.modelo.Numeracion;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.activity.ProductosActivity;
import com.friendlypos.ventadirecta.activity.VentaDirectaActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class PreventaActivity extends BluetoothActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int invoiceIdPreventa;
    private String metodoPagoClientePreventa;
    private String creditoLimiteClientePreventa = null;
    private int selecClienteTabPreventa;
    private String selecClienteFacturacionPreventa;
    private int activoColorPreventa;
    private String dueClientePreventa;
    int nextId;
    private double totalizarSubGrabado;
    private double totalizarSubExento;
    private double totalizarSubTotal;
    private double totalizarDescuento;
    private double totalizarImpuestoIVA;
    private double totalizarTotal;
    private double totalizarTotalDouble;

    private PreSellInvoiceDelegate preSellInvoiceDelegate;

    public int getInvoiceIdPreventa() {
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

    public String getSelecClienteFacturacionPreventa() {
        return selecClienteFacturacionPreventa;
    }

    public void setSelecClienteFacturacionPreventa(String selecClienteFacturacionPreventa) {
        this.selecClienteFacturacionPreventa = selecClienteFacturacionPreventa;
    }

    public String getMetodoPagoClientePreventa() {
        return metodoPagoClientePreventa;
    }

    public void setMetodoPagoClientePreventa(String metodoPagoClientePreventa) {
        this.metodoPagoClientePreventa = metodoPagoClientePreventa;
    }

    public String getCreditoLimiteClientePreventa() {
        return creditoLimiteClientePreventa;
    }

    public void setCreditoLimiteClientePreventa(String creditoLimiteClientePreventa) {
        this.creditoLimiteClientePreventa = creditoLimiteClientePreventa;
    }

    public int getSelecClienteTabPreventa() {
        return selecClienteTabPreventa;
    }

    public void setSelecClienteTabPreventa(int selecClienteTabPreventa) {
        this.selecClienteTabPreventa = selecClienteTabPreventa;
    }

    public String getDueClientePreventa() {
        return dueClientePreventa;
    }

    public void setDueClientePreventa(String dueClientePreventa) {
        this.dueClientePreventa = dueClientePreventa;
    }

    public int getActivoColorPreventa() {
        return activoColorPreventa;
    }

    public void setActivoColorPreventa(int activoColorPreventa) {
        this.activoColorPreventa = activoColorPreventa;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_preventa);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarPreventa);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();





        actionBar.setDisplayHomeAsUpEnabled(true);
        preSellInvoiceDelegate = new PreSellInvoiceDelegate(this);
        connectToPrinter();
        /*toolbar = (Toolbar) findViewById(R.id.toolbarDistribucion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerPreventa);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabsPreventa);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int tabCliente = getSelecClienteTabPreventa();
                if (tabCliente == 0 && tab.getPosition() != 0) {

                    Functions.CreateMessage(PreventaActivity.this, "Preventa", "Seleccione una factura.");

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
        list.add(new PrevSelecClienteFragment());
        list.add(new PrevSelecProductoFragment());
          list.add(new PrevResumenFragment());
       list.add(new PrevTotalizarFragment());
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

                int tabCliente = getSelecClienteTabPreventa();
                if (tabCliente == 1) {
                    String message = "¿Desea cancelar la factura en proceso?";
                    String titulo = "Salir";
                    SpannableString spannableString =  new SpannableString(message);
                    SpannableString spannableStringTitulo =  new SpannableString(titulo);

                    CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getApplicationContext().getAssets(), "font/monse.otf"));
                    spannableString.setSpan(typefaceSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length(), Spanned.SPAN_PRIORITY);

                    AlertDialog dialogReturnSale = new AlertDialog.Builder(PreventaActivity.this)

                            .setTitle(spannableStringTitulo)
                            .setMessage(spannableString)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    String tipoFacturacion = getSelecClienteFacturacionPreventa();

                                    if (tipoFacturacion.equals("Preventa")) {
                                        final Realm realm2 = Realm.getDefaultInstance();

                                        realm2.executeTransaction(new Realm.Transaction() {

                                            @Override
                                            public void execute(Realm realm) {
                                                // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                                                Number numero = realm.where(Numeracion.class).equalTo("sale_type", "2").max("number");

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
                                                numNuevo.setSale_type("2");
                                                numNuevo.setNumeracion_numero(nextId);

                                                realm5.insertOrUpdate(numNuevo);
                                                Log.d("idinvNUEVOCREADO", numNuevo + "");


                                            }

                                        });
                                        realm5.close();
                                    } else if (tipoFacturacion.equals("Proforma")) {
                                        final Realm realm2 = Realm.getDefaultInstance();

                                        // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                                        realm2.executeTransaction(new Realm.Transaction() {

                                            @Override
                                            public void execute(Realm realm) {

                                                Number numero = realm.where(Numeracion.class).equalTo("sale_type", "3").max("number");
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
                                                numNuevo.setSale_type("3");
                                                numNuevo.setNumeracion_numero(nextId);

                                                realm5.insertOrUpdate(numNuevo);
                                                Log.d("idinvNUEVOCREADO", numNuevo + "");


                                            }

                                        });
                                        realm5.close();


                                    }
                                    Intent intent = new Intent(PreventaActivity.this, MenuPrincipal.class);
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
                    Intent intent = new Intent(PreventaActivity.this, MenuPrincipal.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();}
                return true;

            default:
                return super.onOptionsItemSelected(item);
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

    public invoiceDetallePreventa getCurrentInvoice() {
        return preSellInvoiceDelegate.getCurrentInvoice();
    }

    public sale getCurrentVenta() {
        return preSellInvoiceDelegate.getCurrentVenta();
    }

    public invoice getInvoiceByInvoiceDetalles() {
        return preSellInvoiceDelegate.getInvoiceByInvoiceDetalle();
    }

    public List<Pivot> getAllPivotDelegate() {
        return preSellInvoiceDelegate.getAllPivot();
    }

    public void insertProduct(Pivot pivot) {
        preSellInvoiceDelegate.insertProduct(pivot);
    }

    public void borrarProduct(Pivot pivot) {
        preSellInvoiceDelegate.borrarProductoPreventa(pivot);
    }

    public void initCurrentInvoice(String id, String branch_office_id, String numeration, double latitude, double longitude,
                                   String date, String times, String date_presale, String times_presale, String due_data,
                                   String invoice_type_id, String payment_method_id, String totalSubtotal, String totalGrabado,
                                   String totalExento, String totalDescuento, String percent_discount, String totalImpuesto,
                                   String totalTotal, String changing, String notes, String canceled,
                                   String paid_up, String paid, String created_at, String idUsuario,
                                   String idUsuarioAplicado) {
        preSellInvoiceDelegate.initInvoiceDetallePreventa(id, branch_office_id, numeration, latitude, longitude,
                date, times, date_presale, times_presale, due_data,
                invoice_type_id, payment_method_id, totalSubtotal, totalGrabado,
                totalExento, totalDescuento, percent_discount, totalImpuesto,
                totalTotal, changing, notes, canceled,
                paid_up, paid, created_at, idUsuario,
                idUsuarioAplicado);
    }

    public void initCurrentVenta(String p_id, String p_invoice_id, String p_customer_id, String p_customer_name,
                                 String p_cash_desk_id, String p_sale_type, String p_viewed, String p_applied,
                                 String p_created_at, String p_updated_at, String p_reserved, int aplicada, int subida, String facturaDePreventa) {
        preSellInvoiceDelegate.initVentaDetallesPreventa(p_id, p_invoice_id, p_customer_id, p_customer_name,
                p_cash_desk_id, p_sale_type, p_viewed, p_applied,
                p_created_at, p_updated_at, p_reserved, aplicada, subida, facturaDePreventa);
    }

    public void initProducto(int pos) {
        preSellInvoiceDelegate.initProduct(pos);
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


    private List<Numeracion> getList(){

        Realm realm = Realm.getDefaultInstance();

            RealmQuery<Numeracion> query = realm.where(Numeracion.class).equalTo("rec_creada", 1).equalTo("rec_aplicada", 0);
        RealmResults<Numeracion> result1 = query.findAll();

            if(result1.size() == 0){
                Log.d("nadaCreados", "nada" + "");
                //Toast.makeText(getApplicationContext(),"NadaPrev" ,Toast.LENGTH_LONG).show();
            }
            else{
                for (int i = 0; i < result1.size(); i++) {
                    List<Numeracion> salesList1 = realm.where(Numeracion.class).equalTo("rec_creada", 1).equalTo("rec_aplicada", 0).findAll();
                    int numero = salesList1.get(i).getNumeracion_numero();
                    String tipo = salesList1.get(i).getSale_type();

                    nextId = numero - 1;

                    if(tipo.equals("2")){
                        final Realm realm5 = Realm.getDefaultInstance();
                        realm5.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm5) {
                                Numeracion numNuevo = new Numeracion(); // unmanaged
                                numNuevo.setSale_type("2");
                                numNuevo.setNumeracion_numero(nextId);

                                realm5.insertOrUpdate(numNuevo);
                                Log.d("VDNumNuevaAtras", numNuevo + "");


                            }

                        });
                        realm5.close();
                    }
                    else if(tipo.equals("3")){

                        final Realm realm5 = Realm.getDefaultInstance();
                        realm5.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm5) {
                                Numeracion numNuevo = new Numeracion(); // unmanaged
                                numNuevo.setSale_type("3");
                                numNuevo.setNumeracion_numero(nextId);
                                realm5.insertOrUpdate(numNuevo);
                                Log.d("VDNumNuevaAtras", numNuevo + "");


                            }

                        });
                        realm5.close();
                    }
                }

            }

        return result1;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            int tabCliente = getSelecClienteTabPreventa();
            if (tabCliente == 1) {

                String message = "¿Desea cancelar la factura en proceso?";
                String titulo = "Salir";
                SpannableString spannableString =  new SpannableString(message);
                SpannableString spannableStringTitulo =  new SpannableString(titulo);

                CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(TypefaceUtils.load(getApplicationContext().getAssets(), "font/monse.otf"));
                spannableString.setSpan(typefaceSpan, 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length(), Spanned.SPAN_PRIORITY);

                AlertDialog dialogReturnSale = new AlertDialog.Builder(PreventaActivity.this)

                        .setTitle(spannableStringTitulo)
                        .setMessage(spannableString)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String tipoFacturacion = getSelecClienteFacturacionPreventa();

                                if (tipoFacturacion.equals("Preventa")) {
                                    final Realm realm2 = Realm.getDefaultInstance();

                                    realm2.executeTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm) {
                                            // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                                            Number numero = realm.where(Numeracion.class).equalTo("sale_type", "2").max("number");

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
                                            numNuevo.setSale_type("2");
                                            numNuevo.setNumeracion_numero(nextId);

                                            realm5.insertOrUpdate(numNuevo);
                                            Log.d("idinvNUEVOCREADO", numNuevo + "");


                                        }

                                    });
                                    realm5.close();
                                } else if (tipoFacturacion.equals("Proforma")) {
                                    final Realm realm2 = Realm.getDefaultInstance();

                                    // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                                    realm2.executeTransaction(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm) {

                                            Number numero = realm.where(Numeracion.class).equalTo("sale_type", "3").max("number");
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
                                            numNuevo.setSale_type("3");
                                            numNuevo.setNumeracion_numero(nextId);

                                            realm5.insertOrUpdate(numNuevo);
                                            Log.d("idinvNUEVOCREADO", numNuevo + "");


                                        }

                                    });
                                    realm5.close();


                            }
                                Intent intent = new Intent(PreventaActivity.this, MenuPrincipal.class);
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
                Intent intent = new Intent(PreventaActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();}

        }
        return super.onKeyDown(keyCode, event);
    }
}

