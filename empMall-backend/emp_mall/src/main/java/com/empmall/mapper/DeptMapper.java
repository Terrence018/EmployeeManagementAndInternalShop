package com.empmall.mapper;

import com.empmall.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeptMapper {

    //查詢所有部門數據
    //application.yml配置文件，添加mybatis.configuration.map-underscore-to-camel-case=true
    @Select("select id, name, create_time createTime, update_time updateTime from dept order by update_time desc")
    List<Dept> findAll();

    //根據ID刪除部門
    @Delete("delete from dept where id = #{id}")
    void deleteById(Integer id);

    //新增部門
    @Insert("insert into dept(name, create_time, update_time) values(#{name}, #{createTime}, #{updateTime})")
    void insert(Dept dept);

    //根據ID查詢部門數據
    @Select("select id, name, create_time, update_time from dept where id = #{id}")
    Dept getById(Integer id);

    //更新部門
    @Update("update dept set name = #{name}, update_time = #{updateTime} where id = #{id}")
    void update(Dept dept);
}

