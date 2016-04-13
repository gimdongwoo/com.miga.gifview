# GIF Animation view for Android
---

![gif.gif](gif.gif)

Adding GIF animations to your android views. For an iOS version have a look at https://github.com/mpociot/TiImageFromGIF


## Example

Alloy xml

~~~xml
<GifView id="view_gif" module="com.miga.gifview" autoStart="true" image="/images/test.gif"/>
~~~

javascript methods:

~~~javascript
$.gif_view.image = "/images/test.gif";
$.gif_view.autoStart = true;
$.gif_view.start();
$.gif_view.stop();

// or create it in js

var g = require('com.miga.gifview').createGifView({
    width: Ti.UI.FILL,
    height: 40,
    bubbleParent: false,
    touchEnabled: false,
    image: "/images/test.gif",
    bottom: 30,
    left: 0,
    right: 0,
    autoStart: true
});

~~~

tss properties:

~~~css
image: "/images/test.gif",
autoStart: true|false 
~~~
