package com.example.lenovo.eatapplication.fragment.store;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lenovo.eatapplication.R;
import com.example.lenovo.eatapplication.activity.store.ManageClassfiyActivity;
import com.example.lenovo.eatapplication.activity.store.StoreIndexActivity;
import com.example.lenovo.eatapplication.beanLab.StoreLab;
import com.example.lenovo.eatapplication.model.requestModel.ClassfiyModel;
import com.example.lenovo.eatapplication.model.requestModel.EnterModel;
import com.example.lenovo.eatapplication.model.requestModel.ProductModel;
import com.example.lenovo.eatapplication.model.resModel.ClassfiyResponseModel;
import com.example.lenovo.eatapplication.model.resModel.ResponseModel;
import com.example.lenovo.eatapplication.model.resModel.StoreResponseModel;
import com.example.lenovo.eatapplication.util.GsonBuilder;
import com.example.lenovo.eatapplication.util.HttpConnectionUtil;
import com.example.lenovo.eatapplication.util.PathBuilder;
import com.example.lenovo.eatapplication.util.UploadFileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private ImageButton mImagePicture;
    private EditText mEditName, mEditPrice, mEditStock, mEditDetail;
    private Spinner mSpinnerClassfiy;
    private Button mBtnSave, mBtnReturn;
    private File mFile;

    private Handler mHandler;
    private List<ClassfiyResponseModel> mClassfiyResponseModels;
    private ClassfiyModel mClassfiyModel;
    private ProductModel mProductModel = new ProductModel();
    private Gson mGson = GsonBuilder.newInstance();
    private StoreLab mStoreLab;
    private StoreResponseModel mStoreResponseModel;
    private List<String> ls = new LinkedList<>();
    private ArrayAdapter<String> adapter;
    private String uniqueStr;
    private String subFileUrl;

    private static final String classfiySelectUrl = "http://112.124.13.208:8080/Eat_war/api/classfiy/select";
    private static final String productAddUrl = "http://112.124.13.208:8080/Eat_war/api/product/add";
    private static final int RESPONSE_CLASSFIY = 0;
    private static final int RESPONSE_PRODUCT = 1;
    private static final int RESPONSE_PICTURE = 3;

    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);
        mImagePicture = (ImageButton) v.findViewById(R.id.image_btn_pt);
        mEditName = (EditText) v.findViewById(R.id.pt_name);
        mEditPrice = (EditText) v.findViewById(R.id.pt_price);
        mEditStock = (EditText) v.findViewById(R.id.pt_stock);
        mEditDetail = (EditText) v.findViewById(R.id.pt_detail);
        mBtnSave = (Button) v.findViewById(R.id.btn_pt_add);
        mBtnReturn = (Button) v.findViewById(R.id.btn_return);

        mStoreLab = StoreLab.getInstance();
        mStoreResponseModel = mStoreLab.getStoreResponseModel();
        mClassfiyModel = new ClassfiyModel();
        mClassfiyModel.setStoreId(mStoreResponseModel.getId());

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData();
                String dataJson = bundle.getString("dataJson");
                int code = bundle.getInt("code");
                switch (msg.what){
                    case RESPONSE_CLASSFIY:
                        if (code == ResponseModel.ERROR_NOT_FOUND_CLASSFIY){
                            Intent intent = new Intent(getActivity(), ManageClassfiyActivity.class);
                            startActivity(intent);
                        }else if (code == ResponseModel.SUCCEED){
                            Type type = new TypeToken<List<ClassfiyResponseModel>>(){}.getType();
                            mClassfiyResponseModels = mGson.fromJson(dataJson, type);
                            mProductModel.setClassfiyId(mClassfiyResponseModels.get(0).getClassfiyId());
                            for (ClassfiyResponseModel classfiyResponseModel : mClassfiyResponseModels) {
                                ls.add(classfiyResponseModel.getName());
                            }
                            adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, ls);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mSpinnerClassfiy.setAdapter(adapter);
                        }
                        break;
                    case RESPONSE_PRODUCT:
                        if (code == ResponseModel.SUCCEED){
                            new UploadFileUtil().uoloadFile(mFile, EnterModel.PICTURE, subFileUrl,uniqueStr, mHandler);
                            Toast.makeText(getContext(), "添加成功", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };

        mSpinnerClassfiy = (Spinner) v.findViewById(R.id.pt_classfiy);
        mSpinnerClassfiy.setOnItemSelectedListener(this);

        Message selectMessage = mHandler.obtainMessage();
        selectMessage.what = RESPONSE_CLASSFIY;
        new HttpConnectionUtil().getConnection(mGson.toJson(mClassfiyModel), classfiySelectUrl, mHandler, selectMessage);

        mImagePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, RESPONSE_PICTURE);
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ptName = mEditName.getText().toString().trim();
                String detail = mEditDetail.getText().toString().trim();
                String price = mEditPrice.getText().toString().trim();
                String stock = mEditStock.getText().toString().trim();
                if (mFile == null || ptName.equals("")|| detail.equals("") || price.equals("")|| stock.equals("")){
                    Toast.makeText(getContext(),"商品信息未填写完整", Toast.LENGTH_SHORT).show();
                }
                else {
                    uniqueStr = UUID.randomUUID().toString();
                    subFileUrl = PathBuilder.buildPath(uniqueStr);
                    uniqueStr = uniqueStr  + ".jpg";
                    mProductModel.setName(ptName);
                    mProductModel.setDetail(detail);
                    mProductModel.setPrice(Double.valueOf(price));
                    mProductModel.setStock(Integer.valueOf(stock));
                    mProductModel.setPicture(subFileUrl + "/" + uniqueStr);
                    Message ptAddMessage = new Message();
                    ptAddMessage.what = RESPONSE_PRODUCT;
                    new HttpConnectionUtil().getConnection(mGson.toJson(mProductModel), productAddUrl, mHandler, ptAddMessage);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mProductModel.setClassfiyId(mClassfiyResponseModels.get(i).getClassfiyId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_PICTURE) {
            if (data != null) {
                //回显图片
                Uri mUri = data.getData();
                mImagePicture.setImageURI(mUri);
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
