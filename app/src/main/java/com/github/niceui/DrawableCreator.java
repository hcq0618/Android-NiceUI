package com.github.niceui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;

public class DrawableCreator {

    private DrawableCreator() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int mShape = GradientDrawable.RECTANGLE;
        private float mWidth;
        private float mHeight;
        private int mColorId;
        private int mColorInt = Color.TRANSPARENT;
        private float mRadius;
        private float mStrokeWidth;
        private int mStrokeColorId;
        private int mStrokeColorInt = Color.TRANSPARENT;
        private float[] mRadii = null;
        private GradientDrawable.Orientation mGradientOrientation =
                GradientDrawable.Orientation.LEFT_RIGHT;
        private int mStartColor = Color.TRANSPARENT;
        private int mEndColor = Color.TRANSPARENT;

        public Builder setSize(float width, float height) {
            mWidth = width;
            mHeight = height;
            return this;
        }

        public Builder setRadius(float radius) {
            mRadius = radius;
            return this;
        }

        public Builder setRadius(float leftTop, float rightTop, float rightBottom,
                                 float leftBottom) {
            mRadii = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom,
                    leftBottom, leftBottom};
            return this;
        }

        public Builder setColorRes(int colorId) {
            mColorId = colorId;
            return this;
        }

        public Builder setColorInt(int color) {
            mColorInt = color;
            return this;
        }

        public Builder setStrokeWidth(float width) {
            mStrokeWidth = width;
            return this;
        }

        public Builder setStrokeColorRes(int id) {
            mStrokeColorId = id;
            return this;
        }

        public Builder setStrokeColorInt(int colorInt) {
            mStrokeColorInt = colorInt;
            return this;
        }

        public Builder setGradientColors(int startColor, int endColor,
                                         GradientDrawable.Orientation orientation) {
            mStartColor = startColor;
            mEndColor = endColor;
            mGradientOrientation = orientation;
            return this;
        }

        public Builder setShape(int shape) {
            mShape = shape;
            return this;
        }

        public GradientDrawable build(Context context) {
            GradientDrawable mDrawable = new GradientDrawable();

            // set drawable shape
            mDrawable.setShape(mShape);

            // set radius if shape is rectangle
            if (mShape == GradientDrawable.RECTANGLE) {
                if (mRadii != null) {
                    float[] radii = new float[8];
                    for (int i = 0; i < radii.length; i++) {
                        radii[i] = dip2px(context, mRadii[i]);
                    }
                    mDrawable.setCornerRadii(radii);
                } else if (mRadius != 0) {
                    mDrawable.setCornerRadius(dip2px(context, mRadius));
                }
            }

            // size
            if (mWidth != 0 && mHeight != 0) {
                mDrawable.setSize(dip2px(context, mWidth),
                        dip2px(context, mHeight));
            }

            // set drawable color
            if (mStartColor != Color.TRANSPARENT || mEndColor != Color.TRANSPARENT) {
                // gradient colors
                mDrawable.setColors(new int[]{mStartColor, mEndColor});
                mDrawable.setOrientation(mGradientOrientation);
            } else {
                // single color
                int colorInt = mColorInt;
                if (mColorId != 0) {
                    colorInt = context.getResources().getColor(mColorId);
                }
                mDrawable.setColor(colorInt);
            }

            // set stroke
            if (mStrokeWidth != 0) {
                int strokeColor = mStrokeColorInt;
                if (mStrokeColorId != 0) {
                    strokeColor = context.getResources().getColor(mStrokeColorId);
                }
                mDrawable.setStroke(dip2px(context, mStrokeWidth), strokeColor);
            }
            return mDrawable;
        }
    }

    private static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return (int) dipValue;
        }
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics != null) {
            final float scale = displayMetrics.density;
            return (int) (dipValue * scale + 0.5f);
        } else {
            return (int) (dipValue * 3 + 0.5f);
        }
    }
}
