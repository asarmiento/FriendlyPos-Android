package com.friendlypos.crearCliente.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.friendlypos.R
import com.friendlypos.crearCliente.modelo.customer_new
import com.friendlypos.distribucion.util.GPSTracker
import com.friendlypos.login.activity.LoginActivity
import com.friendlypos.login.util.SessionPrefes
import com.friendlypos.principal.activity.BluetoothActivity
import com.friendlypos.principal.activity.MenuPrincipal
import io.realm.Realm
import java.util.regex.Pattern

class crearCliente : BluetoothActivity() {
    @BindView(R.id.toolbarCrearCliente)
    lateinit var toolbarCrearCliente: Toolbar

    @BindView(R.id.cliente_card_nuevo)
    lateinit var cliente_card_nuevo: EditText

    @BindView(R.id.cliente_longitud_nuevo)
    lateinit var cliente_longitud_nuevo: TextView

    @BindView(R.id.cliente_latitud_nuevo)
    lateinit var cliente_latitud_nuevo: TextView

    @BindView(R.id.cliente_placa_nuevo)
    lateinit var cliente_placa_nuevo: EditText

    @BindView(R.id.cliente_model_nuevo)
    lateinit var cliente_model_nuevo: EditText

    @BindView(R.id.cliente_doors_nuevo)
    lateinit var cliente_doors_nuevo: EditText

    @BindView(R.id.cliente_email_nuevo)
    lateinit var cliente_email_nuevo: EditText

    @BindView(R.id.cliente_fantasyname_nuevo)
    lateinit var cliente_fantasyname_nuevo: EditText

    @BindView(R.id.cliente_companyname_nuevo)
    lateinit var cliente_companyname_nuevo: EditText

    @BindView(R.id.cliente_phone_nuevo)
    lateinit var cliente_phone_nuevo: EditText

    @BindView(R.id.cliente_creditlimit_nuevo)
    lateinit var cliente_creditlimit_nuevo: EditText

    @BindView(R.id.cliente_address_nuevo)
    lateinit var cliente_address_nuevo: EditText

    @BindView(R.id.btnCrearCliente)
    lateinit var btnCrearCliente: Button

    @BindView(R.id.cliente_credittime_nuevo)
    lateinit var spinnerCreditTime: Spinner

    @BindView(R.id.cliente_idtype_nuevo)
    lateinit var spinnerIdType: Spinner

    @BindView(R.id.cliente_fe_nuevo)
    lateinit var spinnerFe: Spinner

    var idtype1: String? = null
    var idtype: String? = null
    var card: String? = null
    var fe: String? = null
    var fe1: String? = null
    var placa: String? = null
    var model: String? = null
    var doors: String? = null
    var email: String? = null
    var fantasyname: String? = null
    var companyname: String? = null
    var phone: String? = null
    var creditlimit: String? = null
    var address: String? = null
    var credittime1: String? = null
    var credittime: String? = null
    var longitud: Double = 0.0
    var latitud: Double = 0.0
    var gps: GPSTracker? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var nextId: Int = 0


    var array_spinnerIdType: Array<String> = arrayOf(
        "Seleccione el tipo de cédula", "01: Cédula Fisica",
        "02: Cédula Juridica", "03: Dimex", "04: NITE"
    )

    var array_spinnerCreditTime: Array<String> = arrayOf(
        "Seleccione los días de crédito", "8 Dias",
        "15 Dias", "30 Dias", "45 Dias"
    )

    var array_spinnerFe: Array<String> = arrayOf(
        "Seleccione Fe", "Tiquete Electrónico",
        "Factura Electrónica"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_crear_cliente)
        ButterKnife.bind(this)

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.array_spinnerIdType1,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerIdType.adapter = adapter
        spinnerIdType.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                position: Int, id: Long
            ) {
                idtype1 = array_spinnerIdType[position]

                if (idtype1 == "01: Cédula Fisica") {
                    idtype = "01"
                } else if (idtype1 == "02: Cédula Juridica") {
                    idtype = "02"
                } else if (idtype1 == "03: Dimex") {
                    idtype = "03"
                } else if (idtype1 == "04: NITE") {
                    idtype = "04"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idtype = array_spinnerIdType[0]
            }
        }

        val adapter1 = ArrayAdapter.createFromResource(
            this, R.array.array_spinnerCreditTime1, android.R.layout.simple_spinner_item
        )
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCreditTime.adapter = adapter1
        spinnerCreditTime.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                position: Int, id: Long
            ) {
                credittime1 = array_spinnerCreditTime[position]

                if (credittime1 == "8 Dias") {
                    credittime = "8"
                } else if (credittime1 == "15 Dias") {
                    credittime = "15"
                } else if (credittime1 == "30 Dias") {
                    credittime = "30"
                } else if (credittime1 == "45 Dias") {
                    credittime = "45"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                credittime = array_spinnerCreditTime[0]
            }
        }


        val adapterFE = ArrayAdapter.createFromResource(
            this, R.array.array_spinnerFe1, android.R.layout.simple_spinner_item
        )
        adapterFE.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFe.adapter = adapterFE
        spinnerFe.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View,
                position: Int, id: Long
            ) {
                fe1 = array_spinnerFe[position]

                if (fe1 == "Tiquete Electrónico") {
                    fe = "false"
                } else if (fe1 == "Factura Electrónica") {
                    fe = "true"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                fe = array_spinnerFe[0]
            }
        }

        if (!SessionPrefes.get(this).isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setSupportActionBar(toolbarCrearCliente)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        Log.d("entro_a_crear", "entro_a_crear")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@crearCliente, MenuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            val intent = Intent(this@crearCliente, MenuPrincipal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            Log.d("ATRAS", "Atras")
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun isValidEmail(email: String): Boolean {
        val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun isValidName(name: String): Boolean {
        var check = false
        if (name.matches("^[\\p{L} .'-]+$".toRegex()) && name.length >= 1) {
            check = true
        }
        return check
    }

    private fun isValidMobile(phone2: String): Boolean {
        var check = false
        if (phone2.length == 8) {
            check = true
        }
        return check
    }

    private fun isValidCard01(phone2: String): Boolean {
        var check = false
        if (phone2.length == 9) {
            check = true
        }
        return check
    }

    private fun isValidCard02(phone2: String): Boolean {
        var check = false
        if (phone2.length == 10) {
            check = true
        }
        return check
    }

    private fun isValidCard03(phone2: String): Boolean {
        var check = false
        if (phone2.length == 12) {
            check = true
        }
        return check
    }

    private fun isValidCard04(phone2: String): Boolean {
        var check = false
        if (phone2.length > 9 && phone2.length < 12) {
            check = true
        }
        return check
    }


    fun onClickGo(component: View) {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        when (component.id) {
            R.id.btnCrearCliente -> {
                Log.d("entro_a_boton_crear", "entro_a_boton_crear")
                placa = cliente_placa_nuevo.text.toString()
                model = cliente_model_nuevo.text.toString()
                doors = cliente_doors_nuevo.text.toString()
                email = cliente_email_nuevo.text.toString()

                fantasyname = cliente_fantasyname_nuevo.text.toString()
                companyname = cliente_companyname_nuevo.text.toString()
                phone = cliente_phone_nuevo.text.toString()
                creditlimit = cliente_creditlimit_nuevo.text.toString()

                address = cliente_address_nuevo.text.toString()

                if (isValidEmail(email!!) && isValidName(companyname!!)) {
                    if (latitude != 0.0) {
                        if (longitude != 0.0) {
                            if (idtype1 == "Seleccione el tipo de cédula") {
                                Toast.makeText(
                                    this@crearCliente,
                                    "Seleccione un dato en tipo de cédula",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                if (fe1 == "Seleccione Fe") {
                                    Toast.makeText(
                                        this@crearCliente,
                                        "Seleccione un dato en Fe",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    if (idtype == "01") {
                                        card = cliente_card_nuevo.text.toString()
                                        if (isValidCard01(card!!)) {
                                            enviarInfo()
                                        } else {
                                            cliente_card_nuevo.error =
                                                "La cédula física debe ser de 9 dígitos"
                                            cliente_card_nuevo.requestFocus()
                                            Toast.makeText(
                                                this@crearCliente,
                                                "La cédula física debe ser de 9 dígitos",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else if (idtype == "02") {
                                        card = cliente_card_nuevo.text.toString()
                                        if (isValidCard02(card!!)) {
                                            //  Toast.makeText(crearCliente.this, "Bien", Toast.LENGTH_LONG).show();
                                            enviarInfo()
                                        } else {
                                            cliente_card_nuevo.error =
                                                "La cédula jurídica debe ser de 10 dígitos"
                                            cliente_card_nuevo.requestFocus()
                                            Toast.makeText(
                                                this@crearCliente,
                                                "La cédula jurídica debe ser de 10 dígitos",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else if (idtype == "03") {
                                        card = cliente_card_nuevo.text.toString()
                                        if (isValidCard03(card!!)) {
                                            //   Toast.makeText(crearCliente.this, "Bien", Toast.LENGTH_LONG).show();
                                            enviarInfo()
                                        } else {
                                            cliente_card_nuevo.error =
                                                "El DIMEX debe ser de 12 dígitos"
                                            cliente_card_nuevo.requestFocus()
                                            Toast.makeText(
                                                this@crearCliente,
                                                "El DIMEX debe ser de 12 dígitos",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else if (idtype == "04") {
                                        card = cliente_card_nuevo.text.toString()
                                        if (isValidCard04(card!!)) {
                                            Toast.makeText(
                                                this@crearCliente,
                                                "Bien",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            enviarInfo()
                                        } else {
                                            cliente_card_nuevo.error =
                                                "El NITE debe ser entre 10 u 11 dígitos"
                                            cliente_card_nuevo.requestFocus()
                                            Toast.makeText(
                                                this@crearCliente,
                                                "El NITE debe ser entre 10 u 11 dígitos",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@crearCliente,
                            "Obtenga la ubicación del cliente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (!isValidEmail(email!!)) {
                    cliente_email_nuevo.error = "Email inválido"
                    cliente_email_nuevo.requestFocus()
                } else if (!isValidName(companyname!!)) {
                    cliente_companyname_nuevo.error = "Company Name inválido"
                    cliente_companyname_nuevo.requestFocus()
                }
            }

            R.id.btnUbicacionCliente -> {
                obtenerLocalización()
                cliente_longitud_nuevo.text = "Longitud: $longitude"
                cliente_latitud_nuevo.text = "Latitud: $latitude"
            }
        }
    }

    fun enviarInfo() {
        Log.d("entro_a_enviar", "entro_a_enviar")
        val realm5 = Realm.getDefaultInstance()
        realm5.executeTransaction { realm5 ->
            val currentIdNum = realm5.where(customer_new::class.java).max("id")
            nextId = if (currentIdNum == null) {
                1
            } else {
                currentIdNum.toInt() + 1
            }

            val clienteNuevo = customer_new() // unmanaged

            clienteNuevo.id = nextId

            clienteNuevo.idtype = idtype
            clienteNuevo.card = card
            clienteNuevo.fe = fe

            clienteNuevo.longitud = longitude
            clienteNuevo.latitud = latitude
            clienteNuevo.placa = placa

            clienteNuevo.model = model
            clienteNuevo.doors = doors
            clienteNuevo.name = companyname

            clienteNuevo.email = email
            clienteNuevo.fantasy_name = fantasyname
            clienteNuevo.company_name = companyname

            clienteNuevo.phone = phone
            clienteNuevo.credit_limit = creditlimit
            clienteNuevo.address = address

            clienteNuevo.credit_time = credittime
            clienteNuevo.subidaNuevo = 1

            realm5.insertOrUpdate(clienteNuevo)
            Log.d("entro_a_creado", clienteNuevo.toString() + "")
        }
        realm5.close()
        Toast.makeText(this@crearCliente, "El cliente se creo correctamente", Toast.LENGTH_LONG)
            .show()
        limpiarCampos()
    }

    fun obtenerLocalización() {
        gps = GPSTracker(this@crearCliente)

        if (gps!!.canGetLocation()) {
            latitude = gps!!.latitude
            longitude = gps!!.longitude
        } else {
            gps!!.showSettingsAlert()
        }
    }

    fun limpiarCampos() {
        spinnerCreditTime.setSelection(0)
        spinnerFe.setSelection(0)
        spinnerIdType.setSelection(0)
        cliente_card_nuevo.setText("")
        cliente_placa_nuevo.setText("")
        cliente_model_nuevo.setText("")
        cliente_doors_nuevo.setText("")
        cliente_email_nuevo.setText("")
        cliente_fantasyname_nuevo.setText("")
        cliente_companyname_nuevo.setText("")
        cliente_phone_nuevo.setText("")
        cliente_creditlimit_nuevo.setText("")
        cliente_longitud_nuevo.text = ""
        cliente_latitud_nuevo.text = ""
    }
}
