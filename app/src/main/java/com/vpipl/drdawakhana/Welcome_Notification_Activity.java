package com.vpipl.drdawakhana;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.PrefManager;
import com.vpipl.drdawakhana.Utils.SPUtils;

/**
 * Created by Mukesh 18 Nov 2020.
 */
public class Welcome_Notification_Activity extends Activity {
    String TAG = "Welcome_Notification_Activity";
    private PrefManager prefManager;
    Activity act = Welcome_Notification_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_notification);

        try {
            if (AppUtils.showLogs) Log.v(TAG, "called.....");
            prefManager = new PrefManager(this);
            if (!prefManager.isFirstTimeLaunch()) {
                try {
                    if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                        startActivity(new Intent(act, MainActivity.class));
                    } else {
                        startActivity(new Intent(act, Login_Activity.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AppUtils.showExceptionDialog(act);
                }
                finish();
            }

            findViewById(R.id.btn_trun_on_notification).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        prefManager.setFirstTimeLaunch(false);
                        AppController.getWelcome_Notification_Sts().edit()
                                .putString(SPUtils.Welcome_Notification_Sts, "Y")
                                .commit();

                        Toast.makeText(act, "Notification turning on !!", Toast.LENGTH_SHORT).show();
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(act, MainActivity.class));
                        } else {
                            startActivity(new Intent(act, Login_Activity.class));
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            findViewById(R.id.ll_skip_this).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        prefManager.setFirstTimeLaunch(false);
                        AppController.getWelcome_Notification_Sts().edit()
                                .putString(SPUtils.Welcome_Notification_Sts, "N")
                                .commit();
                        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                            startActivity(new Intent(act, MainActivity.class));
                        } else {
                            startActivity(new Intent(act, Login_Activity.class));
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


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
    }
}
