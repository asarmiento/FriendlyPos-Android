package com.friendlypos.reenvio_email.activity;

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

import com.friendlypos.R;
import com.friendlypos.application.bluetooth.PrinterService;
import com.friendlypos.application.util.Functions;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.distribucion.fragment.BaseFragment;
import com.friendlypos.distribucion.fragment.DistResumenFragment;
import com.friendlypos.distribucion.fragment.DistSelecClienteFragment;
import com.friendlypos.distribucion.fragment.DistSelecProductoFragment;
import com.friendlypos.distribucion.fragment.DistTotalizarFragment;
import com.friendlypos.distribucion.util.Adapter;
import com.friendlypos.principal.activity.BluetoothActivity;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.reenvio_email.fragment.EmailSelecClienteFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.realm.Realm;

public class EmailActivity extends BluetoothActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private Realm realm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbarEmail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        connectToPrinter();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerEmail);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabsEmail);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            /*    int tabCliente = getSelecClienteTab();
                if (tabCliente == 0 && tab.getPosition() != 0) {

                    Functions.CreateMessage(EmailActivity.this, "Email", "Seleccione una factura.");

                    new Handler().postDelayed(
                            new Runnable() {

                                @Override
                                public void run() {
                                    tabLayout.getTabAt(0).select();
                                }
                            }, 100);
                }
                else {
                }*/
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
        getPreferences();
        if (printer_enabled) {
            if (printer == null || printer.equals("")) {
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
        list.add(new EmailSelecClienteFragment());
       /* list.add(new DistResumenFragment());
        list.add(new DistSelecProductoFragment());
        list.add(new DistTotalizarFragment());*/
        adapter.addFragment(list.get(0), "Seleccionar Cliente");
     /*   adapter.addFragment(list.get(1), "Resumen");
        adapter.addFragment(list.get(2), "Seleccionar productos");
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

                Intent intent = new Intent(EmailActivity.this, MenuPrincipal.class);
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
            Intent intent = new Intent(EmailActivity.this, MenuPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Log.d("ATRAS", "Atras");
        }
        return super.onKeyDown(keyCode, event);
    }
}

