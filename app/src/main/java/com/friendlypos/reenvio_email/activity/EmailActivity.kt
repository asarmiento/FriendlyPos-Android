package com.friendlypos.reenvio_email.activity

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import butterknife.ButterKnife
import com.friendlypos.R
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlypos.application.bluetooth.PrinterService
import com.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlypos.application.util.Functions.CreateMessage
import com.friendlypos.distribucion.fragment.BaseFragment
import com.friendlypos.distribucion.util.Adapter
import com.friendlypos.principal.activity.BluetoothActivity
import com.friendlypos.principal.activity.MenuPrincipal
import com.friendlypos.reenvio_email.fragment.EmailSelecClienteFragment
import com.friendlypos.reenvio_email.fragment.EmailSelecFacturaFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import io.realm.Realm

class EmailActivity : BluetoothActivity() {
    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    private val realm: Realm? = null
    @JvmField
    var selecClienteTabEmail: Int = 0
    override var networkStateChangeReceiver: NetworkStateChangeReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_email)
        ButterKnife.bind(this)
        networkStateChangeReceiver = NetworkStateChangeReceiver()
        toolbar = findViewById<View>(R.id.toolbarEmail) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        actionBar!!.setDisplayHomeAsUpEnabled(true)
        connectToPrinter()

        if (!isOnline) {
            CreateMessage(
                this@EmailActivity,
                "Email",
                "Por favor revisar conexión de Internet antes de continuar"
            )
        }


        val viewPager = findViewById<View>(R.id.viewpagerEmail) as ViewPager
        if (viewPager != null) {
            setupViewPager(viewPager)
        }

        tabLayout = findViewById<View>(R.id.tabsEmail) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente: Int = this.selecClienteTabEmail
                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(this@EmailActivity, "Email", "Seleccione una factura.")

                    Handler().postDelayed(
                        { tabLayout!!.getTabAt(0)!!.select() }, 100
                    )
                } else {
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun connectToPrinter() {
        preferences
        if (printer_enabled) {
            if (printer == null || printer == "") {
            } else {
                if (!isServiceRunning(PrinterService.CLASS_NAME)) {
                    startRDService(applicationContext, printer)
                }
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(
            supportFragmentManager
        )
        val list: MutableList<BaseFragment> = ArrayList()
        list.add(EmailSelecClienteFragment())
        list.add(EmailSelecFacturaFragment())
        /*   list.add(new DistSelecProductoFragment());
        list.add(new DistTotalizarFragment());*/
        adapter.addFragment(list[0], "Seleccionar Cliente")
        adapter.addFragment(list[1], "Facturas")
        /*      adapter.addFragment(list.get(2), "Seleccionar productos");
        adapter.addFragment(list.get(3), "Totalizar");*/
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                list[position].updateData()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@EmailActivity, MenuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
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
            val intent = Intent(this@EmailActivity, MenuPrincipal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            Log.d("ATRAS", "Atras")
        }
        return super.onKeyDown(keyCode, event)
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver!!.isNetworkAvailable(this@EmailActivity)
}

