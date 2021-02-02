package com.vpipl.drdawakhana;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDex;

import com.vpipl.drdawakhana.SMS.AppSignatureHelper;
import com.vpipl.drdawakhana.model.FilterList2CheckBox;
import com.vpipl.drdawakhana.model.ProductList;
import com.vpipl.drdawakhana.model.ProductsList;
import com.vpipl.drdawakhana.Utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppController extends Application {
    private static SharedPreferences sp_userinfo;
    private static SharedPreferences sp_isLogin;
    private static SharedPreferences sp_Welcome_Notification_Sts;
    private static SharedPreferences sp_Shipping_Amt;
    private static SharedPreferences sp_Shipping_Charge;
    private static SharedPreferences sp_isInstall;
    private static AppController mInstance;

    public static List<ProductList> productList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> SizeList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> ColorList = new ArrayList<>();

    public static List<ProductList> FruitsList = new ArrayList<>();
    public static List<ProductList> VegatblesList = new ArrayList<>();
    public static List<ProductList> selectedProductList = new ArrayList<>();
    public static List<ProductsList> selectedWishList = new ArrayList<>();
    public static List<ProductsList> selectedProductsList = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> countryList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> categoryList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> homeSliderList = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> stateList = new ArrayList<>();

    public static ArrayList<HashMap<String, String>> category1 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category2 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category3 = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> category4 = new ArrayList<>();

    public static ArrayList<FilterList2CheckBox> priceFilterList = new ArrayList<>();
    public static ArrayList<FilterList2CheckBox> discountFilterList = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> filterList1 = new ArrayList<>();



    public static boolean comesFromFilter = false;

    public static String FiltersCondition = "";
    public static String FiltersConditionStatic = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static synchronized AppController getInstance() {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mInstance = this;
            /* to call initialize SharedPreferences  */
            initSharedPreferences();

            AppSignatureHelper appSignature = new AppSignatureHelper((Context) this);
            appSignature.getAppSignatures();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to initialize instance globally of SharedPreferences
     */
    private void initSharedPreferences() {
        try {
            sp_userinfo = getApplicationContext().getSharedPreferences(SPUtils.USER_INFO, Context.MODE_PRIVATE);
            sp_isLogin = getApplicationContext().getSharedPreferences(SPUtils.IS_LOGIN, Context.MODE_PRIVATE);
            sp_Welcome_Notification_Sts = getApplicationContext().getSharedPreferences(SPUtils.IS_LOGIN, Context.MODE_PRIVATE);
            sp_Shipping_Amt = getApplicationContext().getSharedPreferences(SPUtils.USER_Shipping_Amount, Context.MODE_PRIVATE);
            sp_Shipping_Charge = getApplicationContext().getSharedPreferences(SPUtils.USER_Shipping_Charge, Context.MODE_PRIVATE);
            sp_isInstall = getApplicationContext().getSharedPreferences(SPUtils.IS_INSTALL, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpUserInfo() {
        return sp_userinfo;
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpIsLogin() {
        return sp_isLogin;
    }

    public static synchronized SharedPreferences getWelcome_Notification_Sts() {
        return sp_Welcome_Notification_Sts;
    }
    public static synchronized SharedPreferences getShipping_Amt() {
        return sp_Shipping_Amt;
    }
    public static synchronized SharedPreferences getShipping_Charge() {
        return sp_Shipping_Charge;
    }

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpIsInstall() {
        return sp_isInstall;
    }
}