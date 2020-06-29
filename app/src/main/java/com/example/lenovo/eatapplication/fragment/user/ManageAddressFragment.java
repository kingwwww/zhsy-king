package com.example.lenovo.eatapplication.fragment.user;


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
import com.example.lenovo.eatapplication.activity.user.UserIndexActivity;
import com.example.lenovo.eatapplication.beanLab.ClassfiyLab;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.beanLab.UserLab;
import com.example.lenovo.eatapplication.model.requestModel.AddressModel;
import com.example.lenovo.eatapplication.model.requestModel.ClassfiyModel;
import com.example.lenovo.eatapplication.model.resModel.AddressResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ClassfiyResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ResponseModel;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.model.resModel.UserResponseModel;
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
public class ManageAddressFragment extends Fragment {
    private LinearLayout mContainer;
    private Button mButton, mBtnReturn;
    private AddressModel mAddressModel;
    private List<AddressResponseModel> mAddressResponseModels;
    private Gson mGson = GsonBuilder.newInstance();
    private TextView mTextMessage;
    private static final String AddressSelectUrl = "http://112.124.13.208:8080/Eat_war/api/address/select";
    private static final String AddressAddUrl = "http://112.124.13.208:8080/Eat_war/api/address/add";
    private static final String AddressUpdateUrl = "http://112.124.13.208:8080/Eat_war/api/address/update";
    private static final String AddressDeleteUrl = "http://112.124.13.208:8080/Eat_war/api/address/delete";

    private static final int RESPONSE_SELECT = 0;
    private static final int RESPONSE_ADD = 1;
    private static final int RESPONSE_UPDATE = 2;
    private static final int RESPONSE_DELETE = 3;

    private Handler mHandler;


    public ManageAddressFragment() {

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_address, container, false);
        mButton = (Button) v.findViewById(R.id.btn_add_address);
        mBtnReturn = (Button) v.findViewById(R.id.btn_return_self);

        UserResponseModel userResponseModel = UserLab.getInstance().getUser();
        mAddressModel = new AddressModel();
        mAddressModel.setUserId(userResponseModel.getId());
        mContainer = (LinearLayout) v.findViewById(R.id.addressContainer);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_SELECT:
                        if (code == ResponseModel.ERROR_NOT_FOUND_ADDRESS){
                            mTextMessage = new TextView(getContext());
                            mTextMessage.setText("未查询到相关地址信息，请添加");
                            mTextMessage.setGravity(Gravity.CENTER);
                            mContainer.addView(mTextMessage,0);
                        }else if (code == ResponseModel.SUCCEED){
                            Type type = new TypeToken<List<AddressResponseModel>>(){}.getType();
                            mAddressResponseModels = mGson.fromJson(dataJson, type);

                            for (int i = 0; i < mAddressResponseModels.size(); i++){
                                final AddressResponseModel model = mAddressResponseModels.get(i);
                                // 生成子item
                                View viewItem = addItemView(inflater, model.getAddressId(), model.getName(), model.getPhone(), model.getLocation());
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
        Message selectMsg = new Message();
        selectMsg.what = RESPONSE_SELECT;
        new HttpConnectionUtil().getConnection(mGson.toJson(mAddressModel), AddressSelectUrl, mHandler, selectMsg);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContainer.getChildCount() > 6){
                    Toast.makeText(getContext(), "最多只可添加五个地址！", Toast.LENGTH_SHORT).show();
                }
                else {
                    final View addAddressView = inflater.from(getContext()).inflate(R.layout.address_item, null);
                    Dialog Dialog = new AlertDialog.Builder(getActivity())
                            .setView(addAddressView)
                            .setTitle("Input Classfiy")
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final EditText mEditAddressName = (EditText) addAddressView.findViewById(R.id.address_name);
                                    final EditText mEditAddressPhone = (EditText) addAddressView.findViewById(R.id.address_phone);
                                    final EditText mEditAddressDetail = (EditText) addAddressView.findViewById(R.id.address_detail);
                                    final String inputName = mEditAddressName.getText().toString();
                                    final String inputPhone = mEditAddressPhone.getText().toString();
                                    final String inputDetail = mEditAddressDetail.getText().toString();
                                    if (inputName.equals("")||inputPhone.equals("")||inputDetail.equals("")){
                                        Toast.makeText(getContext(), "请填写完整！", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        //移除提示信息view
                                        if (mTextMessage != null){
                                            mContainer.removeView(mTextMessage);
                                        }
                                        // 生成addressId
                                        final String aid = UUID.randomUUID().toString();
                                        // 生成子item
                                        View viewItem = addItemView(inflater, aid, inputName, inputPhone, inputDetail);
                                        // 传入服务器存库
                                        mAddressModel.setAddressId(aid);
                                        mAddressModel.setName(inputName);
                                        mAddressModel.setPhone(inputPhone);
                                        mAddressModel.setLocation(inputDetail);
                                        Message addMessage = mHandler.obtainMessage();
                                        addMessage.what = RESPONSE_ADD;
                                        new HttpConnectionUtil().getConnection(mGson.toJson(mAddressModel), AddressAddUrl, mHandler, addMessage);
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
                Intent intent = new Intent(getActivity(), UserIndexActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public View addItemView(final LayoutInflater inflater, final String addressId, final String addressName, final String addressPhone, final String addressDetail){
        //动态生成视图view
        final View viewItem = inflater.from(getContext()).inflate(R.layout.address_show_item, null);
        final TextView nameView = (TextView) viewItem.findViewById(R.id.address_show_name);
        final TextView phoneView = (TextView) viewItem.findViewById(R.id.address_show_phone);
        final TextView detailView = (TextView) viewItem.findViewById(R.id.address_show_detail);
        ImageView imageUpdate = (ImageView) viewItem.findViewById(R.id.address_update);
        ImageView imageDelete = (ImageView) viewItem.findViewById(R.id.address_delete);
        nameView.setTextColor(Color.BLACK);
        nameView.setText(addressName);
        phoneView.setText(addressPhone);
        detailView.setTextColor(Color.BLACK);
        detailView.setText(addressDetail);
        imageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View updateClassfiyView = inflater.from(getContext()).inflate(R.layout.address_item, null);
                final EditText mEditUpdateName = (EditText) updateClassfiyView.findViewById(R.id.address_name);
                final EditText mEditUpdatePhone = (EditText) updateClassfiyView.findViewById(R.id.address_phone);
                final EditText mEditUpdateDetail = (EditText) updateClassfiyView.findViewById(R.id.address_detail);
                mEditUpdateName.setText(nameView.getText());
                mEditUpdatePhone.setText(phoneView.getText());
                mEditUpdateDetail.setText(detailView.getText());
                Dialog Dialog = new AlertDialog.Builder(getActivity())
                        .setView(updateClassfiyView)
                        .setCancelable(true)
                        .setTitle("Update Address")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String updateName = mEditUpdateName.getText().toString();
                                final String updatePhone = mEditUpdatePhone.getText().toString();
                                final String updateDetail = mEditUpdateDetail.getText().toString();
                                if (updateName.equals("")||updatePhone.equals("")||updateDetail.equals("")){
                                    Toast.makeText(getContext(), "请填写完整信息！", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mAddressModel.setAddressId(addressId);
                                    mAddressModel.setName(updateName);
                                    mAddressModel.setPhone(updatePhone);
                                    mAddressModel.setLocation(updateDetail);
                                    Message updateMessage = mHandler.obtainMessage();
                                    updateMessage.what = RESPONSE_UPDATE;
                                    new HttpConnectionUtil().getConnection(mGson.toJson(mAddressModel), AddressUpdateUrl, mHandler, updateMessage);
                                    nameView.setText(updateName);
                                    phoneView.setText(updatePhone);
                                    detailView.setText(updateDetail);
                                }

                            }
                        }).create();
                Dialog.show();
            }
        });
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddressModel.setAddressId(addressId);
                Message deleteMessage1 = mHandler.obtainMessage();
                deleteMessage1.what = RESPONSE_DELETE;
                new HttpConnectionUtil().getConnection(mGson.toJson(mAddressModel), AddressDeleteUrl, mHandler, deleteMessage1);
                mContainer.removeView(viewItem);
            }
        });
        return viewItem;
    }

}
