package com.friendlypos.principal.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.distribucion.activity.DistribucionActivity;
import com.friendlypos.principal.fragment.ConfiguracionFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.Bind;

public class MenuPrincipal extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener
       /* implements NavigationView.OnNavigationItemSelectedListener*/ {
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

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

    @Bind(R.id.clickReportes)
    LinearLayout clickReportes;

    @Bind(R.id.clickConfig)
    LinearLayout clickConfig;

    @Bind(R.id.drawer)
    DrawerLayout drawer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* Initializing();*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }



    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
            // Reflection apis to enforce show icon
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
        } catch (Exception e) {
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
                Toast.makeText(MenuPrincipal.this, "CerrarSesion", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_descargar_catalogo:
                Toast.makeText(MenuPrincipal.this, "descargar_catalogo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_descargar_inventario:
                Toast.makeText(MenuPrincipal.this, "descargar_inventario", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_descargar_deudas:
                Toast.makeText(MenuPrincipal.this, "descargar_deudas", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_subir_recibos:
                Toast.makeText(MenuPrincipal.this, "subir_recibos", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_subir_ventas:
                Toast.makeText(MenuPrincipal.this, "subir_ventas", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_devolver_inventario:
                Toast.makeText(MenuPrincipal.this, "devolver_inventario", Toast.LENGTH_SHORT).show();
                break;


        }
        return false;
    }

   /* @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/
/*
    public void Initializing(){
        Fragment fragment = null;
        Class fragmentClass =  ProductosFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }// Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
    }*/
    public void ClickNavigation(View view){
        Fragment fragment = null;
        Class fragmentClass =  ConfiguracionFragment.class;

        switch (view.getId()){

          /*   case R.id.clickClientes:
                Toast.makeText(this, "distribucion", Toast.LENGTH_SHORT).show();

                 fragmentClass = BlankFragment.class;
           clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                break;*/

           /* case R.id.clickDistribucion:
                Toast.makeText(this, "distribucion", Toast.LENGTH_SHORT).show();

               /*  fragmentClass = ProductosFragment.class;
            clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                break;*/
            case R.id.clickVentaDirecta:
                Toast.makeText(this, "ventadirecta", Toast.LENGTH_SHORT).show();

                /*fragmentClass = FragmentB.class;
                clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));*/
                break;
            case R.id.clickPreventa:
                Toast.makeText(this, "preventa", Toast.LENGTH_SHORT).show();

             /*   fragmentClass = FragmentC.class;
              clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));*/
                break;
            case R.id.clickReportes:
                Toast.makeText(this, "reportes", Toast.LENGTH_SHORT).show();

                /*fragmentClass = FragmentD.class;
               clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));*/
                break;
            case R.id.clickConfig:
                fragmentClass = ConfiguracionFragment.class;
            /*   clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));*/
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }// Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.frame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void onClickGo(View component) {

        switch (component.getId()){

            case R.id.clickClientes:
                Intent clientes;
                clientes = new Intent(MenuPrincipal.this, ClientesActivity.class);
                startActivity(clientes);
                finish();

                break;
            case R.id.clickProductos:
                Toast.makeText(this, "productos", Toast.LENGTH_SHORT).show();

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

        }
    }

}
