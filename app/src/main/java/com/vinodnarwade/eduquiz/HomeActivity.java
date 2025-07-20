package com.vinodnarwade.eduquiz;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vinodnarwade.eduquiz.fragments.HelpFragment;
import com.vinodnarwade.eduquiz.fragments.QuizesFragment;
import com.vinodnarwade.eduquiz.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    String userType;
    String roleIs,userName,userId;
    public boolean doubleTap = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    setTitle("Home");

    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    editor = sharedPreferences.edit();

    roleIs = sharedPreferences.getString("roleIs", "").trim();

    if(sharedPreferences.getBoolean("isFirstTime", true)){
        welcome();
    }

    userName = sharedPreferences.getString("userName", "").trim();
    userId = sharedPreferences.getString("userId", "").trim();

    bottomNavigationView = findViewById(R.id.bnvhome);
    bottomNavigationView.setOnNavigationItemSelectedListener(this);
    bottomNavigationView.setSelectedItemId(R.id.menuquiz);

}

    private void welcome() {
        AlertDialog.Builder ad = new AlertDialog.Builder(HomeActivity.this);
        ad.setTitle("EduQuiz");
        ad.setMessage("Welcome to EduQuiz - A best app to train and improve yourself.");
        ad.setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.putBoolean("isFirstTime",false).commit();
                dialog.cancel();
            }
        }).create().show();
    }

    QuizesFragment quizesFragment = new QuizesFragment();
    //StudentProfileFragment studentProfileFragment = new StudentProfileFragment();
    HelpFragment helpFragment = new HelpFragment();
    ProfileFragment profileFragment = new ProfileFragment();

@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.menuquiz) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flhome, quizesFragment).commit();
    }
    else if (itemId == R.id.menuprofile) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flhome, profileFragment).commit();
    } else if (itemId == R.id.menuhelp) {
        getSupportFragmentManager().beginTransaction().replace(R.id.flhome, helpFragment).commit();
    }
    return true;
}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.up_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.up_menu_aboutUs){
            Toast.makeText(HomeActivity.this,"About Us",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this,AboutUsActivity.class);
            startActivity(intent);
            //finish();
        }
        else if(item.getItemId() == R.id.up_menu_contactUs){
            Toast.makeText(HomeActivity.this,"Contact Us",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this,ContactUsActivity.class);
            startActivity(intent);
            //finish();
        }
        else if(item.getItemId() == R.id.up_menu_settings){
            Toast.makeText(HomeActivity.this,"Settings",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this,SettingsActivity.class);
            startActivity(intent);
            //finish();
        }
        else {
            AlertDialog.Builder ad = new AlertDialog.Builder(HomeActivity.this);
            ad.setTitle("Logging Out");
            ad.setMessage("Are you sure for logging out?");
            ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    editor.putBoolean("islogin",false).commit();
                    startActivity(intent);
                    finish();
                }
            }).create().show();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(doubleTap){
            finish();
        }
        else{
            Toast.makeText(this,"Press Again to exit",Toast.LENGTH_SHORT).show();
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            },2000);
        }
    }
}