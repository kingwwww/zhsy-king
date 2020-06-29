package com.example.lenovo.eatapplication.model.resModel;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class ResponseModel<M> implements Serializable {
    // 成功
    public static final int SUCCEED = 1;
    // 未知错误
    public static final int ERROR_UNKNOWN = 0;

    // 没有找到用户信息
    public static final int ERROR_NOT_FOUND_USER = 4041;
    // 没有找到商店
    public static final int ERROR_NOT_FOUND_STORE = 4042;
    // 没有找到订单
    public static final int ERROR_NOT_FOUND_ORDER = 4043;
    // 没有找到商品
    public static final int ERROR_NOT_FOUND_PRODUCT = 4044;

    public static final int ERROR_NOT_FOUND_CLASSFIY = 4045;

    public static final int ERROR_NOT_FOUND_ADDRESS= 4046;


    // 请求参数错误
    public static final int ERROR_PARAMETERS = 4001;
    // 请求参数错误-已存在账户
    public static final int ERROR_PARAMETERS_EXIST_ACCOUNT = 4002;
    // 请求参数错误-已存在名称
    public static final int ERROR_PARAMETERS_EXIST_NAME = 4003;

    // 服务器错误
    public static final int ERROR_SERVICE = 5001;

    // 账户Token错误，需要重新登录
    public static final int ERROR_ACCOUNT_TOKEN = 2001;
    // 账户登录失败
    public static final int ERROR_ACCOUNT_LOGIN = 2002;
    // 账户注册失败
    public static final int ERROR_ACCOUNT_REGISTER = 2003;
    // 没有权限操作
    public static final int ERROR_ACCOUNT_NO_PERMISSION = 2010;



    @Expose
    private int code;
    @Expose
    private String message;
    @Expose
    private M result;

    public ResponseModel() {
        code = 1;
        message = "ok";
    }

    public ResponseModel(M result) {
        this();
        this.result = result;
    }

    public ResponseModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseModel(int code, String message, M result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public boolean isSucceed() {
        return code == SUCCEED;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public M getResult() {
        return result;
    }

    public void setResult(M result) {
        this.result = result;
    }

    public static <M> ResponseModel<M> buildOk() {
        return new ResponseModel<M>();
    }

    public static <M> ResponseModel<M> buildOk(M result) {
        return new ResponseModel<M>(result);
    }

    public static <M> ResponseModel<M> buildParameterError() {
        return new ResponseModel<M>(ERROR_PARAMETERS, "Parameters Error.");
    }

    public static <M> ResponseModel<M> buildNotFoundProduct() {
        return new ResponseModel<M>(ERROR_NOT_FOUND_PRODUCT, "No Product Found");
    }

    public static <M> ResponseModel<M> buildHaveAccountError() {
        return new ResponseModel<M>(ERROR_PARAMETERS_EXIST_ACCOUNT, "Already have this account.");
    }

    public static <M> ResponseModel<M> buildHaveNameError() {
        return new ResponseModel<M>(ERROR_PARAMETERS_EXIST_NAME, "Already have this name.");
    }

    public static <M> ResponseModel<M> buildServiceError() {
        return new ResponseModel<M>(ERROR_SERVICE, "Service Error.");
    }

    public static <M> ResponseModel<M> buildNotFoundUserError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_USER, str != null ? str : "Not Found UserLab.");
    }

    public static <M> ResponseModel<M> buildNotFoundStoreError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_STORE, str != null ? str : "Not Found Store.");
    }

    public static <M> ResponseModel<M> buildNotFoundOrderError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_ORDER, str != null ? str : "Not Found Order.");
    }


    public static <M> ResponseModel<M> buildNotFoundAddressError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_ADDRESS, str != null ? str : "Not Found Address.");
    }

    public static <M> ResponseModel<M> buildNotFoundClassfiyError(String str) {
        return new ResponseModel<M>(ERROR_NOT_FOUND_CLASSFIY, str != null ? str : "Not Found Classfiy.");
    }


    public static <M> ResponseModel<M> buildAccountError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_TOKEN, "Account Error; you need login.");
    }

    public static <M> ResponseModel<M> buildLoginError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_LOGIN, "Account or password error.");
    }

    public static <M> ResponseModel<M> buildRegisterError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_REGISTER, "Have this account.");
    }

    public static <M> ResponseModel<M> buildNoPermissionError() {
        return new ResponseModel<M>(ERROR_ACCOUNT_NO_PERMISSION, "You do not have permission to operate.");
    }

    public static <M> ResponseModel<M> buildCreateError(int type) {
        return new ResponseModel<M>(type, "Create failed.");
    }

}