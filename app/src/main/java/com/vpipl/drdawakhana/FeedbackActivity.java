package com.vpipl.drdawakhana;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends Activity {

    Activity act;

    LinearLayout ll_back;
    EditText edtxt_feedback;
    RelativeLayout rl_login_btn;
    Dialog dialog;
    private RadioGroup rg_view_detail_for;
    ImageView iv_smile, iv_sad, iv_confused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        try {
            act = FeedbackActivity.this;

            edtxt_feedback = (EditText) findViewById(R.id.edtxt_feedback);
            rg_view_detail_for = findViewById(R.id.rg_view_detail_for);
            rl_login_btn = findViewById(R.id.rl_login_btn);

            ll_back = (LinearLayout) findViewById(R.id.ll_back);
            //iv_smile, iv_sad, iv_confused
            iv_smile = (ImageView) findViewById(R.id.iv_smile);
            iv_sad = (ImageView) findViewById(R.id.iv_sad);
            iv_confused = (ImageView) findViewById(R.id.iv_confused);

            ll_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            rl_login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edtxt_feedback.getText().toString().matches("")) {
                        Toast.makeText(act, "Please Enter Feedback !!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isNetworkConnected()) {
                            executeFeedbackSubmitRequest();
                        } else {
                            Toast.makeText(act, "No internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            iv_smile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(act, "Smile", Toast.LENGTH_SHORT).show();

                    iv_smile.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_smiling_active));
                    iv_sad.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_sad_deactive));
                    iv_confused.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_confused_deactive));
                }
            });
            iv_sad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(act, "Sad", Toast.LENGTH_SHORT).show();

                    iv_smile.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_smiling_deactive));
                    iv_sad.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_sad_active));
                    iv_confused.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_confused_deactive));
                }
            });
            iv_confused.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(act, "Confused", Toast.LENGTH_SHORT).show();

                    iv_smile.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_smiling_deactive));
                    iv_sad.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_sad_deactive));
                    iv_confused.setImageDrawable(getResources().getDrawable(R.drawable.ic_feed_confused_active));
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void executeFeedbackSubmitRequest() {
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
                            int selectedId = rg_view_detail_for.getCheckedRadioButtonId();
                            RadioButton radioButton = findViewById(selectedId);
                            String type = radioButton.getText().toString().trim();

                            /*byte[] data1 = type.getBytes("UTF-8");
                            type = Base64.encodeToString(data1, Base64.DEFAULT);

                            byte[] data2 = edtxt_feedback.getText().toString().getBytes("UTF-8");
                            String feedback = Base64.encodeToString(data2, Base64.DEFAULT);*/

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("RID", AppController.getSpUserInfo().getString(SPUtils.USER_ID, "")));
                            postParameters.add(new BasicNameValuePair("Type", type));
                            postParameters.add(new BasicNameValuePair("Message", "" + edtxt_feedback.getText().toString()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodTouser_Feedback, "TAG");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            //  dialog.dismiss();

                            JSONObject jsonObject = new JSONObject(resultData);
                            //   JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));
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


}
