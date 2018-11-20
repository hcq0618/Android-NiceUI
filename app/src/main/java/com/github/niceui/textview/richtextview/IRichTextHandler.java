package com.github.niceui.textview.richtextview;

public interface IRichTextHandler {

    boolean isAsync();

    CharSequence handle(CharSequence text, RichTextParser parser);

    void handleAsync(CharSequence text, RichTextParser parser, RichTextView.Callback callback);

    void onDestroy();
}
