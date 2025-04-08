package com.friendlysystemgroup.friendlypos.Recibos.activity

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.Recibos.delegate.PreSellRecibosDelegate
import com.friendlysystemgroup.friendlypos.Recibos.fragments.RecibosAplicarFragment
import com.friendlysystemgroup.friendlypos.Recibos.fragments.RecibosClientesFragment
import com.friendlysystemgroup.friendlypos.Recibos.fragments.RecibosResumenFragment
import com.friendlysystemgroup.friendlypos.Recibos.fragments.RecibosSeleccionarFacturaFragment
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receiptsDetalle
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.databinding.ActivityRecibosBinding
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.util.Adapter
import com.friendlysystemgroup.friendlypos.preventas.modelo.Numeracion
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class RecibosActivity : BluetoothActivity() {

    // ViewBinding 
    private lateinit var binding: ActivityRecibosBinding

    @JvmField
    var selecClienteTabRecibos: Int = 0

    @JvmField
    var selecFacturaTabRecibos: Int = 0

    @JvmField
    var invoiceIdRecibos: String? = null

    @JvmField
    var clienteIdRecibos: String? = null

    @JvmField
    var totalFacturaSelec: Double = 0.0

    private var totalizarTotal = 0.0
    private var totalizarCancelado = 0.0

    var canceladoPorFactura: Double = 0.0
        set(value) {
            field = field + value
        }

    @JvmField
    var totalizarPagado: Double = 0.0

    @JvmField
    var montoPagar: Double = 0.0

    private var totalizarTotalCheck = 0.0

    var totalizarFinal: Double = 0.0
        set(value) {
            field = field + value
        }

    var montoAgregadoRestante: Double = 0.0
        set(value) {
            field = field - value
        }

    private var totalizarFinalCliente = 0.0

    @JvmField
    var receipts_id_num: String? = null

    var nextId: Int = 0

    private var preSellRecibosDelegate: PreSellRecibosDelegate? = null

    fun getTotalizarCancelado(): Double = totalizarCancelado

    fun setTotalizarCancelado(value: Double) {
        totalizarCancelado += value
    }

    fun getTotalizarTotal(): Double = totalizarTotal

    fun setTotalizarTotal(value: Double) {
        totalizarTotal += value
    }

    fun getTotalizarTotalCheck(): Double = totalizarTotalCheck

    fun setTotalizarTotalCheck(value: Double) {
        totalizarTotalCheck += value
    }

    fun getTotalizarFinalCliente(): Double = totalizarFinalCliente

    fun setTotalizarFinalCliente(value: Double) {
        totalizarFinalCliente += value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mantener la pantalla encendida
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Inicializar ViewBinding
        binding = ActivityRecibosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar la toolbar
        setSupportActionBar(binding.toolbarRecibos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        connectToPrinter()

        // Configurar el ViewPager
        setupViewPager(binding.viewpagerRecibos)
        
        preSellRecibosDelegate = PreSellRecibosDelegate(this)

        // Vincular TabLayout con ViewPager
        binding.tabsRecibos.setupWithViewPager(binding.viewpagerRecibos)

        binding.viewpagerRecibos.addOnPageChangeListener(
            TabLayoutOnPageChangeListener(binding.tabsRecibos)
        )
        
        binding.tabsRecibos.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente = selecClienteTabRecibos

                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(this@RecibosActivity, "Recibos", "Seleccione un cliente.")

                    Handler().postDelayed(
                        { binding.tabsRecibos.getTabAt(0)?.select() }, 100
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
        if (printer_enabled) {
            if (printer.isNullOrEmpty()) {
                // Aquí podrías mostrar un AlertDialog si la impresora no está configurada.
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
        
        list.add(RecibosClientesFragment())
        list.add(RecibosSeleccionarFacturaFragment())
        list.add(RecibosResumenFragment())
        list.add(RecibosAplicarFragment())
        
        adapter.addFragment(list[0], "Seleccionar Cliente")
        adapter.addFragment(list[1], "Selecionar Factura")
        adapter.addFragment(list[2], "Resumen")
        adapter.addFragment(list[3], "Aplicar")
        
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
        return when (item.itemId) {
            android.R.id.home -> {
                if (selecClienteTabRecibos == 1) {
                    val message = "¿Desea cancelar la factura en proceso?"
                    val titulo = "Salir"
                    val spannableString = SpannableString(message)
                    val spannableStringTitulo = SpannableString(titulo)

                    val typefaceSpan = CalligraphyTypefaceSpan(
                        TypefaceUtils.load(
                            applicationContext.assets, "font/monse.otf"
                        )
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

                    AlertDialog.Builder(this@RecibosActivity)
                        .setTitle(spannableStringTitulo)
                        .setMessage(spannableString)
                        .setPositiveButton("OK") { _, _ ->
                            val realm = Realm.getDefaultInstance()
                            realm.executeTransaction { r ->
                                val numero = r.where(Numeracion::class.java)
                                    .equalTo("sale_type", "4")
                                    .max("number")
                                nextId = numero?.toInt()?.minus(1) ?: 1
                            }
                            
                            val realm2 = Realm.getDefaultInstance()
                            realm2.executeTransaction { r ->
                                val numNuevo = Numeracion() 
                                numNuevo.sale_type = "4"
                                numNuevo.numeracion_numero = nextId

                                r.insertOrUpdate(numNuevo)
                                Log.d("RecNumNuevaAtras", numNuevo.toString())
                            }

                            val id = nextId + 1
                            val idRecipiente = id.toString()
                            val realm3 = Realm.getDefaultInstance()
                            realm3.executeTransaction { r ->
                                val result = r.where(receipts::class.java)
                                    .equalTo("receipts_id", idRecipiente)
                                    .findAll()
                                result.deleteAllFromRealm()
                                Log.d("ReciboBorrado", result.toString())
                            }
                            realm2.close()
                            realm3.close()
                            
                            val intent = Intent(
                                this@RecibosActivity,
                                MenuPrincipal::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                        .create()
                        .show()
                } else {
                    val intent = Intent(this@RecibosActivity, MenuPrincipal::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Verifica si el servicio de impresión está en ejecución
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

    fun cleanTotalize() {
        totalizarTotal = 0.0
        totalizarCancelado = 0.0
    }

    fun cleanTotalizeCkeck() {
        totalizarTotalCheck = 0.0
    }

    fun cleanTotalizeFinal() {
        totalizarFinalCliente = 0.0
    }

    val currentRecibos: receiptsDetalle
        get() = preSellRecibosDelegate?.currentRecibos ?: throw IllegalStateException("PreSellRecibosDelegate no inicializado")

    val receiptsByReceiptsDetalle: receipts
        get() = preSellRecibosDelegate?.receiptsByReceiptsDetalle ?: throw IllegalStateException("PreSellRecibosDelegate no inicializado")

    val allRecibosDelegate: List<recibos>
        get() = preSellRecibosDelegate?.allRecibos ?: emptyList()

    fun initCurrentRecibos(
        receipts_id: String?,
        customer_id: String?,
        reference: String?,
        date: String?,
        sum: String?,
        balance: Double,
        notes: String?
    ) {
        preSellRecibosDelegate?.initReciboDetalle(
            receipts_id, customer_id, reference,
            date, sum, balance, notes
        )
    }

    fun insertRecibo(recibo: recibos?) {
        preSellRecibosDelegate?.insertRecibo(recibo)
    }

    fun initRecibo(pos: Int) {
        preSellRecibosDelegate?.initRecibo(pos)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (selecClienteTabRecibos == 1) {
                val message = "¿Desea cancelar la factura en proceso?"
                val titulo = "Salir"
                val spannableString = SpannableString(message)
                val spannableStringTitulo = SpannableString(titulo)

                val typefaceSpan = CalligraphyTypefaceSpan(
                    TypefaceUtils.load(
                        applicationContext.assets, "font/monse.otf"
                    )
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

                AlertDialog.Builder(this@RecibosActivity)
                    .setTitle(spannableStringTitulo)
                    .setMessage(spannableString)
                    .setPositiveButton("OK") { _, _ ->
                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction { r ->
                            val numero = r.where(Numeracion::class.java)
                                .equalTo("sale_type", "4")
                                .max("number")
                            nextId = numero?.toInt()?.minus(1) ?: 1
                        }
                        
                        val realm2 = Realm.getDefaultInstance()
                        realm2.executeTransaction { r ->
                            val numNuevo = Numeracion()
                            numNuevo.sale_type = "4"
                            numNuevo.numeracion_numero = nextId

                            r.insertOrUpdate(numNuevo)
                            Log.d("RecNumNuevaAtras", numNuevo.toString())
                        }

                        val id = nextId + 1
                        val idRecipiente = id.toString()
                        val realm3 = Realm.getDefaultInstance()
                        realm3.executeTransaction { r ->
                            val result = r.where(receipts::class.java)
                                .equalTo("receipts_id", idRecipiente)
                                .findAll()
                            result.deleteAllFromRealm()
                            Log.d("ReciboBorrado", result.toString())
                        }
                        realm2.close()
                        realm3.close()
                        
                        val intent = Intent(this@RecibosActivity, MenuPrincipal::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
            } else {
                val intent = Intent(this@RecibosActivity, MenuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
