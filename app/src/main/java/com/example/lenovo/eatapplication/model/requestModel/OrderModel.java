package com.example.lenovo.eatapplication.model.requestModel;

import com.google.gson.annotations.Expose;

import java.util.List;

public class OrderModel {

    public static final int SELECT_UN_DEAL_ORDER = 0;
    public static final int SELECT_DEAL_ORDER = 1;
    public static final int SELECT_FAILED_ORDER = 2;
    public static final int SELECT_ALL_ORDER = 3;

    public static final int ROLE_USER = 0;
    public static final int ROLE_STORE = 1;

    public static final int ORDER_STATUS_DEFAULT = 0; // 未处理
    public static final int ORDER_STATUS_SUCCESS = 1; // 完成交易
    public static final int ORDER_STATUS_FAILED = 2; // 终止交易

    @Expose
    private String orderId;
    @Expose
    private String userId;
    @Expose
    private String storeId;
    @Expose
    private String name;
    @Expose
    private String phone;
    @Expose
    private String address;
    @Expose
    private Double total;
    @Expose
    List<ProductModel> productModels;
    @Expose
    private Integer status = 0;
    @Expose
    private Integer orderTag;
    @Expose
    private Integer roleTag;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<ProductModel> getProductModels() {
        return productModels;
    }

    public void setProductModels(List<ProductModel> productModels) {
        this.productModels = productModels;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrderTag() {
        return orderTag;
    }

    public void setOrderTag(Integer orderTag) {
        this.orderTag = orderTag;
    }

    public Integer getRoleTag() {
        return roleTag;
    }

    public void setRoleTag(Integer roleTag) {
        this.roleTag = roleTag;
    }
}
