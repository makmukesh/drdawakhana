package com.vpipl.drdawakhana;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vpipl.drdawakhana.Adapters.Category_Level1_list_Adapter;
import com.vpipl.drdawakhana.Adapters.ProductListGrid_Adapter;
import com.vpipl.drdawakhana.firebase.Config;
import com.vpipl.drdawakhana.model.ProductsList;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
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

public class CategoryLevel1ListActivity extends Activity {
    private String TAG = "CategoryLevel1ListActivity";

    Activity act;
    LinearLayout img_nav_back;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    public Category_Level1_list_Adapter cate_adapter;
    RecyclerView recyclerView ,rv_product_listView;
    LinearLayout ll_cate_data_found, ll_data_found, ll_no_data_found;
    public static ArrayList<HashMap<String, String>> array_list = new ArrayList<>();

    public static ArrayList<String> selectedWidthIds = new ArrayList<>();
    public static ArrayList<String> selectedHeightIds = new ArrayList<>();
    public static ArrayList<String> selectedMaterialIds = new ArrayList<>();
    public static ArrayList<String> selectedColorIds = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> WidthList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> HeightList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> MaterialList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> ColorList = new ArrayList<>();

    LinearLayout  layout_sort, layout_filter;

    GridView gridView_products;
    List<ProductsList> productList = new ArrayList<>();
    ProductListGrid_Adapter adapter;
    int checkedRadioButton = 0;
    BottomSheetDialog mBottomSheetDialog = null;

    int pageIndex = 1;
    int NumOfRows = 16;
    Boolean isLoadMore = false;
    int NoofProduct = 0;
    TextView txt_productHeading;
    JSONObject MAsterData = new JSONObject();
    RelativeLayout ll_home_bo_categories, ll_home_bo_wishlist, ll_home_bo_profile, ll_home_bo_cart, ll_home_bo_home;
    static ImageBadgeView mukesh_begview;

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_category_level1_list);

            act = CategoryLevel1ListActivity.this;
            img_nav_back = findViewById(R.id.img_nav_back);
            mukesh_begview = findViewById(R.id.mukesh_begview);

            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            TextView heading = findViewById(R.id.heading);
            heading.setText("" + getIntent().getStringExtra("Category"));

            db = new DatabaseHandler(this);

          /*  if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
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

            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppController.getSpUserInfo().edit().putString(SPUtils.notification_count, "0").commit();
                    //Toast.makeText(NotificationHistoryActivity.this, AppController.getSpUserInfo().getString(SPUtils.notification_count, "0"), Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(MainActivity.this, AddCartCheckOut_Activity.class));
                }
            });
            mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                   // Log.e("count", AppController.getNotification_count().getString(SPUtils.notification_count, "0"));
                    mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));
                    if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
                        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
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
                }
            };*/

            ll_cate_data_found = findViewById(R.id.ll_cate_data_found);
            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.listView);
            gridView_products = (GridView) findViewById(R.id.gridView_products);

          /*  RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            rv_product_listView.setLayoutManager(mLayoutManager);
            rv_product_listView.setItemAnimator(new DefaultItemAnimator());*/

            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());


            if (AppUtils.isNetworkAvailable(act)) {
                executeCategoryListListRequest();
                executeFilterationRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

            /*Bottom Bar Work Start */

            //ll_home_bo_categories, ll_home_bo_wishlist,ll_home_bo_profile, ll_home_bo_cart, ll_home_bo_home
            ll_home_bo_home = findViewById(R.id.ll_home_bo_home);
            ll_home_bo_categories = findViewById(R.id.ll_home_bo_categories);
            ll_home_bo_wishlist = findViewById(R.id.ll_home_bo_wishlist);
            ll_home_bo_profile = findViewById(R.id.ll_home_bo_profile);
            mukesh_begview = findViewById(R.id.mukesh_begview);

            ll_home_bo_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MainActivity.class));
                }
            });
            ll_home_bo_categories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, CategoryMainListActivity.class));
                }
            });
            ll_home_bo_wishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, Wishlist_Activity.class));
                }
            });
            ll_home_bo_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, UpdateProfile_Activity.class));
                }
            });
            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, AddCartCheckOut_Activity.class));
                }
            });
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setCartItem(){
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
    }

    @Override
    protected void onRestart() {
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

       /* LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));*/
    }

    private void executeCategoryListListRequest() {
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
                            postParameters.add(new BasicNameValuePair("HeadingID", "1"));
                            postParameters.add(new BasicNameValuePair("categoryID", "" + getIntent().getStringExtra("categoryID")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoMenusLevel_1_Subcategory, TAG);
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
                                ll_cate_data_found.setVisibility(View.VISIBLE);

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
                HashMap<String, String> map = new HashMap<>();
                map.put("AId", jsonObject.getString("AId"));
                map.put("SCID", jsonObject.getString("SCID"));
                map.put("CID", jsonObject.getString("CID"));
                map.put("SubCategory", jsonObject.getString("SubCategory"));
                map.put("imgPath", SPUtils.cateImageURL + jsonObject.getString("Image"));
                array_list.add(map);
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
                cate_adapter = new Category_Level1_list_Adapter(act, array_list);
                recyclerView.setAdapter(cate_adapter);
                cate_adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                ll_cate_data_found.setVisibility(View.VISIBLE);
            } else {
                ll_cate_data_found.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    /*Product Listing*/
    private void executeFilterationRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(CategoryLevel1ListActivity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    String Categorycode = "0", Level1CategoryId = "0", Level2CategoryId = "0";
                    if (getIntent().getExtras().getString("Type").equalsIgnoreCase("C")) {
                        Categorycode = getIntent().getExtras().getString("categoryID");
                        Level1CategoryId = "0";
                        Level2CategoryId = "0";
                    } else if (getIntent().getExtras().getString("Type").equalsIgnoreCase("S1")) {
                        Categorycode = "0";
                        Level1CategoryId = getIntent().getExtras().getString("categoryID");
                        Level2CategoryId = "0";
                    } else if (getIntent().getExtras().getString("Type").equalsIgnoreCase("S2")) {
                        Categorycode = "0";
                        Level1CategoryId = "0";
                        Level2CategoryId = getIntent().getExtras().getString("categoryID");
                    }

                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("CategoryId", "" + Categorycode));
                    postParameters.add(new BasicNameValuePair("Level1CategoryId", "" + Level1CategoryId));
                    postParameters.add(new BasicNameValuePair("Level2CategoryId", "" + Level2CategoryId));
                    response = AppUtils.callWebServiceWithMultiParam(CategoryLevel1ListActivity.this, postParameters, QueryUtils.methodToSelectProductByCategory, TAG);
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
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            saveProductsListNew(jsonArrayData);
                        } else {
                            noDataFound();
                        }
                    } else {
                        noDataFound();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void saveProductsListNew(final JSONArray jsonArrayProductList) {
        try {
            if (pageIndex == 1) {
                productList.clear();
            }

            db.clearDB();

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);
                ProductsList products = new ProductsList();
                products.setID("" + jsonObjectProducts.getString("ProdId"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                //  products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setDiscountPer("");
                // products.setIsProductNew("" + jsonObjectProducts.getString("NewDisp"));
                products.setIsProductNew("Y");
                products.setImagePath("" + SPUtils.productImageURL + jsonObjectProducts.getString("ImagePath"));
                products.setNewMRP("" + jsonObjectProducts.getString("MRP"));
                products.setNewDP("" + jsonObjectProducts.getString("DP"));
                products.setCatID("L1");

                productList.add(products);

                db.addProducts(products);
            }

            ColorList.clear();
            WidthList.clear();
            HeightList.clear();
            MaterialList.clear();

            if (isLoadMore) {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                setGridViewData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(CategoryLevel1ListActivity.this);
        }
    }
    private void setGridViewData() {
        try {
            ll_no_data_found.setVisibility(View.GONE);
            gridView_products.setVisibility(View.VISIBLE);

            adapter = new ProductListGrid_Adapter(CategoryLevel1ListActivity.this, productList ,"Grid");
            gridView_products.setAdapter(adapter);

            gridView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    Intent intent = new Intent(CategoryLevel1ListActivity.this, ProductDetail_Activity.class);
                    intent.putExtra("productID", productList.get(position).getID());
                    startActivity(intent);
                }
            });

            gridView_products.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    int threshold = 1;
                    int count = gridView_products.getCount();
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (gridView_products.getLastVisiblePosition() >= count - threshold) {
                            if (NoofProduct != productList.size()) {
                                if (AppUtils.isNetworkAvailable(CategoryLevel1ListActivity.this)) {
                                    isLoadMore = true;
//                                    pageIndex++;
                                    NumOfRows = NumOfRows + 16;
                                    // executeToGetProductLoadMoreListRequest();
                                } else {
                                    AppUtils.alertDialog(CategoryLevel1ListActivity.this, getResources().getString(R.string.txt_networkAlert));
                                }
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void noDataFound() {
        try {
            ll_data_found.setVisibility(View.GONE);
            ll_no_data_found.setVisibility(View.VISIBLE);
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
            AppUtils.showExceptionDialog(act);
        }
        System.gc();
    }
    public static void setBadge() {
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}