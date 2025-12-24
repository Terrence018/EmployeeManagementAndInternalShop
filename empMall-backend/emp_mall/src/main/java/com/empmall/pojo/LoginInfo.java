package com.empmall.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封裝登錄結果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private Integer id;
    private String username;
    private String name;
    private String token;

    // 必須有這兩個新欄位
    private Integer role;    // 1管理員, 2員工
    private Integer points;  // 點數餘額

}
