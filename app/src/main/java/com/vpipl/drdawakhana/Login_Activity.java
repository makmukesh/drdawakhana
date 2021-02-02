package com.vpipl.drdawakhana;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mukesh on 17-Nov-20.
 */
public class Login_Activity extends AppCompatActivity {
    String TAG = "LoginActivity";

    EditText et_userMobileNumber, et_userPassword;
    TextView btn_loginJoinNow;
    Button btn_submit;
    TextView txt_eye_close, txt_eye_open;
    Activity act = Login_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vipin_login);

        try {
            et_userPassword = (EditText) findViewById(R.id.et_userPassword);
            et_userMobileNumber = (EditText) findViewById(R.id.et_userMobileNumber);
            btn_loginJoinNow = (TextView) findViewById(R.id.btn_loginJoinNow);
            txt_eye_close = findViewById(R.id.txt_eye_close);
            txt_eye_open = findViewById(R.id.txt_eye_open);
            btn_submit = (Button) findViewById(R.id.btn_login);

            txt_eye_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide password
                    et_userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.VISIBLE);
                    txt_eye_open.setVisibility(View.GONE);
                }
            });
            txt_eye_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show password
                    et_userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.GONE);
                    txt_eye_open.setVisibility(View.VISIBLE);
                }
            });

            btn_loginJoinNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(act, Register_User_Activity.class);
                    startActivity(intent);

                }
            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppUtils.hideKeyboardOnClick(act, v);

                    if (et_userMobileNumber.getText().toString().isEmpty()) {
                        et_userMobileNumber.setError(getResources().getString(R.string.error_required_mobile_number));
                        et_userMobileNumber.requestFocus();
                    } else if (!AppUtils.isValidMobileno(et_userMobileNumber.getText().toString())) {
                        AppUtils.dismissProgressDialog();
                        et_userMobileNumber.setError("Please Enter Valid Mobile No");
                        et_userMobileNumber.requestFocus();
                    } else if (et_userPassword.getText().toString().trim().isEmpty()) {
                        et_userPassword.setError("Please Enter Password.");
                        et_userPassword.requestFocus();
                    } else {
                        if (AppUtils.isNetworkAvailable(act)) {
                            createLoginRequest(et_userMobileNumber.getText().toString().trim(), et_userPassword.getText().toString().trim());
                        } else {
                            AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
                        }
                    }

                }
            });

            findViewById(R.id.txt_loginForgotPassword).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act, ForgotPassword_Activity.class);
                    startActivity(intent);

                }
            });

            et_userMobileNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (AppUtils.isValidMobileno(et_userMobileNumber.getText().toString())) {
                        findViewById(R.id.india).setBackground(getResources().getDrawable(R.drawable.ic_circle_with_check_symbol));
                    } else if (et_userMobileNumber.getText().toString().length() == 10) {
                        findViewById(R.id.india).setBackground(getResources().getDrawable(R.drawable.ic_circle_with_check_symboldeactive));
                        et_userMobileNumber.setError("Please Enter Valid Mobile No");
                    } else {
                        findViewById(R.id.india).setBackground(getResources().getDrawable(R.drawable.ic_circle_with_check_symboldeactive));
                    }
                }
            });

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void createVerifactionRequest(final String mobile, final String Password) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();

                           /* TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
                            String deviceId = telephonyManager.getDeviceId() + "";
                            String simMobileNo = telephonyManager.getLine1Number() + "";
                            String operatorName = telephonyManager.getSimOperatorName() + "";
                            String country = telephonyManager.getSimCountryIso() + "";
                            String serialNumber = telephonyManager.getSimSerialNumber() + "";
                            String subscriberId = telephonyManager.getSubscriberId() + "";
                            String isWifiOrMobile = AppUtils.isNetworkWifiMobileData(act) + "";

                            if (simMobileNo.equalsIgnoreCase("null") || simMobileNo.equalsIgnoreCase("")) {
                                simMobileNo = "0";
                            }

                            postParameters.add(new BasicNameValuePair("MobileNo1", "" + mobile));
                            postParameters.add(new BasicNameValuePair("DeviceMobileNo", "" + simMobileNo));
                            postParameters.add(new BasicNameValuePair("IMEINo", "" + deviceId));
                            postParameters.add(new BasicNameValuePair("ConnectThru", isWifiOrMobile + ""));
                            postParameters.add(new BasicNameValuePair("OperatorName", "" + operatorName));
                            postParameters.add(new BasicNameValuePair("OperatorCountry", "" + country));
                            postParameters.add(new BasicNameValuePair("SIMSerialNo", "" + serialNumber));
                            postParameters.add(new BasicNameValuePair("SIMSubscriberId", "" + subscriberId));
                            postParameters.add(new BasicNameValuePair("SIMIMEINo", ""));
                            postParameters.add(new BasicNameValuePair("HType", "INSTALL"));
                            postParameters.add(new BasicNameValuePair("Version", "" + AppUtils.getAppVersionName(act)));
                            postParameters.add(new BasicNameValuePair("DeviceModel", "" + AppController.getSpIsInstall().getString(SPUtils.IS_INSTALL_DeviceModel, "")));
                            postParameters.add(new BasicNameValuePair("DeviceName", "" + AppController.getSpIsInstall().getString(SPUtils.IS_INSTALL_DeviceName, "")));
*/
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodVerificationMobileNoJson, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            final JSONObject jsonArray = new JSONObject(resultData);
                            if (jsonArray.length() != 0) {

                                if (jsonArray.getString("Status").equalsIgnoreCase("true")) {
                                    createLoginRequest(mobile, Password);
                                } else {
                                    AppUtils.alertDialog(act, jsonArray.getString("Message"));
                                }
                            } else {
                                AppUtils.showExceptionDialog(act);
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

    private void createLoginRequest(final String mobile, final String Password) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNoOrEmailID", "" + mobile));
                            postParameters.add(new BasicNameValuePair("Password", "" + Password));
                            //  postParameters.add(new BasicNameValuePair("GCMDeviceId", "" + FirebaseInstanceId.getInstance().getToken().toString()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoLogin, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            final JSONObject jsonArray = new JSONObject(resultData);

                            if (jsonArray.getString("Status").equalsIgnoreCase("true")) {
                                saveLoginUserInfo(jsonArray.getJSONArray("Data"));
                            } else {
                                AppUtils.alertDialog(act, jsonArray.getString("Message"));
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

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            AppUtils.dismissProgressDialog();

            AppController.getSpUserInfo().edit().clear().commit();
            String asf = "India";
            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_ID, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_Name, jsonArray.getJSONObject(0).getString("MemFirstName"))
                    .putString(SPUtils.USER_Last_Name, jsonArray.getJSONObject(0).getString("MemLastName"))
                    .putString(SPUtils.USER_Address, jsonArray.getJSONObject(0).getString("Address1"))
                    .putString(SPUtils.USER_PinCode, jsonArray.getJSONObject(0).getString("PinCode"))
                    .putString(SPUtils.USER_City, jsonArray.getJSONObject(0).getString("City"))
                    .putString(SPUtils.USER_State, jsonArray.getJSONObject(0).getString("StateCode"))
                    /*.putString(SPUtils.USER_Country, jsonArray.getJSONObject(0).getString("Country"))*/

                    .putString(SPUtils.USER_Country, asf)
                    .putString(SPUtils.USER_MobileNo, jsonArray.getJSONObject(0).getString("Mobl"))
                    .putString(SPUtils.USER_Email, jsonArray.getJSONObject(0).getString("EMail"))
                    .putString(SPUtils.USER_Passw, jsonArray.getJSONObject(0).getString("Passw"))
                    .putString(SPUtils.USER_Doj, jsonArray.getJSONObject(0).getString("Doj"))
                    .putString(SPUtils.USER_MembershipStatus, jsonArray.getJSONObject(0).getString("status"))
                    .commit();

            //for Login Successfully.
            AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

            finish();
            startActivity(new Intent(act, MainActivity.class));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
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
        }
    }
}
