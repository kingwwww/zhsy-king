package com.example.lenovo.eatapplication.fragment.user;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.user.ManageAddressActivity;
import com.example.lenovo.eatapplication.activity.user.UserIndexActivity;
import com.example.lenovo.eatapplication.beanLab.UserLab;
import com.example.lenovo.eatapplication.model.requestModel.EnterModel;
import com.example.lenovo.eatapplication.model.resModel.UserResponseModel;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.example.lenovo.eatapplication.util.PathBuilder;
import com.example.lenovo.eatapplication.util.UploadFileUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.UUID;


public class UserSelfFragment extends Fragment {
    private ImageView mImageUser;
    private EditText mEditName, mEditPhone, mEditPassword;
    private Button mReturnIndex, mBtnSave;
    private UserResponseModel mUserResponseModel;

    private static final int RESPONSE_PICTURE = 40;

    private File mFile;
    private Handler mHandler;
    private Gson mGson = GsonBuilder.newInstance();

    private static final String updateUserUrl = "http://112.124.13.208:8080/Eat_war/api/user/updateUser";
    private static final int RESPONSE_UPDATE_USER = 0;

    public UserSelfFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_user_self, container, false);
        mImageUser = (ImageView) v.findViewById(R.id.image_user_portrait);
        mEditName = (EditText) v.findViewById(R.id.text_user_name);
        mEditPhone = (EditText) v.findViewById(R.id.text_user_phone);
        mEditPassword = (EditText) v.findViewById(R.id.text_user_password);
        mReturnIndex = (Button) v.findViewById(R.id.btn_return_index);
        mBtnSave = (Button) v.findViewById(R.id.btn_save);
        mUserResponseModel = UserLab.getInstance().getUser();

        mImageUser.setImageBitmap(mUserResponseModel.getBitmap());
        mEditName.setText(mUserResponseModel.getName());
        mEditPhone.setText(mUserResponseModel.getPhone());
        mEditPhone.setEnabled(false);
        mEditPassword.setText(mUserResponseModel.getPassword());

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_UPDATE_USER:
                        Toast.makeText(getContext(), "修改成功~", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        mImageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, RESPONSE_PICTURE);
            }
        });

        mReturnIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserIndexActivity.class);
                startActivity(intent);
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditName.getText().toString().trim();
                String password = mEditPassword.getText().toString().trim();
                String uniqueStr = mUserResponseModel.getPortrait();
                String subFileUrl = "";
                if (name.equals("")||password.equals("")){
                    Toast.makeText(getContext(), "请填写完整信息！", Toast.LENGTH_SHORT).show();
                }
                else {
                    EnterModel mEnterModel = new EnterModel();
                    mEnterModel.setId(mUserResponseModel.getId());
                    mEnterModel.setName(name);
                    mEnterModel.setPassword(password);
                    mEnterModel.setPortrait(mUserResponseModel.getPortrait());
                    // 选择了头像
                    if (mFile != null){
                        if (mUserResponseModel.getPortrait().equals(EnterModel.DEFAULT_USER_PORTRAIT)){
                            uniqueStr = UUID.randomUUID().toString();
                            subFileUrl = PathBuilder.buildPath(uniqueStr);
                            uniqueStr = uniqueStr + ".jpg";
                            mEnterModel.setPortrait(subFileUrl + "/" + uniqueStr);
                            mUserResponseModel.setPortrait(subFileUrl + "/" + uniqueStr);
                            UserLab.getInstance().setUser(mUserResponseModel);
                        }
                        new UploadFileUtil().uoloadFile(mFile, EnterModel.PICTURE, subFileUrl, uniqueStr, mHandler);
                    }
                    Message updateUserMsg = new Message();
                    updateUserMsg.what = RESPONSE_UPDATE_USER;
                    new HttpConnectionUtil().getConnection(mGson.toJson(mEnterModel), updateUserUrl, mHandler, updateUserMsg);
                    Toast.makeText(getContext(), "正在传输数据....", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.go_address, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.go_address_item:
                Intent intent = new Intent(getActivity(), ManageAddressActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_PICTURE) {
            if (data != null) {
                //回显图片
                Uri mUri = data.getData();
                mImageUser.setImageURI(mUri);
                //通过图片Uri转为file以备上传到服务器
                String[] arr = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().managedQuery(mUri, arr, null, null, null);
                int img_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String img_path = cursor.getString(img_index);
                mFile = new File(img_path);
            }
            else {
                mImageUser.setImageBitmap(mUserResponseModel.getBitmap());
            }
        }
    }
}
