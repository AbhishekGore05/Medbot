package com.example.mentalhealthapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

public class LoginActivity extends AppCompatActivity {

    EditText Email_editText, Password_editText;

    TextView SignUp, ForgotPass, buttonText;

    private FirebaseAuth mAuth;

    android.widget.ProgressBar ProgressBar;

    boolean passwordVisible;

    View LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialization();     //Function to initialize the variables

        ForgotPass.setOnClickListener(view -> intentNow(ResetPasswordActivity.class)); //To Forgot Password (Reset Password Activity)

        LoginButton.setOnClickListener(view -> {
            if (isConnected()) Login();
        });  //Login Button CTA

        SignUp.setOnClickListener(view -> intentNow(RegisterActivity.class)); //To SignUp Activity

        // Function to see password and hide password
        Password_editText.setOnTouchListener((v, event) -> {
            final int Right = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= Password_editText.getRight() - Password_editText.getCompoundDrawables()[Right].getBounds().width()) {
                    int selection = Password_editText.getSelectionEnd();
                    //Handles Multiple option popups
                    if (passwordVisible) {
                        //set drawable image here
                        Password_editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                        //for hide password
                        Password_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        //set drawable image here
                        Password_editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                        //for show password
                        Password_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    Password_editText.setLongClickable(false); //Handles Multiple option popups
                    Password_editText.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

    }

    //Handles intent -> Individual intent to one single method
    private void intentNow(Class targetActivity) {
        Intent intent = new Intent(getApplicationContext(), targetActivity);
        startActivity(intent);
    }

    private void Login() {
        String email = Email_editText.getText().toString().trim();      //EditText -> String
        String pass = Password_editText.getText().toString().trim();    //EditText -> String

        //Checking entered text if it is valid or not
        if (email.isEmpty()) {
            Email_editText.setError("Field can't be empty");
            Email_editText.requestFocus();
            return;
        } else if (pass.isEmpty()) {
            Password_editText.setError("Field can't be empty");
            Password_editText.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email_editText.setError("Please enter a valid email address");
            Email_editText.requestFocus();
            return;
        } else if (pass.length() < 8) {
            Password_editText.setError("Password must be at least 8 characters");
            Password_editText.requestFocus();
            return;
        }

        ProgressBar.setVisibility(View.VISIBLE);
        buttonText.setVisibility(View.GONE);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;

                ProgressBar.setVisibility(View.GONE);
                buttonText.setVisibility(View.VISIBLE);
                Intent intent2 = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(intent2);
                finishAffinity();


            } else {
                //If the Login Fails
                ProgressBar.setVisibility(View.GONE);
                buttonText.setVisibility(View.VISIBLE);
                DynamicToast.makeError(this, "Failed to Login! Please check your credentials").show();
            }
        });
    }

    //To check Internet Connectivity
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;

        DynamicToast.makeError(getApplicationContext(), "You're not connected to Internet!").show();
        return false;
    }

    //Function to initialize the variables
    private void Initialization() {
        Email_editText = findViewById(R.id.email_box);
        Password_editText = findViewById(R.id.pass_box);
        SignUp = findViewById(R.id.SignUp_tv);
        mAuth = FirebaseAuth.getInstance();
        ForgotPass = findViewById(R.id.forgotpass_tv);
        LoginButton = findViewById(R.id.login_button);
        ProgressBar = findViewById(R.id.buttonProgress);
        buttonText = findViewById(R.id.buttonText);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {

        if (currentUser != null) {
            // User is signed in
            Intent i = new Intent(LoginActivity.this, HomeScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out

        }

    }

}