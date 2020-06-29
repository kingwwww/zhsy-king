package com.example.lenovo.eatapplication.fragment.store;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.store.StoreIndexActivity;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.model.requestModel.EnterModel;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.example.lenovo.eatapplication.util.PathBuilder;
import com.example.lenovo.eatapplication.util.UploadFileUtil;

import java.io.File;
import java.util.UUID;


public class StoreSelfFragment extends Fragment {
    private VideoView mVideoView;
    private EditText mEditName, mEditPassword, mEditDesc, mEditPhone;
    private Button mBtnChange, mBtnReturn, mBtnSave;
    private StoreLab mStoreLab;
    private StoreResponseModel mStoreResponseModel;
    private File mVideoFile;
    private Handler mHandler;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int RESPONSE_UPDATE_MESSAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private static final int RESPONSE_VIDEO = 101;

    private static final String VIDEO_BASE_PATH = "http://112.124.13.208:8080/media/video/";
    private String httpUrl = "http://112.124.13.208:8080/Eat_war/api/store/updateStore";

    private EnterModel mEnterModel = new EnterModel();
    public StoreSelfFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store_self, container, false);
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
        mVideoView = (VideoView) v.findViewById(R.id.video_desc);
        mEditPhone = (EditText) v.findViewById(R.id.store_phone);
        mEditName = (EditText) v.findViewById(R.id.store_name);
        mEditPassword = (EditText) v.findViewById(R.id.store_pswd);
        mEditDesc = (EditText) v.findViewById(R.id.store_desc);
        mBtnChange = (Button) v.findViewById(R.id.change_video);
        mBtnReturn = (Button) v.findViewById(R.id.btn_return);
        mBtnSave = (Button) v.findViewById(R.id.btn_save);
        mStoreLab = StoreLab.getInstance();
        mStoreResponseModel = mStoreLab.getStoreResponseModel();


        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case RESPONSE_UPDATE_MESSAGE:
                        break;
                }
            }
        };

        mEditName.setText(mStoreResponseModel.getName());
        mEditPhone.setText(mStoreResponseModel.getPhone());
        mEditPhone.setEnabled(false);
        mEditPassword.setText(mStoreResponseModel.getPassword());
        if (mStoreResponseModel.getDescription() != null){
            mEditDesc.setText(mStoreResponseModel.getDescription());
        }
        else {
            mEditDesc.setHint("请添加店铺介绍");
        }
        String uri = VIDEO_BASE_PATH + mStoreResponseModel.getDesVideo();
        mVideoView.setVideoURI(Uri.parse(uri));
        mVideoView.requestFocus();
        mVideoView.start();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });

        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*");
                startActivityForResult(intent, RESPONSE_VIDEO);
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditName.getText().toString().trim();
                String password = mEditPassword.getText().toString().trim();
                String description = mEditDesc.getText().toString().trim();
                if (name == null ||  password == null || description == null){
                    Toast.makeText(getContext(),"请填写空白信息",Toast.LENGTH_SHORT).show();
                }
                else {
                    mEnterModel.setId(mStoreResponseModel.getId());
                    mStoreResponseModel.setName(name);
                    mStoreResponseModel.setPassword(password);
                    mStoreResponseModel.setDescription(description);
                    mEnterModel.setName(name);
                    mEnterModel.setPassword(password);
                    mEnterModel.setDescription(description);
                    mEnterModel.setPhone(mStoreResponseModel.getPhone());
                    mEnterModel.setDesVideo(mStoreResponseModel.getDesVideo());
                    String uniqueStr = "";
                    String subFileUrl = "";
                    if (mVideoFile != null){
                        if(mStoreResponseModel.getDesVideo().equals(EnterModel.DEFAULT_STORE_DESVIDEO)){
                            uniqueStr = UUID.randomUUID().toString();
                            subFileUrl = PathBuilder.buildPath(uniqueStr);
                            uniqueStr = uniqueStr + ".mp4";
                            mEnterModel.setDesVideo(subFileUrl + "/" + uniqueStr);
                            mStoreResponseModel.setDesVideo(subFileUrl+ "/" + uniqueStr);
                        }
                        else {
                            uniqueStr = mEnterModel.getDesVideo();
                        }
                        new UploadFileUtil().uoloadFile(mVideoFile, EnterModel.VIDEO, subFileUrl, uniqueStr, new Handler());
                    }
                    Message message = mHandler.obtainMessage();
                    message.what = RESPONSE_UPDATE_MESSAGE;
                    new HttpConnectionUtil().getConnection(GsonBuilder.newInstance().toJson(mEnterModel), httpUrl, new Handler(), message);
                    Toast.makeText(getContext(), "更改成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoreIndexActivity.class);
                startActivity(intent);
            }
        });
        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RESPONSE_VIDEO) {
            if (data != null) {
                System.out.println("正在回显选择的视频文件...");
                //回显视频
                Uri mUri = data.getData();
                String[] arr = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().managedQuery(mUri, arr, null, null, null);
                int img_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String img_path = cursor.getString(img_index);
                File file = new File(img_path);
                String fileName = file.getName();
                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                System.out.println(fileType);
                System.out.println(file.length());
                if (!fileType.equals("mp4")){
                    Toast.makeText(getContext(),"类型不符！",Toast.LENGTH_SHORT).show();
                }else if (file.length() > 30 * 1024 * 1024){
                    Toast.makeText(getContext(),"文件过大！",Toast.LENGTH_SHORT).show();
                }else {
                    mVideoFile = file;
                    mVideoView.setVideoURI(mUri);
                    mVideoView.requestFocus();
                    mVideoView.start();
                    System.out.println("成功回显选择的视频...");
                }
            }
                else {
                System.out.println("未选择视频文件,播放原本视频文件");
                String uri = VIDEO_BASE_PATH + mStoreResponseModel.getDesVideo();
                mVideoView.setVideoURI(Uri.parse(uri));
                mVideoView.requestFocus();
                mVideoView.start();
                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mVideoView.start();
                    }
                });
            }
        }
    }

}
