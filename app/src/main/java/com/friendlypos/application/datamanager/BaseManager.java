package com.friendlypos.application.datamanager;

import com.friendlypos.principal.interfaces.RequestInterface;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import io.realm.RealmObject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by juandiegoGL on 4/6/17.
 */

public class BaseManager {

    private static RequestInterface api;

    public static RequestInterface getApi() {

        if(api == null) {

            // create a converter compatible with Realm
            // GSON can parse the data.
            // Note there is a bug in GSON 2.5 that can cause it to StackOverflow when working with RealmObjects.
            // To work around this, use the ExclusionStrategy below or downgrade to 1.7.1
            // See more here: https://code.google.com/p/google-gson/issues/detail?id=440
            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .create();

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request.Builder ongoing = chain.request().newBuilder();
                            ongoing.addHeader("Accept", "application/json");
                            ongoing.addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImNmNzhkZjlkY2MwMjI2ZjdkNTkzMDNiZjY0NTM0YTYxZWJjMDE5NzkzYjdmN2JhOWE3MDBhMjVkYjc2ODkyNTAwNDU0YzViMWVhNzZlMmI1In0.eyJhdWQiOiIyIiwianRpIjoiY2Y3OGRmOWRjYzAyMjZmN2Q1OTMwM2JmNjQ1MzRhNjFlYmMwMTk3OTNiN2Y3YmE5YTcwMGEyNWRiNzY4OTI1MDA0NTRjNWIxZWE3NmUyYjUiLCJpYXQiOjE1MDUzMzIwMDcsIm5iZiI6MTUwNTMzMjAwNywiZXhwIjoxNTM2ODY4MDA3LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.Bwl6qiipcwS8tWlzAwb-pw3-tPXLr54fmv3lx7jrwnCUWzyTKO9B9cGzNRw3C9mPejqw2PnOTtsCr3fVBy_3CB_wgEqWtITXYq5iiBvylJ7SjEugkgOy9bJqKomkROtmk1zC7E88g8OvZi6trgHxluLG8pVGf4VuQr89arFnAEkYB1T-P0xPnS3idX9mni5KSydxtWCvdXJmFg61Tbgs9X_KHZ64vAZWFWFbVzmEMtdL_S0Zu-u_hoksG6TA1cCa7qnY6nq_ByGMyT1RhvlVI_AA2RhtYXs6y4EbAT6XRrj39EM7kfonI9Vs1Q7vw-fY-vFdm1BC-V5ek5n7YfslcmsWfNvEW1iLAP8ezBuHdo9DHEK5Kz9Jm2DmV90Fq2JlP2bkhf78MxlhbQjCZbiOxouvhC8DuiUGvZqKJTZn-N_tOSVZAmhdT5UuikwLvZqkAZ4puvc-oNECwxyDJrcc_Q4Ll2amV9YmeOZikxXEvwc5TtCXjnvITYlvObqfmCv6ajQlH4L4OS056tDsopDPJ570DLTWbTJNLtoukiSJ4dQ5dPj7vRhjjgU4tB4o8PA9DXx2uLoKJOFYtkbYK-xxYe5pCSc-cfa586lS85GSSXBUzuoMWlRyWCFtdxeh4TWtE-aU2zEVpZzbGjy1iGR2VrvjpNPWeVowaFi4cIbQq_w"
                            );
                            return chain.proceed(ongoing.build());
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    /*.baseUrl(appAPI.ENDPOINT)*/
                    .baseUrl("http://friendlyaccount.com")
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            // prepare call in Retrofit
            api = retrofit.create(RequestInterface.class);
        }

        return api;
    }
}