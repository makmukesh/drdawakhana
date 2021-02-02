package com.vpipl.drdawakhana;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.drdawakhana.Adapters.Package_List_Adapter;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class PackageListActivity extends Activity {
    private String TAG = "PackageListActivity";

    Activity act;
    ImageBadgeView mukesh_begview;
    ValueAnimator animator;

    public Package_List_Adapter adapter;
    RecyclerView recyclerView;
    LinearLayout ll_data_found, ll_no_data_found;
    public static ArrayList<HashMap<String, String>> array_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_package_list);

            act = PackageListActivity.this;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
            TextView heading = findViewById(R.id.heading);

            heading.setText("MemberShip Plan List");
            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mukesh_begview.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.setDuration(500);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setRepeatCount(-1);
                animator.start();
            }

            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.listView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                executePackageListRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executePackageListRequest() {
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
                            postParameters.add(new BasicNameValuePair("FormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToViewPackage, TAG);
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
                            array_list.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_data_found.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                                if (jsonObject.getJSONArray("Data").length() > 0) {
                                    getAllActivityListResult(jsonObject.getJSONArray("Data"));
                                }
                            } else {
                                showListView();
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

    private void getAllActivityListResult(JSONArray jsonArray) {
        try {
            array_list.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //if (getIntent().getStringExtra("Type").equalsIgnoreCase("" + jsonObject.getString("Membership"))) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("PID", jsonObject.getString("MID"));
                    map.put("PackageName", jsonObject.getString("PackageName"));
                    map.put("Validity", jsonObject.getString("PackageValidity"));
                    map.put("Membership", "");
                    map.put("Amount", jsonObject.getString("Amount"));
                    map.put("PackageType", jsonObject.getString("PackageType"));
                    map.put("Remarks", jsonObject.getString("Descri"));

                    array_list.add(map);
               // }
            }
            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (array_list.size() > 0) {
                adapter = new Package_List_Adapter(act, array_list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                ll_data_found.setVisibility(View.VISIBLE);
                ll_no_data_found.setVisibility(View.GONE);
            } else {
                ll_data_found.setVisibility(View.GONE);
                ll_no_data_found.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
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
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppController.getSpUserInfo().edit().clear().commit();
        Intent intent = new Intent(act, Login_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        act.startActivity(intent);
        finish();
    }
}