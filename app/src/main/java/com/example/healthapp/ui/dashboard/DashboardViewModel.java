package com.example.healthapp.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthapp.ui.home.HomeViewModel;
import com.example.healthapp.ui.home.UserClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    int userCollectionSize = 0;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public interface OnDataRetrievedListener {
        void onDataRetrieved(List<UserClass> data);
    }

    public void getUserDataFromFireBase(DashboardViewModel.OnDataRetrievedListener listener){

        user=FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference collectionRef = db.collection("UserData");
        Query query = collectionRef.whereEqualTo("uId", user.getUid());

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                // Iterate through the documents in the query result
                List<UserClass> userCollection = new ArrayList<>();
                if(userCollection.isEmpty() || userCollectionSize!=querySnapshot.getDocuments().size() ){
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Perform your logic here for each document
                        // You can access the document data using document.getData()
                        // For example, to get the uID field:
                        String name = document.getString("name");
                        String weight = document.getString("weight");
                        String date = document.getString("date");
                        String carbs = document.getString("carbs");
                        String fats = document.getString("fats");
                        String protein = document.getString("protein");
                        String target = document.getString("height");
                        String chest = document.getString("chest");
                        String back = document.getString("back");
                        String shoulders = document.getString("shoulders");
                        String legs = document.getString("legs");
                        UserClass userObj = new UserClass();
                        userObj.setName(name);
                        userObj.setDate(date);
                        userObj.setWeight(weight);
                        userObj.setCarbs(carbs);
                        userObj.setFats(fats);
                        userObj.setProtein(protein);
                        userObj.setHeight(target);
                        userObj.setChest(chest);
                        userObj.setLegs(legs);
                        userObj.setShoulders(shoulders);
                        userObj.setBack(back);
                        userCollection.add(userObj);


                        userCollectionSize=userCollection.size();
                        // Update your logic as needed
                    }
                }
                listener.onDataRetrieved(userCollection);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
            }
        });

    }


}