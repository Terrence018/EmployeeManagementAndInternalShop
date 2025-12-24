package com.empmall.utils;

public class CurrentHolder {
    // 維護兩個 ThreadLocal，一個存 ID，一個存 Role
    private static final ThreadLocal<Integer> idTL = new ThreadLocal<>();
    private static final ThreadLocal<Integer> roleTL = new ThreadLocal<>();

    // ID的操作
    public static void setCurrentId(Integer id){
        idTL.set(id);
    }
    public static Integer getCurrentId(){
        return idTL.get();
    }

    // Role的操作
    public static void setCurrentRole(Integer role){
        roleTL.set(role);
    }
    public static Integer getCurrentRole(){
        return roleTL.get();
    }

    // 清除
    public static void remove(){
        idTL.remove();
        roleTL.remove();
    }
}