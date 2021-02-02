package com.vpipl.drdawakhana.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.ThanksScreen_Activity;
import com.vpipl.drdawakhana.Utils.AppUtils;
import com.vpipl.drdawakhana.Utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Mukesh on 17-Nov-20.
 */
public class ThanksOrderDetail_Adapter extends BaseAdapter {
    String TAG = "ThanksOrderDetail_Adapter";
    Context context;
    LayoutInflater inflater = null;
    public ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();

    public ThanksOrderDetail_Adapter(Context context, ArrayList<HashMap<String, String>> orderDetailsList) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.orderDetailsList = orderDetailsList;
    }

    @Override
    public int getCount() {
        return orderDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderDetailsList.get(position);
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
                convertView = inflater.inflate(R.layout.thanks_orderdetail_adapter, parent, false);
                holder = new Holder();

                holder.imageView_product = (ImageView) convertView.findViewById(R.id.imageView_product);
                holder.txt_productName = (TextView) convertView.findViewById(R.id.txt_productName);
                holder.txt_productPrice = (TextView) convertView.findViewById(R.id.txt_productPrice);
                holder.txt_productQty = (TextView) convertView.findViewById(R.id.txt_productQty);
                holder.txt_productOption = (TextView) convertView.findViewById(R.id.txt_productOption);
                holder.txt_productSize = (TextView) convertView.findViewById(R.id.txt_productSize);
                holder.txt_productColor = (TextView) convertView.findViewById(R.id.txt_productColor);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.txt_productName.setText("" + orderDetailsList.get(position).get("ProductName"));
            holder.txt_productPrice.setText(Html.fromHtml("Price : ₹ " + orderDetailsList.get(position).get("Netamount")));
            holder.txt_productQty.setText("Qty : " + orderDetailsList.get(position).get("Quantity"));
            holder.txt_productSize.setText("Size : " + orderDetailsList.get(position).get("Size"));

           /* if (orderDetailsList.get(position).get("Option").equals("NA")) {
                holder.txt_productOption.setText("Option : NA");
                holder.txt_productOption.setVisibility(View.GONE);
            } else {
                holder.txt_productOption.setVisibility(View.VISIBLE);
                holder.txt_productOption.setText("Option : " + orderDetailsList.get(position).get("Option"));
            }

            if (orderDetailsList.get(position).get("Size").equals("NA")) {
                holder.txt_productSize.setText("Size : NA");
                holder.txt_productSize.setVisibility(View.GONE);
            } else {
                holder.txt_productSize.setVisibility(View.VISIBLE);
                holder.txt_productSize.setText("Size : " + orderDetailsList.get(position).get("Size"));
            }

            if (orderDetailsList.get(position).get("Color").equals("NA")) {
                holder.txt_productColor.setText("Color : NA");
                holder.txt_productColor.setVisibility(View.GONE);
            } else {
                holder.txt_productColor.setVisibility(View.VISIBLE);
                holder.txt_productColor.setText("Color : " + orderDetailsList.get(position).get("Color"));
            }*/
            if (orderDetailsList.get(position).get("ImageUrl").contains(SPUtils.productImageURL))
                AppUtils.loadProductImage(context, orderDetailsList.get(position).get("ImageUrl"), holder.imageView_product);
            else
                AppUtils.loadProductImage(context, SPUtils.productImageURL + orderDetailsList.get(position).get("ImageUrl"), holder.imageView_product);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        ImageView imageView_product;

        TextView txt_productName, txt_productPrice, txt_productQty, txt_productOption, txt_productColor, txt_productSize;

    }
}