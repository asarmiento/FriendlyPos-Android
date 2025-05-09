package com.friendlysystemgroup.friendlypos.login.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.friendlysystemgroup.friendlypos.R
import com.friendlysystemgroup.friendlypos.application.util.Functions
import com.friendlysystemgroup.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver
import com.friendlysystemgroup.friendlypos.application.util.LocalImageGetter
import com.friendlysystemgroup.friendlypos.databinding.ActivityLoginBinding
import com.friendlysystemgroup.friendlypos.login.activity.ConfiguracionActivity
import com.friendlysystemgroup.friendlypos.login.api.BaseManager
import com.friendlysystemgroup.friendlypos.login.api.RequestInterface
import com.friendlysystemgroup.friendlypos.login.api.UserError
import com.friendlysystemgroup.friendlypos.login.api.UserResponse
import com.friendlysystemgroup.friendlypos.login.modelo.User
import com.friendlysystemgroup.friendlypos.login.util.Properties
import com.friendlysystemgroup.friendlypos.login.util.SessionPrefes
import com.friendlysystemgroup.friendlypos.principal.activity.MenuPrincipal
import com.friendlysystemgroup.friendlypos.principal.helpers.DescargasHelper
import com.google.android.material.snackbar.Snackbar
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private var api: RequestInterface? = null
    private var networkStateChangeReceiver: NetworkStateChangeReceiver? = null
    private var progress: ProgressDialog? = null
    var download1: DescargasHelper? = null
    var session: SessionPrefes? = null
    
    // Reemplazar @BindView con ViewBinding
    private lateinit var binding: ActivityLoginBinding
    
    var properties: Properties? = null
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Inicializar ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        context = this
        session = SessionPrefes(applicationContext)
        properties = Properties(applicationContext)
        download1 = DescargasHelper(this@LoginActivity)
        api = BaseManager.getApi()
        
        //
        if (properties?.urlWebsrv == null) {
            //if ("http://"+properties.getUrlWebsrv() == null) {
            properties?.urlWebsrv = "friendlyaccount.com"
            //  Toast.makeText(this, "URL: " + "http://"+properties.getUrlWebsrv() + "",Toast.LENGTH_SHORT).show();
        } else {
            //  Toast.makeText(this, "URL1: " + "http://"+properties.getUrlWebsrv() + "",Toast.LENGTH_SHORT).show();
        }

        session?.let {
            if (it.isLoggedIn) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        
        initProgressbar()
        
        val copy = findViewById<HtmlTextView>(R.id.copyright)
        val appContext = context ?: applicationContext
        copy.setHtmlFromString(
            "<font size=\"7sp\"><a href=\"http://www.sistemasamigables.com/\">" + Functions.getVesionNaveCode(
                appContext
            ) + " " + appContext.getString(R.string.credits) + "</a></font>", LocalImageGetter()
        )

        networkStateChangeReceiver = NetworkStateChangeReceiver()

        binding.imageLogo.setOnLongClickListener(OnLongClickListener { v ->
            ShowOpenSettings()
            false
        })

        // Setup
        binding.contraseña.setOnEditorActionListener(OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                if (!isOnline) {
                    showLoginError(getString(R.string.error_network))
                    return@OnEditorActionListener false
                }
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        binding.emailSignInButton.setOnClickListener { view ->
            if (!isOnline) {
                showLoginError(getString(R.string.error_network))
                return@setOnClickListener
            }

            attemptLogin()
        }
    }

    private fun initProgressbar() {
        progress = ProgressDialog(this)
        progress?.setMessage("Iniciando sesión")
        progress?.setCanceledOnTouchOutside(false)
    }

    private fun attemptLogin() {
        progress?.show()
        // Reset errors.
        binding.floatLabelUserId.error = null
        binding.floatLabelPassword.error = null

        // Store values at the time of the login attempt.
        val userdatos: String = binding.usuario.text.toString()
        val userId = userdatos.trim { it <= ' ' }

        val password: String = binding.contraseña.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            progress?.dismiss()
            binding.floatLabelPassword.error = getString(R.string.error_field_required)
            focusView = binding.floatLabelPassword
            cancel = true
        } else if (!isPasswordValid(password)) {
            progress?.dismiss()
            binding.floatLabelPassword.error = getString(R.string.error_invalid_password)
            focusView = binding.floatLabelPassword
            cancel = true
        }

        // Verificar si el ID tiene contenido.
        if (TextUtils.isEmpty(userId)) {
            progress?.dismiss()
            binding.floatLabelUserId.error = getString(R.string.error_field_required)
            focusView = binding.floatLabelUserId
            cancel = true
        } else if (!isUserIdValid(userId)) {
            progress?.dismiss()
            binding.floatLabelUserId.error = getString(R.string.error_invalid_user_id)
            focusView = binding.floatLabelUserId
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Mostrar el indicador de carga y luego iniciar la petición asíncrona.
            api?.let { apiInstance ->
                val user = User(userId, password)
                val call: retrofit2.Call<UserResponse?>? = apiInstance.loginUser(user)

                call?.enqueue(object : retrofit2.Callback<UserResponse?> {
                    override fun onResponse(
                        call: retrofit2.Call<UserResponse?>?,
                        response: retrofit2.Response<UserResponse?>
                    ) {
                        progress?.dismiss()
                        // Procesar errores
                        if (!response.isSuccessful()) {
                            var error = "Ha ocurrido un error. Contacte al administrador"
                            if (response.errorBody()
                                    .contentType()
                                    .subtype()
                                == "json"
                            ) {
                                val userError: UserError =
                                    UserError.fromResponseBody(response.errorBody())

                                error = userError.getMessage()
                                Log.d("LoginActivity", userError.getMessage())
                            } else {
                                try {
                                    // Reportar causas de error no relacionado con la API
                                    Log.d("LoginActivity", response.errorBody().string())
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }

                            showLoginError(error)

                            return
                        }

                        // Guardar afiliado en preferencias
                        response.body()?.let { body ->
                            session?.guardarDatosUsuario(body)
                            Log.d("UserEntrar", body.getToken_type() + "")
                            session?.guardarDatosUsuarioas(userId, password)
                            Log.d("UserEntrar1", "$userId $password")
                            download1?.descargarUsuarios(context ?: this@LoginActivity)
                            entrarMenuPrincipal()
                        }

                        //showAppointmentsScreen();
                    }

                    override fun onFailure(call: retrofit2.Call<UserResponse?>?, t: Throwable) {
                        progress?.dismiss()
                        showLoginError(t.message)
                    }
                })
            }
        }
    }

    val handler: Handler = Handler()
    protected fun entrarMenuPrincipal() {
        val t: Thread = object : Thread() {
            override fun run() {
                try {
                    // Thread.sleep(10000);
                    sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                handler.post(irMenuPrincipal)
            }
        }
        t.start()
    }

    val irMenuPrincipal: Runnable = Runnable { showAppointmentsScreen() }

    private fun isUserIdValid(userId: String): Boolean {
        return userId.length > 10
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    private fun showAppointmentsScreen() {
        startActivity(Intent(this, MenuPrincipal::class.java))
        finish()
    }

    private fun showLoginError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    private fun ShowOpenSettings() {
        val show: Snackbar =
            Snackbar.make(binding.RLLogin, "Abrir Configuraciones?", Snackbar.LENGTH_INDEFINITE)
                .setAction("Abrir") {
                    val i = Intent(applicationContext, ConfiguracionActivity::class.java)
                    startActivity(i)
                }
        val colorNoti: View = show.view
        val safeContext = context ?: this
        colorNoti.setBackgroundColor(safeContext.resources.getColor(R.color.colorPrimaryDark))
        show.show()
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver?.isNetworkAvailable(this) ?: false
}

