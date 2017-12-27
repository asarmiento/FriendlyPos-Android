package com.friendlypos.login.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlypos.R;
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.application.interfaces.RequestInterface;
import com.friendlypos.application.util.Functions;
import com.friendlypos.login.modelo.User;
import com.friendlypos.login.modelo.UserError;
import com.friendlypos.login.modelo.UserResponse;
import com.friendlypos.login.util.Properties;
import com.friendlypos.login.util.SessionPrefes;
import com.friendlypos.principal.activity.MenuPrincipal;
import com.friendlypos.principal.fragment.ConfiguracionFragment;
import com.friendlypos.principal.helpers.DescargasHelper;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

public class LoginActivity extends AppCompatActivity {

    private RequestInterface api;
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private ProgressDialog progress;
    DescargasHelper download1;
    SessionPrefes session;

    @Bind(R.id.usuario)
    EditText mUserIdView;

    @Bind(R.id.contraseña)
    EditText mPasswordView;

    @Bind(R.id.image_logo)
    ImageView mLogoView;

    @Bind(R.id.float_label_user_id)
    TextInputLayout mFloatLabelUserId;

    @Bind(R.id.float_label_password)
    TextInputLayout mFloatLabelPassword;

    @Bind(R.id.login_progress)
    View mProgressView;

    @Bind(R.id.login_form)
    View mLoginFormView;

    @Bind(R.id.email_sign_in_button)
    Button mSignInButton;

    @Bind(R.id.RLLogin)
    RelativeLayout RLLogin;
    Properties properties;

    Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        session = new SessionPrefes(getApplicationContext());
        properties = new Properties(getApplicationContext());
        download1 = new DescargasHelper(LoginActivity.this);
        //
        if (properties.getUrlWebsrv() == null) {
            properties.setUrlWebsrv("friendlyaccount.com");
            Toast.makeText(this, "URL: " + properties.getUrlWebsrv() + "",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "URL: " + properties.getUrlWebsrv() + "",Toast.LENGTH_SHORT).show();
        }


        if (session.isLoggedIn()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        initProgressbar();
        HtmlTextView copy = (HtmlTextView) findViewById(R.id.copyright);
        copy.setHtmlFromString("<font size=\"7sp\"><a href=\"http://www.sistemasamigables.com/\">" + Functions.getVesionNaveCode(context) + " " + context.getString(R.string.credits) + "</a></font>", new HtmlTextView.LocalImageGetter());

        networkStateChangeReceiver = new NetworkStateChangeReceiver();

        mLogoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ShowOpenSettings();
                return false;
            }
        });

        // Setup
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!isOnline()) {
                        showLoginError(getString(R.string.error_network));
                        return false;
                    }
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    showLoginError(getString(R.string.error_network));
                    return;
                }

                attemptLogin();

            }
        });
    }

    private void initProgressbar() {
        progress = new ProgressDialog(this);
        progress.setMessage("Iniciando sesión");
        progress.setCanceledOnTouchOutside(false);
    }

    private void attemptLogin() {
        progress.show();
        // Reset errors.
        mFloatLabelUserId.setError(null);
        mFloatLabelPassword.setError(null);

        // Store values at the time of the login attempt.
        final String userdatos = mUserIdView.getText().toString();
        final String userId = userdatos.trim();

        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            progress.dismiss();
            mFloatLabelPassword.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelPassword;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            progress.dismiss();
            mFloatLabelPassword.setError(getString(R.string.error_invalid_password));
            focusView = mFloatLabelPassword;
            cancel = true;
        }

        // Verificar si el ID tiene contenido.
        if (TextUtils.isEmpty(userId)) {
            progress.dismiss();
            mFloatLabelUserId.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelUserId;
            cancel = true;
        }
        else if (!isUserIdValid(userId)) {
            progress.dismiss();
            mFloatLabelUserId.setError(getString(R.string.error_invalid_user_id));
            focusView = mFloatLabelUserId;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            // Mostrar el indicador de carga y luego iniciar la petición asíncrona.

            api = BaseManager.getApi();
            Call<UserResponse> call = api.loginUser(new User(userId, password));

            call.enqueue(new Callback<UserResponse>() {

                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    progress.dismiss();
                    // Procesar errores
                    if (!response.isSuccessful()) {
                        String error = "Ha ocurrido un error. Contacte al administrador";
                        if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                            UserError userError = UserError.fromResponseBody(response.errorBody());

                            error = userError.getMessage();
                            Log.d("LoginActivity", userError.getMessage());
                        }
                        else {
                            try {
                                // Reportar causas de error no relacionado con la API
                                Log.d("LoginActivity", response.errorBody().string());
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        showLoginError(error);

                        return;
                    }
                    Log.d("fsdfsdfs", response.body().getToken_type() + " " + response.body().getAccess_token() + "");

                    // Guardar afiliado en preferencias
                    session.guardarDatosUsuario(response.body());
                    session.guardarDatosUsuarioas(userId, password);
                    download1.descargarUsuarios(context);
                    entrarMenuPrincipal();

                    //showAppointmentsScreen();
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    progress.dismiss();
                    showLoginError(t.getMessage());
                }
            });
        }
    }final Handler handler = new Handler();
    protected void entrarMenuPrincipal(){
        Thread t = new Thread(){
            public void run(){
                try{
                    Thread.sleep(3000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                handler.post(irMenuPrincipal);
            }
        };
        t.start();
    }
    final Runnable irMenuPrincipal = new Runnable() {
        @Override
        public void run() {
            showAppointmentsScreen();
            Toast.makeText(context, "menu", Toast.LENGTH_LONG).show();
        }
    };

    private boolean isUserIdValid(String userId) {
        return userId.length() > 10;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        mLogoView.setVisibility(visibility);
        mLoginFormView.setVisibility(visibility);
    }

    private void showAppointmentsScreen() {

        startActivity(new Intent(this, MenuPrincipal.class));
        finish();
    }

    private void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void ShowOpenSettings(){
        Snackbar show = Snackbar.make(RLLogin,"Abrir Configuraciones?", Snackbar.LENGTH_INDEFINITE).setAction("Abrir",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), ConfiguracionActivity.class);
                        startActivity(i);
                    }
                });
        View colorNoti= show.getView();

        colorNoti.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        show.show();
    }

    private boolean isOnline() {
        return networkStateChangeReceiver.isNetworkAvailable(this);
    }

}

