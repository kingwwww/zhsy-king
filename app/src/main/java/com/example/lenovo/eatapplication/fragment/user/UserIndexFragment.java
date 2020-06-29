package com.example.lenovo.eatapplication.fragment.user;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.enter.LoginActivity;
import com.example.lenovo.eatapplication.activity.user.ShoppingActivity;
import com.example.lenovo.eatapplication.activity.user.UserSelfActivity;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.beanLab.UserLab;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.model.resModel.UserResponseModel;
import com.example.lenovo.eatapplication.util.DownloadFileUtil;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserIndexFragment extends Fragment {

    private ImageView mUserPortrait;
    private RecyclerView mAllStore;
    private List<StoreResponseModel> mStoreResponseModels;
    private List<Bitmap> mBitmaps = new LinkedList<>();
    private List<String> mBitmapUrls = new LinkedList<>();

    private Button logout;
    private StoreLab mStoreLab;
    private UserResponseModel mUserResponseModel;
    private Handler mHandler;
    private Gson mGson = GsonBuilder.newInstance();

    private static final String GET_ALL_STORE = "http://112.124.13.208:8080/Eat_war/api/store/getAll";
    private static final int RESPONSE_GET_ALL = 0;
    private static final int RESPONSE_PORTRAIT = 1;

    public UserIndexFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_index, container, false);
        mAllStore = (RecyclerView) v.findViewById(R.id.all_store);
        mUserPortrait = (ImageView) v.findViewById(R.id.user_picture);
        mStoreLab = StoreLab.getInstance();
        mUserResponseModel = UserLab.getInstance().getUser();
        logout = (Button) v.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_GET_ALL:
                        Type type = new TypeToken<List<StoreResponseModel>>(){}.getType();
                        mStoreResponseModels = mGson.fromJson(dataJson, type);
                        for (StoreResponseModel storeResponseModel : mStoreResponseModels) {
                            mBitmapUrls.add(storeResponseModel.getPortrait());
                        }
                        mBitmapUrls.add(mUserResponseModel.getPortrait());
                        Message getPortrait = new Message();
                        getPortrait.what = RESPONSE_PORTRAIT;
                        new DownloadFileUtil().downloadFile(mBitmapUrls, mBitmaps, mHandler, getPortrait);
                        break;
                    case RESPONSE_PORTRAIT:
                        mUserPortrait.setImageBitmap(mBitmaps.get(mBitmaps.size() - 1));
                        mUserResponseModel.setBitmap(mBitmaps.get(mBitmaps.size() -1));
                        UserLab.getInstance().setUser(mUserResponseModel);
                        mAllStore.setLayoutManager(new GridLayoutManager(getActivity(),2));
                        mAllStore.setAdapter(new StoreAdapter());
                        break;
                }
            }
        };
        mUserPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserSelfActivity.class);
                startActivity(intent);
            }
        });
        Message getAllMessage = new Message();
        getAllMessage.what = RESPONSE_GET_ALL;
        new HttpConnectionUtil().getConnection("",GET_ALL_STORE, mHandler, getAllMessage);
        return v;
    }

    private class StoreHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private StoreResponseModel storeModel;
        private ImageView mImagePortrait;
        private TextView mTextName, mTextDesc, mTextPhone;
        public StoreHolder(View itemView) {
            super(itemView);
            mImagePortrait = (ImageView) itemView.findViewById(R.id.store_portrait);
            mTextName = (TextView) itemView.findViewById(R.id.store_name);
            mTextDesc = (TextView) itemView.findViewById(R.id.store_desc);
            mTextPhone = (TextView) itemView.findViewById(R.id.store_phone);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mStoreLab.setStoreResponseModel(storeModel);
            Intent intent = new Intent(getActivity(), ShoppingActivity.class);
            startActivity(intent);
        }
    }


    private class StoreAdapter extends RecyclerView.Adapter<StoreHolder> {

        public StoreAdapter(){

        }
        @Override
        public StoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.show_store_item, parent, false);
            return new StoreHolder(view);
        }

        @Override
        public void onBindViewHolder(StoreHolder holder, int position) {
            StoreResponseModel model = mStoreResponseModels.get(position);
            holder.storeModel = model;
            holder.mImagePortrait.setImageBitmap(mBitmaps.get(position));
            holder.mTextName.setText(model.getName());
            holder.mTextDesc.setText(model.getDescription());
            holder.mTextPhone.setText(model.getPhone());
        }

        @Override
        public int getItemCount() {
            return mStoreResponseModels.size();
        }
    }




}
