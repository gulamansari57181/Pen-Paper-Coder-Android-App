package com.example.penpapercoder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIClient {

    private static final String BASE_URL = "https://api.jdoodle.com/v1/";
    static final String API_ID = "a822172d11e300b512cd3138d9542e8c";
    static final String API_SECRET = "880e5ea8c48adc837be2cae9d9171260be5b0cba6cbe378624dc0d072d31a928";
    static final String LANGUAGE = "python3";
    static final String VERSION_INDEX = "3";

    private static APIClient mInstance;
    private Retrofit retrofit;

    private APIClient(OkHttpClient.Builder builder) {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())
                .build();
    }

    static synchronized APIClient getInstance() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);

        if (mInstance == null) {
            mInstance = new APIClient(builder);
        }
        return mInstance;
    }

    APIService getAPI() {
        return retrofit.create(APIService.class);
    }
}

