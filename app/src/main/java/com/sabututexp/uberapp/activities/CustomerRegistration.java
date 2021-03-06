package com.sabututexp.uberapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sabututexp.uberapp.R;

public class CustomerRegistration extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin;
    private TextView mRegistration;

    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthListener;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);

        initialiseView();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user!=null){
                    Intent intent = new Intent(CustomerRegistration.this,CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                final String email = mEmail.getText().toString();
                final  String password = mPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CustomerRegistration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(CustomerRegistration.this,"Sign Up error",Toast.LENGTH_SHORT).show();
                        }else {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_id = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_id);
                            current_user_id.setValue(true);
                        }
                    }
                });
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(buttonClick);
                final String email = mEmail.getText().toString();
                final  String password = mPassword.getText().toString();
                //check for valid email and password
                if (email.isEmpty() || password.isEmpty()) {
                    if (email.isEmpty()) {
                        Toast.makeText(CustomerRegistration.this,"Email can not be empty",Toast.LENGTH_SHORT).show();
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(CustomerRegistration.this,"Password can not be empty",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CustomerRegistration.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(CustomerRegistration.this,"Log In Fail",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void initialiseView(){
        mEmail = (EditText) findViewById(R.id.loginEmailEditText);
        mPassword = (EditText) findViewById(R.id.loginPasswordEditText);
        mLogin = (Button) findViewById(R.id.logINButton);
        mRegistration = (TextView) findViewById(R.id.registrationTextView);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public  void onStop(){
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}