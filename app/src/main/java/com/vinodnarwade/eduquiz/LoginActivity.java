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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
                if(etName.getText().toString().isEmpty()){
                    etName.setError("Please enter Username");
                }
                else if(etName.getText().toString().length() < 8 ){
                    etName.setError("Username Must Contains 8 letters");
                }
                else if(etPassword.getText().toString().isEmpty()){
                    etName.setError("Please enter Password");
                }
                else if(etPassword.getText().toString().length() < 8){
                    etName.setError("Password Must Contains 8 letters");
                }
                else if(radioButtonSelectedId == -1){
                    Toast.makeText(LoginActivity.this,"Please select your role",Toast.LENGTH_SHORT).show();
                }
                else {
                    RadioButton radioButton = findViewById(radioButtonSelectedId);
                    String userType = radioButton.getText().toString();
                    Toast.makeText(LoginActivity.this,"Login as ".concat(userType),Toast.LENGTH_SHORT).show();
                    editor.putBoolean("islogin",true).commit();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}