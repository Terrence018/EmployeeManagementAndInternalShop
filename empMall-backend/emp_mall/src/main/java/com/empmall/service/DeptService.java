package com.empmall.service;

import com.empmall.pojo.Dept;

import java.util.List;

public interface DeptService {
    //查詢所有的部門
    List<Dept> findAll();

    //根據id刪除部門
    void deleteById(Integer id);

    //新增部門
    void add(Dept dept);

    //根據ID查詢數據
    Dept getById(Integer id);

    //修改部門
    void update(Dept dept);
}
