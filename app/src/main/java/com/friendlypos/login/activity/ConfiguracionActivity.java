package com.friendlypos.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.application.util.Functions;
import com.friendlypos.login.util.Properties;

public class ConfiguracionActivity extends AppCompatActivity {
    TextView webServiceUrl;
    FloatingActionButton save;
    Properties properties;
    String nombreURL;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        properties = new Properties(this);
        webServiceUrl = (TextView) findViewById(R.id.txtwebservice);

        nombreURL = properties.getUrlWebsrv();

        webServiceUrl.setText(nombreURL);

        save = (FloatingActionButton) findViewById(R.id.floating);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void save() {
        if (webServiceUrl.getText().toString().equals(nombreURL)) {
            Toast.makeText(this,"No se mismo " + nombreURL + "",Toast.LENGTH_SHORT).show();
            Functions.createNotification(this, 100, "Cambiando el WebService", "No se modifico el webservice ya que es el mismo");
        } else {
            if (Functions.checkURL(webServiceUrl.getText().toString())) {

                properties.setUrlWebsrv((webServiceUrl.getText().toString()));
                Toast.makeText(this,"WebService Mod " + nombreURL + "",Toast.LENGTH_SHORT).show();
                Functions.createNotification(this, 100, "Cambiando el WebService", "WebService Modificado Correctamente");
            } else {
                Toast.makeText(this,"No se mod url " + nombreURL + "",Toast.LENGTH_SHORT).show();
                Functions.createNotification(this, 100, "Cambiando el WebService", "No se modifico el webservice ya el Url Es invalido");
            }
        }
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
}