package com.vpipl.drdawakhana;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.drdawakhana.Adapters.Category_Main_list_Adapter;
import com.vpipl.drdawakhana.firebase.Config;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class CategoryMainListActivity extends Activity {
    private String TAG = "CategoryMainListActivity";

    Activity act;
    LinearLayout img_nav_back;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    public Category_Main_list_Adapter adapter;
    RecyclerView recyclerView;
    LinearLayout ll_data_found, ll_no_data_found;
    public static ArrayList<HashMap<String, String>> array_list = new ArrayList<>();
    RelativeLayout ll_home_bo_categories, ll_home_bo_wishlist, ll_home_bo_profile, ll_home_bo_cart, ll_home_bo_home;
    ImageBadgeView mukesh_begview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_category_main_list);

            act = CategoryMainListActivity.this;
            img_nav_back = findViewById(R.id.img_nav_back);
           TextView heading = findViewById(R.id.heading);
            heading.setText("Main Category List");
            mukesh_begview = findViewById(R.id.mukesh_begview);

            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

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

            /*Home Today Leads */
            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.listView);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());


            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                executeCategoryListListRequest();
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
                            array_list.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_data_found.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
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
                map.put("CID", jsonObject.getString("CID"));
                map.put("Category", jsonObject.getString("Category"));
                map.put("imgPath", SPUtils.cateImageURL + jsonObject.getString("Image"));
                map.put("ShowImage", jsonObject.getString("ShowImage"));
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
                adapter = new Category_Main_list_Adapter(act, array_list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                ll_data_found.setVisibility(View.VISIBLE);
                ll_no_data_found.setVisibility(View.GONE);
            } else {
                ll_data_found.setVisibility(View.GONE);
                ll_no_data_found.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());
    }
}