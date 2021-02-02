package com.vpipl.drdawakhana;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.test.pg.secure.pgsdkv4.PGConstants;
import com.test.pg.secure.pgsdkv4.PaymentGatewayPaymentInitializer;
import com.test.pg.secure.pgsdkv4.PaymentParams;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.Utils.SampleAppConstants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PackagePurchaseActivity extends Activity {
    private String TAG = "PackagePurchaseActivity";

    Activity act;
    int n;
    String s_PID, s_packagename, s_packageAmount, s_package_validity, s_packageType, RefNo = "000000000", FullRequest;
    String ResponseType, FullResponse, response_code, response_message, payment_channel, payment_mode, transaction_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            act = PackagePurchaseActivity.this;

            if (!getIntent().equals(null)) {
                s_PID = getIntent().getStringExtra("PID");
                s_packagename = getIntent().getStringExtra("PackageName");
                s_packageAmount = getIntent().getStringExtra("Amount");
                s_package_validity = getIntent().getStringExtra("Validity");
                s_packageType = getIntent().getStringExtra("PackageType");
            }

            // AppUtils.alertDialogWithFinish(act, s_PID + "  " + s_packagename + "  " + s_packageAmount + "  " + s_package_validity);

            Random rnd = new Random();
            n = 100000 + rnd.nextInt(900000);
            SampleAppConstants.PG_ORDER_ID = Integer.toString(n);
            PaymentParams pgPaymentParams = new PaymentParams();
            pgPaymentParams.setAPiKey(SampleAppConstants.PG_API_KEY);
            pgPaymentParams.setAmount(s_packageAmount);
            pgPaymentParams.setEmail(SampleAppConstants.PG_EMAIL);
            pgPaymentParams.setName(AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
            pgPaymentParams.setPhone(AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, ""));
            pgPaymentParams.setOrderId(SampleAppConstants.PG_ORDER_ID);
            pgPaymentParams.setCurrency(SampleAppConstants.PG_CURRENCY);
            pgPaymentParams.setDescription(SampleAppConstants.PG_DESCRIPTION);
            pgPaymentParams.setCity(SampleAppConstants.PG_CITY);
            pgPaymentParams.setState(SampleAppConstants.PG_STATE);
            pgPaymentParams.setAddressLine1(SampleAppConstants.PG_ADD_1);
            pgPaymentParams.setAddressLine2(SampleAppConstants.PG_ADD_2);
            pgPaymentParams.setZipCode(SampleAppConstants.PG_ZIPCODE);
            pgPaymentParams.setCountry(SampleAppConstants.PG_COUNTRY);
            pgPaymentParams.setReturnUrl(SampleAppConstants.PG_RETURN_URL);
            pgPaymentParams.setMode(SampleAppConstants.PG_MODE);
            pgPaymentParams.setUdf1(SampleAppConstants.PG_UDF1);
            pgPaymentParams.setUdf2(SampleAppConstants.PG_UDF2);
            pgPaymentParams.setUdf3(SampleAppConstants.PG_UDF3);
            pgPaymentParams.setUdf4(SampleAppConstants.PG_UDF4);
            pgPaymentParams.setUdf5(SampleAppConstants.PG_UDF5);

            FullRequest = "apikey|" + pgPaymentParams.getAPiKey() + "|amount|" + pgPaymentParams.getAmount() + "|email|" + pgPaymentParams.getEmail() + "|name|" +
                    pgPaymentParams.getName() + "|phone|" + pgPaymentParams.getPhone() + "|Mobile|" +
                    pgPaymentParams.getPhone() + "|order_id|" + pgPaymentParams.getOrderId() + "|currency|" +
                    pgPaymentParams.getCurrency() + "|description|" + pgPaymentParams.getDescription() + "|city|" + pgPaymentParams.getCity() + "|state|" +
                    pgPaymentParams.getState() + "|address_line_1|" + pgPaymentParams.getAddressLine1() + "|address_line_2|" + pgPaymentParams.getAddressLine2() + "|zip_code|" +
                    pgPaymentParams.getZipCode() + "|country|" + pgPaymentParams.getCountry() + "|return_url|" + pgPaymentParams.getReturnURL() + "|mode|" +
                    pgPaymentParams.getMode() + "|udf1|" + pgPaymentParams.getUDF1() + "|udf2|" + pgPaymentParams.getUDF2()
                    + "|udf3|" + pgPaymentParams.getUDF3() + "|udf4|" + pgPaymentParams.getUDF4() + "|udf5|" + pgPaymentParams.getUDF5();

            if (!s_packageAmount.equalsIgnoreCase("") && !FullRequest.equalsIgnoreCase("")) {
                PaymentBefore();
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void PaymentBefore() {
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
                            /*UserID:Amount:AppType:FullRequest:PaymentType:IP:*/
                            postParameters.add(new BasicNameValuePair("UserID", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Amount", s_packageAmount));
                            postParameters.add(new BasicNameValuePair("AppType", "A"));
                            postParameters.add(new BasicNameValuePair("PaymentType", "Online"));
                            postParameters.add(new BasicNameValuePair("FullRequest", FullRequest));
                            postParameters.add(new BasicNameValuePair("IP", AppUtils.getDeviceId(act)));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToMembershipBeforePayment, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("", "" + e.getMessage());
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
                                JSONObject jo = jsonArrayData.getJSONObject(0);
                                RefNo = jo.getString("RefNo");
                                Log.e("RefNo", RefNo);
                                SampleAppConstants.PG_ORDER_ID = RefNo;
                                PaymentParams pgPaymentParams = new PaymentParams();
                                pgPaymentParams.setAPiKey(SampleAppConstants.PG_API_KEY);
                                pgPaymentParams.setAmount(s_packageAmount);
                                pgPaymentParams.setEmail(SampleAppConstants.PG_EMAIL);
                                pgPaymentParams.setName(AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
                                pgPaymentParams.setPhone(AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, ""));
                                pgPaymentParams.setOrderId(RefNo);
                                pgPaymentParams.setCurrency(SampleAppConstants.PG_CURRENCY);
                                pgPaymentParams.setDescription(SampleAppConstants.PG_DESCRIPTION);
                                pgPaymentParams.setCity(SampleAppConstants.PG_CITY);
                                pgPaymentParams.setState(SampleAppConstants.PG_STATE);
                                pgPaymentParams.setAddressLine1(SampleAppConstants.PG_ADD_1);
                                pgPaymentParams.setAddressLine2(SampleAppConstants.PG_ADD_2);
                                pgPaymentParams.setZipCode(SampleAppConstants.PG_ZIPCODE);
                                pgPaymentParams.setCountry(SampleAppConstants.PG_COUNTRY);
                                pgPaymentParams.setReturnUrl(SampleAppConstants.PG_RETURN_URL);
                                pgPaymentParams.setMode(SampleAppConstants.PG_MODE);
                                pgPaymentParams.setUdf1(SampleAppConstants.PG_UDF1);
                                pgPaymentParams.setUdf2(SampleAppConstants.PG_UDF2);
                                pgPaymentParams.setUdf3(SampleAppConstants.PG_UDF3);
                                pgPaymentParams.setUdf4(SampleAppConstants.PG_UDF4);
                                pgPaymentParams.setUdf5(SampleAppConstants.PG_UDF5);
                                PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams, act);
                                pgPaymentInitialzer.initiatePaymentProcess();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PGConstants.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String paymentResponse = data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
                    System.out.println("paymentResponse: " + paymentResponse);
                    if (paymentResponse.equals("null")) {
                        System.out.println("Transaction Error!");
                        ResponseType = "F";
                        Toast.makeText(act, "Transaction Error!", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject response = new JSONObject(paymentResponse);
                        FullResponse = response.toString().replace(" ", "_");
                        response_code = response.getString("response_code");
                        if (response_code.matches("0")) {
                            ResponseType = "S";
                        } else {
                            ResponseType = "F";
                        }
                        response_message = response.getString("response_message").replace(" ", "_");
                        payment_channel = response.getString("payment_channel").replace(" ", "_");
                        payment_mode = response.getString("payment_mode");
                        transaction_id = response.getString("transaction_id");
                        //   cardmasked = response.getString("cardmasked");

                        if (AppUtils.isNetworkAvailable(act)) {
                            PaymentAfter();
                        } else {
                            AppUtils.alertDialog(act, "No internet connection!");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                ResponseType = "F";
                Toast.makeText(act, "Payment Cancel", Toast.LENGTH_SHORT).show();

                FullResponse = "Payment Cancel By User";
                response_code = "10000";

                response_message = "Payment Cancel By User";
                payment_channel = "123";
                payment_mode = "Online";
                transaction_id = RefNo;
                if (AppUtils.isNetworkAvailable(act)) {
                    PaymentAfter();
                } else {
                    AppUtils.alertDialog(act, "No internet connection!");
                }
            }
        }
    }


    private void PaymentAfter() {
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
                            /*UserID:ResponseType:RefNo:FullResponse:Response_Code:Response_Message:Payment_Chanel:
                            Payment_Mode:Transaction_ID:MembershipID:Amount:FormNo:*/
                            postParameters.add(new BasicNameValuePair("UserID", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("ResponseType", ResponseType));
                            postParameters.add(new BasicNameValuePair("RefNo", RefNo));
                            postParameters.add(new BasicNameValuePair("FullResponse", FullResponse));
                            postParameters.add(new BasicNameValuePair("Response_Code", response_code));
                            postParameters.add(new BasicNameValuePair("Response_Message", response_message));
                            postParameters.add(new BasicNameValuePair("Payment_Chanel", payment_channel));
                            postParameters.add(new BasicNameValuePair("Payment_Mode", payment_mode));
                            postParameters.add(new BasicNameValuePair("Transaction_ID", transaction_id));
                            postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("MembershipID", s_PID));
                            postParameters.add(new BasicNameValuePair("Amount", s_packageAmount));

                            postParameters.add(new BasicNameValuePair("PackageType", s_packageType));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToMembershipAfterPayment, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("", "" + e.getMessage());
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

                                Intent intent = new Intent(act, PackagePurchaseResponseActivity.class);
                                intent.putExtra("txn_id", "" + RefNo);
                                intent.putExtra("Msg", "" + response_message);
                                intent.putExtra("status", "" + ResponseType);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                act.startActivity(intent);
                            } else {
                                AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                            finish();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
            finish();
        }
    }

}