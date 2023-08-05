package com.example.healthapp.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.util.concurrent.CompletableFuture;

public class HomeViewModel extends ViewModel {
    FirebaseUser user;
    String userName;
    String uID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<String> mText;


    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public interface OnDataRetrievedListener {
        void onDataRetrieved(UserClass data);
    }

    public LiveData<String> getText() {
        return mText;
    }

    /**
     *
     * OnDataRetrievedListener is needed together with getUserDataFromFireBase to make sure that the
     * getUserDataFromFireBase is completed before userName=name can be used
     * Because getUserDataFromFireBase is an asycn process
     */
    public void getUserDataFromFireBase(OnDataRetrievedListener listener){
        user=FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = db.collection("UserCollection").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
             String  name  = documentSnapshot.getString("name");
             System.out.println(user.getUid());
             System.out.println(name);
             uID=user.getUid();
             userName=name;
             UserClass userObj = new UserClass();
             userObj.setName(userName);
             userObj.setuId(uID);
             listener.onDataRetrieved(userObj);
            }

        });

    }

    /**
     *
     * this method will execute 1st because getUserDataFromFireBase is async method. This can be seen
     * from the HomeFragment Activity
     */
    public String getUserName(){
        if(userName==null){
            return "username empty...";
        }else {
            return userName;

        }
    }




}