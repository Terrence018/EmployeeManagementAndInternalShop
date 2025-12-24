package com.empmall.service.impl;

import com.empmall.mapper.DeptMapper;
import com.empmall.pojo.Dept;
import com.empmall.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {
    @Autowired
    private DeptMapper deptMapper;
    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {
         deptMapper.deleteById(id);
    }

    @Override
    public void add(Dept dept) {
        //1.補全基礎屬性
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        //2.調用Mapper接口方法插入數據
        deptMapper.insert(dept);
    }

    @Override
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }

    @Override
    public void update(Dept dept) {
        //1. 補全基礎屬性
        dept.setUpdateTime(LocalDateTime.now());

        //2. 調用Mapper接口方法更新部門
        deptMapper.update(dept);


    }
}
