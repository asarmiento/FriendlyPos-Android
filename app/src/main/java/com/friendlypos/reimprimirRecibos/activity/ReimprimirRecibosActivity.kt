package com.friendlysystemgroup.friendlypos.reimprimirRecibos.activity

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.databinding.ActivityReimprimirRecibosBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.util.Adapter
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.ReimprimirRecibos.fragment.ReimprimirReciboFacturaFragment
import com.friendlysystemgroup.friendlypos.ReimprimirRecibos.fragment.ReimprimirReciboResumenFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener

class ReimprimirRecibosActivity : BluetoothActivity() {
    private lateinit var binding: ActivityReimprimirRecibosBinding
    
    @JvmField
    var selecReciboTab: Int = 0
    @JvmField
    var invoiceIdReimprimirRecibo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        binding = ActivityReimprimirRecibosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarReimprimirRecibo)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewPager(binding.viewpagerReimprimirRecibo)
        connectToPrinter()
        
        binding.tabsReimprimirRecibo.setupWithViewPager(binding.viewpagerReimprimirRecibo)
        binding.tabsReimprimirRecibo.tabMode = TabLayout.MODE_FIXED

        binding.viewpagerReimprimirRecibo.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(binding.tabsReimprimirRecibo)
        )
        
        binding.tabsReimprimirRecibo.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente: Int = this@ReimprimirRecibosActivity.selecReciboTab
                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(
                        this@ReimprimirRecibosActivity,
                        "Recibos",
                        "Seleccione un recibo."
                    )

                    Handler().postDelayed(
                        { binding.tabsReimprimirRecibo.getTabAt(0)?.select() }, 100
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // No action needed
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // No action needed
            }
        })
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(supportFragmentManager)
        val list: MutableList<BaseFragment> = ArrayList()
        
        list.add(ReimprimirReciboFacturaFragment())
        list.add(ReimprimirReciboResumenFragment())

        adapter.addFragment(list[0], "Seleccionar Recibo")
        adapter.addFragment(list[1], "Resumen")

        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // No action needed
            }

            override fun onPageSelected(position: Int) {
                list[position].updateData()
            }

            override fun onPageScrollStateChanged(state: Int) {
                // No action needed
            }
        })
    }

    private fun connectToPrinter() {
        //if(bluetoothStateChangeReceiver.isBluetoothAvailable()) {
        preferences
        if (printer_enabled) {
            if (printer.isNullOrEmpty()) {
                // AlertDialog logic commented out in original code
            } else {
                if (!isServiceRunning(PrinterService.CLASS_NAME)) {
                    startRDService(applicationContext, printer)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(
                    this@ReimprimirRecibosActivity,
                    MenuPrincipal::class.java
                )
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
            val intent = Intent(this@ReimprimirRecibosActivity, MenuPrincipal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            Log.d("ATRAS", "Atras")
        }
        return super.onKeyDown(keyCode, event)
    }
}


