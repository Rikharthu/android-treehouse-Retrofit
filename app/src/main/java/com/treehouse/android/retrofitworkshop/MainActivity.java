package com.treehouse.android.retrofitworkshop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.treehouse.android.retrofitworkshop.api.Imgur;
import com.treehouse.android.retrofitworkshop.api.OAuthUtil;
import com.treehouse.android.retrofitworkshop.api.Service;
import com.treehouse.android.retrofitworkshop.model.Basic;
import com.treehouse.android.retrofitworkshop.model.Image;
import com.treehouse.android.retrofitworkshop.view.ImageAdapter;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG=MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.btn_sign_in)
    View signInBtn;
    @Bind(R.id.btn_upload_anon)
    View uploadAnon;

    @Bind(R.id.account_images_container)
    View accountImagesContainer;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.btn_upload)
    View upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        signInBtn.setOnClickListener(this);
        uploadAnon.setOnClickListener(this);
        upload.setOnClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ImageAdapter(this));

        OAuthUtil.initSharedPref(this);

        if (OAuthUtil.isAuthorized()) {
            // TODO set title
            showAccountImages();
        } else {
            toolbar.setTitle("Login");
            showLoginOrAnon();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* MainActivity зарегистрирована как обработчик ссылки https//treehouseworkshop:88 в манифесте
         При авторизации по ссылке регистрации, Imgur API перенаправит нас по ней (мы так настроили)
         Активность обработает этот интент, и вызовется onResume */

        // Получаем ссылку, по которой нас перенаправил Imgur и прикрепил данные авторизации
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Imgur.REDIRECT_URI)) {
            // Достаём данные из ссылки

            Log.d(TAG,uri.toString());
            /* https://treehouseworkshop:88/
                #access_token=36ec4adbad5bfd6781d388616c826bac586ed8c2
                &expires_in=2419200
                &token_type=bearer
                &refresh_token=2afde5794d42f11ade45921bd261d1b93fb9576b
                &account_username=Rikarthu
                &account_id=40052390 */

            // create a temp Uri to make it easier to pull out the data we need
            Uri temp = Uri.parse("https://treehouseworkshop?" + uri.getFragment().trim()); // просто заменит # на ?

            OAuthUtil.set(OAuthUtil.ACCESS_TOKEN, temp.getQueryParameter(OAuthUtil.ACCESS_TOKEN));
            OAuthUtil.set(OAuthUtil.EXPIRES_IN, System.currentTimeMillis() + (Long.parseLong(temp.getQueryParameter(OAuthUtil.EXPIRES_IN)) * 1000));
            OAuthUtil.set(OAuthUtil.TOKEN_TYPE, temp.getQueryParameter(OAuthUtil.TOKEN_TYPE));
            OAuthUtil.set(OAuthUtil.REFRESH_TOKEN, temp.getQueryParameter(OAuthUtil.REFRESH_TOKEN));
            OAuthUtil.set(OAuthUtil.ACCOUNT_USERNAME, temp.getQueryParameter(OAuthUtil.ACCOUNT_USERNAME));

            if (OAuthUtil.isAuthorized()) {
                toolbar.setTitle(OAuthUtil.get(OAuthUtil.ACCOUNT_USERNAME));
                showAccountImages();
            } else {
                // TODO later
            }
        }
    }

    private void showLoginOrAnon() {
        accountImagesContainer.setVisibility(View.GONE);
        signInBtn.setVisibility(View.VISIBLE);
        uploadAnon.setVisibility(View.VISIBLE);
    }

    private void showAccountImages() {
        fetchAccountsImage();

        accountImagesContainer.setVisibility(View.VISIBLE);
        signInBtn.setVisibility(View.GONE);
        uploadAnon.setVisibility(View.GONE);
    }

    private void fetchAccountsImage() {
        Snackbar.make(upload,"Getting Images for Account",Snackbar.LENGTH_SHORT);

        Service.getAuthedApi()
                // Call
                .images(OAuthUtil.get(OAuthUtil.ACCOUNT_USERNAME),0)
                // Enqueue Call object
                .enqueue(new Callback<Basic<ArrayList<Image>>>() {
                    @Override
                    public void onResponse(Call<Basic<ArrayList<Image>>> call, Response<Basic<ArrayList<Image>>> response) {
                        // In Retrofit, onResponse happens on the UI thread
                        // HTTP call succeeded and returned back with data
                        // at this point Json data has been converted by Gson into an object

                        // TODO see assets/sample_response.txt to see, how response looks like

                        if(response.code()== HttpURLConnection.HTTP_OK){ // 200
                            // replace images in a RecyclerView
                            ((ImageAdapter)recyclerView.getAdapter()).swap(response.body().data);
                        }else{
                            Snackbar.make(upload,"Failed :(",Snackbar.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onFailure(Call<Basic<ArrayList<Image>>> call, Throwable t) {
                        Snackbar.make(upload,"Failed :c",Snackbar.LENGTH_SHORT);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_anon:
                // TODO
                break;
            case R.id.btn_upload:
                // TODO
                break;
            case R.id.btn_sign_in:
                // TODO start login process
                // open Authorization URI in a browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Imgur.AUTHORIZATION_URL)));
                break;
        }
    }


}
