package com.example.g.myfirstapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.g.myfirstapp.Fragments.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction fragmentTransaction;

    private FirebaseAuth auth;
    private EditText inputEmail;
    private Button btnLogin;
    private EditText inputPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(AccountActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_account);
        inputEmail = (EditText) findViewById(R.id.editText_email);
        inputPassword = (EditText) findViewById(R.id.editText_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    public void create(View v){
        Intent intentCreat = new Intent(this,RegisterActivity.class);
        startActivity(intentCreat);
        finish(); // call this to finish the current activity
    }

    public void login(View v){
        fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        fragmentTransaction.replace(R.id.fragment_container,loginFragment,"login");
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:

                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AccountActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                //progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {

                                        Toast.makeText(AccountActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    FirebaseUser user = auth.getCurrentUser();
                                    Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                                    intent.putExtra(MainActivity.EXTRA_SESSION_ID, user.getUid());
                                    intent.putExtra(MainActivity.EXTRA_DISPLAY_NAME, user.getEmail());
                                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

                break;

        }
    }
}
