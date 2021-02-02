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

/**
 * Created by Mukesh on 17-Nov-20.
 */

public class Inbox_Adapter extends RecyclerView.Adapter<Inbox_Adapter.MyViewHolder> {

    private ArrayList<HashMap<String, String>> datalist;
    private String olddate = "";
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout LL_date;
        TextView textView_heading,textView_heading2, txt_date;
        TextView txt_time;
        ImageView imageView7;


        public MyViewHolder(View view) {
            super(view);
            textView_heading2 = (TextView) view.findViewById(R.id.textView_heading2);
            textView_heading = (TextView) view.findViewById(R.id.textView_heading);
            txt_date = (TextView) view.findViewById(R.id.txt_date);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            LL_date = (LinearLayout) view.findViewById(R.id.LL_date);
            imageView7 = (ImageView) view.findViewById(R.id.imageView7);

        }
    }

    public Inbox_Adapter(ArrayList<HashMap<String, String>> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inboxlist_adapter, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(itemView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        olddate = datalist.get(position).get("TipDate").trim();

        holder.textView_heading2.setText(Html.fromHtml(datalist.get(position).get("MessageHeading").trim()));
        holder.textView_heading.setText(Html.fromHtml(datalist.get(position).get("MessageText").trim()));

        holder.txt_date.setText("" + datalist.get(position).get("TipDate").trim());

        if (datalist.get(position).get("TimeStamp") != null) {
            holder.txt_time.setText(datalist.get(position).get("TimeStamp"));
        }

        String imagepath = datalist.get(position).get("TipImage").trim();

        //TODO Changes
        if (!imagepath.equalsIgnoreCase("http://bhajilelo.beerstudio.com/admin/images/no-image.jpg")) {
            holder.imageView7.setVisibility(View.VISIBLE);
            AppUtils.loadProductImage(context, imagepath.trim(), holder.imageView7);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
}