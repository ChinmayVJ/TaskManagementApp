package com.example.user.officemanagementapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

public class RegistrationActivity extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText pass;
    TextView signinTxt;
    Button signinBtn;

    FirebaseAuth mAuth;

    ProgressDialog mDiaglog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );

        name = (EditText) findViewById( R.id.name_reg );
        email = (EditText) findViewById( R.id.email_reg );
        pass = (EditText) findViewById( R.id.password_reg );

        signinTxt = (TextView) findViewById( R.id.signin_text );
        signinBtn = (Button) findViewById( R.id.signinButton );

        mAuth = FirebaseAuth.getInstance();

        mDiaglog = new ProgressDialog( this );

        signinTxt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
            }
        } );

        signinBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String memail = email.getText().toString().trim();
                String mpass = pass.getText().toString().trim();

                if (TextUtils.isEmpty( memail ))
                {
                    email.setError( "* E-mail Required *" );
                    return;
                }
                if (TextUtils.isEmpty( mpass ))
                {
                    pass.setError( "* Password Required *" );
                    return;
                }

                mDiaglog.setMessage( "Processing .." );
                mDiaglog.show();

                mAuth.createUserWithEmailAndPassword( memail, mpass ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText( RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT ).show();
                            startActivity( new Intent( getApplicationContext(), HomeActivity.class ) );
                        }else{
                            Toast.makeText( RegistrationActivity.this, "Problem with Registration", Toast.LENGTH_SHORT ).show();
                        }
                        mDiaglog.dismiss();

                    }
                } );

            }
        } );

    }
}
