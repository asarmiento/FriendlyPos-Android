package com.friendlypos.distribucion.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

public class DistribucionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String invoiceId;

    private String totalizarSubGrabado;
    private String totalizarSubExento;
    private String totalizarSubTotal;
    private String totalizarDescuento;
    private String totalizarImpuestoIVA;
    private String totalizarTotal;


    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getTotalizarSubGrabado() {
        return totalizarSubGrabado;
    }

    public void setTotalizarSubGrabado(String totalizarSubGrabado) {
        this.totalizarSubGrabado = totalizarSubGrabado;
    }

    public String getTotalizarSubExento() {
        return totalizarSubExento;
    }

    public void setTotalizarSubExento(String totalizarSubExento) {
        this.totalizarSubExento = totalizarSubExento;
    }

    public String getTotalizarSubTotal() {
        return totalizarSubTotal;
    }

    public void setTotalizarSubTotal(String totalizarSubTotal) {
        this.totalizarSubTotal = totalizarSubTotal;
    }

    public String getTotalizarDescuento() {
        return totalizarDescuento;
    }

    public void setTotalizarDescuento(String totalizarDescuento) {
        this.totalizarDescuento = totalizarDescuento;
    }

    public String getTotalizarImpuestoIVA() {
        return totalizarImpuestoIVA;
    }

    public void setTotalizarImpuestoIVA(String totalizarImpuestoIVA) {
        this.totalizarImpuestoIVA = totalizarImpuestoIVA;
    }

    public String getTotalizarTotal() {
        return totalizarTotal;
    }

    public void setTotalizarTotal(String totalizarTotal) {
        this.totalizarTotal = totalizarTotal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distribucion);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        }}
}

