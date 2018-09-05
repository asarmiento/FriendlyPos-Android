package com.friendlypos.reimpresion_pedidos.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
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
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecClienteFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.fragment.DistTotalizarFragment;
import com.friendlypos.distribucion.modelo.sale;
import com.friendlypos.distribucion.util.Adapter;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.activity.ProductosActivity;
import com.friendlypos.reimpresion_pedidos.fragment.ReimPedidoResumenFragment;
import com.friendlypos.reimpresion_pedidos.fragment.ReimPedidoSelecClienteFragment;
import com.friendlypos.reimpresion_pedidos.fragment.ReimPedidoSelecProductoFragment;
import com.friendlypos.reimpresion_pedidos.fragment.ReimPedidoTotalizarFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ReimprimirPedidosActivity extends BluetoothActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;

    private String invoiceId;
    private String metodoPagoCliente;
    private String creditoLimiteCliente = null;
    private String dueCliente;

    private double totalizarSubGrabado;
    private double totalizarSubExento;
    private double totalizarSubTotal;
    private double totalizarDescuento;
    private double totalizarImpuestoIVA;
    private double totalizarTotal;
    private double totalizarTotalDouble;

    private int selecClienteTab;

    private int selecColorCliente;

    ProgressDialog progressDialog;

    public void cleanTotalize() {
        totalizarSubGrabado = 0.0;
        totalizarSubExento = 0.0;
        totalizarSubTotal = 0.0;
        totalizarDescuento = 0.0;
        totalizarImpuestoIVA = 0.0;
        totalizarTotal = 0.0;
        totalizarTotalDouble = 0.0;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMetodoPagoCliente() {
        return metodoPagoCliente;
    }

    public void setMetodoPagoCliente(String metodoPagoCliente) {
        this.metodoPagoCliente = metodoPagoCliente;
    }


    public String getCreditoLimiteCliente() {
        return creditoLimiteCliente;
    }

    public void setCreditoLimiteCliente(String creditoLimiteCliente) {
        this.creditoLimiteCliente = creditoLimiteCliente;
    }

    public String getDueCliente() {
        return dueCliente;
    }

    public void setDueCliente(String dueCliente) {
        this.dueCliente = dueCliente;
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

    public int getSelecClienteTab() {
        return selecClienteTab;
    }

    public void setSelecClienteTab(int selecClienteTab) {
        this.selecClienteTab = selecClienteTab;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimprimir_pedido);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbarReimprimirPedido);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        connectToPrinter();
        /*toolbar = (Toolbar) findViewById(R.id.toolbarDistribucion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerReimprimirPedido);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabsReimprimirPedido);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabCliente = getSelecClienteTab();
                if (tabCliente == 0 && tab.getPosition() != 0) {

                    Functions.CreateMessage(ReimprimirPedidosActivity.this, "Reimprimir Pedido", "Seleccione una factura.");

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
        list.add(new ReimPedidoSelecClienteFragment());
        list.add(new ReimPedidoResumenFragment());
        list.add(new ReimPedidoSelecProductoFragment());
        list.add(new ReimPedidoTotalizarFragment());
        adapter.addFragment(list.get(0), "Seleccionar Cliente");
       adapter.addFragment(list.get(1), "Resumen");
        adapter.addFragment(list.get(2), "Seleccionar productos");
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

                Intent intent = new Intent(ReimprimirPedidosActivity.this, MenuPrincipal.class);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(ReimprimirPedidosActivity.this, MenuPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Log.d("ATRAS", "Atras");
        }
        return super.onKeyDown(keyCode, event);
    }

}

