package com.example.lenovo.eatapplication.activity.store;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.store.AddProductFragment;

public class AddProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        FragmentManager fm = getSupportFragmentManager();
        AddProductFragment fragment = new AddProductFragment();
        fm.beginTransaction().add(R.id.activity_add_product, fragment).commit();
    }
}
