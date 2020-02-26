package com.example.rpac_sports_events.Twitter;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Base64;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TwitterApiClient {
    private static final String BASIC_AUTH = "dzdpbmphVERPNnY4SXBhbllhU2x4UDlaejpScURaa0Z2RngwdXZ5WlpaV1MxRHR5SHkwdWUwbTNUOUJHQ2hZODdheklib2hJclg2Ug==";
    private static final String BASE_URL = "https://api.twitter.com/";
    private static Retrofit retrofit1;
    private static Retrofit retrofit2;

    public static Retrofit getTwitterApiToken(){
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");

                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Basic "+BASIC_AUTH)
                        .addHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")
                        .post(body)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
            retrofit1 = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return retrofit1;
    }

    public static Retrofit getClient(String bearerToken){
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer "+bearerToken)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
        retrofit2 = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        return retrofit2;
    }


}
