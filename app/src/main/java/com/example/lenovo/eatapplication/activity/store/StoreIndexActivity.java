package com.example.lenovo.eatapplication.activity.store;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.store.StoreIndexFragment;

public class StoreIndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_index);
        FragmentManager fm = getSupportFragmentManager();
        StoreIndexFragment fragment = new StoreIndexFragment();
        fm.beginTransaction().add(R.id.activity_store_index, fragment).commit();
    }


}
