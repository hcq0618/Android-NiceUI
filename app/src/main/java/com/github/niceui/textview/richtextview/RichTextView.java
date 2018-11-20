package com.github.niceui.textview.richtextview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class RichTextView extends TextView {

    private IRichTextHandler richTextHandler;

    private RichTextParser richTextParser;

    public interface Callback {
        void onTextUpdate(CharSequence text);
    }

    public RichTextView(Context context) {
        this(context, null);
    }

    public RichTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        richTextParser = new RichTextParser(this);
    }

    public void setRichTextHandler(IRichTextHandler richTextHandler) {
        this.richTextHandler = richTextHandler;
    }

    @Override
    public void setText(CharSequence text, final BufferType type) {
        if (richTextHandler == null) {
            richTextHandler = new DefaultRichTextHandler();
        }

        if (richTextHandler.isAsync()) {
            richTextHandler.handleAsync(text, richTextParser, new Callback() {
                @Override
                public void onTextUpdate(CharSequence text) {
                    if (text != null) {
                        RichTextView.super.setText(text, type);
                    }
                }
            });
        } else {
            CharSequence resultText = richTextHandler.handle(text, richTextParser);
            if (resultText != null) {
                super.setText(resultText, type);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (richTextHandler != null) {
            richTextHandler.onDestroy();
        }

        super.onDetachedFromWindow();
    }
}
