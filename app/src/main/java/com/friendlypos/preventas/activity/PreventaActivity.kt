package com.friendlysystemgroup.friendlypos.preventas.activity

import android.app.ActivityManager
import android.content.DialogInterface
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
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.distribucion.fragment.BaseFragment
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.distribucion.util.Adapter
import com.friendlysystemgroup.friendlypos.preventas.delegate.PreSellInvoiceDelegate
import com.friendlysystemgroup.friendlypos.preventas.fragment.PrevResumenFragment
import com.friendlysystemgroup.friendlypos.preventas.fragment.PrevSelecClienteFragment
import com.friendlysystemgroup.friendlypos.preventas.fragment.PrevSelecProductoFragment
import com.friendlysystemgroup.friendlypos.preventas.fragment.PrevTotalizarFragment
import com.friendlysystemgroup.friendlypos.preventas.modelo.Numeracion
import com.friendlysystemgroup.friendlypos.preventas.modelo.invoiceDetallePreventa
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import io.realm.Realm
import io.realm.RealmQuery
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class PreventaActivity : BluetoothActivity() {
    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    @JvmField
    var invoiceIdPreventa: Int = 0
    @JvmField
    var metodoPagoClientePreventa: String? = null
    @JvmField
    var creditoLimiteClientePreventa: String? = null
    @JvmField
    var selecClienteTabPreventa: Int = 0
    @JvmField
    var selecClienteFacturacionPreventa: String? = null
    var activoColorPreventa: Int = 0
    @JvmField
    var dueClientePreventa: String? = null
    var nextId: Int = 0
    private var totalizarSubGrabado = 0.0
    private var totalizarSubExento = 0.0
    private var totalizarSubTotal = 0.0
    private var totalizarDescuento = 0.0
    private var totalizarImpuestoIVA = 0.0
    private var totalizarTotal = 0.0
    private var totalizarTotalDouble = 0.0

    private var preSellInvoiceDelegate: PreSellInvoiceDelegate? = null


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
        setContentView(R.layout.activity_preventa)
        
        toolbar = findViewById<View>(R.id.toolbarPreventa) as Toolbar

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar





        actionBar!!.setDisplayHomeAsUpEnabled(true)
        preSellInvoiceDelegate = PreSellInvoiceDelegate(this)
        connectToPrinter()

        /*toolbar = (Toolbar) findViewById(R.id.toolbarDistribucion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        val viewPager = findViewById<View>(R.id.viewpagerPreventa) as ViewPager
        if (viewPager != null) {
            setupViewPager(viewPager)
        }

        tabLayout = findViewById<View>(R.id.tabsPreventa) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)


        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente: Int = this.selecClienteTabPreventa
                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(this@PreventaActivity, "Preventa", "Seleccione una factura.")

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
        list
    }


    private fun connectToPrinter() {
        //if(bluetoothStateChangeReceiver.isBluetoothAvailable()) {
        getPreferences()
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
        list.add(PrevSelecClienteFragment())
        list.add(PrevSelecProductoFragment())
        list.add(PrevResumenFragment())
        list.add(PrevTotalizarFragment())
        adapter.addFragment(list[0], "Seleccionar Cliente")
        adapter.addFragment(list[1], "Seleccionar Productos")
        adapter.addFragment(list[2], "Resumen")
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
                val tabCliente = selecClienteTabPreventa
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

                    val dialogReturnSale = AlertDialog.Builder(this@PreventaActivity)

                        .setTitle(spannableStringTitulo)
                        .setMessage(spannableString)
                        .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                val tipoFacturacion: String =
                                    this.selecClienteFacturacionPreventa

                                if (tipoFacturacion == "Preventa") {
                                    val realm2 = Realm.getDefaultInstance()

                                    realm2.executeTransaction { realm -> // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04
                                        val numero = realm.where(
                                            Numeracion::class.java
                                        ).equalTo("sale_type", "2").max("number")
                                        nextId = if (numero == null) {
                                            1
                                        } else {
                                            numero.toInt() - 1
                                        }
                                    }


                                    val realm5 = Realm.getDefaultInstance()
                                    realm5.executeTransaction { realm5 ->
                                        val numNuevo = Numeracion() // unmanaged
                                        numNuevo.sale_type = "2"
                                        numNuevo.numeracion_numero = nextId

                                        realm5.insertOrUpdate(numNuevo)
                                        Log.d("idinvNUEVOCREADO", numNuevo.toString() + "")
                                    }
                                    realm5.close()
                                } else if (tipoFacturacion == "Proforma") {
                                    val realm2 = Realm.getDefaultInstance()

                                    // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04
                                    realm2.executeTransaction { realm ->
                                        val numero = realm.where(
                                            Numeracion::class.java
                                        ).equalTo("sale_type", "3").max("number")
                                        nextId = if (numero == null) {
                                            1
                                        } else {
                                            numero.toInt() - 1
                                        }
                                    }


                                    val realm5 = Realm.getDefaultInstance()
                                    realm5.executeTransaction { realm5 ->
                                        val numNuevo = Numeracion() // unmanaged
                                        numNuevo.sale_type = "3"
                                        numNuevo.numeracion_numero = nextId

                                        realm5.insertOrUpdate(numNuevo)
                                        Log.d("idinvNUEVOCREADO", numNuevo.toString() + "")
                                    }
                                    realm5.close()
                                }
                                val intent = Intent(
                                    this@PreventaActivity,
                                    MenuPrincipal::class.java
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }).setNegativeButton(
                            "Cancel"
                        ) { dialog, which -> dialog.cancel() }.create()
                    dialogReturnSale.show()
                } else {
                    val intent = Intent(this@PreventaActivity, MenuPrincipal::class.java)
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

    val currentInvoice: invoiceDetallePreventa
        get() = preSellInvoiceDelegate!!.currentInvoice

    val currentVenta: sale
        get() = preSellInvoiceDelegate!!.currentVenta

    val invoiceByInvoiceDetalles: invoice
        get() = preSellInvoiceDelegate!!.invoiceByInvoiceDetalle

    val allPivotDelegate: List<Pivot>
        get() = preSellInvoiceDelegate!!.allPivot

    fun insertProduct(pivot: Pivot?) {
        preSellInvoiceDelegate!!.insertProduct(pivot)
    }

    fun borrarProduct(pivot: Pivot?) {
        preSellInvoiceDelegate!!.borrarProductoPreventa(pivot)
    }

    fun initCurrentInvoice(
        id: String?,
        branch_office_id: String?,
        numeration: String?,
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
        idUsuarioAplicado: String?
    ) {
        preSellInvoiceDelegate!!.initInvoiceDetallePreventa(
            id, branch_office_id, numeration, latitude, longitude,
            date, times, date_presale, times_presale, due_data,
            invoice_type_id, payment_method_id, totalSubtotal, totalGrabado,
            totalExento, totalDescuento, percent_discount, totalImpuesto,
            totalTotal, changing, notes, canceled,
            paid_up, paid, created_at, idUsuario,
            idUsuarioAplicado
        )
    }

    fun initCurrentVenta(
        p_id: String?,
        p_invoice_id: String?,
        p_customer_id: String?,
        p_customer_name: String?,
        p_cash_desk_id: String?,
        p_sale_type: String?,
        p_viewed: String?,
        p_applied: String?,
        p_created_at: String?,
        p_updated_at: String?,
        p_reserved: String?,
        aplicada: Int,
        subida: Int,
        facturaDePreventa: String?
    ) {
        preSellInvoiceDelegate!!.initVentaDetallesPreventa(
            p_id, p_invoice_id, p_customer_id, p_customer_name,
            p_cash_desk_id, p_sale_type, p_viewed, p_applied,
            p_created_at, p_updated_at, p_reserved, aplicada, subida, facturaDePreventa
        )
    }

    fun initProducto(pos: Int) {
        preSellInvoiceDelegate!!.initProduct(pos)
    }


    fun cleanTotalize() {
        totalizarSubGrabado = 0.0
        totalizarSubExento = 0.0
        totalizarSubTotal = 0.0
        totalizarDescuento = 0.0
        totalizarImpuestoIVA = 0.0
        totalizarTotal = 0.0
        totalizarTotalDouble = 0.0
    }

    public override fun onDestroy() {
        super.onDestroy()
        preSellInvoiceDelegate!!.destroy()
        preSellInvoiceDelegate = null
    }


    private val list: List<Numeracion>
        get() {
            val realm = Realm.getDefaultInstance()

            val query: RealmQuery<Numeracion> =
                realm.where(Numeracion::class.java).equalTo("rec_creada", 1)
                    .equalTo("rec_aplicada", 0)
            val result1 = query.findAll()

            if (result1.size == 0) {
                Log.d("nadaCreados", "nada" + "")
                //Toast.makeText(getApplicationContext(),"NadaPrev" ,Toast.LENGTH_LONG).show();
            } else {
                for (i in result1.indices) {
                    val salesList1: List<Numeracion> = realm.where(
                        Numeracion::class.java
                    ).equalTo("rec_creada", 1).equalTo("rec_aplicada", 0).findAll()
                    val numero = salesList1[i].numeracion_numero
                    val tipo = salesList1[i].sale_type

                    nextId = numero - 1

                    if (tipo == "2") {
                        val realm5 = Realm.getDefaultInstance()
                        realm5.executeTransaction { realm5 ->
                            val numNuevo = Numeracion() // unmanaged
                            numNuevo.sale_type = "2"
                            numNuevo.numeracion_numero = nextId

                            realm5.insertOrUpdate(numNuevo)
                            Log.d("VDNumNuevaAtras", numNuevo.toString() + "")
                        }
                        realm5.close()
                    } else if (tipo == "3") {
                        val realm5 = Realm.getDefaultInstance()
                        realm5.executeTransaction { realm5 ->
                            val numNuevo = Numeracion() // unmanaged
                            numNuevo.sale_type = "3"
                            numNuevo.numeracion_numero = nextId
                            realm5.insertOrUpdate(numNuevo)
                            Log.d("VDNumNuevaAtras", numNuevo.toString() + "")
                        }
                        realm5.close()
                    }
                }
            }

            return result1
        }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            val tabCliente = selecClienteTabPreventa
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

                val dialogReturnSale = AlertDialog.Builder(this@PreventaActivity)

                    .setTitle(spannableStringTitulo)
                    .setMessage(spannableString)
                    .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            val tipoFacturacion: String = this.selecClienteFacturacionPreventa

                            if (tipoFacturacion == "Preventa") {
                                val realm2 = Realm.getDefaultInstance()

                                realm2.executeTransaction { realm -> // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04
                                    val numero = realm.where(Numeracion::class.java)
                                        .equalTo("sale_type", "2").max("number")
                                    nextId = if (numero == null) {
                                        1
                                    } else {
                                        numero.toInt() - 1
                                    }
                                }


                                val realm5 = Realm.getDefaultInstance()
                                realm5.executeTransaction { realm5 ->
                                    val numNuevo = Numeracion() // unmanaged
                                    numNuevo.sale_type = "2"
                                    numNuevo.numeracion_numero = nextId

                                    realm5.insertOrUpdate(numNuevo)
                                    Log.d("idinvNUEVOCREADO", numNuevo.toString() + "")
                                }
                                realm5.close()
                            } else if (tipoFacturacion == "Proforma") {
                                val realm2 = Realm.getDefaultInstance()

                                // TODO NUMERACION = VENTA DIRECTA :01, PREVENTA:02, DISTRIBUCION:03 Y PROFORMA: 04
                                realm2.executeTransaction { realm ->
                                    val numero = realm.where(Numeracion::class.java)
                                        .equalTo("sale_type", "3").max("number")
                                    nextId = if (numero == null) {
                                        1
                                    } else {
                                        numero.toInt() - 1
                                    }
                                }


                                val realm5 = Realm.getDefaultInstance()
                                realm5.executeTransaction { realm5 ->
                                    val numNuevo = Numeracion() // unmanaged
                                    numNuevo.sale_type = "3"
                                    numNuevo.numeracion_numero = nextId

                                    realm5.insertOrUpdate(numNuevo)
                                    Log.d("idinvNUEVOCREADO", numNuevo.toString() + "")
                                }
                                realm5.close()
                            }
                            val intent = Intent(
                                this@PreventaActivity,
                                MenuPrincipal::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }).setNegativeButton(
                        "Cancel"
                    ) { dialog, which -> dialog.cancel() }.create()
                dialogReturnSale.show()
            } else {
                val intent = Intent(this@PreventaActivity, MenuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

