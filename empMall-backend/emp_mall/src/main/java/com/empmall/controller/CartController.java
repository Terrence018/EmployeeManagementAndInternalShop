package com.empmall.controller;

import com.empmall.pojo.Cart;
import com.empmall.pojo.Result;
import com.empmall.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 加入購物車
     */
    @PostMapping("/add")
    public Result add(@RequestBody Cart cart) {
        log.info("加入購物車: {}", cart);
        cartService.add(cart);
        return Result.success();
    }

    /**
     * 展示購物車
     */
    @GetMapping("/list")
    public Result list(Integer empId) {
        List<Cart> list = cartService.list(empId);
        return Result.success(list);
    }

    /**
     * 刪除購物車
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        cartService.delete(id);
        return Result.success();
    }
}