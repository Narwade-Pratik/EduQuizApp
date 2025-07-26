package com.vinodnarwade.eduquiz.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.vinodnarwade.eduquiz.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView ivUserProfilePicture;
    EditText etUserName, etUserPassword, etUserRole, etUserID;
    private static final int GalleryPic = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        ivUserProfilePicture = view.findViewById(R.id.civ_profile_fragment_image);
        etUserName = view.findViewById(R.id.et_profile_fragment_name);
        etUserPassword = view.findViewById(R.id.et_profile_fragment_password);
        etUserRole = view.findViewById(R.id.et_profile_fragment_role);
        etUserID = view.findViewById(R.id.et_profile_fragment_id);

        // ImageView click to open gallery
        ivUserProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPic);
            }
        });

        return view;
    }
}
