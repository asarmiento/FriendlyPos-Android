package com.friendlypos.distribucion.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.friendlypos.R;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecClienteFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.fragment.DistTotalizarFragment;
import com.friendlypos.distribucion.util.Adapter;
import com.friendlypos.principal.activity.MenuPrincipal;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class DistribucionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String invoiceId;
    private String metodoPagoCliente;
    private String creditoLimiteCliente= "";
    private String dueCliente;
    private String creditoLimiteClienteSlecc = "";


    private double totalizarSubGrabado;
    private double totalizarSubExento;
    private double totalizarSubTotal;
    private double totalizarDescuento;
    private double totalizarImpuestoIVA;
    private double totalizarTotal;
    private double totalizarTotalDouble;


    ProgressDialog progressDialog;

    public void cleanTotalize() {
        totalizarSubGrabado= 0.0;
        totalizarSubExento = 0.0;
        totalizarSubTotal = 0.0;
        totalizarDescuento= 0.0;
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

  /*  public String getCreditoLimiteClienteSlecc() {
        return creditoLimiteClienteSlecc;
    }

    public void setCreditoLimiteClienteSlecc(String creditoLimiteClienteSlecc) {
        this.creditoLimiteClienteSlecc = creditoLimiteClienteSlecc;
    }*/

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
        this.totalizarSubTotal = totalizarSubTotal;
    }

    public double getTotalizarDescuento() {
        return totalizarDescuento;
    }

    public void setTotalizarDescuento(double totalizarDescuento) {
        this.totalizarDescuento = totalizarDescuento;
    }

    public double getTotalizarImpuestoIVA() {
        return totalizarImpuestoIVA;
    }

    public void setTotalizarImpuestoIVA(double totalizarImpuestoIVA) {
        this.totalizarImpuestoIVA = totalizarImpuestoIVA;
    }

    public double getTotalizarTotalDouble() {
        return totalizarTotalDouble;
    }

    public void setTotalizarTotalDouble(double totalizarTotalDouble) {
        this.totalizarTotalDouble = totalizarTotalDouble;
    }

    public double getTotalizarTotal() {
        return totalizarTotal;
    }

    public void setTotalizarTotal(double totalizarTotal) {
        this.totalizarTotal = totalizarTotal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribucion);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Wait...");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

       /* tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
           public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != 0) {

                    Functions.CreateMessage(DistribucionActivity.this, "Distribución", "Seleccione una factura.");

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
        });*/
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        final List<BaseFragment> list = new ArrayList<>();
        list.add(new DistSelecClienteFragment());
        list.add(new DistResumenFragment());
        list.add(new DistSelecProductoFragment());
        list.add(new DistTotalizarFragment());
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
                progressDialog.dismiss();
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

                Intent intent = new Intent(DistribucionActivity.this, MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

