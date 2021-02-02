package com.vpipl.drdawakhana;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vpipl.drdawakhana.Adapters.ProductDetailImageSliderViewPagerAdapter;
import com.vpipl.drdawakhana.Adapters.TouchImageView;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.BadgeDrawable;
import com.vpipl.drdawakhana.Utils.CirclePageIndicator;
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
import com.vpipl.drdawakhana.Utils.PicassoTrustAll;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.model.ProductsList;
import com.vpipl.drdawakhana.model.SliderItem;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.nikartm.support.ImageBadgeView;

public class ProductDetail_Activity extends AppCompatActivity {

    public static String TAG = "ProductDetail_Activity";
    Activity act = ProductDetail_Activity.this;

    static HashMap<String, String> ProductDetails = new HashMap<>();

    TextView txt_productType, tv_prod_stock;
    TextView txt_Description;
    WebView wv_Description;
    LinearLayout LL_Description;

    private JSONArray HeadingJarray, HeadingJarrayImages;

    Button btn_buyNow;
    String stock_sts = "N";

    TextView txt_productName, txt_productAmount, txt_productSKU;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<>();
    Boolean isBuyClick = true;

    String selectedSizeName = "", selectedSizeId = "0";
    String selectedOptionName = "", selectedOptionId = "0";
    String selectedColorName = "", selectedColorId = "0";

    ArrayList<String> ProductSizes = new ArrayList<>();
    ArrayList<String> ProductOptions = new ArrayList<>();
    ArrayList<String> ProductColors = new ArrayList<>();

    String selectedNewMRP, selectedNewDP, selectedDiscountPer, str_sizeid = "0";

    public ImageBadgeView mukesh_begview;
    ImageView img_menu, img_search, img_cart, img_user;

    TextView txt_outofstock;

    ImageView img_1, img_2, img_3, img_4, img_5, img_big;

    DatabaseHandler databaseHandler;

    ViewPager image_viewPager;
    CirclePageIndicator imagePageIndicator;
    int currentPage = 0;
    Timer timer;
    TextView txt_productPlus, txt_productValue, txt_productMinus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetail_activity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            SetupToolbar();

            databaseHandler = new DatabaseHandler(this);

            txt_productName = (TextView) findViewById(R.id.txt_productName);
            txt_productAmount = (TextView) findViewById(R.id.txt_productAmount);
            txt_productSKU = (TextView) findViewById(R.id.txt_productSKU);

            tv_prod_stock = (TextView) findViewById(R.id.tv_prod_stock);
            txt_productType = (TextView) findViewById(R.id.txt_productType);

            txt_productPlus = findViewById(R.id.txt_productPlus);
            txt_productValue = findViewById(R.id.txt_productValue);
            txt_productMinus = findViewById(R.id.txt_productMinus);

            txt_Description = (TextView) findViewById(R.id.txt_Description);
            wv_Description = (WebView) findViewById(R.id.wv_Description);

            LL_Description = (LinearLayout) findViewById(R.id.LL_Description);

            btn_buyNow = (Button) findViewById(R.id.btn_buyNow);

            txt_outofstock = (TextView) findViewById(R.id.txt_outofstock);

            image_viewPager = (ViewPager) findViewById(R.id.image_viewPager);
            imagePageIndicator = (CirclePageIndicator) findViewById(R.id.imagePageIndicator);

            img_1 = (ImageView) findViewById(R.id.img_1);
            img_2 = (ImageView) findViewById(R.id.img_2);
            img_3 = (ImageView) findViewById(R.id.img_3);
            img_4 = (ImageView) findViewById(R.id.img_4);
            img_5 = (ImageView) findViewById(R.id.img_5);
            img_big = (ImageView) findViewById(R.id.img_big);

            mukesh_begview = findViewById(R.id.mukesh_begview);
            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                }
            });


            if (getIntent().getExtras() != null) {
                if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
                    executeToGetProductDetailRequest();
                } else {
                    AppUtils.alertDialog(ProductDetail_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            } else {
                AppUtils.alertDialog(ProductDetail_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }

            btn_buyNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isBuyClick = true;
                    validateAddCart();
                }
            });

            findViewById(R.id.btn_addtowishlist).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateAddWishlist();
                }
            });

            setOptionMenu();

            try {
                img_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadProductImage(ProductDetail_Activity.this, imageList.get(0).get("image"), img_big);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                img_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadProductImage(ProductDetail_Activity.this, imageList.get(1).get("image"), img_big);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                img_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadProductImage(ProductDetail_Activity.this, imageList.get(2).get("image"), img_big);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                img_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadProductImage(ProductDetail_Activity.this, imageList.get(3).get("image"), img_big);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                img_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadProductImage(ProductDetail_Activity.this, imageList.get(4).get("image"), img_big);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            img_big.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFullScreenImageDialog(img_big.getDrawable());
                }
            });


            txt_productMinus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int i;
                    i = Integer.parseInt(txt_productValue.getText().toString());
                    if (i > 1) {
                        i = i - 1;
                    }
                    txt_productValue.setText("" + i);
                }
            });

            txt_productPlus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int i;
                    i = Integer.parseInt(txt_productValue.getText().toString());
                    i = i + 1;
                    txt_productValue.setText("" + i);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateAddCart() {
        try {

            if (ProductOptions.size() > 0 && selectedOptionId.equals("0")) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Please Select Product Option.");
            } else if (ProductOptions.size() > 0 && selectedOptionName.equals("NA")) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Please Select Product Option.");
            } else if (ProductSizes.size() > 0 && selectedSizeId.equals("0")) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Please Select Product Size.");
            } else if (ProductSizes.size() > 0 && selectedSizeName.equals("NA")) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Please Select Product Size.");
            } else if (ProductColors.size() > 0 && selectedColorId.equals("0")) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Please Select Product Color.");
            } else if (ProductColors.size() > 0 && selectedColorName.equals("NA")) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Please Select Product Color.");
            } else {
                if (stock_sts.equalsIgnoreCase("Y")) {
                    isBuyClick = true;
                    goToAddProductInCart();
                } else {
                    AppUtils.alertDialog(ProductDetail_Activity.this, "Your Product Out of Stock!!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateAddWishlist() {
        try {
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                goToAddProductInWishList();
            } else {
                showLoginRegisterDialog();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToAddProductInCart() {
        try {

            if (AppController.selectedProductsList.size() > 0) {
                Boolean isAdded = false;
                for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                    if (AppController.selectedProductsList.get(i).getID().equals(ProductDetails.get("ProductID"))) {
                        if (AppController.selectedProductsList.get(i).getSelectedSizeId().equals(selectedSizeId) &&
                                AppController.selectedProductsList.get(i).getSelectedOptionId().equals(selectedOptionId) &&
                                AppController.selectedProductsList.get(i).getSelectedColorId().equals(selectedColorId)) {
                            isAdded = true;
                        }
                    }
                }

                if (isAdded) {
                    if (isBuyClick) {
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                        } else {
                            showLoginRegisterDialog();
                        }
                    } else
                        AppUtils.alertDialog(ProductDetail_Activity.this, "Selected Product already exist in Cart. Please update quantity in Cart.");
                } else {
                    addItemInSelectedProductList();
                }
            } else {
                addItemInSelectedProductList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToAddProductInWishList() {
        try {
            createParametersForWishlist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createParametersForWishlist() {

        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            JSONArray jsonArrayMainCart = new JSONArray();

            JSONObject jsonObjectMainCart = new JSONObject();

//            jsonObjectMainCart.put("OrderByFormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, ""));
//            jsonObjectMainCart.put("ProductID", "" + ProductDetails.get("ProductID"));
//            jsonObjectMainCart.put("UserProdID", "" + ProductDetails.get("UserProdID"));
//            jsonObjectMainCart.put("ProductName", "" + ProductDetails.get("ProductName"));
//            jsonObjectMainCart.put("NewMRP", "" + selectedNewMRP);
//            jsonObjectMainCart.put("NewDP", "" + selectedNewDP);
//            jsonObjectMainCart.put("DiscountPer", "" + selectedDiscountPer);
//            jsonObjectMainCart.put("CatID", "" + ProductDetails.get("CatID"));
//            jsonObjectMainCart.put("ShipCharge", "" + ProductDetails.get("ShipCharge"));
//            jsonObjectMainCart.put("selectedSizeName", "" + selectedSizeName);
//            jsonObjectMainCart.put("selectedSizeId", "" + selectedSizeId);
//            jsonObjectMainCart.put("selectedColorName", "" + selectedColorName);
//            jsonObjectMainCart.put("selectedColorId", "" + selectedColorId);
//            jsonObjectMainCart.put("selectedOptionName", "" + selectedOptionName);
//            jsonObjectMainCart.put("selectedOptionId", "" + selectedOptionId);
//            jsonObjectMainCart.put("image", "" + imageList.get(0).get("image"));

            jsonObjectMainCart.put("OrderByFormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID, ""));
            jsonObjectMainCart.put("ProductID", "" + ProductDetail_Activity.ProductDetails.get("ProductID"));
            jsonObjectMainCart.put("Qty", "1");
            jsonObjectMainCart.put("OrderFor", "WR");
            jsonObjectMainCart.put("UID", "0");
            jsonObjectMainCart.put("Size", "" + selectedSizeId);
            jsonObjectMainCart.put("Color", "" + selectedColorId);
            jsonObjectMainCart.put("UserType", "C");

            jsonArrayMainCart.put(jsonObjectMainCart);

            postParameters.add(new BasicNameValuePair("WishListData", jsonArrayMainCart.toString()));

            executeToAddItemInWishList(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToAddItemInWishList(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ProductDetail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(ProductDetail_Activity.this, postParameters, QueryUtils.methodToAddToWishList, TAG);
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
                                addItemInSelectedWishList();
                            } else {
                                AppUtils.alertDialog(ProductDetail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    private void addItemInSelectedProductList() {
        try {
            boolean already_exists = false;
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                if (AppController.selectedProductsList.get(i).getName().equalsIgnoreCase(ProductDetails.get("ProductName"))) {
                    if (AppController.selectedProductsList.get(i).getSelectedSizeId().equals(selectedSizeId) &&
                            AppController.selectedProductsList.get(i).getSelectedOptionId().equals(selectedOptionId) &&
                            AppController.selectedProductsList.get(i).getSelectedColorId().equals(selectedColorId)) {
                        already_exists = true;
                    }
                }
            }

            if (already_exists) {
                if (isBuyClick) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                        startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                    } else {
                        showLoginRegisterDialog();
                    }
                } else
                    AppUtils.alertDialog(ProductDetail_Activity.this, "Selected Product already exist in Cart. Please update quantity in Cart.");
            } else {
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric(10);

                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + ProductDetails.get("ProductID"));
                selectedProduct.setcode("" + ProductDetails.get("UserProdID"));
                selectedProduct.setName("" + ProductDetails.get("ProductName"));

                selectedProduct.setNewMRP("" + selectedNewMRP);
                selectedProduct.setNewDP("" + selectedNewDP);
                selectedProduct.setDiscountPer("" + selectedDiscountPer);

                selectedProduct.setQty("" + txt_productValue.getText().toString());
                selectedProduct.setBaseQty("1");

                selectedProduct.setCatID("" + ProductDetails.get("CatID"));
                selectedProduct.setShipCharge("" + ProductDetails.get("ShipCharge"));

                selectedProduct.setSelectedSizeName("" + selectedSizeName);
                selectedProduct.setSelectedSizeId("" + selectedSizeId);

                selectedProduct.setselectedColorName("" + selectedColorName);
                selectedProduct.setselectedColorId("" + selectedColorId);

                selectedProduct.setSelectedOptionName("" + selectedOptionName);
                selectedProduct.setSelectedOptionId("" + selectedOptionId);

                if (imageList.size() > 0) {
                    selectedProduct.setImagePath("" + imageList.get(0).get("image"));
                }

                AppController.selectedProductsList.add(selectedProduct);

                // setBadgeCount(ProductDetail_Activity.this, (AppController.selectedProductsList.size()));

                if (isBuyClick) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                    } else {
                        showLoginRegisterDialog();
                    }
                } else {
                    AppUtils.alertDialog(ProductDetail_Activity.this, "Success: You have added " + ProductDetails.get("ProductName") + " to your shopping cart!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItemInSelectedWishList() {
        try {
            boolean already_exists = false;


            for (int i = 0; i < AppController.selectedWishList.size(); i++) {
                if (AppController.selectedWishList.get(i).getID().equals(ProductDetails.get("ProductID"))) {
                    if (AppController.selectedWishList.get(i).getSelectedSizeId().equals(selectedSizeId) &&
                            AppController.selectedWishList.get(i).getSelectedOptionId().equals(selectedOptionId) &&
                            AppController.selectedWishList.get(i).getSelectedColorId().equals(selectedColorId)) {
                        already_exists = true;
                    }
                }
            }

            if (already_exists) {
                AppUtils.alertDialog(ProductDetail_Activity.this, "Selected Product already exist in Wishlist. Please update quantity in Wishlist.");
            } else {
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric(10);
                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + ProductDetails.get("ProductID"));
                selectedProduct.setcode("" + ProductDetails.get("UserProdID"));
                selectedProduct.setName("" + ProductDetails.get("ProductName"));
                selectedProduct.setNewMRP("" + selectedNewMRP);
                selectedProduct.setNewDP("" + selectedNewDP);
                selectedProduct.setDiscountPer("" + selectedDiscountPer);
                selectedProduct.setQty("" + txt_productValue.getText().toString());
                selectedProduct.setBaseQty("1");
                selectedProduct.setCatID("" + ProductDetails.get("CatID"));
                selectedProduct.setShipCharge("" + ProductDetails.get("ShipCharge"));
                selectedProduct.setSelectedSizeName("" + selectedSizeName);
                selectedProduct.setSelectedSizeId("" + selectedSizeId);
                selectedProduct.setselectedColorName("" + selectedColorName);
                selectedProduct.setselectedColorId("" + selectedColorId);
                selectedProduct.setSelectedOptionName("" + selectedOptionName);
                selectedProduct.setSelectedOptionId("" + selectedOptionId);

                if (imageList.size() > 0) {
                    selectedProduct.setImagePath("" + imageList.get(0).get("image"));
                }

                AppController.selectedWishList.add(selectedProduct);

                databaseHandler.addProductsWishlist(selectedProduct);

                AppUtils.alertDialog(ProductDetail_Activity.this, "Success: You have added " + ProductDetails.get("ProductName") + " to your Wish list!");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetProductDetailRequest() {
        try {
            if (AppUtils.isNetworkAvailable(ProductDetail_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(ProductDetail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("ProductId", getIntent().getExtras().getString("productID")));
                            response = AppUtils.callWebServiceWithMultiParam(ProductDetail_Activity.this, postParameters, QueryUtils.methodToSelectProductByID, TAG);
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
                                JSONArray jsonArrayProductDetail = jsonObject.getJSONArray("Data");
                                if (jsonArrayProductDetail.length() > 0) {
                                    saveProductDetails(jsonArrayProductDetail.getJSONObject(0));
                                } else {
                                    AppUtils.alertDialogWithFinish(ProductDetail_Activity.this, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialogWithFinish(ProductDetail_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(ProductDetail_Activity.this);
        }
    }

    private void saveProductDetails(final JSONObject jsonObjectProductDetails) {
        try {
            ProductDetails.clear();
            imageList.clear();

            ProductDetails.put("ProductID", "" + jsonObjectProductDetails.getString("ProdId"));
            ProductDetails.put("UserProdID", "" + jsonObjectProductDetails.getString("UserProdId"));
            ProductDetails.put("ProductName", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProductName").trim()));
            ProductDetails.put("ProductDesc", "" + WordUtils.capitalizeFully(jsonObjectProductDetails.getString("ProductDesc")));
            ProductDetails.put("IsshipChrg", "" + jsonObjectProductDetails.getString("IsshipChrg"));
            ProductDetails.put("ShipCharge", "" + jsonObjectProductDetails.getString("ShipCharge"));
            ProductDetails.put("CatID", "" + jsonObjectProductDetails.getString("CatId"));
            ProductDetails.put("Material", "");

            ProductDetails.put("NewMRP", "" + jsonObjectProductDetails.getString("MRP"));
            ProductDetails.put("NewDP", "" + jsonObjectProductDetails.getString("DP"));
            ProductDetails.put("DiscountPer", "" + jsonObjectProductDetails.getString("Discount"));

            ProductDetails.put("Weight", "" + jsonObjectProductDetails.getString("Weight"));
            ProductDetails.put("ProductType", "" + jsonObjectProductDetails.getString("ProductType"));
            ProductDetails.put("Dimensions", "");
            ProductDetails.put("UPC", "");

            stock_sts = "" + jsonObjectProductDetails.getString("StockAvailable");
            selectedNewMRP = "" + jsonObjectProductDetails.getString("MRP");
            selectedNewDP = "" + jsonObjectProductDetails.getString("DP");
            selectedDiscountPer = "" + jsonObjectProductDetails.getString("Discount");

            if (!jsonObjectProductDetails.getString("ImagePath").equals("") && !jsonObjectProductDetails.getString("ImagePath").equals("null")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + SPUtils.productImageURL + jsonObjectProductDetails.getString("ImagePath").replaceAll(" ", "%20"));
                imageList.add(map);
            } else {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "http://shreebalaji.versatileitsolution.com/admin/admin-images/ShreeBalajiLogo.png".replaceAll(" ", "%20"));
                imageList.add(map);
            }
            if (!jsonObjectProductDetails.getString("Imgpath1").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + SPUtils.productImageURL + jsonObjectProductDetails.getString("Imgpath1").replaceAll(" ", "%20"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("Imgpath2").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + SPUtils.productImageURL + jsonObjectProductDetails.getString("Imgpath2").replaceAll(" ", "%20"));
                imageList.add(map);
            }

            if (!jsonObjectProductDetails.getString("Imgpath3").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + SPUtils.productImageURL + jsonObjectProductDetails.getString("Imgpath3").replaceAll(" ", "%20"));
                imageList.add(map);
            }

            //  if (!jsonObjectProductDetails.getString("Imgpath4").equals("../images/NewProducts/")) {
            if (!jsonObjectProductDetails.getString("Imgpath4").equals("")) {
                HashMap<String, String> map = new HashMap<>();
                map.put("image", "" + SPUtils.productImageURL + jsonObjectProductDetails.getString("Imgpath4").replaceAll(" ", "%20"));
                imageList.add(map);
            }
            setProductDetails();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProductDetails() {
        try {
            txt_productName.setText(ProductDetails.get("ProductName"));
            txt_productSKU.setText("(UPC Code: " + ProductDetails.get("UserProdID") + ")");

            String NewDP = "₹ " + ProductDetails.get("NewDP") + "/-";
            String NewMRP = ProductDetails.get("NewMRP");
            String DiscountPer = ProductDetails.get("DiscountPer") + "% off";
            Spannable spanString = null;

            if (DiscountPer.equalsIgnoreCase("0% off")) {
                spanString = new SpannableString("" + NewDP);
//                spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#4a8be5")), 0, NewDP.length(), 0);
                spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, NewDP.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.1f), 0, NewDP.length(), 0);
            } else {
                // spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  " + DiscountPer);
                DiscountPer = "";
                spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  " + DiscountPer);

//                spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#4a8be5")), 0, NewDP.length(), 0);
                spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, NewDP.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.1f), 0, NewDP.length(), 0);
                StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                spanString.setSpan(boldSpan, 0, NewDP.length(), 0);

                spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                spanString.setSpan(new ForegroundColorSpan(Color.GRAY), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);

                spanString.setSpan(new ForegroundColorSpan(Color.GRAY), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
            }

            txt_productAmount.setText(spanString);

            txt_productType.setText(ProductDetails.get("ProductType"));

            if (stock_sts.equalsIgnoreCase("Y")) {
                tv_prod_stock.setText("Available in Stock");
                btn_buyNow.setText("Add To Cart");
            } else {
                tv_prod_stock.setText("Out Of Stock");
                btn_buyNow.setText("Out Of Stock");
            }

            txt_Description.setText(Html.fromHtml(ProductDetails.get("ProductDesc")));
            //wv_Description
            wv_Description.loadDataWithBaseURL("", ProductDetails.get("ProductDesc"), "text/html", "UTF-8", "");
            txt_productAmount.setVisibility(View.VISIBLE);
            LL_Description.setVisibility(View.VISIBLE);

            /*if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                txt_productAmount.setVisibility(View.VISIBLE);
                LL_Description.setVisibility(View.VISIBLE);
            } else {
                txt_productAmount.setVisibility(View.GONE);
                LL_Description.setVisibility(View.GONE);
            }*/
            //  addNewItem("http://shreebalaji.versatileitsolution.com/admin/admin-images/logo.png");
            //TODO Load Images
            /*for (int i = 0; i < 5; i++) {
              //  addNewItem(imageList.get(i).get("image"));
                setImageSlider();
            }*/

            if (imageList.size() > 0) {
                setImageSlider();
            }
                    /*
                    if (!imageList.get(4).get("image").equalsIgnoreCase("http://weddingdecormall.com/../images/NewProducts/")) {
                        loadProductImage(this, imageList.get(4).get("image"), img_5);
                        img_5.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                        img_5.setVisibility(View.VISIBLE);
                    } else {
                        img_5.setEnabled(false);
                    }
                }
                if (imageList.size() >= 4) {
                    if (!imageList.get(3).get("image").equalsIgnoreCase("http://weddingdecormall.com/../images/NewProducts/")) {
                        loadProductImage(this, imageList.get(3).get("image"), img_4);
                        img_4.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                        img_4.setVisibility(View.VISIBLE);
                    } else {
                        img_4.setEnabled(false);
                    }
                }
                if (imageList.size() >= 3) {
                    if (!imageList.get(2).get("image").equalsIgnoreCase("http://weddingdecormall.com/../images/NewProducts/")) {
                        loadProductImage(this, imageList.get(2).get("image"), img_3);
                        img_3.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                        img_3.setVisibility(View.VISIBLE);
                    } else {
                        img_3.setEnabled(false);
                    }
                }
                if (imageList.size() >= 2) {
                    if (!imageList.get(1).get("image").equalsIgnoreCase("http://weddingdecormall.com/../images/NewProducts/")) {
                        loadProductImage(this, imageList.get(1).get("image"), img_2);
                        img_2.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                        img_2.setVisibility(View.VISIBLE);
                    } else {
                        img_2.setEnabled(false);
                    }
                }
                if (imageList.size() >= 1) {
                    if (!imageList.get(0).get("image").equalsIgnoreCase("http://weddingdecormall.com/../images/NewProducts/")) {
                        loadProductImage(this, imageList.get(0).get("image"), img_1);
                        img_1.setBackground(getResources().getDrawable(R.drawable.hot_product_bg_round_rectangle));
                        img_1.setVisibility(View.VISIBLE);
                    } else {
                        img_1.setEnabled(false);
                    }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOptionMenu() {
        try {
            mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                // img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));
            } else {
                ///   img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_user_login));
            }
            //   setBadgeCount(ProductDetail_Activity.this, (AppController.selectedProductsList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setOptionMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            setOptionMenu();
            executeToGetProductDetailRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectedSizeId = "0";
        selectedOptionId = "0";
        selectedColorId = "0";
        selectedSizeName = "";
        selectedOptionName = "";
        selectedColorName = "";
        ProductColors.clear();
        ProductOptions.clear();
        ProductSizes.clear();
    }

    public void SetupToolbar() {
        LinearLayout img_nav_back = findViewById(R.id.img_nav_back);
        TextView heading = findViewById(R.id.heading);

        heading.setText("Product Detail");
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*//   img_menu = (ImageView) findViewById(R.id.img_menu);
        img_search = (ImageView) findViewById(R.id.img_search);
        img_cart = (ImageView) findViewById(R.id.img_cart);
        //   img_user = (ImageView) findViewById(R.id.img_user);
        img_search.setVisibility(View.GONE);

        //  img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {

                    startActivity(new Intent(ProductDetail_Activity.this, AddCartCheckOut_Activity.class));
                } else {
                    showLoginRegisterDialog();
                }
            }
        });

        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    startActivity(new Intent(ProductDetail_Activity.this, Login_Activity.class));
                else
                    AppUtils.showDialogSignOut(ProductDetail_Activity.this);
            }
        });*/

        /*if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_log_out));
        else
            img_user.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_user_login));*/


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
                        startActivity(new Intent(ProductDetail_Activity.this, Register_User_Activity.class));
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
                        startActivity(new Intent(ProductDetail_Activity.this, Login_Activity.class));
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

    public void loadProductImage(Context conn, String imageURL, ImageView imageView) {
        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                if (!this.isFinishing())
                    Glide.with(conn)
                            .load(imageURL)
                            .placeholder(R.drawable.ic_no_image)
                            .error(R.drawable.ic_no_image)
                            .skipMemoryCache(false)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);

            } else {
                if (!this.isFinishing())
                    PicassoTrustAll.getInstance(conn)
                            .load(imageURL)
                            .into(imageView);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showFullScreenImageDialog(Drawable drawable) {
        try {

            final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.imagezoomviewpagerlayout);

            TouchImageView imgDisplay = (TouchImageView) dialog.findViewById(R.id.imgDisplay);
            imgDisplay.setImageDrawable(drawable);

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImageSlider() {
        try {
            image_viewPager.setAdapter(new ProductDetailImageSliderViewPagerAdapter(act, imageList));

            final float density = getResources().getDisplayMetrics().density;
            imagePageIndicator.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
            imagePageIndicator.setStrokeColor(Color.parseColor("#EEEEEE"));
            imagePageIndicator.setPageColor(Color.parseColor("#EEEEEE"));
            imagePageIndicator.setStrokeWidth(1);
            imagePageIndicator.setRadius(4 * density);
            imagePageIndicator.setViewPager(image_viewPager);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == imageList.size()) {
                        currentPage = 0;
                    }
                    image_viewPager.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 3000, 5000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}