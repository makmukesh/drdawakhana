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
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
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
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.model.ProductsList;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class ProductListGrid_Activity extends AppCompatActivity {
    public static String TAG = "ProductListGrid_Activity";

    public static ImageBadgeView mukesh_begview;
    public static ArrayList<String> selectedWidthIds = new ArrayList<>();
    public static ArrayList<String> selectedHeightIds = new ArrayList<>();
    public static ArrayList<String> selectedMaterialIds = new ArrayList<>();
    public static ArrayList<String> selectedColorIds = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> WidthList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> HeightList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> MaterialList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> ColorList = new ArrayList<>();

    LinearLayout layout_noData, layout_sort, layout_filter;

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

    DatabaseHandler db;

    ImageView img_menu, img_search, img_cart, img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productlistgrid_activity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            AppController.comesFromFilter = false;

            layout_filter = (LinearLayout) findViewById(R.id.layout_filter);
            layout_sort = (LinearLayout) findViewById(R.id.layout_sort);

            gridView_products = (GridView) findViewById(R.id.gridView_products);
            layout_noData = (LinearLayout) findViewById(R.id.layout_noData);

            txt_productHeading = (TextView) findViewById(R.id.txt_productHeading);

            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(com.vpipl.drdawakhana.ProductListGrid_Activity.this, com.vpipl.drdawakhana.AddCartCheckOut_Activity.class));
                }
            });

            db = new DatabaseHandler(this);

            if (getIntent().getExtras() != null) {
                if (AppUtils.isNetworkAvailable(com.vpipl.drdawakhana.ProductListGrid_Activity.this)) {
                    isLoadMore = false;
                    pageIndex = 1;
                    // executeToGetProductListRequest();
                    executeFilterationRequest();
                } else {
                    AppUtils.alertDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                    noDataFound();
                }
            } else {
                AppUtils.alertDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
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

//            layout_filter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        startActivity(new Intent(ProductListGrid_Activity.this, Filter_Activity.class));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
           /* if (AppController.comesFromFilter) {
                AppController.comesFromFilter = false;

                if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                    isLoadMore = false;
                    pageIndex = 1;
                    productList.clear();
                    saveProductsListFilter(MAsterData);
                } else {
                    AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            } else {
                productList.clear();
                saveProductsList(MAsterData);
            }*/

            setOptionMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBottomSheetDialog() {
        try {
            mBottomSheetDialog = new BottomSheetDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
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

                    if (AppUtils.isNetworkAvailable(com.vpipl.drdawakhana.ProductListGrid_Activity.this)) {
                        isLoadMore = false;
                        pageIndex = 1;
                        productList.clear();
                        productList = db.getAllProducts(checkedRadioButton);

                        setGridViewData();

                    } else {
                        AppUtils.alertDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
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

    private void executeToGetProductListRequest() {
        try {
            if (AppUtils.isNetworkAvailable(com.vpipl.drdawakhana.ProductListGrid_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Type", getIntent().getExtras().getString("Type")));

                            postParameters.add(new BasicNameValuePair("CategoryID", getIntent().getExtras().getString("categoryID")));

                            int sort = checkedRadioButton + 1;

                            postParameters.add(new BasicNameValuePair("Sort", "" + sort));
                            postParameters.add(new BasicNameValuePair("PageIndex", "" + pageIndex));
                            postParameters.add(new BasicNameValuePair("NumOfRows", "" + NumOfRows));
                            postParameters.add(new BasicNameValuePair("FiltersCondition", "" + AppController.FiltersCondition));

                            postParameters.add(new BasicNameValuePair("UserType", "N"));

                            response = AppUtils.callWebServiceWithMultiParam(com.vpipl.drdawakhana.ProductListGrid_Activity.this, postParameters,
                                    QueryUtils.methodToGetProductList, TAG);
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

                            MAsterData = jsonObject;

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");

                                if (jsonArrayProductList.length() > 0) {
//                                    NoofProduct = Integer.parseInt(jsonObject.getJSONArray("NoofProduct").getJSONObject(0).getString("Total"));
                                    if (jsonObject.getJSONArray("ProductHeadingName").length() > 0)
                                        txt_productHeading.setText("" + jsonObject.getJSONArray("ProductHeadingName").getJSONObject(0).getString("HeadName").replaceAll("&amp;", "&") + "");

                                    saveProductsList(jsonObject);
                                } else {
                                    noDataFound();
                                }
                            } else {
                                noDataFound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
        }
    }

    private void executeToGetProductLoadMoreListRequest() {
        try {
            if (AppUtils.isNetworkAvailable(com.vpipl.drdawakhana.ProductListGrid_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Type", getIntent().getExtras().getString("Type")));

                            postParameters.add(new BasicNameValuePair("CategoryID", getIntent().getExtras().getString("categoryID")));

                            int sort = checkedRadioButton + 1;

                            postParameters.add(new BasicNameValuePair("SortProducts", "" + sort));
                            postParameters.add(new BasicNameValuePair("PageIndex", "" + pageIndex));
                            postParameters.add(new BasicNameValuePair("NumOfRows", "" + NumOfRows));
                            postParameters.add(new BasicNameValuePair("FiltersCondition", "" + AppController.FiltersCondition));

                            postParameters.add(new BasicNameValuePair("UserType", "N"));

                            response = AppUtils.callWebServiceWithMultiParam(com.vpipl.drdawakhana.ProductListGrid_Activity.this, postParameters, QueryUtils.methodToGetProductList, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {

                            JSONObject jsonObject = new JSONObject(resultData);

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");
                                if (jsonArrayProductList.length() > 0) {
                                    saveProductsList(jsonObject);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
        }
    }

    private void saveProductsList(final JSONObject jsonObject) {
        try {

            JSONArray jsonArrayProductList = jsonObject.getJSONArray("ProductList");

//            JSONArray jsonArrayProductColor = jsonObject.getJSONArray("ProductColor");
//            JSONArray jsonArrayProductWidth = jsonObject.getJSONArray("ProductSize");
//            JSONArray jsonArrayProductHeight = jsonObject.getJSONArray("ProductSize");
//            JSONArray jsonArrayProductMaterial = jsonObject.getJSONArray("ProductSize");

            if (pageIndex == 1) {
                productList.clear();
            }

            db.clearDB();

            for (int i = 0; i < jsonArrayProductList.length(); i++) {
                JSONObject jsonObjectProducts = jsonArrayProductList.getJSONObject(i);
                ProductsList products = new ProductsList();
                products.setID("" + jsonObjectProducts.getString("ProdID"));
                products.setcode("" + jsonObjectProducts.getString("ProductCode"));
                products.setName("" + WordUtils.capitalizeFully(jsonObjectProducts.getString("ProductName")));
                products.setDiscount("" + jsonObjectProducts.getString("Discount"));
                products.setDiscountPer("" + jsonObjectProducts.getString("DiscountPer"));
                products.setIsProductNew("" + jsonObjectProducts.getString("NewDisp"));
                products.setImagePath("" + SPUtils.productImageURL + jsonObjectProducts.getString("NewImgPath"));
                products.setNewMRP("" + jsonObjectProducts.getString("NewMRP"));
                products.setNewDP("" + jsonObjectProducts.getString("NewDP"));

//              products.setselectedSizeId("" + jsonObjectProducts.getString("SizeIDs"));
//              products.setselectedColorId("" + jsonObjectProducts.getString("ColorIDs"));

                productList.add(products);

                db.addProducts(products);
            }

            ColorList.clear();
//            for (int aa = 0; aa < jsonArrayProductColor.length(); aa++) {
//                JSONObject jsonObjectProductColor = jsonArrayProductColor.getJSONObject(aa);
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("ColorCode", "" + jsonObjectProductColor.getString("ColorCode").toString());
//                map.put("ColorID", "" + jsonObjectProductColor.getString("ColorID").toString());
//                ColorList.add(map);
//            }

            WidthList.clear();
//            for (int bb = 0; bb < jsonArrayProductWidth.length(); bb++) {
//                JSONObject jsonObjectProductSize = jsonArrayProductWidth.getJSONObject(bb);
//                HashMap<String, String> map = new HashMap<>();
//                map.put("SizeID", "" + jsonObjectProductSize.getString("SizeID"));
//                map.put("Size", "" + jsonObjectProductSize.getString("Size"));
//                WidthList.add(map);
//            }

            HeightList.clear();
//            for (int bb = 0; bb < jsonArrayProductHeight.length(); bb++) {
//                JSONObject jsonObjectProductSize = jsonArrayProductHeight.getJSONObject(bb);
//                HashMap<String, String> map = new HashMap<>();
//                map.put("SizeID", "" + jsonObjectProductSize.getString("SizeID"));
//                map.put("Size", "" + jsonObjectProductSize.getString("Size"));
//                HeightList.add(map);
//            }

            MaterialList.clear();
//            for (int bb = 0; bb < jsonArrayProductMaterial.length(); bb++) {
//                JSONObject jsonObjectProductSize = jsonArrayProductMaterial.getJSONObject(bb);
//                HashMap<String, String> map = new HashMap<>();
//                map.put("SizeID", "" + jsonObjectProductSize.getString("SizeID"));
//                map.put("Size", "" + jsonObjectProductSize.getString("Size"));
//                MaterialList.add(map);
//            }

            if (isLoadMore) {
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                setGridViewData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
        }
    }

    private void saveProductsListFilter(final JSONObject jsonObject) {
        try {

            if (pageIndex == 1) {
                productList.clear();
            }

            if (selectedColorIds.size() == 0) {
                for (int i = 0; i < ColorList.size(); i++) {
                    HashMap Jobject = ColorList.get(i);
                    selectedColorIds.add(Jobject.get("SizeID").toString());
                }
            }

            if (selectedWidthIds.size() == 0) {
                for (int i = 0; i < WidthList.size(); i++) {
                    HashMap Jobject = WidthList.get(i);
                    selectedWidthIds.add(Jobject.get("SizeID").toString());
                }
            }

            if (selectedHeightIds.size() == 0) {
                for (int i = 0; i < HeightList.size(); i++) {
                    HashMap Jobject = HeightList.get(i);
                    selectedHeightIds.add(Jobject.get("SizeID").toString());
                }
            }

            if (selectedMaterialIds.size() == 0) {
                for (int i = 0; i < MaterialList.size(); i++) {
                    HashMap Jobject = MaterialList.get(i);
                    selectedMaterialIds.add(Jobject.get("SizeID").toString());
                }
            }

            productList = db.getFilteredProducts(selectedWidthIds, selectedHeightIds, selectedMaterialIds, selectedColorIds);

            setGridViewData();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
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
                        startActivity(new Intent(com.vpipl.drdawakhana.ProductListGrid_Activity.this, Register_User_Activity.class));
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
                        startActivity(new Intent(com.vpipl.drdawakhana.ProductListGrid_Activity.this, Login_Activity.class));
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

    private void setGridViewData() {
        try {
            layout_noData.setVisibility(View.GONE);
            gridView_products.setVisibility(View.VISIBLE);

            adapter = new ProductListGrid_Adapter(com.vpipl.drdawakhana.ProductListGrid_Activity.this, productList,"Grid");
            gridView_products.setAdapter(adapter);

            gridView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    Intent intent = new Intent(com.vpipl.drdawakhana.ProductListGrid_Activity.this, ProductDetail_Activity.class);
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
                                if (AppUtils.isNetworkAvailable(com.vpipl.drdawakhana.ProductListGrid_Activity.this)) {
                                    isLoadMore = true;
//                                    pageIndex++;
                                    NumOfRows = NumOfRows + 16;
                                    // executeToGetProductLoadMoreListRequest();
                                } else {
                                    AppUtils.alertDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
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
            layout_noData.setVisibility(View.VISIBLE);
            gridView_products.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionMenu() {
        try {

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));

            } else {
                img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_user_login));

            }
            // setBadgeCount(ProductListGrid_Activity.this, (AppController.selectedProductsList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (AppController.comesFromFilter) {
                AppController.comesFromFilter = false;

               /* if (AppUtils.isNetworkAvailable(ProductListGrid_Activity.this)) {
                    isLoadMore = false;
                    pageIndex = 1;
                    saveProductsListFilter(MAsterData);
                } else {
                    AppUtils.alertDialog(ProductListGrid_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            DatabaseHandler sqLiteHelper = DatabaseHandler.getInstance(this);
            sqLiteHelper.clearDB();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
        }
    }

    public void setBadgeCount(Context context, int count) {
        try {

            ImageView imageView = (ImageView) findViewById(R.id.img_cart);


            if (imageView != null) {
                LayerDrawable icon = (LayerDrawable) imageView.getDrawable();
                //Update LayerDrawable's BadgeDrawable
                BadgeDrawable badge;// Reuse drawable if possible
                Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge); //getting the layer 2
                if (reuse != null && reuse instanceof BadgeDrawable) {
                    badge = (BadgeDrawable) reuse;
                } else {
                    badge = new BadgeDrawable(context);
                }
                badge.setCount("" + count);
                icon.mutate();
                icon.setDrawableByLayerId(R.id.ic_badge, badge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeFilterationRequest() {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    String Categorycode = "0", Level1CategoryId = "0", Level2CategoryId = "0";
                    if (getIntent().getExtras().getString("Type").equalsIgnoreCase("H")) {
                        Categorycode = getIntent().getExtras().getString("categoryID");
                        Level1CategoryId = "0";
                        Level2CategoryId = "0";
                    } else if (getIntent().getExtras().getString("Type").equalsIgnoreCase("C")) {
                        Categorycode = "0";
                        Level1CategoryId = getIntent().getExtras().getString("categoryID");
                        Level2CategoryId = "0";
                    } else if (getIntent().getExtras().getString("Type").equalsIgnoreCase("S")) {
                        Categorycode = "0";
                        Level1CategoryId = "0";
                        Level2CategoryId = getIntent().getExtras().getString("categoryID");
                    }

                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("CategoryId", "" + Categorycode));
                    postParameters.add(new BasicNameValuePair("Level1CategoryId", "" + Level1CategoryId));
                    postParameters.add(new BasicNameValuePair("Level2CategoryId", "" + Level2CategoryId));
                    response = AppUtils.callWebServiceWithMultiParam(com.vpipl.drdawakhana.ProductListGrid_Activity.this, postParameters, QueryUtils.methodToSelectProductByCategory, TAG);
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
            AppUtils.showExceptionDialog(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
        }
    }

    public static void setBadge() {
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
    }

    public void SetupToolbar() {
        LinearLayout img_nav_back =  findViewById(R.id.img_nav_back);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       /* img_menu = (ImageView) findViewById(R.id.img_menu);
        img_search = (ImageView) findViewById(R.id.img_search);
        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_user = (ImageView) findViewById(R.id.img_user);
        img_search.setVisibility(View.GONE);
        mukesh_begview = findViewById(R.id.mukesh_begview);
        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                    startActivity(new Intent(ProductListGrid_Activity.this, AddCartCheckOut_Activity.class));
                } else {
                    showLoginRegisterDialog();
                }*//*
                startActivity(new Intent(com.vpipl.drdawakhana.ProductListGrid_Activity.this, com.vpipl.drdawakhana.AddCartCheckOut_Activity.class));
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(com.vpipl.drdawakhana.ProductListGrid_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(com.vpipl.drdawakhana.ProductListGrid_Activity.this);
            }
        });

        //  setBadgeCount(ProductListGrid_Activity.this, 0);

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_user_login));

*/
    }

}
