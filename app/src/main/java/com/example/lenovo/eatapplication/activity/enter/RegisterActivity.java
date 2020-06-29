package com.example.lenovo.eatapplication.activity.enter;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.enter.RegisterFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FragmentManager fm = getSupportFragmentManager();
        RegisterFragment fragment = new RegisterFragment();
        fm.beginTransaction().add(R.id.register_fragment_container, fragment).commit();
    }
}
