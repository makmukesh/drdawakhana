package com.vpipl.drdawakhana.firebase;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;

import com.vpipl.drdawakhana.AppController;
import com.vpipl.drdawakhana.Splash_Activity;
import com.vpipl.drdawakhana.Utils.SPUtils;

//  changes by Mukesh 10-11-2020

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void handleIntent(Intent intent) {
        String mobilenumber = "", title = "", message = "";
        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);

                if (key.equalsIgnoreCase("gcm.notification.MobileNumber")) {
                    mobilenumber = intent.getExtras().get(key).toString();
                } else if (key.equalsIgnoreCase("gcm.notification.title")) {
                    title = intent.getExtras().get(key).toString();
                } else if (key.equalsIgnoreCase("gcm.notification.body")) {
                    message = intent.getExtras().get(key).toString();
                    message = Html.fromHtml(message).toString().replaceAll("&nbsp", "").replaceAll("\n", "").replaceAll("\t", "");
                }

                Log.e("FirebaseDataReceiver", "Key: " + key + " Value: " + value);
            }
        }

        Log.e("mobilenumber", "" + mobilenumber + " title " + "::::" + title + " message " + " :::: " + message);
        if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
            String str = AppController.getWelcome_Notification_Sts().getString(SPUtils.Welcome_Notification_Sts, "") ;
            Log.e("WelcomeNotiSts", "" + str);
            if (AppController.getWelcome_Notification_Sts().getString(SPUtils.Welcome_Notification_Sts, "N").equalsIgnoreCase("Y")) {
                handleNotification(title, message, mobilenumber);
            }
        }
    }

    private void handleNotification(String Title, String Desc, String Mobile) {
        try {
            Log.e(TAG, "Title: " + Title);
            Log.e(TAG, "Message: " + Desc);
            Log.e(TAG, "MobileNumber: " + Mobile);
            Intent resultIntent = new Intent(getApplicationContext(), Splash_Activity.class);
            showNotificationMessage(getApplicationContext(), Title, Desc, Mobile, resultIntent);

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("title", Title);
                pushNotification.putExtra("message", Desc);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
            } else {
                // If the app is in background, firebase itself handles the notification
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String MobileNumber, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, MobileNumber, intent);
    }
}