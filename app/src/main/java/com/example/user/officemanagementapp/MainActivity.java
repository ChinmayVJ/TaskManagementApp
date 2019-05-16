package com.example.user.officemanagementapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView logintext;
    EditText email;
    EditText pass;

    Button loginButton;

    FirebaseAuth mAuth;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        logintext = (TextView) findViewById( R.id.login_text );
        email = (EditText) findViewById( R.id.email );
        pass = (EditText) findViewById( R.id.password );
        loginButton = (Button) findViewById( R.id.loginButton );

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null)
        {
            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
        }

        mDialog = new ProgressDialog( this );

        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService( Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String memail = email.getText().toString().trim();
                String mpass = pass.getText().toString().trim();

                if (TextUtils.isEmpty( memail )){
                    email.setError( "* E-Mail Required *" );
                }
                if (TextUtils.isEmpty( mpass ))
                {
                    pass.setError( "* Password Required *" );
                }

                mDialog.setMessage( "Logging In ..." );
                mDialog.show();

                mAuth.signInWithEmailAndPassword( memail, mpass ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText( MainActivity.this, "Login Successful.", Toast.LENGTH_SHORT ).show();
                            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
                        }else{
                            Toast.makeText( MainActivity.this, "Incorrect E-Mail or Password", Toast.LENGTH_SHORT ).show();
                        }
                        mDialog.dismiss();
                    }
                } );

            }
        } );

        logintext.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent( getApplicationContext(), RegistrationActivity.class ) );
            }
        } );

    }
}
