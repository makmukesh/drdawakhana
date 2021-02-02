package com.vpipl.drdawakhana;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;


public class AddDeliveryAddress_Activity extends AppCompatActivity {
    String TAG = "AddDeliveryAddress_Activity";

    Button btn_save, btn_cancel;
    TextInputEditText et_firstName, et_lastName, et_address1, et_address2, et_city, et_state, et_pinCode, et_mobileNumber;
    String state[];

    String selectedStateId = "0";

    ImageView img_menu;
    ImageView img_search;
    ImageView img_cart;
    ImageView img_user;
    public ImageBadgeView mukesh_begview;

    public void SetupToolbar() {
        LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
        TextView heading = findViewById(R.id.heading);
        heading.setText("Add Delivery Address");
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

      /*  img_menu = (ImageView) findViewById(R.id.img_menu);
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
                finish();
            }
        });*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adddeliveryaddress_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            executeStateRequest();

            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            btn_save = (Button) findViewById(R.id.btn_save);

            et_firstName = (TextInputEditText) findViewById(R.id.et_firstName);
            et_lastName = (TextInputEditText) findViewById(R.id.et_lastName);

            et_address1 = (TextInputEditText) findViewById(R.id.et_address1);
            et_address2 = (TextInputEditText) findViewById(R.id.et_address2);
            et_city = (TextInputEditText) findViewById(R.id.et_city);
            et_state = (TextInputEditText) findViewById(R.id.et_state);
            et_pinCode = (TextInputEditText) findViewById(R.id.et_pinCode);
            et_mobileNumber = (TextInputEditText) findViewById(R.id.et_mobileNumber);

            mukesh_begview = findViewById(R.id.mukesh_begview);
            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(AddDeliveryAddress_Activity.this, AddCartCheckOut_Activity.class));
                }
            });
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());


            et_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.stateList.size() != 0) {
                        showStateDialog();
                    }
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(AddDeliveryAddress_Activity.this, view);
                    validateAddressRequest();
                }
            });

            /*et_pinCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (s.length() == 6) {
                        executePinCodeRequest(s.toString());
                        AppUtils.hideKeyboardOnClick(AddDeliveryAddress_Activity.this, et_pinCode);
                    }
                }
            });


            et_pinCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||  actionId == EditorInfo.IME_NULL || actionId== 125412)
                    {
                        AppUtils.hideKeyboardOnClick(AddDeliveryAddress_Activity.this, view);
                        if (et_pinCode.getText().toString().length() == 6) {
                            executePinCodeRequest(et_pinCode.getText().toString());
                            AppUtils.hideKeyboardOnClick(AddDeliveryAddress_Activity.this, et_pinCode);

                        }
                        return true;
                    }
                    return false;
                }
            });
*/
            setAddressValue();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    void setAddressValue() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            boolean containsKey = getIntent().getExtras().containsKey("position");

            if (containsKey) {
                int Position = getIntent().getIntExtra("position", 0);
                et_firstName.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("MemFirstName"));
                et_lastName.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("MemLastName"));
                et_address1.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("Address1"));
                et_address2.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("Address2"));
                et_city.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("City"));
                et_state.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("StateName"));
                et_pinCode.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("PinCode"));
                et_mobileNumber.setText(MyAddressList_Activity.deliveryAddressList.get(Position).get("Mobl"));
            } else {
                et_firstName.setText(AppController.getSpUserInfo().getString(SPUtils.USER_FIRST_NAME, ""));
                et_lastName.setText(AppController.getSpUserInfo().getString(SPUtils.USER_LAST_NAME, ""));
                et_address1.setText(AppController.getSpUserInfo().getString(SPUtils.USER_ADDRESS, ""));
                et_address2.setText(AppController.getSpUserInfo().getString(SPUtils.USER_ADDRESS2, ""));
                et_city.setText(AppController.getSpUserInfo().getString(SPUtils.USER_CITY, ""));

                et_state.setText(AppController.getSpUserInfo().getString(SPUtils.USER_STATE, ""));
                et_pinCode.setText(AppController.getSpUserInfo().getString(SPUtils.USER_PINCODE, ""));
                et_mobileNumber.setText(AppController.getSpUserInfo().getString(SPUtils.USER_MOBILE_NO, ""));
            }
        }
    }

    private void validateAddressRequest() {
        try {
            if (et_firstName.getText().toString().isEmpty()) {
                et_firstName.setError("Please Enter First Name");
                et_firstName.requestFocus();
            } else if (et_lastName.getText().toString().isEmpty()) {
                et_lastName.setError("Please Enter Last Name");
                et_lastName.requestFocus();
            } else if (et_address1.getText().toString().isEmpty()) {
                et_address1.setError("Please Enter Billing Address");
                et_address1.requestFocus();
            } else if (et_address2.getText().toString().isEmpty()) {
                et_address2.setError("Please Enter Landmark");
                et_address2.requestFocus();
            } else if (et_pinCode.getText().toString().isEmpty()) {
                et_pinCode.setError("Please Enter PinCode");
                et_pinCode.requestFocus();
            } else if (!et_pinCode.getText().toString().matches(AppUtils.mPINCodePattern)) {
                et_pinCode.setError(getResources().getString(R.string.error_et_mr_PINno));
                et_pinCode.requestFocus();
            } else if (et_city.getText().toString().isEmpty()) {
                et_city.setError("Please Enter City");
                et_city.requestFocus();
            } else if (et_state.getText().toString().isEmpty()) {
                et_state.setError("Please Select State");
                et_state.requestFocus();
            } else if (et_mobileNumber.getText().toString().isEmpty()) {
                et_mobileNumber.setError("Please Enter Mobile No.");
                et_mobileNumber.requestFocus();
            } else if (!AppUtils.isValidMobileno(et_mobileNumber.getText().toString())) {
                et_mobileNumber.setError("Please Enter Valid Mobile No.");
                et_mobileNumber.requestFocus();
            } else {
                startAddressRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    private void startAddressRequest() {
        try {
            if (AppUtils.isNetworkAvailable(AddDeliveryAddress_Activity.this)) {
                List<NameValuePair> postParameters = new ArrayList<>();
                postParameters.add(new BasicNameValuePair("Formno", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("FirstName", et_firstName.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("LastName", et_lastName.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Address1", et_address1.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Address2", et_address2.getText().toString().trim().replace(",", " ")));


                for (int i = 0; i < AppController.stateList.size(); i++) {
                    if (et_state.getText().toString().equalsIgnoreCase(AppController.stateList.get(i).get("State"))) {
                        selectedStateId = AppController.stateList.get(i).get("STATECODE");
                    }
                }

                postParameters.add(new BasicNameValuePair("StateCode", "" + selectedStateId.trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("District", "0"));
                postParameters.add(new BasicNameValuePair("City", et_city.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("PinCode", et_pinCode.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("MobileNo", et_mobileNumber.getText().toString().trim().replace(",", " ")));
                postParameters.add(new BasicNameValuePair("Email", AppController.getSpUserInfo().getString(SPUtils.USER_EMAIL, "")));

//                String Usertype = (AppController.getSpUserInfo().getString(SPUtils.USER_TYPE, ""));
//                if (Usertype.equalsIgnoreCase("CUSTOMER"))
                postParameters.add(new BasicNameValuePair("UserType", "N"));
//                else if (Usertype.equalsIgnoreCase("DISTRIBUTOR"))
//                    postParameters.add(new BasicNameValuePair("UserType", "D"));
//                else
//                    postParameters.add(new BasicNameValuePair("UserType", "N"));


                executeAddAddressRequest(postParameters);
            } else {
                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    private void executeAddAddressRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(AddDeliveryAddress_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(AddDeliveryAddress_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(AddDeliveryAddress_Activity.this, postParameters, QueryUtils.methodToAddCheckOutDeliveryAddress, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    saveDeliveryAddressInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    private void saveDeliveryAddressInfo(JSONArray jsonArrayData) {
        try {
            CheckoutToPay_Activity.deliveryAddressList.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject jsonObject = jsonArrayData.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                map.put("ID", "" + jsonObject.getString("ID"));
                map.put("MemFirstName", "" + WordUtils.capitalizeFully(jsonObject.getString("MemFirstName")));
                map.put("MemLastName", "" + WordUtils.capitalizeFully(jsonObject.getString("MemLastName")));
                map.put("Address1", "" + WordUtils.capitalizeFully(jsonObject.getString("Address1")));
                map.put("Address2", "" + WordUtils.capitalizeFully(jsonObject.getString("Address2")));
                map.put("CountryID", "" + jsonObject.getString("CountryID"));
                map.put("CountryName", "" + WordUtils.capitalizeFully(jsonObject.getString("CountryName")));
                map.put("StateCode", "" + jsonObject.getString("StateCode"));
                map.put("StateName", "" + WordUtils.capitalizeFully(jsonObject.getString("StateName")));
                map.put("District", "" + WordUtils.capitalizeFully(jsonObject.getString("District")));
                map.put("City", "" + WordUtils.capitalizeFully(jsonObject.getString("City")));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Email", "" + jsonObject.getString("MailID"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("EntryType", "" + jsonObject.getString("EntryType"));
                map.put("Address", "" + WordUtils.capitalizeFully(jsonObject.getString("Address").replace("&nbsp;", " ")));
                CheckoutToPay_Activity.deliveryAddressList.add(map);
            }

            final Dialog dialog = AppUtils.createDialog(AddDeliveryAddress_Activity.this, true);
            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText("Your delivery address is added successfully!!!");
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();

                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        try {
                            if (getIntent().getExtras().getString("ComesFrom").equalsIgnoreCase("CheckoutToPay_Activity")) {

                                if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                                    startActivity(new Intent(AddDeliveryAddress_Activity.this, CheckoutToPay_Activity.class));
                                    finish();
                                } else {
                                    showLoginRegisterDialog();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    AppUtils.dismissProgressDialog();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(AddDeliveryAddress_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(AddDeliveryAddress_Activity.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                AppUtils.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResult(JSONArray jsonArray) {
        try {
            AppController.stateList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();

                map.put("STATECODE", jsonObject.getString("STATECODE"));
                map.put("State", WordUtils.capitalizeFully(jsonObject.getString("State")));

                AppController.stateList.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStateDialog() {
        try {
            state = new String[AppController.stateList.size()];
            for (int i = 0; i < AppController.stateList.size(); i++) {
                state[i] = AppController.stateList.get(i).get("State");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select State");
            builder.setItems(state, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // Do something with the selection
                    et_state.setText(state[item]);
                }
            });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }


    private void executePinCodeRequest(final String PinCode) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(AddDeliveryAddress_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("Pincode", PinCode));
                    response = AppUtils.callWebServiceWithMultiParam(AddDeliveryAddress_Activity.this, postParameters, QueryUtils.methodtoGet_PincodeWiseDetails, TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                AppUtils.dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(resultData);

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                        JSONObject jsonObject1 = jsonArrayData.getJSONObject(0);

                        et_city.setText(WordUtils.capitalizeFully(jsonObject1.getString("CityName")));
                        et_state.setText(WordUtils.capitalizeFully(jsonObject1.getString("StateName")));

                        selectedStateId = jsonObject1.getString("StateID");

                    } else {
                        et_city.setText("");
                        et_state.setText("");

                        AppUtils.alertDialog(AddDeliveryAddress_Activity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(AddDeliveryAddress_Activity.this);
        }
    }

    public void showLoginRegisterDialog() {
        try {

            final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog_three);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Please Select an option to continue."));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Register");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startActivity(new Intent(AddDeliveryAddress_Activity.this, Register_User_Activity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Login");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startActivity(new Intent(AddDeliveryAddress_Activity.this, Login_Activity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
