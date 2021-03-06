package com.example.lenovo.eatapplication.activity.user;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.user.UserIndexFragment;

public class UserIndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_index);
        FragmentManager fm = getSupportFragmentManager();
        UserIndexFragment fragment = new UserIndexFragment();
        fm.beginTransaction().add(R.id.activity_user_index, fragment).commit();
    }
}
