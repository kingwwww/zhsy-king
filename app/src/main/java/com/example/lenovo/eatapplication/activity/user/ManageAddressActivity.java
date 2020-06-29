package com.example.lenovo.eatapplication.activity.user;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.user.ManageAddressFragment;

public class ManageAddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);
        FragmentManager fm = getSupportFragmentManager();
        ManageAddressFragment fragment = new ManageAddressFragment();
        fm.beginTransaction().add(R.id.activity_manage_address, fragment).commit();
    }
}
