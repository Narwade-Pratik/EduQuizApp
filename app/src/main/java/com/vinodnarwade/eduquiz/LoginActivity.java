package com.vinodnarwade.eduquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText etName,etPassword;
    Button btnLogin;
    CheckBox cbPass;
    TextView tvCreateNew;
    RadioGroup radioGroup;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean("islogin",false)){
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

        etName = findViewById(R.id.etloginname);
        etPassword = findViewById(R.id.etloginpwd);
        btnLogin = findViewById(R.id.btnlogin);
        cbPass = findViewById(R.id.cbloginshowhidepwd);
        tvCreateNew = findViewById(R.id.tvlogincreatenew);
        radioGroup = findViewById(R.id.rglogin);

        tvCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });



        cbPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioButtonSelectedId = radioGroup.getCheckedRadioButtonId();

                if(!validatePassword() | !validateUsername()){
                    return;
                }

                if (radioButtonSelectedId == -1) {
                    Toast.makeText(LoginActivity.this, "Please select your role", Toast.LENGTH_SHORT).show();
                } else {
                    checkUser();
                }
            }
        });

    }

    public boolean validateUsername(){
        String val = etName.getText().toString();
        if(val.isEmpty()){
            etName.setError("Please enter Username");
        } else{
            etName.setError(null);
            return true;
        }
        return false;
    }
    public boolean validatePassword(){
        String val = etPassword.getText().toString();
        if(val.isEmpty()){
            etPassword.setError("Please enter Password");
        } else{
            etPassword.setError(null);
            return true;
        }
        return false;
    }

    public void checkUser(){
        String userName = etName.getText().toString().trim();
        String userPassword = etPassword.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("userName").equalTo(userName);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String passwordFromDatabase = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordFromDatabase, userPassword)) {
                            int radioButtonSelectedId = radioGroup.getCheckedRadioButtonId();
                            RadioButton radioButton = findViewById(radioButtonSelectedId);
                            String userType = radioButton.getText().toString();

                            Toast.makeText(LoginActivity.this, "Login as ".concat(userType), Toast.LENGTH_SHORT).show();

                            editor.putBoolean("islogin", true).apply();
                            editor.putString("roleIs", userType).apply();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            etPassword.setError("Invalid password!!!");
                            etPassword.requestFocus();
                        }
                    }
                } else {
                    etName.setError("User doesn't exist...");
                    etName.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Firebase error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}