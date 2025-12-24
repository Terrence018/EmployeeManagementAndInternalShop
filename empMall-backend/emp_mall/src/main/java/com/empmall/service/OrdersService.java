package com.empmall.service;

import com.empmall.pojo.BatchOrderRequest;
import com.empmall.pojo.EmpReportVO;
import com.empmall.pojo.Orders;
import java.util.List;

public interface OrdersService {
    /**
     * 兌換商品 (核心功能)
     * @param empId 員工ID
     * @param productId 商品ID
     * @param deliveryMethod 配送方式 (1自取, 2寄送)
     * @param address 地址
     */
    void exchange(Integer empId, Integer productId, Integer deliveryMethod, String address);

    // 查詢我的訂單
    List<Orders> listMyOrders(Integer empId);

    // 查詢所有訂單 (管理員用)
    List<Orders> listAllOrders();

    // 更新訂單狀態
    void updateStatus(Integer id, Integer status);

    //批量兌換商品(購物車使用)
    void batchExchange(BatchOrderRequest request);

    //取消訂單
    void cancel(Integer orderId);

    List<EmpReportVO> getTop10Products();
    List<EmpReportVO> getCategorySales();
    List<EmpReportVO> getSalesTrend();
}