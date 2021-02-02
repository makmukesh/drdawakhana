package com.vpipl.drdawakhana;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.CircularImageView;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mukesh on 17-Nov-20.
 */
public class UpdateProfile_Activity extends AppCompatActivity {

    String TAG = "UpdateProfile_Activity";
    Button btn_updateChange;
    CircularImageView iv_Profile_Pic;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    EditText et_userName, et_address, et_email/*, et_city, et_state, et_postCode*/;
    TextView et_userMobileNumber, txt_top_name, txt_top_emailid,et_userdob;
    Activity act = UpdateProfile_Activity.this;
    String str_gender = "Male";
    LinearLayout img_nav_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        try {
            if (AppUtils.showLogs) Log.v(TAG, "called.....");

            img_nav_back = findViewById(R.id.img_nav_back);
            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            iv_Profile_Pic = findViewById(R.id.iv_Profile_Pic);
            et_userName = (EditText) findViewById(R.id.et_userName);
            et_userMobileNumber = (TextView) findViewById(R.id.et_userMobileNumber);
            et_email = (EditText) findViewById(R.id.et_email);
            et_address = (EditText) findViewById(R.id.et_address);
            txt_top_name = (TextView) findViewById(R.id.txt_top_name);
            txt_top_emailid = (TextView) findViewById(R.id.txt_top_emailid);
            et_userdob = (TextView) findViewById(R.id.et_userdob);

            btn_updateChange = (Button) findViewById(R.id.btn_updateChange);

            btn_updateChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboardOnClick(act, view);
                    validateUpdateProfileRequest();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {
                executeToGetProfileInfo();
            } else {
                AppUtils.alertDialog(this, getResources().getString(R.string.txt_networkAlert));
            }

            /*LinearLayout lin_back = (LinearLayout) findViewById(R.id.lin_back);
            lin_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });*/


            List<String> genderlist = new ArrayList<String>();
            genderlist.add("Male");
            genderlist.add("Female");

            final Spinner spinner_gender = (Spinner)findViewById(R.id.spinner_gender);
            ArrayAdapter<String> physicalStatusadapter = new ArrayAdapter<String>(UpdateProfile_Activity.this, android.R.layout.simple_spinner_dropdown_item, genderlist);
            spinner_gender.setAdapter(physicalStatusadapter);

            spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    str_gender = parent.getItemAtPosition(position).toString();
                    ((TextView) view).setTextColor(Color.GRAY);
                    ((TextView) view).setTextSize(15);
                    ((TextView) view).setSingleLine(true);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            iv_Profile_Pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions())
                        selectImage();
                }
            });

            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic, ""), iv_Profile_Pic);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeToGetProfileInfo() {
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
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToViewProfile, TAG);
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
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (jsonArrayData.length() != 0) {
                                    getProfileInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
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

    private void getProfileInfo(JSONArray jsonArray) {
        try {
            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_FORM_NUMBER, "" + jsonArray.getJSONObject(0).getString("FormNo"))
                    .putString(SPUtils.USER_ID, "" + jsonArray.getJSONObject(0).getString("ID"))
                    .putString(SPUtils.USER_Name, "" + (jsonArray.getJSONObject(0).getString("MemFirstName")))
                    .putString(SPUtils.USER_LAST_NAME, "" + (jsonArray.getJSONObject(0).getString("MemLastName")))
                    .putString(SPUtils.USER_MobileNo, jsonArray.getJSONObject(0).getString("Mobl"))
                    .putString(SPUtils.USER_Email, "" + jsonArray.getJSONObject(0).getString("EMail"))
                    .putString(SPUtils.USER_Address, "" + (jsonArray.getJSONObject(0).getString("Address1")))
                    .putString(SPUtils.USER_PinCode, "" + jsonArray.getJSONObject(0).getString("PinCode"))
                    .putString(SPUtils.USER_City, (jsonArray.getJSONObject(0).getString("City")))
                    .putString(SPUtils.USER_State, (jsonArray.getJSONObject(0).getString("StateCode")))
                    .putString(SPUtils.USER_Country, (jsonArray.getJSONObject(0).getString("CountryName")))
                    .putString(SPUtils.USER_profile_pic, (SPUtils.ProfileURL + jsonArray.getJSONObject(0).getString("Image")))
                    .commit();

            setProfileDetails();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }


    private void validateUpdateProfileRequest() {
        try {
            if (et_userName.getText().toString().isEmpty()) {
                et_userName.setError(getResources().getString(R.string.error_field_required));
                et_userName.requestFocus();
            } else if (et_userMobileNumber.getText().toString().isEmpty()) {
                et_userMobileNumber.setError(getResources().getString(R.string.error_field_required));
                et_userMobileNumber.requestFocus();
            } else if (et_userMobileNumber.getText().toString().length() != 10) {
                et_userMobileNumber.setError(getResources().getString(R.string.error_et_userMobileNumber));
                et_userMobileNumber.requestFocus();
            } else if (!TextUtils.isEmpty(et_email.getText().toString().trim()) && !et_email.getText().toString().matches(AppUtils.mEmaiPattern)) {
                et_email.setError(getResources().getString(R.string.error_et_userEmail));
                et_email.requestFocus();
            } else if (et_address.getText().toString().isEmpty()) {
                et_address.setError(getResources().getString(R.string.error_field_required));
                et_address.requestFocus();
            }/* else if (et_city.getText().toString().isEmpty()) {
                et_city.setError(getResources().getString(R.string.error_field_required));
                et_city.requestFocus();
            } else if (et_state.getText().toString().isEmpty()) {
                et_state.setError(getResources().getString(R.string.error_field_required));
                et_state.requestFocus();
            } else if (et_postCode.getText().toString().isEmpty()) {
                et_postCode.setError(getResources().getString(R.string.error_field_required));
                et_postCode.requestFocus();
            } else if (!et_postCode.getText().toString().matches(AppUtils.mPINCodePattern)) {
                et_postCode.setError(getResources().getString(R.string.error_et_userPostCode));
                et_postCode.requestFocus();
            } */ else {
                startUpdateProfile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void startUpdateProfile() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                if (AppUtils.showLogs)
                    Log.v(TAG, "In startUpdateProfile startUpdateProfile called..");

                List<NameValuePair> postParameters = new ArrayList<>();

                /*postParameters.add(new BasicNameValuePair("Name", et_userName.getText().toString()));
                postParameters.add(new BasicNameValuePair("MobileNo", "" + et_userMobileNumber.getText().toString()));
                postParameters.add(new BasicNameValuePair("Emailid", et_email.getText().toString()));
                postParameters.add(new BasicNameValuePair("Address", et_address.getText().toString()));
                postParameters.add(new BasicNameValuePair("city", et_city.getText().toString()));
                postParameters.add(new BasicNameValuePair("Country", et_country.getText().toString()));
                postParameters.add(new BasicNameValuePair("State", et_state.getText().toString()));
                postParameters.add(new BasicNameValuePair("Pincode", et_postCode.getText().toString()));*/
                char first = str_gender.charAt(0);
                postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                postParameters.add(new BasicNameValuePair("FirstName", et_userName.getText().toString()));
                postParameters.add(new BasicNameValuePair("LastName", ""));
                postParameters.add(new BasicNameValuePair("MobileNo", AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, "")));
                postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.USER_Passw, "")));
                postParameters.add(new BasicNameValuePair("Gender", "" + first));
                postParameters.add(new BasicNameValuePair("EmailId", et_email.getText().toString()));
                postParameters.add(new BasicNameValuePair("DOB", "123"));

                executeUpdateprofileRequest(postParameters);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void executeUpdateprofileRequest(final List<NameValuePair> postParameters) {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUpdateProfile, TAG);
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
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                AppUtils.alertDialogWithFinish(act, "" + jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
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


    private void setProfileDetails() {
        try {
            et_userName.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
            txt_top_name.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Name, ""));
            et_userMobileNumber.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_MobileNo, ""));
            et_email.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Email, ""));
            if(!AppController.getSpUserInfo().getString(SPUtils.USER_Email, "").equalsIgnoreCase("")) {
                txt_top_emailid.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Email, ""));
                txt_top_emailid.setVisibility(View.VISIBLE);
            }
            else{
                txt_top_emailid.setVisibility(View.GONE);
            }
            et_address.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Address, ""));

            AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic, ""),iv_Profile_Pic);

            // et_city.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_City, ""));
            //  et_state.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_State, ""));
            //  et_country.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_Country, ""));
            // et_postCode.setText("" + AppController.getSpUserInfo().getString(SPUtils.USER_PinCode, ""));
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
        return super.onOptionsItemSelected(item);
    }*/

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
    /*Profile photo updated Code added by mukesh on 15-12-2020 11;53 AM*/
    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);
        int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionREAD_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionREAD_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        String imageStoragePath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        executePostImageUploadRequest(bitmap);
        iv_Profile_Pic.setImageBitmap(bitmap);

        Log.e("from camera data", imageStoragePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        executePostImageUploadRequest(bm);
        iv_Profile_Pic.setImageBitmap(bm);
        String imagepath = bm.toString();
        Log.e("from gallery data", imagepath);
    }

    private void executePostImageUploadRequest(final Bitmap bitmap) {
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
                            postParameters.add(new BasicNameValuePair("FormNo", AppController.getSpUserInfo().getString(SPUtils.USER_FORM_NUMBER, "")));
                            postParameters.add(new BasicNameValuePair("Image", AppUtils.getBase64StringFromBitmap(bitmap)));
                            postParameters.add(new BasicNameValuePair("Extension", "PNG"));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToUpdateProfileImage, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (!jsonArrayData.getJSONObject(0).getString("Image").equals("")) {
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic, (SPUtils.ProfileURL + jsonArrayData.getJSONObject(0).getString("Image"))).commit();
                                    AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic, ""), iv_Profile_Pic);

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
}