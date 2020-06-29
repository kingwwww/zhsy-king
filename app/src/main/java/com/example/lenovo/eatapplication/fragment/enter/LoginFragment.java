package com.example.lenovo.eatapplication.fragment.enter;


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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.enter.RegisterActivity;
import com.example.lenovo.eatapplication.activity.store.StoreIndexActivity;
import com.example.lenovo.eatapplication.activity.user.UserIndexActivity;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.beanLab.UserLab;
import com.example.lenovo.eatapplication.model.requestModel.EnterModel;
import com.example.lenovo.eatapplication.model.resModel.ResponseModel;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.model.resModel.UserResponseModel;
import com.example.lenovo.eatapplication.util.CodeUtils;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText mEditPhone, mEditPassword, mEditCode;
    private RadioButton mRadioUser, mRadioStore;
    private Button mButtonLogin, mButtonGoReg;
    private ImageView mImageCode;
    private CodeUtils mCodeUtils;
    private String mCode;
    private Bitmap mBitmap;
    private Handler mHandler;

    private int login_tag;

    private EnterModel mEnterModel;

    private static final String httpUrl = "http://112.124.13.208:8080/Eat_war/api/enter/login";
    private static final int RESPONSE_LOGIN = 0;

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v =  inflater.inflate(R.layout.fragment_login, container, false);

        mCodeUtils = CodeUtils.newInstance();
        mEnterModel = new EnterModel();
        login_tag = EnterModel.TAG_USER;
        mEnterModel.setTag(login_tag);

        mBitmap = mCodeUtils.createBitmap();
        mCode = mCodeUtils.getCode();

        mEditPhone = (EditText) v.findViewById(R.id.edit_login_phone);
        mEditPassword = (EditText) v.findViewById(R.id.edit_login_password);
        mEditCode = (EditText) v.findViewById(R.id.edit_login_code);
        mRadioUser = (RadioButton) v.findViewById(R.id.radio_login_user);
        mRadioStore = (RadioButton) v.findViewById(R.id.radio_login_store);
        mImageCode = (ImageView) v.findViewById(R.id.image_login_code);
        mButtonLogin = (Button) v.findViewById(R.id.btn_login);
        mButtonGoReg = (Button) v.findViewById(R.id.btn_go_register);

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Gson gson = GsonBuilder.newInstance();
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_LOGIN:
                        switch (code){
                            case ResponseModel.ERROR_ACCOUNT_LOGIN:
                                Toast.makeText(getContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                break;
                            case ResponseModel.ERROR_NOT_FOUND_USER:
                                Toast.makeText(getContext(), "不存在此用户,请注册", Toast.LENGTH_SHORT).show();
                                break;
                            case ResponseModel.ERROR_NOT_FOUND_STORE:
                                Toast.makeText(getContext(), "不存在此商户,请注册", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                if (login_tag == EnterModel.TAG_USER) {
                                    UserResponseModel userResponseModel = gson.fromJson(dataJson, UserResponseModel.class);
                                    UserLab.getInstance().setUser(userResponseModel);
                                    Intent intent = new Intent(getActivity(), UserIndexActivity.class);
                                    startActivity(intent);
                                }
                                else if (login_tag == EnterModel.TAG_STORE){
                                    StoreResponseModel storeResponseModel = gson.fromJson(dataJson, StoreResponseModel.class);
                                    StoreLab.getInstance().setStoreResponseModel(storeResponseModel);
                                    Intent intent = new Intent(getActivity(), StoreIndexActivity.class);
                                    startActivity(intent);
                                }
                                break;
                        }
                        break;
                }
            }
        };
        mImageCode.setImageBitmap(mBitmap);

        mImageCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBitmap = mCodeUtils.createBitmap();
                mImageCode.setImageBitmap(mBitmap);
                mCode = mCodeUtils.getCode();
            }
        });

        mRadioUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_tag = EnterModel.TAG_USER;
                mEnterModel.setTag(login_tag);
            }
        });

        mRadioStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_tag = EnterModel.TAG_STORE;
                mEnterModel.setTag(login_tag);
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mEditPhone.getText().toString().trim();
                String password = mEditPassword.getText().toString().trim();
                String inputCode = mEditCode.getText().toString().trim();
                if (!mCode.equalsIgnoreCase(inputCode)){
                    Toast.makeText(getContext(), "验证码不正确", Toast.LENGTH_SHORT).show();
                }
                else {
                    mEnterModel.setPhone(phone);
                    mEnterModel.setPassword(password);
                    Message message = mHandler.obtainMessage();
                    message.what = RESPONSE_LOGIN;
                    new HttpConnectionUtil().getConnection(GsonBuilder.newInstance().toJson(mEnterModel), httpUrl, mHandler, message);
                }
            }
        });

        mButtonGoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

}
