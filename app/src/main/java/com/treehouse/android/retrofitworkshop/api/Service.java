package com.treehouse.android.retrofitworkshop.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Adapter that transforms an interface into an object interface */
public class Service {

    /** This will return an instance of the Image.Auth */
    public static Imgur.Auth getAuthedApi(){


        // according to the Imgur API, every request must be authorized
        // => add header to each request vie the interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        // every single request through this client will be intercepted by this code
                        // get intercepted request
                        Request authed = chain.request()
                                .newBuilder()
                                // append authorization header with token to each request
                                .addHeader("Authorization","Bearer "+OAuthUtil.get(OAuthUtil.ACCESS_TOKEN))
                                .build();

                        // proceed to the next step of network request
                        return chain.proceed(authed);
                    }
                }).build();

        return new Retrofit.Builder()
                .baseUrl(Imgur.IMGUR_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Gson will take care in converting json to objects
                .client(client) // specify our modified http client with interceptor, which will be used for every call
                .build()
                .create(Imgur.Auth.class);
    }

}
