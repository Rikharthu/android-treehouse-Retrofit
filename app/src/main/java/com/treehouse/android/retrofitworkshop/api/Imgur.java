package com.treehouse.android.retrofitworkshop.api;


import com.treehouse.android.retrofitworkshop.model.Basic;
import com.treehouse.android.retrofitworkshop.model.Image;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface Imgur {

    // ACHTUNG! It is Imgur specific!
    // base url to connect to the Imgur API
    String IMGUR_BASE_URL = "https://api.imgur.com";
    // required by Imgur API. Client that makes the request
    String IMGUR_CLIENT_ID="670b51d31d3b933";
    // imgur authorization url (авторизает к нашей апликации, перенаправит на https://treehouseworkshop:88
    // предварительно прикрепив данные авторизации к строке
    String AUTHORIZATION_URL="https://api.imgur.com/oauth2/authorize?client_id="+IMGUR_CLIENT_ID
            +"&response_type=token"; // response type. we want to receive token back
    // it's in our request intent-filter. Imgur will redirect us to that URL, which our activity will handle
    String REDIRECT_URI="https://treehouseworkshop:88";


    /** Interface that declares API endpoints, their HTTP and callback types, parameters */
    interface Auth{
        // annotate that this is gonne be and Http GET call
        // see imgur documentation https://api.imgur.com/endpoints
        @GET("3/account/{username}/images/{page}")
        Call<Basic<ArrayList<Image>>> images(@Path("username") String username,
                                              @Path("page") int page);
        // in retrofit return type is always a generic "Call" from OkHttp
        // we expect to get back Basic<ArrayList<Image>> type
        // i.e. We want to get ArrayList of Images wrapped inside Basic

        @Multipart // <-- has multiple parts
        @POST("3/upload")
        /** Uploads image to the Imgur.
         * @return the Image object that was uploaded with a link it was uploaded to */
        Call<Basic<Image>> uploadImage(@Part("image")RequestBody image);

    }

    /** Interface for another Imgur API, that allows anonymos uploading */
    interface Anon{
        @Multipart
        @POST("3/upload")
        Call<Basic<Image>> uploadImage(@Part("image")RequestBody image);

    }

}
