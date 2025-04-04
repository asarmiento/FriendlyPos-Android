package com.friendlysystemgroup.friendlypos.ventadirecta.activity

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
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
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
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import io.realm.Realm
import io.realm.RealmQuery
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class VentaDirectaActivity : BluetoothActivity() {
    var session: SessionPrefes? = null
    private var toolbar: Toolbar? = null
    private var tabLayout: TabLayout? = null
    var invoiceIdVentaDirecta: Int = 0
        private set
    @JvmField
    var metodoPagoClienteVentaDirecta: String? = null
    @JvmField
    var creditoLimiteClienteVentaDirecta: String? = null
    @JvmField
    var selecClienteTabVentaDirecta: Int = 0
    @JvmField
    var dueClienteVentaDirecta: String? = null
    var nextId: Int = 0
    private var totalizarSubGrabado = 0.0
    private var totalizarSubExento = 0.0
    private var totalizarSubTotal = 0.0
    private var totalizarDescuento = 0.0
    private var totalizarImpuestoIVA = 0.0
    private var totalizarTotal = 0.0
    private var totalizarTotalDouble = 0.0
    var facturaid1: List<Pivot>? = null
    var idFacturaSeleccionada: String? = null
    var idInvetarioSelec: Int = 0
    var amount_inventario: Double = 0.0

    private var preSellInvoiceDelegate: PreSellInvoiceDelegateVD? = null

    fun setInvoiceIdPreventa(invoiceIdPreventa: Int) {
        this.invoiceIdVentaDirecta = invoiceIdPreventa
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
        setContentView(R.layout.activity_ventadirecta)
        ButterKnife.bind(this)
        toolbar = findViewById<View>(R.id.toolbarVentaDirecta) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        session = SessionPrefes(applicationContext)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        preSellInvoiceDelegate = PreSellInvoiceDelegateVD(this)
        connectToPrinter()

        /*toolbar = (Toolbar) findViewById(R.id.toolbarDistribucion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        val viewPager = findViewById<View>(R.id.viewpagerVentaDirecta) as ViewPager
        if (viewPager != null) {
            setupViewPager(viewPager)
        }

        tabLayout = findViewById<View>(R.id.tabsVentaDirecta) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabCliente: Int = this.selecClienteTabVentaDirecta
                if (tabCliente == 0 && tab.position != 0) {
                    CreateMessage(
                        this@VentaDirectaActivity,
                        "Venta Directa",
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


        list
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
        list.add(VentaDirSelecClienteFragment())
        list.add(VentaDirSelecProductoFragment())
        list.add(VentaDirResumenFragment())
        list.add(VentaDirTotalizarFragment())
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
                val tabCliente = selecClienteTabVentaDirecta
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

                    val dialogReturnSale = AlertDialog.Builder(this@VentaDirectaActivity)

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
                                    .equalTo("sale_type", "1").max("number")
                                nextId = if (numero == null) {
                                    1
                                } else {
                                    numero.toInt() - 1
                                }
                            }


                            val realm5 = Realm.getDefaultInstance()
                            realm5.executeTransaction { realm5 ->
                                val numNuevo = Numeracion() // unmanaged
                                numNuevo.sale_type = "1"
                                numNuevo.numeracion_numero = nextId

                                realm5.insertOrUpdate(numNuevo)
                                Log.d("VDNumNuevaAtras", numNuevo.toString() + "")
                            }
                            realm5.close()

                            devolverTodo()


                            val intent = Intent(
                                this@VentaDirectaActivity,
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
                    val intent = Intent(
                        this@VentaDirectaActivity,
                        MenuPrincipal::class.java
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }



                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

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

    val currentInvoice: invoiceDetalleVentaDirecta
        get() = preSellInvoiceDelegate!!.currentInvoiceVentaDirecta

    val currentVenta: sale
        get() = preSellInvoiceDelegate!!.currentVentaVentaDirecta

    val invoiceByInvoiceDetalles: invoice
        get() = preSellInvoiceDelegate!!.invoiceByInvoiceDetalleVentaDirecta

    val allPivotDelegate: List<Pivot>
        get() = preSellInvoiceDelegate!!.allPivotVentaDirecta

    fun insertProduct(pivot: Pivot?) {
        preSellInvoiceDelegate!!.insertProductVentaDirecta(pivot)
    }

    fun borrarProduct(pivot: Pivot?) {
        preSellInvoiceDelegate!!.borrarProductoVentaDirecta(pivot)
    }

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
        preSellInvoiceDelegate!!.initInvoiceDetalleVentaDirecta(
            id, type, branch_office_id, numeration, key, consecutive_number, latitude, longitude,
            date, times, date_presale, times_presale, due_data,
            invoice_type_id, payment_method_id, totalSubtotal, totalGrabado,
            totalExento, totalDescuento, percent_discount, totalImpuesto,
            totalTotal, changing, notes, canceled,
            paid_up, paid, created_at, idUsuario,
            idUsuarioAplicado, creada, aplicada
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
        preSellInvoiceDelegate!!.initVentaDetallesVentaDirecta(
            p_id, p_invoice_id, p_customer_id, p_customer_name,
            p_cash_desk_id, p_sale_type, p_viewed, p_applied,
            p_created_at, p_updated_at, p_reserved, aplicada, subida, facturaDePreventa
        )
    }

    fun initProducto(pos: Int) {
        preSellInvoiceDelegate!!.initProductVentaDirecta(pos)
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


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            val tabCliente = selecClienteTabVentaDirecta
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

                val dialogReturnSale = AlertDialog.Builder(this@VentaDirectaActivity)

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
                                .equalTo("sale_type", "1").max("number")
                            nextId = if (numero == null) {
                                1
                            } else {
                                numero.toInt() - 1
                            }
                        }


                        val realm5 = Realm.getDefaultInstance()
                        realm5.executeTransaction { realm5 ->
                            val numNuevo = Numeracion() // unmanaged
                            numNuevo.sale_type = "1"
                            numNuevo.numeracion_numero = nextId

                            realm5.insertOrUpdate(numNuevo)
                            Log.d("VDNumNuevaAtras", numNuevo.toString() + "")
                        }
                        realm5.close()
                        devolverTodo()
                        val intent = Intent(
                            this@VentaDirectaActivity,
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
                val intent = Intent(this@VentaDirectaActivity, MenuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    private val list: List<Numeracion>
        get() {
            val realm = Realm.getDefaultInstance()
            val query: RealmQuery<Numeracion> =
                realm.where(Numeracion::class.java).equalTo("sale_type", "1")
                    .equalTo("rec_creada", 1).equalTo("rec_aplicada", 0)
            val result1 = query.findAll()


            if (result1.size == 0) {
                Log.d("nadaCreados", "nada" + "")
                // Toast.makeText(getApplicationContext(),"Nada" ,Toast.LENGTH_LONG).show();
            } else {
                for (i in result1.indices) {
                    val salesList1: List<Numeracion> = realm.where(
                        Numeracion::class.java
                    ).equalTo("sale_type", "1").equalTo("rec_creada", 1).equalTo("rec_aplicada", 0)
                        .findAll()
                    val numero = salesList1[i].numeracion_numero

                    nextId = numero - 1

                    val realm5 = Realm.getDefaultInstance()
                    realm5.executeTransaction { realm5 ->
                        val numNuevo = Numeracion() // unmanaged
                        numNuevo.sale_type = "1"
                        numNuevo.numeracion_numero = nextId

                        realm5.insertOrUpdate(numNuevo)
                        Log.d("VDNumNuevaAtras", numNuevo.toString() + "")
                    }
                    realm5.close()
                }
                //  devolverTodo();
            }
            return result1
        }


    fun devolverTodo() {
        facturaid1 =
            allPivotDelegate //realm.where(Pivot.class).equalTo("invoice_id", idFacturaSeleccionada).findAll();
        Log.d("PRODUCTOSFACTURAS", facturaid1.toString() + "")

        for (i in facturaid1!!.indices) {
            val eventRealm = facturaid1!![i]
            val cantidadDevolver = eventRealm.amount!!.toDouble()

            Log.d("PRODUCTOSFACTURASEPA1", eventRealm.toString() + "")
            Log.d("PRODUCTOSFACTURASEPA", cantidadDevolver.toString() + "")

            val resumenProductoId = eventRealm.id
            Log.d("resumenProductoId", resumenProductoId.toString() + "")

            // TRANSACCIÓN BD PARA SELECCIONAR LOS DATOS DEL INVENTARIO
            val realm3 = Realm.getDefaultInstance()
            realm3.executeTransaction { realm3 ->
                val inventario = realm3.where(Inventario::class.java)
                    .equalTo("product_id", eventRealm.product_id).findFirst()
                if (inventario != null) {
                    idInvetarioSelec = inventario.id
                    amount_inventario = inventario.amount!!.toDouble()
                    Log.d("idinventario", idInvetarioSelec.toString() + "")
                } else {
                    amount_inventario = 0.0
                    // increment index
                    val currentIdNum = realm3.where(Inventario::class.java).max("id")

                    nextId = if (currentIdNum == null) {
                        1
                    } else {
                        currentIdNum.toInt() + 1
                    }

                    val invnuevo = Inventario() // unmanaged
                    invnuevo.id = nextId
                    invnuevo.product_id = eventRealm.product_id
                    invnuevo.initial = "0".toString()
                    invnuevo.amount = cantidadDevolver.toString()
                    invnuevo.amount_dist = "0".toString()
                    invnuevo.distributor = "0".toString()

                    realm3.insertOrUpdate(invnuevo)
                    Log.d("idinvNUEVOCREADO", invnuevo.toString() + "")
                }
                realm3.close()
            }


            // OBTENER NUEVO AMOUNT_DIST
            val nuevoAmountDevuelto = cantidadDevolver + amount_inventario
            Log.d("nuevoAmount", nuevoAmountDevuelto.toString() + "")

            // TRANSACCIÓN PARA ACTUALIZAR EL CAMPO AMOUNT_DIST EN EL INVENTARIO
            val realm2 = Realm.getDefaultInstance()
            realm2.executeTransaction { realm2 ->
                val inv_actualizado =
                    realm2.where(Inventario::class.java).equalTo("id", idInvetarioSelec)
                        .findFirst()
                inv_actualizado!!.amount = nuevoAmountDevuelto.toString()
                realm2.insertOrUpdate(inv_actualizado)
                realm2.close()
            }
        }
        session!!.guardarDatosBloquearBotonesDevolver(0)
    }
}


