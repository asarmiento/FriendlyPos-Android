package com.friendlypos.login.datamanager;

import android.content.Context;
import android.util.Log;

import com.friendlypos.R;
import com.friendlypos.application.datamanager.BaseManager;
import com.friendlypos.login.interfaces.DataManagerInterface;
import com.friendlypos.login.interfaces.ServiceCallback;
import com.friendlypos.login.modelo.AppResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {

    public static final String TAG = DataManager.class.getSimpleName();

    private String mBaseUrl;

    public DataManager(Context context) {
        mBaseUrl = context.getString(R.string.base_url);
    }

    public void login(String user, String password, final ServiceCallback listener) {
        DataManagerInterface apiService = BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<AppResponse> call = apiService.login(user, password);
        Log.d(TAG, call.request().toString());
        Log.d(TAG, call.request().body().toString());
        call.enqueue(new Callback<AppResponse>() {

            @Override
            public void onResponse(Call<AppResponse> call, Response<AppResponse> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<AppResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}