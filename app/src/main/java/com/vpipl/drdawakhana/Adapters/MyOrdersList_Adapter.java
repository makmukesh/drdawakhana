package com.vpipl.drdawakhana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.drdawakhana.MyOrdersDetails_Activity;
import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrdersList_Adapter extends RecyclerView.Adapter<MyOrdersList_Adapter.MyViewHolder> {
    LayoutInflater inflater = null;
    private Context context;
    String TAG = "MyOrdersList_Adapter";

    public static ArrayList<HashMap<String, String>> ordersList;

    public MyOrdersList_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        ordersList = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.myorders_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.txt_OrderNo.setText("Order No : " + ordersList.get(position).get("OrderNo"));
            holder.txt_OrderAmt.setText(context.getResources().getString(R.string.Rs) + " " +ordersList.get(position).get("OrderAmt"));
            holder.txt_OrderDate.setText(AppUtils.getDateFromAPIDate(ordersList.get(position).get("OrderDate")));
            holder.txt_OrderStatus.setText(ordersList.get(position).get("OrderStatus"));

            /*if (ordersList.get(position).get("OrderStatus").equalsIgnoreCase("N"))
                holder.txt_OrderStatus.setText("Confirmation Pending");
            else
                holder.txt_OrderStatus.setText("Order Confirmed");
*/

           /* Drawable upArrow = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow, null);
            upArrow.setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.colorAccent, null), PorterDuff.Mode.SRC_ATOP);
            holder.icon.setBackgroundDrawable(upArrow);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_OrderNo, txt_OrderAmt, txt_OrderDate, txt_OrderStatus;
        ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            txt_OrderNo = view.findViewById(R.id.txt_OrderNo);
            txt_OrderAmt = view.findViewById(R.id.txt_OrderAmt);
            txt_OrderDate = view.findViewById(R.id.txt_OrderDate);
            txt_OrderStatus = view.findViewById(R.id.txt_OrderStatus);
            icon = view.findViewById(R.id.icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "setOnClickListener.." + getPosition());

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, MyOrdersDetails_Activity.class);
                            intent.putExtra("position", getPosition());
                            context.startActivity(intent);

                        }
                    };
                    handler.postDelayed(runnable, 500);


                }
            });
        }
    }
}