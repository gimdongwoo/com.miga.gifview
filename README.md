# GIF Animation view for Android
---

![gif.gif](gif.gif)

Adding GIF animations to your android views using https://github.com/felipecsl/GifImageView 

For an iOS version have a look at https://github.com/mpociot/TiImageFromGIF


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


### ListView example:

index.js
~~~javascript
var template = {
    properties: {
        backgroundColor: 'transparent',
        height: Ti.UI.SIZE
    },
    childTemplates: [{
        type: 'gif.GifView',
        bindId: 'gif',
        properties: {
            image:"",
        }

    }]
};

var s = Ti.UI.createListSection();

var l = Ti.UI.createListView({
    templates: {
        'template': template
    },
    defaultItemTemplate: 'template',
    sections: [s]
});


var list = [];

for (var i = 0; i < 10; ++i) {

    list.push({
        gif: {
            image: "/images/test.gif",
            height: 60,
            width: 60,
            autoStart:true
        },
        properties: {
            height: 61,
            id: i
        }
    });
}
s.setItems(list);
$.index.add(l);
$.index.open();
~~~

alloy.js
~~~javascript
var gif = require('com.miga.gifview');  
~~~


### Fix: First frame is not transparent but has a black background

Try to add your gif with `autoStart: false` and use a short delay to start it:
```_.delay(function(){ gif.start();}, 150);```
