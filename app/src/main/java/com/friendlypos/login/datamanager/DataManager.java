package com.friendlypos.login.datamanager;

import android.content.Context;
import android.util.Log;

import com.friendlypos.R;
import com.friendlypos.login.interfaces.DataManagerInterface;
import com.friendlypos.login.interfaces.ServiceCallback;
import com.friendlypos.login.modelo.AppResponse;
import com.friendlypos.login.modelo.GetColumnPlusResponse;
import com.friendlypos.login.modelo.GetStypeResponse;

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
        DataManagerInterface apiService =
            BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
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

    public void register(String user, String password, final ServiceCallback listener) {
        DataManagerInterface apiService =
            BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<AppResponse> call = apiService.register(user, password);
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

    public void assignMacAddress(String user, String mac_address, final ServiceCallback listener) {
        DataManagerInterface apiService =
            BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<AppResponse> call = apiService.assignMacAddress(user, mac_address);
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

    public void getColumnsPlus(String user, String mac_address_list, String date, final ServiceCallback listener) {
        DataManagerInterface apiService =
            BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<GetColumnPlusResponse> call = apiService.getColumnsPlus(user, mac_address_list, date);
        Log.d(TAG, "getColumnsPlus " + call.request().toString());
        call.enqueue(new Callback<GetColumnPlusResponse>() {

            @Override
            public void onResponse(Call<GetColumnPlusResponse> call, Response<GetColumnPlusResponse> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<GetColumnPlusResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    public void getStype(String user, String mac_address_list, final ServiceCallback listener) {
        DataManagerInterface apiService =
            BaseManager.getClient(mBaseUrl).create(DataManagerInterface.class);
        Call<GetStypeResponse> call = apiService.getStype(user, mac_address_list);
        Log.d(TAG, "getStype " + call.request().toString());
        call.enqueue(new Callback<GetStypeResponse>() {

            @Override
            public void onResponse(Call<GetStypeResponse> call, Response<GetStypeResponse> response) {
                listener.onSuccess(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<GetStypeResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }
}