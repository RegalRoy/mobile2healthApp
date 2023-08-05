package com.example.healthapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.healthapp.MainActivity2;
import com.example.healthapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    String height, weight, userName, fats, carbs, protein, chest, shoulder, back, legs;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button buttonLog;
    FirebaseUser user;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        TextView textView = binding.textHome;
        TextView txtView2 = binding.textHome2;
        UserClass userObj = new UserClass();
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        //this method will execute 2nd (async)
        homeViewModel.getUserDataFromFireBase(new HomeViewModel.OnDataRetrievedListener() {
            @Override
            public void onDataRetrieved(UserClass data) {
                txtView2.setText("Welcome user " + data.getName() + "\n");

                userName = data.getName().toString();

                userObj.setuId(data.getuId());
                userObj.setName(userName);


            }
        });

        buttonLog = binding.buttonReg;
        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    height = binding.editTxtHeight.getText().toString();
                    weight = binding.editTxtWeight.getText().toString();
                    carbs = binding.editTxtCarbs.getText().toString();
                    protein = binding.editTxtProtein.getText().toString();
                    fats = binding.editTextFats.getText().toString();
                    chest = binding.editTextChest.getText().toString();
                    shoulder = binding.editTextShoulder.getText().toString();
                    back = binding.editTextBack.getText().toString();
                    legs = binding.editTextLegs.getText().toString();
                    if (height.isEmpty() || weight.isEmpty() || carbs.isEmpty() || protein.isEmpty() || fats.isEmpty() || chest.isEmpty() || shoulder.isEmpty() || back.isEmpty() || legs.isEmpty()) {
                        Toast.makeText(getActivity(), "Fields should not be blank", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Fields should not be blank", Toast.LENGTH_SHORT).show();
                }


                userObj.setHeight(height);
                userObj.setWeight(weight);
                userObj.setCarbs(carbs);
                userObj.setFats(fats);
                userObj.setProtein(protein);
                userObj.setChest(chest);
                userObj.setBack(back);
                userObj.setShoulders(shoulder);
                userObj.setLegs(legs);
                Map<String, Object> updates = new HashMap<>();
//                updates.put("height", FieldValue.arrayUnion(height));
                DateObjClass dateObjClass = new DateObjClass();
                dateObjClass.setDate(new Date().toString());
                dateObjClass.setWeight(weight);
                updates.put("weight", dateObjClass);
                userObj.setDate(dateObjClass.getDate());

                DocumentReference docRef = db.collection("UserData").document(userObj.getuId());
                docRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Document exists
                                    // Perform your logic here if the document exists
                                } else {
                                    // Document does not exist
                                    // Perform your logic here if the document does not exist
//                                    db.collection("UserData").document(userObj.getuId()).set(userObj);
                                }
                            }
                        });
//                db.collection("UserData").document(userObj.getuId()).set(userObj);

                db.collection("UserData").add(userObj);
//            //SnackBar here
                Snackbar.make(view, "Entry Added!", Snackbar.LENGTH_LONG).setAction("Undo?", undoEntries).show();
            }
        });
        //this method will execute 1st
        txtView2.setText(homeViewModel.getUserName());

        //getting info form the home page (height and weight)

        //saving to firebase
        return root;
    }

    View.OnClickListener undoEntries = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
//            deleteLatEntry();
        }
    };

    private void deleteLatEntry() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference collectionRef = db.collection("UserData"); // Replace "your_collection" with the actual collection name
        Query query = collectionRef.whereEqualTo("uId", user.getUid());
        query.orderBy("date", Query.Direction.DESCENDING).limit(1);

//        Query query = collectionRef.whereEqualTo("uId", user.getUid())
//                .orderBy("date", Query.Direction.DESCENDING)
//                .limit(1);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {


            for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                doc.get("date");
                String docId=doc.getId();
                System.out.println(doc.get("date"));

                collectionRef.document(docId).delete().addOnSuccessListener(aVoid ->{

                }).addOnFailureListener(e->{

                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}