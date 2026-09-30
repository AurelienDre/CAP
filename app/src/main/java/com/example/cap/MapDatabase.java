package com.example.cap;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MapDatabase {

    private final RequestQueue queue;
    private final String url;
    public static final String TAG = "MapDatabase";

    public MapDatabase(Context context, String URL){
        this.queue = Volley.newRequestQueue(context);
        this.url = URL;
    }

    public interface ResponseListener<T> {
        void onResponse(T response);
    }

    public void getDivingList(ResponseListener<ArrayList<String>> listener){
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET, url,null,
                response -> {
                    try {
                        ArrayList<String> divingList = new ArrayList<>();
                        JSONArray array = response.getJSONArray("message");
                        for (int i = 0; i < array.length(); i++) {
                            divingList.add(array.getString(i));
                        }
                        listener.onResponse(divingList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                (Response.ErrorListener) error -> {}
        );

        jsonRequest.setTag(TAG);
        this.queue.add(jsonRequest);
    }

    public void stopConnection(){
        if (this.queue != null) {
            this.queue.cancelAll(TAG);
        }
    }
}