package com.example.lenovo.eatapplication.fragment.user;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.user.SettleActivity;
import com.example.lenovo.eatapplication.beanLab.ProductLab;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.model.resModel.ClassfiyResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ProductResponseModel;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.util.BezierTypeEvaluator;
import com.example.lenovo.eatapplication.util.DownloadFileUtil;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.R.drawable.ic_input_add;


public class ShoppingFragment extends Fragment {

    private static LinearLayout mLinearLayout;
    private TextView mTextName, mTextDesc, mTextPhone, mCountPrice;
    private VideoView mVideoView;
    private RecyclerView mRecyclerClassfiy, mRecyclerProduct;
    private static Activity mActivity;
    private static ImageView carView;
    private Button mSendButton;

    private StoreResponseModel mStoreResponseModel;
    private List<ClassfiyResponseModel> mClassfiyResponseModels;
    private List<ProductResponseModel> mProducts = new LinkedList<>();
    private List<Bitmap> mBitmaps = new LinkedList<>();
    private List<String> mBitmapUrls = new LinkedList<>();

    private Handler mHandler;
    private Gson mGson = GsonBuilder.newInstance();

    private static final String VIDEO_BASE_PATH = "http://112.124.13.208:8080/media/video/";
    private static final String GET_DETAIL_URL = "http://112.124.13.208:8080/Eat_war/api/store/getDetail";

    private static final int RESPONSE_GETDETAIL = 0;
    private static final int RESPONSE_PICTURE = 1;

    private static Boolean mShouldScroll = false;
    private static int mToPosition;
    private int current = 0;



    public ShoppingFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping, container, false);
        mLinearLayout = (LinearLayout) v.findViewById(R.id.mainLayout);
        mTextName = (TextView) v.findViewById(R.id.shop_store_name);
        mTextDesc = (TextView) v.findViewById(R.id.shop_store_desc);
        mTextPhone = (TextView) v.findViewById(R.id.shop_store_phone);
        mCountPrice = (TextView) v.findViewById(R.id.countPrice);
        mVideoView = (VideoView) v.findViewById(R.id.store_video);
        mRecyclerClassfiy = (RecyclerView) v.findViewById(R.id.product_classify);
        mRecyclerProduct = (RecyclerView) v.findViewById(R.id.product_recyclerView);
        mSendButton = (Button) v.findViewById(R.id.sendbutton);

        carView = (ImageView) v.findViewById(R.id.carView);
        mStoreResponseModel = StoreLab.getInstance().getStoreResponseModel();
        mTextName.setText("店家名称:     " + mStoreResponseModel.getName());
        mTextDesc.setText("商家简介: " + mStoreResponseModel.getDescription());
        mTextPhone.setText("联系热线: " + mStoreResponseModel.getPhone());

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
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                super.handleMessage(msg);
                switch (msg.what){
                    case RESPONSE_GETDETAIL:
                        Type type = new TypeToken<List<ClassfiyResponseModel>>(){}.getType();
                        mClassfiyResponseModels = mGson.fromJson(dataJson, type);
                        for (ClassfiyResponseModel classfiyResponseModel : mClassfiyResponseModels) {
                            if (current == 0){
                                current += classfiyResponseModel.getProductResponseModels().size();
                                classfiyResponseModel.setIndex(0);
                            }
                            else {
                                classfiyResponseModel.setIndex(current);
                                current += classfiyResponseModel.getProductResponseModels().size();
                            }
                            for (ProductResponseModel productResponseModel : classfiyResponseModel.getProductResponseModels()) {
                                mProducts.add(productResponseModel);
                                mBitmapUrls.add(productResponseModel.getPicture());
                            }
                        }
                        mRecyclerClassfiy.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerClassfiy.setAdapter(new ClassifyAdapter(mClassfiyResponseModels));
                        Message bitmapMessage = new Message();
                        bitmapMessage.what = RESPONSE_PICTURE;
                        new DownloadFileUtil().downloadFile(mBitmapUrls, mBitmaps, mHandler, bitmapMessage);
                        break;
                    case RESPONSE_PICTURE:
                        for (int i =0; i < mBitmaps.size(); i++){
                            mProducts.get(i).setBitmap(mBitmaps.get(i));
                        }
                        mRecyclerProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerProduct.setAdapter(new ProductAdapter());
                        break;
                }
            }
        };
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String count = mCountPrice.getText().toString();
                if(count.equals("选点什么吧...")){
                    Toast.makeText(getContext(),"选点什么再结算啊！",Toast.LENGTH_SHORT).show();
                }
                else{
                    ProductLab.getInstance().setProductResponseModels(mProducts);
                    Intent intent = new Intent(getActivity(), SettleActivity.class);
                    intent.putExtra("count_price",count);
                    startActivity(intent);
                }
            }
        });
        Message getDetailMessage = new Message();
        getDetailMessage.what = RESPONSE_GETDETAIL;
        Map<String, String> map = new HashMap<>();
        map.put("storeId", mStoreResponseModel.getId());
        new HttpConnectionUtil().getConnection(mGson.toJson(map), GET_DETAIL_URL, mHandler, getDetailMessage);
        return v;
    }

    private class ClassfiyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView classfiyView;
        private ClassfiyResponseModel mClassfiyModel;

        public ClassfiyHolder(View itemView) {
            super(itemView);
            classfiyView = (TextView) itemView.findViewById(R.id.text_classfiyName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            smoothMoveToPosition(mRecyclerProduct, mClassfiyModel.getIndex());
        }
    }

    private class ClassifyAdapter extends RecyclerView.Adapter<ClassfiyHolder>{

        List<ClassfiyResponseModel> models;

        public ClassifyAdapter(List<ClassfiyResponseModel> classfiyModels){
            models = classfiyModels;
        }

        @Override
        public ClassfiyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.classfiy_item, parent, false);
            return new ClassfiyHolder(view);
        }

        @Override
        public void onBindViewHolder(ClassfiyHolder holder, int position) {
            holder.classfiyView.setText(models.get(position).getName());
            holder.mClassfiyModel = models.get(position);
        }

        @Override
        public int getItemCount() {
            return models.size();
        }
    }

    private class ProductHolder extends RecyclerView.ViewHolder{
        private ProductResponseModel productResponseModel;
        private ImageView mImageProduct;
        private ImageView mImageAdd;
        private ImageView mImageReduce;
        private TextView mNameView, mCountView, mPriceView, mStockView, mTextDesc;
        private int mPosition;
        private int count = 0;

        public ProductHolder(View itemView) {
            super(itemView);
            mImageProduct = (ImageView) itemView.findViewById(R.id.image_product);
            mImageAdd = (ImageView) itemView.findViewById(R.id.image_add);
            mImageReduce = (ImageView) itemView.findViewById(R.id.image_reduce);
            mNameView = (TextView) itemView.findViewById(R.id.product_name);
            mPriceView = (TextView) itemView.findViewById(R.id.product_price);
            mCountView = (TextView) itemView.findViewById(R.id.countView);
            mStockView = (TextView) itemView.findViewById(R.id.text_stock);
            mTextDesc = (TextView) itemView.findViewById(R.id.text_desc);
            mImageAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int stock = Integer.valueOf(mStockView.getText().toString());
                    if(stock == 0){
                        Toast.makeText(getContext(), "库存不足", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mSendButton.setBackgroundColor(getResources().getColor(R.color.limegreen));
                        if (count == 0) {
                            mImageReduce.setImageDrawable(getResources().getDrawable(R.drawable.reduce));
                            mImageReduce.setEnabled(true);
                        }
                        count = count + 1;
                        mCountView.setText(String.valueOf(count));
                        //将单例中的对应商品加一
                        mProducts.get(mPosition).setNumber(count);
                        mStockView.setText(String.valueOf(stock - 1));
                        String oldCountPrice = mCountPrice.getText().toString();
                        if (oldCountPrice.equals("选点什么吧...")) {
                            oldCountPrice = "0";
                        } else {
                            oldCountPrice = oldCountPrice.substring(oldCountPrice.indexOf("￥") + 1);
                        }

                        BigDecimal a = BigDecimal.valueOf(Double.valueOf(oldCountPrice));
                        BigDecimal b = BigDecimal.valueOf(productResponseModel.getPrice());
                        String counts  = a.add(b).toString();
                        mCountPrice.setText("￥" + counts);
                        add(mImageAdd);
                    }
                }
            });

            mImageReduce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int stock = Integer.valueOf(mStockView.getText().toString());
                    String oldCountPrice = mCountPrice.getText().toString();
                    count = count - 1;

                    if(oldCountPrice == "选点什么吧..."|| count < 0){

                    }
                    else {
                        oldCountPrice = oldCountPrice.substring(oldCountPrice.indexOf("￥") + 1);
                        BigDecimal a = BigDecimal.valueOf(Double.valueOf(oldCountPrice));
                        BigDecimal b = BigDecimal.valueOf(productResponseModel.getPrice());
                        String counts  = a.subtract(b).toString();
                        counts = counts.equals("0.0")?"选点什么吧...":"￥"+counts;
                        mCountPrice.setText(counts);
                    }

                    count = count == -1 ?0:count;
                    // 将单例中的商品减一，到商品详细页中备用
                    mProducts.get(mPosition).setNumber(count);

                    if(count == 0){
                        mImageReduce.setImageDrawable(null);
                        mImageReduce.setEnabled(false);
                        mCountView.setText("");
                        mStockView.setText(String.valueOf(stock + 1));
                    }
                    else{
                        mCountView.setText(String.valueOf(count));
                        mStockView.setText(String.valueOf(stock + 1));
                    }

                    if(mCountView.getText().toString() == "选点什么吧..."){
                        mSendButton.setBackgroundColor(Color.LTGRAY);
                    }

                }
            });
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {

        public ProductAdapter(){

        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, int position) {
            ProductResponseModel model = mProducts.get(position);
            holder.productResponseModel = model;
            holder.mPosition = position;
            holder.mImageProduct.setImageBitmap(mBitmaps.get(position));
            holder.mNameView.setText(model.getName());
            holder.mTextDesc.setText(model.getDetail());
            holder.mStockView.setText(String.valueOf(model.getStock()));
            holder.mPriceView.setTextColor(Color.RED);
            holder.mPriceView.setText("￥" + String.valueOf(model.getPrice()));
        }

        @Override
        public int getItemCount() {
            return mProducts.size();
        }
    }
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {

        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));

        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));

        if (position < firstItem) {

            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {

            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            mRecyclerView.smoothScrollToPosition(position);
            mShouldScroll = true;
            mToPosition = position;
        }
    }

    public void add(final View view) {

        int[] startPosition = new int[2];
        int[] endPosition = new int[2];

        view.getLocationOnScreen(startPosition);
        carView.getLocationOnScreen(endPosition);

        System.out.println(startPosition[0]+"-------"+startPosition[1]);
        System.out.println(endPosition[0]+"-------"+endPosition[1]);
        startPosition[0] = startPosition[0] -30;
        startPosition[1] = startPosition[1] - 240;
        endPosition[0] = endPosition[0] + 70;
        PointF startF = new PointF();
        PointF endF = new PointF();
        PointF controllF = new PointF();


        startF.x = startPosition[0];
        startF.y = startPosition[1] ;
        endF.x = endPosition[0];
        endF.y = endPosition[1];
        controllF.x = endF.x;
        controllF.y = startF.y;

        final ImageView imageView = new ImageView(mActivity);
        mLinearLayout.addView(imageView);
        imageView.setImageResource(ic_input_add);
        imageView.getLayoutParams().width = 80;
        imageView.getLayoutParams().height = 80;
        imageView.setVisibility(View.VISIBLE);
        imageView.setX(startF.x);
        imageView.setY(startF.y);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BezierTypeEvaluator(controllF), startF, endF);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                imageView.setX(pointF.x);
                imageView.setY(pointF.y);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                imageView.setVisibility(View.GONE);
                mLinearLayout.removeView(imageView);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        ObjectAnimator objectAnimatorX = new ObjectAnimator().ofFloat(carView , "scaleX" , 0.8f, 1.0f);
        ObjectAnimator objectAnimatorY = new ObjectAnimator().ofFloat(carView , "scaleY" , 0.8f, 1.0f);
        objectAnimatorX.setInterpolator(new AccelerateInterpolator());
        objectAnimatorY.setInterpolator(new AccelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.play(objectAnimatorX).with(objectAnimatorY).after(valueAnimator);

        set .setDuration(500);
        set .start();
    }
}
