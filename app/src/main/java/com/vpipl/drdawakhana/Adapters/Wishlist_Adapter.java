package com.vpipl.drdawakhana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.drdawakhana.AddCartCheckOut_Activity;
import com.vpipl.drdawakhana.AppController;
import com.vpipl.drdawakhana.ProductDetail_Activity;
import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.DatabaseHandler;
import com.vpipl.drdawakhana.Utils.QueryUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;
import com.vpipl.drdawakhana.model.ProductsList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Wishlist_Adapter extends BaseAdapter {
    String TAG = "AddCartCheckout_Adapter";
    Context context;
    LayoutInflater inflater = null;

    DatabaseHandler databaseHandler;

    public Wishlist_Adapter(Context context) {
        this.context = context;
        databaseHandler = new DatabaseHandler(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return AppController.selectedWishList.size();
    }

    @Override
    public Object getItem(int position) {
        return AppController.selectedWishList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.wishlist_product_adapter, parent, false);

                holder = new Holder();

                holder.imageView_product = (ImageView) convertView.findViewById(R.id.imageView_product);

                holder.LL_prodDetail = (LinearLayout) convertView.findViewById(R.id.LL_prodDetail);
                holder.ll_imageView = (LinearLayout) convertView.findViewById(R.id.ll_imageView);

                holder.txt_productName = (TextView) convertView.findViewById(R.id.txt_productName);
                holder.img_delete = (ImageView) convertView.findViewById(R.id.img_delete);
                holder.img_cart = (ImageView) convertView.findViewById(R.id.img_cart);
                holder.txt_add_to_cart = (TextView) convertView.findViewById(R.id.txt_add_to_cart);
                holder.txt_productPrice = (TextView) convertView.findViewById(R.id.txt_productPrice);

                holder.txt_productOption = (TextView) convertView.findViewById(R.id.txt_productOption);
                holder.txt_productColor = (TextView) convertView.findViewById(R.id.txt_productColor);
                holder.txt_productSize = (TextView) convertView.findViewById(R.id.txt_productSize);

                holder.img_delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        executeToDeleteProduct(((int) v.getTag()));
                    }
                });

                holder.LL_prodDetail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        GoToProductDetails(((int) v.getTag()));
                    }
                });

               holder.ll_imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        GoToProductDetails(((int) v.getTag()));
                    }
                });

                holder.img_cart.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        AddProducttoCart(((int) v.getTag()));
                    }
                });
                holder.txt_add_to_cart.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        AddProducttoCart(((int) v.getTag()));
                    }
                });

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.txt_productName.setText(AppController.selectedWishList.get(position).getName());
            holder.txt_productPrice.setText("Price : ₹ " + Html.fromHtml(AppController.selectedWishList.get(position).getNewDP()));

            AppUtils.loadProductImage(context, AppController.selectedWishList.get(position).getImagePath(), holder.imageView_product);

            if (AppController.selectedWishList.get(position).getSelectedOptionName().equals("NA")) {
                holder.txt_productOption.setText("Option : NA");
            } else {
                holder.txt_productOption.setText("Option : " + AppController.selectedWishList.get(position).getSelectedOptionName());
            }

            if (AppController.selectedWishList.get(position).getSelectedSizeName().equals("NA")) {
                holder.txt_productSize.setText("Size : NA");
            } else {
                holder.txt_productSize.setText("Size : " + AppController.selectedWishList.get(position).getSelectedSizeName());
            }

            if (AppController.selectedWishList.get(position).getSelectedColorName().equals("NA")) {
                holder.txt_productColor.setText("Color : NA");
            } else {
                holder.txt_productColor.setText("Color : " + AppController.selectedWishList.get(position).getSelectedColorName());
            }

            holder.img_delete.setTag(position);
            holder.ll_imageView.setTag(position);
            holder.LL_prodDetail.setTag(position);
            holder.img_cart.setTag(position);
            holder.txt_add_to_cart.setTag(position);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private void executeToDeleteProduct(final int position) {
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
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("OrderByFormNo", AppController.getSpUserInfo().getString(SPUtils.USER_ID, "")));
                            postParameters.add(new BasicNameValuePair("ProductId", "" + AppController.selectedWishList.get(position).getID().trim().replace(",", " ")));
                            postParameters.add(new BasicNameValuePair("IMEINo", "" + AppUtils.getDeviceId(context)));

                            response = AppUtils.callWebServiceWithMultiParam(context, postParameters, QueryUtils.methodToDeleteFromWishlistProduct, TAG);
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
                            if (jsonObject.getString("Status").equalsIgnoreCase("True"))
                            { deleteProduct(position);
                                AppUtils.alertDialog(context, jsonObject.getString("Message"));
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

    private void deleteProduct(final int position) {
        try {

            databaseHandler.DeleteProductFromWishlist(Integer.parseInt(AppController.selectedWishList.get(position).getID()));
            AppController.selectedWishList.remove(position);
            notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GoToProductDetails(final int position) {
        try {

            context.startActivity(new Intent(context, ProductDetail_Activity.class).putExtra("productID", "" + AppController.selectedWishList.get(position).getID()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AddProducttoCart(final int position) {
        try {

            Boolean isAdded = false;
            for (int i = 0; i < AppController.selectedProductsList.size(); i++) {
                if (AppController.selectedProductsList.get(i).getID().equals(AppController.selectedWishList.get(position).getID())) {
                    if (AppController.selectedProductsList.get(i).getSelectedSizeId().equals(AppController.selectedWishList.get(position).getSelectedSizeId()) &&
                            AppController.selectedProductsList.get(i).getSelectedOptionId().equals(AppController.selectedWishList.get(position).getSelectedOptionId()) &&
                            AppController.selectedProductsList.get(i).getSelectedColorId().equals(AppController.selectedWishList.get(position).getSelectedColorId())) {
                        isAdded = true;
                    }
                }
            }

            addItemInSelectedProductList(position, isAdded);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItemInSelectedProductList(int position, boolean isAlreadyExist) {
        try {

            ProductsList selectedProduct = AppController.selectedWishList.get(position);

            if (!isAlreadyExist)
                AppController.selectedProductsList.add(selectedProduct);

          //  deleteProduct(position);
            context.startActivity(new Intent(context, AddCartCheckOut_Activity.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Holder {
        TextView txt_productName, txt_productPrice, txt_productOption, txt_productColor, txt_productSize,txt_add_to_cart;
        ImageView img_delete, imageView_product, img_cart;
        LinearLayout LL_prodDetail, ll_imageView;
    }
}