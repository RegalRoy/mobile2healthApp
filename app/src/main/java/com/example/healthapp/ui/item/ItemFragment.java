package com.example.healthapp.ui.item;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.healthapp.MainActivity;
import com.example.healthapp.MainActivity3;
import com.example.healthapp.R;
import com.example.healthapp.databinding.FragmentHomeBinding;
import com.example.healthapp.databinding.FragmentItemBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFragment extends Fragment {
    RequestQueue requestQueue;
    String exerciseType="";
    String exerciceMuscle="";
    String exerciseDifficulty="";
    RecyclerView recyclerView;
    List<ExerciseClass> myList = new ArrayList<>();


//    private FragmentHomeBinding binding;
    private FragmentItemBinding binding;
    private static final String API_KEY = "KFQ1UYfZp5gafPmXHcroDg==pg0zUyIyDjz7VCb0";
    private static final String API_URL = "https://api.api-ninjas.com/v1/aircraft?manufacturer=Gulfstream&model=G550";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ItemViewModel homeViewModel =
                new ViewModelProvider(this).get(ItemViewModel.class);

//        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding = FragmentItemBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textItem;
        requestQueue = VolleySingleten.getInstance(getContext()).getRequestQueue();


        binding.exerciseTypeID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                exerciseType=binding.exerciseTypeID.getSelectedItem().toString();
                checkFieldsAndRun();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.exerciseMuscleID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                exerciceMuscle=binding.exerciseMuscleID.getSelectedItem().toString();
                checkFieldsAndRun();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.exerciseDiffID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                exerciseDifficulty=binding.exerciseDiffID.getSelectedItem().toString();
                checkFieldsAndRun();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        checkFieldsAndRun();

//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        binding.recyclerViewId.setLayoutManager(lm);
        return root;
    }

    private void checkFieldsAndRun() {
        if(!exerciseType.isEmpty() || !exerciceMuscle.isEmpty() || !exerciseDifficulty.isEmpty()){
            APICallTask();
            System.out.println("Type Selected " + exerciseType);
            System.out.println("Muscle Selected " + exerciceMuscle);
            System.out.println("Diff Selected " + exerciseDifficulty);
        }else{
            Snackbar mySnackbar = Snackbar.make(binding.getRoot(), " ENTER CHOICES, and click the CARD for the demo! ",  Snackbar.LENGTH_SHORT);
            mySnackbar.show();


        }
    }


    private void APICallTask() {
//        String url = "https://api.api-ninjas.com/v1/aircraft?manufacturer=Gulfstream&model=G550";

//        String url = String.format("https://api.api-ninjas.com/v1/exercises?muscle=%s?type=%s?difficulty=%s", exerciceMuscle, exerciseType, exerciseDifficulty) ;
        String url = String.format("https://api.api-ninjas.com/v1/exercises?muscle=%s&type=%s&difficulty=%s", exerciceMuscle, exerciseType, exerciseDifficulty) ;



        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            myList.clear();
                            for(int i = 0 ; i < response.length() ; i++){
                                JSONObject jsonObject = response.getJSONObject(i);
//                                System.out.println(jsonObject);
                                ExerciseClass exerciseClass = new ExerciseClass(
                                        jsonObject.getString("name"),
                                        jsonObject.getString("type"),
                                        jsonObject.getString("muscle"),
                                        jsonObject.getString("equipment"),
                                        jsonObject.getString("difficulty")
                                );
                                myList.add(exerciseClass);
                            }



                            RecyclerViewAdapterCustom adapterCustom = new RecyclerViewAdapterCustom(myList, new RecyclerViewAdapterCustom._OnClickListener() {
                                @Override
                                public void _OnClick(int i) {
                                    myList.get(i).getName();
                                    Intent intent = new Intent(getContext(), MainActivity3.class);
                                    intent.putExtra("exercise",  myList.get(i).getName());
                                    startActivity(intent);
//                                    startActivity(new Intent(getContext(), MainActivity3.class));
                                }
                            } ,getContext());

                            binding.recyclerViewId.setAdapter(adapterCustom);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
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
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Include custom headers
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Api-Key", "KFQ1UYfZp5gafPmXHcroDg==pg0zUyIyDjz7VCb0");
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}