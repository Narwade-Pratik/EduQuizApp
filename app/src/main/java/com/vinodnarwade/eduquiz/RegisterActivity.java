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

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText etName,etEmail,etPhoneNo,etPwd,etConfirmPwd,etUserName;
    Button etBtn;
    TextView tvAccount;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = findViewById(R.id.etregname);
        etUserName = findViewById(R.id.etregusername);
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
                String name = etName.getText().toString().trim();
                String emailId = etEmail.getText().toString().trim();
                String phNumber = etPhoneNo.getText().toString().trim();
                String password = etPwd.getText().toString().trim();
                String userName = etUserName.getText().toString().trim();

                if(name.isEmpty()){
                    etName.setError("Please enter your Name");
                    return;
                }
                else if(name.length()<5){
                    etName.setError("Name must contain at least 6 letters");
                    return;
                }
                else if(emailId.isEmpty()){
                    etEmail.setError("Please enter your Email Id");
                    return;
                }
                else if(!emailId.contains("@gmail.com")){
                    etEmail.setError("Enter valid Email Id");
                    return;
                }
                else if(phNumber.isEmpty()){
                    etPhoneNo.setError("Please enter your Phone Number");
                    return;
                }
                else if(phNumber.length()!=10){
                    etPhoneNo.setError("Phone no must contain 10 letters");
                    return;
                }
                else if(password.isEmpty()){
                    etPwd.setError("Create new Password");
                    return;
                }
                else if(password.length()<5){
                    etPwd.setError("Password must have more than 5 numbers");
                    return;
                }
                else if(!etConfirmPwd.getText().toString().equals(etPwd.getText().toString())){
                    etConfirmPwd.setError("Enter correct created Password");
                    return;
                }
                else{
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");
                    HelperClass helperClass = new HelperClass(emailId,name,password,phNumber,userName);
                    reference.child(name).setValue(helperClass);
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