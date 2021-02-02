package com.vpipl.drdawakhana.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.drdawakhana.AppController;
import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.Utils.AppUtils;

public class CheckoutToPay_Adapter extends BaseAdapter {
    String TAG = "CheckoutToPay_Adapter";
    Context context;
    LayoutInflater inflater = null;

    public CheckoutToPay_Adapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return AppController.selectedProductsList.size();
    }

    @Override
    public Object getItem(int position) {
        return AppController.selectedProductsList.get(position);
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
                convertView = inflater.inflate(R.layout.checkoutproductlist_adapter, parent, false);
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

            holder.txt_productName.setText(AppController.selectedProductsList.get(position).getName());
            holder.txt_productPrice.setText("Price : ₹ " + Double.parseDouble(AppController.selectedProductsList.get(position).getNewDP()) * Double.parseDouble(AppController.selectedProductsList.get(position).getQty()));

            AppUtils.loadProductImage(context, AppController.selectedProductsList.get(position).getImagePath().replace("\\", ""), holder.imageView_product);

            holder.txt_productQty.setText("Qty : " + AppController.selectedProductsList.get(position).getQty());

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return convertView;
    }

    private static class Holder {
        TextView txt_productName, txt_productPrice, txt_productQty,  txt_productOption, txt_productColor, txt_productSize;
        ImageView imageView_product;
    }
}