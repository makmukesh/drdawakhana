package com.vpipl.drdawakhana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.drdawakhana.AppController;
import com.vpipl.drdawakhana.ProductDetail_Activity;
import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.model.ProductsList;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home_Top_Product_list_Adapter extends RecyclerView.Adapter<Home_Top_Product_list_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;
    String ProductID = "0", ProductName, NewMRP = "0", NewDP = "0", DiscountPer = "0", CatID = "0", image;
    DatabaseHandler databaseHandler;

    public Home_Top_Product_list_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_top_product_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_productName.setText(array_List.get(position).get("ProductName"));
            holder.txt_productAmount.setText("\u20B9 " + array_List.get(position).get("DP"));
            // holder.txt_date_time.setText(array_List.get(position).get("imgPath"));
            AppUtils.loadProductImage(context, array_List.get(position).get("ImagePath"), holder.imageView_product);

            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetail_Activity.class);
                    intent.putExtra("productID", "" + array_List.get(position).get("ProdId"));
                    intent.putExtra("ProductName", "" + array_List.get(position).get("ProductName"));
                    context.startActivity(intent);
                }
            });
            holder.imageView_wishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(context, ProductDetail_Activity.class);
                    intent.putExtra("ProdId", "" + array_List.get(position).get("ProdId"));
                    intent.putExtra("ProductName", "" + array_List.get(position).get("ProductName"));
                    context.startActivity(intent);*/
                    ProductID = array_List.get(position).get("ProdId");
                    ProductName = array_List.get(position).get("ProductName");
                    NewMRP = array_List.get(position).get("MRP");
                    NewDP = array_List.get(position).get("DP");
                    DiscountPer = array_List.get(position).get("Discount");
                    CatID = array_List.get(position).get("CatId");
                    image = array_List.get(position).get("ImagePath");
                    if(!ProductID.equalsIgnoreCase("")) {
                        createParametersForWishlist();

                    }
                }
            });

            holder.txt_productName.setSelected(true);
            holder.txt_productName.setSingleLine(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_productName, txt_productAmount;
        ImageView imageView_product, imageView_wishlist;
        FrameLayout ll_main;
        LinearLayout ll_add_to_cart;

        public MyViewHolder(View view) {
            super(view);
            imageView_product = (ImageView) view.findViewById(R.id.imageView_product);
            imageView_wishlist = (ImageView) view.findViewById(R.id.imageView_wishlist);
            txt_productName = (TextView) view.findViewById(R.id.txt_productName);
            txt_productAmount = (TextView) view.findViewById(R.id.txt_productAmount);
            ll_main = (FrameLayout) view.findViewById(R.id.ll_main);
            ll_add_to_cart = (LinearLayout) view.findViewById(R.id.ll_add_to_cart);
        }
    }

    private void createParametersForWishlist() {

        try {
            List<NameValuePair> postParameters = new ArrayList<>();

            JSONArray jsonArrayMainCart = new JSONArray();

            JSONObject jsonObjectMainCart = new JSONObject();
            jsonObjectMainCart.put("OrderByFormNo", "" + AppController.getSpUserInfo().getString(SPUtils.USER_ID, ""));
            jsonObjectMainCart.put("ProductID", "" + ProductID);
            jsonObjectMainCart.put("Qty", "1");
            jsonObjectMainCart.put("OrderFor", "WR");
            jsonObjectMainCart.put("UID", "0");
            jsonObjectMainCart.put("Size", "0");
            jsonObjectMainCart.put("Color", "0");
            jsonObjectMainCart.put("UserType", "C");

            jsonArrayMainCart.put(jsonObjectMainCart);

            postParameters.add(new BasicNameValuePair("WishListData", jsonArrayMainCart.toString()));

            executeToAddItemInWishList(postParameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeToAddItemInWishList(final List<NameValuePair> postParameters) {
        try {
            if (AppUtils.isNetworkAvailable(context)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(context);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(context, postParameters, QueryUtils.methodToAddToWishList, "TAG");
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
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                addItemInSelectedWishList();
                            } else {
                                AppUtils.alertDialog(context, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(context);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }

    private void addItemInSelectedWishList() {
        try {
            boolean already_exists = false;


            for (int i = 0; i < AppController.selectedWishList.size(); i++) {
                if (AppController.selectedWishList.get(i).getID().equals(ProductID)) {
                    already_exists = true;
                }
            }

            if (already_exists) {
                AppUtils.alertDialog(context, "Selected Product already exist in Wishlist. Please update quantity in Wishlist.");
            } else {
                ProductsList selectedProduct = new ProductsList();

                String randomNo = AppUtils.generateRandomAlphaNumeric(10);
                selectedProduct.setRandomNo("" + randomNo.trim().replace(",", " "));
                selectedProduct.setUID("0");//UID save only in case of combo package else value would be 0.
                selectedProduct.setID("" + ProductID);
                selectedProduct.setcode("" + ProductID);
                selectedProduct.setName("" + ProductName);
                selectedProduct.setNewMRP("" + NewMRP);
                selectedProduct.setNewDP("" + NewDP);
                selectedProduct.setDiscountPer("" + DiscountPer);
                selectedProduct.setQty("1");
                selectedProduct.setBaseQty("1");
                selectedProduct.setCatID("" + CatID);
                selectedProduct.setShipCharge("0");
                selectedProduct.setSelectedSizeName("Test");
                selectedProduct.setSelectedSizeId("0");
                selectedProduct.setselectedColorName("Test");
                selectedProduct.setselectedColorId("0");
                selectedProduct.setSelectedOptionName("Test");
                selectedProduct.setSelectedOptionId("0");
                selectedProduct.setImagePath("" + image);

                AppController.selectedWishList.add(selectedProduct);

                databaseHandler = new DatabaseHandler(context);
                databaseHandler.addProductsWishlist(selectedProduct);

                AppUtils.alertDialog(context, "Success: You have added " + ProductName + " to your Wish list!");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}