package com.vpipl.drdawakhana;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vpipl.drdawakhana.Adapters.VipinAdapter;
import com.vpipl.drdawakhana.model.Vipindata;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.speech.RecognizerIntent;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.drdawakhana.Adapters.ExpandableListAdapter;
import com.vpipl.drdawakhana.Adapters.ImageSliderViewPagerAdapter;
import com.vpipl.drdawakhana.model.ProductList;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.CirclePageIndicator;
import com.vpipl.drdawakhana.Utils.CircularImageView;
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeView;

import static android.view.View.GONE;

public class HomeDashboard_Activity extends AppCompatActivity {
    public static String TAG = "Home_Activity";
    public static DrawerLayout drawer;
    public static NavigationView navigationView;
    public MenuItem menu_Cart = null, menu_Login = null;
    public ActionBarDrawerToggle drawerToggle;
    CirclePageIndicator imagePageIndicator;
    LinearLayout LLFruits, LLFruitsProducts;
    Button btn_view_all_fruits;
    LinearLayout LLVegetables, LLVegetablesProducts;
    Button btn_view_all_Vegetables;
    EditText et_search;
    CircularImageView profileImage;
    int currentPage = 0;
    Timer timer;
    TextView txt_welcome_name;
    private ViewPager pager_slider;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    boolean isBigScreen = false;
    DatabaseHandler databaseHandler;
    ImageSliderViewPagerAdapter imageSliderViewPagerAdapter;

    // vipin
    private RecyclerView noti_rv;
    private List<Vipindata> notification_data;
    private VipinAdapter noti_adapter;
    String path = "";
    ImageBadgeView ibv_cartcount;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    LinearLayout nav_lin_history, nav_lin_shop_by_category, nav_lin_cart, nav_lin_mywishlist, nav_lin_noti, nav_lin_profile, nav_lin_chanpass, nav_lin_share;
    Activity act = HomeDashboard_Activity.this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vipinhome);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {

            FirebaseApp.initializeApp(this);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            AppUtils.setActionbarTitle(getSupportActionBar(), act);

            LLFruits = (LinearLayout) findViewById(R.id.LLFruits);
            btn_view_all_fruits = (Button) findViewById(R.id.btn_view_all_fruits);
            LLFruitsProducts = (LinearLayout) findViewById(R.id.LLFruitsProducts);
            LLFruitsProducts.setVisibility(GONE);

            LLVegetables = (LinearLayout) findViewById(R.id.LLvegatables);
            btn_view_all_Vegetables = (Button) findViewById(R.id.btn_view_all_vegatables);
            LLVegetablesProducts = (LinearLayout) findViewById(R.id.LLVegatablesProducts);
            LLVegetablesProducts.setVisibility(GONE);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            pager_slider = (ViewPager) findViewById(R.id.pager_slider);
            imagePageIndicator = (CirclePageIndicator) findViewById(R.id.imagePageIndicator);
            navigationView = (NavigationView) findViewById(R.id.nav_view);

            pager_slider.setClipToPadding(false);
            // set padding manually, the more you set the padding the more you see of prev & next page
            pager_slider.setPadding(40, 0, 40, 0);
            // sets a margin b/w individual pages to ensure that there is a gap b/w them
            pager_slider.setPageMargin(10);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
            toggle.setHomeAsUpIndicator(R.drawable.ic_menu);
            toggle.syncState();

            getSupportActionBar().setDisplayShowTitleEnabled(false);

            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            });

            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = navHeaderView.findViewById(R.id.txt_welcome_name);

            profileImage = navHeaderView.findViewById(R.id.iv_Profile_Pic);
            LinearLayout LL_Nav = navHeaderView.findViewById(R.id.LL_Nav);

            nav_lin_history = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_history);
            nav_lin_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MyOrders_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_shop_by_category = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_shop_by_category);
            nav_lin_shop_by_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, CategoryMainListActivity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_cart = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_cart);
            nav_lin_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, AddCartCheckOut_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_mywishlist = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_mywishlist);
            nav_lin_mywishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, Wishlist_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_noti = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_noti);
            nav_lin_noti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, Inbox_Activity.class).putExtra("ComesFrom", "Home"));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_profile = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_profile);
            nav_lin_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(act, Login_Activity.class));
                    else
                        startActivity(new Intent(act, UpdateProfile_Activity.class));
                }
            });
            nav_lin_chanpass = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_chanpass);
            nav_lin_chanpass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, ChangePassword_Activity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            LinearLayout nav_lin_feedback = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_feedback);
            nav_lin_feedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, FeedbackActivity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            LinearLayout nav_lin_contact_us = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_contact_us);
            nav_lin_contact_us.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, ContactUs_Activity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });
            nav_lin_share = (LinearLayout) navHeaderView.findViewById(R.id.nav_lin_share);
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
                }
            });

            et_search = (EditText) findViewById(R.id.et_search);
            LinearLayout ask = (LinearLayout) findViewById(R.id.ask);
            ask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askSpeechInput();

                }
            });

            expListView = (ExpandableListView) findViewById(R.id.left_drawer);
            expListView.setVisibility(GONE);
            imageSliderViewPagerAdapter = new ImageSliderViewPagerAdapter(act);

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

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

            LL_Nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        startActivity(new Intent(act, UpdateProfile_Activity.class));
                    }

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
            });

            btn_view_all_fruits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(act, ProductListGrid_Activity.class);
                    intent.putExtra("HID", "1");
                    intent.putExtra("ChildItemTitle", "Fruits");
                    startActivity(intent);
                }
            });

            btn_view_all_Vegetables.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(act, ProductListGrid_Activity.class);
                    intent.putExtra("HID", "2");
                    intent.putExtra("ChildItemTitle", "Vegetables");
                    startActivity(intent);
                }
            });

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            double wi = (double) width / (double) dm.xdpi;
            double hi = (double) height / (double) dm.ydpi;
            double x = Math.pow(wi, 2);
            double y = Math.pow(hi, 2);
            double screenInches = Math.sqrt(x + y);

            if (screenInches >= 7.0)
                isBigScreen = true;

            databaseHandler = new DatabaseHandler(this);


            if (AppUtils.isNetworkAvailable(this)) {
                imageSliderViewPagerAdapter.notifyDataSetChanged();

                executeGetCategoryRequest();

                if (AppController.homeSliderList.size() <= 0)
                    executeToGetImageSlider();
                else
                    setImageSlider();
            } else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));


            String token = FirebaseInstanceId.getInstance().getToken().toString();
            Log.e("FCM Token : ", token);

            TextView textView = (TextView) findViewById(R.id.free_delivery_label);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            animation.setRepeatCount(Animation.INFINITE);
            textView.startAnimation(animation);

            noti_rv = (RecyclerView) findViewById(R.id.noti_recycler_view);
            noti_rv.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
            layoutManager.setReverseLayout(false);
            noti_rv.setLayoutManager(new GridLayoutManager(this, 3));

            notification_data = new ArrayList<>();
            noti_adapter = new VipinAdapter(notification_data, this);

            if (AppUtils.isNetworkAvailable(act)) {
                NotificationLlist();
            } else {

            }

            LinearLayout profile_me = (LinearLayout) findViewById(R.id.profile_me);
            profile_me.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                        startActivity(new Intent(act, Login_Activity.class));
                    else
                        startActivity(new Intent(act, UpdateProfile_Activity.class));
                }
            });

            LinearLayout myorder = (LinearLayout) findViewById(R.id.myorder);
            myorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MyOrders_Activity.class));
                }
            });

            LinearLayout notification = (LinearLayout) findViewById(R.id.notification);
            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, Inbox_Activity.class).putExtra("ComesFrom", "Home"));
                }
            });

            LinearLayout cart = (LinearLayout) findViewById(R.id.cart);
            ibv_cartcount = (ImageBadgeView) findViewById(R.id.ibv_cartcount);
            int asd = AppController.selectedProductList.size();
            Log.e("asd", "" + asd);
            ibv_cartcount.setBadgeValue(AppController.selectedProductList.size())
                    .setBadgePosition(BadgePosition.TOP_RIGHT)
                    .setShowCounter(true);


            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, AddCartCheckOut_Activity.class));
                }
            });

            LinearLayout home = (LinearLayout) findViewById(R.id.home);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(act, MainActivity.class));
                    finish();
                }
            });

            LinearLayout logout = (LinearLayout) findViewById(R.id.logout);
            logout.setVisibility(GONE);
            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                logout.setVisibility(View.VISIBLE);
            }

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.showDialogSignOut(act);
                }
            });

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

    public void LoadNavigationHeaderItems() {
        txt_welcome_name.setText("Guest");
      /*  profileImage.setImageResource(0);
        profileImage.setImageDrawable(null);
        profileImage.setBackground(getResources().getDrawable(R.drawable.logo));*/

        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String welcome_text = WordUtils.capitalizeFully(AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
            txt_welcome_name.setText(welcome_text);

        }
    }

    private void setImageSlider() {
        try {

            imageSliderViewPagerAdapter.notifyDataSetChanged();

            pager_slider.setAdapter(new ImageSliderViewPagerAdapter(act));

            final float density = getResources().getDisplayMetrics().density;
            imagePageIndicator.setFillColor(getResources().getColor(R.color.color__bg_orange));
            imagePageIndicator.setStrokeColor(getResources().getColor(R.color.color_cccccc));
            imagePageIndicator.setStrokeWidth(0.5f);
            imagePageIndicator.setRadius(6 * density);
            imagePageIndicator.setViewPager(pager_slider);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == AppController.homeSliderList.size()) {
                        currentPage = 0;
                    }
                    pager_slider.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled

                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 500, 3000);


            pager_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    currentPage = position;
                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            executeUpdateDeviceID();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void setOptionMenu() {
        try {

            setBadgeCount(act, (AppController.selectedProductList.size()));
            if (menu_Login != null) {
                if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                    menu_Login.setIcon(getResources().getDrawable(R.drawable.ic_icon_log_out));
                } else {
                    menu_Login.setIcon(getResources().getDrawable(R.drawable.ic_icon_user));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBadgeCount(Context context, int count) {
        try {
            if (menu_Cart != null) {
                LayerDrawable icon = (LayerDrawable) menu_Cart.getIcon();
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
    }*/
    public void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(act, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure ?? Do you want to Exit ?"));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText("No");
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

    private void enableExpandableList() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        List<String> Empty = new ArrayList<>();
        List<String> cat2 = new ArrayList<>();

        for (int i = 0; i < AppController.categoryList.size(); i++) {
            cat2.add(AppController.categoryList.get(i).get("Heading"));
        }

        listDataHeader.add("Shop Product");
        listDataChild.put(listDataHeader.get(0), cat2);
        /*listDataHeader.add("My Orders");*/
        /*listDataChild.put(listDataHeader.get(1), Empty);
        listDataHeader.add("My Profile");
        listDataChild.put(listDataHeader.get(2), Empty);
        listDataHeader.add("Notifications");
        listDataChild.put(listDataHeader.get(3), Empty);
        listDataHeader.add("Change Password");*/
        listDataHeader.add("Change Password");
        listDataChild.put(listDataHeader.get(1), Empty);
        listDataHeader.add("Share Your Feedback");
        listDataChild.put(listDataHeader.get(1), Empty);
        listDataHeader.add("Share App");
        listDataChild.put(listDataHeader.get(2), Empty);


        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);
                if (GroupTitle.trim().equalsIgnoreCase("My Orders")) {
                    startActivity(new Intent(act, MyOrders_Activity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Notifications")) {

                    startActivity(new Intent(act, Inbox_Activity.class).putExtra("ComesFrom", "Home"));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("My Profile")) {
                    startActivity(new Intent(act, UpdateProfile_Activity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                if (GroupTitle.trim().equalsIgnoreCase("Change Password")) {
                    startActivity(new Intent(act, ChangePassword_Activity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                if (GroupTitle.trim().equalsIgnoreCase("Share Your Feedback")) {
                    startActivity(new Intent(act, FeedbackActivity.class));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Share App")) {

                    String share = "Download BhajiLelo App\n\nhttps://play.google.com/store/apps/details?id=com.vpiplapnakirana";

                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    i.putExtra(android.content.Intent.EXTRA_TEXT, share);
                    startActivity(Intent.createChooser(i, "Share App via"));

                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                if (ChildItemTitle.trim().equalsIgnoreCase("")) {
                    startActivity(new Intent(act, UpdateProfile_Activity.class));
                } else {
                    Intent intent = new Intent(act, ProductListGrid_Activity.class);
                    String HID = "";

                    for (int i = 0; i < AppController.categoryList.size(); i++) {
                        String category = AppController.categoryList.get(i).get("Heading");
                        if ((ChildItemTitle.equalsIgnoreCase(category))) {
                            HID = AppController.categoryList.get(i).get("HID");
                        }
                    }

                    intent.putExtra("HID", "" + HID);
                    intent.putExtra("ChildItemTitle", ChildItemTitle);
                    startActivity(intent);
                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

   /* private void executeToGetFruitListRequest() {
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
                            postParameters.add(new BasicNameValuePair("HID", "1"));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetProductList, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

//                            Toast.makeText(act, "API Response", Toast.LENGTH_SHORT).show();

                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("Data");

                                if (jsonArrayProductList.length() > 0) {
                                    DrawFruitsProductsList(jsonArrayProductList);
                                }
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
    }*/

    /*public void DrawFruitsProductsList(JSONArray Jarray) {

        Typeface typeface = ResourcesCompat.getFont(act, R.font.opensans_regular);

//        TextView txt_1 = (TextView) findViewById(R.id.txt_1);
//        for (int i = 0; i < AppController.categoryList.size(); i++) {
//            String HID = AppController.categoryList.get(i).get("HID");
//            if ((HID.equalsIgnoreCase("1"))) {
//                txt_1.setText("" + AppController.categoryList.get(i).get("Heading"));
//            }
//        }

        LLFruits.removeAllViews();
        LLFruitsProducts.setVisibility(GONE);
        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (isBigScreen)
            layoutParams.setMargins((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));
        else
            layoutParams.setMargins((int) (5 * density), (int) (5 * density), (int) (5 * density), (int) (5 * density));

        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams.setMargins(0, (int) (5 * density), 0, (int) (0 * density));

        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (120 * density), (int) (120 * density));

        for (int i = 0; i < Jarray.length(); i++) {
            try {
                JSONObject Jobject = Jarray.getJSONObject(i);

                final ProductList list = new ProductList(
                        "" + Jobject.getString("ProdId"),
                        "" + (Jobject.getString("ProductName")),
                        "" + Jobject.getString("MRP"),
                        "" + Jobject.getString("MRPTitle"),
                        "0",
                        "" + Jobject.getString("ProductQtyTitle"),
                        "" + Jobject.getString("ProductQtyValue"),
                        "" + Jobject.getString("ProductImg"),
                        "0",
                        "NA",
                        "" + Jobject.getString("MRP")
                );

                AppController.FruitsList.add(list);

                String ProductName = AppUtils.CapsFirstLetterString(Jobject.getString("ProductName"));
                String NewMRP = Jobject.getString("ProductQtyTitle");
                String imgpath = Jobject.getString("ProductImg");

                FrameLayout FL = new FrameLayout(getApplicationContext());
                FL.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                FL.setLayoutParams(layoutParams);

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
                LL.setMinimumHeight((int) (150 * density));

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setMinimumWidth((int) (120 * density));
                imageView.setMinimumHeight((int) (120 * density));
                imageView.setMaxHeight((int) (120 * density));
                imageView.setMaxWidth((int) (120 * density));
                imageView.setLayoutParams(imageparams);

                AppUtils.loadProductImage(act, imgpath, imageView);

                TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setSingleLine(true);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);

                if (isBigScreen)
                    tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                else
                    tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                tvproductname.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvproductname.setText(Html.fromHtml(ProductName));

                tvproductname.setTypeface(typeface);

                final TextView tvproductmrp = new TextView(getApplicationContext());

                if (isBigScreen)
                    tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                else
                    tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                tvproductmrp.setLayoutParams(textparams);
                tvproductmrp.setTextColor(getResources().getColor(android.R.color.black));
                tvproductmrp.setTypeface(typeface);

                LinearLayout LLbottom = new LinearLayout(getApplicationContext());
                LLbottom.setOrientation(LinearLayout.HORIZONTAL);
                LLbottom.setGravity(Gravity.END);
                LLbottom.setGravity(Gravity.RIGHT);
                LLbottom.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams imageparamssmall = new LinearLayout.LayoutParams((int) (35 * density), (int) (35 * density));

                LinearLayout LLplusminus = new LinearLayout(getApplicationContext());
                LLplusminus.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams textparamsplusminus = new LinearLayout.LayoutParams((int) (25 * density), (int) (25 * density));
                textparamsplusminus.setMargins((int) (2 * density), (int) (5 * density), (int) (2 * density), (int) (5 * density));

                TextView tvplus = new TextView(getApplicationContext());
                TextView tvminus = new TextView(getApplicationContext());
                final TextView tvvalue = new TextView(getApplicationContext());
                final TextView tvvalueKG = new TextView(getApplicationContext());

                tvplus.setText("+");
                tvminus.setText("-");
                tvvalue.setText("1");

                tvplus.setGravity(Gravity.CENTER);
                tvminus.setGravity(Gravity.CENTER);
                tvvalue.setGravity(Gravity.CENTER);
                tvvalueKG.setGravity(Gravity.CENTER);

                tvvalue.setTextColor(Color.BLACK);
                tvminus.setTextColor(Color.BLACK);
                tvplus.setTextColor(Color.BLACK);
                tvvalueKG.setTextColor(Color.BLACK);

                tvplus.setSingleLine(true);
                tvminus.setSingleLine(true);
                tvvalue.setSingleLine(true);
                tvvalueKG.setSingleLine(true);

                tvplus.setTypeface(typeface);
                tvminus.setTypeface(typeface);
                tvvalue.setTypeface(typeface);
                tvvalueKG.setTypeface(typeface);

                tvminus.setLayoutParams(textparamsplusminus);
                tvplus.setLayoutParams(textparamsplusminus);
                tvvalue.setLayoutParams(textparamsplusminus);

                tvvalueKG.setPadding((int) (5 * density), (int) (2 * density), (int) (5 * density), (int) (2 * density));

                tvminus.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                tvplus.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                tvvalue.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                tvvalueKG.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));

                tvvalueKG.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);

                tvminus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int i;
                        i = Integer.parseInt(tvvalue.getText().toString());
                        if (i > 1) {
                            i = i - 1;
                        }
                        tvvalue.setText("" + i);
                    }
                });

                tvplus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int i;
                        i = Integer.parseInt(tvvalue.getText().toString());
                        i = i + 1;
                        tvvalue.setText("" + i);
                    }
                });

                if (Float.parseFloat(list.getProductRate()) > 0) {
                    tvproductmrp.setText(NewMRP);
                    LLplusminus.addView(tvminus);
                    LLplusminus.addView(tvvalue);
                    LLplusminus.addView(tvplus);

                } else {
                    final List<ProductPacking> productPackingArrayListList = databaseHandler.getProductPacking(Integer.parseInt(list.getProductId()));

                    try {
                        tvproductmrp.setText("₹ " + " " + productPackingArrayListList.get(3).getNewMRP());
                        tvvalueKG.setText(productPackingArrayListList.get(3).getPackingText());
                        LLplusminus.addView(tvvalueKG);

                        list.setselectedPackingName(productPackingArrayListList.get(3).getPackingText());
                        list.setselectedPackingId(productPackingArrayListList.get(3).getPackingID());
                        list.setProductQty("1");
                        list.setNewMRP(productPackingArrayListList.get(3).getNewMRP());
                    } catch (Exception e) {
                        e.printStackTrace();

                        tvproductmrp.setText("₹ " + " " + productPackingArrayListList.get(0).getNewMRP());
                        tvvalueKG.setText(productPackingArrayListList.get(0).getPackingText());
                        LLplusminus.addView(tvvalueKG);

                        list.setselectedPackingName(productPackingArrayListList.get(0).getPackingText());
                        list.setselectedPackingId(productPackingArrayListList.get(0).getPackingID());
                        list.setProductQty("1");
                        list.setNewMRP(productPackingArrayListList.get(0).getNewMRP());
                    }
                    tvvalueKG.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final String[] stateArray = new String[productPackingArrayListList.size()];
                            for (int i = 0; i < productPackingArrayListList.size(); i++) {
                                stateArray[i] = productPackingArrayListList.get(i).getPackingText();
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(act);
                            builder.setTitle("Select Quantity");
                            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    // Do something with the selection

                                    String NewMRP = "₹ " + " " + productPackingArrayListList.get(item).getNewMRP();
                                    tvproductmrp.setText(NewMRP);

                                    tvvalueKG.setText(productPackingArrayListList.get(item).getPackingText());

                                    list.setselectedPackingName(productPackingArrayListList.get(item).getPackingText());
                                    list.setselectedPackingId(productPackingArrayListList.get(item).getPackingID());
                                    list.setProductQty("1");

                                    list.setNewMRP(productPackingArrayListList.get(item).getNewMRP());

//                                selectedProducts.setNewDP(productPackingArrayListList.get(item).getNewDP());
//                                selectedProducts.setNewMRP(productPackingArrayListList.get(item).getNewMRP());
                                }
                            });
                            builder.create().show();
                        }
                    });
                }

                ImageView add_to_cart = new ImageView(getApplicationContext());
                add_to_cart.setMinimumHeight((int) (35 * density));
                add_to_cart.setMaxHeight((int) (35 * density));
                add_to_cart.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                add_to_cart.setLayoutParams(imageparamssmall);

                add_to_cart.setImageDrawable(getResources().getDrawable(R.drawable.icon_bag));

                add_to_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addItemInSelectedProductList(list, tvvalue.getText().toString());
                    }
                });

                LLbottom.addView(LLplusminus);
                LLbottom.addView(add_to_cart);

                LL.setId(i);
                LL.addView(imageView);
                LL.addView(tvproductname);
                LL.addView(tvproductmrp);
                LL.addView(LLbottom);

                FL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(act, ProductListGrid_Activity.class);
                        intent.putExtra("HID", "1");
                        intent.putExtra("ChildItemTitle", "Fruits");
                        startActivity(intent);

                    }
                });

                FL.addView(LL);
                LLFruits.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/


   /* public void DrawCategoriesList() {

        Typeface typeface = ResourcesCompat.getFont(act, R.font.opensans_regular);

//        TextView txt_1 = (TextView) findViewById(R.id.txt_1);
//        for (int i = 0; i < AppController.categoryList.size(); i++) {
//            String HID = AppController.categoryList.get(i).get("HID");
//            if ((HID.equalsIgnoreCase("1"))) {
//                txt_1.setText("" + AppController.categoryList.get(i).get("Heading"));
//            }
//        }

        LLFruits.removeAllViews();
        LLFruitsProducts.setVisibility(View.VISIBLE);
        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (isBigScreen)
            layoutParams.setMargins((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));
        else
            layoutParams.setMargins((int) (5 * density), (int) (5 * density), (int) (5 * density), (int) (5 * density));

        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams.setMargins(0, (int) (5 * density), 0, (int) (5 * density));

        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (120 * density), LinearLayout.LayoutParams.WRAP_CONTENT);
        imageparams.setMargins(0, (int) (5 * density), 0, (int) (5 * density));

        for (int i = AppController.categoryList.size(); i > 0; i--) {
            try {
                HashMap<String, String> map = AppController.categoryList.get(i - 1);

                String ProductName = AppUtils.CapsFirstLetterString(map.get("Heading"));
                String imgpath = map.get("ImgPath");
                String HID = map.get("HID");

                FrameLayout FL = new FrameLayout(getApplicationContext());
                FL.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                FL.setLayoutParams(layoutParams);

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
                LL.setMinimumHeight((int) (150 * density));
                LL.setMinimumWidth((int) (120 * density));
                LL.setGravity(Gravity.CENTER_HORIZONTAL);

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setMinimumWidth((int) (150 * density));
                imageView.setMinimumHeight((int) (100 * density));
                imageView.setLayoutParams(imageparams);
                imageView.setAdjustViewBounds(true);
                AppUtils.loadProductImage(act, imgpath, imageView);

                final TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setSingleLine(true);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);
                tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tvproductname.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvproductname.setText(Html.fromHtml(ProductName));
                tvproductname.setTypeface(typeface);

                final TextView tvproductmrp = new TextView(getApplicationContext());
                tvproductmrp.setLayoutParams(textparams);
                tvproductmrp.setTextColor(getResources().getColor(android.R.color.white));
                tvproductmrp.setTypeface(typeface);
                tvproductmrp.setText("View All");
                tvproductmrp.setAllCaps(false);
                tvproductmrp.setPadding((int) (10 * density), (int) (5 * density), (int) (10 * density), (int) (5 * density));
                tvproductmrp.setBackground(getResources().getDrawable(R.drawable.round_rectangle_orange));

                tvproductmrp.setId(Integer.parseInt(HID));
                FL.setId(Integer.parseInt(HID));

                LL.addView(tvproductname);
                LL.addView(imageView);
                LL.addView(tvproductmrp);

                FL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(act, ProductListGrid_Activity.class);
                        intent.putExtra("HID", "" + tvproductmrp.getId());
                        intent.putExtra("ChildItemTitle", "" + tvproductname.getText().toString());
                        startActivity(intent);
                    }
                });

                FL.addView(LL);
                LLFruits.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/


    private void addItemInSelectedProductList(ProductList productList, String quantity) {
        try {
            boolean already_exists = false;
            int position = 0;

            for (int i = 0; i < AppController.selectedProductList.size(); i++) {
                if (AppController.selectedProductList.get(i).getProductName().equalsIgnoreCase(productList.getProductName())) {
                    already_exists = true;
                    position = i;
                }
            }

            ProductList list = new ProductList(productList.getProductId(), productList.getProductName(), productList.getProductRate(), productList.getProductRateTitle(), quantity, productList.getProductQtyTitle(), productList.getProductQtyValue(), productList.getProductImage(), productList.getselectedPackingId(), productList.getselectedPackingName(), productList.getNewMRP());

            if (!already_exists) {
                AppController.selectedProductList.add(list);
            } else {
                AppController.selectedProductList.set(position, list);
            }

            //setBadgeCount(act, AppController.selectedProductList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void executeToGetVegetablesListRequest() {
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
                            postParameters.add(new BasicNameValuePair("HID", "2"));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToGetProductList, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

//                            Toast.makeText(act, "API Response", Toast.LENGTH_SHORT).show();

                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayProductList = jsonObject.getJSONArray("Data");

                                if (jsonArrayProductList.length() > 0) {
                                    DrawVegetablesProductsList(jsonArrayProductList);
                                }
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
    }*/

   /* public void DrawVegetablesProductsList(JSONArray Jarray) {

        TextView txt_2 = (TextView) findViewById(R.id.txt_2);
        for (int i = 0; i < AppController.categoryList.size(); i++) {
            String HID = AppController.categoryList.get(i).get("HID");
            if ((HID.equalsIgnoreCase("2"))) {
                txt_2.setText("" + AppController.categoryList.get(i).get("Heading"));
            }
        }

        LLVegetables.removeAllViews();
        LLVegetablesProducts.setVisibility(View.VISIBLE);
        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (isBigScreen)
            layoutParams.setMargins((int) (10 * density), (int) (10 * density), (int) (10 * density), (int) (10 * density));
        else
            layoutParams.setMargins((int) (5 * density), (int) (5 * density), (int) (5 * density), (int) (5 * density));

        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textparams.setMargins(0, (int) (5 * density), 0, (int) (0 * density));

        LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams((int) (120 * density), (int) (120 * density));

        for (int i = 0; i < Jarray.length(); i++) {
            try {
                JSONObject Jobject = Jarray.getJSONObject(i);

                final ProductList list = new ProductList(
                        "" + Jobject.getString("ProdId"),
                        "" + (Jobject.getString("ProductName")),
                        "" + Jobject.getString("MRP"),
                        "" + Jobject.getString("MRPTitle"),
                        "0",
                        "" + Jobject.getString("ProductQtyTitle"),
                        "" + Jobject.getString("ProductQtyValue"),
                        "" + Jobject.getString("ProductImg"),
                        "0",
                        "NA",
                        "" + Jobject.getString("MRP")
                );

                AppController.VegatblesList.add(list);

                Typeface typeface = ResourcesCompat.getFont(act, R.font.opensans_regular);

                String ProductName = AppUtils.CapsFirstLetterString(Jobject.getString("ProductName"));
                String NewMRP = Jobject.getString("ProductQtyTitle");
                String imgpath = Jobject.getString("ProductImg");
//                String imgpath = "http://bhajilelo.versatileitsolution.com/ProductImg/ic_tometo.jpg";

                FrameLayout FL = new FrameLayout(getApplicationContext());
                FL.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                FL.setLayoutParams(layoutParams);

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setMinimumHeight((int) (150 * density));
                LL.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setMinimumWidth((int) (120 * density));
                imageView.setMinimumHeight((int) (120 * density));
                imageView.setMaxHeight((int) (120 * density));
                imageView.setMaxWidth((int) (120 * density));
                imageView.setLayoutParams(imageparams);

                AppUtils.loadProductImage(act, imgpath, imageView);

                TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setLayoutParams(textparams);
                tvproductname.setMaxLines(1);
                tvproductname.setSingleLine(true);
                tvproductname.setEllipsize(TextUtils.TruncateAt.END);

                if (isBigScreen)
                    tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                else
                    tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                tvproductname.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvproductname.setText(Html.fromHtml(ProductName));
                tvproductname.setTypeface(typeface);

                final TextView tvproductmrp = new TextView(getApplicationContext());
                if (isBigScreen)
                    tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                else
                    tvproductmrp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                tvproductmrp.setLayoutParams(textparams);
                tvproductmrp.setTextColor(getResources().getColor(android.R.color.black));
                tvproductmrp.setText(NewMRP);
                tvproductmrp.setTypeface(typeface);

                LinearLayout LLbottom = new LinearLayout(getApplicationContext());
                LLbottom.setOrientation(LinearLayout.HORIZONTAL);
                LLbottom.setGravity(Gravity.END);
                LLbottom.setGravity(Gravity.RIGHT);
                LLbottom.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams imageparamssmall = new LinearLayout.LayoutParams((int) (35 * density), (int) (35 * density));

                LinearLayout LLplusminus = new LinearLayout(getApplicationContext());
                LLplusminus.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams textparamsplusminus = new LinearLayout.LayoutParams((int) (25 * density), (int) (25 * density));
                textparamsplusminus.setMargins((int) (2 * density), (int) (5 * density), (int) (2 * density), (int) (5 * density));

                TextView tvplus = new TextView(getApplicationContext());
                TextView tvminus = new TextView(getApplicationContext());
                final TextView tvvalue = new TextView(getApplicationContext());
                final TextView tvvalueKG = new TextView(getApplicationContext());

                tvplus.setText("+");
                tvminus.setText("-");
                tvvalue.setText("1");

                tvplus.setGravity(Gravity.CENTER);
                tvminus.setGravity(Gravity.CENTER);
                tvvalue.setGravity(Gravity.CENTER);
                tvvalueKG.setGravity(Gravity.CENTER);

                tvplus.setSingleLine(true);
                tvminus.setSingleLine(true);
                tvvalue.setSingleLine(true);
                tvvalueKG.setSingleLine(true);

                tvplus.setTypeface(typeface);
                tvminus.setTypeface(typeface);
                tvvalue.setTypeface(typeface);
                tvvalueKG.setTypeface(typeface);

                tvvalue.setTextColor(Color.BLACK);
                tvminus.setTextColor(Color.BLACK);
                tvplus.setTextColor(Color.BLACK);
                tvvalueKG.setTextColor(Color.BLACK);

                tvminus.setLayoutParams(textparamsplusminus);
                tvplus.setLayoutParams(textparamsplusminus);
                tvvalue.setLayoutParams(textparamsplusminus);

                tvvalueKG.setPadding((int) (5 * density), (int) (2 * density), (int) (5 * density), (int) (2 * density));


                tvminus.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                tvplus.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                tvvalue.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));
                tvvalueKG.setBackground(getResources().getDrawable(R.drawable.bg_round_rectangle_color));

                tvvalueKG.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);


                tvminus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int i;
                        i = Integer.parseInt(tvvalue.getText().toString());
                        if (i > 1) {
                            i = i - 1;
                        }
                        tvvalue.setText("" + i);
                    }
                });

                tvplus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int i;
                        i = Integer.parseInt(tvvalue.getText().toString());
                        i = i + 1;
                        tvvalue.setText("" + i);
                    }
                });


                if (Float.parseFloat(list.getProductRate()) > 0) {
                    tvproductmrp.setText(NewMRP);
                    LLplusminus.addView(tvminus);
                    LLplusminus.addView(tvvalue);
                    LLplusminus.addView(tvplus);

                } else {
                    final List<ProductPacking> productPackingArrayListList = databaseHandler.getProductPacking(Integer.parseInt(list.getProductId()));

                    try {
                        tvproductmrp.setText("₹ " + " " + productPackingArrayListList.get(3).getNewMRP());
                        tvvalueKG.setText(productPackingArrayListList.get(3).getPackingText());
                        LLplusminus.addView(tvvalueKG);

                        list.setselectedPackingName(productPackingArrayListList.get(3).getPackingText());
                        list.setselectedPackingId(productPackingArrayListList.get(3).getPackingID());
                        list.setProductQty("1");
                        list.setNewMRP(productPackingArrayListList.get(3).getNewMRP());
                    } catch (Exception e) {
                        e.printStackTrace();

                        tvproductmrp.setText("₹ " + " " + productPackingArrayListList.get(0).getNewMRP());
                        tvvalueKG.setText(productPackingArrayListList.get(0).getPackingText());
                        LLplusminus.addView(tvvalueKG);

                        list.setselectedPackingName(productPackingArrayListList.get(0).getPackingText());
                        list.setselectedPackingId(productPackingArrayListList.get(0).getPackingID());
                        list.setProductQty("1");
                        list.setNewMRP(productPackingArrayListList.get(0).getNewMRP());
                    }


                    tvvalueKG.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final String[] stateArray = new String[productPackingArrayListList.size()];
                            for (int i = 0; i < productPackingArrayListList.size(); i++) {
                                stateArray[i] = productPackingArrayListList.get(i).getPackingText();
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(act);
                            builder.setTitle("Select Quantity");
                            builder.setItems(stateArray, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    // Do something with the selection

                                    String NewMRP = "₹ " + " " + productPackingArrayListList.get(item).getNewMRP();
                                    tvproductmrp.setText(NewMRP);
                                    tvvalueKG.setText(productPackingArrayListList.get(item).getPackingText());

                                    list.setselectedPackingName(productPackingArrayListList.get(item).getPackingText());
                                    list.setselectedPackingId(productPackingArrayListList.get(item).getPackingID());
                                    list.setProductQty("1");

                                    list.setNewMRP(productPackingArrayListList.get(item).getNewMRP());
                                }
                            });
                            builder.create().show();
                        }
                    });
                }


                ImageView add_to_cart = new ImageView(getApplicationContext());
                add_to_cart.setMinimumHeight((int) (35 * density));
                add_to_cart.setMaxHeight((int) (35 * density));
                add_to_cart.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                add_to_cart.setLayoutParams(imageparamssmall);

                add_to_cart.setImageDrawable(getResources().getDrawable(R.drawable.icon_bag));

                add_to_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addItemInSelectedProductList(list, tvvalue.getText().toString());
                    }
                });

                LLbottom.addView(LLplusminus);
                LLbottom.addView(add_to_cart);

                LL.setId(i);
                LL.addView(imageView);
                LL.addView(tvproductname);
                LL.addView(tvproductmrp);
                LL.addView(LLbottom);

                FL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(act, ProductListGrid_Activity.class);
                        intent.putExtra("HID", "2");
                        intent.putExtra("ChildItemTitle", "Vegetables");
                        startActivity(intent);

                    }
                });

                FL.addView(LL);
                LLVegetables.addView(FL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            LoadNavigationHeaderItems();
            //setOptionMenu();
            //  setBadgeCount(act, AppController.selectedProductList.size());
            ibv_cartcount.setBadgeValue(AppController.selectedProductList.size())
                    .setBadgePosition(BadgePosition.TOP_RIGHT)
                    .setShowCounter(true);
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
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawer(navigationView);
            } else {
                showExitDialog();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    /* @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:

                 if (drawer.isDrawerOpen(navigationView)) {
                     drawer.closeDrawer(navigationView);
                 } else {
                     drawer.openDrawer(navigationView);
                 }
                 break;

             case R.id.menu_Cart:
                 startActivity(new Intent(act, AddCartCheckOut_Activity.class));
                 break;

             case R.id.menu_Login:
                 if (!AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                     startActivity(new Intent(act, LoginActivity.class));
                 else
                     AppUtils.showDialogSignOut(act);
                 break;

             default:
                 break;

         }

         if (drawer.isDrawerOpen(GravityCompat.START)) {
             drawer.closeDrawer(GravityCompat.START);
         }
         return super.onOptionsItemSelected(item);
     }
 */
    @Override
    protected void onResume() {
        super.onResume();
        LoadNavigationHeaderItems();

        //setOptionMenu();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_home, menu);
            menu_Login = menu.findItem(R.id.menu_Login);
            menu_Cart = menu.findItem(R.id.menu_Cart);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }*/

    private void executeGetCategoryRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodAppAllCategory, TAG);
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
                        JSONArray jsonArrayHeadingMenu = jsonObject.getJSONArray("Data");
                        getCategoryResult(jsonArrayHeadingMenu);
                    } else {
                        AppUtils.alertDialog(act, "Sorry Seems to be an server error. Please try again!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getCategoryResult(JSONArray jsonArrayHeadingMenu) {
        try {

//          Toast.makeText(this, "API Response", Toast.LENGTH_SHORT).show();

            AppController.categoryList.clear();


            for (int i = 0; i < jsonArrayHeadingMenu.length(); i++) {
                JSONObject jsonObject = jsonArrayHeadingMenu.getJSONObject(i);

                HashMap<String, String> map = new HashMap<>();

                map.put("HID", jsonObject.getString("HID"));
                map.put("Heading", AppUtils.CapsFirstLetterString(jsonObject.getString("Heading").trim()));
                // map.put("ImgPath", jsonObject.getString("ImgPath"));
                if (jsonObject.getString("ImgPath").equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/banana.png")) {
                    map.put("ImgPath", "http://versatileitsolution.com/appupload/banana.jpg");
                } else if (jsonObject.getString("ImgPath").equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/Green-chilli.png")) {
                    map.put("ImgPath", "http://versatileitsolution.com/appupload/green_chilli_new.jpg");
                } else if (jsonObject.getString("ImgPath").equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/cow-milk-category.jpg")) {
                    map.put("ImgPath", "http://versatileitsolution.com/appupload/milk.jpg");
                }
                AppController.categoryList.add(map);
            }

            //DrawCategoriesList();

//            for (int i = 0; i < AppController.categoryList.size(); i++) {
//                if (AppController.categoryList.get(i).get("HID").equalsIgnoreCase("2"))
//                    executeToGetVegetablesListRequest();
//            }

            enableExpandableList();
            LoadNavigationHeaderItems();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToGetImageSlider() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodHomePageSlider, TAG);
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
                        getImageSliderResult(jsonObject.getJSONArray("Data"));
                    } else {
                        AppUtils.alertDialog(act, "Sorry Seems to be an server error. Please try again!!!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getImageSliderResult(JSONArray jsonArrayData) {
        try {
            AppController.homeSliderList.clear();

            for (int i = 0; i < jsonArrayData.length(); i++) {
                final JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                if (!jsonObject.getString("Imgid").equalsIgnoreCase("4") &&
                        !jsonObject.getString("Imgid").equalsIgnoreCase("5")) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Imgid", "" + jsonObject.getString("Imgid"));
                    map.put("ImgPath", "" + jsonObject.getString("ImgPath"));
                    //     map.put("CategoryID", "" + jsonObject.getString("CategoryID"));
                    AppController.homeSliderList.add(map);
                }
            }
            setImageSlider();


        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    private void executeUpdateDeviceID() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNO", "" + AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, "")));
                            postParameters.add(new BasicNameValuePair("GCMDeviceId", "" + FirebaseInstanceId.getInstance().getToken()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUpdateDeviceID, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {

                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    /// vipin
    private void NotificationLlist() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://bhajilelo.versatileitsolution.com/webservice/Service.asmx/MainCategory", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", response.toString());
                    String Status = jsonObject.getString("Status");
                    if (Status.matches("True")) {
                        JSONArray array = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {


                            Log.e("count", String.valueOf(array.length()));
                            // int count = Integer.valueOf(array.length());
                            JSONObject ob = array.getJSONObject(i);
                           /* if (ob.getString("ImgPath").equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/banana.png")) {
                                path =  "http://versatileitsolution.com/appupload/banana.jpg";
                            } else if (jsonObject.getString("ImgPath").equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/Green-chilli.png")) {
                                path = "http://versatileitsolution.com/appupload/green_chilli_new.jpg";
                            } else if (jsonObject.getString("ImgPath").equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/cow-milk-category.jpg")) {
                                path = "http://versatileitsolution.com/appupload/milk.jpg";
                            }*/
                            String imggepath = ob.getString("ImgPath");
                            if (imggepath.equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/banana.png")) {
                                path = "http://versatileitsolution.com/appupload/banana.jpg";
                            }
                            if (imggepath.equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/Green-chilli.png")) {
                                path = "http://versatileitsolution.com/appupload/green_chilli_new.jpg";
                            }
                            if (imggepath.equalsIgnoreCase("http://bhajilelo.versatileitsolution.com/Admin/images/cow-milk-category.jpg")) {
                                path = "http://versatileitsolution.com/appupload/milk.jpg";
                            }
                            Vipindata ld = new Vipindata(ob.getString("HID"), ob.getString("Heading"), path);
                            notification_data.add(ld);
                        }
                        noti_rv.setAdapter(noti_adapter);
                    } else {
                        String message = jsonObject.getString("Message");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

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

}