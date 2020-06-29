package com.example.lenovo.eatapplication.activity.store;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.fragment.store.order.StoreFailedFragment;
import com.example.lenovo.eatapplication.fragment.store.order.StoreFinishiFragment;
import com.example.lenovo.eatapplication.fragment.store.order.StoreUnDealFragment;

public class StoreOrderActivity extends AppCompatActivity {
    private TextView mTextTitle;
    private ImageView mImageUnDeal, mImageFailed, mImageFinish;
    private Fragment storeUnDeal;
    private Fragment storeFailed;
    private Fragment storeDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_order);
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mImageUnDeal = (ImageView) findViewById(R.id.image_undeal);
        mImageFailed = (ImageView) findViewById(R.id.image_failed);
        mImageFinish = (ImageView) findViewById(R.id.image_finish);
        // 默认初始化未接单信息
        innitFragment(0);

        mImageUnDeal .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                innitFragment(0);
            }
        });
        mImageFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                innitFragment(1);
            }
        });
        mImageFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                innitFragment(2);
            }
        });
    }

    private void innitFragment(int index){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (index) {
            case 0:
                storeUnDeal = new StoreUnDealFragment();
                transaction.add(R.id.data_container, storeUnDeal);
                transaction.show(storeUnDeal);

                mTextTitle.setText("未接订单信息");
                break;
            case 1:

                storeFailed = new StoreFailedFragment();
                transaction.add(R.id.data_container, storeFailed);
                transaction.show(storeFailed);

                mTextTitle.setText("拒绝订单信息");
                break;
            case 2:
                storeDeal = new StoreFinishiFragment();
                transaction.add(R.id.data_container, storeDeal);
                transaction.show(storeDeal);
                mTextTitle.setText("已完成订单信息");
                break;
            default:
                break;
        }
        transaction.commit();
    }


    private void hideFragment(FragmentTransaction transaction) {
        if (storeUnDeal != null) {
            transaction.hide(storeUnDeal);
        }
        if (storeFailed != null) {
            transaction.hide(storeFailed);
        }
        if (storeDeal != null) {
            transaction.hide(storeDeal);
        }
    }
}
