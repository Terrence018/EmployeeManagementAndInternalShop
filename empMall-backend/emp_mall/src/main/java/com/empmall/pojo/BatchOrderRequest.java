package com.empmall.pojo;

import lombok.Data;

import java.util.List;

//批量下單請求
@Data
public class BatchOrderRequest {
    private Integer empId;
    private List<CartItemRequest> items;
    private Integer deliveryMethod;
    private String address;

    //購物車項
    @Data
    public static class CartItemRequest {
        private Integer productId;
        private Integer quantity;
    }
}