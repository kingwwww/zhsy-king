package com.example.lenovo.eatapplication.fragment.store;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.store.StoreIndexActivity;
import com.example.lenovo.eatapplication.beanLab.ClassfiyLab;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.model.requestModel.ClassfiyModel;
import com.example.lenovo.eatapplication.model.resModel.ClassfiyResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ResponseModel;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageClassfiyFragment extends Fragment {
    private LinearLayout mContainer;
    private Button mButton, mBtnReturn;
    private StoreLab mStoreLab;
    private ClassfiyLab mClassfiyLab;
    private StoreResponseModel mStoreResponseModel;
    private ClassfiyModel mClassfiyModel;
    private List<ClassfiyResponseModel> mClassfiyResponseModels;
    private Gson mGson = GsonBuilder.newInstance();
    private TextView mTextMessage;
    private static final String classfiySelectUrl = "http://112.124.13.208:8080/Eat_war/api/classfiy/select";
    private static final String classfiyAddUrl = "http://112.124.13.208:8080/Eat_war/api/classfiy/add";
    private static final String classfiyUpdateUrl = "http://112.124.13.208:8080/Eat_war/api/classfiy/update";
    private static final String classfiyDeleteUrl = "http://112.124.13.208:8080/Eat_war/api/classfiy/delete";

    private static final int RESPONSE_SELECT = 0;
    private static final int RESPONSE_ADD = 1;
    private static final int RESPONSE_UPDATE = 2;
    private static final int RESPONSE_DELETE = 3;

    private Handler mHandler;

    public ManageClassfiyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_classfiy, container, false);
        mStoreLab = StoreLab.getInstance();
        mClassfiyLab = ClassfiyLab.getInstance();
        mClassfiyModel = new ClassfiyModel();
        mStoreResponseModel = mStoreLab.getStoreResponseModel();
        mClassfiyModel.setStoreId(mStoreResponseModel.getId());

        mContainer = (LinearLayout) v.findViewById(R.id.view_container);
        mButton = (Button) v.findViewById(R.id.btn_addClassfiy);
        mBtnReturn = (Button) v.findViewById(R.id.btn_return);

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_SELECT:
                        if (code == ResponseModel.ERROR_NOT_FOUND_CLASSFIY){
                            mTextMessage = new TextView(getContext());
                            mTextMessage.setText("无分类信息，请添加");
                            mTextMessage.setGravity(Gravity.CENTER);
                            mContainer.addView(mTextMessage,0);
                        }else if (code == ResponseModel.SUCCEED){

                            Type type = new TypeToken<List<ClassfiyResponseModel>>(){}.getType();
                            mClassfiyResponseModels = mGson.fromJson(dataJson, type);
                            mClassfiyLab.setClassfiyResponseModels(mClassfiyResponseModels);

                            for (int i = 0; i < mClassfiyResponseModels.size(); i++){
                                final ClassfiyResponseModel model = mClassfiyResponseModels.get(i);
                                // 生成子item
                                View viewItem = addItemView(inflater, model.getClassfiyId(), model.getName());
                                mContainer.addView(viewItem, 0);
                            }
                        }
                        break;
                    case RESPONSE_ADD:
                        break;
                    case RESPONSE_UPDATE:
                        break;
                    case RESPONSE_DELETE:
                        break;
                }

            }
        };

        Message selectMessage = mHandler.obtainMessage();
        selectMessage.what = RESPONSE_SELECT;
        new HttpConnectionUtil().getConnection(mGson.toJson(mClassfiyModel), classfiySelectUrl, mHandler, selectMessage);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContainer.getChildCount() > 11){
                    Toast.makeText(getContext(), "最多只可添加十个！", Toast.LENGTH_SHORT).show();
                }
                else {
                    final View addClassfiyView = inflater.from(getContext()).inflate(R.layout.classfiy_add_item, null);
                    Dialog Dialog = new AlertDialog.Builder(getActivity())
                            .setView(addClassfiyView)
                            .setTitle("Input Classfiy")
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    EditText mEditClassfiyName = (EditText) addClassfiyView.findViewById(R.id.edit_classfiy_name);
                                    final String inputName = mEditClassfiyName.getText().toString();
                                    if (inputName.equals("")){
                                        Toast.makeText(getContext(), "请填写分类名称！", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        //移除提示信息view
                                        if (mTextMessage != null){
                                            mContainer.removeView(mTextMessage);
                                        }
                                        // 生成classfiyId
                                        final String cid = UUID.randomUUID().toString();
                                        // 生成子item
                                        View viewItem = addItemView(inflater, cid, inputName);
                                        // 传入服务器存库
                                        mClassfiyModel.setClassfiyId(cid);
                                        mClassfiyModel.setStoreId(mStoreResponseModel.getId());
                                        mClassfiyModel.setName(mEditClassfiyName.getText().toString());
                                        Message addMessage = mHandler.obtainMessage();
                                        addMessage.what = RESPONSE_ADD;
                                        new HttpConnectionUtil().getConnection(mGson.toJson(mClassfiyModel), classfiyAddUrl, mHandler, addMessage);
                                        // 显示到界面
                                        mContainer.addView(viewItem, 0);
                                    }
                                }
                            })
                            .create();
                    Dialog.show();
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

    public View addItemView(final LayoutInflater inflater, final String classfiyId, final String classfiyName){
        //动态生成视图view
        final View viewItem = inflater.from(getContext()).inflate(R.layout.classfiy_show_item, null);
        final TextView nameView = (TextView) viewItem.findViewById(R.id.classfiy_name);
        ImageView imageUpdate = (ImageView) viewItem.findViewById(R.id.classfiy_update);
        ImageView imageDelete = (ImageView) viewItem.findViewById(R.id.classfiy_delete);
        nameView.setTextColor(Color.BLACK);
        nameView.setText(classfiyName);
        imageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View updateClassfiyView = inflater.from(getContext()).inflate(R.layout.classfiy_add_item, null);
                final EditText mEditUpdateName = (EditText) updateClassfiyView.findViewById(R.id.edit_classfiy_name);
                mEditUpdateName.setText(nameView.getText());
                Dialog Dialog = new AlertDialog.Builder(getActivity())
                        .setView(updateClassfiyView)
                        .setCancelable(true)
                        .setTitle("Update Classfiy")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String updateName = mEditUpdateName.getText().toString();
                                if (updateName.equals("")){
                                    Toast.makeText(getContext(), "请填写分类名称！", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    mClassfiyModel.setName(updateName);
                                    mClassfiyModel.setClassfiyId(classfiyId);
                                    Message updateMessage = mHandler.obtainMessage();
                                    updateMessage.what = RESPONSE_UPDATE;
                                    new HttpConnectionUtil().getConnection(mGson.toJson(mClassfiyModel), classfiyUpdateUrl, mHandler, updateMessage);
                                    nameView.setText(updateName);
                                }
                            }
                        }).create();
                Dialog.show();
            }
        });
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClassfiyModel.setStoreId(null);
                mClassfiyModel.setName(null);
                mClassfiyModel.setClassfiyId(classfiyId);
                Message deleteMessage1 = mHandler.obtainMessage();
                deleteMessage1.what = RESPONSE_DELETE;
                new HttpConnectionUtil().getConnection(mGson.toJson(mClassfiyModel), classfiyDeleteUrl, mHandler, deleteMessage1);
                mContainer.removeView(viewItem);
            }
        });
        return viewItem;
    }
}
