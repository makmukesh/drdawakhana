package com.vpipl.drdawakhana.Adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersDetailList_Adapter extends RecyclerView.Adapter<MyOrdersDetailList_Adapter.MyViewHolder> {
    LayoutInflater inflater = null;
    private Context context;

    public static ArrayList<HashMap<String, String>> ordersList;

    public MyOrdersDetailList_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.myordersdetails_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txt_productName.setText(Html.fromHtml(ordersList.get(position).get("ProductName")));
            holder.txt_productAmount.setText(context.getResources().getString(R.string.Rs) + " " + Html.fromHtml(ordersList.get(position).get("NetAmount")));
            AppUtils.loadProductImage(context, ordersList.get(position).get("ProductImg"), holder.imageView_product);

            holder.txt_productQty.setText(Html.fromHtml(ordersList.get(position).get("Qty")));
            holder.txt_productQtyTitle.setText(Html.fromHtml(ordersList.get(position).get("ProductQtyTitle")));

            holder.ll_mrp.setVisibility(View.GONE);

            holder.txt_productName.setSelected(true);
            holder.txt_productName.setSingleLine(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_productName, txt_productQty, txt_productAmount, txt_productQtyTitle;
        ImageView imageView_product;
        LinearLayout ll_mrp;

        public MyViewHolder(View view) {
            super(view);
            txt_productName = view.findViewById(R.id.txt_productName);
            txt_productQty = view.findViewById(R.id.txt_productQty);
            txt_productAmount = view.findViewById(R.id.txt_productAmount);
            txt_productQtyTitle = view.findViewById(R.id.txt_productQtyTitle);
            imageView_product = view.findViewById(R.id.imageView_product);
            ll_mrp = view.findViewById(R.id.ll_mrp);
        }
    }
}