package com.friendlysystemgroup.friendlypos.principal.activity

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlysystemgroup.friendlypos.crearCliente.activity.crearCliente
import com.friendlysystemgroup.friendlypos.login.activity.LoginActivity
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes.Companion.get
import com.friendlysystemgroup.friendlypos.principal.adapters.ClientesAdapter
import com.friendlysystemgroup.friendlypos.principal.modelo.Clientes
import io.realm.Realm
import java.util.Locale

class ClientesActivity : BluetoothActivity(), SearchView.OnQueryTextListener {
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    private var adapter: ClientesAdapter? = null
    private var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_clientes)
        

        // Redirección al Login
        if (!get(this).isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        connectToPrinter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        adapter = ClientesAdapter(this@ClientesActivity, list)
        recyclerView.adapter = adapter

        Log.d("lista", list.toString() + "")
    }


    private val list: List<Clientes>
        get() {
            realm = Realm.getDefaultInstance()
            val query = realm.where(Clientes::class.java)
            val result1 = query.findAll()
            if (result1.size == 0) {
                Toast.makeText(
                    applicationContext,
                    "Favor descargar datos primero",
                    Toast.LENGTH_LONG
                ).show()
            }
            return result1
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@ClientesActivity, MenuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_cliente, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)

        MenuItemCompat.setOnActionExpandListener(
            item,
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    // Do something when collapsed
                    adapter!!.setFilter(this.list)
                    return true // Return true to collapse action view
                }

                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    // Do something when expanded
                    return true // Return true to expand action view
                }
            })
        return super.onCreateOptionsMenu(menu)
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_crearNuevo -> {
                val intent = Intent(application, crearCliente::class.java)
                startActivity(intent)
            }
        }
        return false
    }


    override fun onQueryTextChange(newText: String): Boolean {
        val filteredModelList = filter(list, newText)
        adapter!!.setFilter(filteredModelList)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }


    private fun filter(models: List<Clientes>, query: String): List<Clientes> {
        var query = query
        query = query.lowercase(Locale.getDefault())

        val filteredModelList: MutableList<Clientes> = ArrayList()
        for (model in models) {
            val text = model.fantasyName.lowercase(Locale.getDefault())
            Log.d("FiltroPreventa", text)
            if (text.contains(query)) {
                filteredModelList.add(model)
            }
        }
        return filteredModelList
    }


    private fun connectToPrinter() {
        //if(bluetoothStateChangeReceiver.isBluetoothAvailable()) {
        preferences
        if (printer_enabled) {
            if (printer == null || printer == "") {
//                AlertDialog d = new AlertDialog.Builder(context)
//                        .setTitle(getResources().getString(R.string.printer_alert))
//                        .setMessage(getResources().getString(R.string.message_printer_not_found))
//                        .setNegativeButton(getString(android.R.string.ok), null)
//                        .show();
            } else {
                if (!isServiceRunning(PrinterService.CLASS_NAME)) {
                    startRDService(applicationContext, printer)
                }
            }
        }
    }

    //Check if the printing service is running
    fun isServiceRunning(serviceClassName: String): Boolean {
        val activityManager =
            applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Int.MAX_VALUE)

        for (runningServiceInfo in services) {
            if (runningServiceInfo.service.className == serviceClassName) {
                return true
            }
        }
        return false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            val intent = Intent(this@ClientesActivity, MenuPrincipal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            Log.d("ATRAS", "Atras")
        }
        return super.onKeyDown(keyCode, event)
    }
}
