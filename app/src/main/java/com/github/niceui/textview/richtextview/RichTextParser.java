package com.github.niceui.textview.richtextview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.Gravity;

import com.github.niceui.DrawableCreator;

import org.json.JSONArray;
import org.json.JSONObject;

public class RichTextParser {

    private RichTextView richTextView;

    private Context context;

    private static final int VERTICAL_BOTTOM = 0;
    private static final int VERTICAL_CENTER = 1;
    private static final int VERTICAL_TOP = 2;

    private SparseIntArray textAlignments = new SparseIntArray();

    private SparseIntArray typefaceStyles = new SparseIntArray();

    RichTextParser(RichTextView richTextView) {
        this.richTextView = richTextView;
        context = richTextView.getContext();

        textAlignments.put(0, Gravity.START | Gravity.CENTER_VERTICAL);
        textAlignments.put(1, Gravity.CENTER);
        textAlignments.put(2, Gravity.END | Gravity.CENTER_VERTICAL);

        typefaceStyles.put(0, Typeface.BOLD);
        typefaceStyles.put(1, Typeface.ITALIC);
        typefaceStyles.put(2, Typeface.BOLD_ITALIC);
    }

    CharSequence parse(String json) {
        if (TextUtils.isEmpty(json)) {
            return "";
        }

        Object richObject;
        int verticalAlignment = VERTICAL_BOTTOM;

        if (json.startsWith("{") && json.endsWith("}")) {
            try {
                JSONObject textJsonObject = new JSONObject(json);
                if (textJsonObject.has(RichTextKey.RICH_TEXT_LIST)) {
                    setBackground(textJsonObject);

                    verticalAlignment = textJsonObject.optInt(RichTextKey.VERTICAL_ALIGNMENT);

                    if (textJsonObject.has(RichTextKey.ALIGNMENT)) {
                        int alignment = textJsonObject.optInt(RichTextKey.ALIGNMENT);
                        setGravity(alignment);
                    }

                    richObject = textJsonObject.opt(RichTextKey.RICH_TEXT_LIST);
                } else {
                    richObject = textJsonObject;
                }
            } catch (Exception e) {
                richObject = json;
                e.printStackTrace();
            }
        } else if (json.startsWith("[") && json.endsWith("]")) {
            try {
                richObject = new JSONArray(json);
            } catch (Exception e) {
                richObject = json;
                e.printStackTrace();
            }
        } else {
            richObject = json;
        }

        return createText(json, richObject, verticalAlignment);
    }

    private CharSequence createText(String json, Object richObject, int verticalAlignment) {
        int textSize = px2dip(richTextView.getTextSize());

        if (richObject instanceof JSONObject) {
            return parseSpannable((JSONObject) richObject, textSize, 0, verticalAlignment);
        } else if (richObject instanceof JSONArray) {
            return arrayParseSpannable(json, (JSONArray) richObject, textSize, verticalAlignment);
        } else if (richObject instanceof String) {
            return (String) richObject;
        }

        return json == null ? "" : json;
    }

    private int dip2px(float dipValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics != null) {
            final float scale = displayMetrics.density;
            return (int) (dipValue * scale + 0.5f);
        } else {
            return (int) (dipValue * 3 + 0.5f) /* 使用主流手机的 density */;
        }
    }

    private int px2dip(float pxValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics != null) {
            final float scale = displayMetrics.density;
            return (int) (pxValue / scale + 0.5f);
        } else {
            return (int) (pxValue / 3 + 0.5f) /* 使用主流手机的 density */;
        }
    }

    private CharSequence parseSpannable(JSONObject announceJson, int textSize, int maxHeight, int verticalAlignment) {
        String text = announceJson.optString(RichTextKey.TEXT);
        SpannableStringBuilder spanText = new SpannableStringBuilder(!TextUtils.isEmpty(text) ? text : "");

        //间距实现使用添加全角空格方式
        int letterSpace = announceJson.optInt(RichTextKey.KERNING);
        if (letterSpace != 0) {
            int textViewSize = textSize != 0 ? textSize : 14;
            float scale = (float) 4.5 / (textSize == 0 ? textViewSize : textSize);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                builder.append(text.charAt(i));
                builder.append("\u00A0");
            }
            spanText = new SpannableStringBuilder(builder.toString());
            if (builder.toString().length() > 1) {
                for (int i = 1; i < builder.toString().length(); i += 2) {
                    spanText.setSpan(new ScaleXSpan(scale * letterSpace), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        if (textSize != 0) {
            spanText.setSpan((new AbsoluteSizeSpan(dip2px(textSize))), 0, spanText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (maxHeight != 0) {
            spanText.setSpan((new VerticalAlignmentSpan(maxHeight, verticalAlignment)), 0,
                    spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        String fontName = announceJson.optString(RichTextKey.FONT_NAME);
        if (!TextUtils.isEmpty(fontName)) {
            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "icon-font.ttf");
                spanText.setSpan(new CustomTypefaceSpan("", typeface), 0,
                        spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String textColor = announceJson.optString(RichTextKey.TEXT_COLOR);
        if (!TextUtils.isEmpty(textColor)) {
            try {
                spanText.setSpan(new ForegroundColorSpan(Color.parseColor(textColor)), 0,
                        spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String backgroundColor = announceJson.optString(RichTextKey.BACKGROUND_COLOR);
        if (!TextUtils.isEmpty(backgroundColor)) {
            try {
                spanText.setSpan(new BackgroundColorSpan(Color.parseColor(backgroundColor)), 0,
                        spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (announceJson.has(RichTextKey.TEXT_STYLE)) {
            int textStyle = announceJson.optInt(RichTextKey.TEXT_STYLE);
            if (typefaceStyles.indexOfKey(textStyle) >= 0) {
                int style = typefaceStyles.get(textStyle);
                spanText.setSpan(new StyleSpan(style), 0, spanText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (announceJson.has(RichTextKey.STRIKE_THROUGH)) {
            boolean strikeThrough = announceJson.optBoolean(RichTextKey.STRIKE_THROUGH);
            if (strikeThrough) {
                spanText.setSpan(new StrikethroughSpan(), 0, spanText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                CharacterStyle unableStrikeThroughSpan = new CharacterStyle() {
                    @Override
                    public void updateDrawState(TextPaint tp) {
                        tp.setStrikeThruText(false);
                    }
                };
                spanText.setSpan(unableStrikeThroughSpan, 0, spanText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (announceJson.has(RichTextKey.UNDERLINE)) {
            boolean underline = announceJson.optBoolean(RichTextKey.UNDERLINE);
            if (underline) {
                spanText.setSpan(new UnderlineSpan(), 0, spanText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                CharacterStyle unableUnderlineSpan = new CharacterStyle() {
                    @Override
                    public void updateDrawState(TextPaint tp) {
                        tp.setUnderlineText(false);
                    }
                };
                spanText.setSpan(unableUnderlineSpan, 0, spanText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spanText;
    }

    private CharSequence arrayParseSpannable(String text, JSONArray textJsonArray, int textSize, int verticalAlignment) {
        SpannableStringBuilder resultString = new SpannableStringBuilder();

        int maxHeight = arrayMaxHeight(textJsonArray, textSize, verticalAlignment);
        for (int i = 0; i < textJsonArray.length(); i++) {
            if (textJsonArray.opt(i) instanceof JSONObject) {
                JSONObject object = textJsonArray.optJSONObject(i);
                CharSequence builder = parseSpannable(object, textSize, maxHeight, verticalAlignment);
                if (builder != null) {
                    resultString.append(builder);
                }
            } else {
                return text;
            }

        }
        return resultString;
    }

    private int arrayMaxHeight(JSONArray textJsonArray, int textSize, int verticalAlignment) {
        int maxHeight = 0;
        //先找出最大的size
        if (verticalAlignment == VERTICAL_CENTER || verticalAlignment == VERTICAL_TOP) {
            int maxSize = 0;
            JSONObject maxSpan = null;
            for (int i = 0; i < textJsonArray.length(); i++) {
                JSONObject object = textJsonArray.optJSONObject(i);
                int size = object.optInt(RichTextKey.TEXT_SIZE);
                if (size == 0) {
                    size = textSize;
                }
                if (size > maxSize) {
                    maxSize = size;
                    maxSpan = object;
                }
            }

            if (maxSpan != null) {
                TextPaint maxPaint = getTextPaint(maxSpan);
                int maxAcsent = Math.abs((int) maxPaint.ascent());
                maxHeight = Math.max(maxAcsent, maxHeight);
            }
        }
        return maxHeight;
    }

    private TextPaint getTextPaint(JSONObject object) {
        TextPaint paint = new TextPaint();
        paint.setAntiAlias(true);

        float textSize = object.optInt(RichTextKey.TEXT_SIZE);
        if (textSize != 0) {
            paint.setTextSize(dip2px(textSize));
        }

        int textStyle = object.optInt(RichTextKey.TEXT_STYLE);
        if (typefaceStyles.indexOfKey(textStyle) >= 0) {
            paint.setTypeface(Typeface.defaultFromStyle(typefaceStyles.get(textStyle)));
        }
        return paint;
    }

    private void setBackground(JSONObject textJsonObject) {
        if (textJsonObject == null) {
            return;
        }

        DrawableCreator.Builder gdBuilder = DrawableCreator.builder();

        String labelColor = textJsonObject.optString(RichTextKey.LABEL_COLOR);
        if (!TextUtils.isEmpty(labelColor)) {
            gdBuilder.setColorInt(Color.parseColor(labelColor));
        }

        float cornerRadius = (float) textJsonObject.optDouble(RichTextKey.CORNER_RADIUS, 0);
        if (cornerRadius != 0) {
            gdBuilder.setRadius(cornerRadius);

            int padding = dip2px(cornerRadius / 2);
            int paddingLeft = richTextView.getPaddingLeft() != 0 ? richTextView.getPaddingLeft() : padding;
            int paddingRight = richTextView.getPaddingRight() != 0 ? richTextView.getPaddingRight() : padding;
            richTextView.setPadding(paddingLeft, richTextView.getPaddingTop(), paddingRight, richTextView.getPaddingBottom());
        }

        String borderColor = textJsonObject.optString(RichTextKey.BORDER_COLOR);
        if (!TextUtils.isEmpty(borderColor)) {
            gdBuilder.setStrokeColorInt(Color.parseColor(borderColor));
        }

        float borderWidth = (float) textJsonObject.optDouble(RichTextKey.BORDER_WIDTH, 0);
        if (borderWidth != 0) {
            gdBuilder.setStrokeWidth(borderWidth);
        }

        richTextView.setBackground(gdBuilder.build(context));
    }

    private void setGravity(int alignment) {
        if (textAlignments.indexOfKey(alignment) >= 0) {
            richTextView.setGravity(textAlignments.get(alignment));
        }
    }

    /**
     * 使TextView中不同大小字体垂直居中
     */
    private class VerticalAlignmentSpan extends MetricAffectingSpan {
        private int maxHeight;
        @SuppressWarnings("UnusedAssignment")
        private int verticalAlignment = VERTICAL_BOTTOM;

        private VerticalAlignmentSpan(int maxHeight, int verticalAlignment) {
            this.maxHeight = maxHeight;
            this.verticalAlignment = verticalAlignment;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            float top = 0;
            if (verticalAlignment == VERTICAL_CENTER) {
                top = (maxHeight + paint.getFontMetrics().ascent) / 2;
            } else if (verticalAlignment == VERTICAL_TOP) {
                top = maxHeight + paint.getFontMetrics().ascent;
            }
            paint.baselineShift -= top;
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            float top = 0;
            if (verticalAlignment == VERTICAL_CENTER) {
                top = (maxHeight + paint.getFontMetrics().ascent) / 2;
            } else if (verticalAlignment == VERTICAL_TOP) {
                top = maxHeight + paint.getFontMetrics().ascent;
            }
            paint.baselineShift -= top;
        }
    }

    /**
     * 使用自定义的tff文件字体
     */
    private class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        private CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }
            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(tf);
        }
    }

}
