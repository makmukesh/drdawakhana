package com.vpipl.drdawakhana.Adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.drdawakhana.ProductListGrid_Activity;
import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.model.Vipindata;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VipinAdapter extends RecyclerView.Adapter<VipinAdapter.ViewHolder> {
    private List<Vipindata> notification_data;
    private Context context;

    public VipinAdapter(List<Vipindata> notification_data, Context context) {
        this.notification_data = notification_data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vipin_prodlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Vipindata listData=notification_data.get(position);
        holder.product_name.setText(listData.getHeading());
        Picasso.with(context).load(listData.getImgPath()).into(holder.pro_image);
        String id  = listData.getHID();
        holder.card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductListGrid_Activity.class);
                intent.putExtra("HID",  listData.getHID());
                intent.putExtra("ChildItemTitle", listData.getHeading());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notification_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView product_name;
        public CardView card1;
        private ImageView pro_image;

        public ViewHolder(View itemView) {
            super(itemView);
            //img=(ImageView)itemView.findViewById(R.id.image_view);
            product_name = (TextView)itemView.findViewById(R.id.product_name);
            pro_image = (ImageView) itemView.findViewById(R.id.pro_image);
            card1 = (CardView) itemView.findViewById(R.id.card1);
        }
    }
}