package com.friendlypos.ventadirecta.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

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
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.ventadirecta.delegate.PreSellInvoiceDelegateVD;
import com.friendlypos.ventadirecta.fragment.VentaDirSelecClienteFragment;
import com.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class VentaDirectaActivity extends BluetoothActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int invoiceIdPreventa;
    private String metodoPagoClienteVentaDirecta;
    private String creditoLimiteClienteVentaDirecta = null;
    private int selecClienteTabVentaDirecta;
    private String dueClienteVentaDirecta;

    private double totalizarSubGrabado;
    private double totalizarSubExento;
    private double totalizarSubTotal;
    private double totalizarDescuento;
    private double totalizarImpuestoIVA;
    private double totalizarTotal;
    private double totalizarTotalDouble;

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
        setContentView(R.layout.activity_ventadirecta);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarVentaDirecta);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

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
     /*   list.add(new PrevSelecProductoFragment());
        list.add(new PrevResumenFragment());
        list.add(new PrevTotalizarFragment());*/
        adapter.addFragment(list.get(0), "Seleccionar Cliente");
     /*   adapter.addFragment(list.get(1), "Seleccionar Productos");
        adapter.addFragment(list.get(2), "Resumen");
        adapter.addFragment(list.get(3), "Totalizar");*/
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

                Intent intent = new Intent(VentaDirectaActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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

    public void initCurrentInvoice(String id, String branch_office_id, String numeration, double latitude, double longitude,
                                   String date, String times, String date_presale, String times_presale, String due_data,
                                   String invoice_type_id, String payment_method_id, String totalSubtotal, String totalGrabado,
                                   String totalExento, String totalDescuento, String percent_discount, String totalImpuesto,
                                   String totalTotal, String changing, String notes, String canceled,
                                   String paid_up, String paid, String created_at, String idUsuario,
                                   String idUsuarioAplicado) {
        preSellInvoiceDelegate.initInvoiceDetalleVentaDirecta(id, branch_office_id, numeration, latitude, longitude,
                date, times, date_presale, times_presale, due_data,
                invoice_type_id, payment_method_id, totalSubtotal, totalGrabado,
                totalExento, totalDescuento, percent_discount, totalImpuesto,
                totalTotal, changing, notes, canceled,
                paid_up, paid, created_at, idUsuario,
                idUsuarioAplicado);
    }

    public void initCurrentVenta(String p_id, String p_invoice_id, String p_customer_id, String p_customer_name,
                                 String p_cash_desk_id, String p_sale_type, String p_viewed, String p_applied,
                                 String p_created_at, String p_updated_at, String p_reserved, int aplicada, int subida, int facturaDePreventa) {
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
            Intent intent = new Intent(VentaDirectaActivity.this, MenuPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Log.d("ATRAS", "Atras");
        }
        return super.onKeyDown(keyCode, event);
    }
}

