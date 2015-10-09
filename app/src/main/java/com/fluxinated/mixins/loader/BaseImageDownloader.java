package com.fluxinated.mixins.loader;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Fluxi on 9/1/2015.
 */
public class BaseImageDownloader implements ImageDownloader
{
    /** {@value} */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /** {@value} */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    protected static final int MAX_REDIRECT_COUNT = 5;

    private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. " + "You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";

    private Context mContext;
    protected final int connectTimeout;
    protected final int readTimeout;

    public BaseImageDownloader(Context context)
    {
        this(context, DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT);
    }

    public BaseImageDownloader(Context context,int connectTimeout,int readTimeout)
    {
        this.mContext = context.getApplicationContext();
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }
    @Override
    public InputStream getStream(String imageUri) throws IOException, URISyntaxException
    {
        switch(Scheme.ofUri(imageUri))
        {

            case FILE:
                return getStreamFromFile(imageUri);
            case HTTP:
            case HTTPS:
                return getStreamFromNetwork(imageUri);
            default:
                return getStreamFromOtherSource(imageUri);
        }

    }

    private InputStream getStreamFromFile(String Uri) throws IOException, URISyntaxException
    {
        //String filepath = Scheme.FILE.crop(URI);
        URI filepath = Scheme.FILE.Uri(Uri);
      //  Log.e("pathpath",Uri);
        return new FileInputStream(new File(filepath));
    }

    /**
     * @param conn Opened request connection (response code is available)
     * @return <b>true</b> - if data from connection is correct and should be read and processed;
     *         <b>false</b> - if response contains irrelevant data and shouldn't be processed
     * @throws IOException
     */
    protected boolean shouldBeProcessed(HttpURLConnection conn) throws IOException {
        return conn.getResponseCode() == 200;
    }

    /**
     * Create {@linkplain HttpURLConnection HTTP connection} for incoming URL
     *
     * @param url   URL to connect to
     * @return {@linkplain HttpURLConnection Connection} for incoming URL. Connection isn't established so it still configurable.
     * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
     *                     URL.
     */
    protected HttpURLConnection createConnection(String url) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setInstanceFollowRedirects(true);
        return conn;
    }

    private InputStream getStreamFromNetwork(String Uri) throws IOException, URISyntaxException {
        Log.e("from","network");
        /*HttpURLConnection conn = createConnection(URI);

        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"));
            redirectCount++;
        }

       InputStream imageStream = null;
        try {
            imageStream = conn.getInputStream();
        } catch (IOException e) {
            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
            //IoUtils.readAndCloseStream(conn.getErrorStream());
            //throw e;
        }
        if (!shouldBeProcessed(conn))
        {
            Utils.closeSilently(imageStream,null);
            throw new IOException("Image request failed with response code " + conn.getResponseCode());
        }*/
        URL imageURI = new URL(Uri);
        HttpURLConnection conn = (HttpURLConnection)imageURI.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setInstanceFollowRedirects(true);
        InputStream is=conn.getInputStream();
        return is;
    }

    private InputStream getStreamFromOtherSource(String imageUri) throws IOException {
        String filepath = Scheme.FILE.crop(imageUri);
        Log.e("FilePath", filepath);
        throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri));
    }
}
