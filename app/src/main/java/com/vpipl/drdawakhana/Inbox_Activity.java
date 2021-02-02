package com.vpipl.drdawakhana;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.drdawakhana.Adapters.Inbox_Adapter;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Inbox_Activity extends Activity {
    private RecyclerView recyclerView;
    public static ArrayList<HashMap<String, String>> AllTips = new ArrayList<>();
    String ComesFrom = "Notification";
    Activity act = Inbox_Activity.this;
    LinearLayout ll_no_data_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            
            LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
            TextView heading = findViewById(R.id.heading);

            heading.setText("Notification List");
            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            
             ll_no_data_found = (LinearLayout)findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

            AllTips.clear();

            if (AppUtils.isNetworkAvailable(this)) {
                // executeInboxList();
            }
            else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));


            ComesFrom = getIntent().getStringExtra("ComesFrom");

            recyclerView.setVisibility(View.GONE);
            ll_no_data_found.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void executeInboxList() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("MobileNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, "")));
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetMessageHistory, "Inbox_Activity");
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
                        JSONArray jsonArrayOld = jsonObject.getJSONArray("Data");
                        getTipsResults(jsonArrayOld);

                        recyclerView.setVisibility(View.VISIBLE);
                        ll_no_data_found.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        ll_no_data_found.setVisibility(View.VISIBLE);
                      //  AppUtils.alertDialogWithFinish(act, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getTipsResults(JSONArray jsonArrayNewTips) {
        try {

            AllTips.clear();

            for (int i = 0; i < jsonArrayNewTips.length(); i++) {
                JSONObject jsonObject = jsonArrayNewTips.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("MessageHeading", jsonObject.getString("MessageHeading").trim());
                map.put("MessageText", jsonObject.getString("MessageText").trim());
                map.put("FormNo", jsonObject.getString("MobileNo"));
                map.put("TipImage", jsonObject.getString("ImagePath"));
                map.put("TimeStamp", getDateFromAPIDate(jsonObject.getString("RTS")));
                map.put("TipDate", getOnlyDateFromAPIDate(jsonObject.getString("RTS")));

                AllTips.add(map);
            }

            if (AllTips.size() > 0) {
                Inbox_Adapter mAdapter = new Inbox_Adapter(AllTips, act);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setNestedScrollingEnabled(false);

                recyclerView.setAdapter(mAdapter);

                recyclerView.setVisibility(View.VISIBLE);
                ll_no_data_found.setVisibility(View.GONE);
            }
            else {
                recyclerView.setVisibility(View.GONE);
                ll_no_data_found.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getDateFromAPIDate(String date) {
        try {
            if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);

            if (date.contains("/Date("))
                cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
            else
                cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

            date = DateFormat.format("h:mm a", cal).toString();

            if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String getOnlyDateFromAPIDate(String date) {
        try {
            if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);

            if (date.contains("/Date("))
                cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
            else
                cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

            date = DateFormat.format("EE, MMM dd", cal).toString();

            if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    @Override
    public void onBackPressed() {

        if (ComesFrom.equalsIgnoreCase("Home"))
            finish();
        else
            startSplash(new Intent(act, MainActivity.class));

    }

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}