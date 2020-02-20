package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    protected Button btnSignIn;
    protected Button btnSignUp;
    protected TextView txtforgetpass;

    //Firebase..

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!= null){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }


        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);


        btnSignIn = findViewById(R.id.btnsignin);
        btnSignUp = findViewById(R.id.btnsingup);
        txtforgetpass = findViewById(R.id.txtforgetpass);

        //for complete login
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)){
                    email.setError("Required Field..");
                    return;
                }
                if(TextUtils.isEmpty(mPass)){
                    password.setError("Required Field..");
                    return;
                }


                mAuth.signInWithEmailAndPassword(mEmail,mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                             Toast.makeText(getApplicationContext(),"Login Complete",Toast.LENGTH_LONG).show();

                         }else{
                             Toast.makeText(getApplicationContext(),"Wrong email address or password",Toast.LENGTH_LONG).show();

                         }
                    }
                });


                email.setText("");
                password.setText("");
            }

        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });

        txtforgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgetPassword.class));
            }
        });

    }
}
