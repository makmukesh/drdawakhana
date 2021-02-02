package com.vpipl.drdawakhana;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mukesh on 17-Nov-20.
 */
public class ChangePassword_Activity extends AppCompatActivity {
    String TAG = "ChangePassword_Activity";

    Button btn_updateChange;
    EditText et_oldLoginPassword, et_newLoginPassword, et_confirmLoginPassword;
    LinearLayout coordinatorLayout;
    Activity act = ChangePassword_Activity.this;

    String old_pass;
    String new_pass;
    String confirm_pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            if (AppUtils.showLogs) Log.v(TAG, "called.....");

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
            TextView heading = findViewById(R.id.heading);

            heading.setText("Change Password");
            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            coordinatorLayout = (LinearLayout) findViewById(R.id.coordinatorLayout);

            btn_updateChange = (Button) findViewById(R.id.btn_updateChange);

            et_oldLoginPassword = (EditText) findViewById(R.id.et_oldLoginPassword);
            et_newLoginPassword = (EditText) findViewById(R.id.et_newLoginPassword);
            et_confirmLoginPassword = (EditText) findViewById(R.id.et_confirmLoginPassword);

            btn_updateChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);

                    ValidateData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }


    public void ValidateData() {
        old_pass = et_oldLoginPassword.getText().toString();
        new_pass = et_newLoginPassword.getText().toString();
        confirm_pass = et_confirmLoginPassword.getText().toString();

        String str_old_pwd = AppController.getSpUserInfo().getString(SPUtils.USER_Passw, "");

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(old_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_old_password));
            focusView = et_oldLoginPassword;
            cancel = true;
        } else if (!old_pass.equalsIgnoreCase(str_old_pwd)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_not_old_password));
            focusView = et_oldLoginPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(new_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_new_password));
            focusView = et_newLoginPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(confirm_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_confirm_password));
            focusView = et_confirmLoginPassword;
            cancel = true;
        } else if (!new_pass.equalsIgnoreCase(confirm_pass)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.password_mismatch));
            focusView = et_confirmLoginPassword;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        createChangePasswordRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void createChangePasswordRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();


            postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("OldPassword", old_pass));
            postParameters.add(new BasicNameValuePair("NewPassword", new_pass));
            postParameters.add(new BasicNameValuePair("ConfirmPassword", confirm_pass));


            executeChangePassword(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeChangePassword(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                        String response = null;
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToChangePassword, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                final Dialog dialog = AppUtils.createDialog(act, true);
                                TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
                                dialog4all_txt.setText(jsonObject.getString("Message"));
                                dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();

                                        AppController.getSpUserInfo().edit().clear().commit();
                                        AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, false).commit();

                                        AppController.productList.clear();
                                        AppController.selectedProductList.clear();
                                        AppController.selectedWishList.clear();
                                        AppController.selectedProductsList.clear();

                                        AppController.countryList.clear();
                                        AppController.categoryList.clear();
                                        AppController.homeSliderList.clear();

                                        Intent intent = new Intent(act, Login_Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                        finish();
                                    }
                                });
                                dialog.show();

                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }
}
