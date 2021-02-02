package com.vpipl.drdawakhana;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.drdawakhana.Adapters.ThanksOrderDetail_Adapter;
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

/**
 * Created by PC14 on 08-Apr-16.
 */
public class ThanksScreen_Activity extends AppCompatActivity {
    public ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
    public LinearLayout layout_noData, layout_listView;
    public ListView list_orderDetails;
    public ThanksOrderDetail_Adapter adapter;
    public ViewGroup thankyouHeaderView = null, thankyouFooterView = null;
    public TextView txt_orderNumber, txt_orderDate, txt_orderAmount, txt_deliveryStatus, txt_totalAmount, txt_deliveryCharge ,tv_order_confirmation ;
    public TextView txt_name, txt_address, txt_mobNo, txt_email;
    TextView txt_order ,txt_back_to_home;
    String TAG = "Thanks_Activity";
    Button btn_startShopping, btn_shopMore ,btn_view_invoice;

    ImageView img_menu;
    ImageView img_search;
    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {
        LinearLayout img_nav_back =  findViewById(R.id.img_nav_back);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToHome();
            }
        });
        /*img_menu = (ImageView) findViewById(R.id.img_menu);
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
                moveToHome();
            }
        });*/
    }

    public ImageBadgeView mukesh_begview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanks_screen_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            layout_listView = (LinearLayout) findViewById(R.id.layout_listView);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
            list_orderDetails = (ListView) findViewById(R.id.list_orderDetails);
            btn_startShopping = (Button) findViewById(R.id.btn_startShopping);
            txt_order = (TextView) findViewById(R.id.txt_order);
            txt_back_to_home = (TextView) findViewById(R.id.txt_back_to_home);


            mukesh_begview = findViewById(R.id.mukesh_begview);
            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ThanksScreen_Activity.this, AddCartCheckOut_Activity.class));
                }
            });
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

            if (getIntent().getExtras() != null) {
                String orderDetails = "";
                orderDetails = getIntent().getExtras().getString("orderDetails");

                JSONObject jsonObject = new JSONObject(orderDetails);

                JSONArray jsonArrayMainOrder = jsonObject.getJSONArray("MainOrder");
                JSONArray jsonArrayOrderDetails = jsonObject.getJSONArray("OrderDetails");

                orderDetailsList.clear();

                for (int i = 0; i < jsonArrayOrderDetails.length(); i++) {
                    JSONObject jsonObjectMainOrder = jsonArrayMainOrder.getJSONObject(0);
                    JSONObject jsonObjectOrderDetails = jsonArrayOrderDetails.getJSONObject(i);

                    HashMap<String, String> map = new HashMap<>();

                    map.put("GrossOrderAmt", "" + jsonObjectMainOrder.getString("GrossOrderAmt"));
                    map.put("Shipping", "" + jsonObjectMainOrder.getString("Shipping"));
                    map.put("OrderAmt", "" + jsonObjectMainOrder.getString("OrderAmt"));

                    map.put("OrderQvp", "" + jsonObjectMainOrder.getString("OrderCvp"));

                    map.put("OrderNo", "" + jsonObjectMainOrder.getString("OrderNo"));
                    // map.put("OrderDate", "" + jsonObjectMainOrder.getString("OrderDate"));
                    map.put("OrderDate", "" + jsonObjectMainOrder.getString("RecTimeStamp"));

                    if (jsonObjectMainOrder.getString("IsConfirm").equals("N")) {
                        map.put("OrderStatus", "Confirmation Pending");
                    } else {
                        map.put("OrderStatus", "Confirmed");
                    }

                    String DeliveryAddressName = "";

                    if (jsonObjectMainOrder.getString("MemFirstName").equals("")) {
                        if (!jsonObjectMainOrder.getString("MemLastName").equals("")) {
                            DeliveryAddressName = jsonObjectMainOrder.getString("MemLastName");
                        }
                    } else {
                        if (jsonObjectMainOrder.getString("MemLastName").equals("")) {
                            DeliveryAddressName = jsonObjectMainOrder.getString("MemFirstName");
                        } else {
                            DeliveryAddressName = jsonObjectMainOrder.getString("MemFirstName") + " " + jsonObjectMainOrder.getString("MemLastName");
                        }
                    }

                    map.put("DeliveryAddressName", "" + DeliveryAddressName);

                    map.put("Mobl", "" + jsonObjectMainOrder.getString("Mobl"));
                    map.put("EMail", "" + jsonObjectMainOrder.getString("EMail"));
                    map.put("address", "" + jsonObjectMainOrder.getString("Address1"));
                    map.put("PinCode", "" + jsonObjectMainOrder.getString("PinCode"));
                    map.put("StateName", "" + jsonObjectMainOrder.getString("StateName"));
                    map.put("City", "" + jsonObjectMainOrder.getString("City"));

                    map.put("ProductName", "" + jsonObjectOrderDetails.getString("ProductName"));
                    map.put("Quantity", "" + jsonObjectOrderDetails.getString("Quantity"));
                    map.put("ImageUrl", "" + jsonObjectOrderDetails.getString("ImageUrl").replaceAll(" ","%20"));
                    map.put("Netamount", "" + jsonObjectOrderDetails.getString("NetAmount"));

                    map.put("Size", "" + jsonObjectOrderDetails.getString("Size"));
                    map.put("Option", "");
                    map.put("Color", "" + jsonObjectOrderDetails.getString("Color"));

                    // map.put("IsKit",""+jsonObjectOrderDetails.getString("IsKit"));
                    // map.put("UID",""+jsonObjectOrderDetails.getString("UID"));
                    // map.put("ProdType",""+jsonObjectOrderDetails.getString("ProdType"));

                    orderDetailsList.add(map);
                }
            } else {
                showNoDataLayout();
            }

            if (AppUtils.showLogs) Log.v(TAG, "orderDetailsList....." + orderDetailsList.size());

            if (orderDetailsList.size() > 0) {
               // showOrderListView();
                txt_order.setText("invoice no. #" + orderDetailsList.get(0).get("OrderNo"));
            } else {
                showNoDataLayout();
            }

            btn_startShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        moveToHome();
                        /* AppController.selectedProductsList.clear();

                        Intent intent = new Intent(ThanksScreen_Activity.this, CategoryMainListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            txt_back_to_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveToHome();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ThanksScreen_Activity.this);
        }
    }

    private void showOrderListView() {
        try {
            layout_listView.setVisibility(View.VISIBLE);
            layout_noData.setVisibility(View.GONE);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            thankyouHeaderView = (ViewGroup) inflater.inflate(R.layout.thankyouheader_layout, list_orderDetails, false);
            setHeaderDetails();

            thankyouFooterView = (ViewGroup) inflater.inflate(R.layout.thankyoufooter_layout, list_orderDetails, false);
            setFooterDetails();

            adapter = new ThanksOrderDetail_Adapter(ThanksScreen_Activity.this, orderDetailsList);
            list_orderDetails.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHeaderDetails() {
        try {
            if (thankyouHeaderView != null) {
                // tv_order_confirmation = (TextView) thankyouHeaderView.findViewById(R.id.tv_order_confirmation);
                txt_orderNumber = (TextView) thankyouHeaderView.findViewById(R.id.txt_orderNumber);
                txt_orderDate = (TextView) thankyouHeaderView.findViewById(R.id.txt_orderDate);

                txt_totalAmount = (TextView) thankyouHeaderView.findViewById(R.id.txt_totalAmount);

                txt_deliveryCharge = (TextView) thankyouHeaderView.findViewById(R.id.txt_deliveryCharge);
                txt_orderAmount = (TextView) thankyouHeaderView.findViewById(R.id.txt_orderAmount);

                txt_deliveryStatus = (TextView) thankyouHeaderView.findViewById(R.id.txt_deliveryStatus);

                btn_shopMore = (Button) thankyouHeaderView.findViewById(R.id.btn_shopMore);

                btn_shopMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToHome();
                    }
                });

                txt_orderNumber.setText("" + orderDetailsList.get(0).get("OrderNo"));
                //   txt_orderDate.setText("" + AppUtils.getDateandTimeFromAPIDate(orderDetailsList.get(0).get("OrderDate")));
                txt_orderDate.setText("" + AppUtils.getDateFromAPIDate(orderDetailsList.get(0).get("OrderDate")));

                txt_totalAmount.setText(Html.fromHtml("&#8377 " + orderDetailsList.get(0).get("OrderAmt") + "/-"));
                txt_deliveryCharge.setText(Html.fromHtml("&#8377 " + orderDetailsList.get(0).get("Shipping") + "/-"));
                // txt_orderAmount.setText(Html.fromHtml("&#8377 " + orderDetailsList.get(0).get("OrderAmt") + "/-"));

                txt_deliveryStatus.setText(orderDetailsList.get(0).get("OrderStatus"));

                if (orderDetailsList.get(0).get("OrderStatus").contains("Pending"))
                    txt_deliveryStatus.setTextColor(getResources().getColor(R.color.app_color_red));
                else
                    txt_deliveryStatus.setTextColor(getResources().getColor(R.color.companyLight));

                list_orderDetails.addHeaderView(thankyouHeaderView, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFooterDetails() {
        try {
            if (thankyouFooterView != null) {
                txt_name = (TextView) thankyouFooterView.findViewById(R.id.txt_name);
                txt_address = (TextView) thankyouFooterView.findViewById(R.id.txt_address);
                txt_mobNo = (TextView) thankyouFooterView.findViewById(R.id.txt_mobNo);
                txt_email = (TextView) thankyouFooterView.findViewById(R.id.txt_email);

                txt_name.setText(WordUtils.capitalizeFully(orderDetailsList.get(0).get("DeliveryAddressName")));
                txt_address.setText(WordUtils.capitalizeFully(orderDetailsList.get(0).get("address")+", "+orderDetailsList.get(0).get("City")+", "+orderDetailsList.get(0).get("StateName")+", "+orderDetailsList.get(0).get("PinCode")));
                txt_mobNo.setText("MobileNo : " + WordUtils.capitalizeFully(orderDetailsList.get(0).get("Mobl")));
                txt_email.setText("Email : " + WordUtils.capitalizeFully(orderDetailsList.get(0).get("EMail")));

                // tv_order_confirmation.setText("Dear "+ txt_name.getText().toString()+" , thanks for shopping with. We have received your order "+txt_orderNumber.getText().toString()+" amounting to INR "+ txt_totalAmount.getText().toString()  +" . We will contact you shortly");

                list_orderDetails.addFooterView(thankyouFooterView, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoDataLayout() {
        try {
            layout_listView.setVisibility(View.GONE);
            layout_noData.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveToHome() {
        try {
            AppController.selectedProductsList.clear();

            Intent intent = new Intent(ThanksScreen_Activity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (AppUtils.showLogs) Log.v(TAG, "onKeyDown key called.....");
            moveToHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (AppUtils.showLogs) Log.v(TAG, "onDestroy() called.....");
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ThanksScreen_Activity.this);
        }
    }

    private void executeEncryptPayoutNoRequest(final String payoutno) {
        try {
            if (AppUtils.isNetworkAvailable(ThanksScreen_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ThanksScreen_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("BillNo", payoutno));
                            response = AppUtils.callWebServiceWithMultiParam(ThanksScreen_Activity.this, postParameters, QueryUtils.methodToWebEncryption, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ThanksScreen_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                String invoice_no = jsonObject.getString("Message") ;
                                String URL = SPUtils.productImageURL + invoice_no ;
                                startActivity(new Intent(ThanksScreen_Activity.this, Payment_Activity.class).putExtra("URL", URL));
                                finish();

                                //   executeEncryptFormNoRequest(jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(ThanksScreen_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ThanksScreen_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ThanksScreen_Activity.this);
        }
    }
}

