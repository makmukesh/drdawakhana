package com.vpipl.drdawakhana.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;

import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.drdawakhana.AppController;
import com.vpipl.drdawakhana.CheckoutToPay_Activity;
import com.vpipl.drdawakhana.Login_Activity;
import com.vpipl.drdawakhana.MainActivity;
import com.vpipl.drdawakhana.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mukesh on 17-Nov-20.
 */
public class AppUtils {
    public static boolean showLogs = true;
    static Context context;

    public static String mEmaiPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    //IFSC Code normally contains 11 characters.In that first 4 characters
    //are alphabets,5th character is 0 and next 6 characters are numerics
    //public static String mIFSCPattern ="[A-Z|a-z]{4}[0][\\d]{6}$";
    //public static String mIFSCPattern ="[A-Z|a-z|0-9]{4}[0][A-Z|a-z|0-9]{6}$";
    //one digit from 1 to 9 and rest five digits in the inclusive range 0-9

    public static String mPINCodePattern = "^[1-9][0-9]{5}$";

    public static ProgressDialog progressDialog;


    //public static String smsAPIURL = "http://mysms.versatileitsolution.com/submitsms.jsp?user=goforhol&key=6ef57df046XX&mobile=<rqmbl>&message=<rqsms>&senderid=HOLDAY&accusage=1\n";
    //  public static String smsAPIURL = "http://mysms.versatileitsolution.com/submitsms.jsp?user=MLMSOFT&key=2045737a9cXX&mobile=<rqmbl>&message=<rqsms>&senderid=VPIPLB&accusage=1";
    public static String smsAPIURL = "http://mysms.versatileitsolution.com/submitsms.jsp?user=MLMSOFT&key=2045737a9cXX&mobile=<rqmbl>&message=<rqsms>&senderid=ASCEND&accusage=1";

    public static String smsAPIURL() {
        return (smsAPIURL);
    }

    public static void loadSlidingImage(Context conn, String imageURL, ImageView imageView) {
        try {
            Glide.with(conn)
                    .load(imageURL)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPlayServices(Context con, String pageName) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(con);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) con, 100).show();
            } else {
                if (AppUtils.showLogs)
                    Log.v(pageName, "This device Google Play Services is not supported.");
                ((Activity) con).finish();
            }
            return false;
        }
        return true;
    }

    public static String getAppVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String versionName = "";
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
        return versionName;
    }

    public static void saveAppVersionAndDeviceId(String gcm_deviceId) {
        try {
            if (AppUtils.showLogs) Log.v("saveAppVersionAndDevice", "gcm_deviceId:" + gcm_deviceId);

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.GCM_DEVICE_ID, gcm_deviceId)
                    .commit();

            AppController.getSpIsInstall().edit()
                    .putString(SPUtils.GCM_DEVICE_ID, gcm_deviceId)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
        return connected;
    }

    public static Dialog createDialog(Context context, boolean single) {
        final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        if (single)
            dialog.setContentView(R.layout.custom_dialog_one);
        else
            dialog.setContentView(R.layout.custom_dialog_two);

        return dialog;
    }

    public static void alertDialog(Context context, String message) {
        try {
            final Dialog dialog = createDialog(context, true);
            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setTextColor(context.getResources().getColor(R.color.colorLogo));
            dialog4all_txt.setText(message);
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }

    public static void alertDialogWithFinishHome(final Context context, String message) {
        try {
            final Dialog dialog = createDialog(context, true);
            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setTextColor(context.getResources().getColor(R.color.colorLogo));
            dialog4all_txt.setText(message);
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }

    public static void showAlertSnakeMessage(View coordinatorLayout, String message) {
        try {
            Snackbar.make(coordinatorLayout, "" + message, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }

    public static void alertDialogWithFinish(final Context context, String message) {
        try {
            final Dialog dialog = createDialog(context, true);
            TextView dialog4all_txt = dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setTextColor(context.getResources().getColor(R.color.colorLogo));
            dialog4all_txt.setText(message);
            dialog.findViewById(R.id.txt_submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ((Activity) context).finish();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }


    public static StringBuilder CapsNameWord(String string) {
        StringBuilder builder = new StringBuilder();

        try {
            String arr[] = string.split(" ");
            String arr1[] = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                arr1[i] = arr[i].trim().substring(0, 1).toUpperCase() + arr[i].trim().substring(1, arr[i].length());
            }

            for (String s : arr1) {
                builder.append(s).append(" ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder;
    }

    public static String CapsFirstLetterString(String string) {
        return string.trim().substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String isNetworkWifiMobileData(Context context) {
        String isType = "";
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
                isType = "W";
            } else if (networkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
                isType = "M";
            } else {
                isType = "MW";
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
        return isType;
    }

    public static void showDialogSignOut(final Context con) {
        try {
            final Dialog dialog = AppUtils.createDialog(con, false);
            dialog.setCancelable(false);

//            final DatabaseHandler sqLiteHelper = new DatabaseHandler(con);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setTextColor(con.getResources().getColor(R.color.colorLogo));
            txt_DialogTitle.setText(Html.fromHtml(con.getResources().getString(R.string.txt_signout_message)));

            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText(con.getResources().getString(R.string.txt_yes));
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();

                        AppController.getSpUserInfo().edit().clear().commit();
                        AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, false).commit();

                        AppController.productList.clear();
                        AppController.selectedProductList.clear();

                        AppController.countryList.clear();
                        AppController.categoryList.clear();
                        AppController.homeSliderList.clear();
                        CheckoutToPay_Activity.deliveryAddressList.clear();
//                        sqLiteHelper.clearDB();

                        Intent intent = new Intent(con, Login_Activity.class);
                        //Intent intent = new Intent(con, Login_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        con.startActivity(intent);
                        ((Activity) con).finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setTextColor(con.getResources().getColor(R.color.colorPrimary));
            txt_cancel.setText(con.getResources().getString(R.string.txt_no));
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
            AppUtils.showExceptionDialog(con);
        }
    }

    public static void hideKeyboardOnClick(Context con, View view) {
        try {
            InputMethodManager inputManager = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showExceptionDialog(Context con) {
        try {
            AppUtils.alertDialog(con, con.getResources().getString(R.string.txt_exception));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setActionbarTitle(ActionBar bar, Context con) {
        try {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setElevation(0);
            bar.setTitle("" + con.getResources().getString(R.string.app_name));

//            final Drawable upArrow = con.getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
//            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//            bar.setHomeAsUpIndicator(upArrow);

//            bar.setIcon(R.drawable.ic_app_logo_title_small);
            // bar.setBackgroundDrawable(ResourcesCompat.getDrawable(con.getResources(), R.drawable.ic_title_back, null));
//            bar.setHomeAsUpIndicator(R.drawable.ic_back_arrow);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showProgressDialog(Context conn) {
        try {
            if (progressDialog != null) {
                if (!progressDialog.isShowing()) {
                    if (!((Activity) conn).isFinishing()) {
                        progressDialog.show();
                    }
                }
            } else {
                progressDialog = new ProgressDialog(conn);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setTitle("Loading...");
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setInverseBackgroundForced(false);
                progressDialog.show();

                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String calculateTotalOrderAmount() {
        double totalCartAmount = 0.0d;
        try {
            for (int j = 0; j < AppController.selectedProductList.size(); j++) {
                double amount;
                amount = ((Double.parseDouble(AppController.selectedProductList.get(j).getProductQty())) * (Double.parseDouble(AppController.selectedProductList.get(j).getNewMRP())));
                totalCartAmount = (totalCartAmount + amount);
            }
            if (AppUtils.showLogs)
                Log.v("AppUtils", "calculateTotalOrderAmount()..." + totalCartAmount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCartAmount + "";
    }

    public static String calculateNetQty() {
        int netQty = 0;
        try {
            for (int j = 0; j < AppController.selectedProductList.size(); j++) {
                netQty = (netQty + Integer.parseInt(AppController.selectedProductList.get(j).getProductQty()));
            }
            if (AppUtils.showLogs) Log.v("AppUtils", "netQty..." + netQty);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return netQty + "";
    }

    public static void loadProductImage(Context conn, String imageURL, ImageView imageView) {
        try {
            if (imageURL.equals("") && imageURL.isEmpty()) {
                if (AppUtils.showLogs) Log.v("AppUtils", "loadImage in if imageURL..." + imageURL);
                imageURL = "http://img.victorentertainmentshop.com/img/no_image/300/300/no.jpg";
            }

            Glide.with(conn)
                    .load(imageURL)
                    .placeholder(R.drawable.ic_no_image)
                    .error(R.drawable.ic_no_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printQuery(String pageName, List<NameValuePair> postParam) {
        try {
            String query = "";
            for (int i = 0; i < postParam.size(); i++) {
                query = query + " " + postParam.get(i).getName() + " : " + postParam.get(i).getValue();
            }

            if (AppUtils.showLogs) Log.e(pageName, "Executing Parameters..." + query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean isValidMobileno(String s) {
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public static String callWebServiceWithMultiParam(Context con, List<NameValuePair> postParameters, String methodName, String pageName) {
        BufferedReader in = null;
        try {

            System.setProperty("http.keepAlive", "false");
            HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
            int timeoutConnection = 0;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 0;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


            DefaultHttpClient client = new DefaultHttpClient();
            client.setParams(httpParameters);

            String result = null;
            if (AppUtils.isNetworkAvailable(con)) {

                try {


                    Log.e(pageName, "Executing URL..." + SPUtils.serviceAPIURL + methodName);

                    printQuery(pageName + " :: " + methodName, postParameters);

                    HttpPost request = new HttpPost(SPUtils.serviceAPIURL + methodName);
                    UrlEncodedFormEntity formEntity = null;
//                try {
                    formEntity = new UrlEncodedFormEntity(postParameters);
                    formEntity.setContentEncoding(HTTP.UTF_8);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                    request.setEntity(formEntity);
                    HttpResponse response = null;
//                try {
                    response = client.execute(request);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
                    in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                    StringBuilder sb = new StringBuilder();
                    String line;
                    String NL = System.getProperty("line.separator");
//                try {
                    while ((line = in.readLine()) != null) {
                        sb.append(line).append(NL);
                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

//                try {
                    in.close();
//                } catch (Exception e) {
//                e.printStackTrace();
//                }

//                try {

                    Log.e(pageName + "::" + methodName + "", "Response..... " + sb.toString());
                    result = sb.toString();


//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AppUtils.alertDialog(con, con.getResources().getString(R.string.txt_networkAlert));
            }

            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String getDeviceId(Context con) {
        String deviceid = "0000000000";
        String TODO = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            deviceid = Settings.Secure.getString(con.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return (deviceid);
    }
    public static String generateRandomAlphaNumeric(int length) {
        SecureRandom rnd = new SecureRandom();
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);

        try {
            for (int i = 0; i < length; i++) {
                sb.append(AB.charAt(rnd.nextInt(AB.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static String getDateFromAPIDate(String date) {
        try {
            if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);

            if (date.contains("/Date("))
                cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
            else
                cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

            date = DateFormat.format("dd-MMM-yyyy", cal).toString();

            if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }
    public static void loadImage(Context conn, String imageURL, ImageView imageView) {
        try {
            if (imageURL.equals("") && imageURL.isEmpty()) {
                imageURL = imageURL + "/design_img/swam.png";
            }

            Glide.with(conn)
                    .load(imageURL)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .fallback(R.mipmap.ic_launcher)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getBase64StringFromBitmap(Bitmap bitmap) {
        String imageString = "";
        try {
            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                byte[] image = stream.toByteArray();
                if (AppUtils.showLogs)
                    Log.e("AppUtills", "Image Size after comress : " + image.length);
                imageString = Base64.encodeToString(image, Base64.DEFAULT);
            } else {
                imageString = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageString;
    }

}
