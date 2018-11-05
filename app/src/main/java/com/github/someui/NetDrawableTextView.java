package com.github.someui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class NetDrawableTextView extends TextView {

    private int drawableWidth = -1, drawableHeight = -1;

    private enum DrawableBearing {
        LEFT, TOP, RIGHT, BOTTOM
    }

    private IBitmapDownLoader bitmapDownLoader;

    public interface IBitmapDownLoader {
        void load(String url, DrawableBearing bearing, NetDrawableTextView textView, IBitmapDownLoadCallback callback);
    }

    public interface IBitmapDownLoadCallback {
        void onBitmapLoaded(Bitmap bitmap);
    }

    public NetDrawableTextView(Context context) {
        this(context, null);
    }

    public NetDrawableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetDrawableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBitmapDownLoader(IBitmapDownLoader bitmapDownLoader) {
        this.bitmapDownLoader = bitmapDownLoader;
    }

    public int getDrawableWidth() {
        return drawableWidth;
    }

    public int getDrawableHeight() {
        return drawableHeight;
    }

    public void setDrawableSize(int drawableWidth, int drawableHeight) {
        this.drawableWidth = drawableWidth;
        this.drawableHeight = drawableHeight;
    }

    private boolean isSetSize() {
        return drawableWidth > 0 && drawableHeight > 0;
    }

    public void setCompoundDrawables(String leftUrl, String topUrl, String rightUrl, String bottomUrl) {
        load(leftUrl, DrawableBearing.LEFT);
        load(topUrl, DrawableBearing.TOP);
        load(rightUrl, DrawableBearing.RIGHT);
        load(bottomUrl, DrawableBearing.BOTTOM);
    }

    private void load(String url, DrawableBearing bearing) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (bitmapDownLoader != null) {
            bitmapDownLoader.load(url, bearing, this, new BitmapDownLoadCallback(bearing));
        }
    }

    private class BitmapDownLoadCallback implements IBitmapDownLoadCallback {

        private DrawableBearing bearing;

        private BitmapDownLoadCallback(DrawableBearing bearing) {
            this.bearing = bearing;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap) {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            if (isSetSize()) {
                drawable.setBounds(0, 0, drawableWidth, drawableHeight);
            } else {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            }

            switch (bearing) {
                case LEFT:
                    setCompoundDrawables(drawable, null, null, null);
                    break;
                case TOP:
                    setCompoundDrawables(null, drawable, null, null);
                    break;
                case RIGHT:
                    setCompoundDrawables(null, null, drawable, null);
                    break;
                case BOTTOM:
                    setCompoundDrawables(null, null, null, drawable);
                    break;
            }
        }
    }
}
