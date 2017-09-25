package com.friendlypos.login.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.friendlypos.R;
import com.friendlypos.login.activity.BaseActivity;
import com.friendlypos.login.activity.LoginActivity;
import com.friendlypos.login.helper.InitHelper;
import com.friendlypos.login.interfaces.LoginListener;
import com.friendlypos.login.interfaces.ServiceCallback;
import com.friendlypos.login.modelo.AppResponse;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends BaseFragment implements InitHelper {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private static final String LOGIN = "Login ";

    @Bind(R.id.login)
    Button mLogin;

    @Bind(R.id.usuario)
    EditText mEmail;

    @Bind(R.id.contraseña)
    EditText mPassword;

    @Bind(R.id.usuario_wrapper)
    TextInputLayout mEmailWrapper;

    @Bind(R.id.contraseña_wrapper)
    TextInputLayout mPassWrapper;

    private ProgressDialog progressDialog;

    private LoginListener loginListener;

    private View mainView;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, mainView);
        init();
        return mainView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loginListener = (LoginActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginListener = null;
        ButterKnife.unbind(mainView);
    }

  /*  @OnClick({R.id.register})
    public void onRegisterClick(View v) {
        replaceCurrentFragment(RegisterFragment.newInstance());
    }*/

    @OnClick(R.id.login)
    public void onLoginClick(View v) {
        String email = mEmail.getText().toString();
        String pass = mPassword.getText().toString();


        if (((LoginActivity) getActivity()).isValidEmail(email, mEmail)) {
            if (((LoginActivity) getActivity()).isValidPassword(pass, mPassword)) {
                login(email, pass);
            }
        }
    }

    private void login(final String user, final String password) {
        progressDialog.show();
        loginListener.doLogin(user, password, new ServiceCallback() {

            @Override
            public void onSuccess(Object status, Object response) {
                progressDialog.dismiss();
                loginListener.onLoginSuccess((AppResponse) response, user, password);
                Log.d(TAG, LOGIN + response.toString());
            }

            @Override
            public void onError(Object networkError) {
                progressDialog.dismiss();
                loginListener.onLoginError(((AppResponse) networkError).getExpires_in());
            }

            @Override
            public void onPreExecute() {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BaseActivity) getActivity()).enableDisableBackButtonToolbar(false);
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void init() {
        initFrontEnd();
        initBackEnd();
    }

    @Override
    public void initFrontEnd() {
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(LOGIN);
    }

    @Override
    public void initBackEnd() {
    }
}