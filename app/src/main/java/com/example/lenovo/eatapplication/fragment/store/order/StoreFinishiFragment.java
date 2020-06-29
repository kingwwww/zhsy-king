package com.example.lenovo.eatapplication.fragment.store.order;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.model.requestModel.OrderModel;
import com.example.lenovo.eatapplication.model.resModel.OrderDetailResponseModel;
import com.example.lenovo.eatapplication.model.resModel.OrderResponseModel;
import com.example.lenovo.eatapplication.util.DownloadFileUtil;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.example.lenovo.eatapplication.util.RecyclerViewAtRecycleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFinishiFragment extends Fragment {
    private RecyclerView mRecyclerOrder;

    private Handler mHandler;
    private Gson mGson = GsonBuilder.newInstance();
    private static final String getOrderUrl = "http://112.124.13.208:8080/Eat_war/api/order/getOrder";
    private static final int RESPONSE_GET_ORDER = 0;
    private static final int RESPONSE_GET_PICTURE = 1;

    private List<OrderResponseModel> mOrderResponseModels;
    private List<Bitmap> mBitmaps = new LinkedList<>();
    private List<String> mBitmapUrls = new LinkedList<>();
    private List<Integer> mIndexs = new LinkedList<>();
    private int current = 0;

    public StoreFinishiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_store_finishi, container, false);

        mRecyclerOrder = (RecyclerView) v.findViewById(R.id.order_finish);
        mHandler = new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_GET_ORDER:
                        Type type = new TypeToken<List<OrderResponseModel>>(){}.getType();
                        mOrderResponseModels = mGson.fromJson(dataJson, type);
                        if (mOrderResponseModels == null){
                            Toast.makeText(getContext(),"暂无订单信息...",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            for (OrderResponseModel orderResponseModel : mOrderResponseModels) {
                                if (current == 0){
                                    mIndexs.add(0);

                                }
                                else {
                                    mIndexs.add(current);

                                }
                                current += orderResponseModel.getOrderDetailResponseModels().size();
                                for (OrderDetailResponseModel orderDetailResponseModel : orderResponseModel.getOrderDetailResponseModels()) {
                                    mBitmapUrls.add(orderDetailResponseModel.getPicture());
                                }
                            }
                            Message getPictureMsg = new Message();
                            getPictureMsg.what = RESPONSE_GET_PICTURE;
                            new DownloadFileUtil().downloadFile(mBitmapUrls, mBitmaps, mHandler, getPictureMsg);
                        }
                        break;
                    case RESPONSE_GET_PICTURE:

                        mRecyclerOrder.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerOrder.setAdapter(new OrderAdapter());
                        break;
                }
            }
        };
        OrderModel orderModel = new OrderModel();
        orderModel.setStoreId(StoreLab.getInstance().getStoreResponseModel().getId());
        orderModel.setOrderTag(OrderModel.SELECT_DEAL_ORDER);
        orderModel.setRoleTag(OrderModel.ROLE_STORE);
        Message getOrderMessage = new Message();
        getOrderMessage.what = RESPONSE_GET_ORDER;
        new HttpConnectionUtil().getConnection(mGson.toJson(orderModel), getOrderUrl, mHandler, getOrderMessage);
        return v;
    }

    private class OrderHolder extends RecyclerView.ViewHolder {

        private RecyclerViewAtRecycleView mRecyclerOrderDetail;
        private TextView mTextName, mTextPhone, mTextAddress, mTextCountPrice, mTextStatus;

        public OrderHolder(View itemView) {
            super(itemView);
            mRecyclerOrderDetail = (RecyclerViewAtRecycleView) itemView.findViewById(R.id.order_detail);
            mTextName = (TextView) itemView.findViewById(R.id.people_name);
            mTextPhone = (TextView) itemView.findViewById(R.id.people_phone);
            mTextAddress = (TextView) itemView.findViewById(R.id.people_address);
            mTextCountPrice = (TextView) itemView.findViewById(R.id.orderPrice);
            mTextStatus = (TextView) itemView.findViewById(R.id.statusTag);
        }
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderHolder>{

        @Override
        public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.order_show_item, parent, false);
            return new OrderHolder(view);
        }

        @Override
        public void onBindViewHolder(final OrderHolder holder, int position) {
            final OrderResponseModel model = mOrderResponseModels.get(position);
            holder.mTextName.setText(model.getName());
            holder.mTextPhone.setText(model.getPhone());
            holder.mTextAddress.setText(model.getAddress());
            holder.mTextCountPrice.setTextColor(Color.RED);
            holder.mTextCountPrice.setText("￥" + String.valueOf(model.getTotal()));
            holder.mRecyclerOrderDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
            holder.mTextStatus.setTextColor(Color.GREEN);
            holder.mTextStatus.setText("已完成");
            List<Integer> bitmapIndex = new LinkedList<>();
            int startCount = mIndexs.get(position);
            int endCount = 0;
            // 到达订单数量最后一个
            if (position == (mOrderResponseModels.size() - 1)){
                endCount = mBitmaps.size() - mIndexs.get(position);
            }
            else {
                endCount = mIndexs.get(position + 1 ) - mIndexs.get(position);
            }
            for (int i = 0 ;i < endCount;i++){
                bitmapIndex.add(startCount);
                startCount += 1;
            }
            holder.mRecyclerOrderDetail.setAdapter(new OrderDetailAdapter(model.getOrderDetailResponseModels(),bitmapIndex));
        }

        @Override
        public int getItemCount() {
            return mOrderResponseModels.size();
        }
    }

    private class OrderDetailHolder extends RecyclerView.ViewHolder {

        private ImageView mImagePicture;
        private TextView mPtName, mPtPrice, mPtNumber, mPtCountPrice;
        public OrderDetailHolder(View itemView) {
            super(itemView);
            mImagePicture = (ImageView) itemView.findViewById(R.id.item_picture);
            mPtName = (TextView) itemView.findViewById(R.id.item_product);
            mPtPrice = (TextView) itemView.findViewById(R.id.item_price);
            mPtNumber = (TextView) itemView.findViewById(R.id.item_number);
            mPtCountPrice = (TextView) itemView.findViewById(R.id.item_count_price);
        }
    }

    private class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailHolder>{
        private List<OrderDetailResponseModel> mOrderDetails;
        private List<Integer> index;

        public OrderDetailAdapter(List<OrderDetailResponseModel> mDatas, List<Integer> number){
            mOrderDetails = mDatas;
            index = number;
        }
        @Override
        public OrderDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.settle_product_item, parent, false);
            return new OrderDetailHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderDetailHolder holder, int position) {
            OrderDetailResponseModel mOrderResModel = mOrderDetails.get(position);
            holder.mImagePicture.setImageBitmap(mBitmaps.get(index.get(position)));
            holder.mPtName.setText(mOrderResModel.getName());
            holder.mPtPrice.setTextColor(Color.RED);
            holder.mPtPrice.setText("￥" + String.valueOf(mOrderResModel.getPrice()));
            holder.mPtNumber.setText("x"+String.valueOf(mOrderResModel.getNumber()));
            holder.mPtCountPrice.setTextColor(Color.RED);
            BigDecimal a = BigDecimal.valueOf(Double.valueOf(mOrderResModel.getNumber()));
            BigDecimal b = BigDecimal.valueOf(mOrderResModel.getPrice());
            String counts  = a.multiply(b).toString();
            holder.mPtCountPrice.setText("￥" + counts);
        }

        @Override
        public int getItemCount() {
            return mOrderDetails.size();
        }
    }

}
