package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    protected Button btnsignUp;
    protected Button btnsignIn;

    //Firbase..

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_reg);
        password = findViewById(R.id.password_reg);

        btnsignUp = findViewById(R.id.btnsignup_reg);
        btnsignIn = findViewById(R.id.btnsignin_reg);

        //for registration
        btnsignIn.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        //for signup
        btnsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field..");
                    return;
                }
                if(TextUtils.isEmpty(mPass)){
                    password.setError("Require Field");
                    return;
                }



                mAuth.createUserWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                            Toast.makeText(getApplicationContext(),"Registration Complete",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_LONG).show();

                        }
                    }
                });
                //For clearing email and password field
                email.setText("");
                password.setText("");

            }
        });
    }
}
