package com.vpipl.drdawakhana.Adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vpipl.drdawakhana.AppController;
import com.vpipl.drdawakhana.CategoryLevel1ListActivity;
import com.vpipl.drdawakhana.CategoryLevel2ListActivity;
import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.SearchProducts_Activity;
import com.vpipl.drdawakhana.model.ProductsList;
import com.vpipl.drdawakhana.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductListGrid_Adapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater = null;

    List<ProductsList> productList;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<>();

    public ProductListGrid_Adapter(Context context, List<ProductsList> productList, String comesfrom) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productList = productList;

    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.productgrid_adapter, parent, false);
                holder = new Holder();
                holder.txt_productName = (TextView) convertView.findViewById(R.id.txt_productName);
                holder.txt_productAmount = (TextView) convertView.findViewById(R.id.txt_productAmount);
                holder.imageView_product = (ImageView) convertView.findViewById(R.id.imageView_product);
                holder.ll_add_to_cart = (LinearLayout) convertView.findViewById(R.id.ll_add_to_cart);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.txt_productName.setSelected(true);
            holder.txt_productName.setSingleLine(true);

            StringBuilder sb = new StringBuilder(productList.get(position).getName().trim());
            int ii = 0;
            while ((ii = sb.indexOf(" ", ii + 20)) != -1) {
                sb.replace(ii, ii + 1, "\n");
            }

            holder.txt_productName.setText(sb.toString());

            String NewDP = "₹ " + productList.get(position).getNewDP() + "/-";
            String NewMRP = productList.get(position).getNewMRP();
            String DiscountPer = productList.get(position).getDiscountPer() + "% off";
            Spannable spanString = null;

            if (DiscountPer.equalsIgnoreCase("0% off")) {
                spanString = new SpannableString("" + NewDP);
                spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.app_color_black)), 0, NewDP.length(), 0);
                //spanString.setSpan(new AbsoluteSizeSpan(20), 0,NewDP.length(),0);
            } else {
//                    spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  " + DiscountPer);
                spanString = new SpannableString("" + NewDP + "  " + NewMRP + "  ");

                spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.app_color_black)), 0, NewDP.length(), 0);
                spanString.setSpan(new RelativeSizeSpan(1.0f), 0, NewDP.length(), 0);
                StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
                spanString.setSpan(boldSpan, 0, NewDP.length(), 0);

                spanString.setSpan(new StrikethroughSpan(), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);
                spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.app_color_gray_dark)), (NewDP.length() + 2), (((NewDP.length() + 2)) + (NewMRP.length())), 0);

                spanString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.app_color_gray_dark)), ((((NewDP.length() + 2)) + (NewMRP.length())) + 2), spanString.length(), 0);
            }
            holder.txt_productAmount.setText(spanString);

               /* if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false))
                    holder.txt_productAmount.setVisibility(View.VISIBLE);
                else
                    holder.txt_productAmount.setVisibility(View.GONE);*/


            AppUtils.loadProductImage(context, productList.get(position).getImagePath(), holder.imageView_product);

            holder.ll_add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductsList selectedProducts = productList.get(position);

                    if (Integer.parseInt(selectedProducts.getQty()) > 0)
                        addItemInSelectedProductList(position);

                    //  ProductListGrid_Activity.setNetPaybleValue();
                    //  SearchProducts_Activity.setNetPaybleValue();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        TextView txt_productName, txt_productAmount;
        ImageView imageView_product;
        LinearLayout ll_add_to_cart;
    }

    private void addItemInSelectedProductList(final int position) {
        try {
            boolean already_exists = false;
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                if (AppController.selectedProductsList.get(i).getName().equalsIgnoreCase(productList.get(position).getName())) {
                    already_exists = true;
                }
            }
            if (already_exists) {
                //AppUtils.alertDialog(context, "Selected Product already exist in Cart. Please update quantity in Cart.");
                Toast.makeText(context, "Selected Product already exist in Cart. Please update quantity in Cart.", Toast.LENGTH_SHORT).show();
            } else {
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric(10);

                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + productList.get(position).getID());
                selectedProduct.setcode("" + productList.get(position).getcode());
                selectedProduct.setName("" + productList.get(position).getName());

                selectedProduct.setNewMRP("" + productList.get(position).getNewMRP());
                selectedProduct.setNewDP("" + productList.get(position).getNewDP());
                selectedProduct.setDiscountPer("" + productList.get(position).getDiscount());

                selectedProduct.setQty("1");
                selectedProduct.setBaseQty("1");

                selectedProduct.setCatID("" + productList.get(position).getCatID());
                selectedProduct.setShipCharge("" + productList.get(position).getShipCharge());

                selectedProduct.setSelectedSizeName("");
                selectedProduct.setSelectedSizeId("0");

                selectedProduct.setselectedColorName("");
                selectedProduct.setselectedColorId("0");

                selectedProduct.setSelectedOptionName("");
                selectedProduct.setSelectedOptionId("0");

                selectedProduct.setImagePath("" + productList.get(position).getImagePath());

                AppController.selectedProductsList.add(selectedProduct);

                if (productList.get(position).getCatID().equalsIgnoreCase("L1")) {
                    CategoryLevel1ListActivity.setBadge();
                } else if (productList.get(position).getCatID().equalsIgnoreCase("L2")) {
                    CategoryLevel2ListActivity.setBadge();
                } else if (productList.get(position).getCatID().equalsIgnoreCase("S")) {
                    SearchProducts_Activity.setBadge();
                }

                // AppUtils.alertDialog(context, "Success: You have added " + productList.get(position).getName() + " to your shopping cart!");
                // Toasty.success(context, "Success: You have added " + productList.get(position).getName() + " to your shopping cart!", Toast.LENGTH_SHORT, true).show();
                Toast.makeText(context, "Success: You have added " + productList.get(position).getName() + " to your shopping cart!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}