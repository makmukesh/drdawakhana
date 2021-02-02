package com.vpipl.drdawakhana;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;

import ru.nikartm.support.ImageBadgeView;


public class Filter_Activity extends AppCompatActivity {

    Button btn_clearFilter, btn_apply;

    LinearLayout layout_width;
    LinearLayout layout_height;
    LinearLayout layout_material;
    LinearLayout layout_color;

//    ArrayList<String> selectedWidthId = new ArrayList<>();
//    ArrayList<String> selectedHeightId = new ArrayList<>();
//    ArrayList<String> selectedMaterialId = new ArrayList<>();
//    ArrayList<String> selectedColorId = new ArrayList<>();

    ImageView img_menu;
    ImageView img_search;
    ImageView img_cart;
    ImageView img_user;

    public void SetupToolbar() {
        LinearLayout img_nav_back =  findViewById(R.id.img_nav_back);
        img_nav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       /* img_menu = (ImageView) findViewById(R.id.img_menu);
        img_search = (ImageView) findViewById(R.id.img_search);
        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_user = (ImageView) findViewById(R.id.img_user);

        img_user.setVisibility(View.GONE);
        img_search.setVisibility(View.GONE);
        img_cart.setVisibility(View.GONE);

        img_menu.setImageResource(R.drawable.ic_arrow_back_white_px);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppController.comesFromFilter = false;
                ProductListGrid_Activity.selectedWidthIds.clear();
                finish();
            }
        });*/
    }
    public ImageBadgeView mukesh_begview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        SetupToolbar();

        btn_clearFilter = (Button) findViewById(R.id.btn_clearFilter);
        btn_apply = (Button) findViewById(R.id.btn_apply);

        mukesh_begview = findViewById(R.id.mukesh_begview);
        mukesh_begview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Filter_Activity.this, AddCartCheckOut_Activity.class));
            }
        });
        mukesh_begview.setBadgeValue(AppController.selectedProductsList.size());

        btn_clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.comesFromFilter = false;
                ProductListGrid_Activity.selectedWidthIds.clear();
                finish();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.comesFromFilter = true;
                finish();
            }
        });

        if (ProductListGrid_Activity.WidthList.size() > 0) {
            ProductListGrid_Activity.selectedWidthIds.clear();
            DrawProductWidth();
        }

        if (ProductListGrid_Activity.HeightList.size() > 0) {
            ProductListGrid_Activity.selectedHeightIds.clear();
            DrawProductHeight();
        }
        if (ProductListGrid_Activity.MaterialList.size() > 0) {
            ProductListGrid_Activity.selectedMaterialIds.clear();
            DrawProductMaterial();
        }
        if (ProductListGrid_Activity.ColorList.size() > 0) {
            ProductListGrid_Activity.selectedColorIds.clear();
            DrawProductColor();
        }
    }

    @Override
    public void onBackPressed() {
        AppController.comesFromFilter = false;
        ProductListGrid_Activity.selectedWidthIds.clear();
        finish();
    }

    public void DrawProductWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        layout_width.removeAllViews();

        findViewById(R.id.ll_width_top).setVisibility(View.VISIBLE);

        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) (5 * density), (int) (15 * density), (int) (5 * density));

        final ArrayList<LinearLayout> LLlist = new ArrayList<>();
        final ArrayList<TextView> TVlist = new ArrayList<>();

        for (int i = 0; i < ProductListGrid_Activity.WidthList.size(); i++) {
            try {
                HashMap Jobject = ProductListGrid_Activity.WidthList.get(i);

                int SizeId = (int) Jobject.get("SizeID");
                String SizeName = Jobject.get("Size").toString().toUpperCase();

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.HORIZONTAL);
                LL.setLayoutParams(layoutParams);
                LL.setGravity(Gravity.CENTER);

                final TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                tvproductname.setText(SizeName);
                tvproductname.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

                LL.setId(SizeId);
                LL.addView(tvproductname);

                LLlist.add(LL);
                TVlist.add(tvproductname);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                layout_width.addView(LL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void DrawProductHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int Height = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) Height / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        layout_height.removeAllViews();

        findViewById(R.id.ll_height_top).setVisibility(View.VISIBLE);


        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) (5 * density), (int) (15 * density), (int) (5 * density));

        final ArrayList<LinearLayout> LLlist = new ArrayList<>();
        final ArrayList<TextView> TVlist = new ArrayList<>();

        for (int i = 0; i < ProductListGrid_Activity.HeightList.size(); i++) {
            try {
                HashMap Jobject = ProductListGrid_Activity.HeightList.get(i);

                int SizeId = (int) Jobject.get("SizeID");
                String SizeName = Jobject.get("Size").toString().toUpperCase();

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.HORIZONTAL);
                LL.setLayoutParams(layoutParams);
                LL.setGravity(Gravity.CENTER);

                final TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                tvproductname.setText(SizeName);
                tvproductname.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

                LL.setId(SizeId);
                LL.addView(tvproductname);

                LLlist.add(LL);
                TVlist.add(tvproductname);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });

                layout_height.addView(LL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void DrawProductMaterial() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        layout_material.removeAllViews();

        findViewById(R.id.ll_material_top).setVisibility(View.VISIBLE);

        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) (5 * density), (int) (15 * density), (int) (5 * density));

        final ArrayList<LinearLayout> LLlist = new ArrayList<>();
        final ArrayList<TextView> TVlist = new ArrayList<>();

        for (int i = 0; i < ProductListGrid_Activity.MaterialList.size(); i++) {
            try {
                HashMap Jobject = ProductListGrid_Activity.MaterialList.get(i);

                int SizeId = (int) Jobject.get("SizeID");
                String SizeName = Jobject.get("Size").toString().toUpperCase();

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.HORIZONTAL);
                LL.setLayoutParams(layoutParams);
                LL.setGravity(Gravity.CENTER);

                final TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                tvproductname.setText(SizeName);
                tvproductname.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);


                LL.setId(SizeId);
                LL.addView(tvproductname);

                LLlist.add(LL);
                TVlist.add(tvproductname);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                layout_material.addView(LL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void DrawProductColor() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        layout_color.removeAllViews();

        findViewById(R.id.ll_color_top).setVisibility(View.VISIBLE);

        float density = getResources().getDisplayMetrics().density;

        int paddingDp = (int) (10 * density);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) (5 * density), (int) (15 * density), (int) (5 * density));

        final ArrayList<LinearLayout> LLlist = new ArrayList<>();
        final ArrayList<TextView> TVlist = new ArrayList<>();

        for (int i = 0; i < ProductListGrid_Activity.ColorList.size(); i++) {
            try {
                HashMap Jobject = ProductListGrid_Activity.ColorList.get(i);

                int SizeId = (int) Jobject.get("SizeID");
                String SizeName = Jobject.get("Size").toString().toUpperCase();

                final LinearLayout LL = new LinearLayout(getApplicationContext());
                LL.setOrientation(LinearLayout.HORIZONTAL);
                LL.setLayoutParams(layoutParams);
                LL.setGravity(Gravity.CENTER);

                final TextView tvproductname = new TextView(getApplicationContext());
                tvproductname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                tvproductname.setText(SizeName);
                tvproductname.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);

                LL.setId(SizeId);
                LL.addView(tvproductname);

                LLlist.add(LL);
                TVlist.add(tvproductname);

                LL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                layout_color.addView(LL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}