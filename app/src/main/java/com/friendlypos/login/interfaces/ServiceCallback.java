package com.friendlypos.login.interfaces;

public interface ServiceCallback<S, T, E> {

    void onSuccess(S status, T response);

    void onError(E networkError);

    void onPreExecute();
}
