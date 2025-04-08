package com.friendlysystemgroup.friendlypos.reimpresion_pedidos.activity

import android.app.ActivityManager
import android.app.ProgressDialog
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
import com.friendlysystemgroup.friendlypos.databinding.ActivityReimprimirPedidoBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.util.Adapter
import com.friendlysystemgroup.friendlypos.preventas.delegate.PreSellInvoiceDelegate
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.fragment.ReimPedidoResumenFragment
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.fragment.ReimPedidoSelecClienteFragment
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.fragment.ReimPedidoSelecProductoFragment
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.fragment.ReimPedidoTotalizarFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener

class ReimprimirPedidosActivity : BluetoothActivity() {
    private lateinit var binding: ActivityReimprimirPedidoBinding
    private var preSellInvoiceDelegate: PreSellInvoiceDelegate? = null
    
    @JvmField
    var invoiceId: String? = null
    @JvmField
    var metodoPagoCliente: String? = null
    @JvmField
    var creditoLimiteCliente: String? = null
    @JvmField
    var dueCliente: String? = null

    private var totalizarSubGrabado = 0.0
    private var totalizarSubExento = 0.0
    private var totalizarSubTotal = 0.0
    private var totalizarDescuento = 0.0
    private var totalizarImpuestoIVA = 0.0
    private var totalizarTotal = 0.0
    private var totalizarTotalDouble = 0.0

    @JvmField
    var selecClienteTab: Int = 0

    private val selecColorCliente = 0

    var progressDialog: ProgressDialog? = null

    fun cleanTotalize() {
        totalizarSubGrabado = 0.0
        totalizarSubExento = 0.0
        totalizarSubTotal = 0.0
        totalizarDescuento = 0.0
        totalizarImpuestoIVA = 0.0
        totalizarTotal = 0.0
        totalizarTotalDouble = 0.0
    }


    fun getTotalizarSubGrabado(): Double {
        return totalizarSubGrabado
    }

    fun setTotalizarSubGrabado(totalizarSubGrabado: Double) {
        this.totalizarSubGrabado = this.totalizarSubGrabado + totalizarSubGrabado
    }

    fun getTotalizarSubExento(): Double {
        return totalizarSubExento
    }

    fun setTotalizarSubExento(totalizarSubExento: Double) {
        this.totalizarSubExento = this.totalizarSubExento + totalizarSubExento
    }

    fun getTotalizarSubTotal(): Double {
        return totalizarSubTotal
    }

    fun setTotalizarSubTotal(totalizarSubTotal: Double) {
        this.totalizarSubTotal = this.totalizarSubTotal + totalizarSubTotal
    }

    fun getTotalizarDescuento(): Double {
        return totalizarDescuento
    }

    fun setTotalizarDescuento(totalizarDescuento: Double) {
        this.totalizarDescuento = this.totalizarDescuento + totalizarDescuento
    }

    fun getTotalizarImpuestoIVA(): Double {
        return totalizarImpuestoIVA
    }

    fun setTotalizarImpuestoIVA(totalizarImpuestoIVA: Double) {
        this.totalizarImpuestoIVA = this.totalizarImpuestoIVA + totalizarImpuestoIVA
    }

    fun getTotalizarTotal(): Double {
        return totalizarTotal
    }

    fun setTotalizarTotal(totalizarTotal: Double) {
        this.totalizarTotal = this.totalizarTotal + totalizarTotal
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        binding = ActivityReimprimirPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbarReimprimirPedido)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        preSellInvoiceDelegate = PreSellInvoiceDelegate(this)
        connectToPrinter()

        setupViewPager(binding.viewpagerReimprimirPedido)

        binding.tabsReimprimirPedido.setupWithViewPager(binding.viewpagerReimprimirPedido)

        binding.viewpagerReimprimirPedido.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(binding.tabsReimprimirPedido)
        )
        
        binding.tabsReimprimirPedido.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente: Int = this@ReimprimirPedidosActivity.selecClienteTab
                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(
                        this@ReimprimirPedidosActivity,
                        "Reimprimir Pedido",
                        "Seleccione una factura."
                    )

                    Handler().postDelayed(
                        { binding.tabsReimprimirPedido.getTabAt(0)?.select() }, 100
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


    private fun connectToPrinter() {
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

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(supportFragmentManager)
        val list: MutableList<BaseFragment> = ArrayList()
        
        list.add(ReimPedidoSelecClienteFragment())
        list.add(ReimPedidoResumenFragment())
        list.add(ReimPedidoSelecProductoFragment())
        list.add(ReimPedidoTotalizarFragment())
        
        adapter.addFragment(list[0], "Seleccionar Cliente")
        adapter.addFragment(list[1], "Resumen")
        adapter.addFragment(list[2], "Seleccionar productos")
        adapter.addFragment(list[3], "Totalizar")
        
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(
                    this@ReimprimirPedidosActivity,
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
            val intent = Intent(this@ReimprimirPedidosActivity, MenuPrincipal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            Log.d("ATRAS", "Atras")
        }
        return super.onKeyDown(keyCode, event)
    }

    fun initProducto(pos: Int) {
        preSellInvoiceDelegate?.initProduct(pos)
    }
}

