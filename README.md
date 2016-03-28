# GIF Animation view for Android
---

![gif.gif](gif.gif)

Adding GIF animations to your android views. For an iOS version have a look at https://github.com/mpociot/TiImageFromGIF


## Example

Alloy xml

~~~xml
<GifView id="view_gif" module="com.miga.gifview" autoStart="true" image="/images/test.gif"/>
~~~

methods:

~~~javascript
$.gif_view.image = "/images/test.gif";
$.gif_view.autoStart = true;
$.gif_view.start();
$.gif_view.stop();
~~~

properties:

~~~css
image: "/images/test.gif",
autoStart: true|false 
~~~
