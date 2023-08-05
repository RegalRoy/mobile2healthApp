package com.example.healthapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.healthapp.databinding.FragmentNotificationsBinding;
import com.example.healthapp.ui.item.VolleySingleten;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NotificationsFragment extends Fragment {
    String query;
    RequestQueue requestQueue;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        requestQueue = VolleySingleten.getInstance(getContext()).getRequestQueue();
        binding.floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query = binding.foodinputid.getText().toString();
                String url = String.format("https://api.api-ninjas.com/v1/nutrition?query=%s", query);
                String urlImage = String.format("https://api.pexels.com/v1/search/?query=%s", query);
                ApiCallTask(url);
                ApiCallTaskImage(urlImage);
//                System.out.println(url);
//                Toast.makeText(getContext(), "btn clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    private void ApiCallTaskImage(String urlImage) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlImage, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("photos");
//
                    JSONObject jsonObject =   jsonArray.getJSONObject(0);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("src");
                    String imgUrl = jsonObject1.getString("medium");
                    Glide.with(getContext()).load(imgUrl).into(binding.foodimg);
//                    System.out.println(jsonArray1Vid);
                } catch (JSONException e) {
                    Snackbar mySnackbar = Snackbar.make(binding.getRoot(), " Food not recognized from IMG ",  Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }

//                    Glide.with(getContext()).load(response.getString("photos[0].src.medium")).into(binding.foodimg);
            }
        }, null){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "q6i0TgzMXmXn2g9oyA5y7zZcbC2GYb0LYNtC9FehP9gd9pTQzd0GVjFe");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void ApiCallTask(String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
//                    System.out.println(jsonObject);
                    binding.caloriesVal.setText(jsonObject.getString("calories"));
                    binding.servingSizeGVal.setText(jsonObject.getString("serving_size_g"));
                    binding.fatSaturatedGVal.setText(jsonObject.getString("fat_saturated_g"));
                    binding.fatTotalGVal.setText(jsonObject.getString("fat_total_g"));
                    binding.proteinGVal.setText(jsonObject.getString("protein_g"));
                    binding.sodiumMgVal.setText(jsonObject.getString("sodium_mg"));
                    binding.potassiumMgVal.setText(jsonObject.getString("potassium_mg"));
                    binding.cholesterolMgVal.setText(jsonObject.getString("cholesterol_mg"));
                    binding.carbohydratesTotalGVal.setText(jsonObject.getString("carbohydrates_total_g"));
                    binding.fiberGVal.setText(jsonObject.getString("fiber_g"));
                    binding.sugarGVal.setText(jsonObject.getString("sugar_g"));
                } catch (JSONException e) {
                    Snackbar mySnackbar = Snackbar.make(binding.getRoot(), " Food not rcognized from Food ",  Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                String errorMessage2 = error.getMessage();
                if (error.networkResponse != null) {
                    String errorMessage = new String(error.networkResponse.data);
                    System.out.println(errorMessage);
                } else {
                    System.out.println("Unknown error occurred");
                }
                if (errorMessage2 != null) {
                    System.out.println("Error Message: " + errorMessage2);
                } else {
                    System.out.println("Unknown error occurred");
                }


            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Api-Key", "KFQ1UYfZp5gafPmXHcroDg==pg0zUyIyDjz7VCb0");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonArrayRequest);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}