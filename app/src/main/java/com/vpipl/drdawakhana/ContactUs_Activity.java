package com.vpipl.drdawakhana;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.vpipl.drdawakhana.Utils.AppUtils;

/**
 * Created by Mukesh 18 Nov 2020.
 */
public class ContactUs_Activity extends Activity {
    String TAG = "ContactUs_Activity";
    Activity act = ContactUs_Activity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactus_activity);

        try {
            if (AppUtils.showLogs) Log.v(TAG, "called.....");

            findViewById(R.id.ic_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            findViewById(R.id.txt_5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getResources().getString(R.string.contact_email), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Subject Here");
                        startActivity(emailIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            findViewById(R.id.txt_6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent callIntent = new Intent(Intent.ACTION_VIEW);
                        callIntent.setData(Uri.parse(getResources().getString(R.string.contact_website)));
                        startActivity(callIntent);
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
