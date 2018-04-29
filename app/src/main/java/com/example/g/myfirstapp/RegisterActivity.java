package com.example.g.myfirstapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.g.myfirstapp.Classes.UserFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button btnSignUp;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
    //private DatabaseReference mDatabase;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        btnSignUp = (Button) findViewById(R.id.btn_register);
        inputEmail = (EditText) findViewById(R.id.editText_email);
        inputPassword = (EditText) findViewById(R.id.editText_password);
        inputPassword2 = (EditText) findViewById(R.id.editText_confPassword);
        btnSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String email = inputEmail.getText().toString().trim();
                        String password = inputPassword.getText().toString().trim();
                        String pass2= inputPassword2.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (password.length() < 6) {
                            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!password.equals(pass2)) {
                            Toast.makeText(getApplicationContext(), "The passwords don't match", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!email.contains("@")) {
                            Toast.makeText(getApplicationContext(), "Invalid email adress", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //progressBar.setVisibility(View.GONE);
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful())
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this, "Authentication failed." + message,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this, "Authentication Succesful:" , Toast.LENGTH_SHORT).show();
                                            user = auth.getCurrentUser();
                                            writeNewUser(email);

                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.putExtra(MainActivity.EXTRA_SESSION_ID, user.getUid());
                                            intent.putExtra(MainActivity.EXTRA_DISPLAY_NAME, user.getEmail());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                }

        );
    }

    public void haveAccount(View v){
        Intent intentCreat = new Intent(this,AccountActivity.class);
        startActivity(intentCreat);
        finish(); // call this to finish the current activity
    }

    private void writeNewUser( String email) {
        String name=email.split("@")[0];
        UserFireBase nUser = new UserFireBase(name, email,user.getUid(),user.getPhotoUrl(),0,0);
        db.collection("users").document(user.getUid()).set(nUser.getMap());
        //mDatabase.child("users").setValue(user);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("USERNameUpdateTag", "User profile updated.");
                        }
                    }
                });
    }
}
