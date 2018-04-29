package com.example.g.myfirstapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by G on 23-Apr-18.
 */

public class AsyncGettingBitmapFromUrl extends AsyncTask<String, Void, Bitmap> {

    private OnEventListener<Bitmap> mCallBack;
    public Exception mException;

    public AsyncGettingBitmapFromUrl( OnEventListener<Bitmap> callback) {
        mCallBack = callback;
    }


    @Override
    protected Bitmap doInBackground(String... params) {

        System.out.println("doInBackground");

        Bitmap bitmap = null;
        Log.d("Reviewsabiia", "doInBackground: "+params[0]);
        bitmap = getBitmapFromURL(params[0]);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(result);
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        HttpURLConnection connection=null;
        try {
            URL url = new URL(src);
            connection= (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.e("Error", "getBitmapFromURL: "+e.getMessage() );
            return null;
        }
        finally {
            if(connection!=null)
            {
                connection.disconnect();
            }
        }
    }
}