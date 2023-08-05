package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.healthapp.databinding.ActivityMain2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    Button button, btnLogIn;
    String email, password;
    private ActivityMain2Binding binding;
    private FirebaseAuth mAuth;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    String name, role, age, height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
//        setContentView(R.layout.activity_main2);
        setContentView(binding.getRoot());
        button=binding.buttonReg;
        btnLogIn=binding.buttonLogin;
        FirebaseApp.initializeApp(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                try{
                    /**
                     * user registration
                     */
                    email = binding.editTextTextPersonName.getText().toString();
                    password = binding.editTextTextPassword.getText().toString();
                    name=binding.editTextTextPersonName2.getText().toString();
                    role=binding.editTextTextPersonName4.getText().toString();
                    age=binding.editTextTextPersonName5.getText().toString();
                    height=binding.editTextTextPersonName6.getText().toString();

                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity2.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(MainActivity2.this, "Registration OK!", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
//
                                        /**
                                         * saving user data to firestore db
                                         */
                                        Map<String, String> userObject = new HashMap<>();
                                        userObject.put("name", name);
                                        userObject.put("role", role);
                                        userObject.put("height", height);
                                        userObject.put("age", age);
                                        userObject.put("email", email);
                                        userObject.put("password", password);
//                                      db=FirebaseFirestore.getInstance();
                                        db.collection("UserCollection").document(email).set(userObject)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(MainActivity2.this, "Creating user in Firebase OK!", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity2.this, "Creating user in Firebase NOT OK!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        /**
                                         * By moving the Firestore code inside the onComplete callback, you ensure that the user is created and authenticated before attempting to access Firestore. This approach guarantees that the user has the necessary permissions to write data to the Firestore database.
                                         */
//                            updateUI(user);
                                        startActivity(new Intent(MainActivity2.this, MainActivity.class));
                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(MainActivity2.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                                    }
                                }


                            });



                }catch (Exception e){
                    Toast.makeText(MainActivity2.this, "error", Toast.LENGTH_SHORT).show();
                }
                

            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * log in
                 */
                try{
                    email = binding.editTextTextPersonName.getText().toString();
                    password = binding.editTextTextPassword.getText().toString();
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity2.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity2.this, "Log in OK!", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(MainActivity2.this, MainActivity.class));
                                    }else{
                                        Toast.makeText(MainActivity2.this, "Log in failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }catch (Exception e){
                    Toast.makeText(MainActivity2.this, "Log in Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            currentUser.reload();
//            Toast.makeText(this, "user not signed in", Toast.LENGTH_SHORT).show();
//        }
//    }
}