package com.friendlysystemgroup.friendlypos.login.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.application.util.Functions.checkURL
import com.friendlysystemgroup.friendlypos.application.util.Functions.createNotification
import com.friendlysystemgroup.friendlypos.login.util.Properties
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ConfiguracionActivity : AppCompatActivity() {
    var webServiceUrl: TextView? = null
    var save: FloatingActionButton? = null
    var properties: Properties? = null
    var nombreURL: String? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)
        properties = Properties(this)
        webServiceUrl = findViewById<View>(R.id.txtwebservice) as TextView

        nombreURL = properties!!.urlWebsrv

        webServiceUrl!!.text = nombreURL

        save = findViewById<View>(R.id.floating) as FloatingActionButton
        save!!.setOnClickListener {
            try {
                save()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun save() {
        if (webServiceUrl!!.text.toString() == nombreURL) {
            Toast.makeText(this, "No se mismo $nombreURL", Toast.LENGTH_SHORT).show()
            createNotification(
                this,
                100,
                "Cambiando el WebService",
                "No se modifico el webservice ya que es el mismo"
            )
        } else {
            if (checkURL(webServiceUrl!!.text.toString())) {
                properties!!.urlWebsrv = (webServiceUrl!!.text.toString())
                Toast.makeText(this, "WebService Mod $nombreURL", Toast.LENGTH_SHORT).show()
                createNotification(
                    this,
                    100,
                    "Cambiando el WebService",
                    "WebService Modificado Correctamente"
                )
            } else {
                Toast.makeText(this, "No se mod url $nombreURL", Toast.LENGTH_SHORT).show()
                createNotification(
                    this,
                    100,
                    "Cambiando el WebService",
                    "No se modifico el webservice ya el Url Es invalido"
                )
            }
        }
        val i = Intent(applicationContext, LoginActivity::class.java)
        startActivity(i)
    }
}