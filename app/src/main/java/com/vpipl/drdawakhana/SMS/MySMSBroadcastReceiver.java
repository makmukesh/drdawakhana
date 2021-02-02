package com.vpipl.drdawakhana.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import com.vpipl.drdawakhana.Utils.SmsListener;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * Created by Mukesh on 17-Nov-20.
 */

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    final SmsManager sms = SmsManager.getDefault();

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    //   String otp = extraSMSCode.get(message);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    if (message.contains("kNem9YYvN2V+")) {
                        String otp = message.substring(4, 10).trim();
                        Log.e("otp", "" + otp);
                        //     Toast.makeText(context, otp , Toast.LENGTH_SHORT).show();
                        mListener.messageReceived(otp);
                    }
                    //   Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                    break;
                case CommonStatusCodes.TIMEOUT:
                    Log.e("TIMEOUT", " TIMEOUT");
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    //    Toast.makeText(context, "Faild sms" , Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}