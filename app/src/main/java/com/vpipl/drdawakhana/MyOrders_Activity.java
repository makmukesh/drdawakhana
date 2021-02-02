package com.vpipl.drdawakhana;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.drdawakhana.Adapters.MyOrdersList_Adapter;
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

/**
 * Created by Mukesh on 17-Nov-20.
 */
public class MyOrders_Activity extends AppCompatActivity {
    String TAG = "MyOrders_Activity";
    RecyclerView listView;
    LinearLayout layout_listView, layout_nodata;
    LinearLayout coordinatorLayout;

    public static MyOrdersList_Adapter adapter;
    ArrayList<HashMap<String, String>> ordersList = new ArrayList<>();
    Activity act = MyOrders_Activity.this;

    Button btn_upcomming,btn_postorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorders_activity);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
            TextView heading = findViewById(R.id.heading);

            heading.setText("My Orders");
            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            coordinatorLayout = (LinearLayout) findViewById(R.id.coordinatorLayout);
            listView = (RecyclerView) findViewById(R.id.listView);
            layout_listView = (LinearLayout) findViewById(R.id.layout_listView);
            layout_nodata = (LinearLayout) findViewById(R.id.layout_nodata);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());
            int resId1 = R.anim.test;
            LayoutAnimationController animation1 = AnimationUtils.loadLayoutAnimation(this, resId1);
            listView.setLayoutAnimation(animation1);

            if (AppUtils.isNetworkAvailable(act)) {
                if (AppUtils.showLogs) Log.v(TAG, "onCreate executeGetMyOrdersRequest() called");
                executeGetMyOrdersRequest();
            } else {
                AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_networkAlert));
                showNoData();
            }

            btn_upcomming = (Button)findViewById(R.id.btn_upcomming);
            btn_postorder = (Button)findViewById(R.id.btn_postorder);

            btn_upcomming.setBackgroundResource(R.drawable.vipin_back);
            btn_postorder.setBackgroundResource(R.drawable.vipin_back_reverse);

            btn_upcomming.setTextColor(getResources().getColor(R.color.app_color_white));
            btn_postorder.setTextColor(getResources().getColor(R.color.colorDark));
           /* btn_upcomming.setTextColor(Integer.parseInt("#fff"));
            btn_postorder.setTextColor(Integer.parseInt("#205172"));*/

            btn_upcomming.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_upcomming.setBackgroundResource(R.drawable.vipin_back);
                    btn_postorder.setBackgroundResource(R.drawable.vipin_back_reverse);
                    btn_upcomming.setTextColor(getResources().getColor(R.color.app_color_white));
                    btn_postorder.setTextColor(getResources().getColor(R.color.colorDark));
                }
            });

            btn_postorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_postorder.setBackgroundResource(R.drawable.vipin_back);
                    btn_upcomming.setBackgroundResource(R.drawable.vipin_back_reverse);

                    btn_postorder.setTextColor(getResources().getColor(R.color.app_color_white));
                    btn_upcomming.setTextColor(getResources().getColor(R.color.colorDark));

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeGetMyOrdersRequest() {
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
                            postParameters.add(new BasicNameValuePair("OrderByFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetViewOrdersList, TAG);
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
                                JSONArray jsonArrayData = jsonObject.getJSONArray("FillNewOrdersDetail");

                                if (jsonArrayData.length() > 0) {
                                    getOrdersListResult(jsonArrayData);
                                } else {
                                    showNoData();
                                }
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                if (AppUtils.showLogs)
                                    Log.v(TAG, "executeGetMyOrdersRequest executed...Failed... called");
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

    private void getOrdersListResult(JSONArray jsonArray) {
        try {
            ordersList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();
                //String OrderNo,String OrderAmt,String OrderStatus,String OrderDate
                map.put("OrderNo", "" + jsonObject.getString("OrderNo"));
                map.put("OrderAmt", "" + jsonObject.getString("OrderAmt"));
                map.put("OrderDate", "" + jsonObject.getString("OrderDate"));
                map.put("OrderStatus", "" + jsonObject.getString("OrderStatus"));
                ordersList.add(map);
            }

            if (AppUtils.showLogs) Log.v(TAG, "ordersList..." + ordersList);
            showListView();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (ordersList.size() > 0) {
                adapter = new MyOrdersList_Adapter(act, ordersList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_listView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layout_nodata.setVisibility(View.GONE);
            } else {
                showNoData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoData() {
        try {
            layout_listView.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            layout_nodata.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
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
