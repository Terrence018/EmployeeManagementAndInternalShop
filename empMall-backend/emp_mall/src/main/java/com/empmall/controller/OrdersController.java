package com.empmall.controller;

import com.empmall.pojo.BatchOrderRequest;
import com.empmall.pojo.OrderRequest;
import com.empmall.pojo.Orders;
import com.empmall.pojo.Result;
import com.empmall.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersService orderService;

    /**
     * 員工兌換商品
     * POST /orders/exchange
     */
    @PostMapping("/exchange")
    public Result exchange(@RequestBody OrderRequest orderRequest) {
        log.info("接收到兌換請求: {}", orderRequest);
        try {
            ordersService.exchange(
                    orderRequest.getEmpId(),
                    orderRequest.getProductId(),
                    orderRequest.getDeliveryMethod(),
                    orderRequest.getAddress()
            );
            return Result.success(); // 兌換成功
        } catch (Exception e) {
            log.error("兌換失敗", e);
            return Result.error(e.getMessage()); // 回傳錯誤訊息 (例如: 餘額不足)
        }
    }

    /**
     * 查詢我的訂單 (員工用)
     * GET /orders/my/{empId}
     */
    @GetMapping("/my/{empId}")
    public Result listMyOrders(@PathVariable Integer empId) {
        log.info("查詢員工訂單, ID: {}", empId);
        List<Orders> list = ordersService.listMyOrders(empId);
        return Result.success(list);
    }

    /**
     * 查詢所有訂單 (管理員用)
     * GET /orders/all
     */
    @GetMapping("/all")
    public Result listAllOrders() {
        log.info("管理員查詢所有訂單");
        List<Orders> list = ordersService.listAllOrders();
        return Result.success(list);
    }

    /**
     * 更新訂單狀態 (管理員發貨/結案用)
     * PUT /orders/{id}/{status}
     */
    @PutMapping("/{id}/{status}")
    public Result updateStatus(@PathVariable Integer id, @PathVariable Integer status) {
        log.info("更新訂單ID: {}, 狀態變更為: {}", id, status);
        ordersService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/batchExchange")
    public Result batchExchange(@RequestBody BatchOrderRequest request) {
        log.info("接收到批量兌換請求: {}", request);
        try {
            ordersService.batchExchange(request);
            return Result.success();
        } catch (Exception e) {
            log.error("批量兌換失敗", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 專門處理取消邏輯：退款、補庫存、寫日誌
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id) {
        log.info("管理員執行取消訂單操作, ID: {}", id);
        try {
            // 呼叫包含業務邏輯的 cancel 方法
            ordersService.cancel(id);
            return Result.success();
        } catch (Exception e) {
            log.error("取消訂單失敗", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/report/top10")
    public Result reportTop10() {
        return Result.success(orderService.getTop10Products());
    }

    @GetMapping("/report/category")
    public Result reportCategory() {
        return Result.success(orderService.getCategorySales());
    }

    @GetMapping("/report/trend")
    public Result reportTrend() {
        return Result.success(orderService.getSalesTrend());
    }

}