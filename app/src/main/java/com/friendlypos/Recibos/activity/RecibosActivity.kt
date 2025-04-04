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
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
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
    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
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
        set(canceladoPorFactura) {
            field = field + canceladoPorFactura
        }
    @JvmField
    var totalizarPagado: Double = 0.0
    @JvmField
    var montoPagar: Double = 0.0
    private var totalizarTotalCheck = 0.0
    var totalizarFinal: Double = 0.0
        set(totalizarFinal) {
            field = field + totalizarFinal
        }
    var montoAgregadoRestante: Double = 0.0
        set(montoAgregadoRestante) {
            field = field - montoAgregadoRestante
        }
    private var totalizarFinalCliente = 0.0
    @JvmField
    var receipts_id_num: String? = null
    var nextId: Int = 0
    private var preSellRecibosDelegate: PreSellRecibosDelegate? = null


    fun getTotalizarCancelado(): Double {
        return totalizarCancelado
    }

    fun setTotalizarCancelado(totalizarCancelado: Double) {
        this.totalizarCancelado = this.totalizarCancelado + totalizarCancelado
    }

    fun getTotalizarTotal(): Double {
        return totalizarTotal
    }

    fun setTotalizarTotal(totalizarTotal: Double) {
        this.totalizarTotal = this.totalizarTotal + totalizarTotal
    }


    fun getTotalizarTotalCheck(): Double {
        return totalizarTotalCheck
    }

    fun setTotalizarTotalCheck(totalizarTotalCheck: Double) {
        this.totalizarTotalCheck = this.totalizarTotalCheck + totalizarTotalCheck
    }


    fun getTotalizarFinalCliente(): Double {
        return totalizarFinalCliente
    }

    fun setTotalizarFinalCliente(totalizarFinalCliente: Double) {
        this.totalizarFinalCliente = this.totalizarFinalCliente + totalizarFinalCliente
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_recibos)



        toolbar = findViewById<View>(R.id.toolbarRecibos) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        connectToPrinter()
        val viewPager = findViewById<View>(R.id.viewpagerRecibos) as ViewPager
        if (viewPager != null) {
            setupViewPager(viewPager)
        }
        preSellRecibosDelegate = PreSellRecibosDelegate(this)
        tabLayout = findViewById<View>(R.id.tabsRecibos) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente: Int = selecClienteTabRecibos

                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(this@RecibosActivity, "Recibos", "Seleccione un cliente.")

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
                val tabCliente = selecClienteTabRecibos
                if (tabCliente == 1) {
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

                    val dialogReturnSale = AlertDialog.Builder(this@RecibosActivity)

                        .setTitle(spannableStringTitulo)
                        .setMessage(spannableString)
                        .setPositiveButton(
                            "OK"
                        ) { dialog, which ->
                            val realm2 = Realm.getDefaultInstance()
                            realm2.executeTransaction { realm -> // increment index
                                /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();
                                        
                                                                            if(numeracion.getId()){}
                                        */

                                // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                                val numero = realm.where(Numeracion::class.java)
                                    .equalTo("sale_type", "4").max("number")
                                nextId = if (numero == null) {
                                    1
                                } else {
                                    numero.toInt() - 1
                                }
                            }
                            val realm5 = Realm.getDefaultInstance()
                            realm5.executeTransaction { realm5 ->
                                val numNuevo = Numeracion() // unmanaged
                                numNuevo.sale_type = "4"
                                numNuevo.numeracion_numero = nextId

                                realm5.insertOrUpdate(numNuevo)
                                Log.d("RecNumNuevaAtras", numNuevo.toString() + "")
                            }

                            val id = nextId + 1
                            val idRecipiente = id.toString()
                            val realm6 = Realm.getDefaultInstance()
                            realm6.executeTransaction {
                                val result = realm6.where(
                                    receipts::class.java
                                ).equalTo("receipts_id", idRecipiente).findAll()
                                result.deleteAllFromRealm()
                                Log.d("ReciboBorrado", result.toString() + "")
                            }
                            realm5.close()
                            realm6.close()
                            val intent = Intent(
                                this@RecibosActivity,
                                MenuPrincipal::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }.setNegativeButton(
                            "Cancel"
                        ) { dialog, which -> dialog.cancel() }.create()
                    dialogReturnSale.show()
                } else {
                    val intent = Intent(this@RecibosActivity, MenuPrincipal::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }


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
        get() = preSellRecibosDelegate!!.currentRecibos


    val receiptsByReceiptsDetalle: receipts
        get() = preSellRecibosDelegate!!.receiptsByReceiptsDetalle

    val allRecibosDelegate: List<recibos>
        get() = preSellRecibosDelegate!!.allRecibos

    fun initCurrentRecibos(
        receipts_id: String?, customer_id: String?, reference: String?,
        date: String?, sum: String?, balance: Double, notes: String?
    ) {
        preSellRecibosDelegate!!.initReciboDetalle(
            receipts_id, customer_id, reference,
            date, sum, balance, notes
        )
    }

    fun insertRecibo(recibo: recibos?) {
        preSellRecibosDelegate!!.insertRecibo(recibo)
    }

    fun initRecibo(pos: Int) {
        preSellRecibosDelegate!!.initRecibo(pos)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            val tabCliente = selecClienteTabRecibos
            if (tabCliente == 1) {
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
                spannableStringTitulo.setSpan(typefaceSpan, 0, titulo.length, Spanned.SPAN_PRIORITY)

                val dialogReturnSale = AlertDialog.Builder(this@RecibosActivity)

                    .setTitle(spannableStringTitulo)
                    .setMessage(spannableString)
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        val realm2 = Realm.getDefaultInstance()
                        realm2.executeTransaction { realm -> // increment index
                            /*  Numeracion numeracion = realm.where(Numeracion.class).equalTo("id", "3").findFirst();
                                
                                                                    if(numeracion.getId()){}
                                */

                            // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04

                            val numero = realm.where(Numeracion::class.java)
                                .equalTo("sale_type", "4").max("number")
                            nextId = if (numero == null) {
                                1
                            } else {
                                numero.toInt() - 1
                            }
                        }
                        val realm5 = Realm.getDefaultInstance()
                        realm5.executeTransaction { realm5 ->
                            val numNuevo = Numeracion() // unmanaged
                            numNuevo.sale_type = "4"
                            numNuevo.numeracion_numero = nextId

                            realm5.insertOrUpdate(numNuevo)
                            Log.d("RecNumNuevaAtras", numNuevo.toString() + "")
                        }

                        val id = nextId + 1
                        val idRecipiente = id.toString()
                        val realm6 = Realm.getDefaultInstance()
                        realm6.executeTransaction {
                            val result = realm6.where(receipts::class.java)
                                .equalTo("receipts_id", idRecipiente).findAll()
                            result.deleteAllFromRealm()
                            Log.d("ReciboBorrado", result.toString() + "")
                        }
                        realm5.close()
                        realm6.close()
                        val intent = Intent(
                            this@RecibosActivity,
                            MenuPrincipal::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }.setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.cancel() }.create()
                dialogReturnSale.show()
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
