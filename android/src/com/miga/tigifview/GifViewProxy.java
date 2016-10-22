package com.miga.gifview;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import android.net.Uri;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.view.TiUIView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


@Kroll.proxy(creatableInModule = TigifviewModule.class)
public class GifViewProxy extends TiViewProxy {

    GifDrawable gifDrawable;
    GifImageView gifView;
    TiApplication appContext;
    Activity activity;
    String imageSrc;
    private TiBaseFile file;
    private boolean autoStart = false;
    private String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private TiBlob imgObj = null;

	public GifViewProxy() {
		super();
		appContext = TiApplication.getInstance();
		activity   = appContext.getCurrentActivity();
	}
    


	@Kroll.method
    public void stop() {
        if (gifView != null) {
            gifDrawable.stop();
        }
    }

    @Kroll.method
    public void start() {
        if (gifView != null) {
            gifDrawable.start();
        }
    }

    @Kroll.getProperty @Kroll.method
    public String getImage() {
        return imageSrc;      
    }

    @Kroll.setProperty @Kroll.method
    public void setImage(String url) {
        if (gifView != null) {
            gifDrawable.stop();
        }
        imageSrc = url;
        openImage();          
    }

    @Kroll.setProperty @Kroll.method
    public void setAutoStart(boolean val) {
        autoStart = val;       
    }
    
    @Kroll.getProperty @Kroll.method
    public boolean getAutoStart() {
        return autoStart;       
    }
    
    @Override
    public TiUIView createView(Activity activity) {
        TiUIView view = new GifView(this);
        return view;
    }

    private String getPathToApplicationAsset(String assetName)
    	{
    		// The url for an application asset can be created by resolving the specified
    		// path with the proxy context. This locates a resource relative to the 
    		// application resources folder
    		
    		String result = resolveUrl(null, assetName);
    		return result;
    	}
        
    // Handle creation options
	@Override
	public void handleCreationDict(KrollDict options) {
		super.handleCreationDict(options);

		if (options.containsKey("image")) {
			imageSrc = options.getString("image");          
		}
		if (options.containsKey("autoStart")) {
			autoStart = options.getBoolean("autoStart");          
		}

	}


    public byte[] readBytes(InputStream inputStream) throws IOException {
        // http://stackoverflow.com/a/2436413/5193915
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        
        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }
    
    public byte[] getRemoteImage(final URL aURL) {
        try {
            final URLConnection conn = aURL.openConnection();
            conn.connect();
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bis.close();

            return byteBuffer.toByteArray();            
        } catch (IOException e) {
            Log.e("round","Error fetching url");
        }
        return null;
    }
    
    private void openImage(){
        
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(imageSrc);//replace with string to compare

        if(m.find()) { 
            // external file               
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        if (gifView != null) {
                            //gifDrawable = new GifDrawable(getRemoteImage(new URL(imageSrc)));
                            Uri uri = Uri.parse(imageSrc);
                            gifView.setImageURI(uri);
                            gifDrawable = (GifDrawable) gifView.getDrawable();
                            if (autoStart){
                                gifDrawable.start();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("round","REMOTE error" + e);
                        e.printStackTrace();
                    }
                }
            });
            thread.start();        
        } else {
            // local file
            String url = getPathToApplicationAsset(imageSrc);
            TiBaseFile file = TiFileFactory.createTitaniumFile(new String[] { url }, false);            
             
            try {
                if (file!=null) {
                    if (gifView != null) {
                        
                        gifDrawable = new GifDrawable( file.getNativeFile() );
                        gifView.setImageDrawable(gifDrawable);
                        
                        if (autoStart){
                            gifDrawable.start();
                        }
                    } else {
                        Log.e("GIF","View not found");    
                    }
                } else {
                    Log.e("GIF","File is null");
                }
            } catch (IOException e){
                
            }
        }
    }
    
    private class GifView extends TiUIView {
        // create view
        public GifView(final TiViewProxy proxy) {
            super(proxy);
            String packageName = proxy.getActivity().getPackageName();
            Resources resources = proxy.getActivity().getResources();
            View gifWrapper;
            int resID_layout = -1;
            int resID_gif       = -1;

            resID_layout = resources.getIdentifier("layout", "layout", packageName);
            resID_gif    = resources.getIdentifier("gifImageView", "id", packageName);
            
            Log.i("GIF", resID_layout + " + " + resID_gif);
            
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            gifWrapper = inflater.inflate(resID_layout, null);
            
            
            if (resID_gif != 0){
                gifView   = (GifImageView)gifWrapper.findViewById(resID_gif);            
                openImage();
                setNativeView(gifWrapper);
            } else {
                Log.e("GIF", "Layout not found");
            }
        }

        @Override
        public void processProperties(KrollDict d) {
            super.processProperties(d);
            
            if (d.containsKey("image")) {
    			imageSrc = d.getString("image");          
    		}
    		if (d.containsKey("autoStart")) {
    			autoStart = d.getBoolean("autoStart");          
    		}
            openImage();
        }

    }

}
