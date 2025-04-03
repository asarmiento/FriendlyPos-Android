package com.friendlypos.login.activity

import android.content.Context
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.friendlypos.application.util.Functions
import com.friendlypos.login.modelo.User
import com.friendlypos.login.util.Properties
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.io.IOException
import butterknife.BindView
import butterknife.ButterKnife

class LoginActivity : AppCompatActivity() {
    private var api: RequestInterface? = null
    private var networkStateChangeReceiver: NetworkStateChangeReceiver? = null
    private var progress: ProgressDialog? = null
    var download1: DescargasHelper? = null
    var session: SessionPrefes? = null

    @BindView(R.id.usuario)
    lateinit var mUserIdView: EditText

    @BindView(R.id.contraseña)
    lateinit var mPasswordView: EditText

    @BindView(R.id.image_logo)
    lateinit var mLogoView: ImageView

    @BindView(R.id.float_label_user_id)
    lateinit var mFloatLabelUserId: TextInputLayout

    @BindView(R.id.float_label_password)
    lateinit var mFloatLabelPassword: TextInputLayout

    @BindView(R.id.login_progress)
    lateinit var mProgressView: View

    @BindView(R.id.login_form)
    lateinit var mLoginFormView: View

    @BindView(R.id.email_sign_in_button)
    lateinit var mSignInButton: Button

    @BindView(R.id.RLLogin)
    lateinit var RLLogin: RelativeLayout
    var properties: Properties? = null

    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
        context = this
        session = SessionPrefes(getApplicationContext())
        properties = Properties(getApplicationContext())
        download1 = DescargasHelper(this@LoginActivity)
        //
        if (properties!!.urlWebsrv == null) {
            //if ("http://"+properties.getUrlWebsrv() == null) {
            properties!!.urlWebsrv = "friendlyaccount.com"
            //  Toast.makeText(this, "URL: " + "http://"+properties.getUrlWebsrv() + "",Toast.LENGTH_SHORT).show();
        } else {
            //  Toast.makeText(this, "URL1: " + "http://"+properties.getUrlWebsrv() + "",Toast.LENGTH_SHORT).show();
        }


        if (session.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        initProgressbar()
        val copy: HtmlTextView = findViewById<View>(R.id.copyright) as HtmlTextView
        copy.setHtmlFromString(
            "<font size=\"7sp\"><a href=\"http://www.sistemasamigables.com/\">" + Functions.getVesionNaveCode(
                context!!
            ) + " " + context.getString(R.string.credits) + "</a></font>", LocalImageGetter()
        )

        networkStateChangeReceiver = NetworkStateChangeReceiver()

        mLogoView!!.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                ShowOpenSettings()
                return false
            }
        })

        // Setup
        mPasswordView.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(textView: TextView, id: Int, keyEvent: KeyEvent): Boolean {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!this.isOnline) {
                        showLoginError(getString(R.string.error_network))
                        return false
                    }
                    attemptLogin()
                    return true
                }
                return false
            }
        })

        mSignInButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (!this.isOnline) {
                    showLoginError(getString(R.string.error_network))
                    return
                }

                attemptLogin()
            }
        })
    }

    private fun initProgressbar() {
        progress = ProgressDialog(this)
        progress.setMessage("Iniciando sesión")
        progress.setCanceledOnTouchOutside(false)
    }

    private fun attemptLogin() {
        progress.show()
        // Reset errors.
        mFloatLabelUserId.setError(null)
        mFloatLabelPassword.setError(null)

        // Store values at the time of the login attempt.
        val userdatos: String = mUserIdView.getText().toString()
        val userId = userdatos.trim { it <= ' ' }

        val password: String = mPasswordView.getText().toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            progress.dismiss()
            mFloatLabelPassword.setError(getString(R.string.error_field_required))
            focusView = mFloatLabelPassword
            cancel = true
        } else if (!isPasswordValid(password)) {
            progress.dismiss()
            mFloatLabelPassword.setError(getString(R.string.error_invalid_password))
            focusView = mFloatLabelPassword
            cancel = true
        }

        // Verificar si el ID tiene contenido.
        if (TextUtils.isEmpty(userId)) {
            progress.dismiss()
            mFloatLabelUserId.setError(getString(R.string.error_field_required))
            focusView = mFloatLabelUserId
            cancel = true
        } else if (!isUserIdValid(userId)) {
            progress.dismiss()
            mFloatLabelUserId.setError(getString(R.string.error_invalid_user_id))
            focusView = mFloatLabelUserId
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Mostrar el indicador de carga y luego iniciar la petición asíncrona.

            api = BaseManager.api
            val call: retrofit2.Call<UserResponse> = api.loginUser(User(userId, password))

            call.enqueue(object : retrofit2.Callback<UserResponse?> {
                override fun onResponse(
                    call: retrofit2.Call<UserResponse?>?,
                    response: retrofit2.Response<UserResponse?>
                ) {
                    progress.dismiss()
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
                    session.guardarDatosUsuario(response.body())
                    Log.d("UserEntrar", response.body().getToken_type() + "")
                    session.guardarDatosUsuarioas(userId, password)
                    Log.d("UserEntrar1", "$userId $password")
                    download1.descargarUsuarios(context)
                    entrarMenuPrincipal()

                    //showAppointmentsScreen();
                }

                override fun onFailure(call: retrofit2.Call<UserResponse?>?, t: Throwable) {
                    progress.dismiss()
                    showLoginError(t.message)
                }
            })
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
            Snackbar.make(RLLogin, "Abrir Configuraciones?", Snackbar.LENGTH_INDEFINITE).setAction(
                "Abrir",
                View.OnClickListener {
                    val i: Intent =
                        Intent(getApplicationContext(), ConfiguracionActivity::class.java)
                    startActivity(i)
                })
        val colorNoti: View = show.getView()

        colorNoti.setBackgroundColor(context!!.resources.getColor(R.color.colorPrimaryDark))
        show.show()
    }

    private val isOnline: Boolean
        get() = networkStateChangeReceiver.isNetworkAvailable(this)
}

