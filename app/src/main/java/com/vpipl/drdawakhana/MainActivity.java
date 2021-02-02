package com.vpipl.drdawakhana;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.RequestQueue;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.vpipl.drdawakhana.Adapters.Category_Home_Main_list_Adapter;
import com.vpipl.drdawakhana.Adapters.Home_Top_Product_list_Adapter;
import com.vpipl.drdawakhana.Adapters.SliderAdapterExample;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.CirclePageIndicator;
import com.vpipl.drdawakhana.Utils.CircularImageView;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.model.SliderItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import ru.nikartm.support.ImageBadgeView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";

    Activity act;
    public static ArrayList<HashMap<String, String>> imageSlider = new ArrayList<>();
    SliderView sliderView;
    private SliderAdapterExample adapter_slider;

    ImageBadgeView mukesh_begview;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    TextView txt_name, txt_nav_user_name, txt_header_mobileno, txt_department;

    CircularImageView nav_user_profile;
    //Bottom Bar
    RelativeLayout rl_footer;
    RelativeLayout ll_home_bo_categories, ll_home_bo_wishlist, ll_home_bo_profile, ll_home_bo_cart, ll_home_bo_home;

    /*New*/
    CircularImageView user_profile;
    TextView txt_home_name;
    EditText et_search;
    LinearLayout ll_cate_data_found, ll_top_products_data_found, ll_best_selling_data_found, ll_deals_offers_data_found;
    RecyclerView rv_category_list, rv_top_product_list, rv_best_selling_list;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    public Category_Home_Main_list_Adapter adapter;
    public static ArrayList<HashMap<String, String>> cate_array_list = new ArrayList<>();
    public Home_Top_Product_list_Adapter top_prod_adapter;
    public static ArrayList<HashMap<String, String>> top_prod_array_list = new ArrayList<>();

    // public Home_Best_Product_list_Adapter best_prod_adapter;
    public static ArrayList<HashMap<String, String>> best_prod_array_list = new ArrayList<>();

    LinearLayout nav_lin_shop_by_category, nav_lin_my_orders, nav_lin_noti, nav_lin_mywishlist, nav_lin_chanpass,
            nav_lin_feedback, nav_lin_contact_us, nav_lin_share, nav_lin_logout, nav_lin_get_membership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            act = MainActivity.this;
            final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            final LinearLayout holder = findViewById(R.id.holder);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    float scaleFactor = 7f;
                    float slideX = drawerView.getWidth() * slideOffset;

                    holder.setTranslationX(slideX);
                    holder.setScaleX(1 - (slideOffset / scaleFactor));
                    holder.setScaleY(1 - (slideOffset / scaleFactor));

                    super.onDrawerSlide(drawerView, slideOffset);
                }
            };

            drawer.addDrawerListener(toggle);

            //  drawer.setScrimColor(getResources().getColor(R.color.colorPrimary));
            drawer.setScrimColor(Color.TRANSPARENT);
            toggle.syncState();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorDark));
            } else {
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorDark));
            }

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerview = navigationView.getHeaderView(0);
            nav_user_profile = (CircularImageView) headerview.findViewById(R.id.iv_Profile_Pic);
            txt_nav_user_name = (TextView) headerview.findViewById(R.id.txt_welcome_name);
            txt_header_mobileno = (TextView) headerview.findViewById(R.id.txt_header_mobileno);

            nav_lin_shop_by_category = headerview.findViewById(R.id.nav_lin_shop_by_category);
            nav_lin_my_orders = headerview.findViewById(R.id.nav_lin_my_orders);
            nav_lin_noti = headerview.findViewById(R.id.nav_lin_noti);
            nav_lin_mywishlist = headerview.findViewById(R.id.nav_lin_mywishlist);
            nav_lin_chanpass = headerview.findViewById(R.id.nav_lin_chanpass);
            nav_lin_feedback = headerview.findViewById(R.id.nav_lin_feedback);
            nav_lin_contact_us = headerview.findViewById(R.id.nav_lin_contact_us);
            nav_lin_get_membership = headerview.findViewById(R.id.nav_lin_get_membership);
            nav_lin_share = headerview.findViewById(R.id.nav_lin_share);
            nav_lin_logout = headerview.findViewById(R.id.nav_lin_logout);

            //AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, ""), nav_user_profile);
            txt_nav_user_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
            txt_header_mobileno.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, ""));

            /*Action Bar Work Start */
            txt_name = findViewById(R.id.txt_home_name);
            txt_department = findViewById(R.id.txt_department);
            user_profile = findViewById(R.id.user_profile);

            txt_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
            txt_department.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Email, ""));
            txt_department.setVisibility(View.GONE);

            txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(MainActivity.this, ThanksScreen_Activity.class);
                    intent.putExtra("orderDetails", "{\"Status\":\"True\",\"Message\":\"Your order has been successfully placed.!\",\"MainOrder\":[{\"ID\":44,\"OrderNo\":18584149,\"OrderDate\":\"\\/Date(1606761000000)\\/\",\"MemFirstName\":\"f\",\"MemLastName\":\"f\",\"Address1\":\"f\",\"Address2\":\"f\",\"CountryID\":1,\"CountryName\":\"India\",\"StateCode\":42,\"District\":\"\",\"City\":\"d\",\"PinCode\":311001,\"Mobl\":9983333276,\"EMail\":\"medical@gmail.com\",\"FormNo\":1000007,\"UserType\":\"E\",\"Passw\":\"\",\"PayMode\":\"COD\",\"ChDDNo\":0,\"ChDate\":\"\\/Date(-2209008600000)\\/\",\"ChAmt\":331.00,\"BankName\":\"\",\"BranchName\":\"\",\"Remark\":\"\",\"GroupNo\":0,\"OrderType\":\"I\",\"OrderAmt\":306.00,\"OrderItem\":1,\"OrderQty\":1,\"OrderQvp\":0.00,\"OrderCvp\":0.00,\"OrderByFormNo\":1000007,\"ActiveStatus\":\"Y\",\"HostIp\":\"000000000\",\"RecTimeStamp\":\"\\/Date(1606798617350)\\/\",\"IsTransfer\":\"Y\",\"DispatchDate\":null,\"DispatchStatus\":\"N\",\"DispatchQty\":0,\"RemainQty\":0,\"DispatchAmount\":0.00,\"Shipping\":25.00,\"SessID\":1,\"RcptImage\":[],\"StatusCode\":null,\"IDType\":\"N\",\"IDNo\":\"1000007\",\"IsConfirm\":\"N\",\"ConfDate\":null,\"ConfUserId\":955,\"OrderFor\":\"WR\",\"DepositAcNm\":null,\"DepositAcNo\":null,\"DepositBank\":null,\"DepositBranch\":null,\"DepositIfsc\":null,\"MSessID\":6,\"RefFormNo\":0,\"DiscPer\":0.00,\"Discount\":0.00,\"OldShipping\":0.00,\"ShippingStatus\":\"Y\",\"PromoID\":0,\"VoucherAmt\":0.00,\"IsVoucher\":\"N\",\"ActQvp\":0.00,\"Color\":\"\",\"Size\":\"\",\"Packing\":\"0\",\"PackCharge\":0.00,\"DocStatus\":\"P\",\"DocRemarks\":\"\",\"OrderThru\":\"A\",\"GrossOrderAmt\":0.00,\"CompanyID\":1,\"CODCharges\":0.00,\"TotalCGSTAmount\":0.000,\"TotalSGSTAmount\":0.000,\"TotalIGSTAmount\":0.000,\"IsOrderBook\":\"N\",\"OrderBookDate\":null,\"ShipmentID\":\"\",\"OrderReponse\":\"\",\"InvoiceNo\":\"\",\"InvoiceDt\":\"\",\"Remarks\":\"\",\"Del_Status\":\"N\",\"Del_Remarks\":\"\",\"Del_UserID\":0,\"Del_UserName\":\"\",\"Del_RTS\":null,\"StateName\":\"DAMAN \\u0026 DIU\"}],\"OrderDetails\":[{\"OrderNo\":18584149,\"OrderDate\":\"01 Dec 2020\",\"ProductName\":\"Chyawanprash Awaleha 1 Kg..\",\"productcode\":4,\"Price\":306.00,\"ImageUrl\":\"Photo/637414785358316042.jpg\",\"Quantity\":1,\"NetAmount\":306.00,\"OrderDesc\":\"\",\"Bv\":0.00,\"Color\":\"NA\",\"Size\":\"NA\",\"Packing\":\"0\",\"CVP\":0.00,\"totalAmt\":306.00}]}\n");
                    startActivity(intent);*/
                }
            });

            //AppUtils.loadProductImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, ""), user_profile);

            ////nav_lin_my_orders,nav_lin_noti,nav_lin_mywishlist,nav_lin_chanpass,nav_lin_feedback,nav_lin_contact_us,nav_lin_share,nav_lin_logout
            nav_lin_shop_by_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, CategoryMainListActivity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_my_orders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MyOrders_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_noti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, Inbox_Activity.class).putExtra("ComesFrom", "Home"));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_mywishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, Wishlist_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_chanpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, ChangePassword_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, FeedbackActivity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_contact_us.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, ContactUs_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_get_membership.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, PackageListActivity.class));
                    // Toast.makeText(act, "Coming Soon", Toast.LENGTH_SHORT).show();
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String share = "Download " + getResources().getString(R.string.app_name) + " \n\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    i.putExtra(android.content.Intent.EXTRA_TEXT, share);
                    startActivity(Intent.createChooser(i, "Share App via"));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showDialogSignOut(act);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });

            /*Action Bar Work Start */

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

            /*animator = ValueAnimator.ofFloat(0f, 1f);
            if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
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
                    //  startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                }
            });
            mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //  Log.e("count", AppController.getNotification_count().getString(SPUtils.notification_count, "0"));
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
            /*Bottom Bar Work End */

            if (AppUtils.isNetworkAvailable(act)) {

            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);

            /*New*/
            ll_cate_data_found = findViewById(R.id.ll_cate_data_found);
            ll_top_products_data_found = findViewById(R.id.ll_top_products_data_found);
            ll_best_selling_data_found = findViewById(R.id.ll_best_selling_data_found);
            ll_deals_offers_data_found = findViewById(R.id.ll_deals_offers_data_found);
            rv_category_list = findViewById(R.id.rv_category_list);
            rv_top_product_list = findViewById(R.id.rv_top_product_list);
            rv_best_selling_list = findViewById(R.id.rv_best_selling_list);
            et_search = findViewById(R.id.et_search);

            LinearLayout ask = (LinearLayout) findViewById(R.id.ask);
            ask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askSpeechInput();

                }
            });

            /*TOp Category*/
            rv_category_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rv_category_list.setItemAnimator(new DefaultItemAnimator());

            rv_top_product_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rv_top_product_list.setItemAnimator(new DefaultItemAnimator());

            rv_best_selling_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rv_best_selling_list.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                executeCategoryListListRequest();
                executeTopBestListRequest();
                executeSliderPhotosRequest();
                createLoginRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

            et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b)
                        et_search.setCursorVisible(true);
                    else
                        et_search.setCursorVisible(false);
                }
            });

            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        AppUtils.hideKeyboardOnClick(act, view);
                        performSearch();
                        return true;
                    }
                    return false;
                }
            });

            sliderView = findViewById(R.id.imageSlider);

            adapter_slider = new SliderAdapterExample(this);
            sliderView.setSliderAdapter(adapter_slider);

            sliderView.setIndicatorAnimation(IndicatorAnimations.DROP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINDEPTHTRANSFORMATION);
            sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
            sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.companyDark));
            sliderView.setIndicatorUnselectedColor(getResources().getColor(R.color.colorPrimaryDark));
            sliderView.setScrollTimeInSec(3);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();

            sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
                @Override
                public void onIndicatorClicked(int position) {
                    sliderView.setCurrentPagePosition(position);
                }
            });

            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic, ""), nav_user_profile);
            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic, ""), user_profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        if (et_search.getText().toString().isEmpty()) {
            AppUtils.alertDialog(act, "Please Enter search keyword.");
            et_search.requestFocus();
        } else {
            startActivity(new Intent(this, SearchProducts_Activity.class).putExtra("Keyword", et_search.getText().toString()));
            et_search.setText("");
            et_search.setCursorVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            showExitDialog();
        }
    }

    @Override
    protected void onRestart() {
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
        //  mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));
        /*if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) == 0) {
            if (animator == null) {
            } else {
                if (animator.isStarted()) {
                    animator.pause();
                    animator.cancel();
                }
            }
        }*/
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
       /* LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) == 0) {
            if (animator == null) {
            } else {
                if (animator.isStarted()) {
                    animator.pause();
                    animator.cancel();
                }
            }
        }*/
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      /*  if (id == R.id.nav_profile) {
            startActivity(new Intent(act, MyProfileActivity.class));
        }
        else if (id == R.id.nav_mashinari) {
            startActivity(new Intent(act, MachineryActivity.class));
        }else if (id == R.id.nav_notification) {
            startActivity(new Intent(act, NotificationActivity.class));
        } else if (id == R.id.nav_attendance) {
            startActivity(new Intent(act, AttendanceHistoryActivity.class));
        } else if (id == R.id.nav_circuler) {
            startActivity(new Intent(act, CirculerListActivity.class));
        } *//*else if (id == R.id.nav_feedback) {
            startActivity(new Intent(act, FeedbackActivity.class));
        }*//* else if (id == R.id.nav_meeting) {
            startActivity(new Intent(act, MeetingListActivity.class));
        } else if (id == R.id.nav_pooling) {
            startActivity(new Intent(act, PoolingListActivity.class));
        } else if (id == R.id.nav_membership) {
            startActivity(new Intent(act, PackageListActivity.class));
        } else if (id == R.id.nav_logout) {
            AppUtils.showDialogSignOut(act);
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(act, User_Feedback_Activity.class));
        } else if (id == R.id.nav_photo_gallery) {
            startActivity(new Intent(act, PhotoGalleryActivity.class));
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(act, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit from App?"));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(act, CloseActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
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

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*New*/
    // Showing google speech input dialog

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // Receiving speech input

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    et_search.setText(result.get(0));
                }
                break;
            }

        }
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoMenusCategories, TAG);
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
                            cate_array_list.clear();
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
            cate_array_list.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("CID", jsonObject.getString("CID"));
                map.put("Category", jsonObject.getString("Category"));
                map.put("imgPath", SPUtils.cateImageURL + jsonObject.getString("Image"));
                map.put("ShowImage", jsonObject.getString("ShowImage"));
                cate_array_list.add(map);
            }
            showListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (cate_array_list.size() > 0) {
                adapter = new Category_Home_Main_list_Adapter(act, cate_array_list);
                rv_category_list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                rv_category_list.setVisibility(View.VISIBLE);

                ll_cate_data_found.setVisibility(View.VISIBLE);
            } else {
                ll_cate_data_found.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeTopBestListRequest() {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToBestAndTopSellingProduct, TAG);
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
                                if (jsonObject.getJSONArray("HotSelling").length() > 0) {
                                    ll_top_products_data_found.setVisibility(View.VISIBLE);
                                    top_prod_array_list.clear();
                                    getAllProdActivityListResult(jsonObject.getJSONArray("HotSelling"));
                                }
                                if (jsonObject.getJSONArray("TopSelling").length() > 0) {
                                    ll_best_selling_data_found.setVisibility(View.VISIBLE);
                                    best_prod_array_list.clear();
                                    getAllBestProdActivityListResult(jsonObject.getJSONArray("TopSelling"));
                                }
                            } else {
                                ll_top_products_data_found.setVisibility(View.GONE);
                                ll_best_selling_data_found.setVisibility(View.GONE);
                                showProdListView();
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

    private void getAllProdActivityListResult(JSONArray jsonArray) {
        try {
            top_prod_array_list.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ProdId", jsonObject.getString("ProdId"));
                map.put("ProductName", jsonObject.getString("ProductName"));
                map.put("ImagePath", SPUtils.cateImageURL + jsonObject.getString("ImagePath"));
                map.put("MRP", jsonObject.getString("MRP"));
                map.put("DP", jsonObject.getString("DP"));
                map.put("CatId", jsonObject.getString("CatId"));
                map.put("Discount", jsonObject.getString("Discount"));
                top_prod_array_list.add(map);
            }
            showProdListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showProdListView() {
        try {
            if (top_prod_array_list.size() > 0) {
                top_prod_adapter = new Home_Top_Product_list_Adapter(act, top_prod_array_list);
                rv_top_product_list.setAdapter(top_prod_adapter);
                top_prod_adapter.notifyDataSetChanged();
                rv_top_product_list.setVisibility(View.VISIBLE);
            } else {
                ll_top_products_data_found.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void getAllBestProdActivityListResult(JSONArray jsonArray) {
        try {
            best_prod_array_list.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("ProdId", jsonObject.getString("ProdId"));
                map.put("ProductName", jsonObject.getString("ProductName"));
                map.put("ImagePath", SPUtils.cateImageURL + jsonObject.getString("ImagePath"));
                map.put("MRP", jsonObject.getString("MRP"));
                map.put("DP", jsonObject.getString("DP"));
                best_prod_array_list.add(map);
            }
            showBestProdListView();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showBestProdListView() {
        try {
            if (best_prod_array_list.size() > 0) {
                top_prod_adapter = new Home_Top_Product_list_Adapter(act, best_prod_array_list);
                rv_best_selling_list.setAdapter(top_prod_adapter);
                top_prod_adapter.notifyDataSetChanged();
                rv_best_selling_list.setVisibility(View.VISIBLE);
            } else {
                ll_best_selling_data_found.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    /*Slider*/
    public void addNewItem(String URL) {
        SliderItem sliderItem = new SliderItem();
        sliderItem.setDescription("");
        sliderItem.setImageUrl("" + URL);
        adapter_slider.addItem(sliderItem);
    }

    private void executeSliderPhotosRequest() {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToSelect_HomeSlider, TAG);
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
                                for (int i = 0; i < jsonObject.getJSONArray("Data").length(); i++) {
                                    JSONObject jsonObject1 = jsonObject.getJSONArray("Data").getJSONObject(i);
                                    addNewItem(SPUtils.productImageURL + jsonObject1.getString("Image"));
                                }
                            } else {
                                //   AppUtils.alertDialog(act, jsonObject.getString("Message"));
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

    private void createLoginRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        // AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNoOrEmailID", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, "")));
                            postParameters.add(new BasicNameValuePair("Password", "" + AppController.getSpUserInfo().getString(SPUtils.USER_Passw, "")));
                            //  postParameters.add(new BasicNameValuePair("GCMDeviceId", "" + FirebaseInstanceId.getInstance().getToken().toString()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodtoLogin, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            //  AppUtils.dismissProgressDialog();

                            final JSONObject jsonArray = new JSONObject(resultData);

                            if (jsonArray.getString("Status").equalsIgnoreCase("true")) {
                                saveLoginUserInfo(jsonArray.getJSONArray("Data"));
                            } else {
                                AppController.getSpUserInfo().edit().clear().commit();
                                AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, false).commit();

                                AppController.productList.clear();
                                AppController.selectedProductList.clear();

                                AppController.countryList.clear();
                                AppController.categoryList.clear();
                                AppController.homeSliderList.clear();
                                CheckoutToPay_Activity.deliveryAddressList.clear();

                                Intent intent = new Intent(act, Login_Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
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

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            AppUtils.dismissProgressDialog();

            AppController.getSpUserInfo().edit().clear().commit();
            String asf = "India";
            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_ID, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_FORM_NUMBER, jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_Name, jsonArray.getJSONObject(0).getString("MemFirstName"))
                    .putString(SPUtils.USER_Last_Name, jsonArray.getJSONObject(0).getString("MemLastName"))
                    .putString(SPUtils.USER_Address, jsonArray.getJSONObject(0).getString("Address1"))
                    .putString(SPUtils.USER_PinCode, jsonArray.getJSONObject(0).getString("PinCode"))
                    .putString(SPUtils.USER_City, jsonArray.getJSONObject(0).getString("City"))
                    .putString(SPUtils.USER_State, jsonArray.getJSONObject(0).getString("StateCode"))
                    /*.putString(SPUtils.USER_Country, jsonArray.getJSONObject(0).getString("Country"))*/

                    .putString(SPUtils.USER_Country, asf)
                    .putString(SPUtils.USER_MobileNo, jsonArray.getJSONObject(0).getString("Mobl"))
                    .putString(SPUtils.USER_Email, jsonArray.getJSONObject(0).getString("EMail"))
                    .putString(SPUtils.USER_Passw, jsonArray.getJSONObject(0).getString("Passw"))
                    .putString(SPUtils.USER_Doj, jsonArray.getJSONObject(0).getString("Doj"))
                    .putString(SPUtils.USER_MembershipStatus, jsonArray.getJSONObject(0).getString("status"))
                    .putString(SPUtils.USER_profile_pic,"" + SPUtils.ProfileURL + jsonArray.getJSONObject(0).getString("Image"))
                    .commit();

            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic, ""),user_profile);
            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic, ""),nav_user_profile);
            //for Login Successfully.
            //  AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

            //  finish();
            //  startActivity(new Intent(act, MainActivity.class));

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

}
