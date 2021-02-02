package com.vpipl.drdawakhana;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.test.pg.secure.pgsdkv4.PGConstants;
import com.test.pg.secure.pgsdkv4.PaymentGatewayPaymentInitializer;
import com.test.pg.secure.pgsdkv4.PaymentParams;
import com.vpipl.drdawakhana.Adapters.CheckoutToPay_Adapter;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.Utils.SampleAppConstants;
import com.vpipl.drdawakhana.model.ProductsList;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ru.nikartm.support.ImageBadgeView;

public class CheckoutToPay_Activity extends AppCompatActivity {

    public static String TAG = "CheckoutToPay_Activity";
    public static ArrayList<HashMap<String, String>> deliveryAddressList = new ArrayList<>();
    public static String addressListPosition = "0";

    public LinearLayout layout_cartProductList;
    public LinearLayout layout_noData;
    public ListView list_cartProducts;
    public TextView txt_addressChange, txt_name, txt_address, txt_mobNo;
    public TextView txt_subTotalAmount, txt_deliveryCharge, txt_netpayable, txt_discount, txtCodcharges, txt_codchargelabel;
    RadioGroup rg_paymode;
    RadioButton rb_cod, rb_online;
    LinearLayout ll_payment_cod, ll_payment_online;

    public Button btn_activity_checkout, btn_startShopping;
    public Button btn_payNow, btn_shop_more;
    public CheckoutToPay_Adapter adapter;
    public ViewGroup addressHeaderView = null;
    public ViewGroup addressFooterView = null;

    String ComesFrom = "Other";
    String paymentMode = "Cash On Delivery";
    String paymentModeShort = "COD";

    ImageView img_menu;
    ImageView img_search;
    ImageView img_cart;
    ImageView img_user;

    public String calculateSelectedProductTotalAmount() {
        double amount = 0.0d;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                double countAmount = 0.0d;
                countAmount = ((Double.parseDouble(AppController.selectedProductsList.get(i).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(i).getQty())));
                amount = amount + countAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ((int) amount) + "";
    }

    public String calculateSelectedProductTotalShipCharge() {
        double amount = 0d;
        try {
            Log.e("MembershipStatus" , "" + AppController.getSpUserInfo().getString(SPUtils.USER_MembershipStatus, ""));
            if (!AppController.getSpUserInfo().getString(SPUtils.USER_MembershipStatus, "").equalsIgnoreCase("Y")) {
                double total_amount = Double.parseDouble(calculateSelectedProductTotalAmount());
                // double d_Min_ship_amt = Double.parseDouble(AppController.getSpUserInfo().getString(SPUtils.Min_ship_amt, "0"));
                // double d_Ship_per = Double.parseDouble(AppController.getSpUserInfo().getString(SPUtils.Ship_per, "0"));
                double d_Min_ship_amt = Double.parseDouble(AppController.getShipping_Amt().getString(SPUtils.USER_Shipping_Amount, "0"));
                double d_Ship_charge = Double.parseDouble(AppController.getShipping_Charge().getString(SPUtils.USER_Shipping_Charge, "0"));

                if (total_amount <= d_Min_ship_amt) {
                    //  amount = (total_amount * d_Ship_per) / 100;
                    amount = d_Ship_charge;
                } else {
                    amount = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (amount) + "";
    }

    public String calculateSelectedProductTotalNetPayable() {
        double amount = 0.0d;
        try {
           /* if (!paymentMode.equalsIgnoreCase("Online Payment")) {
                amount = (Double.parseDouble(calculateSelectedProductTotalAmount()) + Double.parseDouble(calculateSelectedProductTotalShipCharge()) + Double.parseDouble(calculateSelectedProductTotalCODCharge()));
            } else {*/
            amount = (Double.parseDouble(calculateSelectedProductTotalAmount()) + Double.parseDouble(calculateSelectedProductTotalShipCharge()) - Double.parseDouble(calculateSelectedProductTotalDiscount()));
            //}

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ((int) amount) + "";
    }

    public String calculateSelectedProductTotalQty() {
        int qty = 0;
        try {
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                qty = qty + (Integer.parseInt(AppController.selectedProductsList.get(i).getQty()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return qty + "";
    }

    public String calculateSelectedProductTotalCODCharge() {
        int qty = 0;
        double amount = 0;

//        if (!paymentMode.equalsIgnoreCase("Online Payment")) {
//            try {
//                for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
//                    qty = qty + (Integer.parseInt(AppController.selectedProductsList.get(i).getQty()));
//                }
//
//                amount = qty * 45;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return amount + "";
    }

    public String calculateSelectedProductTotalDiscount() {

        double amount = 0d;
        if (paymentMode.equalsIgnoreCase("Online Payment")) {
            double total_amount = Double.parseDouble(calculateSelectedProductTotalAmount());
            // amount = (total_amount * 10) / 100;
        }
        return (amount) + "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkouttopay_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            if (getIntent().getExtras() != null) {
                ComesFrom = getIntent().getStringExtra("COMESFROM");
            }

            layout_cartProductList = (LinearLayout) findViewById(R.id.layout_cartProductList);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
            list_cartProducts = (ListView) findViewById(R.id.list_cartProducts);

            btn_startShopping = (Button) findViewById(R.id.btn_startShopping);
            btn_activity_checkout = (Button) findViewById(R.id.btn_activity_checkout);

            btn_startShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(CheckoutToPay_Activity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });
            btn_activity_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (paymentModeShort.equalsIgnoreCase("")) {
                        AppUtils.alertDialog(CheckoutToPay_Activity.this, "Please Select Pay Mode");
                    } else {
                        showPaymentConfirmationDialog();
                    }
                }
            });
            executeToGetAddressesList();

            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }

    private void executeToGetAddressesList() {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(CheckoutToPay_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("UserType", "N"));
                            response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToGetCheckOutDeliveryAddress, TAG);
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
                            deliveryAddressList.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                                if (jsonArrayData.length() > 0) {
                                    saveDeliveryAddressInfo(jsonArrayData);
                                } else {
                                    String msz = jsonObject.getString("Message");

                                    if (msz.contains("No Address Found,Please Add New Address")) {
                                        ShowDialog(msz);
                                    }
                                    else {
                                        AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                                    }
                                }
                            } else {
                                AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(CheckoutToPay_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }

    private void saveDeliveryAddressInfo(JSONArray jsonArrayData) {
        try {
            deliveryAddressList.clear();
            for (int i = 0; i < jsonArrayData.length(); i++) {
                JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ID", "" + jsonObject.getString("ID"));
                map.put("MemFirstName", "" + jsonObject.getString("MemFirstName"));
                map.put("MemLastName", "" + jsonObject.getString("MemLastName"));
                map.put("Address1", "" + jsonObject.getString("Address1"));
                map.put("Address2", "" + jsonObject.getString("Address2"));
                map.put("CountryID", "" + jsonObject.getString("CountryID"));
                map.put("CountryName", "" + jsonObject.getString("CountryName"));
                map.put("StateCode", "" + jsonObject.getString("StateCode"));
                map.put("StateName", "" + jsonObject.getString("StateName"));
                map.put("District", "" + jsonObject.getString("District"));
                map.put("City", "" + jsonObject.getString("City"));
                map.put("PinCode", "" + jsonObject.getString("PinCode"));
                map.put("Email", "" + jsonObject.getString("MailID"));
                map.put("Mobl", "" + jsonObject.getString("Mobl"));
                map.put("EntryType", "" + jsonObject.getString("EntryType"));
                map.put("Address", "" + jsonObject.getString("Address").replace("&nbsp;", " "));
                if (AppUtils.showLogs)
                    Log.e(TAG, "Address..." + jsonObject.getString("Address").replace("&nbsp;", " "));
                deliveryAddressList.add(map);
            }
            if (AppUtils.showLogs) Log.e(TAG, "deliveryAddressList..." + deliveryAddressList);


            if (AppController.selectedProductsList.size() > 0) {
                setProductSelectedCartList();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEmptyCart() {
        try {
            layout_cartProductList.setVisibility(View.GONE);
            list_cartProducts.setVisibility(View.GONE);
            layout_noData.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProductSelectedCartList() {
        try {
            if (AppController.selectedProductsList.size() > 0) {
                if (deliveryAddressList.size() > 0) {

                    layout_cartProductList.setVisibility(View.VISIBLE);
                    list_cartProducts.setVisibility(View.VISIBLE);
                    layout_noData.setVisibility(View.GONE);

                    if (list_cartProducts.getHeaderViewsCount() > 0) {
                        list_cartProducts.removeHeaderView(addressHeaderView);
                    }

                    if (list_cartProducts.getFooterViewsCount() > 0) {
                        list_cartProducts.removeFooterView(addressFooterView);
                    }

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    addressHeaderView = (ViewGroup) inflater.inflate(R.layout.addcartlist_header, list_cartProducts, false);
                    addressFooterView = (ViewGroup) inflater.inflate(R.layout.addcartlist_footer, list_cartProducts, false);

                    list_cartProducts.addHeaderView(addressHeaderView, null, false);
                    list_cartProducts.addFooterView(addressFooterView, null, false);
                   // list_cartProducts.addHeaderView(addressFooterView, null, false);

                    setHeaderDetails();

                    adapter = new CheckoutToPay_Adapter(CheckoutToPay_Activity.this);
                    list_cartProducts.setAdapter(adapter);
                } else {
                    showEmptyCart();
                }
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHeaderDetails() {
        try {
            if (addressHeaderView != null) {
                txt_addressChange = (TextView) addressHeaderView.findViewById(R.id.txt_addressChange);
                txt_addressChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(CheckoutToPay_Activity.this, ChangeDeliveryAddress_Activity.class));
                    }
                });

                txt_name = (TextView) addressHeaderView.findViewById(R.id.txt_name);
                txt_address = (TextView) addressHeaderView.findViewById(R.id.txt_address);
                txt_mobNo = (TextView) addressHeaderView.findViewById(R.id.txt_mobNo);

                txt_subTotalAmount = (TextView) addressFooterView.findViewById(R.id.txt_subTotalAmount);
                txt_deliveryCharge = (TextView) addressFooterView.findViewById(R.id.txt_deliveryCharge);
                txt_netpayable = (TextView) addressFooterView.findViewById(R.id.txt_netpayable);
                txt_discount = (TextView) addressFooterView.findViewById(R.id.txt_discount);
                txtCodcharges = (TextView) addressFooterView.findViewById(R.id.txt_codCharge);
                txt_codchargelabel = (TextView) addressFooterView.findViewById(R.id.txt_codchargelabel);

                rg_paymode = (RadioGroup) addressFooterView.findViewById(R.id.rg_paymode);
                rb_cod = (RadioButton) addressFooterView.findViewById(R.id.rb_cod);
                rb_online = (RadioButton) addressFooterView.findViewById(R.id.rb_online);

                btn_shop_more = (Button) addressFooterView.findViewById(R.id.btn_shop_more);
                btn_payNow = (Button) addressFooterView.findViewById(R.id.btn_checkout);
                ll_payment_cod = (LinearLayout) addressFooterView.findViewById(R.id.ll_payment_cod);
                ll_payment_online = (LinearLayout) addressFooterView.findViewById(R.id.ll_payment_online);
                final RadioButton rb_pay_cod = (RadioButton) addressFooterView.findViewById(R.id.rb_pay_cod);
                final RadioButton rb_pay_online = (RadioButton) addressFooterView.findViewById(R.id.rb_pay_online);

                txt_subTotalAmount.setText("\u20B9 " + Html.fromHtml(calculateSelectedProductTotalAmount()));
                txt_deliveryCharge.setText("+ ₹ " + Html.fromHtml(calculateSelectedProductTotalShipCharge()));
                //txtCodcharges.setText("- ₹ " + Html.fromHtml(calculateSelectedProductTotalDiscount()));
                txt_discount.setText("- ₹ " + Html.fromHtml(calculateSelectedProductTotalDiscount()));
                txt_netpayable.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));

                btn_shop_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CheckoutToPay_Activity.this, MainActivity.class));
                    }
                });

                btn_payNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (paymentModeShort.equalsIgnoreCase("")) {
                            AppUtils.alertDialog(CheckoutToPay_Activity.this, "Please Select Pay Mode");
                        } else {
                            showPaymentConfirmationDialog();
                        }

                    }
                });


               /* mukesh_begview = findViewById(R.id.mukesh_begview);
                mukesh_begview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(CheckoutToPay_Activity.this, .AddCartCheckOut_Activity.class));
                    }
                });
                mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());*/

                rg_paymode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButtonTwo = (RadioButton) findViewById(checkedId);
                        String view_detail_side = radioButtonTwo.getText().toString();

//                        if (view_detail_side.equalsIgnoreCase("Cash On Delivery")) {
//
//                            paymentMode = view_detail_side;
//
//                            txt_codchargelabel.setText("COD Charges");
//                            txtCodcharges.setText("+ ₹ " + Html.fromHtml(calculateSelectedProductTotalCODCharge()));
//                            txt_netpayable.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));
//
//                        } else {
//                            paymentMode = view_detail_side;

                        txt_codchargelabel.setText("Online Pay Discount");
                        txtCodcharges.setText("- ₹ " + Html.fromHtml(calculateSelectedProductTotalDiscount()));
                        txt_netpayable.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));
//                        }

                    }
                });
                txtCodcharges.setText("- ₹ " + Html.fromHtml(calculateSelectedProductTotalDiscount()));
                txt_netpayable.setText("₹ " + Html.fromHtml(calculateSelectedProductTotalNetPayable()));
                ll_payment_cod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paymentMode = "Cash On Delivery";
                        paymentModeShort = "COD";
                        rb_pay_cod.setChecked(true);
                        rb_pay_online.setChecked(false);
                    }
                });
                ll_payment_online.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paymentMode = "Online Payment";
                        paymentModeShort = "O";
                        rb_pay_cod.setChecked(false);
                        rb_pay_online.setChecked(true);
                    }
                });
                rb_pay_cod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paymentMode = "Cash On Delivery";
                        paymentModeShort = "COD";
                        rb_pay_cod.setChecked(true);
                        rb_pay_online.setChecked(false);
                    }
                });
                rb_pay_online.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paymentMode = "Online Payment";
                        paymentModeShort = "O";
                        rb_pay_cod.setChecked(false);
                        rb_pay_online.setChecked(true);
                    }
                });

                setAddressValue(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showPaymentConfirmationDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you confirm to place an Order of " + txt_netpayable.getText().toString() + " through " + paymentMode + ". Please click on Confirm to proceed ahead."));
//            txt_DialogTitle.setText(Html.fromHtml("Are you confirm to place an Order of " + txt_netpayable.getText().toString() + " . Please click on Confirm to Order Now."));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Confirm");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        if (paymentModeShort.equalsIgnoreCase("COD")) {
                            startPaymentRequest();
                        } else {
                            startPaymentRequest();
                            // Toast.makeText(CheckoutToPay_Activity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        }
//                        executeToGetAvailablityPinCode(AppController.selectedProductsList.get(0).getDeliveryAddressPinCode());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Cancel");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
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

/*    private void startPaymentRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            JSONArray jsonArrayOrder = new JSONArray();
            JSONArray jsonArrayOrderDetail = new JSONArray();

            JSONObject jsonObjectOrder = new JSONObject();
            jsonObjectOrder.put("MemFirstName", AppController.selectedProductsList.get(0).getDeliveryAddressFirstName().trim());
            jsonObjectOrder.put("MemLasttName", AppController.selectedProductsList.get(0).getDeliveryAddressLastName().trim());
            jsonObjectOrder.put("Address1", AppController.selectedProductsList.get(0).getDeliveryAddress1().trim().replaceAll(",", " "));
            jsonObjectOrder.put("Address2", AppController.selectedProductsList.get(0).getDeliveryAddress2().trim().replaceAll(",", " "));
            jsonObjectOrder.put("StateID", AppController.selectedProductsList.get(0).getDeliveryAddressStateCode().trim().replaceAll(",", " "));
            jsonObjectOrder.put("StateName", AppController.selectedProductsList.get(0).getDeliveryAddressStateName().trim().replaceAll(",", " "));
            jsonObjectOrder.put("District", AppController.selectedProductsList.get(0).getDeliveryAddressDistrict().trim().replaceAll(",", " "));
            jsonObjectOrder.put("UserType", "G");
            jsonObjectOrder.put("IdType", "N");
            jsonObjectOrder.put("PayMode", "" + paymentModeShort);
            jsonObjectOrder.put("Remarks", " ");
            jsonObjectOrder.put("ChDDNo", "0");
            jsonObjectOrder.put("ChDate", "");
            jsonObjectOrder.put("BankName", "");
            jsonObjectOrder.put("BranchName", "");

            jsonObjectOrder.put("TotalDP", calculateSelectedProductTotalAmount().trim().replace(",", " "));
            jsonObjectOrder.put("Item", "" + AppController.selectedProductsList.size());
            jsonObjectOrder.put("TotalQty", calculateSelectedProductTotalQty().trim().replace(",", " "));

            jsonObjectOrder.put("ShipCharge", "" + calculateSelectedProductTotalShipCharge().trim().replaceAll(",", " "));

            jsonObjectOrder.put("color", "" + AppController.selectedProductsList.get(0).getselectedColorName().trim());
            jsonObjectOrder.put("Size", "" + AppController.selectedProductsList.get(0).getSelectedSizeName().trim());
            jsonObjectOrder.put("Pack", "0");
            jsonObjectOrder.put("Packing", "0");
            jsonObjectOrder.put("OrderFor", "");

            jsonObjectOrder.put("City", AppController.selectedProductsList.get(0).getDeliveryAddressCity().trim().replace(",", " "));
            jsonObjectOrder.put("PinCode", AppController.selectedProductsList.get(0).getDeliveryAddressPinCode().trim().replace(",", " "));
            jsonObjectOrder.put("MobileNo", AppController.selectedProductsList.get(0).getDeliveryAddressMob().trim().replace(",", " "));
            jsonObjectOrder.put("Email", AppController.selectedProductsList.get(0).getDeliveryAddressEmail().trim().replace(",", " "));
            jsonObjectOrder.put("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));

            jsonObjectOrder.put("TotalBV", "0");
            jsonObjectOrder.put("TotalPV", "0");

            jsonObjectOrder.put("HostIp", "000000000");

            jsonArrayOrder.put(jsonObjectOrder);

            for (int j = 0; j < AppController.selectedProductsList.size(); j++) {

                JSONObject jsonObjectDetail = new JSONObject();
                jsonObjectDetail.put("Productid", AppController.selectedProductsList.get(j).getID().trim().replace(",", " "));
                jsonObjectDetail.put("ProductName", AppController.selectedProductsList.get(j).getName().trim().replace(",", " "));
                jsonObjectDetail.put("Qty", AppController.selectedProductsList.get(j).getQty().trim().replace(",", " "));
                jsonObjectDetail.put("DP", AppController.selectedProductsList.get(j).getNewDP().trim().replace(",", " "));
                jsonObjectDetail.put("Price", AppController.selectedProductsList.get(j).getNewMRP().trim().replace(",", " "));

                double SubTotal;
                SubTotal = ((Double.parseDouble(AppController.selectedProductsList.get(j).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(j).getQty())));

                jsonObjectDetail.put("SubTotal", "" + ((int) SubTotal));
                jsonObjectDetail.put("colorDetails", "" + AppController.selectedProductsList.get(j).getSelectedColorName().trim());
                jsonObjectDetail.put("SizeDetails", "" + AppController.selectedProductsList.get(j).getSelectedSizeName().trim());
                jsonObjectDetail.put("PackDetails", "0");
                jsonObjectDetail.put("PackingDetails", "0");
                jsonObjectDetail.put("OrderForDetails", "");

                jsonObjectDetail.put("ShipChargeDetails", "" + AppController.selectedProductsList.get(j).getShipCharge().trim().replace(",", " "));

                String Imageurl = AppController.selectedProductsList.get(j).getImagePath().trim().replace(",", " ").replace("\\", "");

                if (Imageurl.contains(SPUtils.productImageURL))
                    jsonObjectDetail.put("ImageUrl", "" + Imageurl.replace(SPUtils.productImageURL, ""));
                else
                    jsonObjectDetail.put("ImageUrl", "" + Imageurl);

                jsonObjectDetail.put("BV", "0");
                jsonObjectDetail.put("PV", "0");
                jsonObjectDetail.put("UID", "0");
                jsonObjectDetail.put("IsKit", "0");
                jsonObjectDetail.put("ProdType", "");
                jsonObjectDetail.put("DiscountPer", "" + AppController.selectedProductsList.get(j).getDiscountPer().trim().replace(",", " ").replace("\\", ""));
                jsonObjectDetail.put("PackingID", "" + AppController.selectedProductsList.get(j).getSelectedOptionId().trim());
                jsonObjectDetail.put("PackingText", "" + AppController.selectedProductsList.get(j).getSelectedOptionName().trim());
                jsonArrayOrderDetail.put(jsonObjectDetail);
            }
            postParameters.add(new BasicNameValuePair("Order", jsonArrayOrder.toString()));
            postParameters.add(new BasicNameValuePair("OrderDetails", "" + jsonArrayOrderDetail.toString()));
            postParameters.add(new BasicNameValuePair("OrderType", "I"));
            executeToMakeOrderPaymentRequest(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void executeToMakeOrderPaymentRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(CheckoutToPay_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToAddOrder, TAG);
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
                                AppController.selectedProductsList.clear();
                                Intent intent = new Intent(CheckoutToPay_Activity.this, ThanksScreen_Activity.class);
                                intent.putExtra("orderDetails", "" + jsonObject.toString());
                                startActivity(intent);
                                finish();
                            } else {
                                AppUtils.alertDialogWithFinish(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(CheckoutToPay_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }*/

    public void setAddressValue(int position) {
        try {
            if (deliveryAddressList.size() > 0) {
                txt_name.setText(WordUtils.capitalizeFully(deliveryAddressList.get(position).get("MemFirstName")));
                txt_address.setText((WordUtils.capitalizeFully(Html.fromHtml(deliveryAddressList.get(position).get("Address")).toString().trim() + ", Mobile : " + deliveryAddressList.get(position).get("Mobl"))).replaceAll(",", " "));
//              txt_mobNo.setText("M : "+ deliveryAddressList.get(position).get("Mobl"));

                if (AppController.selectedProductsList.size() > 0) {
                    ProductsList selectedProduct = new ProductsList();

                    selectedProduct.setID("" + AppController.selectedProductsList.get(0).getID());
                    selectedProduct.setcode("" + AppController.selectedProductsList.get(0).getcode());
                    selectedProduct.setUID("" + AppController.selectedProductsList.get(0).getUID());
                    selectedProduct.setName("" + AppController.selectedProductsList.get(0).getName());
                    selectedProduct.setImagePath("" + AppController.selectedProductsList.get(0).getImagePath());
                    selectedProduct.setNewMRP("" + AppController.selectedProductsList.get(0).getNewMRP());
                    selectedProduct.setNewDP("" + AppController.selectedProductsList.get(0).getNewDP());
                    selectedProduct.setDescription("" + AppController.selectedProductsList.get(0).getDescription());
                    selectedProduct.setDetail("" + AppController.selectedProductsList.get(0).getDetail());
                    selectedProduct.setKeyFeature("" + AppController.selectedProductsList.get(0).getKeyFeature());
                    selectedProduct.setDiscount("" + AppController.selectedProductsList.get(0).getDiscount());
                    selectedProduct.setDiscountPer("" + AppController.selectedProductsList.get(0).getDiscountPer());
                    selectedProduct.setShipCharge("" + AppController.selectedProductsList.get(0).getShipCharge());
                    selectedProduct.setCatID("" + AppController.selectedProductsList.get(0).getCatID());
                    selectedProduct.setRandomNo("" + AppController.selectedProductsList.get(0).getRandomNo());
                    selectedProduct.setQty("" + AppController.selectedProductsList.get(0).getQty());
                    selectedProduct.setBaseQty("" + AppController.selectedProductsList.get(0).getBaseQty());
                    selectedProduct.setIsProductNew("" + AppController.selectedProductsList.get(0).getIsProductNew());
                    selectedProduct.setSelectedSizeId("" + AppController.selectedProductsList.get(0).getSelectedSizeId());
                    selectedProduct.setSelectedSizeName("" + AppController.selectedProductsList.get(0).getSelectedSizeName());
                    selectedProduct.setselectedColorId("" + AppController.selectedProductsList.get(0).getselectedColorId());
                    selectedProduct.setselectedColorName("" + AppController.selectedProductsList.get(0).getselectedColorName());
                    selectedProduct.setSelectedOptionId("" + AppController.selectedProductsList.get(0).getSelectedOptionId());
                    selectedProduct.setSelectedOptionName("" + AppController.selectedProductsList.get(0).getSelectedOptionName());

                    selectedProduct.setDeliveryAddressID("" + deliveryAddressList.get(position).get("ID"));
                    selectedProduct.setDeliveryAddressFirstName("" + deliveryAddressList.get(position).get("MemFirstName"));
                    selectedProduct.setDeliveryAddressLastName("" + deliveryAddressList.get(position).get("MemLastName"));
                    selectedProduct.setDeliveryAddress("" + deliveryAddressList.get(position).get("Address"));
                    selectedProduct.setDeliveryAddress1("" + deliveryAddressList.get(position).get("Address1"));
                    selectedProduct.setDeliveryAddress2("" + deliveryAddressList.get(position).get("Address2"));
                    selectedProduct.setDeliveryAddressCountryID("" + deliveryAddressList.get(position).get("CountryID"));
                    selectedProduct.setDeliveryAddressCountryName("" + deliveryAddressList.get(position).get("CountryName"));
                    selectedProduct.setDeliveryAddressStateCode("" + deliveryAddressList.get(position).get("StateCode"));
                    selectedProduct.setDeliveryAddressStateName("" + deliveryAddressList.get(position).get("StateName"));
                    selectedProduct.setDeliveryAddressDistrict("" + deliveryAddressList.get(position).get("District"));
                    selectedProduct.setDeliveryAddressCity("" + deliveryAddressList.get(position).get("City"));
                    selectedProduct.setDeliveryAddressPinCode("" + deliveryAddressList.get(position).get("PinCode"));
                    selectedProduct.setDeliveryAddressEmail("" + deliveryAddressList.get(position).get("Email"));
                    selectedProduct.setDeliveryAddressMob("" + deliveryAddressList.get(position).get("Mobl"));

                    AppController.selectedProductsList.set(0, selectedProduct);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (adapter != null) {
                setAddressValue(Integer.parseInt(CheckoutToPay_Activity.addressListPosition));
                adapter.notifyDataSetChanged();
                setOptionMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppUtils.dismissProgressDialog();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }

    private void setOptionMenu() {
        try {
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));
            else
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_user_login));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ShowDialog(String message) {
        final Dialog dialog = AppUtils.createDialog(CheckoutToPay_Activity.this, true);
        TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
        dialog4all_txt.setText(message);

        TextView textView = (TextView) dialog.findViewById(R.id.txt_submit);
        textView.setText("Add New Address");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(CheckoutToPay_Activity.this, AddDeliveryAddress_Activity.class);
                intent.putExtra("ComesFrom", "CheckoutToPay_Activity");
                startActivity(intent);
            }
        });
        dialog.show();
    }

    public ImageBadgeView mukesh_begview;

    public void SetupToolbar() {
        LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
        TextView heading = findViewById(R.id.heading);
        heading.setText("Payment Details");
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*Online order api integrations 30-11-2020 11:50 Am*/

    private void startPaymentRequest() {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            JSONArray jsonArrayOrder = new JSONArray();
            JSONArray jsonArrayOrderDetail = new JSONArray();

            JSONObject jsonObjectOrder = new JSONObject();
            jsonObjectOrder.put("MemFirstName", AppController.selectedProductsList.get(0).getDeliveryAddressFirstName().trim());
            jsonObjectOrder.put("MemLasttName", AppController.selectedProductsList.get(0).getDeliveryAddressLastName().trim());
            jsonObjectOrder.put("Address1", AppController.selectedProductsList.get(0).getDeliveryAddress1().trim().replaceAll(",", " "));
            jsonObjectOrder.put("Address2", AppController.selectedProductsList.get(0).getDeliveryAddress2().trim().replaceAll(",", " "));
            jsonObjectOrder.put("StateID", AppController.selectedProductsList.get(0).getDeliveryAddressStateCode().trim().replaceAll(",", " "));
            jsonObjectOrder.put("StateName", AppController.selectedProductsList.get(0).getDeliveryAddressStateName().trim().replaceAll(",", " "));
            jsonObjectOrder.put("District", AppController.selectedProductsList.get(0).getDeliveryAddressDistrict().trim().replaceAll(",", " "));
            jsonObjectOrder.put("UserType", "G");
            jsonObjectOrder.put("IdType", "N");
            jsonObjectOrder.put("PayMode", "" + paymentModeShort);
            jsonObjectOrder.put("Remarks", " ");
            jsonObjectOrder.put("ChDDNo", "0");
            jsonObjectOrder.put("ChDate", "");
            jsonObjectOrder.put("BankName", "");
            jsonObjectOrder.put("BranchName", "");

            jsonObjectOrder.put("TotalDP", calculateSelectedProductTotalAmount().trim().replace(",", " "));
            jsonObjectOrder.put("Item", "" + AppController.selectedProductsList.size());
            jsonObjectOrder.put("TotalQty", calculateSelectedProductTotalQty().trim().replace(",", " "));

            jsonObjectOrder.put("ShipCharge", "" + calculateSelectedProductTotalShipCharge().trim().replaceAll(",", " "));

            jsonObjectOrder.put("color", "" + AppController.selectedProductsList.get(0).getselectedColorName().trim());
            jsonObjectOrder.put("Size", "" + AppController.selectedProductsList.get(0).getSelectedSizeName().trim());
            jsonObjectOrder.put("Pack", "0");
            jsonObjectOrder.put("Packing", "0");
            jsonObjectOrder.put("OrderFor", "");

            jsonObjectOrder.put("City", AppController.selectedProductsList.get(0).getDeliveryAddressCity().trim().replace(",", " "));
            jsonObjectOrder.put("PinCode", AppController.selectedProductsList.get(0).getDeliveryAddressPinCode().trim().replace(",", " "));
            jsonObjectOrder.put("MobileNo", AppController.selectedProductsList.get(0).getDeliveryAddressMob().trim().replace(",", " "));
            jsonObjectOrder.put("Email", AppController.selectedProductsList.get(0).getDeliveryAddressEmail().trim().replace(",", " "));
            jsonObjectOrder.put("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));
            jsonObjectOrder.put("IdNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "").trim().replace(",", " "));

            jsonObjectOrder.put("TotalBV", "0");
            jsonObjectOrder.put("TotalPV", "0");

            jsonObjectOrder.put("HostIp", "000000000");

            jsonArrayOrder.put(jsonObjectOrder);

            for (int j = 0; j < AppController.selectedProductsList.size(); j++) {

                JSONObject jsonObjectDetail = new JSONObject();
                jsonObjectDetail.put("Productid", AppController.selectedProductsList.get(j).getID().trim().replace(",", " "));
                jsonObjectDetail.put("ProductName", AppController.selectedProductsList.get(j).getName().trim().replace(",", " "));
                jsonObjectDetail.put("Qty", AppController.selectedProductsList.get(j).getQty().trim().replace(",", " "));
                jsonObjectDetail.put("DP", AppController.selectedProductsList.get(j).getNewDP().trim().replace(",", " "));
                jsonObjectDetail.put("Price", AppController.selectedProductsList.get(j).getNewMRP().trim().replace(",", " "));

                double SubTotal;
                SubTotal = ((Double.parseDouble(AppController.selectedProductsList.get(j).getNewDP())) * (Double.parseDouble(AppController.selectedProductsList.get(j).getQty())));

                jsonObjectDetail.put("SubTotal", "" + ((int) SubTotal));
                jsonObjectDetail.put("colorDetails", "" + AppController.selectedProductsList.get(j).getSelectedColorName().trim());
                jsonObjectDetail.put("SizeDetails", "" + AppController.selectedProductsList.get(j).getSelectedSizeName().trim());
                jsonObjectDetail.put("PackDetails", "0");
                jsonObjectDetail.put("PackingDetails", "0");
                jsonObjectDetail.put("OrderForDetails", "");

                jsonObjectDetail.put("ShipChargeDetails", "" + AppController.selectedProductsList.get(j).getShipCharge().trim().replace(",", " "));

                String Imageurl = AppController.selectedProductsList.get(j).getImagePath().trim().replace(",", " ").replace("\\", "");

                if (Imageurl.contains(SPUtils.productImageURL))
                    jsonObjectDetail.put("ImageUrl", "" + Imageurl.replace(SPUtils.productImageURL, ""));
                else
                    jsonObjectDetail.put("ImageUrl", "" + Imageurl);

                jsonObjectDetail.put("BV", "0");
                jsonObjectDetail.put("PV", "0");
                jsonObjectDetail.put("UID", "0");
                jsonObjectDetail.put("IsKit", "0");
                jsonObjectDetail.put("ProdType", "");
                jsonObjectDetail.put("DiscountPer", "" + AppController.selectedProductsList.get(j).getDiscountPer().trim().replace(",", " ").replace("\\", ""));
                jsonObjectDetail.put("PackingID", "" + AppController.selectedProductsList.get(j).getSelectedOptionId().trim());
                jsonObjectDetail.put("PackingText", "" + AppController.selectedProductsList.get(j).getSelectedOptionName().trim());
                jsonArrayOrderDetail.put(jsonObjectDetail);
            }

            Random rnd = new Random();
            int n = 100000 + rnd.nextInt(900000);
            SampleAppConstants.PG_ORDER_ID = Integer.toString(n);

            JSONObject jsonObjectOnlineRequest = new JSONObject();
            jsonObjectOnlineRequest.put("API_KEY", "" + SampleAppConstants.PG_API_KEY);
            jsonObjectOnlineRequest.put("AMOUNT", "" + calculateSelectedProductTotalAmount().trim().replace(",", " "));
            jsonObjectOnlineRequest.put("EMAIL", "" + AppController.selectedProductsList.get(0).getDeliveryAddressEmail().trim().replace(",", " "));
            jsonObjectOnlineRequest.put("NAME", "" + AppController.selectedProductsList.get(0).getDeliveryAddressFirstName().trim());
            jsonObjectOnlineRequest.put("PHONE", "" + AppController.selectedProductsList.get(0).getDeliveryAddressMob().trim().replace(",", " "));
            jsonObjectOnlineRequest.put("ORDER_ID", "");
            jsonObjectOnlineRequest.put("CURRENCY", "" + SampleAppConstants.PG_CURRENCY);
            jsonObjectOnlineRequest.put("DESCRIPTION", SampleAppConstants.PG_DESCRIPTION);
            jsonObjectOnlineRequest.put("CITY", "" + AppController.selectedProductsList.get(0).getDeliveryAddressCity().trim().replace(",", " "));
            jsonObjectOnlineRequest.put("STATE", "" + AppController.selectedProductsList.get(0).getDeliveryAddressStateCode().trim().replaceAll(",", " "));
            jsonObjectOnlineRequest.put("ADD_1", "" + AppController.selectedProductsList.get(0).getDeliveryAddress().trim().replaceAll(",", " "));
            jsonObjectOnlineRequest.put("ADD_2", "");
            jsonObjectOnlineRequest.put("ZIPCODE", "" + AppController.selectedProductsList.get(0).getDeliveryAddressPinCode().trim().replace(",", " "));
            jsonObjectOnlineRequest.put("COUNTRY", "" + SampleAppConstants.PG_COUNTRY);
            jsonObjectOnlineRequest.put("RETURN_URL", "" + SampleAppConstants.PG_RETURN_URL);
            jsonObjectOnlineRequest.put("MODE", "" + SampleAppConstants.PG_MODE);
            jsonObjectOnlineRequest.put("UDF1", "" + SampleAppConstants.PG_UDF1);
            jsonObjectOnlineRequest.put("UDF2", "" + SampleAppConstants.PG_UDF2);
            jsonObjectOnlineRequest.put("UDF3", "" + SampleAppConstants.PG_UDF3);
            jsonObjectOnlineRequest.put("UDF4", "" + SampleAppConstants.PG_UDF4);
            jsonObjectOnlineRequest.put("UDF5", "" + SampleAppConstants.PG_UDF5);

            postParameters.add(new BasicNameValuePair("Order", jsonArrayOrder.toString()));
            postParameters.add(new BasicNameValuePair("OrderDetails", "" + jsonArrayOrderDetail.toString()));
            postParameters.add(new BasicNameValuePair("OrderType", "I"));

            if (paymentModeShort.equalsIgnoreCase("O")) {
                postParameters.add(new BasicNameValuePair("Request", "" + jsonObjectOnlineRequest.toString()));
                postParameters.add(new BasicNameValuePair("Device", "" + AppUtils.getDeviceId(CheckoutToPay_Activity.this)));
            }

            executeToMakeOrderPaymentRequest(postParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToMakeOrderPaymentRequest(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(CheckoutToPay_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            if (paymentModeShort.equalsIgnoreCase("O")) {
                                response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToAddOrderOnline, TAG);
                            } else {
                                response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToAddOrder, TAG);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (paymentModeShort.equalsIgnoreCase("O")) {
                                    JSONArray jsonArrayMainOrder = jsonObject.getJSONArray("MainOrder");
                                    JSONObject jsonObjectMainOrder = jsonArrayMainOrder.getJSONObject(0);
                                    OnlinePaymentRedirection(jsonObjectMainOrder.getString("OrderNo").toString());
                                } else {
                                    AppController.selectedProductsList.clear();
                                    Intent intent = new Intent(CheckoutToPay_Activity.this, ThanksScreen_Activity.class);
                                    intent.putExtra("orderDetails", "" + jsonObject.toString());
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                AppUtils.alertDialog(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(CheckoutToPay_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }


    private void OnlinePaymentRedirection(String orderno) {

        try {
            PaymentParams pgPaymentParams = new PaymentParams();
            pgPaymentParams.setAPiKey(SampleAppConstants.PG_API_KEY);
             pgPaymentParams.setAmount(calculateSelectedProductTotalAmount().trim().replace(",", " "));
          //  pgPaymentParams.setAmount("15");
          //  pgPaymentParams.setEmail(AppController.selectedProductsList.get(0).getDeliveryAddressEmail().trim().replace(",", " "));
            pgPaymentParams.setEmail(SampleAppConstants.PG_EMAIL.trim().replace(",", " "));
            pgPaymentParams.setName(AppController.selectedProductsList.get(0).getDeliveryAddressFirstName().trim());
            pgPaymentParams.setPhone(AppController.selectedProductsList.get(0).getDeliveryAddressMob().trim().replace(",", " "));
            pgPaymentParams.setOrderId(orderno);
            pgPaymentParams.setCurrency(SampleAppConstants.PG_CURRENCY);
            // pgPaymentParams.setDescription(edtxt_remarks_online.getText().toString().replaceAll(",", " "));
            pgPaymentParams.setDescription(SampleAppConstants.PG_DESCRIPTION);
            pgPaymentParams.setCity(AppController.selectedProductsList.get(0).getDeliveryAddressCity().trim().replace(",", " "));
            pgPaymentParams.setState(AppController.selectedProductsList.get(0).getDeliveryAddressStateName().trim().replaceAll(",", " "));
            pgPaymentParams.setAddressLine1(AppController.selectedProductsList.get(0).getDeliveryAddress().trim().replaceAll(",", " "));
            pgPaymentParams.setAddressLine2(SampleAppConstants.PG_ADD_2);
            pgPaymentParams.setZipCode(AppController.selectedProductsList.get(0).getDeliveryAddressPinCode().trim().replace(",", " "));
            pgPaymentParams.setCountry(SampleAppConstants.PG_COUNTRY);
            pgPaymentParams.setReturnUrl(SampleAppConstants.PG_RETURN_URL);
            pgPaymentParams.setMode(SampleAppConstants.PG_MODE);
            pgPaymentParams.setUdf1(SampleAppConstants.PG_UDF1);
            pgPaymentParams.setUdf2(SampleAppConstants.PG_UDF2);
            pgPaymentParams.setUdf3(SampleAppConstants.PG_UDF3);
            pgPaymentParams.setUdf4(SampleAppConstants.PG_UDF4);
            pgPaymentParams.setUdf5(SampleAppConstants.PG_UDF5);

            PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams, CheckoutToPay_Activity.this);
            pgPaymentInitialzer.initiatePaymentProcess();

            AppController.selectedProductsList.clear();
        } catch (Exception e) {
            Log.e("Paymentgateway" , e.getMessage());
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

                        //   Toast.makeText(this, "Transaction ID: NIL", Toast.LENGTH_SHORT).show();
                        //  Toast.makeText(this, "Transaction Status: Transaction Error!", Toast.LENGTH_SHORT).show();
                        //    transactionIdView.setText("Transaction ID: NIL");
                        //   transactionStatusView.setText("Transaction Status: Transaction Error!");
                        //  startActivity(new Intent(CheckoutToPay_Activity.this , Home_Activity.class));
                        //  Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();

                        AppUtils.alertDialogWithFinishHome(CheckoutToPay_Activity.this, "Transaction Error!");

                    } else {
                        JSONObject response = new JSONObject(paymentResponse);
                        startAfterPaymentRequest(response);
                        /*if (response.getString("response_code").equalsIgnoreCase("0")) {
                            startAfterPaymentRequest(response);
                        } else {
                            AppUtils.alertDialogWithFinishHome(CheckoutToPay_Activity.this, response.getString("response_message"));
                        }*/
                        //   Toast.makeText(this, "Transaction ID: NIL" + response.getString("transaction_id"), Toast.LENGTH_SHORT).show();
                        //   Toast.makeText(this, "Transaction Status: Transaction Error!" + response.getString("response_message") , Toast.LENGTH_SHORT).show();
                        //   startActivity(new Intent(CheckoutToPay_Activity.this , Home_Activity.class));
                        //  Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        //  transactionIdView.setText("Transaction ID: "+response.getString("transaction_id"));
                        // transactionStatusView.setText("Transaction Status: "+response.getString("response_message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(CheckoutToPay_Activity.this, "Payment Cancel", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void startAfterPaymentRequest(JSONObject onlineresponse) {
        try {
            List<NameValuePair> postParameters = new ArrayList<>();
// FullRes:	            amount:            bank_ref_num:            bankcode:            email:            encryptedPaymentId:            response_code:
// response_Message:           OrderNo:            TxnAmount:            ClientCode:            BankRefNo:            Message:            firstname:            mode:
//  payuMoneyId:            phone:            status:            txnid:

            postParameters.add(new BasicNameValuePair("FullRes", "" + onlineresponse.toString()));
            postParameters.add(new BasicNameValuePair("amount", "" + onlineresponse.getString("amount")));
            postParameters.add(new BasicNameValuePair("bank_ref_num", "" + onlineresponse.getString("payment_channel")));
            postParameters.add(new BasicNameValuePair("bankcode", ""));
            postParameters.add(new BasicNameValuePair("email", "" + onlineresponse.getString("email")));
            postParameters.add(new BasicNameValuePair("encryptedPaymentId", ""));
            postParameters.add(new BasicNameValuePair("response_code", "" + onlineresponse.getString("response_code")));
            postParameters.add(new BasicNameValuePair("response_Message", "" + onlineresponse.getString("response_message")));
            postParameters.add(new BasicNameValuePair("OrderNo", "" + onlineresponse.getString("order_id")));
            postParameters.add(new BasicNameValuePair("TxnAmount", "" + onlineresponse.getString("amount")));
            postParameters.add(new BasicNameValuePair("ClientCode", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
            postParameters.add(new BasicNameValuePair("BankRefNo", ""));
            postParameters.add(new BasicNameValuePair("Message", ""));
            postParameters.add(new BasicNameValuePair("firstname", ""));
            postParameters.add(new BasicNameValuePair("mode", ""));
            postParameters.add(new BasicNameValuePair("payuMoneyId", ""));
            postParameters.add(new BasicNameValuePair("phone", "" + onlineresponse.getString("phone")));
            postParameters.add(new BasicNameValuePair("status", ""));
            postParameters.add(new BasicNameValuePair("txnid", ""));

            executeToAfterOnlinePaymentRequest(postParameters, onlineresponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToAfterOnlinePaymentRequest(final List<NameValuePair> postParameters, final JSONObject onlineafterresponse) {
        try {
            if (AppUtils.isNetworkAvailable(CheckoutToPay_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(CheckoutToPay_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(CheckoutToPay_Activity.this, postParameters, QueryUtils.methodToOnlineOrderAfterSucess, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (onlineafterresponse.getString("response_code").equalsIgnoreCase("0")) {
                                    AppController.selectedProductsList.clear();
                                    Intent intent = new Intent(CheckoutToPay_Activity.this, ThanksScreen_Activity.class);
                                    intent.putExtra("orderDetails", "" + jsonObject.toString());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    /*Intent intent = new Intent(CheckoutToPay_Activity.this, Order_Failed_Activity.class);
                                    intent.putExtra("orderid", "" + onlineafterresponse.getString("AGENTID"));
                                    intent.putExtra("orderamount", "" + onlineafterresponse.getString("amount"));
                                    intent.putExtra("orderstatus", "" + onlineafterresponse.getString("response_message"));
                                    intent.putExtra("paymentdatetime", "" + onlineafterresponse.getString("payment_datetime"));
                                    startActivity(intent);
                                    finish();*/
                                    AppUtils.alertDialogWithFinish(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialogWithFinishHome(CheckoutToPay_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(CheckoutToPay_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CheckoutToPay_Activity.this);
        }
    }
}
