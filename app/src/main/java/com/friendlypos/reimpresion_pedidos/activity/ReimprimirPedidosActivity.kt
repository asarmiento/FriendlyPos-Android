package com.friendlysystemgroup.friendlypos.reimpresion_pedidos.activity

import android.app.ActivityManager
import android.app.ProgressDialog
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

import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
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
    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
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
        setContentView(R.layout.activity_reimprimir_pedido)
        

        toolbar = findViewById<View>(R.id.toolbarReimprimirPedido) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        preSellInvoiceDelegate = PreSellInvoiceDelegate(this)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        connectToPrinter()

        /*toolbar = (Toolbar) findViewById(R.id.toolbarDistribucion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        val viewPager = findViewById<View>(R.id.viewpagerReimprimirPedido) as ViewPager
        if (viewPager != null) {
            setupViewPager(viewPager)
        }

        tabLayout = findViewById<View>(R.id.tabsReimprimirPedido) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente: Int = this.selecClienteTab
                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(
                        this@ReimprimirPedidosActivity,
                        "Reimprimir Pedido",
                        "Seleccione una factura."
                    )

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

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(
            supportFragmentManager
        )
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
        preSellInvoiceDelegate!!.initProduct(pos)
    }
}

