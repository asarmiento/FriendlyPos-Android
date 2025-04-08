package com.friendlysystemgroup.friendlypos.ventadirecta.activity

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.databinding.ActivityVentadirectaBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.distribucion.util.Adapter
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.preventas.modelo.Numeracion
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.ventadirecta.delegate.PreSellInvoiceDelegateVD
import com.friendlysystemgroup.friendlypos.ventadirecta.fragment.VentaDirResumenFragment
import com.friendlysystemgroup.friendlypos.ventadirecta.fragment.VentaDirSelecClienteFragment
import com.friendlysystemgroup.friendlypos.ventadirecta.fragment.VentaDirSelecProductoFragment
import com.friendlysystemgroup.friendlypos.ventadirecta.fragment.VentaDirTotalizarFragment
import com.friendlysystemgroup.friendlypos.ventadirecta.modelo.invoiceDetalleVentaDirecta
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class VentaDirectaActivity : BluetoothActivity() {
    private lateinit var binding: ActivityVentadirectaBinding
    private lateinit var session: SessionPrefes
    private lateinit var fragmentList: List<BaseFragment>
    private var preSellInvoiceDelegate: PreSellInvoiceDelegateVD? = null
    
    // Propiedades de la factura
    var invoiceIdVentaDirecta: Int = 0
        private set
    var metodoPagoClienteVentaDirecta: String? = null
    var creditoLimiteClienteVentaDirecta: String? = null
    var selecClienteTabVentaDirecta: Int = 0
    var dueClienteVentaDirecta: String? = null
    var nextId: Int = 0
    var facturaid1: List<Pivot>? = null
    var idFacturaSeleccionada: String? = null
    var idInvetarioSelec: Int = 0
    var amount_inventario: Double = 0.0

    // Propiedades para totalizar
    private var totalizarSubGrabado = 0.0
    private var totalizarSubExento = 0.0
    private var totalizarSubTotal = 0.0
    private var totalizarDescuento = 0.0
    private var totalizarImpuestoIVA = 0.0
    private var totalizarTotal = 0.0
    private var totalizarTotalDouble = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        binding = ActivityVentadirectaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupDependencies()
        setupViewPager()
        setupTabs()
        connectToPrinter()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarVentaDirecta)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupDependencies() {
        session = SessionPrefes(applicationContext)
        preSellInvoiceDelegate = PreSellInvoiceDelegateVD(this)
    }
    
    private fun setupViewPager() {
        val adapter = Adapter(supportFragmentManager)
        
        fragmentList = listOf(
            VentaDirSelecClienteFragment(),
            VentaDirSelecProductoFragment(),
            VentaDirResumenFragment(),
            VentaDirTotalizarFragment()
        )
        
        adapter.addFragment(fragmentList[0], "Seleccionar Cliente")
        adapter.addFragment(fragmentList[1], "Seleccionar Productos")
        adapter.addFragment(fragmentList[2], "Resumen")
        adapter.addFragment(fragmentList[3], "Totalizar")
        
        binding.viewpagerVentaDirecta.apply {
            this.adapter = adapter
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                
                override fun onPageSelected(position: Int) {
                    fragmentList[position].updateData()
                }
                
                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
    }
    
    private fun setupTabs() {
        binding.tabsVentaDirecta.apply {
            setupWithViewPager(binding.viewpagerVentaDirecta)
            
            binding.viewpagerVentaDirecta.addOnPageChangeListener(
                TabLayoutOnPageChangeListener(this)
            )
            
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    if (selecClienteTabVentaDirecta == 0 && tab.position != 0) {
                        CreateMessage(
                            this@VentaDirectaActivity,
                            "Venta Directa",
                            "Seleccione una factura."
                        )
                        
                        Handler(Looper.getMainLooper()).postDelayed({
                            getTabAt(0)?.select()
                        }, 100)
                    }
                }
                
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }
    
    private fun connectToPrinter() {
        preferences
        if (printer_enabled) {
            printer?.let { printerAddress ->
                if (printerAddress.isNotEmpty() && !isServiceRunning(PrinterService.CLASS_NAME)) {
                    PrinterService.startRDService(applicationContext, printerAddress)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (selecClienteTabVentaDirecta == 1) {
                    showCancelConfirmation()
                } else {
                    navigateToMainMenu()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showCancelConfirmation() {
        val message = "¿Desea cancelar la factura en proceso?"
        val titulo = "Salir"
        
        val spannableString = SpannableString(message)
        val spannableStringTitulo = SpannableString(titulo)
        
        val typefaceSpan = CalligraphyTypefaceSpan(
            TypefaceUtils.load(applicationContext.assets, "font/monse.otf")
        )
        
        spannableString.setSpan(
            typefaceSpan,
            0,
            message.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        
        spannableStringTitulo.setSpan(
            typefaceSpan,
            0,
            titulo.length,
            Spanned.SPAN_PRIORITY
        )
        
        AlertDialog.Builder(this)
            .setTitle(spannableStringTitulo)
            .setMessage(spannableString)
            .setPositiveButton("OK") { _, _ ->
                decrementarNumeracion()
                devolverTodo()
                navigateToMainMenu()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> 
                dialog.cancel() 
            }
            .create()
            .show()
    }
    
    private fun decrementarNumeracion() {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction { r ->
                val numero = r.where(Numeracion::class.java)
                    .equalTo("sale_type", "1").max("number")
                
                nextId = if (numero == null) 1 else numero.toInt() - 1
                
                val numNuevo = Numeracion().apply { 
                    sale_type = "1"
                    numeracion_numero = nextId
                }
                
                r.insertOrUpdate(numNuevo)
                Log.d("VDNumNuevaAtras", "Numeración actualizada: $numNuevo")
            }
        } finally {
            realm.close()
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
    
    // Getters y setters para totalización
    fun getTotalizarSubGrabado(): Double = totalizarSubGrabado
    fun setTotalizarSubGrabado(value: Double) { totalizarSubGrabado += value }
    
    fun getTotalizarSubExento(): Double = totalizarSubExento
    fun setTotalizarSubExento(value: Double) { totalizarSubExento += value }
    
    fun getTotalizarSubTotal(): Double = totalizarSubTotal
    fun setTotalizarSubTotal(value: Double) { totalizarSubTotal += value }
    
    fun getTotalizarDescuento(): Double = totalizarDescuento
    fun setTotalizarDescuento(value: Double) { totalizarDescuento += value }
    
    fun getTotalizarImpuestoIVA(): Double = totalizarImpuestoIVA
    fun setTotalizarImpuestoIVA(value: Double) { totalizarImpuestoIVA += value }
    
    fun getTotalizarTotal(): Double = totalizarTotal
    fun setTotalizarTotal(value: Double) { totalizarTotal += value }
    
    fun setInvoiceIdPreventa(invoiceIdPreventa: Int) {
        this.invoiceIdVentaDirecta = invoiceIdPreventa
    }

    // Delegados para manejo de facturas
    val currentInvoice: invoiceDetalleVentaDirecta
        get() = preSellInvoiceDelegate?.currentInvoiceVentaDirecta 
            ?: throw IllegalStateException("PreSellInvoiceDelegate no inicializado")

    val currentVenta: sale
        get() = preSellInvoiceDelegate?.currentVentaVentaDirecta 
            ?: throw IllegalStateException("PreSellInvoiceDelegate no inicializado")

    val invoiceByInvoiceDetalles: invoice
        get() = preSellInvoiceDelegate?.invoiceByInvoiceDetalleVentaDirecta 
            ?: throw IllegalStateException("PreSellInvoiceDelegate no inicializado")

    val allPivotDelegate: List<Pivot>
        get() = preSellInvoiceDelegate?.allPivotVentaDirecta 
            ?: emptyList()

    fun insertProduct(pivot: Pivot?) {
        preSellInvoiceDelegate?.insertProductVentaDirecta(pivot)
    }

    fun borrarProduct(pivot: Pivot?) {
        preSellInvoiceDelegate?.borrarProductoVentaDirecta(pivot)
    }

    fun devolverTodo() {
        preSellInvoiceDelegate?.devolverTodoVentaDirecta()
    }

    // Inicialización de factura actual
    fun initCurrentInvoice(
        id: String?,
        type: String?,
        branch_office_id: String?,
        numeration: String?,
        key: String?,
        consecutive_number: String?,
        latitude: Double,
        longitude: Double,
        date: String?,
        times: String?,
        date_presale: String?,
        times_presale: String?,
        due_data: String?,
        invoice_type_id: String?,
        payment_method_id: String?,
        totalSubtotal: String?,
        totalGrabado: String?,
        totalExento: String?,
        totalDescuento: String?,
        percent_discount: String?,
        totalImpuesto: String?,
        totalTotal: String?,
        changing: String?,
        notes: String?,
        canceled: String?,
        paid_up: String?,
        paid: String?,
        created_at: String?,
        idUsuario: String?,
        idUsuarioAplicado: String?,
        creada: Int,
        aplicada: Int
    ) {
        preSellInvoiceDelegate?.initCurrentInvoiceVentaDirecta(
            id, type, branch_office_id, numeration, key, consecutive_number,
            latitude, longitude, date, times, date_presale, times_presale,
            due_data, invoice_type_id, payment_method_id, totalSubtotal,
            totalGrabado, totalExento, totalDescuento, percent_discount,
            totalImpuesto, totalTotal, changing, notes, canceled, paid_up,
            paid, created_at, idUsuario, idUsuarioAplicado, creada, aplicada
        )
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (selecClienteTabVentaDirecta == 1) {
                showCancelConfirmation()
            } else {
                navigateToMainMenu()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        preSellInvoiceDelegate = null
    }
}


