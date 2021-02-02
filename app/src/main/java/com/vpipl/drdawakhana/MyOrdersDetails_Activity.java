package com.vpipl.drdawakhana;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.drdawakhana.Adapters.MyOrdersDetailList_Adapter;
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
public class MyOrdersDetails_Activity extends AppCompatActivity {
    String TAG = "MyOrdersDetails_Activity";
    RecyclerView listView;
    LinearLayout layout_listView, layout_nodata;
    LinearLayout coordinatorLayout;

    MyOrdersDetailList_Adapter adapter;
    ArrayList<HashMap<String, String>> ordersDetailList = new ArrayList<>();

    TextView txt_orderNo, txt_orderDate, txt_orderStatus, txt_orderAmount;
    Activity act = MyOrdersDetails_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myordersdetails_activity);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
            TextView heading = findViewById(R.id.heading);

            heading.setText("Order Detail");
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

            txt_orderNo = (TextView) findViewById(R.id.txt_orderNo);
            txt_orderDate = (TextView) findViewById(R.id.txt_OrderDate);
            txt_orderStatus = (TextView) findViewById(R.id.txt_orderStatus);
            txt_orderAmount = (TextView) findViewById(R.id.txt_orderAmount);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            listView.setLayoutManager(mLayoutManager);
            listView.setItemAnimator(new DefaultItemAnimator());
            int resId1 = R.anim.test;
            LayoutAnimationController animation1 = AnimationUtils.loadLayoutAnimation(this, resId1);
            listView.setLayoutAnimation(animation1);

            if (AppUtils.isNetworkAvailable(act)) {
                executeGetMyOrdersDetailsRequest();
            } else {
                AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_networkAlert));
                showNoData();
            }

            txt_orderNo.setText("Order No. " + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderNo"));
            txt_orderDate.setText("" + AppUtils.getDateFromAPIDate(MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderDate")));


            if (MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderStatus").equalsIgnoreCase("N"))
                txt_orderStatus.setText("Confirmation Pending");
            else
                txt_orderStatus.setText("Order Confirmed");

//            txt_orderStatus.setText("" + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderStatus"));
            txt_orderAmount.setText(getResources().getString(R.string.Rs) + " " + MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderAmt"));

            /*LinearLayout lin_back = (LinearLayout) findViewById(R.id.lin_back);
            lin_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showAlertSnakeMessage(coordinatorLayout, getResources().getString(R.string.txt_exception));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeGetMyOrdersDetailsRequest() {
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
                            postParameters.add(new BasicNameValuePair("OrderNo", MyOrdersList_Adapter.ordersList.get(getIntent().getExtras().getInt("position")).get("OrderNo")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetViewOrdersDetails, TAG);
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

                                if (jsonArrayData.length() > 0) {
                                    getOrdersDetailListResult(jsonArrayData);
                                } else {
                                    showNoData();
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
        }
    }

    private void getOrdersDetailListResult(JSONArray jsonArray) {
        try {
            ordersDetailList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ProductImg", "" + SPUtils.productImageURL + jsonObject.getString("ImgPath"));
                map.put("OrderNo", "" + jsonObject.getString("OrderNo"));

                map.put("Productid", "" + jsonObject.getString("ProductID"));
                map.put("ProductName", "" + jsonObject.getString("ProductName"));
                map.put("NetAmount", "" + jsonObject.getString("Netamount"));
                map.put("Qty", "" + jsonObject.getString("Qty"));
                map.put("MRPTitle", "");
                map.put("ProductQtyTitle", "");
                map.put("ProductQtyValue", "");
                map.put("PackingText", "");
                /*map.put("MRPTitle", "" + jsonObject.getString("MRPTitle"));
                map.put("ProductQtyTitle", "" + jsonObject.getString("ProductQtyTitle"));
                map.put("ProductQtyValue", "" + jsonObject.getString("ProductQtyValue"));
                map.put("PackingText", "" + jsonObject.getString("PackingText"));*/

                ordersDetailList.add(map);
            }

            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (ordersDetailList.size() > 0) {
                adapter = new MyOrdersDetailList_Adapter(act, ordersDetailList);
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
