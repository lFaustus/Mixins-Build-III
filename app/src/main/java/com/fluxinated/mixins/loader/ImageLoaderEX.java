package com.fluxinated.mixins.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.fluxinated.mixins.customviews.CircleImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Fluxi on 9/1/2015.
 */
public class ImageLoaderEX
{
    private MemoryCache mMemoryCache;
    private FileCache mFileCache;
    private Map<ImageView,String> mImageViews;
    private ExecutorService mExecutorService;
    private Handler mHandler;
    private Context mContext;
    private BaseImageDownloader mBaseImageDownloader;

    {
        this.mMemoryCache = new MemoryCache();
        this.mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
        this.mExecutorService = Executors.newFixedThreadPool(5);
        this.mHandler = new Handler();
    }

    public ImageLoaderEX(Context context)
    {
        this.mHandler = new Handler();
        this.mFileCache = new FileCache(context);
        this.mBaseImageDownloader = new BaseImageDownloader(context);
        this.mContext = context;

    }

    public ImageLoaderEX setImagePlaceHolder(int resourceID)
    {

        return this;
    }

    public ImageLoaderEX setImagePlaceHolder(String imageURI)
    {

      return this;
    }


    public void DisplayImage(String imageURI,String Name,ImageView imageview)
    {
        mImageViews.put(imageview,imageURI);
        Bitmap bitmap = mMemoryCache.get(imageURI);
        if(!(imageview instanceof CircleImageView))
        imageview.setScaleType(ImageView.ScaleType.FIT_XY);

        if(bitmap != null)
        {
            //imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            imageview.setImageBitmap(bitmap);
            Log.d("Bitmap From","Memory Cache");
        }
        else
        {
            QueuePhoto(imageURI,Name,imageview);
            Log.d("Yeah", "yeah4");
            //imageview.setScaleType(ImageView.ScaleType.FIT_XY);
              /*
              * serves as placeholder(image to be shown while waiting for the decoded image to be loaded)
              */
            //imageview.setImageResource(R.drawable.hello);
           // imageview.setImageBitmap(null);
        }
    }


    public void DisplayImageFromWeb(String imageURI,String Name,ImageView imageview)
    {
        mImageViews.put(imageview,imageURI);
        Bitmap bitmap = mMemoryCache.get(imageURI);
        if(!(imageview instanceof CircleImageView))
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);

        if(bitmap != null)
        {
            //imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            imageview.setImageBitmap(bitmap);
            Log.d("Bitmap From","Memory Cache");
        }
        else
        {
            QueuePhoto(imageURI,Name,imageview);
            Log.d("Yeah", "yeah4");
            //imageview.setScaleType(ImageView.ScaleType.FIT_XY);
              /*
              * serves as placeholder(image to be shown while waiting for the decoded image to be loaded)
              */
            //imageview.setImageResource(R.drawable.hello);
            // imageview.setImageBitmap(null);
        }
    }


    private void QueuePhoto(String imageUrl,String Name,ImageView imageview)
    {
        mExecutorService.submit(new PhotoLoader(new PhotoToLoad(Name, imageUrl, imageview)));
    }

    private Bitmap getBitmap(PhotoToLoad photoToLoad)
    {
        File f = mFileCache.getFile(photoToLoad.URL);

        //from SD cache
        Bitmap b = readFileAndDecodeToBitmap(f);
        if(b!=null)
        {
            Log.d("Bitmap From", "SD Cache");
            return b;
        }


        try
        {

            InputStream is = this.mBaseImageDownloader.getStream(photoToLoad.URL);

            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);

            return readFileAndDecodeToBitmap(f);
        }
        catch(Throwable ex)
        {
            if(ex instanceof OutOfMemoryError)
                mMemoryCache.clear();
            else if (ex instanceof URISyntaxException)
            {
                Log.e("URISyntaxException",ex.getMessage());
            }

            else
                ex.printStackTrace();

        }
        return null;
    }

    private Bitmap getBitmapFromWeb(PhotoToLoad photoToLoad)
    {
        File f = mFileCache.getFile(photoToLoad.URL);

        //from SD cache
        Bitmap b = readFileAndDecodeToBitmap(f);
        if(b!=null)
        {
            Log.d("Bitmap From", "SD Cache");
            return b;
        }


        try
        {

            InputStream is = this.mBaseImageDownloader.getStream(photoToLoad.URL);

            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);

            return readFileAndDecodeToBitmap(f);
        }
        catch(Throwable ex)
        {
            if(ex instanceof OutOfMemoryError)
                mMemoryCache.clear();
            else if (ex instanceof URISyntaxException)
            {
                Log.e("URISyntaxException",ex.getMessage());
            }

            else
                ex.printStackTrace();

        }
        return null;
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap readFileAndDecodeToBitmap(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=100;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            if(scale>=2){
                scale/=2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);

            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




    private class PhotoToLoad
    {
        private String Name;
        private String URL;
        private ImageView mImageView;

        public PhotoToLoad(String name, String URL, ImageView imageview)
        {
            this.Name = name;
            this.URL =  URL;
            this.mImageView = imageview;
        }
    }

    boolean imageviewPersistInMap(PhotoToLoad photoToLoad)
    {
        String tag = mImageViews.get(photoToLoad.mImageView);
        if(tag == null || !tag.equals(photoToLoad.URL))
            return false;
        return true;
    }

    private class PhotoLoader implements Runnable
    {
        PhotoToLoad mPhotoToLoad;

        PhotoLoader(PhotoToLoad photoToload)
        {
            this.mPhotoToLoad = photoToload;
        }

        @Override
        public void run()
        {

            try
            {
                if(!imageviewPersistInMap(mPhotoToLoad))
                    return;

                //Bitmap bmp = getBitmap(mPhotoToLoad);
                Bitmap  bmp = getBitmapFromWeb(mPhotoToLoad);
               // if(bmp == null)
                   // bmp = getBitmapFromWeb(mPhotoToLoad);
                ImageLoaderEX.this.mMemoryCache.put(mPhotoToLoad.URL,bmp);

                if(!imageviewPersistInMap(mPhotoToLoad))
                    return;

                BitmapDisplayer mBitmapDisplayer = new BitmapDisplayer(bmp,mPhotoToLoad);
                ImageLoaderEX.this.mHandler.post(mBitmapDisplayer);

            }catch(Throwable th)
            {
                th.printStackTrace();
            }
        }
    }
    private class BitmapDisplayer implements Runnable
    {
        Bitmap mBitmap;
        PhotoToLoad mPhotoToLoad;

        public BitmapDisplayer(Bitmap bitmap,PhotoToLoad photoToLoad)
        {
            this.mBitmap = bitmap;
            this.mPhotoToLoad = photoToLoad;


        }
        @Override
        public void run()
        {

            if(!imageviewPersistInMap(mPhotoToLoad))
            {
                Log.e("animation","animate");
                return;
            }
            if(this.mBitmap != null)
            {
                //Animations(this.mPhotoToLoad.mImageView, mBitmap);
                //Animations.animate(this.mPhotoToLoad.mImageView, mBitmap);
                //Animate(this.mPhotoToLoad.mImageView, mBitmap);
                this.mPhotoToLoad.mImageView.setImageBitmap(mBitmap);
            }
            else //if image not found or failed to load
                this.mPhotoToLoad.mImageView.setImageBitmap(null);
        }
    }



    private void Animate(final ImageView v, final Bitmap mBitmap)
    {
        Animation slideOut;
        final Animation slideIn;

        slideOut = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.0F,
                Animation.RELATIVE_TO_PARENT,0.0F,
                Animation.RELATIVE_TO_PARENT,0.0F,
                Animation.RELATIVE_TO_PARENT,-1.0F);
        slideOut.setDuration(1000);
        slideOut.setInterpolator(new AccelerateDecelerateInterpolator());


        slideIn = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,0.0F,
                Animation.RELATIVE_TO_PARENT,0.0F,
                Animation.RELATIVE_TO_PARENT,-1.0F,
                Animation.RELATIVE_TO_PARENT,0.0F);
        slideIn.setDuration(1000);
        slideIn.setInterpolator(new BounceInterpolator());

        slideOut.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(mBitmap);
                v.startAnimation(slideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        v.startAnimation(slideOut);
    }

    private class Animate
    {
        View view;
        ArrayList<Animation> mAnimations = new ArrayList<>();
        int mAnimationSetRepeatCount = 0;
        int mAnimationSetStartTime;
        Interpolator mAnimationSetInterpolator;
        boolean mAnimationSetFillAfter = false, mAllowShareInterpolator = false;
        AnimationSet mAnimationSet;
        Animation.AnimationListener mAnimationListener;
        public Animate(View view)
        {
            this.view = view;
        }

        public Animate setAnimations(Animation animation)
        {
            this.mAnimations.add(animation);
            return this;
        }

        public Animate setAnimationRepeatCount(int value)
        {
            this.mAnimationSetRepeatCount = value;
            return this;
        }

        public Animate setAnimationSetFillAfter(boolean setfillafter)
        {
            this.mAnimationSetFillAfter = setfillafter;
            return this;
        }

        public Animate setAnimationSetStartTime(int value)
        {
            this.mAnimationSetStartTime = value;
            return this;
        }

        public Animate AllowShareInterpolator(boolean allow)
        {
            this.mAllowShareInterpolator = allow;
            return this;
        }

        public Animate setAnimationSetInterpolator(Interpolator interpolator)
        {
            this.mAnimationSetInterpolator = interpolator;
            return this;
        }

        public Animate setAnimationSetListener(Animation.AnimationListener animationListener)
        {
            this.mAnimationListener = animationListener;
            return this;
        }
        public void startAnimation()
        {
            this.mAnimationSet = new AnimationSet(this.mAllowShareInterpolator);
            for (Animation animation: this.mAnimations)
            {
               this.mAnimationSet.addAnimation(animation);
            }
            this.mAnimationSet.setRepeatCount(this.mAnimationSetRepeatCount);
            this.mAnimationSet.setFillAfter(this.mAnimationSetFillAfter);
            this.mAnimationSet.setStartTime(this.mAnimationSetStartTime);

            if(this.mAnimationSetInterpolator != null && this.mAllowShareInterpolator)
            {
                this.mAnimationSet.setInterpolator(this.mAnimationSetInterpolator);
            }
            if(this.mAnimationListener != null)
                this.mAnimationSet.setAnimationListener(this.mAnimationListener);

            this.view.setAnimation(this.mAnimationSet);
            this.mAnimationSet.start();
            //this.view.startAnimation(this.mAnimationSet);
            //return this;
        }
    }
}
