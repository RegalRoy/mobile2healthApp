package com.example.healthapp.ui.item;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleten {

    private RequestQueue requestQueue;
    private static  VolleySingleten instance;

    private VolleySingleten(Context context){
        requestQueue = Volley.newRequestQueue((context.getApplicationContext()));
    }

    public static synchronized VolleySingleten getInstance(Context context){
        if(instance==null){
            instance = new VolleySingleten(context);
        }
        return  instance;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
