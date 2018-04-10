package com.friendlypos.preventas.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.os.Handler;
import com.friendlypos.R;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.util.Adapter;
import com.friendlypos.preventas.delegate.PreSellInvoiceDelegate;
import com.friendlypos.preventas.fragment.PrevSelecClienteFragment;
import com.friendlypos.preventas.fragment.PrevSelecProductoFragment;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class PreventaActivity extends BluetoothActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int invoiceIdPreventa;
    private String metodoPagoClientePreventa;
    private String creditoLimiteClientePreventa = null;
    private int selecClienteTabPreventa;
    private String dueClientePreventa;

    private PreSellInvoiceDelegate preSellInvoiceDelegate;

    public int getInvoiceIdPreventa() {
        return invoiceIdPreventa;
    }

    public void setInvoiceIdPreventa(int invoiceIdPreventa) {
        this.invoiceIdPreventa = invoiceIdPreventa;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
      /*    list.add(new DistSelecProductoFragment());
        list.add(new DistTotalizarFragment());*/
        adapter.addFragment(list.get(0), "Seleccionar Cliente");
        adapter.addFragment(list.get(1), "Seleccionar Productos");
    /*  adapter.addFragment(list.get(2), "Resumen");
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

                Intent intent = new Intent(PreventaActivity.this, MenuPrincipal.class);
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

    public invoiceDetallePreventa getCurrentInvoice() {
        return preSellInvoiceDelegate.getCurrentInvoice();
    }

    public void insertProduct(Pivot pivot) {
        preSellInvoiceDelegate.insertProduct(pivot);
    }

    public void initCurrentInvoice(int counter, String payMethodId) {
        preSellInvoiceDelegate.initInvoiceDetallePreventa(counter, payMethodId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preSellInvoiceDelegate.destroy();
        preSellInvoiceDelegate = null;
    }
}

