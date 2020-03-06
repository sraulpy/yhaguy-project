package com.yhaguy.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectHttp {

    private final static OkHttpClient HTTPCLIENT = new OkHttpClient();

    public static void main(String[] args) throws Exception {
    	ConnectHttp.getCotizaciones();
    }

    public static String getCotizaciones() throws Exception {

        Request request = new Request.Builder()
                .url("https://dolar.melizeche.com/api/1.0/")
                .build();

        try (Response response = HTTPCLIENT.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }

    }	
}
