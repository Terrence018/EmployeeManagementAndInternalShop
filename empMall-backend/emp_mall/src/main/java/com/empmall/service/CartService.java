package com.empmall.service;

import com.empmall.pojo.Cart;

import java.util.List;

public interface CartService {

    //添加
    void add(Cart cart);

    //修改
    List<Cart> list(Integer empId);

    //删除
    void delete(Integer id);
}
