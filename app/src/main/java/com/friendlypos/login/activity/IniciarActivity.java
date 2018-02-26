package com.friendlypos.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.activity.MenuPrincipal;

public class IniciarActivity extends AppCompatActivity {
    SessionPrefes session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionPrefes(getApplicationContext());

        //String adsadas = session.getTiempo();
       // Log.d("#fsfsdf", adsadas);

        if (session.isLoggedIn() == false){
            //Toast.makeText(this, "false" + "",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else if (session.isLoggedIn()== true){
          //  Toast.makeText(this, "true" + "",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, MenuPrincipal.class));
            finish();
        }
/*
        else if (session.getTiempo().equals("31536000")){
            startActivity(new Intent(this, MenuPrincipal.class));
            finish();
        }*/
    }

}
