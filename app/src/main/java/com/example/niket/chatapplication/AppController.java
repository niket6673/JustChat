package com.example.niket.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.X509TrustManagerExtensions;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class AppController extends MultiDexApplication {

    public static final String TAG = AppController.class.getSimpleName();
   /* private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }*/


    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        SharedPreferences preferences = getSharedPreferences(GlobalConstants.SHARED_PREFRENCES, 0);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //SugarContext.terminate();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        }
        return mRequestQueue;
    }


    private static final int MY_SOCKET_TIMEOUT_MS = 120000;

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler defaultUEH;


        /*
         * if any of the parameters is null, the respective functionality
         * will not be used
         */
        public CustomExceptionHandler() {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        public void uncaughtException(Thread t, Throwable e) {
            //String timestamp = TimestampFormatter.getInstance().getTimestamp();

            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            String stacktrace = result.toString();
            printWriter.close();
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            int version = Build.VERSION.SDK_INT;
            String versionRelease = Build.VERSION.RELEASE;
            String appVersion = "";
            try {
                PackageInfo pInfo = mInstance.getPackageManager().getPackageInfo(getPackageName(), 0);
                appVersion = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e2) {
                e2.printStackTrace();
                appVersion = BuildConfig.VERSION_NAME;
            }
            stacktrace = "Hello User, \n \n Please forward the crash log to make Enlte better. \n "
                    + " \n Android Version Level: " + version
                    + " \n Android Version Number: " + versionRelease
                    + " \n Model: " + model
                    + " \n Manufacturer: " + manufacturer
                    + " \n App Version: " + appVersion
                    + " \n \n " + stacktrace
                    + "\n \n Regards \n Enlte team";
            sendToEmail(stacktrace);

            System.exit(0);
            defaultUEH.uncaughtException(t, e);
        }


        private void sendToEmail(String stacktrace) {
            final String subject = "Enlte: Crash Report";
            final String body = stacktrace;

            final Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.fromParts("mailto", "choudhary.niket21@gmail.com", null));
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            mInstance.startActivity(emailIntent);
        }
    }


}
