package com.fluxinated.mixins.loader;

import android.support.annotation.Nullable;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 KB

    /**
     * Reads all data and copy from inputstream to outputstream and close them silently
     *
     * @param is Input stream
     * @param  os Output stream
     */
    public static void CopyStream(InputStream is, OutputStream os)
    {
        //int total = 0;
        //int size =0;

        try
        {
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            int count;
            while ((count = is.read(bytes, 0, DEFAULT_BUFFER_SIZE)) != -1)
                os.write(bytes, 0, count);
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
        finally
        {
            closeSilently(is, os);
        }
    }
    public static void closeSilently(Closeable closeable,@Nullable Closeable closeable2) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
        if(closeable2 !=null)
        {
            try {
                closeable2.close();
            } catch (Exception ignored) {
            }
        }
    }
       /* try
        {
            //size = is.available();
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
                //total +=count;
                //if(progressListener!=null)
                //progressListener.OnLoadingImage((total*100)/size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}*/
}