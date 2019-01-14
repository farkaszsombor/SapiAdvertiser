package ro.sapientia.ms.sapiadvertiser.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ro.sapientia.ms.sapiadvertiser.R;

public class AdImageAdapter extends PagerAdapter {

    private Activity context;
    private ArrayList<String> images;

    public AdImageAdapter(Activity context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.ad_viewpager_item,container,false);
        ImageView imageView = itemView.findViewById(R.id.pager_imageView);
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);

        Glide.with(context.getApplicationContext())
                .load(images.get(position))
                .into(imageView);

        container.addView(itemView);
        return  itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
