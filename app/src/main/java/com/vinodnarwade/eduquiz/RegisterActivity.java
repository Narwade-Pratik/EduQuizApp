package com.vinodnarwade.eduquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhoneNo, etPwd, etConfirmPwd,
            etUserName, etClass, etParentEmail, etParentPhNo;

    Button etBtn;
    TextView tvAccount;
    RadioGroup rgRole;

    String roleIs;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // =========================
        // INITIALIZE VIEWS
        // =========================

        etName = findViewById(R.id.etregname);
        etUserName = findViewById(R.id.etregusername);
        etEmail = findViewById(R.id.etregemailid);
        etPhoneNo = findViewById(R.id.etregphno);

        etParentEmail = findViewById(R.id.etregparentemailid);
        etParentPhNo = findViewById(R.id.etregparentphno);

        etBtn = findViewById(R.id.btregregister);

        etPwd = findViewById(R.id.etregpwd);
        etConfirmPwd = findViewById(R.id.etregconfirmpwd);

        tvAccount = findViewById(R.id.tvregalreadyacc);

        rgRole = findViewById(R.id.rgregister);

        etClass = findViewById(R.id.etregclass);


        // =========================
        // HIDE STUDENT FIELDS
        // =========================

        etClass.setVisibility(View.GONE);
        etParentEmail.setVisibility(View.GONE);
        etParentPhNo.setVisibility(View.GONE);


        // =========================
        // ALREADY HAVE ACCOUNT
        // =========================

        tvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RegisterActivity.this,
                        LoginActivity.class
                );

                startActivity(intent);
                finish();
            }
        });


        // =========================
        // ROLE SELECTION
        // =========================

        rgRole.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(
                            RadioGroup group,
                            int checkedId) {

                        if (checkedId == -1) {
                            return;
                        }

                        RadioButton selectedRadioButton =
                                findViewById(checkedId);

                        String selectedRole =
                                selectedRadioButton
                                        .getText()
                                        .toString()
                                        .trim();


                        // =========================
                        // STUDENT
                        // =========================

                        if (selectedRole.equalsIgnoreCase("Student")) {

                            etClass.setVisibility(View.VISIBLE);
                            etParentEmail.setVisibility(View.VISIBLE);
                            etParentPhNo.setVisibility(View.VISIBLE);

                        }

                        // =========================
                        // OTHER ROLES
                        // =========================

                        else {

                            etClass.setVisibility(View.GONE);
                            etParentEmail.setVisibility(View.GONE);
                            etParentPhNo.setVisibility(View.GONE);

                            // Clear student-specific fields
                            etClass.setText("");
                            etParentEmail.setText("");
                            etParentPhNo.setText("");
                        }
                    }
                });


        // =========================
        // REGISTER BUTTON
        // =========================

        etBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // =========================
                // GET VALUES
                // =========================

                String name =
                        etName.getText()
                                .toString()
                                .trim();

                String userName =
                        etUserName.getText()
                                .toString()
                                .trim();

                String emailId =
                        etEmail.getText()
                                .toString()
                                .trim();

                String phNumber =
                        etPhoneNo.getText()
                                .toString()
                                .trim();

                String parentEmailId =
                        etParentEmail.getText()
                                .toString()
                                .trim();

                String parentPhNumber =
                        etParentPhNo.getText()
                                .toString()
                                .trim();

                String password =
                        etPwd.getText()
                                .toString()
                                .trim();

                String confirmPassword =
                        etConfirmPwd.getText()
                                .toString()
                                .trim();

                String classInput =
                        etClass.getText()
                                .toString()
                                .trim();


                // =========================
                // CHECK ROLE
                // =========================

                int radioButtonSelectedId =
                        rgRole.getCheckedRadioButtonId();

                if (radioButtonSelectedId == -1) {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Please select your Role.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                RadioButton radioButton =
                        findViewById(radioButtonSelectedId);

                roleIs =
                        radioButton
                                .getText()
                                .toString()
                                .trim();


                // =========================
                // NAME VALIDATION
                // =========================

                if (name.isEmpty()) {

                    etName.setError(
                            "Please enter your Name"
                    );

                    etName.requestFocus();
                    return;
                }

                if (name.length() < 6) {

                    etName.setError(
                            "Name must contain at least 6 characters"
                    );

                    etName.requestFocus();
                    return;
                }


                // =========================
                // USERNAME VALIDATION
                // =========================

                if (userName.isEmpty()) {

                    etUserName.setError(
                            "Create username"
                    );

                    etUserName.requestFocus();
                    return;
                }


                // =========================
                // EMAIL VALIDATION
                // =========================

                if (emailId.isEmpty()) {

                    etEmail.setError(
                            "Please enter your Email Id"
                    );

                    etEmail.requestFocus();
                    return;
                }

                if (!emailId.matches(
                        "[a-zA-Z0-9._%+-]+@gmail\\.com")) {

                    etEmail.setError(
                            "Enter valid Gmail Id"
                    );

                    etEmail.requestFocus();
                    return;
                }


                // =========================
                // PHONE VALIDATION
                // =========================

                if (phNumber.isEmpty()) {

                    etPhoneNo.setError(
                            "Please enter your Phone Number"
                    );

                    etPhoneNo.requestFocus();
                    return;
                }

                if (!phNumber.matches("\\d{10}")) {

                    etPhoneNo.setError(
                            "Phone number must contain 10 digits"
                    );

                    etPhoneNo.requestFocus();
                    return;
                }


                // =========================
                // STUDENT-ONLY VALIDATION
                // =========================

                if (roleIs.equalsIgnoreCase("Student")) {


                    // =========================
                    // CLASS
                    // =========================

                    if (classInput.isEmpty()) {

                        etClass.setError(
                                "Please enter your class"
                        );

                        etClass.requestFocus();
                        return;
                    }


                    // =========================
                    // PARENT EMAIL
                    // =========================

                    if (parentEmailId.isEmpty()) {

                        etParentEmail.setError(
                                "Please enter Parent Email Id"
                        );

                        etParentEmail.requestFocus();
                        return;
                    }

                    if (!parentEmailId.matches(
                            "[a-zA-Z0-9._%+-]+@gmail\\.com")) {

                        etParentEmail.setError(
                                "Enter valid Parent Gmail Id"
                        );

                        etParentEmail.requestFocus();
                        return;
                    }


                    // =========================
                    // PARENT PHONE
                    // =========================

                    if (parentPhNumber.isEmpty()) {

                        etParentPhNo.setError(
                                "Please enter Parent Phone Number"
                        );

                        etParentPhNo.requestFocus();
                        return;
                    }

                    if (!parentPhNumber.matches("\\d{10}")) {

                        etParentPhNo.setError(
                                "Parent phone number must contain 10 digits"
                        );

                        etParentPhNo.requestFocus();
                        return;
                    }
                }


                // =========================
                // PASSWORD VALIDATION
                // =========================

                if (password.isEmpty()) {

                    etPwd.setError(
                            "Create new Password"
                    );

                    etPwd.requestFocus();
                    return;
                }

                if (password.length() < 5) {

                    etPwd.setError(
                            "Password must contain at least 5 characters"
                    );

                    etPwd.requestFocus();
                    return;
                }


                // =========================
                // CONFIRM PASSWORD
                // =========================

                if (!confirmPassword.equals(password)) {

                    etConfirmPwd.setError(
                            "Password does not match"
                    );

                    etConfirmPwd.requestFocus();
                    return;
                }


                // =========================
                // CLASS VALUE
                // =========================

                String className = "";

                if (roleIs.equalsIgnoreCase("Student")) {

                    className = "Class: " + classInput;
                }


                // =========================
                // FIREBASE
                // =========================

                database =
                        FirebaseDatabase.getInstance();

                reference =
                        database.getReference("Users");


                String userId =
                        reference.push().getKey();


                if (userId == null) {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Unable to create user ID.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }


                // =========================
                // CREATE USER OBJECT
                // =========================

                HelperClass helperClass =
                        new HelperClass(
                                userId,
                                emailId,
                                parentEmailId,
                                name,
                                password,
                                phNumber,
                                parentPhNumber,
                                userName,
                                roleIs,
                                className
                        );


                // =========================
                // SAVE TO FIREBASE
                // =========================

                reference
                        .child(userId)
                        .setValue(helperClass)
                        .addOnSuccessListener(unused -> {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration Done.",
                                    Toast.LENGTH_SHORT
                            ).show();


                            Intent intent =
                                    new Intent(
                                            RegisterActivity.this,
                                            LoginActivity.class
                                    );

                            startActivity(intent);
                            finish();

                        })
                        .addOnFailureListener(e -> {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration failed: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            }
        });
    }


    // =========================
    // BACK BUTTON
    // =========================

    @Override
    public void onBackPressed() {

        Intent intent =
                new Intent(
                        RegisterActivity.this,
                        LoginActivity.class
                );

        startActivity(intent);
        finish();

        super.onBackPressed();
    }
}