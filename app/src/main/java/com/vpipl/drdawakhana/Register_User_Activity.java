package com.vpipl.drdawakhana;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.drdawakhana.SMS.MySMSBroadcastReceiver;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SmsListener;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Register_User_Activity extends AppCompatActivity {
    String TAG = "Registration_Activity";
    EditText et_userName, et_lastName, et_userMobileNumber, et_email, et_password;
    Button btn_submit;
    LinearLayout coordinatorLayout;

    LinearLayout layout_OTP;
    private EditText edtxt_otp;
    TextView textView5, textView6, textView4;
    CountDownTimer countDownTimer;
    private String OTP;
    String count_timer_sts = "N";

    Button button_sendotp;
    Activity act = Register_User_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vipin_register);

        try {
            if (AppUtils.showLogs) Log.v(TAG, "called.....");

            coordinatorLayout = (LinearLayout) findViewById(R.id.coordinatorLayout);
            et_userName = (EditText) findViewById(R.id.et_userName);
            et_lastName = (EditText) findViewById(R.id.et_lastName);
            et_userMobileNumber = (EditText) findViewById(R.id.et_userMobileNumber);
            et_email = (EditText) findViewById(R.id.et_email);
            et_password = (EditText) findViewById(R.id.et_password);

            edtxt_otp = (EditText) findViewById(R.id.edtxt_otp);

            layout_OTP = (LinearLayout) findViewById(R.id.layout_OTP);
            textView6 = (TextView) findViewById(R.id.textView6);
            textView5 = (TextView) findViewById(R.id.textView5);
            textView4 = (TextView) findViewById(R.id.textView4);

            button_sendotp = (Button) findViewById(R.id.btn_next);

            btn_submit = (Button) findViewById(R.id.btn_submit);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                }
            });

            button_sendotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppUtils.hideKeyboardOnClick(act, view);
                    if (AppUtils.isNetworkAvailable(act)) {
                        executeRegistrationClick();
                    } else {
                        AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
                    }
                }
            });


            edtxt_otp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 5) {
                        btn_submit.setVisibility(View.VISIBLE);
                        textView6.setVisibility(View.GONE);
                        textView5.setVisibility(View.GONE);
                    }
                }
            });

           /* SmsReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            edtxt_otp.setText(messageText);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/
            SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

            Task<Void> task = client.startSmsRetriever();

            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //         Toast.makeText(act, "Success", Toast.LENGTH_SHORT).show();
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //            Toast.makeText(act, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

            MySMSBroadcastReceiver.bindListener(new SmsListener() {
                @Override
                public void messageReceived(String messageText) {
                    try {
                        if (messageText.length() == 6) {
                            edtxt_otp.setText(messageText);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            countDownTimer = new CountDownTimer(90000, 1000) {
                public void onTick(long millisUntilFinished) {
                    textView4.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    OTP = "";
                    textView4.setVisibility(View.GONE);
                    textView6.setText("OTP has expired.");
                    textView6.setVisibility(View.VISIBLE);
                    textView5.setVisibility(View.VISIBLE);
                    count_timer_sts = "Y";
                }
            };

            textView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    edtxt_otp.setText("");
                    edtxt_otp.setError(null);
                    executemethodtoSendOTP(et_userMobileNumber.getText().toString().trim());
                    //  RequestOTPByApi();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }

    private void executeRegistrationClick() {
        try {
            if (et_userName.getText().toString().trim().isEmpty()) {
                et_userName.setError(getResources().getString(R.string.error_field_required));
                et_userName.requestFocus();
            } else if (!AppUtils.isValidMobileno(et_userMobileNumber.getText().toString())) {
                AppUtils.dismissProgressDialog();
                et_userMobileNumber.setError("Please Enter Valid Mobile No");
                et_userMobileNumber.requestFocus();
            } else if (!TextUtils.isEmpty(et_email.getText().toString().trim()) && !et_email.getText().toString().matches(AppUtils.mEmaiPattern)) {
                et_email.setError(getResources().getString(R.string.error_invalid_email));
                et_email.requestFocus();
            } else if (et_password.getText().toString().trim().isEmpty()) {
                et_password.setError("Please Enter Password");
                et_password.requestFocus();
            } else {
                 executemethodtoSendRegistrationOTP(et_userMobileNumber.getText().toString().trim());

                /*OTP = "123456";
                Log.e("Register OTP", "" + OTP);

                et_userMobileNumber.setEnabled(false);

                edtxt_otp.setEnabled(true);
                countDownTimer.start();
                textView4.setVisibility(View.VISIBLE);
                button_sendotp.setVisibility(View.GONE);
                layout_OTP.setVisibility(View.VISIBLE);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }

    private void getSellerRegistrationRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {

                List<NameValuePair> postParameters = new ArrayList<>();
                /*FirstName:LastName:Password:MobileNo:Emailid:DeviceID:*/
                postParameters.add(new BasicNameValuePair("FirstName", et_userName.getText().toString()));
                postParameters.add(new BasicNameValuePair("LastName", et_lastName.getText().toString()));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + et_userMobileNumber.getText().toString()));
                postParameters.add(new BasicNameValuePair("Password", et_password.getText().toString()));
                postParameters.add(new BasicNameValuePair("Emailid", et_email.getText().toString()));
                postParameters.add(new BasicNameValuePair("FirebaseID", FirebaseInstanceId.getInstance().getToken()));
                postParameters.add(new BasicNameValuePair("DeviceID", "00000000000"));

                executeMemberRegistrationRequest(postParameters);
            } else {
                AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeMemberRegistrationRequest(final List<NameValuePair> postParameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoRegistration, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                showLoginDialog("Registration is Completed, Please Login to Continue");
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


    public void showLoginDialog(String Msg) {
        try {
            final Dialog dialog = AppUtils.createDialog(act, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Login");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Intent intent = new Intent(act, Login_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

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
        if (countDownTimer != null)
            countDownTimer.cancel();
        try {
            AppUtils.dismissProgressDialog();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }

    public void ValidateData() {

        String Otp = edtxt_otp.getText().toString();

        if (TextUtils.isEmpty(Otp)) {
            edtxt_otp.setError("OTP is Required");
            edtxt_otp.requestFocus();
        } else if (!OTP.equalsIgnoreCase(Otp)) {
            if (count_timer_sts.equalsIgnoreCase("N")) {
                Toast.makeText(act, "Invalid OTP", Toast.LENGTH_LONG).show();
                edtxt_otp.setError("Invalid OTP");
                edtxt_otp.requestFocus();
            } else {
                textView5.setVisibility(View.VISIBLE);
            }
        } else {
            if (AppUtils.isNetworkAvailable(this)) {
                getSellerRegistrationRequest();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }


    private void executemethodtoSendRegistrationOTP(final String mobile) {
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

                            postParameters.add(new BasicNameValuePair("Mobile", mobile.trim()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoSendRegistrationOTP, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject object = new JSONObject(resultData);
                            JSONArray jsonArrayData = object.getJSONArray("Data");


                            if (object.getString("Status").equalsIgnoreCase("True")) {
                                et_userMobileNumber.setEnabled(false);

                                edtxt_otp.setEnabled(true);
                                OTP = jsonArrayData.getJSONObject(0).getString("OTP");
                                countDownTimer.start();
                                textView4.setVisibility(View.VISIBLE);
                                button_sendotp.setVisibility(View.GONE);
                                layout_OTP.setVisibility(View.VISIBLE);
                            } else {
                                et_userMobileNumber.setEnabled(true);

                                AppUtils.alertDialog(act, object.getString("Message"));

                                edtxt_otp.setEnabled(false);
                                OTP = "";
                                countDownTimer.cancel();
                                textView4.setVisibility(View.GONE);
                                button_sendotp.setVisibility(View.VISIBLE);
                                layout_OTP.setVisibility(View.GONE);
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

    private void executemethodtoSendOTP(final String mobile) {
        try {
            if (AppUtils.isNetworkAvailable(this)) {
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
                            postParameters.add(new BasicNameValuePair("Mobile", mobile));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoSendRegistrationOTP, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject object = new JSONObject(resultData);
                            JSONArray jsonArrayData = object.getJSONArray("Data");

//                            if (jsonArrayData.length() > 0)
//                            {
                            if (object.getString("Status").equalsIgnoreCase("True")) {
                                OTP = jsonArrayData.getJSONObject(0).getString("OTP");

                                textView6.setText("New OTP Sent Successfully");
                                textView6.setVisibility(View.VISIBLE);
                                textView5.setVisibility(View.GONE);
                                if (countDownTimer != null) {
                                    countDownTimer.start();
                                    textView4.setVisibility(View.VISIBLE);
                                }

                            } else {
                                AppUtils.alertDialog(act, object.getString("Message"));
                            }
//                            } else {
//                                AppUtils.alertDialog(act, object.getString("Message"));
//                            }

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

    private void RequestOTPByApi() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        final String str2 = String.format("%06d", number);
        System.out.println(str2);
        String msg = "<%23> " + str2 + " is your OTP for logging into the " + getResources().getString(R.string.app_name) + " app. For security reasons. " +
                "do not share the OTP. Cj0UEzy6h4o"; //+
        // "This verification is important for safety of your account and must be done before you proceed with wallet transfer.";
        String mobileno = "" + et_userMobileNumber.getText().toString();
        String SMSURL = AppUtils.smsAPIURL();
        SMSURL = SMSURL.replaceAll("<rqmbl>", mobileno);
        SMSURL = SMSURL.replaceAll("<rqsms>", msg);

        String URL = SMSURL;
        URL = URL.replace(" ", "%20");
        System.out.println(URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("otpres", "res" + response);
                try {
                    OTP = str2;
                    Log.e("Register OTP", "" + OTP);

                    et_userMobileNumber.setEnabled(false);

                    edtxt_otp.setEnabled(true);
                    countDownTimer.start();
                    textView4.setVisibility(View.VISIBLE);
                    button_sendotp.setVisibility(View.GONE);
                    layout_OTP.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("otperror", "" + error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}