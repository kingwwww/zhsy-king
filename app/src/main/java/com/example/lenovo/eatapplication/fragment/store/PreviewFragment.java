package com.example.lenovo.eatapplication.fragment.store;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.store.ManageClassfiyActivity;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.model.requestModel.EnterModel;
import com.example.lenovo.eatapplication.model.requestModel.ProductModel;
import com.example.lenovo.eatapplication.model.resModel.ClassfiyResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ProductResponseModel;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.util.DownloadFileUtil;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.example.lenovo.eatapplication.util.UploadFileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviewFragment extends Fragment{

    private VideoView mVideoShow;
    private RecyclerView mRecyclerViewClassfiy, mRecyclerViewProduct;
    private TextView mViewToClassfiy, mStoreName;
    private Spinner mSpinnerClassfiy;
    private List<String> ls = new LinkedList<>();
    private ArrayAdapter<String> adapter;
    private ImageButton mImageUpdate;
    private File mFile;

    private List<ClassfiyResponseModel> mClassfiyResponseModels;
    private List<ProductResponseModel> mProducts = new LinkedList<>();
    private List<Bitmap> mBitmaps = new LinkedList<>();
    private List<String> mBitmapUrls = new LinkedList<>();

    private Gson mGson = GsonBuilder.newInstance();
    private Handler mHandler;
    private StoreLab mStoreLab;
    private StoreResponseModel mStoreResponseModel;
    private ProductModel mProductModel = new ProductModel();

    private static final String VIDEO_BASE_PATH = "http://112.124.13.208:8080/media/video/";
    private static final String GET_DETAIL_URL = "http://112.124.13.208:8080/Eat_war/api/store/getDetail";
    private static final String DELETE_PRODUCT_URL = "http://112.124.13.208:8080/Eat_war/api/product/delete";
    private static final String UPDATE_PRODUCT_URL = "http://112.124.13.208:8080/Eat_war/api/product/update";

    private static final int RESPONSE_GETDETAIL = 0;
    private static final int RESPONSE_UPDATE_PRODUCT = 1;
    private static final int RESPONSE_DELETE_PRODUCT = 2;
    private static final int RESPONSE_PORTRAIT = 3;
    private static final int RESPONSE_PICTURE = 40;

    private static Boolean mShouldScroll = false;
    private static int mToPosition;
    private int current = 0;



    public PreviewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_preview, container, false);
        mStoreLab = StoreLab.getInstance();
        mStoreResponseModel = mStoreLab.getStoreResponseModel();
        mVideoShow = (VideoView) v.findViewById(R.id.video_show);
        mRecyclerViewClassfiy = (RecyclerView) v.findViewById(R.id.recyclerView_classfiy);
        mRecyclerViewProduct = (RecyclerView) v.findViewById(R.id.recycleView_product);
        mViewToClassfiy = (TextView) v.findViewById(R.id.view_manage_classfiy);
        mStoreName = (TextView) v.findViewById(R.id.text_store_name);
        mViewToClassfiy = (TextView) v.findViewById(R.id.view_manage_classfiy);

        mRecyclerViewProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll){
                    mShouldScroll = false;
                    smoothMoveToPosition(mRecyclerViewProduct,mToPosition);
                }
            }
        });

        String uri = VIDEO_BASE_PATH + mStoreResponseModel.getDesVideo();
        mVideoShow.setVideoURI(Uri.parse(uri));
        mVideoShow.requestFocus();
        mVideoShow.start();
        mVideoShow.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoShow.start();
            }
        });

        mStoreName.setText("店家名称：" + mStoreResponseModel.getName());
        mViewToClassfiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ManageClassfiyActivity.class);
                startActivity(intent);
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
                        mRecyclerViewClassfiy.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerViewClassfiy.setAdapter(new ClassifyAdapter(mClassfiyResponseModels));
                        Message bitmapMessage = new Message();
                        bitmapMessage.what = RESPONSE_PORTRAIT;
                        new DownloadFileUtil().downloadFile(mBitmapUrls, mBitmaps, mHandler, bitmapMessage);
                        break;
                    case RESPONSE_PORTRAIT:
                        mRecyclerViewProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mRecyclerViewProduct.setAdapter(new ProductAdapter(mProducts, mBitmaps));
                        break;

                    case RESPONSE_DELETE_PRODUCT:
                        break;

                    case RESPONSE_UPDATE_PRODUCT:

                        break;
                }
            }
        };
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
            smoothMoveToPosition(mRecyclerViewProduct, mClassfiyModel.getIndex());
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

        private ImageView mImageView;
        private TextView mTextName, mTextPrice, mTextStock, mTextDesc, mTextUpdate, mTextDelete;

        public ProductHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.pt_show_picture);
            mTextName = (TextView) itemView.findViewById(R.id.pt_show_name);
            mTextPrice = (TextView) itemView.findViewById(R.id.pt_show_price);
            mTextStock = (TextView) itemView.findViewById(R.id.pt_show_stock);
            mTextDesc = (TextView) itemView.findViewById(R.id.pt_show_desc);
            mTextUpdate = (TextView) itemView.findViewById(R.id.pt_update);
            mTextDelete = (TextView) itemView.findViewById(R.id.pt_delete);
        }

    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {

        List<ProductResponseModel> models;
        List<Bitmap> bitmaps;

        public ProductAdapter(List<ProductResponseModel> mProducts, List<Bitmap> mBitmaps){
            models = mProducts;
            bitmaps = mBitmaps;
        }

        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.product_preview_item, parent, false);
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(final ProductHolder holder, final int position) {
            final ProductResponseModel model = models.get(position);

            holder.mImageView.setImageBitmap(bitmaps.get(position));
            holder.mTextName.setText(model.getName());
            holder.mTextPrice.setText("￥"+ String.valueOf(model.getPrice()));
            holder.mTextPrice.setTextColor(Color.RED);
            holder.mTextStock.setText(String.valueOf(model.getStock()));
            holder.mTextDesc.setText(model.getDetail());

            holder.mTextUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View updateProductView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_product, null);
                    mImageUpdate = (ImageButton) updateProductView.findViewById(R.id.image_btn_pt);
                    final EditText mEditName = (EditText) updateProductView.findViewById(R.id.pt_name);
                    final EditText mEditPrice = (EditText) updateProductView.findViewById(R.id.pt_price);
                    final EditText mEditStock = (EditText) updateProductView.findViewById(R.id.pt_stock);
                    final EditText mEditDetail = (EditText) updateProductView.findViewById(R.id.pt_detail);
                    for (ClassfiyResponseModel classfiyResponseModel : mClassfiyResponseModels) {
                        ls.add(classfiyResponseModel.getName());
                    }
                    adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, ls);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerClassfiy = (Spinner) updateProductView.findViewById(R.id.pt_classfiy);
                    mSpinnerClassfiy.setAdapter(adapter);
                    mSpinnerClassfiy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            mProductModel.setClassfiyId(mClassfiyResponseModels.get(i).getClassfiyId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    mImageUpdate.setImageBitmap(((BitmapDrawable)holder.mImageView.getDrawable()).getBitmap());
                    mImageUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_PICK, null);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, RESPONSE_PICTURE);
                        }
                    });
                    mEditName.setText(holder.mTextName.getText());
                    mEditPrice.setText(holder.mTextPrice.getText().subSequence(1,holder.mTextPrice.getText().length()));
                    mEditStock.setText(holder.mTextStock.getText());
                    mEditDetail.setText(holder.mTextDesc.getText());
                    Dialog Dialog = new AlertDialog.Builder(getActivity())
                            .setView(updateProductView)
                            .setCancelable(true)
                            .setTitle("Update Product")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String uri = VIDEO_BASE_PATH + mStoreResponseModel.getDesVideo();
                                    mVideoShow.setVideoURI(Uri.parse(uri));
                                    mVideoShow.requestFocus();
                                    mVideoShow.start();
                                    mVideoShow.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            mVideoShow.start();
                                        }
                                    });
                                    String name = mEditName.getText().toString().trim();
                                    String price = mEditPrice.getText().toString().trim();
                                    String stock = mEditStock.getText().toString().trim();
                                    String detail = mEditDetail.getText().toString().trim();
                                    if (name.equals("")||price.equals("")||stock.equals("")||detail.equals("")){
                                        Toast.makeText(getContext(), "信息填写不完整！！！", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        mProductModel.setProductId(model.getId());
                                        mProductModel.setName(name);
                                        mProductModel.setPrice(Double.valueOf(price));
                                        mProductModel.setStock(Integer.valueOf(stock));
                                        mProductModel.setDetail(detail);
                                        mProductModel.setPicture(model.getPicture());
                                        Message updateMessage = new Message();
                                        updateMessage.what = RESPONSE_UPDATE_PRODUCT;
                                        new HttpConnectionUtil().getConnection(mGson.toJson(mProductModel), UPDATE_PRODUCT_URL, mHandler, updateMessage);
                                        // 不为null说明修改了图片 上传到服务器覆盖之前的图片
                                        if (mFile != null){
                                            new UploadFileUtil().uoloadFile(mFile, EnterModel.PICTURE, "", model.getPicture(), mHandler);
                                        }
                                        holder.mImageView.setImageBitmap(((BitmapDrawable)mImageUpdate.getDrawable()).getBitmap());
                                        holder.mTextName.setText(name);
                                        holder.mTextPrice.setText("￥"+ String.valueOf(price));
                                        holder.mTextPrice.setTextColor(Color.RED);
                                        holder.mTextStock.setText(String.valueOf(stock));
                                        holder.mTextDesc.setText(detail);
                                        Toast.makeText(getContext(), "修改成功~~", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).create();
                    Dialog.show();
                }
            });
            holder.mTextDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> map = new HashMap<>();
                    map.put("productId", model.getId());
                    Message deleteProductMessage = new Message();
                    deleteProductMessage.what = RESPONSE_DELETE_PRODUCT;
                    new HttpConnectionUtil().getConnection(mGson.toJson(map), DELETE_PRODUCT_URL, mHandler, deleteProductMessage);
                    models.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return models.size();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_PICTURE) {
            if (data != null) {
                //回显图片
                Uri mUri = data.getData();
                mImageUpdate.setImageURI(mUri);
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
