package com.vpipl.drdawakhana;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mukesh on 17-Nov-20.
 */

public class ForgotPassword_Activity extends AppCompatActivity {
    String TAG = "ForgotPassword_Activity";

    EditText edtxt_userid;
    Button btn_resetPassword;

    String userid;
    Activity act = ForgotPassword_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {

            edtxt_userid = (EditText) findViewById(R.id.et_userMobileNumber);
            btn_resetPassword = (Button) findViewById(R.id.btn_resetPassword);

            btn_resetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                }
            });

            LinearLayout lin_back = (LinearLayout) findViewById(R.id.lin_back);
            lin_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void ValidateData() {

        userid = edtxt_userid.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(userid)) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_required_mobile_number));
            focusView = edtxt_userid;
            cancel = true;
        } else if (userid.trim().length() != 10) {
            AppUtils.alertDialog(act, getResources().getString(R.string.error_invalid_mobile_number));
            focusView = edtxt_userid;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        executeForgetRequest();
                    }
                };
                new Handler().postDelayed(runnable, 500);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }


    private void executeForgetRequest() {
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

                        List<NameValuePair> postParameters = new ArrayList<>();
                        try {
                            postParameters.add(new BasicNameValuePair("MobileNo", userid));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToForgotPassword, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jobject = new JSONObject(resultData);

                            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                                String message = jobject.getString("Message");
                                AppUtils.alertDialogWithFinish(act, message);
                            } else {
                                AppUtils.alertDialog(act, jobject.getString("Message"));
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
            AppUtils.showExceptionDialog(act);
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
            AppUtils.showExceptionDialog(act);

        }
    }
}