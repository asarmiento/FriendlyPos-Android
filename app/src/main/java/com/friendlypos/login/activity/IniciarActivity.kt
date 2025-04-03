package com.friendlypos.login.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.friendlypos.R
import com.friendlypos.login.util.SessionPrefes
import com.friendlypos.principal.activity.MenuPrincipal

class IniciarActivity : AppCompatActivity() {
    var session: SessionPrefes? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        session = SessionPrefes(applicationContext)

        //String adsadas = session.getTiempo();
        // Log.d("#fsfsdf", adsadas);
        if (session!!.isLoggedIn == false) {
            //Toast.makeText(this, "false" + "",Toast.LENGTH_SHORT).show();

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else if (session!!.isLoggedIn == true) {
            //  Toast.makeText(this, "true" + "",Toast.LENGTH_SHORT).show();

            startActivity(Intent(this, MenuPrincipal::class.java))
            finish()
        }
        /*
        else if (session.getTiempo().equals("31536000")){
            startActivity(new Intent(this, MenuPrincipal.class));
            finish();
        }*/
    }
}
