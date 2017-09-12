package com.friendlypos.principal.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.principal.fragment.ConfiguracionFragment;
import com.friendlypos.principal.fragment.FragmentA;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

import butterknife.Bind;

public class MenuPrincipal extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener
       /* implements NavigationView.OnNavigationItemSelectedListener*/ {
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

    @Bind(R.id.click1)
    LinearLayout clk1;

    @Bind(R.id.click2)
    LinearLayout clk2;

    @Bind(R.id.click3)
    LinearLayout clk3;

    @Bind(R.id.click4)
    LinearLayout clk4;

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

            case R.id.pmnuDelete:
                Toast.makeText(MenuPrincipal.this, "You clicked delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pmnuEdit:
                Toast.makeText(MenuPrincipal.this, "You clicked edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pmnuShare:
                Toast.makeText(MenuPrincipal.this, "You clicked share", Toast.LENGTH_SHORT).show();
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
        Class fragmentClass =  FragmentA.class;
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
        Class fragmentClass =  FragmentA.class;

        switch (view.getId()){
            case R.id.click1:
                fragmentClass = FragmentA.class;
           /*  clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));*/
                break;
            case R.id.click2:
                /*fragmentClass = FragmentB.class;
                clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));*/
                break;
            case R.id.click3:
             /*   fragmentClass = FragmentC.class;
              clk1.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk2.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));
                clk3.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_clickb, null));
                clk4.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_click, null));*/
                break;
            case R.id.click4:
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


}
