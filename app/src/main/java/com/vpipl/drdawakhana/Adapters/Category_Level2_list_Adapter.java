package com.vpipl.drdawakhana.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.drdawakhana.ProductListGrid_Activity;
import com.vpipl.drdawakhana.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Category_Level2_list_Adapter extends RecyclerView.Adapter<Category_Level2_list_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;

    public Category_Level2_list_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_category_level1_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_productName.setText(array_List.get(position).get("SubCategory"));
            // holder.txt_date_time.setText(array_List.get(position).get("imgPath"));
           // AppUtils.loadProductImage(context , array_List.get(position).get("imgPath") , holder.imageView_product);

            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductListGrid_Activity.class);
                    intent.putExtra("categoryID", "" + array_List.get(position).get("CID"));
                    intent.putExtra("SCID", "" + array_List.get(position).get("SCID"));
                    intent.putExtra("SCID2", "" + array_List.get(position).get("SCID2"));
                    intent.putExtra("SubCategory", "" + array_List.get(position).get("SubCategory"));
                    intent.putExtra("Type", "S2");
                    context.startActivity(intent);
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

        TextView txt_productName;
        ImageView imageView_product;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            imageView_product = (ImageView) view.findViewById(R.id.imageView_product);
            txt_productName = (TextView) view.findViewById(R.id.txt_productName);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}