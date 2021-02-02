package com.vpipl.drdawakhana;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vpipl.drdawakhana.Adapters.ProductListGrid_Adapter;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.BadgeDrawable;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.model.FilterList2CheckBox;
import com.vpipl.drdawakhana.model.ProductsList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class SearchProducts_Activity extends AppCompatActivity {

    public static String TAG = "SearchProducts_Activity";

    LinearLayout layout_noData, layout_sort;
    public static ImageBadgeView mukesh_begview;
    GridView gridView_products;
    List<ProductsList> productList = new ArrayList<>();
    ProductListGrid_Adapter adapter;
    String isDisplayView = "Grid";
    TextView txt_productHeading;
    BottomSheetDialog mBottomSheetDialog = null;
    int checkedRadioButton = 0;
    int pageIndex = 1;
    int NumOfRows = 16;
    Boolean isLoadMore = false;
    String Keyword = "";
    TextView heading;

    ImageView img_menu, img_search, img_cart, img_user;

    public void SetupToolbar() {
        LinearLayout img_nav_back =  findViewById(R.id.img_nav_back);
        ImageBadgeView mukesh_begview =  findViewById(R.id.mukesh_begview);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        heading = findViewById(R.id.heading);
        heading.setText("Product List");

        mukesh_begview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchProducts_Activity.this, AddCartCheckOut_Activity.class));
            }
        });

        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchproducts_activity);

        try {

            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();


            AppController.FiltersCondition = "";
            AppController.priceFilterList.clear();
            AppController.discountFilterList.clear();
            AppController.filterList1.clear();
            AppController.comesFromFilter = false;

            layout_sort = (LinearLayout) findViewById(R.id.layout_sort);

            gridView_products = (GridView) findViewById(R.id.gridView_products);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);

            txt_productHeading = (TextView) findViewById(R.id.txt_productHeading);
            mukesh_begview = findViewById(R.id.mukesh_begview);
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(com.vpipl.drdawakhana.SearchProducts_Activity.this, com.vpipl.drdawakhana.AddCartCheckOut_Activity.class));
                }
            });

            if (getIntent().getExtras() != null) {
                Keyword = getIntent().getStringExtra("Keyword");
                performDirectSearch(Keyword);
            }

            layout_sort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        showBottomSheetDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.SearchProducts_Activity.this);
        }
    }

    private void showBottomSheetDialog() {
        try {
            mBottomSheetDialog = new BottomSheetDialog(com.vpipl.drdawakhana.SearchProducts_Activity.this);
            View view = getLayoutInflater().inflate(R.layout.bottomsheet_sort_layout, null);
            RadioGroup radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup);
            RadioButton radioButton1 = (RadioButton) view.findViewById(R.id.radioButton1);
            RadioButton radioButton2 = (RadioButton) view.findViewById(R.id.radioButton2);
            RadioButton radioButton3 = (RadioButton) view.findViewById(R.id.radioButton3);
            RadioButton radioButton4 = (RadioButton) view.findViewById(R.id.radioButton4);
            radiogroup.check(radiogroup.getChildAt(checkedRadioButton).getId());

            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{

                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[]{

                                getResources().getColor(R.color.color_666666) //disabled
                                , getResources().getColor(R.color.colorPrimary) //enabled

                        }
                );


                radioButton1.setButtonTintList(colorStateList);//set the color tint list
                radioButton1.invalidate(); //could not be necessary
                radioButton2.setButtonTintList(colorStateList);//set the color tint list
                radioButton2.invalidate(); //could not be necessary
                radioButton3.setButtonTintList(colorStateList);//set the color tint list
                radioButton3.invalidate(); //could not be necessary
                radioButton4.setButtonTintList(colorStateList);//set the color tint list
                radioButton4.invalidate(); //could not be necessary
            }

            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup rg, int i) {
                    if (rg.getCheckedRadioButtonId() == rg.getChildAt(0).getId()) {
                        checkedRadioButton = 0;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(1).getId()) {
                        checkedRadioButton = 1;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(2).getId()) {
                        checkedRadioButton = 2;
                    } else if (rg.getCheckedRadioButtonId() == rg.getChildAt(3).getId()) {
                        checkedRadioButton = 3;
                    }

                    if (mBottomSheetDialog != null) {
                        mBottomSheetDialog.dismiss();
                    }

                    if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
                        isLoadMore = false;
                        pageIndex = 1;
                        performDirectSearch(Keyword);
                    } else {
                        AppUtils.alertDialog(SearchProducts_Activity.this, getResources().getString(R.string.txt_networkAlert));
                        noDataFound();
                    }
                }
            });

            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mBottomSheetDialog = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performDirectSearch(String Keyword) {
        try {
            if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
                isLoadMore = false;
                pageIndex = 1;
                executeToGetSearchedProductListRequest(Keyword);
            } else {
                AppUtils.alertDialog(SearchProducts_Activity.this, getResources().getString(R.string.txt_networkAlert));
                noDataFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetSearchedProductListRequest(final String searchKeyword) {
        try {
            if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(SearchProducts_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("SearchContent", searchKeyword));
                            int sort = checkedRadioButton + 1;
                            postParameters.add(new BasicNameValuePair("SortBy", "" + sort));

//                            postParameters.add(new BasicNameValuePair("PageIndex", ""+pageIndex));
//                            postParameters.add(new BasicNameValuePair("NumOfRows",""+NumOfRows));

                            postParameters.add(new BasicNameValuePair("FiltersCondition", "" + AppController.FiltersCondition));

                            postParameters.add(new BasicNameValuePair("UserType", "N"));

                            response = AppUtils.callWebServiceWithMultiParam(SearchProducts_Activity.this, postParameters, QueryUtils.methodToGetSearchProductsList, TAG);
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
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("HeadingMenu");
                                if (jsonArrayProductList.length() > 0) {
//                                  NoofProduct= Integer.parseInt(jsonObject.getJSONArray("NoofProduct").getJSONObject(0).getString("DtNoofProduct"));
                                    txt_productHeading.setText(Keyword);
                                   // heading.setText(Keyword);

//                                  txt_productTotal.setText("Showing "+NoofProduct+" items ");

                                    saveProductsList(jsonObject);
                                } else {
                                    noDataFound();
                                }
                            } else {
                                noDataFound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
        }
    }

    private void saveProductsList(final JSONObject jsonObject) {
        try {
            JSONArray jsonArrayProductList = jsonObject.getJSONArray("HeadingMenu");

            if (pageIndex == 1) {
                productList.clear();
            }

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);
                ProductsList products = new ProductsList();
                products.setID("" + jsonObjectProducts.getString("ProdID"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + jsonObjectProducts.getString("ProductName"));

                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setIsProductNew("" + jsonObjectProducts.getString("NewDisp"));
                //  products.setImagePath("" + SPUtils.productImageURL + jsonObjectProducts.getString("ImagePath"));
                products.setImagePath("" + SPUtils.cateImageURL  + jsonObjectProducts.getString("NewImgPath"));
                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));
                products.setCatID("S");

                productList.add(products);
            }
            if (AppUtils.showLogs) Log.e(TAG, "productList..." + productList);

            if (isLoadMore) {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {

                setGridViewData();
            }

//            if (AppController.filterList1.size() == 0) {
//                createFilterList(jsonObject);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
        }
    }

    private void setGridViewData() {
        try {

            layout_noData.setVisibility(View.GONE);
            gridView_products.setVisibility(View.VISIBLE);

            adapter = new ProductListGrid_Adapter(SearchProducts_Activity.this, productList ,"Grid");
            gridView_products.setAdapter(adapter);

            gridView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(SearchProducts_Activity.this, ProductDetail_Activity.class);
                    intent.putExtra("productID", productList.get(position).getID());
                    startActivity(intent);
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void noDataFound() {
        try {
            layout_noData.setVisibility(View.VISIBLE);
            gridView_products.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFilterList(final JSONObject jsonObject) {
        try {
            //Array list of Filter Options
            AppController.filterList1.clear();
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("name", "PRICE");
            AppController.filterList1.add(map1);
            HashMap<String, String> map2 = new HashMap<>();
            map2.put("name", "DISCOUNT");
            AppController.filterList1.add(map2);

            int MinPrice = 0, MaxPrice = 0, MinDiscount = 0, Maxdiscount = 0, DiffPrice = 0, PriceRangeGap = 0, PriceStartRange = 0, DiscStartRange = 0;
            JSONArray jsonArrayPriceAndDisount = jsonObject.getJSONArray("CategroyMenu");

            MinPrice = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MinPrice"));
            MaxPrice = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MaxPrice"));
            MinDiscount = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("MinDiscount"));
            Maxdiscount = Integer.parseInt(jsonArrayPriceAndDisount.getJSONObject(0).getString("Maxdiscount"));

            DiffPrice = (MaxPrice - MinPrice);

            if (DiffPrice >= 20000) {
                PriceRangeGap = 2000;
            } else if (DiffPrice >= 5000) {
                PriceRangeGap = 1000;
            } else {
                PriceRangeGap = 500;
            }

            PriceStartRange = (MinPrice - (MinPrice % PriceRangeGap));


            AppController.priceFilterList.clear();
            for (int i = 0; i < 8; i++) {
                String text = "", id = "";

                if (PriceStartRange <= MaxPrice) {
                    if (i < 7) {
                        text = "₹ " + PriceStartRange + " - ₹ " + (PriceStartRange + PriceRangeGap - 1);


                        id = "(Cast(a.DP2 as numeric(18,0))>=" + PriceStartRange + " AND Cast(a.DP2 as numeric(18,0))<=" + (PriceStartRange + PriceRangeGap - 1) + ")";

                    } else {
                        text = "₹ " + PriceStartRange + " and Above";


                        id = "(Cast(a.DP2 as numeric(18,0))>=" + PriceStartRange + ")";
                    }

                    PriceStartRange = PriceStartRange + PriceRangeGap;

                    FilterList2CheckBox priceFilter = new FilterList2CheckBox(text, id, false);
                    AppController.priceFilterList.add(priceFilter);
                }
            }

            DiscStartRange = (MinDiscount - (MinDiscount % 15));
            AppController.discountFilterList.clear();
            for (int i = 0; i < 8; i++) {
                String text = "", id = "";

                if (DiscStartRange <= Maxdiscount) {

                    if (i == 0) {


                        id = "(Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + " AND Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))<=" + (DiscStartRange + 15) + ")";

                        text = " Below " + (DiscStartRange + 15) + "% Discount";


                    } else if ((i > 0 && i < 6) && (DiscStartRange + 15 <= 100)) {

                        id = "(Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + " AND Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))<=" + (DiscStartRange + 15) + ")";

                        text = "" + DiscStartRange + "% - " + (DiscStartRange + 15) + "%";
                    } else {


                        id = "(Cast((((MRP-DP2)/MRP)*100) as numeric(18,0))>=" + DiscStartRange + ")";

                        text = "" + DiscStartRange + "% and Above";

                        FilterList2CheckBox discountFilter = new FilterList2CheckBox(text, id, false);
                        AppController.discountFilterList.add(discountFilter);
                        break;
                    }

                    DiscStartRange = DiscStartRange + 15;

                    FilterList2CheckBox discountFilter = new FilterList2CheckBox(text, id, false);
                    AppController.discountFilterList.add(discountFilter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setOptionMenu() {
        try {
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));
                mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
//                if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
//                    setBadgeCount(SearchProducts_Activity.this, 0);
//                    executeToGetCartItemCount();
//                } else {
//                    AppUtils.alertDialog(SearchProducts_Activity.this, getResources().getString(R.string.txt_networkAlert));
//                }
            } else {
              //  img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_user_login));

            }
           // setBadgeCount(SearchProducts_Activity.this, (AppController.selectedProductsList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void executeToGetCartItemCount() {
//        try {
//            if (AppUtils.isNetworkAvailable(SearchProducts_Activity.this)) {
//                new AsyncTask<Void, Void, String>() {
//                    @Override
//                    protected void onPreExecute() {
//                        AppUtils.showProgressDialog(SearchProducts_Activity.this);
//                    }
//
//                    @Override
//                    protected String doInBackground(Void... params) {
//                        String response = "";
//                        try {
//                            List<NameValuePair> postParameters = new ArrayList<>();
//                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
//                            response = AppUtils.callWebServiceWithMultiParam(SearchProducts_Activity.this, postParameters, QueryUtils.methodToGetCartTotalCount, TAG);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return response;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String resultData) {
//                        try {
//                            AppUtils.dismissProgressDialog();
//
//                            JSONObject jsonObject = new JSONObject(resultData);
//
//                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
//
//                                if (jsonObject.getJSONArray("Data").length() > 0) {
//                                    if (!jsonObject.getJSONArray("Data").getJSONObject(0).getString("TotalCartCount").equals("")) {
//                                        setBadgeCount(SearchProducts_Activity.this, Integer.parseInt(jsonObject.getJSONArray("Data").getJSONObject(0).getString("TotalCartCount")));
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (AppController.comesFromFilter) {
                AppController.comesFromFilter = false;
                isLoadMore = false;
                pageIndex = 1;
                performDirectSearch(Keyword);
            }
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
            isLoadMore = false;
            pageIndex = 1;
            performDirectSearch(Keyword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method used for onOptionsItemSelected events
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            AppController.FiltersCondition = "";
            AppController.priceFilterList.clear();
            AppController.discountFilterList.clear();
            AppController.filterList1.clear();
            AppController.comesFromFilter = false;

            AppUtils.dismissProgressDialog();
            //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(SearchProducts_Activity.this);
        }
    }

    public void showLoginRegisterDialog() {
        try {

            final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog_three);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Please Select an option to continue."));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Register");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startActivity(new Intent(SearchProducts_Activity.this, Register_User_Activity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("Login");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        startActivity(new Intent(SearchProducts_Activity.this, Login_Activity.class));
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

    public static void setBadge() {
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
    }
}
