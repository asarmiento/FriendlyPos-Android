package com.friendlysystemgroup.friendlypos.principal.activity

import android.Manifest
import android.app.ActivityManager
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout


import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.R.id
import com.friendlysystemgroup.friendlypos.Recibos.activity.RecibosActivity
import com.friendlysystemgroup.friendlypos.Recibos.modelo.EnviarRecibos
import com.friendlysystemgroup.friendlypos.Recibos.modelo.receipts
import com.friendlysystemgroup.friendlypos.Recibos.modelo.recibos
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService
import com.friendlysystemgroup.friendlypos.application.bluetooth.PrinterService.Companion.startRDService
import com.friendlysystemgroup.friendlypos.application.util.Functions.CreateMessage
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions.imprimirDevoluciónMenu
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions.imprimirLiquidacionMenu
import com.friendlysystemgroup.friendlypos.application.util.PrinterFunctions.imprimirOrdenCarga
import com.friendlysystemgroup.friendlypos.crearCliente.modelo.customer_new
import com.friendlysystemgroup.friendlypos.distribucion.activity.DistribucionActivity
import com.friendlysystemgroup.friendlypos.distribucion.modelo.EnviarFactura
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Inventario
import com.friendlysystemgroup.friendlypos.distribucion.modelo.Pivot
import com.friendlysystemgroup.friendlypos.distribucion.modelo.invoice
import com.friendlysystemgroup.friendlypos.distribucion.modelo.sale
import com.friendlysystemgroup.friendlypos.login.activity.LoginActivity
import com.friendlysystemgroup.friendlypos.login.modelo.Usuarios
import com.friendlysystemgroup.friendlypos.login.util.Properties
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.preventas.activity.PreventaActivity
import com.friendlysystemgroup.friendlypos.preventas.modelo.EnviarClienteVisitado
import com.friendlysystemgroup.friendlypos.preventas.modelo.visit
import com.friendlysystemgroup.friendlypos.principal.activity.GraficoActivity
import com.friendlysystemgroup.friendlypos.principal.fragment.ConfiguracionFragment
import com.friendlysystemgroup.friendlypos.principal.helpers.DescargasHelper
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelper
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperClienteGPS
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperClienteNuevo
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperClienteVisitado
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperPreventa
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperProductoDevuelto
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperProforma
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperRecibos
import com.friendlysystemgroup.friendlypos.principal.helpers.SubirHelperVentaDirecta
import com.friendlysystemgroup.friendlypos.principal.modelo.ConsecutivosNumberFe
import com.friendlysystemgroup.friendlypos.principal.modelo.EnviarClienteGPS
import com.friendlysystemgroup.friendlypos.principal.modelo.EnviarClienteNuevo
import com.friendlysystemgroup.friendlypos.principal.modelo.EnviarProductoDevuelto
import com.friendlysystemgroup.friendlypos.principal.modelo.customer_location
import com.friendlysystemgroup.friendlypos.Reenvio_email.activity.EmailActivity
import com.friendlysystemgroup.friendlypos.Reimpresion.activity.ReimprimirActivity
import com.friendlysystemgroup.friendlypos.Reimpresion_pedidos.activity.ReimprimirPedidosActivity
import com.friendlysystemgroup.friendlypos.ReimprimirRecibos.activity.ReimprimirRecibosActivity
import com.friendlysystemgroup.friendlypos.ventadirecta.activity.VentaDirectaActivity
import com.github.clans.fab.FloatingActionButton
import io.realm.Realm
import io.realm.RealmQuery
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan
import uk.co.chrisjenx.calligraphy.TypefaceUtils

class MenuPrincipal : BluetoothActivity(),
    PopupMenu.OnMenuItemClickListener /* implements NavigationView.OnNavigationItemSelectedListener*/ {
    override var networkStateChangeReceiver: NetworkStateChangeReceiver? = null
    var bloquear: Int = 0
    private var but1: FloatingActionButton? = null
    private var but2: FloatingActionButton? = null
    private var but3: FloatingActionButton? = null
    private var but4: FloatingActionButton? = null
    private var but5: FloatingActionButton? = null

    @BindView(id.clickClientes)
    lateinit var clickClientes: LinearLayout

    @BindView(id.clickProductos)
    lateinit var clickProductos: LinearLayout

    @BindView(id.clickDistribucion)
    lateinit var clickDistribucion: LinearLayout

    @BindView(id.clickVentaDirecta)
    lateinit var clickVentaDirecta: LinearLayout

    @BindView(id.clickPreventa)
    lateinit var clickPreventa: LinearLayout

    @BindView(id.clickRecibos)
    lateinit var clickRecibos: LinearLayout

    @BindView(id.clickReimprimirVentas)
    lateinit var clickReimprimirVentas: LinearLayout

    @BindView(id.clickReimprimirPedidos)
    lateinit var clickReimprimirPedidos: LinearLayout

    @BindView(id.clickConfig)
    lateinit var clickConfig: LinearLayout

    var drawer: DrawerLayout? = null

    @BindView(id.txtNombreUsuario)
    lateinit var txtNombreUsuario: TextView

    var session: SessionPrefes? = null
    var download1: DescargasHelper? = null
    var subir1: SubirHelper? = null
    var subirPreventa: SubirHelperPreventa? = null
    var subirVentaDirecta: SubirHelperVentaDirecta? = null
    var subirProforma: SubirHelperProforma? = null
    var subirClienteVisitado: SubirHelperClienteVisitado? = null
    var subirProductoDevuelto: SubirHelperProductoDevuelto? = null
    var subirClienteGPS: SubirHelperClienteGPS? = null
    var subirClienteNuevo: SubirHelperClienteNuevo? = null
    var subirHelperRecibos: SubirHelperRecibos? = null
    var usuer: String? = null
    var idUsuario: String? = null
    var facturaId: String? = null
    var facturaIdDevolver: Int = 0
    var facturaIdA: String? = null


    var facturaCostumer: String? = null
    var apiConsecutivo: String? = null
    var facturaIdCV: Int = 0
    var facturaIdNuevoCliente: Int = 0
    var facturaIdDevuelto: Int = 0
    var facturaIdCC: Int = 0

    var facturaIdRecibos: String? = null
    var facturaIdGPS: String? = null
    private var properties: Properties? = null
    private val descargaDatosEmpresa = 0
    private var cambioDatosEmpresa = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_menu_principal)
        
        val toolbar = findViewById<Toolbar>(id.toolbar)
        setSupportActionBar(toolbar)


        networkStateChangeReceiver = NetworkStateChangeReceiver()
        ActivityCompat.requestPermissions(
            this@MenuPrincipal,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            123
        )
        session = SessionPrefes(applicationContext)
        properties = Properties(applicationContext)
        drawer = findViewById<View>(id.drawer_layout) as DrawerLayout
        download1 = DescargasHelper(this@MenuPrincipal)
        subir1 = SubirHelper(this@MenuPrincipal)
        subirPreventa = SubirHelperPreventa(this@MenuPrincipal)
        subirVentaDirecta = SubirHelperVentaDirecta(this@MenuPrincipal)
        subirProforma = SubirHelperProforma(this@MenuPrincipal)
        subirClienteVisitado = SubirHelperClienteVisitado(this@MenuPrincipal)
        subirClienteGPS = SubirHelperClienteGPS(this@MenuPrincipal)
        subirClienteNuevo = SubirHelperClienteNuevo(this@MenuPrincipal)
        subirHelperRecibos = SubirHelperRecibos(this@MenuPrincipal)
        subirProductoDevuelto = SubirHelperProductoDevuelto(this@MenuPrincipal)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()
        connectToPrinter()

        // Redirección al Login
        if (!session!!.isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            usuer = session!!.usuarioPrefs
            Log.d("userasd", usuer!!)

            consecutivoApi1()
        }

        but1 = findViewById<View>(id.nav_distribucion) as FloatingActionButton

        but1!!.setOnClickListener {
            consecutivoApi(idUsuario)
            if (apiConsecutivo == null) {
                Toast.makeText(
                    applicationContext,
                    "Favor descargar info de empresa",
                    Toast.LENGTH_LONG
                ).show()
            } else if (apiConsecutivo == "0") {
                if (!properties!!.blockedApp) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Botón no disponible",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (session!!.datosBloquearBotonesDevolver == 1) {
                if (!properties!!.blockedApp) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Botón no disponible, descargar el inventario ya que fue devuelto",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                if (!properties!!.blockedApp) {
                    val intent = Intent(application, DistribucionActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        but2 = findViewById<View>(id.nav_ventadirecta) as FloatingActionButton

        but2!!.setOnClickListener {
            consecutivoApi(idUsuario)
            Log.d(
                "sessionInicio",
                session!!.datosBloquearBotonesDevolver.toString() + ""
            )
            if (apiConsecutivo == null) {
                Toast.makeText(
                    applicationContext,
                    "Favor descargar info de empresa",
                    Toast.LENGTH_LONG
                ).show()
            } else if (apiConsecutivo == "0") {
                if (!properties!!.blockedApp) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Botón no disponible",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else if (session!!.datosBloquearBotonesDevolver == 1) {
                Log.d(
                    "session",
                    session!!.datosBloquearBotonesDevolver.toString() + ""
                )
                if (!properties!!.blockedApp) {
                    Log.d("properties", properties!!.blockedApp.toString() + "")
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Botón no disponible, descargar el inventario ya que fue devuelto",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                if (!properties!!.blockedApp) {
                    val intent = Intent(application, VentaDirectaActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        but3 = findViewById<View>(id.nav_preventa) as FloatingActionButton

        but3!!.setOnClickListener { /*    if(session.getDatosBloquearBotonesDevolver() == 1){
                        if (!properties.getBlockedApp()) {
                            Toast.makeText(MenuPrincipal.this, "Botón no disponible, descargar el inventario ya que fue devuelto", Toast.LENGTH_LONG).show();
                        }
                    }
    
                    else */
            if (!properties!!.blockedApp) {
                val intent = Intent(application, PreventaActivity::class.java)
                startActivity(intent)
            }
        }

        but4 = findViewById<View>(id.nav_recibos) as FloatingActionButton

        but4!!.setOnClickListener {
            if (!properties!!.blockedApp) {
                val intent = Intent(application, RecibosActivity::class.java)
                startActivity(intent)
                // Toast.makeText(MenuPrincipal.this, "Botón no disponible", Toast.LENGTH_LONG).show();
            }
        }

        but5 = findViewById<View>(id.nav_email) as FloatingActionButton

        but5!!.setOnClickListener {
            if (!properties!!.blockedApp) {
                val intent = Intent(application, EmailActivity::class.java)
                startActivity(intent)
                // Toast.makeText(MenuPrincipal.this, "Botón no disponible", Toast.LENGTH_LONG).show();
            }
        }
    }

    fun consecutivoApi1() {
        val realm = Realm.getDefaultInstance()
        val usuarios = realm.where(Usuarios::class.java).equalTo("email", usuer).findFirst()
        if (usuarios == null) {
            txtNombreUsuario.text = usuer
        } else {
            val nombreUsuario = usuarios.username
            idUsuario = usuarios.id
            Log.d("userasd", nombreUsuario!!)
            txtNombreUsuario.text = nombreUsuario
        }
        realm.close()
    }

    fun consecutivoApi(idUsuario: String?) {
        if (idUsuario == null) {
        } else {
            val realm5 = Realm.getDefaultInstance()
            val numNuevo = realm5.where(
                ConsecutivosNumberFe::class.java
            ).equalTo("user_id", idUsuario).findFirst()

            if (numNuevo == null) {
                Log.d("conse", "conse")
            } else {
                apiConsecutivo = numNuevo.api
                Log.d("apiConsecutivo", apiConsecutivo)
            }
            realm5.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
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

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onMenuItemSelecDescarga(item: MenuItem): Boolean {
        showPopupD(findViewById(item.itemId))
        return true
    }

    fun onMenuItemSelecSubida(item: MenuItem): Boolean {
        showPopupS(findViewById(item.itemId))
        return true
    }

    fun showPopupD(view: View) {
        val popup = PopupMenu(this@MenuPrincipal, view)
        try {
            val fields = popup.javaClass.declaredFields
            for (field in fields) {
                if (field.name == POPUP_CONSTANT) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popup]
                    val classPopupHelper = Class.forName(
                        menuPopupHelper!!.javaClass.name
                    )
                    val setForceIcons = classPopupHelper.getMethod(
                        POPUP_FORCE_SHOW_ICON,
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    fun showPopupS(view: View) {
        val popup = PopupMenu(this@MenuPrincipal, view)
        try {
            val fields = popup.javaClass.declaredFields
            for (field in fields) {
                if (field.name == POPUP_CONSTANT) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popup]
                    val classPopupHelper = Class.forName(
                        menuPopupHelper!!.javaClass.name
                    )
                    val setForceIcons = classPopupHelper.getMethod(
                        POPUP_FORCE_SHOW_ICON,
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popup.menuInflater.inflate(R.menu.popup_menu_s, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            id.menu_cerrarsesion -> {
                val alertDialog = AlertDialog.Builder(
                    this@MenuPrincipal
                ).setPositiveButton(
                    "Si"
                ) { dialog, which ->
                    cambioDatosEmpresa = 0
                    session!!.prefDescargaDatos = cambioDatosEmpresa
                    session!!.cerrarSesion()
                    finish()
                }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.cancel() }.create()


                val message = "¿Seguro que quiere cerrar sesión?"
                val spannableString = SpannableString(message)

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


                alertDialog.setMessage(spannableString)
                alertDialog.show()
            }

            id.btn_descargar_datosempresa -> {
                cambioDatosEmpresa = session!!.prefDescargaDatos

                //cambioDatosEmpresa = getDescargaDatosEmpresa();
                if (cambioDatosEmpresa == 0) {
                    download1!!.descargarDatosEmpresa(this@MenuPrincipal)
                    download1!!.descargarUsuarios(this@MenuPrincipal)
                    cambioDatosEmpresa = 1
                    session!!.prefDescargaDatos = cambioDatosEmpresa
                } else {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Ya los datos están descargados",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            id.btn_descargar_recibos -> {
                cambioDatosEmpresa = session!!.prefDescargaDatos

                if (cambioDatosEmpresa == 1) {
                    download1!!.descargarRecibos(this@MenuPrincipal)
                } else {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Descargar datos de la empresa primero",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            id.btn_descargar_catalogo -> {
                //  cambioDatosEmpresa = getDescargaDatosEmpresa();
                val realm12 = Realm.getDefaultInstance()

                /*  RealmQuery<datosTotales> query12 = realm12.where(datosTotales.class);
                final RealmResults<datosTotales> invoice12 = query12.findAll();
                Log.d("qweqweq", invoice12.toString());*/
                val query12: RealmQuery<invoice> =
                    realm12.where(invoice::class.java).equalTo("subida", 1)
                val invoice12 = query12.findAll()
                Log.d("qweqweq", invoice12.toString())

                if (invoice12.size == 0) {
                    cambioDatosEmpresa = session!!.prefDescargaDatos

                    if (cambioDatosEmpresa == 1) {
                        download1!!.descargarCatalogo(this@MenuPrincipal)
                    } else {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Descargar datos de la empresa primero",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Existen facturas pendientes de subir",
                        Toast.LENGTH_LONG
                    ).show()
                }
                realm12.close()
            }

            id.btn_descargar_inventario -> {
                session!!.guardarDatosBloquearBotonesDevolver(0)
                val realm4 = Realm.getDefaultInstance()

                val query4: RealmQuery<invoice> =
                    realm4.where(invoice::class.java).equalTo("subida", 1)
                val invoice4 = query4.findAll()
                Log.d("qweqweq", invoice4.toString())

                if (invoice4.size == 0) {
                    cambioDatosEmpresa = session!!.prefDescargaDatos

                    // cambioDatosEmpresa = getDescargaDatosEmpresa();
                    if (cambioDatosEmpresa == 1) {
                        download1!!.descargarInventario(this@MenuPrincipal)
                    } else {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Descargar datos de la empresa primero",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Existen facturas pendientes de subir",
                        Toast.LENGTH_LONG
                    ).show()
                }
                realm4.close()
            }

            id.btn_subir_ventas -> {
                //TODO SABER COMO TENER QUE SUBIR LOS DATOS, SI UNO X UNO O TODOS DE UN SOLO.
                val realm = Realm.getDefaultInstance()

                val query: RealmQuery<invoice> =
                    realm.where(invoice::class.java).equalTo("facturaDePreventa", "Distribucion")
                        .equalTo("subida", 1).or().equalTo("devolucionInvoice", 1)
                val invoice1 = query.findAll()
                Log.d("qweqweq", invoice1.toString())
                val listaFacturas = realm.copyFromRealm(invoice1)
                Log.d("qweqweq1", listaFacturas.toString() + "")
                realm.close()

                if (listaFacturas.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay facturas para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var i = 0
                    while (i < listaFacturas.size) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Subiendo información...",
                            Toast.LENGTH_SHORT
                        ).show()

                        facturaId = listaFacturas[i].id.toString()
                        Log.d("facturaId", facturaId + "")
                        val obj = EnviarFactura(listaFacturas[i])
                        Log.d("My App", obj.toString() + "")

                        subir1!!.sendPost(obj, facturaId)
                        i++
                    }
                }
            }

            id.btn_subir_pedidos -> {
                val realmPedidos = Realm.getDefaultInstance()

                val queryPedidos: RealmQuery<invoice> =
                    realmPedidos.where(invoice::class.java).equalTo("subida", 1)
                        .equalTo("facturaDePreventa", "Preventa")
                val invoicePedidos = queryPedidos.findAll()
                Log.d("SubFacturaInvP", invoicePedidos.toString())
                val listaFacturasPedidos = realmPedidos.copyFromRealm(invoicePedidos)
                Log.d("SubFacturaListaP", listaFacturasPedidos.toString() + "")
                realmPedidos.close()

                if (listaFacturasPedidos.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay facturas para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var i = 0
                    while (i < listaFacturasPedidos.size) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Subiendo información...",
                            Toast.LENGTH_SHORT
                        ).show()

                        facturaId = listaFacturasPedidos[i].id.toString()
                        Log.d("facturaId", facturaId + "")
                        val obj = EnviarFactura(listaFacturasPedidos[i])
                        Log.d("My App", obj.toString() + "")

                        subirPreventa!!.sendPostPreventa(obj, facturaId)

                        i++
                    }
                }
            }

            id.btn_subir_ventadirecta -> {
                val realmVentaDirecta = Realm.getDefaultInstance()

                val queryVentaDirecta: RealmQuery<invoice> = realmVentaDirecta.where(
                    invoice::class.java
                ).equalTo("subida", 1).equalTo("facturaDePreventa", "VentaDirecta")
                val invoiceVentaDirecta = queryVentaDirecta.findAll()




                Log.d("SubFacturaInvV", invoiceVentaDirecta.toString())
                val listaFacturasVentaDirecta = realmVentaDirecta.copyFromRealm(invoiceVentaDirecta)
                Log.d("SubFacturaListaV", listaFacturasVentaDirecta.toString() + "")


                if (listaFacturasVentaDirecta.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay facturas para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var i = 0
                    while (i < listaFacturasVentaDirecta.size) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Subiendo información...",
                            Toast.LENGTH_SHORT
                        ).show()

                        facturaId = listaFacturasVentaDirecta[i].id.toString()
                        val size = invoiceVentaDirecta[i]!!.productofactura!!.size
                        Log.d("pivot", size.toString() + "")

                        val query1 = realmVentaDirecta.where(Pivot::class.java)
                            .equalTo("invoice_id", facturaId)
                        val result1 = query1.findAll()
                        Log.d("pivot", result1.toString() + "")

                        Log.d("facturaIdSub", facturaId + "")
                        val obj = EnviarFactura(listaFacturasVentaDirecta[i])
                        Log.d("MyAppSub", obj.toString() + "")
                        subirVentaDirecta!!.sendPostVentaDirecta(obj, facturaId)
                        i++
                    }
                }
            }

            id.btn_subir_proforma -> {
                val realmProforma = Realm.getDefaultInstance()

                val queryProforma: RealmQuery<invoice> =
                    realmProforma.where(invoice::class.java).equalTo("subida", 1)
                        .equalTo("facturaDePreventa", "Proforma")
                val invoiceProforma = queryProforma.findAll()
                Log.d("SubFacturaInvPRO", invoiceProforma.toString())
                val listaFacturasProforma = realmProforma.copyFromRealm(invoiceProforma)
                Log.d("SubFacturaListaPROV", listaFacturasProforma.toString() + "")
                realmProforma.close()

                if (listaFacturasProforma.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay facturas para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var i = 0
                    while (i < listaFacturasProforma.size) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Subiendo información...",
                            Toast.LENGTH_SHORT
                        ).show()

                        facturaId = listaFacturasProforma[i].id.toString()
                        Log.d("facturaId", facturaId + "")
                        val obj = EnviarFactura(listaFacturasProforma[i])
                        Log.d("My App", obj.toString() + "")
                        subirProforma!!.sendPostProforma(obj, facturaId)
                        i++
                    }
                }
            }

            id.btn_devolver_inventario -> {
                val realmDevolver1 = Realm.getDefaultInstance()
                val queryDevolver1: RealmQuery<invoice> =
                    realmDevolver1.where(invoice::class.java).equalTo("subida", 1)
                val invoiceDevolver1 = queryDevolver1.findAll()
                Log.d("qweqweq", invoiceDevolver1.toString())

                if (invoiceDevolver1.size == 0) {
                    val realmDevolver = Realm.getDefaultInstance()

                    val queryDevolver: RealmQuery<Inventario> = realmDevolver.where(
                        Inventario::class.java
                    ).equalTo("devuelvo", 1)
                    val invoiceDevolver = queryDevolver.findAll()
                    Log.d("qweqweq", invoiceDevolver.toString())
                    val listaDevolver = realmDevolver.copyFromRealm(invoiceDevolver)
                    Log.d("qweqweq1", listaDevolver.toString() + "")
                    realmDevolver.close()

                    if (listaDevolver.size == 0) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "No hay productos para devolver",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        session!!.guardarDatosBloquearBotonesDevolver(1)
                        var i = 0
                        while (i < listaDevolver.size) {
                            Toast.makeText(
                                this@MenuPrincipal,
                                "Subiendo información...",
                                Toast.LENGTH_SHORT
                            ).show()

                            facturaIdDevuelto = listaDevolver[i].id
                            Log.d("facturaIdCV", facturaIdDevuelto.toString() + "")

                            val obj = EnviarProductoDevuelto(listaDevolver[i])
                            Log.d("My App", obj.toString() + "")
                            subirProductoDevuelto!!.sendPostClienteProductoDevuelto(
                                obj,
                                facturaIdDevuelto
                            )
                            i++
                        }
                    }
                } else {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Existen facturas pendientes de subir",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            id.btn_subir_recibos -> {
                val realmRecibos = Realm.getDefaultInstance()

                val queryRecibos: RealmQuery<receipts> =
                    realmRecibos.where(receipts::class.java).equalTo("aplicado", 1)
                val invoiceRecibos = queryRecibos.findAll()
                Log.d("qweqweq", invoiceRecibos.toString())
                val listaRecibos = realmRecibos.copyFromRealm(invoiceRecibos)
                Log.d("qweqweq1", listaRecibos.toString() + "")
                realmRecibos.close()

                if (listaRecibos.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay facturas para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "Subiendo información...",
                        Toast.LENGTH_SHORT
                    ).show()
                    var i = 0
                    while (i < listaRecibos.size) {
                        facturaIdRecibos = listaRecibos[i].customer_id
                        Log.d("facturaIdRecibos", facturaIdRecibos + "")
                        val obj = EnviarRecibos(listaRecibos[i])
                        Log.d("My App", obj.toString() + "")
                        subirHelperRecibos!!.sendPostRecibos(obj)
                        i++
                    }
                }
            }

            id.btn_subir_gpscliente -> {
                val realmClienteGPS = Realm.getDefaultInstance()

                val queryClienteGPS: RealmQuery<customer_location> = realmClienteGPS.where(
                    customer_location::class.java
                ).equalTo("subidaEdit", 1)
                val invoiceGPS = queryClienteGPS.findAll()
                Log.d("qweqweq", invoiceGPS.toString())
                val listaGPS = realmClienteGPS.copyFromRealm(invoiceGPS)
                Log.d("qweqweq1", listaGPS.toString() + "")
                realmClienteGPS.close()

                if (listaGPS.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay clientes para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var i = 0
                    while (i < listaGPS.size) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Subiendo información...",
                            Toast.LENGTH_SHORT
                        ).show()

                        facturaIdGPS = listaGPS[i].id
                        Log.d("facturaIdCV", facturaIdGPS + "")
                        val obj = EnviarClienteGPS(listaGPS[i])
                        Log.d("My App", obj.toString() + "")
                        subirClienteGPS!!.sendPostClienteGPS(obj)
                        i++
                    }
                }
            }

            id.btn_subir_cliente_nuevo -> {
                val realmClienteNuevo = Realm.getDefaultInstance()

                val queryClienteNuevo: RealmQuery<customer_new> = realmClienteNuevo.where(
                    customer_new::class.java
                ).equalTo("subidaNuevo", 1)
                val invoiceNuevo = queryClienteNuevo.findAll()
                Log.d("qweqweq", invoiceNuevo.toString())
                val listaNuevo = realmClienteNuevo.copyFromRealm(invoiceNuevo)
                Log.d("qweqweq1", listaNuevo.toString() + "")
                realmClienteNuevo.close()

                if (listaNuevo.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay clientes para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var i = 0
                    while (i < listaNuevo.size) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Subiendo información...",
                            Toast.LENGTH_SHORT
                        ).show()

                        facturaIdNuevoCliente = listaNuevo[i].id
                        Log.d("facturaIdCV", facturaIdNuevoCliente.toString() + "")
                        val obj = EnviarClienteNuevo(listaNuevo[i])
                        Log.d("My App", obj.toString() + "")
                        subirClienteNuevo!!.sendPostClienteNuevo(obj)
                        i++
                    }
                }
            }

            id.btn_subir_clienteVisitados -> {
                val realmClienteVisitados = Realm.getDefaultInstance()

                val queryClienteVisitados: RealmQuery<visit> = realmClienteVisitados.where(
                    visit::class.java
                ).equalTo("subida", 1)
                val invoiceVisits = queryClienteVisitados.findAll()
                Log.d("qweqweq", invoiceVisits.toString())
                val listaVisits = realmClienteVisitados.copyFromRealm(invoiceVisits)
                Log.d("qweqweq1", listaVisits.toString() + "")
                realmClienteVisitados.close()

                if (listaVisits.size == 0) {
                    Toast.makeText(
                        this@MenuPrincipal,
                        "No hay facturas para subir",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var i = 0
                    while (i < listaVisits.size) {
                        Toast.makeText(
                            this@MenuPrincipal,
                            "Subiendo información...",
                            Toast.LENGTH_SHORT
                        ).show()

                        facturaIdCV = listaVisits[i].id
                        Log.d("facturaIdCV", facturaIdCV.toString() + "")
                        val obj = EnviarClienteVisitado(listaVisits[i])
                        Log.d("My App", obj.toString() + "")
                        subirClienteVisitado!!.sendPostClienteVisitado(obj)
                        actualizarClienteVisitado()
                        i++
                    }
                }

                Toast.makeText(this@MenuPrincipal, "Se subio con éxito", Toast.LENGTH_SHORT).show()
            }

            id.btn_imprimir_liquidacion -> {
                /* if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {*/
                imprimirLiquidacionMenu(this@MenuPrincipal)
                Toast.makeText(this@MenuPrincipal, "imprimir liquidacion", Toast.LENGTH_SHORT)
                    .show()
            }

            id.btn_imprimir_orden_carga -> {
                /* if(bluetoothStateChangeReceiver.isBluetoothAvailable()== true) {*/
                imprimirOrdenCarga(this@MenuPrincipal)
                Toast.makeText(this@MenuPrincipal, "imprimir liquidacion", Toast.LENGTH_SHORT)
                    .show()
            }

            id.btn_imprimir_devolucion -> if (bluetoothStateChangeReceiver!!.isBluetoothAvailable == true) {
                imprimirDevoluciónMenu(this@MenuPrincipal)
                Toast.makeText(this@MenuPrincipal, "imprimir devolucion", Toast.LENGTH_SHORT).show()
            } else if (bluetoothStateChangeReceiver!!.isBluetoothAvailable == false) {
                CreateMessage(
                    this@MenuPrincipal,
                    "Error",
                    "La conexión del bluetooth ha fallado, favor revisar o conectar el dispositivo"
                )
            }
        }
        return false
    }

    fun actualizarFactura(factura: String?) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA FACTURAS

        val realm2 = Realm.getDefaultInstance()

        try {
            realm2.executeTransaction {
                val factura_actualizada =
                    realm2.where(invoice::class.java).equalTo("id", factura).findFirst()
                factura_actualizada!!.subida = 0
                factura_actualizada.devolucionInvoice = 0
                realm2.insertOrUpdate(factura_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "errorACT", Toast.LENGTH_SHORT).show()
        }
        realm2.close()
    }

    protected fun actualizarVenta(factura: String?) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()

        try {
            realm3.executeTransaction { realm3 ->
                val sale_actualizada =
                    realm3.where(sale::class.java).equalTo("invoice_id", factura).findFirst()
                sale_actualizada!!.subida = 0
                sale_actualizada.devolucion = 0
                realm3.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "errorACTVEN", Toast.LENGTH_SHORT).show()
        }
        realm3.close()
    }

    protected fun actualizarClienteGPS(factura: String?) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()

        try {
            realm3.executeTransaction { realm3 ->
                val sale_actualizada = realm3.where(
                    customer_location::class.java
                ).equalTo("id", factura).findFirst()
                sale_actualizada!!.subidaEdit = 0
                realm3.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "errorACTVEN", Toast.LENGTH_SHORT).show()
        }
        realm3.close()
    }

    protected fun actualizarClienteNuevo(factura: Int) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()

        try {
            realm3.executeTransaction { realm3 ->
                val sale_actualizada =
                    realm3.where(customer_new::class.java).equalTo("id", factura)
                        .findFirst()
                sale_actualizada!!.subidaNuevo = 0
                realm3.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "errorACTVEN", Toast.LENGTH_SHORT).show()
        }
        realm3.close()
    }

    protected fun actualizarVentaDist(factura: String?) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()

        try {
            realm3.executeTransaction { realm3 ->
                val sale_actualizada =
                    realm3.where(sale::class.java).equalTo("invoice_id", factura).findFirst()
                sale_actualizada!!.subida = 0
                realm3.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "errorACTVEN", Toast.LENGTH_SHORT).show()
        }
        realm3.close()
    }

    protected fun actualizarClienteVisitado() {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()

        try {
            realm3.executeTransaction {
                val sale_actualizada =
                    realm3.where(visit::class.java).equalTo("id", facturaIdCV).findFirst()
                sale_actualizada!!.subida = 0
                realm3.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "error", Toast.LENGTH_SHORT).show()
        }
        realm3.close()
    }

    protected fun actualizarAplicadoRecibo(factura: String?) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realmRecibos = Realm.getDefaultInstance()

        try {
            realmRecibos.executeTransaction {
                val sale_actualizada = realmRecibos.where(receipts::class.java)
                    .equalTo("customer_id", factura).findFirst()
                sale_actualizada!!.aplicado = 0
                realmRecibos.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "error", Toast.LENGTH_SHORT).show()
        }
        realmRecibos.close()
    }

    protected fun actualizarAbonadoRecibo(factura: String?) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()

        try {
            realm3.executeTransaction { realm3 ->
                val sale_actualizada =
                    realm3.where(recibos::class.java).equalTo("invoice_id", factura)
                        .findFirst()
                sale_actualizada!!.abonado = 0
                realm3.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "errorACTVEN", Toast.LENGTH_SHORT).show()
        }
        realm3.close()
    }

    protected fun actualizarInventarioDevuelto(factura: Int) {
        // TRANSACCION PARA ACTUALIZAR CAMPOS DE LA TABLA VENTAS

        val realm3 = Realm.getDefaultInstance()

        try {
            realm3.executeTransaction { realm3 ->
                val sale_actualizada =
                    realm3.where(Inventario::class.java).equalTo("id", factura)
                        .findFirst()
                sale_actualizada!!.devuelvo = 0
                realm3.insertOrUpdate(sale_actualizada)
            }
        } catch (e: Exception) {
            Log.e("error", "error", e)
            Toast.makeText(this@MenuPrincipal, "errorACTVEN", Toast.LENGTH_SHORT).show()
        }
        realm3.close()
    }


    fun ClickNavigation(view: View) {
        var fragment: Fragment? = null
        var fragmentClass: Class<*> = ConfiguracionFragment::class.java

        when (view.id) {
            id.clickConfig -> fragmentClass = ConfiguracionFragment::class.java

        }
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        } // Insert the fragment by replacing any existing fragment

        val fragmentManager = fragmentManager
        val mFragmentTransaction = fragmentManager.beginTransaction()
        mFragmentTransaction.replace(id.frame, fragment).commit()

        val drawer = findViewById<View>(id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
    }

    fun onClickGo(component: View) {
        consecutivoApi1()
        consecutivoApi(idUsuario)

        if (apiConsecutivo == null) {
            Toast.makeText(applicationContext, "Favor descargar info de empresa", Toast.LENGTH_LONG)
                .show()
        } else if (session!!.datosBloquearBotonesDevolver == 1) {
            when (component.id) {
                id.clickDistribucion -> Toast.makeText(
                    this@MenuPrincipal,
                    "Botón no disponible, descargar el inventario ya que fue devuelto",
                    Toast.LENGTH_LONG
                ).show()

                id.clickVentaDirecta -> Toast.makeText(
                    this@MenuPrincipal,
                    "Botón no disponible, descargar el inventario ya que fue devuelto",
                    Toast.LENGTH_LONG
                ).show()

                id.clickClientes -> {
                    val clientes =
                        Intent(this@MenuPrincipal, ClientesActivity::class.java)
                    startActivity(clientes)
                    finish()
                }

                id.clickProductos -> {
                    val productos =
                        Intent(this@MenuPrincipal, ProductosActivity::class.java)
                    startActivity(productos)
                    finish()
                }

                id.clickPreventa -> {
                    val preventa =
                        Intent(this@MenuPrincipal, PreventaActivity::class.java)
                    startActivity(preventa)
                    finish()
                }

                id.clickRecibos -> {
                    val recibos = Intent(this@MenuPrincipal, RecibosActivity::class.java)
                    startActivity(recibos)
                    finish()
                }

                id.clickReimprimirVentas -> {
                    val reimprimir =
                        Intent(this@MenuPrincipal, ReimprimirActivity::class.java)
                    startActivity(reimprimir)
                    finish()
                }

                id.clickReimprimirPedidos -> {
                    val reimprimirpedidos = Intent(
                        this@MenuPrincipal,
                        ReimprimirPedidosActivity::class.java
                    )
                    startActivity(reimprimirpedidos)
                    finish()
                }

                id.clickReimprimirRecibos -> {
                    //  Toast.makeText(MenuPrincipal.this, "Botón no disponible", Toast.LENGTH_LONG).show();
                    val reimprimirrecibos = Intent(
                        this@MenuPrincipal,
                        ReimprimirRecibosActivity::class.java
                    )
                    startActivity(reimprimirrecibos)
                    finish()
                }

                id.clickGrafico -> {
                    val graf = Intent(this@MenuPrincipal, GraficoActivity::class.java)
                    startActivity(graf)
                    finish()
                }

                id.clickEmail -> {
                    val email = Intent(this@MenuPrincipal, EmailActivity::class.java)
                    startActivity(email)
                    finish()
                }
            }
        } else if (apiConsecutivo == "0") {
            when (component.id) {
                id.clickDistribucion -> Toast.makeText(
                    this@MenuPrincipal,
                    "Botón no disponible",
                    Toast.LENGTH_LONG
                ).show()

                id.clickVentaDirecta -> Toast.makeText(
                    this@MenuPrincipal,
                    "Botón no disponible",
                    Toast.LENGTH_LONG
                ).show()

                id.clickClientes -> {
                    val clientes =
                        Intent(this@MenuPrincipal, ClientesActivity::class.java)
                    startActivity(clientes)
                    finish()
                }

                id.clickProductos -> {
                    val productos =
                        Intent(this@MenuPrincipal, ProductosActivity::class.java)
                    startActivity(productos)
                    finish()
                }

                id.clickPreventa -> {
                    val preventa =
                        Intent(this@MenuPrincipal, PreventaActivity::class.java)
                    startActivity(preventa)
                    finish()
                }

                id.clickRecibos -> {
                    val recibos = Intent(this@MenuPrincipal, RecibosActivity::class.java)
                    startActivity(recibos)
                    finish()
                }

                id.clickReimprimirVentas -> {
                    val reimprimir =
                        Intent(this@MenuPrincipal, ReimprimirActivity::class.java)
                    startActivity(reimprimir)
                    finish()
                }

                id.clickReimprimirPedidos -> {
                    val reimprimirpedidos = Intent(
                        this@MenuPrincipal,
                        ReimprimirPedidosActivity::class.java
                    )
                    startActivity(reimprimirpedidos)
                    finish()
                }

                id.clickReimprimirRecibos -> {
                    // Toast.makeText(MenuPrincipal.this, "Botón no disponible", Toast.LENGTH_LONG).show();
                    val reimprimirrecibos = Intent(
                        this@MenuPrincipal,
                        ReimprimirRecibosActivity::class.java
                    )
                    startActivity(reimprimirrecibos)
                    finish()
                }

                id.clickGrafico -> {
                    val graf = Intent(this@MenuPrincipal, GraficoActivity::class.java)
                    startActivity(graf)
                    finish()
                }

                id.clickEmail -> {
                    val email = Intent(this@MenuPrincipal, EmailActivity::class.java)
                    startActivity(email)
                    finish()
                }
            }
        } else {
            when (component.id) {
                id.clickClientes -> {
                    val clientes = Intent(this@MenuPrincipal, ClientesActivity::class.java)
                    startActivity(clientes)
                    finish()
                }

                id.clickProductos -> {
                    val productos =
                        Intent(this@MenuPrincipal, ProductosActivity::class.java)
                    startActivity(productos)
                    finish()
                }

                id.clickDistribucion -> {
                    val dist =
                        Intent(this@MenuPrincipal, DistribucionActivity::class.java)
                    startActivity(dist)
                    finish()
                }

                id.clickVentaDirecta -> {
                    val vd = Intent(this@MenuPrincipal, VentaDirectaActivity::class.java)
                    startActivity(vd)
                    finish()
                }

                id.clickPreventa -> {
                    val preventa =
                        Intent(this@MenuPrincipal, PreventaActivity::class.java)
                    startActivity(preventa)
                    finish()
                }

                id.clickRecibos -> {
                    val recibos = Intent(this@MenuPrincipal, RecibosActivity::class.java)
                    startActivity(recibos)
                    finish()
                }

                id.clickReimprimirVentas -> {
                    val reimprimir =
                        Intent(this@MenuPrincipal, ReimprimirActivity::class.java)
                    startActivity(reimprimir)
                    finish()
                }

                id.clickReimprimirPedidos -> {
                    val reimprimirpedidos = Intent(
                        this@MenuPrincipal,
                        ReimprimirPedidosActivity::class.java
                    )
                    startActivity(reimprimirpedidos)
                    finish()
                }

                id.clickReimprimirRecibos -> {
                    // Toast.makeText(MenuPrincipal.this, "Botón no disponible", Toast.LENGTH_LONG).show();
                    val reimprimirrecibos = Intent(
                        this@MenuPrincipal,
                        ReimprimirRecibosActivity::class.java
                    )
                    startActivity(reimprimirrecibos)
                    finish()
                }

                id.clickGrafico -> {
                    val graf = Intent(this@MenuPrincipal, GraficoActivity::class.java)
                    startActivity(graf)
                    finish()
                }

                id.clickEmail -> {
                    val email = Intent(this@MenuPrincipal, EmailActivity::class.java)
                    startActivity(email)
                    finish()
                }
            }
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

    private val isOnline: Boolean
        get() = networkStateChangeReceiver!!.isNetworkAvailable(this)

    fun codigoDeRespuestaPreventa(
        codS: String,
        messageS: String,
        resultS: String,
        cod: Int,
        idFacturaSubida: String?
    ): Int {
        val realmPedidos = Realm.getDefaultInstance()
        val queryPedidos: invoice =
            realmPedidos.where(invoice::class.java).equalTo("id", idFacturaSubida)
                .equalTo("subida", 1).equalTo("facturaDePreventa", "Preventa").findFirst()
        val numeroFactura = queryPedidos.numeration
        if (codS == "1" && resultS == "true") {
            facturaId = queryPedidos.id.toString()
            actualizarVenta(facturaId)
            actualizarFactura(facturaId)
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        } else {
            if (messageS == "La factura ya existe en el servidor .$numeroFactura") {
                facturaId = queryPedidos.id.toString()
                actualizarVenta(facturaId)
                actualizarFactura(facturaId)
            }
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        }

        /*  Realm realmPedidos = Realm.getDefaultInstance();
        RealmQuery<invoice> queryPedidos = realmPedidos.where(invoice.class).equalTo("subida", 1).equalTo("facturaDePreventa", "Preventa");
        final RealmResults<invoice> invoicePedidos = queryPedidos.findAll();
        Log.d("SubFacturaInvP", invoicePedidos.toString());
        List<invoice> listaFacturasPedidos = realmPedidos.copyFromRealm(invoicePedidos);
        Log.d("SubFacturaListaP", listaFacturasPedidos + "");
        realmPedidos.close();

        if(listaFacturasPedidos.size()== 0){
            Toast.makeText(MenuPrincipal.this,"No hay más facturas para subir", Toast.LENGTH_LONG).show();
        }else {

            for (int i = 0; i < listaFacturasPedidos.size(); i++) {
                facturaId = String.valueOf(listaFacturasPedidos.get(i).getId());
                if (codS.equals("1") && resultS.equals("true")) {
                    actualizarVenta(facturaId);
                    actualizarFactura(facturaId);
                    Toast.makeText(MenuPrincipal.this, messageS, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MenuPrincipal.this, messageS, Toast.LENGTH_LONG).show();
                }
            }
        }*/
        return cod
    }

    fun codigoDeRespuestaVD(
        codS: String,
        messageS: String,
        resultS: String,
        cod: Int,
        idFacturaSubida: String?
    ): Int {
        val realmPedidos = Realm.getDefaultInstance()
        val queryPedidos: invoice =
            realmPedidos.where(invoice::class.java).equalTo("id", idFacturaSubida)
                .equalTo("subida", 1).equalTo("facturaDePreventa", "VentaDirecta").findFirst()
        val numeroFactura = queryPedidos.numeration

        if (codS == "1" && resultS == "true") {
            facturaId = queryPedidos.id.toString()
            actualizarVenta(facturaId)
            actualizarFactura(facturaId)
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        } else {
            if (messageS == "La factura ya existe en el servidor .$numeroFactura") {
                facturaId = queryPedidos.id.toString()
                actualizarVenta(facturaId)
                actualizarFactura(facturaId)
            }
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        }

        return cod
    }

    fun codigoDeRespuestaClienteGPS(
        codS: String,
        messageS: String?,
        resultS: String,
        cod: Int
    ): Int {
        val realmPedidos = Realm.getDefaultInstance()
        val queryPedidos: RealmQuery<customer_location> = realmPedidos.where(
            customer_location::class.java
        ).equalTo("subidaEdit", 1)
        val invoicePedidos = queryPedidos.findAll()
        Log.d("SubFacturaInvP", invoicePedidos.toString())
        val listaFacturasPedidos = realmPedidos.copyFromRealm(invoicePedidos)
        Log.d("SubFacturaListaP", listaFacturasPedidos.toString() + "")
        realmPedidos.close()

        if (listaFacturasPedidos.size == 0) {
            Toast.makeText(this@MenuPrincipal, "No hay más facturas para subir", Toast.LENGTH_LONG)
                .show()
        } else {
            for (i in listaFacturasPedidos.indices) {
                facturaId = listaFacturasPedidos[i].id.toString()
                if (codS == "1" && resultS == "true") {
                    actualizarClienteGPS(facturaId)
                    Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
                }
            }
        }
        return cod
    }

    fun codigoDeRespuestaClienteNuevo(
        codS: String,
        messageS: String?,
        resultS: String,
        cod: Int
    ): Int {
        val realmPedidos = Realm.getDefaultInstance()
        val queryPedidos: RealmQuery<customer_new> =
            realmPedidos.where(customer_new::class.java).equalTo("subidaNuevo", 1)
        val invoicePedidos = queryPedidos.findAll()
        Log.d("SubFacturaInvP", invoicePedidos.toString())
        val listaFacturasPedidos = realmPedidos.copyFromRealm(invoicePedidos)
        Log.d("SubFacturaListaP", listaFacturasPedidos.toString() + "")
        realmPedidos.close()

        if (listaFacturasPedidos.size == 0) {
            Toast.makeText(this@MenuPrincipal, "No hay más facturas para subir", Toast.LENGTH_LONG)
                .show()
        } else {
            for (i in listaFacturasPedidos.indices) {
                facturaIdCC = listaFacturasPedidos[i].id
                if (codS == "1" && resultS == "true") {
                    actualizarClienteNuevo(facturaIdCC)
                    Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
                }
            }
        }
        return cod
    }


    fun codigoDeRespuestaDistr(
        codS: String,
        messageS: String,
        resultS: String,
        cod: Int,
        idFacturaSubida: String?
    ): Int {
        val realmPedidos = Realm.getDefaultInstance()
        val queryPedidos: invoice =
            realmPedidos.where(invoice::class.java).equalTo("id", idFacturaSubida)
                .equalTo("facturaDePreventa", "Distribucion").equalTo("subida", 1).or()
                .equalTo("devolucionInvoice", 1).findFirst()
        val numeroFactura = queryPedidos.numeration
        if (codS == "1" && resultS == "true") {
            facturaId = queryPedidos.id.toString()
            actualizarVenta(facturaId)
            actualizarFactura(facturaId)
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        } else {
            if (messageS == "La factura ya existe en el servidor .$numeroFactura") {
                facturaId = queryPedidos.id.toString()
                actualizarVenta(facturaId)
                actualizarFactura(facturaId)
            }
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        }



        return cod
    }

    fun codigoDeRespuestaProforma(
        codS: String,
        messageS: String?,
        resultS: String,
        cod: Int,
        idFacturaSubida: String?
    ): Int {
        val realmPedidos = Realm.getDefaultInstance()
        val queryPedidos: invoice =
            realmPedidos.where(invoice::class.java).equalTo("id", idFacturaSubida)
                .equalTo("subida", 1).equalTo("facturaDePreventa", "Proforma").findFirst()


        if (codS == "1" && resultS == "true") {
            facturaId = queryPedidos.id.toString()
            actualizarVenta(facturaId)
            actualizarFactura(facturaId)
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        }


        return cod
    }

    fun codigoDeRespuestaRecibos(codS: String, messageS: String?, resultS: String, cod: Int): Int {
        val realmRecibos = Realm.getDefaultInstance()
        val queryRecibos: RealmQuery<receipts> =
            realmRecibos.where(receipts::class.java).equalTo("aplicado", 1)
        val invoiceRecibos = queryRecibos.findAll()
        Log.d("SubFacturaInvP", invoiceRecibos.toString())
        val listaRecibos = realmRecibos.copyFromRealm(invoiceRecibos)
        Log.d("qweqweq1", listaRecibos.toString() + "")
        realmRecibos.close()

        if (listaRecibos.size == 0) {
            Toast.makeText(this@MenuPrincipal, "No hay más facturas para subir", Toast.LENGTH_LONG)
                .show()
        } else {
            for (i in listaRecibos.indices) {
                facturaCostumer = listaRecibos[i].customer_id.toString()

                facturaId = listaRecibos[i].customer_id.toString()

                val listaRecibosa: List<recibos> = listaRecibos[i].listaRecibos


                if (codS == "1" && resultS == "true") {
                    actualizarAplicadoRecibo(facturaCostumer)

                    for (a in listaRecibosa.indices) {
                        facturaIdA = listaRecibosa[a].invoice_id.toString()
                        actualizarAbonadoRecibo(facturaIdA)
                    }

                    Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
                }
            }
        }
        return cod
    }

    fun codigoDeRespuestaProductoDevuelto(
        codS: String,
        messageS: String?,
        resultS: String,
        cod: Int,
        idFacturaSubida: Int
    ): Int {
        val realmPedidos = Realm.getDefaultInstance()

        val queryPedidos: Inventario =
            realmPedidos.where(Inventario::class.java).equalTo("id", idFacturaSubida)
                .equalTo("devuelvo", 1).findFirst()

        if (codS == "1" && resultS == "true") {
            facturaIdDevolver = queryPedidos.id
            actualizarInventarioDevuelto(facturaIdDevolver)
            Log.d("Devuelto", "Devuelto")
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@MenuPrincipal, messageS, Toast.LENGTH_LONG).show()
        }


        return cod
    }


    companion object {
        private const val POPUP_CONSTANT = "mPopup"
        private const val POPUP_FORCE_SHOW_ICON = "setForceShowIcon"
    }
}
