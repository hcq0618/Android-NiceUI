package com.github.niceui.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class StrikeTextView extends TextView {

    private final Paint paint;

    public StrikeTextView(Context context) {
        this(context, null);
    }

    public StrikeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrikeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
    }

    public void setStrikeColor(int color) {
        paint.setColor(color);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public void setStrikeHeight(float height) {
        paint.setStrokeWidth(height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int y = getHeight() / 2;
        canvas.drawLine(0, y, getWidth(), y, paint);
    }
}
