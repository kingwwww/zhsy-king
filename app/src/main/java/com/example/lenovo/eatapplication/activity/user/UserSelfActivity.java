package com.example.lenovo.eatapplication.activity.user;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.user.UserSelfFragment;

public class UserSelfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_self);
        FragmentManager fm = getSupportFragmentManager();
        UserSelfFragment fragment = new UserSelfFragment();
        fm.beginTransaction().add(R.id.activity_user_self, fragment).commit();
    }
}
