package com.vinodnarwade.eduquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText etName,etEmail,etPhoneNo,etPwd,etConfirmPwd;
    Button etBtn;
    TextView tvAccount;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.etregname);
        etEmail = findViewById(R.id.etregemailid);
        etPhoneNo = findViewById(R.id.etregphno);
        etBtn = findViewById(R.id.btregregister);
        etPwd = findViewById(R.id.etregpwd);
        etConfirmPwd = findViewById(R.id.etregconfirmpwd);
        tvAccount = findViewById(R.id.tvregalreadyacc);

        tvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        etBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().isEmpty()){
                    etName.setError("Please enter your Name");
                }
                else if(etName.getText().toString().length()<7){
                    etName.setError("Name must contain at least 8 letters");
                }
                else if(etEmail.getText().toString().isEmpty()){
                    etEmail.setError("Please enter your Email Id");
                }
                else if(!etEmail.getText().toString().contains("@gmail.com")){
                    etEmail.setError("Enter valid Email Id");
                }
                else if(etPhoneNo.getText().toString().isEmpty()){
                    etPhoneNo.setError("Please enter your Phone Number");
                }
                else if(etPhoneNo.getText().toString().length()<9){
                    etPhoneNo.setError("Phone no must contain 10 letters");
                }
                else if(etPwd.getText().toString().isEmpty()){
                    etPwd.setError("Create new Password");
                }
                else if(etPwd.getText().toString().length()<5){
                    etPwd.setError("Password must have more than 5 numbers");
                }
                else if(!etConfirmPwd.getText().toString().equals(etPwd.getText().toString())){
                    etConfirmPwd.setError("Enter correct created Password");
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Registration Done.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}