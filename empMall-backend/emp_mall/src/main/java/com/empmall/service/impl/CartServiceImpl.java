package com.empmall.service.impl;

import com.empmall.mapper.CartMapper;
import com.empmall.pojo.Cart;
import com.empmall.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// CartServiceImpl.java
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;

    @Override
    public void add(Cart cart) {
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        if(cart.getQuantity() == null) cart.setQuantity(1);
        cartMapper.addToCart(cart);
    }

    @Override
    public List<Cart> list(Integer empId) {
        return cartMapper.listCart(empId);
    }

    @Override
    public void delete(Integer id) {
        cartMapper.deleteById(id);
    }
}