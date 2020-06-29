package com.example.lenovo.eatapplication.activity.store;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.store.PreviewFragment;

public class PreViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);
        FragmentManager fm = getSupportFragmentManager();
        PreviewFragment fragment = new PreviewFragment();
        fm.beginTransaction().add(R.id.activity_pre_view, fragment).commit();
    }
}
