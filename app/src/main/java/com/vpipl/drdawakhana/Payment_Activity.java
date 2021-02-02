package com.vpipl.drdawakhana;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.vpipl.drdawakhana.Utils.AppUtils;

import ru.nikartm.support.ImageBadgeView;


public class Payment_Activity extends AppCompatActivity {

    private static final String TAG = "Payment_Activity";
    WebView web_view;
    String url = "";

    ImageView img_menu;
    ImageView img_search;
    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {
        LinearLayout img_nav_back =  findViewById(R.id.img_nav_back);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       /* img_menu = (ImageView) findViewById(R.id.img_menu);
        img_search = (ImageView) findViewById(R.id.img_search);
        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_user = (ImageView) findViewById(R.id.img_user);

        img_user.setVisibility(View.GONE);
        img_search.setVisibility(View.GONE);
        img_cart.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHome();
            }
        });*/
    }

    private void moveToHome() {
        try {
            AppController.selectedProductsList.clear();

            Intent intent = new Intent(com.vpipl.drdawakhana.Payment_Activity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImageBadgeView mukesh_begview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();


        url = getIntent().getStringExtra("URL");

        web_view = (WebView) findViewById(R.id.web_view);

        mukesh_begview = findViewById(R.id.mukesh_begview);
        mukesh_begview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.vpipl.drdawakhana.Payment_Activity.this, com.vpipl.drdawakhana.AddCartCheckOut_Activity.class));
            }
        });
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setBuiltInZoomControls(true);
        web_view.getSettings().setDisplayZoomControls(true);
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        web_view.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        web_view.getSettings().setAllowFileAccess(true);
        web_view.getSettings().setAppCacheEnabled(true);
        web_view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.addJavascriptInterface(new MyJavaScriptInterface(this), "AndroidApplication");

        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e(TAG, url);
                if (url.contains("PaymentError.aspx")){
                                startActivity(new Intent(com.vpipl.drdawakhana.Payment_Activity.this, MainActivity.class));
                                finish();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.e(TAG, url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e(TAG, url);
            }
        });

        if (AppUtils.isNetworkAvailable(com.vpipl.drdawakhana.Payment_Activity.this)) {
            web_view.loadUrl(url);
        } else {
            AppUtils.alertDialogWithFinish(com.vpipl.drdawakhana.Payment_Activity.this, getResources().getString(R.string.txt_networkAlert));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.Payment_Activity.this);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(com.vpipl.drdawakhana.Payment_Activity.this, MainActivity.class));
        finish();
    }

    public class MyJavaScriptInterface
    {
        Context mContext;

        MyJavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast)
        {
            startActivity(new Intent(com.vpipl.drdawakhana.Payment_Activity.this, ThanksScreen_Activity.class).putExtra("ORDERNUMBER",toast));
            finish();
        }
    }
}