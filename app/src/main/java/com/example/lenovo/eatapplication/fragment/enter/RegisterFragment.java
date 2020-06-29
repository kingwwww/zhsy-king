package com.example.lenovo.eatapplication.fragment.enter;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.model.requestModel.EnterModel;
import com.example.lenovo.eatapplication.model.resModel.ResponseModel;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.example.lenovo.eatapplication.util.PathBuilder;
import com.example.lenovo.eatapplication.util.UploadFileUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private static final int RESPONSE_PICTURE = 3;

    private ImageButton mPortraitButton;
    private File mFile;
    private EditText mEditName, mEditPhone, mEditPassword, mEditRepeat;
    private RadioButton mRadioUser, mRadioStore;
    private Button mButtonRegister;
    private EnterModel mEnterModel;
    private Handler mHandler;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String httpUrl = "http://112.124.13.208:8080/Eat_war/api/enter/register";
    private static final int RESPONSE_REGISTER = 0;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private String uniqueStr = "";
    private String subFileUrl = "";

    public RegisterFragment() {

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_register, container, false);

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(getActivity(),
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //默认为用户注册
        mEnterModel = new EnterModel();
        mEnterModel.setTag(EnterModel.TAG_USER);

        mPortraitButton = (ImageButton) v.findViewById(R.id.image_reg_portrait);
        mEditName = (EditText) v.findViewById(R.id.edit_reg_name);
        mEditPhone = (EditText) v.findViewById(R.id.edit_reg_phone);
        mEditPassword = (EditText) v.findViewById(R.id.edit_reg_password);
        mEditRepeat = (EditText) v.findViewById(R.id.edit_reg_repeat);
        mRadioUser = (RadioButton) v.findViewById(R.id.radio_reg_user);
        mRadioStore = (RadioButton) v.findViewById(R.id.radio_reg_store);
        mButtonRegister = (Button) v.findViewById(R.id.btn_register);

         mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_REGISTER:
                        if (code == ResponseModel.SUCCEED) {
                            if (mFile != null){
                                new UploadFileUtil().uoloadFile(mFile, EnterModel.PICTURE, subFileUrl, uniqueStr, mHandler);
                            }
                            Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        } else if (code == ResponseModel.ERROR_ACCOUNT_REGISTER) {
                            Toast.makeText(getContext(), "该手机号已被注册，请重新注册！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
        };
        mPortraitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, RESPONSE_PICTURE);
            }
        });

        mRadioUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEnterModel.setTag(EnterModel.TAG_USER);
            }
        });

        mRadioStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEnterModel.setTag(EnterModel.TAG_STORE);
            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mEditName.getText().toString().trim();
                String phone = mEditPhone.getText().toString().trim();
                String password = mEditPassword.getText().toString().trim();
                String repeat = mEditRepeat.getText().toString().trim();
                if (name.equals("") || phone.equals("") || password.equals("")|| repeat.equals("")){
                    Toast.makeText(getContext(),"信息未填写完整", Toast.LENGTH_SHORT).show();
                }else
                if (!password.equals(repeat)){
                    Toast.makeText(getContext(),"两次密码输入不一致", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(mFile != null){
                        uniqueStr = UUID.randomUUID().toString();
                        subFileUrl = PathBuilder.buildPath(uniqueStr);
                        uniqueStr = uniqueStr + ".jpg";
                        mEnterModel.setPortrait(subFileUrl + "/" + uniqueStr);
                    }
                    mEnterModel.setName(name);
                    mEnterModel.setPhone(phone);
                    mEnterModel.setPassword(password);
                    String requestJson = GsonBuilder.newInstance().toJson(mEnterModel);
                    Message message = mHandler.obtainMessage();
                    message.what = RESPONSE_REGISTER;
                    new HttpConnectionUtil().getConnection(requestJson, httpUrl , mHandler, message);
                }
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_PICTURE) {
            if (data != null) {
                //回显图片
                Uri mUri = data.getData();
                mPortraitButton.setImageURI(mUri);
                //通过图片Uri转为file以备上传到服务器
                String[] arr = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().managedQuery(mUri, arr, null, null, null);
                int img_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String img_path = cursor.getString(img_index);
                mFile = new File(img_path);
            }
        }
    }

}
