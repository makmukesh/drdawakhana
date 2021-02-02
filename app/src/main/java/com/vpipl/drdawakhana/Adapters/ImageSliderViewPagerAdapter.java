package com.vpipl.drdawakhana.Adapters;

import android.content.Context;

import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpipl.drdawakhana.AppController;
import com.vpipl.drdawakhana.R;
import com.vpipl.drdawakhana.Utils.AppUtils;


/**
 * Specifies the WelcomeScreen Item List functionality
 */
public class ImageSliderViewPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public ImageSliderViewPagerAdapter(Context con) {
        this.context = con;
    }

    @Override
    public int getCount() {
        return AppController.homeSliderList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView swipeImageView;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.home_swipeimage_layout, container, false);
        swipeImageView = itemView.findViewById(R.id.swipeImageView);

        try {

            try {
                AppUtils.loadSlidingImage(context, AppController.homeSliderList.get(position).get("ImgPath"), swipeImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }

            swipeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 /*   if (!AppController.homeSliderList.get(position).get("CategoryID").equalsIgnoreCase("0"))
                    {
                        Intent intent = new Intent(context, ProductListGrid_Activity.class);
                        intent.putExtra("HID", AppController.homeSliderList.get(position).get("CategoryID"));
                        intent.putExtra("ChildItemTitle", "");
                        context.startActivity(intent);
                    }*/
                }
            });

            container.addView(itemView);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}