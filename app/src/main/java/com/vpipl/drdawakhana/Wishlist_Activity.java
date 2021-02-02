package com.vpipl.drdawakhana;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vpipl.drdawakhana.Adapters.Wishlist_Adapter;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.model.ProductsList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class Wishlist_Activity extends AppCompatActivity {

    public FrameLayout layout_cartProductList;
    public LinearLayout layout_noData;
    public ListView list_cartProducts;
    public Button btn_startShopping;
    public ImageBadgeView mukesh_begview;
    String TAG = "Wishlist_Activity";
    Wishlist_Adapter adapter;

    public void showEmptyCart() {
        try {
            layout_cartProductList.setVisibility(View.GONE);
            list_cartProducts.setVisibility(View.GONE);
            layout_noData.setVisibility(View.VISIBLE);

            if (AppController.selectedWishList.size() > 0) {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            } else {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DatabaseHandler databaseHandler;

    ImageView img_menu;
    ImageView img_search;
    ImageView img_cart;
    ImageView img_user;

    LinearLayout ll_nav_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishlist_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
            ImageBadgeView mukesh_begview = findViewById(R.id.mukesh_begview);
            TextView heading = findViewById(R.id.heading);

            heading.setText("My Wishlist");
            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

            databaseHandler = new DatabaseHandler(this);

            layout_cartProductList = (FrameLayout) findViewById(R.id.layout_cartProductList);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);
            list_cartProducts = (ListView) findViewById(R.id.list_cartProducts);

            btn_startShopping = (Button) findViewById(R.id.btn_startShopping);

            btn_startShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    Intent intent = new Intent(Wishlist_Activity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

            executeToGetWishListItemList();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wishlist_Activity.this);
        }
    }

    private void executeToGetWishListItemList() {
        try {
            if (AppUtils.isNetworkAvailable(Wishlist_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wishlist_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Wishlist_Activity.this, postParameters, QueryUtils.methodToGetWishListProductDetail, TAG);
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
                                JSONArray jsonArrayMainCart = jsonObject.getJSONArray("Data");
                                if (jsonArrayMainCart.length() > 0) {
                                    saveGetWishlistItems(jsonObject);
                                }
                            } else {
                                AppUtils.alertDialog(Wishlist_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wishlist_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wishlist_Activity.this);
        }
    }

    private void saveGetWishlistItems(JSONObject jsonObject) {
        try {

            databaseHandler.clearWishlist();

            AppController.selectedWishList.clear();

            JSONArray jsonArrayMainCart = jsonObject.getJSONArray("Data");

            if (jsonArrayMainCart.length() > 0) {

                for (int i = 0; i < jsonArrayMainCart.length(); i++) {
                    JSONObject jsonObjectMainCart = jsonArrayMainCart.getJSONObject(i);

                    ProductsList selectedProduct = new ProductsList();

                    selectedProduct.setID("" + jsonObjectMainCart.getString("ProductID"));
                    selectedProduct.setName("" + jsonObjectMainCart.getString("ProductName"));
                    selectedProduct.setNewMRP("" + jsonObjectMainCart.getString("MRP"));

                    selectedProduct.setNewDP("" + jsonObjectMainCart.getString("NewDP"));

                    selectedProduct.setQty("" + jsonObjectMainCart.getString("Quantity"));
                    selectedProduct.setShipCharge("" + jsonObjectMainCart.getString("ShipCharge"));

                    selectedProduct.setImagePath(SPUtils.productImageURL + jsonObjectMainCart.getString("ImageURL").replace("\\", ""));
                    selectedProduct.setUID("" + jsonObjectMainCart.getString("UID"));

                    selectedProduct.setSelectedSizeId("" + jsonObjectMainCart.getString("Size"));
                    selectedProduct.setselectedColorId("" + jsonObjectMainCart.getString("Color"));

                    String sizename = "";
                    for (int ii = 0; ii < AppController.SizeList.size(); ii++) {
                        if (jsonObjectMainCart.getString("Size").equalsIgnoreCase(AppController.SizeList.get(ii).get("SizeId"))) {
                            sizename = AppController.SizeList.get(ii).get("Size");
                        }
                    }
                    selectedProduct.setSelectedSizeName(sizename);

                    String colorname = "";
                    for (int ii = 0; ii < AppController.ColorList.size(); ii++) {
                        if (jsonObjectMainCart.getString("Color").equalsIgnoreCase(AppController.ColorList.get(ii).get("colorid"))) {
                            colorname = AppController.ColorList.get(ii).get("colorname");
                        }
                    }

                    selectedProduct.setselectedColorName(colorname);

                    databaseHandler.addProductsWishlist(selectedProduct);
                }
            }

            AppController.selectedWishList = databaseHandler.getAllProductsWishlist();

            setProductSelectedWishList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProductSelectedWishList() {
        try {
            if (AppController.selectedWishList.size() > 0) {
                adapter = new Wishlist_Adapter(Wishlist_Activity.this);
                list_cartProducts.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                layout_cartProductList.setVisibility(View.VISIBLE);
                list_cartProducts.setVisibility(View.VISIBLE);
                layout_noData.setVisibility(View.GONE);

                if (AppController.selectedWishList.size() > 0) {
                    if (img_cart != null)
                        img_cart.setVisibility(View.GONE);
                } else {
                    if (img_cart != null)
                        img_cart.setVisibility(View.GONE);
                }

                showCartData();

            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCartData() {
        try {
            layout_cartProductList.setVisibility(View.VISIBLE);
            list_cartProducts.setVisibility(View.VISIBLE);
            layout_noData.setVisibility(View.GONE);

            if (AppController.selectedWishList.size() > 0) {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            } else {
                if (img_cart != null)
                    img_cart.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

            if (AppController.selectedWishList.size() > 0) {
                setProductSelectedWishList();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

            if (AppController.selectedWishList.size() > 0) {
                setProductSelectedWishList();
            } else {
                showEmptyCart();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToWishlistClearAll() {
        try {
            if (AppUtils.isNetworkAvailable(Wishlist_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wishlist_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("OrderByFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID, "")));
                            postParameters.add(new BasicNameValuePair("IMEINo", "" + AppUtils.getDeviceId(Wishlist_Activity.this)));
                            response = AppUtils.callWebServiceWithMultiParam(Wishlist_Activity.this, postParameters, QueryUtils.methodToDeleteFromWishlistAllProduct, TAG);
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
                                cartClearAll();
                                AppUtils.alertDialog(Wishlist_Activity.this, jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Wishlist_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wishlist_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wishlist_Activity.this);
        }
    }

    private void cartClearAll() {
        try {
            databaseHandler.clearWishlist();
            AppController.selectedWishList.clear();
            showEmptyCart();
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
            AppUtils.showExceptionDialog(Wishlist_Activity.this);
        }
    }


    private void executeToDeleteProduct(final int position) {
        try {
            if (AppUtils.isNetworkAvailable(Wishlist_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Wishlist_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("OrderByFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID, "")));
                            postParameters.add(new BasicNameValuePair("ProductId", "" + AppController.selectedWishList.get(position).getID().trim().replace(",", " ")));
                            postParameters.add(new BasicNameValuePair("IMEINo", "" + AppUtils.getDeviceId(Wishlist_Activity.this)));
                            response = AppUtils.callWebServiceWithMultiParam(Wishlist_Activity.this, postParameters, QueryUtils.methodToDeleteFromWishlistProduct, TAG);
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
                                deleteProduct(position);
                            } else {
                                AppUtils.alertDialog(Wishlist_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Wishlist_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Wishlist_Activity.this);
        }
    }

    private void deleteProduct(final int position) {
        try {

            AppController.selectedWishList.remove(position);

            if (AppController.selectedWishList.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                showEmptyCart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}