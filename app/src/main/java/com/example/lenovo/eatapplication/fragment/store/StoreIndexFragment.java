package com.example.lenovo.eatapplication.fragment.store;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.enter.LoginActivity;
import com.example.lenovo.eatapplication.activity.store.AddProductActivity;
import com.example.lenovo.eatapplication.activity.store.ManageClassfiyActivity;
import com.example.lenovo.eatapplication.activity.store.PreViewActivity;
import com.example.lenovo.eatapplication.activity.store.StoreOrderActivity;
import com.example.lenovo.eatapplication.activity.store.StoreSelfActivity;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.util.DownloadFileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreIndexFragment extends Fragment {

    private ImageView mImagePortrait;
    private TextView mTextName, mTextPhone;
    private ImageButton mImageSelf, mImagePdt, mImageShow, mImageOrder;
    private StoreLab mStoreLab;
    private Handler mHandler;

    private static final int RESPONSE_DOWNLOAD_FILE = 0;
    private List<String> filePaths = new ArrayList<>();
    private List<Bitmap> mBitmaps = new ArrayList<>();
    private Button logout;


    public StoreIndexFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_store_index, container, false);

        mStoreLab = StoreLab.getInstance();
        final StoreResponseModel storeResponseModel = mStoreLab.getStoreResponseModel();


        logout = (Button) v.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        mImagePortrait = (ImageView) v.findViewById(R.id.image_store_portrait);
        mTextName = (TextView) v.findViewById(R.id.store_name);
        mTextPhone = (TextView) v.findViewById(R.id.store_phone);
        mImageSelf = (ImageButton) v.findViewById(R.id.image_btn_selef);
        mImagePdt = (ImageButton) v.findViewById(R.id.image_btn_pdt);
        mImageShow = (ImageButton) v.findViewById(R.id.image_btn_show);
        mImageOrder = (ImageButton) v.findViewById(R.id.image_order);

        mTextName.setText(storeResponseModel.getName());
        mTextPhone.setText(storeResponseModel.getPhone());

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mImagePortrait.setImageBitmap(mBitmaps.get(0));
                storeResponseModel.setBitmap(mBitmaps.get(0));
            }
        };

        filePaths.add(storeResponseModel.getPortrait());
        Message message = mHandler.obtainMessage();
        message.what = RESPONSE_DOWNLOAD_FILE;
        new DownloadFileUtil().downloadFile(filePaths, mBitmaps, mHandler, message);

        mImageSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoreSelfActivity.class);
                startActivity(intent);
            }
        });

        mImagePdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                startActivity(intent);
            }
        });

        mImageShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PreViewActivity.class);
                startActivity(intent);
            }
        });

        mImageOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoreOrderActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }



}
