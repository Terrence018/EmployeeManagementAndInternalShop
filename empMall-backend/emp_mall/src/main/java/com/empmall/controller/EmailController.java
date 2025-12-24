package com.empmall.controller;

import com.empmall.pojo.Result;
import com.empmall.service.MailService; // 引入剛剛寫好的 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private MailService mailService;

    // 用來暫存驗證碼 (供 EmpController 使用)
    private static final Map<String, CodeData> CODE_STORE = new ConcurrentHashMap<>();

    // 內部類
    private static class CodeData {
        String code;
        long expireTime;
        public CodeData(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }

    /**
     * 發送驗證碼
     */
    @PostMapping("/send")
    public Result sendCode(@RequestParam String email) {
        // 生成 6 位數隨機碼
        String code = String.valueOf(new Random().nextInt(899999) + 100000);

        String subject = "【員工商城】您的驗證碼";
        String content = "<h1>您的驗證碼是：<span style='color:blue'>" + code + "</span></h1>" +
                "<p>請在 5 分鐘內輸入，切勿外洩給他人。</p>";

        // 呼叫 Service 背景寄信 (瞬間完成，不會等信寄出去)
        mailService.sendAsyncMail(email, subject, content);

        // 存入 Map
        CODE_STORE.put(email, new CodeData(code, System.currentTimeMillis() + 5 * 60 * 1000));

        return Result.success("驗證碼已在發送佇列中，請稍候查收：" + email);
    }

    /**
     * 檢查驗證碼
     */
    public static boolean checkCode(String email, String inputCode) {

        // 檢查 Map 中是否有該 email
        CodeData data = CODE_STORE.get(email);
        if (data == null) return false;
        if (System.currentTimeMillis() > data.expireTime) {
            CODE_STORE.remove(email);
            return false;
        }
        return data.code.equals(inputCode);
    }

    /**
     * 移除驗證碼
     */
    public static void removeCode(String email) {
        CODE_STORE.remove(email);
    }
}