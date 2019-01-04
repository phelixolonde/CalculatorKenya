package com.automata.calculatorkenya;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static android.content.ContentValues.TAG;

/**
 * Created by HANSEN on 10/07/2018.
 */

public class CalculatorKenya extends Application {
    RequestQueue mRequestQueue;

    private static CalculatorKenya mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;




    }



    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static synchronized CalculatorKenya getInstance() {
        return mInstance;
    }
}
