package com.vpipl.drdawakhana;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
import com.vpipl.drdawakhana.Utils.PrefManager;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class Splash_Activity extends Activity {

    String TAG = "Splash_Activity";

    DatabaseHandler db;
    String currentVersion, latestVersion;
    Activity act = Splash_Activity.this;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        try {
            Log.v(TAG, "GCM_DEVICE_ID:" + AppController.getSpIsInstall().getString(SPUtils.GCM_DEVICE_ID, ""));
            Log.v("DeviceID", "" + SPUtils.GCM_DEVICE_ID);

            db = new DatabaseHandler(this);

            getCurrentVersionnew();
            //  executeToGetAllPrdocuctPacking();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("These Permissions are required for use this Application")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public void showUpdateDialog(String Msg) {
        try {
            final Dialog dialog = AppUtils.createDialog(act, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Update Now");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        dialog.dismiss();
                        finish();

                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setVisibility(GONE);
            txt_cancel.setText("Update Later");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void moveNextScreen() {
        try {
            if (AppUtils.showLogs) Log.v(TAG, "called.....");
            prefManager = new PrefManager(this);
            if (!prefManager.isFirstTimeLaunch()) {
                try {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        startActivity(new Intent(act, MainActivity.class));
                    } else {
                        startActivity(new Intent(act, Login_Activity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtils.showExceptionDialog(act);
                }
            } else {
                startSplash(new Intent(act, WelcomeActivity.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    ////  update version

    private void getCurrentVersionnew() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersionnew().execute();

    }

    private class GetLatestVersionnew extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName()).get();
                latestVersion = doc.getElementsByClass("htlgb").get(6).text();
            } catch (Exception e) {
                e.printStackTrace();
                latestVersion = currentVersion;
            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (!isFinishing()) {
                        showUpdateDialog();
                    }
                } else {
                    String regId = FirebaseInstanceId.getInstance().getToken();
                    Log.e("Token", "" + regId);
                    if (AppUtils.isNetworkAvailable(act)) {
                        checkShippingChargeFromServer();
                    } else {
                        AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
                    }
                }
            } else
                //   background.start();
                super.onPostExecute(jsonObject);
        }
    }


    private void showUpdateDialog() {
        final Dialog dialog = new Dialog(act, R.style.ThemeDialogCustom);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_update);

        TextView dialog4all_txt = dialog.findViewById(R.id.tvDescription);
        Button btnNone = dialog.findViewById(R.id.btnNone);
        ImageView iv_update_image = dialog.findViewById(R.id.iv_update_image);
        dialog4all_txt.setText("An Update is available,Please Update App from Play Store.");


        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private void checkShippingChargeFromServer() {

        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToShippingChargesDetails, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                String Amount = jsonArrayData.getJSONObject(0).getString("Amount");
                                String SheepingCharge = jsonArrayData.getJSONObject(0).getString("SheepingCharge");
                                AppController.getShipping_Amt().edit().putString(SPUtils.USER_Shipping_Amount, "" + Amount).commit();
                                AppController.getShipping_Charge().edit().putString(SPUtils.USER_Shipping_Charge, "" + SheepingCharge).commit();
                            }
                            moveNextScreen();
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                            moveNextScreen();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }
}