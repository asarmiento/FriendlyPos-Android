package com.friendlypos.Recibos.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;

import com.friendlypos.R;
import com.friendlypos.Recibos.delegate.PreSellRecibosDelegate;
import com.friendlypos.Recibos.fragments.RecibosAplicarFragment;
import com.friendlypos.Recibos.fragments.RecibosClientesFragment;
import com.friendlypos.Recibos.fragments.RecibosResumenFragment;
import com.friendlypos.Recibos.fragments.RecibosSeleccionarFacturaFragment;
import com.friendlypos.Recibos.modelo.receipts;
import com.friendlypos.Recibos.modelo.receiptsDetalle;
import com.friendlypos.Recibos.modelo.recibos;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecClienteFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.fragment.DistTotalizarFragment;
import com.friendlypos.distribucion.modelo.Pivot;
import com.friendlypos.distribucion.modelo.invoice;
import com.friendlypos.distribucion.util.Adapter;
import com.friendlypos.preventas.delegate.PreSellInvoiceDelegate;
import com.friendlypos.preventas.modelo.invoiceDetallePreventa;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.realm.RealmList;

public class RecibosActivity extends BluetoothActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private int selecClienteTabRecibos;
    private int selecFacturaTabRecibos;
    private String invoiceIdRecibos;
    private String clienteIdRecibos;
    private double totalFacturaSelec;
    private double totalizarTotal;
    private double totalizarCancelado;
    private double canceladoPorFactura;
    private double totalizarPagado;
    private double montoPagar;
    private double totalizarTotalCheck;
    private double totalizarFinal;
    private double montoAgregadoRestante;
    private double totalizarFinalCliente;
    private String receipts_id_num;

    private PreSellRecibosDelegate preSellRecibosDelegate;


    public double getMontoAgregadoRestante() {
        return montoAgregadoRestante;
    }

    public void setMontoAgregadoRestante(double montoAgregadoRestante) {
        this.montoAgregadoRestante = this.montoAgregadoRestante - montoAgregadoRestante;
    }

    public double getMontoPagar() {
        return montoPagar;
    }

    public void setMontoPagar(double montoPagar) {
        this.montoPagar = montoPagar;
    }

    public double getTotalizarPagado() {
        return totalizarPagado;
    }

    public void setTotalizarPagado(double totalizarPagado) {
        this.totalizarPagado = totalizarPagado;
    }

    public double getTotalizarCancelado() {
        return totalizarCancelado;
    }

    public void setTotalizarCancelado(double totalizarCancelado) {
        this.totalizarCancelado = this.totalizarCancelado + totalizarCancelado;
    }

    public double getCanceladoPorFactura() {
        return canceladoPorFactura;
    }

    public void setCanceladoPorFactura(double canceladoPorFactura) {
        this.canceladoPorFactura = this.canceladoPorFactura + canceladoPorFactura;
    }

    public double getTotalizarFinal() {
        return totalizarFinal;
    }

    public void setTotalizarFinal(double totalizarFinal) {
        this.totalizarFinal = this.totalizarFinal + totalizarFinal;
    }

    public double getTotalizarTotal() {
        return totalizarTotal;
    }

    public void setTotalizarTotal(double totalizarTotal) {
        this.totalizarTotal = this.totalizarTotal + totalizarTotal;
    }

    public int getSelecClienteTabRecibos() {
        return selecClienteTabRecibos;
    }

    public void setSelecClienteTabRecibos(int selecClienteTabRecibos) {
        this.selecClienteTabRecibos = selecClienteTabRecibos;
    }

    public int getSelecFacturaTabRecibos() {
        return selecFacturaTabRecibos;
    }

    public void setSelecFacturaTabRecibos(int selecFacturaTabRecibos) {
        this.selecFacturaTabRecibos = selecFacturaTabRecibos;
    }

    public String getInvoiceIdRecibos() {
        return invoiceIdRecibos;
    }

    public void setInvoiceIdRecibos(String invoiceIdRecibos) {
        this.invoiceIdRecibos = invoiceIdRecibos;
    }

    public double getTotalFacturaSelec() {
        return totalFacturaSelec;
    }

    public void setTotalFacturaSelec(double totalFacturaSelec) {
        this.totalFacturaSelec = totalFacturaSelec;
    }

    public String getClienteIdRecibos() {
        return clienteIdRecibos;
    }

    public void setClienteIdRecibos(String clienteIdRecibos) {
        this.clienteIdRecibos = clienteIdRecibos;
    }


    public double getTotalizarTotalCheck() {
        return totalizarTotalCheck;
    }

    public void setTotalizarTotalCheck(double totalizarTotalCheck) {
        this.totalizarTotalCheck = this.totalizarTotalCheck + totalizarTotalCheck;
    }


    public double getTotalizarFinalCliente() {
        return totalizarFinalCliente;
    }

    public void setTotalizarFinalCliente(double totalizarFinalCliente) {
        this.totalizarFinalCliente = this.totalizarFinalCliente + totalizarFinalCliente;
    }

    public String getReceipts_id_num() {
        return receipts_id_num;
    }

    public void setReceipts_id_num(String receipts_id_num) {
        this.receipts_id_num = receipts_id_num;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recibos);

        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbarRecibos);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        connectToPrinter();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerRecibos);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        preSellRecibosDelegate = new PreSellRecibosDelegate(this);
        tabLayout = (TabLayout) findViewById(R.id.tabsRecibos);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabCliente = getSelecClienteTabRecibos();

                if (tabCliente == 0 && tab.getPosition() != 0) {
                    Functions.CreateMessage(RecibosActivity.this, "Recibos", "Seleccione un cliente.");

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
        list.add(new RecibosClientesFragment());
       list.add(new RecibosSeleccionarFacturaFragment());
         list.add(new RecibosResumenFragment());
       list.add(new RecibosAplicarFragment());
        adapter.addFragment(list.get(0), "Seleccionar Cliente");
      adapter.addFragment(list.get(1), "Selecionar Factura");
          adapter.addFragment(list.get(2), "Resumen");
        adapter.addFragment(list.get(3), "Aplicar");
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

                Intent intent = new Intent(RecibosActivity.this, MenuPrincipal.class);
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

    public void cleanTotalize() {
        totalizarTotal = 0.0;
        totalizarCancelado = 0.0;

    }
    public void cleanTotalizeCkeck() {

        totalizarTotalCheck = 0.0;
    }

    public void cleanTotalizeFinal() {

        totalizarFinalCliente = 0.0;
    }

    public receiptsDetalle getCurrentRecibos() {
        return preSellRecibosDelegate.getCurrentRecibos();
    }


    public receipts getReceiptsByReceiptsDetalle() {
        return preSellRecibosDelegate.getReceiptsByReceiptsDetalle();
    }

    public List<recibos> getAllRecibosDelegate() {
        return preSellRecibosDelegate.getAllRecibos();
    }

    public void initCurrentRecibos(String receipts_id, String customer_id, String reference,
                                   String date, String sum, double balance, String notes) {
        preSellRecibosDelegate.initReciboDetalle(receipts_id, customer_id, reference,
                date, sum, balance, notes);
    }

    public void insertRecibo(recibos recibo) {
        preSellRecibosDelegate.insertRecibo(recibo);
    }

    public void initRecibo(int pos) {
        preSellRecibosDelegate.initRecibo(pos);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(RecibosActivity.this, MenuPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Log.d("ATRAS", "Atras");
        }
        return super.onKeyDown(keyCode, event);
    }
}
