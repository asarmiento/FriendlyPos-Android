package com.friendlypos.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.friendlypos.R;
import com.friendlypos.app.broadcastreceiver.NetworkStateChangeReceiver;
import com.friendlypos.login.controller.RealmController;
import com.friendlypos.login.datamanager.DataManager;
import com.friendlypos.login.fragment.LoginFragment;
import com.friendlypos.login.helper.InitHelper;
import com.friendlypos.login.interfaces.LoginListener;
import com.friendlypos.login.interfaces.ServiceCallback;
import com.friendlypos.login.modelo.AppResponse;
import com.friendlypos.login.util.SecurePreferences;

import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements InitHelper, NetworkStateChangeReceiver.InternetStateHasChange,
        LoginListener {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private NetworkStateChangeReceiver networkStateChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(networkStateChangeReceiver);
    }

    @Override
    public void init() {
        initFrontEnd();
        initBackEnd();
    }

    @Override
    public void initFrontEnd() {
        String currentUser = getCurrentUser();
        if (currentUser == null) {
            replaceCurrentFragment(LoginFragment.newInstance());
        }
        else {
            goToPrincipalScreen(currentUser, false);
        }
    }

    @Override
    public void initBackEnd() {
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
        networkStateChangeReceiver.setInternetStateHasChange(this);
    }

    public boolean isNetworkAvailable() {
        return networkStateChangeReceiver.isNetworkAvailable(this);
    }

    public boolean isValidPassword(String pass, EditText password) {
        boolean result;
        if (!TextUtils.isEmpty(pass)) {
            password.setError(null);
            result = true;
        }
        else {
            password.setError(getString(R.string.Invalid_Password));
            result = false;
        }
        return result;
    }

    public boolean isValidEmail(String email_address, EditText email) {
        boolean result;
        if (!TextUtils.isEmpty(email_address) && android.util.Patterns.EMAIL_ADDRESS.matcher(email_address).matches()) {
            email.setError(null);
            result = true;
        }
        else {
            email.setError(getString(R.string.Invalid_Email_Address));
            result = false;
        }
        return result;
    }

    @Override
    public void networkChangedState(boolean isInternetAvailable) {
        //TODO IMPLEMENT LISTENER
    }

    private void saveCurrentUser(String user, String password) {
        SecurePreferences preferences = new SecurePreferences(this, "test", "test", true);
        preferences.put("user", user);
        preferences.put("pass", password);
    }

    private String getCurrentUser() {
        SecurePreferences preferences = new SecurePreferences(this, "test", "test", true);
        if (preferences.containsKey("user")) {
            return preferences.getString("pass");
        }
        else {
            return null;
        }
    }

    @Override
    public void doLogin(String user, String password, ServiceCallback callback) {
        if (isNetworkAvailable()) {
            new DataManager(this).login(user, password, callback);
        }
        else {
            showNoInternetConnectionMessage(TAG);
        }
    }

    @Override
    public void onLoginSuccess(AppResponse response, String user, String pass) {
        if (response.getError() == null) {
            saveCurrentUser(user, pass);
            Log.d(TAG, response.toString());
            goToPrincipalScreen(user, true);
        }
        else {
            onLoginError(response.getError());
        }
    }

    @Override
    public void onLoginError(String errorMessage) {
        Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
        Log.e(TAG, errorMessage);
    }

 /*   @Override
    public void doRegister(String email, String password, ServiceCallback callback) {
        if (isNetworkAvailable()) {
            new DataManager(this).register(email, password, callback);
        }
        else {
            showNoInternetConnectionMessage(TAG);
        }
    }*/

    @Override
    public void onRegisterSuccess(AppResponse saviorResponse) {
        if (saviorResponse.getError() == null) {
            Log.d(TAG, saviorResponse.toString());
            goToLoginScreen();
        }
        else {
            onLoginError(saviorResponse.getError());
        }
    }

    @Override
    public void onRegisterError(String errorMessage) {
        Toast.makeText(this, getString(R.string.error_register), Toast.LENGTH_SHORT).show();
        Log.e(TAG, errorMessage);
    }

    private void goToPrincipalScreen(String user, boolean firstTime) {

        Toast.makeText(this, "login success", Toast.LENGTH_SHORT).show();

    }

    private void goToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}