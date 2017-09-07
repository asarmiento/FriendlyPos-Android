package com.friendlypos.login.interfaces;

/**
 * Created by juandiegoGL on 4/6/17.
 */
public interface ServiceCallback<S, T, E> {

    /**
     * This function will be called when the service returns a success.
     *
     * @param response the response object returned by the associated parser.
     */
    void onSuccess(S status, T response);

    /**
     * This function will be called when the service returs an error (400+, 500+ 300+)
     *
     * @param networkError the NetworkError object.
     */
    void onError(E networkError);

    /**
     * use this call back to start any kind of pre execute task for a service.
     * pre execute methods will be called.
     */
    void onPreExecute();
}
