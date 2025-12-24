package com.empmall.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PointsLog {
    private Integer id;
    private Integer empId;
    private Integer type; // 1:增加, 2:減少
    private Integer points;
    private Integer balance;
    private String info;
    private LocalDateTime createTime;

    // 擴充屬性 (查詢時顯示員工名字用)
    private String empName;

    // 操作人 ID (存入資料庫用)
    private Integer operatorId;

    //操作人姓名 (前端顯示用，不存入資料庫)
    private String operatorName;
}