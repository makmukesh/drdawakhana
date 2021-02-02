package com.vpipl.drdawakhana;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class ViewProfile_Activity extends AppCompatActivity {
    String TAG = "ViewProfile_Activity";

    TextView txt_userName, txt_mobileNo, txt_email, txt_address, txt_city, txt_state, txt_country, txt_postCode;

    Button btn_updateMyProfile;
    Activity act = ViewProfile_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        try {
           /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            AppUtils.setActionbarTitle(getSupportActionBar(), act);*/

            btn_updateMyProfile = (Button) findViewById(R.id.btn_updateMyProfile);

            txt_userName = (TextView) findViewById(R.id.txt_userName);
            txt_mobileNo = (TextView) findViewById(R.id.txt_mobileNo);
            txt_email = (TextView) findViewById(R.id.txt_email);
            txt_address = (TextView) findViewById(R.id.txt_address);
            txt_city = (TextView) findViewById(R.id.txt_city);
            txt_state = (TextView) findViewById(R.id.txt_state);
            txt_country = (TextView) findViewById(R.id.txt_country);
            txt_postCode = (TextView) findViewById(R.id.txt_postCode);

            btn_updateMyProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //for after 500 millisecond second
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(act, UpdateProfile_Activity.class));

                        }
                    };
                    new Handler().postDelayed(runnable, 500);
                }
            });

//            imgBtn_changePassword.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //for after 500 millisecond second
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            startActivity(new Intent(act, ChangePassword_Activity.class));
//
//                        }
//                    };
//                    new Handler().postDelayed(runnable, 500);
//                }
//            });

            if (AppUtils.isNetworkAvailable(act)) {
                if (AppUtils.showLogs) Log.v(TAG, "executeToGetProfileInfo called..");
                executeToGetProfileInfo();
            } else {
                AppUtils.showExceptionDialog(act);
            }

            /*LinearLayout lin_back = (LinearLayout)findViewById(R.id.lin_back);
            lin_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeToGetProfileInfo() {
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
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToViewProfile, TAG);
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
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonArrayData.length() != 0) {
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                }
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


    private void getProfileInfo(JSONArray jsonArray) {
        try {


            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_FORM_NUMBER, "" + jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_ID, "" + jsonArray.getJSONObject(0).getString("ID"))
                    .putString(SPUtils.USER_Name, "" + (jsonArray.getJSONObject(0).getString("MemFirstName")))
                    .putString(SPUtils.USER_LAST_NAME, "" + (jsonArray.getJSONObject(0).getString("MemLastName")))
                    .putString(SPUtils.USER_MobileNo, jsonArray.getJSONObject(0).getString("Mobl"))
                    .putString(SPUtils.USER_Email, "" + jsonArray.getJSONObject(0).getString("EMail"))
                    .putString(SPUtils.USER_Address, "" + (jsonArray.getJSONObject(0).getString("Address1")))
                    .putString(SPUtils.USER_PinCode, "" + jsonArray.getJSONObject(0).getString("PinCode"))
                    .putString(SPUtils.USER_City, (jsonArray.getJSONObject(0).getString("City")))
                    .putString(SPUtils.USER_State, (jsonArray.getJSONObject(0).getString("StateCode")))
                    .putString(SPUtils.USER_Country, (jsonArray.getJSONObject(0).getString("CountryName")))
                    .commit();

            setProfileDetails();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void setProfileDetails() {
        try {
            txt_userName.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
            txt_mobileNo.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, ""));
            txt_email.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Email, ""));
            txt_address.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Address, ""));
            txt_city.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_City, ""));
            txt_state.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_State, ""));
            String asd = AppController.getSpUserInfo().getString(SPUtils.USER_Country, "");
            txt_country.setText("India");
            txt_postCode.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_PinCode, ""));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                if (AppUtils.showLogs) Log.v(TAG, "onRestart executeToGetProfileInfo called..");
                executeToGetProfileInfo();
            } else {
                AppUtils.showExceptionDialog(act);
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