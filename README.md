# Android-NiceUI
## DrawableCreator
Help for generating drawable or shape effects, so that u dont need to create a larger number of xml files.

**usage**
```
DrawableCreator.builder().setXXXX(...).build(context);
```
## NetDrawableTextView
Use for downloading images from network, and set images to TextViews' compound drawables(Left/Right/Top/Bottom).

u can create NetDrawableTextView from code or xml.

**usage**
1. implement IBitmapDownLoader interface, so that u can custom your download images logic, whatever http or image loader library u use.
2. when a image downloaded, u need to invoke the onBitmapLoaded callback method from IBitmapDownLoadCallback interface,that instant is delivery from IBitmapDownLoader interface.
3. create or get a NetDrawableTextView instant and then just call the setCompoundDrawables method for the instant.

```
private class BitmapDownLoader implement IBitmapDownLoader{
        @Override
        void load(String url, DrawableBearing bearing, NetDrawableTextView textView,@NotNull IBitmapDownLoadCallback callback); {
        //download a image logic
        ....
        callback.onBitmapLoaded(bitmap);
        }
}


NetDrawableTextView textView = new NetDrawableTextView(context);
textView.setBitmapDownLoader(new BitmapDownLoader());
textView.setCompoundDrawables("http://XXXXX", null, null, null) ;
```
## StrikeTextView
This is a custom TextView extend TextView, what use for setting a custom color to strikethrough, the strikethrough color can be different to its text color.

u can create StrikeTextView from code or xml.

```
StrikeTextView textView = new StrikeTextView(context);
textView.setStrikeColor(Color.RED);
textView.setStrikeHeight(1);
textView.setTextColor(Color.BLACK);
textView.setTextSize(12);
textView.setText("123");
```

- - -
**MIT License**

Copyright (c) 2018 hcq0618

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.