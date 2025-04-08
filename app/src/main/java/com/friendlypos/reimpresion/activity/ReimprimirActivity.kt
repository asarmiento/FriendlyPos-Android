package com.friendlysystemgroup.friendlypos.reimpresion.activity

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.databinding.ActivityReimprimirBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.util.Adapter
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.reimpresion.fragment.ReimprimirFacturaFragment
import com.friendlysystemgroup.friendlypos.reimpresion.fragment.ReimprimirResumenFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener

/**
 * Actividad para la reimpresión de facturas
 */
class ReimprimirActivity : BluetoothActivity() {
    private lateinit var binding: ActivityReimprimirBinding
    
    // Identificadores de la factura y estado de selección
    var invoiceIdReimprimir: String? = null
        private set
    private var selecFacturaTab: Int = 0
    
    // Lista de fragmentos
    private lateinit var fragmentList: List<BaseFragment>
    
    companion object {
        private const val TAG = "ReimprimirActivity"
        private const val TAB_DELAY_MS = 100L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        binding = ActivityReimprimirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupViewPager()
        setupTabs()
        connectToPrinter()
        
        Log.d(TAG, "Actividad creada correctamente")
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarReimprimir)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewPager() {
        val adapter = Adapter(supportFragmentManager)
        fragmentList = listOf(
            ReimprimirFacturaFragment.newInstance(),
            ReimprimirResumenFragment.newInstance()
        )
        
        adapter.addFragment(fragmentList[0], "Seleccionar Factura")
        adapter.addFragment(fragmentList[1], "Resumen")

        binding.viewpagerReimprimir.apply {
            this.adapter = adapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) { /* No se requiere acción */ }

                override fun onPageSelected(position: Int) {
                    fragmentList[position].updateData()
                }

                override fun onPageScrollStateChanged(state: Int) {
                    /* No se requiere acción */
                }
            })
        }
    }
    
    private fun setupTabs() {
        binding.apply {
            tabsReimprimir.setupWithViewPager(viewpagerReimprimir)
            tabsReimprimir.tabMode = TabLayout.MODE_FIXED
            
            viewpagerReimprimir.addOnPageChangeListener(
                TabLayoutOnPageChangeListener(tabsReimprimir)
            )
            
            tabsReimprimir.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (selecFacturaTab == 0 && tab.position != 0) {
                        mostrarMensajeSeleccioneFactura()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    /* No se requiere acción */
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    /* No se requiere acción */
                }
            })
        }
    }
    
    private fun mostrarMensajeSeleccioneFactura() {
        CreateMessage(
            this,
            "Reimpresión",
            "Seleccione una factura."
        )

        Handler(Looper.getMainLooper()).postDelayed({
            binding.tabsReimprimir.getTabAt(0)?.select()
        }, TAB_DELAY_MS)
    }

    private fun connectToPrinter() {
        if (printer_enabled) {
            printer?.takeIf { it.isNotEmpty() }?.let { printerAddress ->
                if (!isServiceRunning(PrinterService.CLASS_NAME)) {
                    PrinterService.startRDService(applicationContext, printerAddress)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateToMainMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToMainMenu() {
        Intent(this, MenuPrincipal::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
        finish()
    }

    private fun isServiceRunning(serviceClassName: String): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Int.MAX_VALUE)
        return services.any { it.service.className == serviceClassName }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navigateToMainMenu()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    
    fun setSelectedInvoice(invoiceId: String) {
        invoiceIdReimprimir = invoiceId
        selecFacturaTab = 1
    }
}
