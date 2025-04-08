package com.friendlysystemgroup.friendlypos.crearCliente.activity

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


import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.crearCliente.modelo.customer_new
import com.friendlysystemgroup.friendlypos.databinding.ActivityCrearClienteBinding
import com.friendlysystemgroup.friendlypos.distribucion.util.GPSTracker
import com.friendlysystemgroup.friendlypos.login.activity.LoginActivity
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.principal.activity.BluetoothActivity
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import io.realm.Realm
import java.util.regex.Pattern

/**
 * Actividad para crear un nuevo cliente
 */
class CrearClienteActivity : BluetoothActivity() {
    private lateinit var binding: ActivityCrearClienteBinding
    
    // Datos del cliente
    private var idType: String? = null
    private var card: String? = null
    private var facturaElectronica: String? = null
    private var placa: String? = null
    private var model: String? = null
    private var doors: String? = null
    private var email: String? = null
    private var fantasyName: String? = null
    private var companyName: String? = null
    private var phone: String? = null
    private var creditLimit: String? = null
    private var address: String? = null
    private var creditTime: String? = null
    
    // Ubicación
    private var gps: GPSTracker? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    
    // ID para Realm
    private var nextId: Int = 0
    
    // Opciones para spinners
    private val idTypeOptions = arrayOf(
        "Seleccione el tipo de cédula", 
        "01: Cédula Fisica",
        "02: Cédula Juridica", 
        "03: Dimex", 
        "04: NITE"
    )

    private val creditTimeOptions = arrayOf(
        "Seleccione los días de crédito", 
        "8 Dias",
        "15 Dias", 
        "30 Dias", 
        "45 Dias"
    )

    private val facturaElectronicaOptions = arrayOf(
        "Seleccione Fe", 
        "Tiquete Electrónico",
        "Factura Electrónica"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        binding = ActivityCrearClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        verificarSesion()
        configurarToolbar()
        configurarSpinners()
        configurarBotones()
        
        Log.d(TAG, "Actividad creada correctamente")
    }
    
    private fun verificarSesion() {
        if (!SessionPrefes.get(this).isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbarCrearCliente)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    private fun configurarSpinners() {
        // Configurar spinner de tipo de cédula
        configurarSpinnerIdType()
        
        // Configurar spinner de tiempo de crédito
        configurarSpinnerCreditTime()
        
        // Configurar spinner de factura electrónica
        configurarSpinnerFacturaElectronica()
    }
    
    private fun configurarSpinnerIdType() {
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.array_spinnerIdType1,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        
        binding.clienteIdtypeNuevo.adapter = adapter
        binding.clienteIdtypeNuevo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = idTypeOptions[position]
                
                idType = when (selectedOption) {
                    "01: Cédula Fisica" -> "01"
                    "02: Cédula Juridica" -> "02"
                    "03: Dimex" -> "03"
                    "04: NITE" -> "04"
                    else -> null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                idType = null
            }
        }
    }
    
    private fun configurarSpinnerCreditTime() {
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.array_spinnerCreditTime1, 
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        
        binding.clienteCredittimeNuevo.adapter = adapter
        binding.clienteCredittimeNuevo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = creditTimeOptions[position]
                
                creditTime = when (selectedOption) {
                    "8 Dias" -> "8"
                    "15 Dias" -> "15"
                    "30 Dias" -> "30"
                    "45 Dias" -> "45"
                    else -> null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                creditTime = null
            }
        }
    }
    
    private fun configurarSpinnerFacturaElectronica() {
        val adapter = ArrayAdapter.createFromResource(
            this, R.array.array_spinnerFe1, 
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        
        binding.clienteFeNuevo.adapter = adapter
        binding.clienteFeNuevo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = facturaElectronicaOptions[position]
                
                facturaElectronica = when (selectedOption) {
                    "Tiquete Electrónico" -> "false"
                    "Factura Electrónica" -> "true"
                    else -> null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                facturaElectronica = null
            }
        }
    }
    
    private fun configurarBotones() {
        binding.btnCrearCliente.setOnClickListener {
            ocultarTeclado()
            validarYCrearCliente()
        }
        
        binding.btnUbicacionCliente?.setOnClickListener {
            obtenerLocalizacion()
            actualizarEtiquetasUbicacion()
        }
    }
    
    private fun ocultarTeclado() {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
    
    private fun validarYCrearCliente() {
        // Leer datos de los campos
        leerDatosDeCampos()
        
        // Validar datos básicos
        if (!validarDatosBasicos()) {
            return
        }
        
        // Validar ubicación
        if (!validarUbicacion()) {
            return
        }
        
        // Validar campos según tipo de ID
        validarCedulaSegunTipo()
    }
    
    private fun leerDatosDeCampos() {
        placa = binding.clientePlacaNuevo.text.toString()
        model = binding.clienteModelNuevo.text.toString()
        doors = binding.clienteDoorsNuevo.text.toString()
        email = binding.clienteEmailNuevo.text.toString()
        fantasyName = binding.clienteFantasynameNuevo.text.toString()
        companyName = binding.clienteCompanynameNuevo.text.toString()
        phone = binding.clientePhoneNuevo.text.toString()
        creditLimit = binding.clienteCreditlimitNuevo.text.toString()
        address = binding.clienteAddressNuevo.text.toString()
        card = binding.clienteCardNuevo.text.toString()
    }
    
    private fun validarDatosBasicos(): Boolean {
        if (!isValidEmail(email ?: "")) {
            binding.clienteEmailNuevo.error = "Email inválido"
            binding.clienteEmailNuevo.requestFocus()
            return false
        }
        
        if (!isValidName(companyName ?: "")) {
            binding.clienteCompanynameNuevo.error = "Nombre de compañía inválido"
            binding.clienteCompanynameNuevo.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun validarUbicacion(): Boolean {
        if (latitude == 0.0 || longitude == 0.0) {
            Toast.makeText(
                this,
                "Obtenga la ubicación del cliente",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }
    
    private fun validarCedulaSegunTipo() {
        when {
            idType == null || idType == "Seleccione el tipo de cédula" -> {
                Toast.makeText(this, "Seleccione un tipo de cédula", Toast.LENGTH_LONG).show()
            }
            
            facturaElectronica == null || facturaElectronica == "Seleccione Fe" -> {
                Toast.makeText(this, "Seleccione un tipo de factura electrónica", Toast.LENGTH_LONG).show()
            }
            
            idType == "01" && !isValidCard01(card ?: "") -> {
                mostrarErrorCedula("La cédula física debe ser de 9 dígitos")
            }
            
            idType == "02" && !isValidCard02(card ?: "") -> {
                mostrarErrorCedula("La cédula jurídica debe ser de 10 dígitos")
            }
            
            idType == "03" && !isValidCard03(card ?: "") -> {
                mostrarErrorCedula("El DIMEX debe ser de 12 dígitos")
            }
            
            idType == "04" && !isValidCard04(card ?: "") -> {
                mostrarErrorCedula("El NITE debe ser entre 10 y 11 dígitos")
            }
            
            else -> guardarCliente()
        }
    }
    
    private fun mostrarErrorCedula(mensaje: String) {
        binding.clienteCardNuevo.error = mensaje
        binding.clienteCardNuevo.requestFocus()
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
    
    private fun guardarCliente() {
        Log.d(TAG, "Guardando cliente...")
        val realm = Realm.getDefaultInstance()
        
        try {
            realm.executeTransaction { r ->
                val currentIdNum = r.where(customer_new::class.java).max("id")
                nextId = if (currentIdNum == null) 1 else currentIdNum.toInt() + 1
                
                val clienteNuevo = customer_new().apply {
                    id = nextId
                    idtype = idType
                    card = this@CrearClienteActivity.card
                    fe = facturaElectronica
                    longitud = longitude
                    latitud = latitude
                    placa = this@CrearClienteActivity.placa
                    model = this@CrearClienteActivity.model
                    doors = this@CrearClienteActivity.doors
                    name = companyName
                    email = this@CrearClienteActivity.email
                    fantasy_name = fantasyName
                    company_name = companyName
                    phone = this@CrearClienteActivity.phone
                    credit_limit = creditLimit
                    address = this@CrearClienteActivity.address
                    credit_time = creditTime
                    subidaNuevo = 1
                }
                
                r.insertOrUpdate(clienteNuevo)
                Log.d(TAG, "Cliente creado: $clienteNuevo")
            }
            
            Toast.makeText(this, "El cliente se creó correctamente", Toast.LENGTH_LONG).show()
            limpiarCampos()
            
        } finally {
            realm.close()
        }
    }
    
    private fun obtenerLocalizacion() {
        gps = GPSTracker(this).apply {
            if (canGetLocation()) {
                latitude = this.latitude
                longitude = this.longitude
            } else {
                showSettingsAlert()
            }
        }
    }
    
    private fun actualizarEtiquetasUbicacion() {
        binding.clienteLongitudNuevo.text = "Longitud: $longitude"
        binding.clienteLatitudNuevo.text = "Latitud: $latitude"
    }
    
    private fun limpiarCampos() {
        binding.clienteCredittimeNuevo.setSelection(0)
        binding.clienteFeNuevo.setSelection(0)
        binding.clienteIdtypeNuevo.setSelection(0)
        binding.clienteCardNuevo.setText("")
        binding.clientePlacaNuevo.setText("")
        binding.clienteModelNuevo.setText("")
        binding.clienteDoorsNuevo.setText("")
        binding.clienteEmailNuevo.setText("")
        binding.clienteFantasynameNuevo.setText("")
        binding.clienteCompanynameNuevo.setText("")
        binding.clientePhoneNuevo.setText("")
        binding.clienteCreditlimitNuevo.setText("")
        binding.clienteLongitudNuevo.text = ""
        binding.clienteLatitudNuevo.text = ""
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navegarAlMenuPrincipal()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navegarAlMenuPrincipal()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    
    private fun navegarAlMenuPrincipal() {
        Intent(this, MenuPrincipal::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
        finish()
    }
    
    // Funciones de validación
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }

    private fun isValidName(name: String): Boolean {
        return name.matches("^[\\p{L} .'-]+$".toRegex()) && name.isNotEmpty()
    }

    private fun isValidCard01(card: String): Boolean = card.length == 9
    private fun isValidCard02(card: String): Boolean = card.length == 10
    private fun isValidCard03(card: String): Boolean = card.length == 12
    private fun isValidCard04(card: String): Boolean = card.length in 10..11
    
    companion object {
        private const val TAG = "CrearClienteActivity"
    }
}
