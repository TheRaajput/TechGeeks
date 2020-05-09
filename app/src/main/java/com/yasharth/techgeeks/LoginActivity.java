package com.yasharth.techgeeks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private EditText LoginEmailText;
    private EditText LoginPassText;
    private Button LoginBtn;
    private Button LoginRegBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        LoginBtn = (Button)findViewById(R.id.login_but);
        LoginRegBtn = (Button)findViewById(R.id.login_reg);
        LoginEmailText = (EditText)findViewById(R.id.login_email);
        LoginPassText = (EditText)findViewById(R.id.reg_pass);
        progressBar = (ProgressBar)findViewById(R.id.login_progress);


        LoginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intoReg = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intoReg);
                finish();

            }
        });


        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String loginEmail = LoginEmailText.getText().toString();
                String loginPass = LoginPassText.getText().toString();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                sendTomain();

                            }else {

                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error:"+error,Toast.LENGTH_LONG).show();

                            }

                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }
                else{
                    Toast.makeText(LoginActivity.this,"Please Enter ID and Password ", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){

            sendTomain();

        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void sendTomain() {

        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
