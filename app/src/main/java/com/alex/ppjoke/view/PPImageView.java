package com.alex.ppjoke.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.libcommon.utils.PixUtil;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PPImageView extends AppCompatImageView {
    public PPImageView(Context context) {
        super(context);
    }

    public PPImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PPImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String imageUrl) {
        setImageUrl(this, imageUrl, false);
    }

    @BindingAdapter(value = {"imageUrl","isCircle"},requireAll = true)
    public static void setImageUrl(PPImageView view, String imageUrl, boolean isCircle){
        RequestBuilder<Drawable> builder = Glide.with(view).load(imageUrl);
        if(isCircle){
            builder.transform(new CircleCrop());
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(layoutParams != null && layoutParams.width>0 && layoutParams.height>0){
            builder.override(layoutParams.width,layoutParams.height);
        }
        builder.into(view);
    }

    public void bindData(int widthPx, int heightPx, int marginLeft, String imgUrl){{
        bindData(widthPx,heightPx,marginLeft,PixUtil.getScreenWidth(),PixUtil.getScreenHeight(),imgUrl);
    }}

    public void bindData(int widthPx, int heightPx, int marginLeft, int maxWidth, int maxHeight, String imgUrl){
        if(widthPx <= 0 || heightPx <= 0){
            Glide.with(this).load(imgUrl).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    int height = resource.getIntrinsicHeight();
                    int width = resource.getIntrinsicWidth();
                    setSize(width,height,marginLeft,maxWidth,maxHeight);

                    setImageDrawable(resource);
                }
            });
            return;
        }
        setSize(widthPx,heightPx,marginLeft,maxWidth,maxHeight);
        setImageUrl(this,imgUrl,false);
    }

    private void setSize(int width, int height, int marginLeft, int maxWidth, int maxHeight) {
        int finalWidth,finalHeight;
        if(width > height){
            finalWidth = maxWidth;
            finalHeight = (int) (height/(width * 1.0f/finalWidth));
        }else{
            finalHeight = maxHeight;
            finalWidth = (int) (width/(height * 1.0f/finalHeight));
        }
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(finalWidth, finalHeight);
        params.leftMargin=height>width? PixUtil.dp2px(marginLeft) :0;
        setLayoutParams(params);
    }


    public static void setBlurImageUrl(ImageView imageView, String blurUrl, int radius) {
        Glide.with(imageView).load(blurUrl).override(radius)
                .transform(new BlurTransformation())
                .dontAnimate()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setBackground(resource);
                    }
                });
    }
}
