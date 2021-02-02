package com.vpipl.drdawakhana.Service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.vpipl.drdawakhana.Utils.AppUtils;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mukesh on 17-Nov-20.
 */
public class GetDataService extends Service {
    private String TAG = "GetDataService";

    @Override
    public void onCreate() {
        super.onCreate();

        try {

            if (AppUtils.isNetworkAvailable(GetDataService.this)) {
                executeStateRequest();
            } else {
                stopSelf();
            }

        } catch (Exception e) {
            stopSelf();
            e.printStackTrace();
        }
    }

    private void executeStateRequest() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
//                    response = AppUtils.callWebServiceWithMultiParam(GetDataService.this, postParameters, QueryUtils.methodMaster_FillState, TAG);
                } catch (Exception ex) {
                    stopSelf();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String resultData) {
                try {
                    JSONObject jsonObject = new JSONObject(resultData);
                    JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        if (jsonArrayData.length() != 0) {
                            getStateResult(jsonArrayData);
                        } else {
                            stopSelf();
                        }
                    } else {
                        stopSelf();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getStateResult(JSONArray jsonArray) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
