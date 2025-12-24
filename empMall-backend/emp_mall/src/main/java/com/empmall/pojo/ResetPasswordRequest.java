package com.empmall.pojo;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String code;        // 驗證碼
    private String newPassword; // 新密碼
}
