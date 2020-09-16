package com.dfa.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dfa.R;
import com.dfa.pojo.response.AdvertisementResponse;
import com.dfa.ui.contribute.DonateActivity;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class SlidingImage_Adapter extends PagerAdapter {

    private ArrayList<AdvertisementResponse.Data> IMAGES;
    private LayoutInflater inflater;
    private Context context;

    public SlidingImage_Adapter(Context context, ArrayList<AdvertisementResponse.Data> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        final ImageView ivAddsBackground = (ImageView) imageLayout.findViewById(R.id.ivAddsBackground);
        final TextView mCount = imageLayout.findViewById(R.id.count);
        if(IMAGES.get(position).getPath()!=null) {

            if(!IMAGES.get(position).getExternal_link().equals("1")){
                Glide.with(context)
                        .load(IMAGES.get(position).getPath())
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(imageView);

                Glide.with(context).load(IMAGES.get(position).getPath())
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(7, 5)))
                        .into(ivAddsBackground);
            } else {
                Glide.with(context)
                        .load(R.drawable.btn_donation1)
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(imageView);

                Glide.with(context).load(R.drawable.btn_donation1)
                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(7, 5)))
                        .into(ivAddsBackground);

            }




            view.addView(imageLayout, 0);
        }


        ivAddsBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!IMAGES.get(position).getExternal_link().equals("1")){
                    Intent browserIntent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse(IMAGES.get(position).getExternal_link()));
                    context.startActivity(browserIntent);
                } else {
                    Intent intent=new Intent(context, DonateActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}