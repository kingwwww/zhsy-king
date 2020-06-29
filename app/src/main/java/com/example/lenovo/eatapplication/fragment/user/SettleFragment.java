package com.example.lenovo.eatapplication.fragment.user;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.user.UserIndexActivity;
import com.example.lenovo.eatapplication.beanLab.ProductLab;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.beanLab.UserLab;
import com.example.lenovo.eatapplication.model.requestModel.AddressModel;
import com.example.lenovo.eatapplication.model.requestModel.OrderModel;
import com.example.lenovo.eatapplication.model.requestModel.ProductModel;
import com.example.lenovo.eatapplication.model.resModel.AddressResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ProductResponseModel;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;


public class SettleFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private RecyclerView mRecyclerView;
    private TextView mOrderPrice;
    private Spinner mSpinnerAddress;
    private List<String> ls = new LinkedList<>();
    private ArrayAdapter<String> adapter;
    private EditText mEditName, mEditPhone, mEditAddress;
    private Button mBtnReturn, mBtnPay;

    private List<ProductResponseModel> mProductResponseModels;
    private List<AddressResponseModel> mAddressResponseModels;
    private List<ProductResponseModel> mSettleProducts = new LinkedList<>();
    private OrderModel mOrderModel = new OrderModel();
    private List<ProductModel> mProductModels = new LinkedList<>();

    private Handler mHandler;
    private Gson mGson = GsonBuilder.newInstance();

    private static final String SETTLE_ORDER_URL = "http://112.124.13.208:8080/Eat_war/api/order/add";
    private static final String SELECT_ADDRESS_URL = "http://112.124.13.208:8080/Eat_war/api/address/select";
    private static final int RESPONSE_ORDER_ADD = 0;
    private static final int RESPONSE_ADDRESS_SELECT = 1;

    public SettleFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_settle, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.product_settle);
        mOrderPrice = (TextView) v.findViewById(R.id.orderPrice);
        mEditName = (EditText) v.findViewById(R.id.input_name);
        mEditPhone = (EditText) v.findViewById(R.id.input_phone);
        mEditAddress = (EditText) v.findViewById(R.id.input_address);
        mBtnReturn = (Button) v.findViewById(R.id.btn_return);
        mBtnPay = (Button) v.findViewById(R.id.btn_hand);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                switch (msg.what){
                    case RESPONSE_ORDER_ADD:
                        Toast.makeText(getContext(),"提交成功~",Toast.LENGTH_SHORT).show();
                        break;
                    case RESPONSE_ADDRESS_SELECT:
                        Type type = new TypeToken<List<AddressResponseModel>>(){}.getType();
                        mAddressResponseModels = mGson.fromJson(dataJson, type);
                        if (mAddressResponseModels!=null){

                            for (AddressResponseModel addressResponseModel : mAddressResponseModels) {

                                ls.add(addressResponseModel.getLocation());
                            }
                            adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, ls);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mSpinnerAddress.setAdapter(adapter);

                        }else {

                            Toast.makeText(getActivity(),"请添加收货地址",Toast.LENGTH_SHORT).show();

                        }

                        break;
                }
            }
        };

        mSpinnerAddress = (Spinner) v.findViewById(R.id.spinner_address);
        mSpinnerAddress.setOnItemSelectedListener(this);

        Message selectAddressMsg = new Message();
        selectAddressMsg.what = RESPONSE_ADDRESS_SELECT;
        AddressModel model = new AddressModel();
        model.setUserId(UserLab.getInstance().getUser().getId());
        new HttpConnectionUtil().getConnection(mGson.toJson(model), SELECT_ADDRESS_URL, mHandler, selectAddressMsg);

        String countPrice = getActivity().getIntent().getStringExtra("count_price");
        mOrderPrice.setTextColor(Color.RED);
        mOrderPrice.setText(countPrice);

        mProductResponseModels = ProductLab.getInstance().getProductResponseModels();
        for (ProductResponseModel productResponseModel : mProductResponseModels) {
            if (productResponseModel.getNumber() != 0){
                mSettleProducts.add(productResponseModel);
            }
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new SettleAdapter());

        mBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserIndexActivity.class);
                startActivity(intent);
            }
        });
        mBtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditName.getText().toString().trim();
                String phone = mEditPhone.getText().toString().trim();
                String address = mEditAddress.getText().toString().trim();
                if (name.equals("")||phone.equals("")||address.equals("")){
                    Toast.makeText(getContext(),"请完整填写收货信息",Toast.LENGTH_SHORT).show();
                }
                else {
                    mOrderModel.setName(name);
                    mOrderModel.setPhone(phone);
                    mOrderModel.setAddress(address);
                    mOrderModel.setUserId(UserLab.getInstance().getUser().getId());
                    mOrderModel.setStoreId(StoreLab.getInstance().getStoreResponseModel().getId());
                    mOrderModel.setTotal(Double.valueOf(mOrderPrice.getText().toString().substring(1)));
                    for (ProductResponseModel settleProduct : mSettleProducts) {
                        ProductModel product = new ProductModel();
                        product.setProductId(settleProduct.getId());
                        product.setName(settleProduct.getName());
                        product.setPicture(settleProduct.getPicture());
                        product.setPrice(settleProduct.getPrice());
                        product.setNumber(settleProduct.getNumber());
                        mProductModels.add(product);
                    }
                    mOrderModel.setProductModels(mProductModels);
                    Message settleOrderMsg = new Message();
                    settleOrderMsg.what = RESPONSE_ORDER_ADD;
                    new HttpConnectionUtil().getConnection(mGson.toJson(mOrderModel), SETTLE_ORDER_URL, mHandler, settleOrderMsg);
                    Toast.makeText(getContext(),"正在提交中....",Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        AddressResponseModel model = mAddressResponseModels.get(i);
        mEditName.setText(model.getName());
        mEditPhone.setText(model.getPhone());
        mEditAddress.setText(model.getLocation());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class SettleHolder extends RecyclerView.ViewHolder{

        private ImageView mImagePicture;
        private TextView mTextName, mTextPrice, mTextNumber, mTextCountPrice;
        public SettleHolder(View itemView) {
            super(itemView);
            mImagePicture = (ImageView) itemView.findViewById(R.id.item_picture);
            mTextName = (TextView) itemView.findViewById(R.id.item_product);
            mTextPrice = (TextView) itemView.findViewById(R.id.item_price);
            mTextNumber = (TextView) itemView.findViewById(R.id.item_number);
            mTextCountPrice = (TextView) itemView.findViewById(R.id.item_count_price);
        }
    }

    private class SettleAdapter extends RecyclerView.Adapter<SettleHolder> {

        @Override
        public SettleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.settle_product_item, parent, false);
            return new SettleHolder(view);
        }

        @Override
        public void onBindViewHolder(SettleHolder holder, int position) {
            ProductResponseModel settleProduct = mSettleProducts.get(position);
            holder.mImagePicture.setImageBitmap(settleProduct.getBitmap());
            holder.mTextName.setText(settleProduct.getName());
            holder.mTextNumber.setText("x"+settleProduct.getNumber());
            holder.mTextPrice.setTextColor(Color.RED);
            holder.mTextPrice.setText("￥" + settleProduct.getPrice());

            BigDecimal a = BigDecimal.valueOf(Double.valueOf(settleProduct.getNumber()));
            BigDecimal b = BigDecimal.valueOf(settleProduct.getPrice());
            String counts  = a.multiply(b).toString();
            holder.mTextCountPrice.setTextColor(Color.RED);
            holder.mTextCountPrice.setText("￥" + counts );
        }

        @Override
        public int getItemCount() {
            return mSettleProducts.size();
        }
    }

}
