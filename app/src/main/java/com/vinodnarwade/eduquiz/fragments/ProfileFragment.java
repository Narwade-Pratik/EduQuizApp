package com.vinodnarwade.eduquiz.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.HelperClass;
import com.vinodnarwade.eduquiz.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView ivUserProfilePicture;
    EditText etName, etUsername, etEmail, etPhone, etId, etRole, etPassword;
    EditText etClass, etParentEmail, etParentPhone;
    View llStudentSection;
    ProgressBar progressBar;
    AppCompatButton btnUpdate;

    private static final int GalleryPic = 1;
    private static final int MAX_IMAGE_DIMENSION = 300; // px

    private String userId;
    private String currentRole;

    // Holds a NEWLY picked image (base64) pending save. Null = no change.
    private String pendingProfileImageBase64 = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivUserProfilePicture = view.findViewById(R.id.civ_profile_fragment_image);

        etName = view.findViewById(R.id.et_profile_fragment_name);
        etUsername = view.findViewById(R.id.et_profile_fragment_username);
        etEmail = view.findViewById(R.id.et_profile_fragment_email);
        etPhone = view.findViewById(R.id.et_profile_fragment_phone);
        etId = view.findViewById(R.id.et_profile_fragment_id);
        etRole = view.findViewById(R.id.et_profile_fragment_role);
        etPassword = view.findViewById(R.id.et_profile_fragment_password);

        llStudentSection = view.findViewById(R.id.llProfileStudentSection);
        etClass = view.findViewById(R.id.et_profile_fragment_class);
        etParentEmail = view.findViewById(R.id.et_profile_fragment_parent_email);
        etParentPhone = view.findViewById(R.id.et_profile_fragment_parent_phone);

        progressBar = view.findViewById(R.id.progressBarProfile);
        btnUpdate = view.findViewById(R.id.btnupdatedata);

        ivUserProfilePicture.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GalleryPic);
        });

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(requireContext());

        userId = sharedPreferences.getString("userId", "");

        loadProfile();

        btnUpdate.setOnClickListener(v -> updateProfile());

        return view;
    }

    // =========================================================
    // IMAGE PICK RESULT
    // =========================================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPic
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            Uri selectedImageUri = data.getData();

            try {

                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(),
                        selectedImageUri
                );

                Bitmap resizedBitmap = resizeBitmap(originalBitmap, MAX_IMAGE_DIMENSION);

                ivUserProfilePicture.setImageBitmap(resizedBitmap);

                pendingProfileImageBase64 = bitmapToBase64(resizedBitmap);

                Toast.makeText(
                        getContext(),
                        "Photo selected. Tap 'Update Profile' to save.",
                        Toast.LENGTH_SHORT
                ).show();

            } catch (IOException e) {

                Toast.makeText(getContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap original, int maxDimension) {

        int width = original.getWidth();
        int height = original.getHeight();

        if (width <= maxDimension && height <= maxDimension) {
            return original;
        }

        float ratio = Math.min(
                (float) maxDimension / width,
                (float) maxDimension / height
        );

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

        byte[] bytes = stream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // =========================================================
    // LOAD PROFILE
    // =========================================================

    private void loadProfile() {

        if (userId == null || userId.isEmpty()) {

            Toast.makeText(getContext(), "User not found. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                progressBar.setVisibility(View.GONE);

                if (!snapshot.exists()) {
                    Toast.makeText(getContext(), "Profile not found.", Toast.LENGTH_LONG).show();
                    return;
                }

                HelperClass user;

                try {
                    // Preferred path: automatic mapping into the model class.
                    user = snapshot.getValue(HelperClass.class);

                } catch (DatabaseException e) {
                    user = buildHelperClassManually(snapshot);
                }

                if (user == null) {
                    Toast.makeText(getContext(), "Failed to parse profile.", Toast.LENGTH_LONG).show();
                    return;
                }

                bindProfile(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Manually builds a HelperClass instance by reading each child node
     * individually and converting it to a String regardless of whether it
     * was stored in Firebase as a String or a Number. This avoids relying
     * on Firebase's strict automatic type mapping (getValue(Class)), which
     * throws DatabaseException on any type mismatch.
     */
    private HelperClass buildHelperClassManually(DataSnapshot snapshot) {

        HelperClass user = new HelperClass();

        user.setName(readAsString(snapshot, "name"));
        user.setUserName(readAsString(snapshot, "userName"));
        user.setEmailId(readAsString(snapshot, "emailId"));
        user.setPhoneNumber(readAsString(snapshot, "phoneNumber"));
        user.setUserId(readAsString(snapshot, "userId"));
        user.setRoleIs(readAsString(snapshot, "roleIs"));
        user.setPassword(readAsString(snapshot, "password"));

        user.setClassName(readAsString(snapshot, "className"));
        user.setParentEmailId(readAsString(snapshot, "parentEmailId"));
        user.setParentPhoneNumber(readAsString(snapshot, "parentPhoneNumber"));

        user.setProfileImageBase64(readAsString(snapshot, "profileImageBase64"));

        return user;
    }

    /**
     * Reads a child value as a String no matter its underlying type in
     * Firebase (String, Long, Double, Boolean, etc.). Returns null if the
     * child doesn't exist.
     */
    private String readAsString(DataSnapshot snapshot, String childKey) {

        DataSnapshot child = snapshot.child(childKey);

        if (!child.exists()) {
            return null;
        }

        Object value = child.getValue();

        if (value == null) {
            return null;
        }

        // Covers String, Long, Double, Boolean, etc.
        return String.valueOf(value);
    }

    private void bindProfile(HelperClass user) {

        currentRole = user.getRoleIs();

        etName.setText(user.getName());
        etUsername.setText(user.getUserName());
        etEmail.setText(user.getEmailId());
        etPhone.setText(user.getPhoneNumber());
        etId.setText(user.getUserId());
        etRole.setText(user.getRoleIs());
        etPassword.setText(user.getPassword());

        boolean isStudent =
                currentRole != null && currentRole.trim().equalsIgnoreCase("Student");

        if (isStudent) {

            llStudentSection.setVisibility(View.VISIBLE);

            etClass.setText(user.getClassName());
            etParentEmail.setText(user.getParentEmailId());
            etParentPhone.setText(user.getParentPhoneNumber());

        } else {

            llStudentSection.setVisibility(View.GONE);
        }

        // Load saved profile picture, if any
        String savedImageBase64 = user.getProfileImageBase64();

        if (savedImageBase64 != null && !savedImageBase64.trim().isEmpty()) {

            try {

                byte[] decodedBytes = Base64.decode(savedImageBase64, Base64.DEFAULT);

                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        decodedBytes, 0, decodedBytes.length
                );

                ivUserProfilePicture.setImageBitmap(bitmap);

            } catch (Exception e) {

                // Corrupt/invalid stored image — keep default placeholder.
            }
        }
    }

    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    private void updateProfile() {

        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name cannot be empty");
            etName.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("Username cannot be empty");
            etUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email cannot be empty");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty() || !phone.matches("\\d{10}")) {
            etPhone.setError("Enter a valid 10-digit phone number");
            etPhone.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 5) {
            etPassword.setError("Password must be at least 5 characters");
            etPassword.requestFocus();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("userName", username);
        updates.put("emailId", email);
        updates.put("phoneNumber", phone);
        // Always write password as a String so it can never be re-saved
        // as a number and cause this same crash again.
        updates.put("password", password);

        boolean isStudent =
                currentRole != null && currentRole.trim().equalsIgnoreCase("Student");

        if (isStudent) {

            String className = etClass.getText().toString().trim();
            String parentEmail = etParentEmail.getText().toString().trim();
            String parentPhone = etParentPhone.getText().toString().trim();

            if (className.isEmpty()) {
                etClass.setError("Class cannot be empty");
                etClass.requestFocus();
                return;
            }

            if (parentEmail.isEmpty()) {
                etParentEmail.setError("Parent email cannot be empty");
                etParentEmail.requestFocus();
                return;
            }

            if (parentPhone.isEmpty() || !parentPhone.matches("\\d{10}")) {
                etParentPhone.setError("Enter a valid 10-digit parent phone number");
                etParentPhone.requestFocus();
                return;
            }

            updates.put("className", className);
            updates.put("parentEmailId", parentEmail);
            updates.put("parentPhoneNumber", parentPhone);
        }

        // Include the new photo only if the user picked one this session
        if (pendingProfileImageBase64 != null) {
            updates.put("profileImageBase64", pendingProfileImageBase64);
        }

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId);

        ref.updateChildren(updates)
                .addOnSuccessListener(unused -> {

                    progressBar.setVisibility(View.GONE);
                    pendingProfileImageBase64 = null;

                    Toast.makeText(getContext(), "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}