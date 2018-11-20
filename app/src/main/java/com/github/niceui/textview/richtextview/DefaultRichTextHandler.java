package com.github.niceui.textview.richtextview;

import android.os.AsyncTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultRichTextHandler implements IRichTextHandler {

    private ExecutorService executor;

    private ParseTask parseTask;

    DefaultRichTextHandler() {
        executor = Executors.newFixedThreadPool(1);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public CharSequence handle(CharSequence text, RichTextParser parser) {
        if (parser != null) {
            return parser.parse(text.toString());
        }
        return null;
    }

    @Override
    public void handleAsync(CharSequence text, RichTextParser parser, RichTextView.Callback callback) {
        cancel();

        parseTask = new ParseTask(parser, this, callback);
        parseTask.executeOnExecutor(executor, text);
    }

    private void cancel() {
        if (parseTask != null) {
            parseTask.cancel(true);
        }
    }

    @Override
    public void onDestroy() {
        cancel();

        executor.shutdownNow();
    }

    private static class ParseTask extends AsyncTask<CharSequence, CharSequence, CharSequence> {

        private IRichTextHandler richTextHandler;
        private RichTextView.Callback callback;
        private RichTextParser parser;

        private ParseTask(RichTextParser parser, IRichTextHandler richTextHandler, RichTextView.Callback callback) {
            this.parser = parser;
            this.richTextHandler = richTextHandler;
            this.callback = callback;
        }

        @Override
        protected CharSequence doInBackground(CharSequence[] texts) {
            if (richTextHandler != null) {
                return richTextHandler.handle(texts[0], parser);
            }
            return null;
        }

        @Override
        protected void onPostExecute(CharSequence resultText) {
            if (callback != null) {
                callback.onTextUpdate(resultText);
            }
        }
    }
}
