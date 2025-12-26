package com.empmall.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data

public class Emp {
    private Integer id; //ID,主鍵
    private String username; //用戶名(帳號)
    private String password; //密碼
    private String name; //姓名
    private Integer gender; //性别, 1:男, 2:女
    private String phone; //手機號碼
    private String job; //工作
    private Integer salary; //薪水
    private String image; //頭像
    private LocalDate entryDate; //入職日期
    private Integer deptId; //部門ID
    private LocalDateTime createTime; //創建時間
    private LocalDateTime updateTime; //修改時間

    //封裝部門名稱
    private String deptName;

    //封裝工作信息
    private List<EmpExpr> exprList;

    // 在原本的 salary 下面加入：
    private Integer role;   // 1 管理員, 2 員工
    private Integer points; // 員工點數

    private String email; //信箱
    private String code;

    private Integer status; // 狀態 1:在職 0:離職

}
