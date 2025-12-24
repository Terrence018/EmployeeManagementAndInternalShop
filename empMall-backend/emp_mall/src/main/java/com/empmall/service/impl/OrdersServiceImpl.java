package com.empmall.service.impl;

import com.empmall.mapper.*;
import com.empmall.pojo.*;
import com.empmall.service.OrdersService;
import com.empmall.utils.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired private OrdersMapper ordersMapper;
    @Autowired private ProductsMapper productsMapper;
    @Autowired private EmpMapper empMapper;
    @Autowired private PointsLogMapper pointsLogMapper;
    @Autowired private CartMapper cartMapper;
    @Autowired private OrderItemMapper orderItemMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchange(Integer empId, Integer productId, Integer deliveryMethod, String address) {
        // 1. 查詢商品資訊
        Products product = productsMapper.getById(productId);
        if (product == null || product.getStatus() != 1) { // 建議加上商品狀態判斷(是否上架)
            throw new RuntimeException("商品不存在或已下架");
        }

        // 2. 查詢員工目前點數
        Integer currentPoints = empMapper.getPoints(empId);
        if (currentPoints == null) {
            throw new RuntimeException("員工帳號異常");
        }

        // 3. 檢查餘額
        if (currentPoints < product.getPointsNeeded()) {
            throw new RuntimeException("點數不足，無法兌換！");
        }

        // 4. 計算扣款與餘額
        int cost = product.getPointsNeeded();
        int leftPoints = currentPoints - cost;

        // 5. 更新員工點數 (建議判斷 update 返回行數，防止併發扣成負數)
        empMapper.updatePoints(empId, leftPoints);

        // 6. 扣除庫存 (利用資料庫行鎖防止超賣)
        int affectedRows = productsMapper.reduceStock(productId, 1);
        if (affectedRows == 0) {
            throw new RuntimeException("搶購失敗！商品剛剛好賣完了");
        }

        // 7. 寫入點數日誌
        PointsLog log = new PointsLog();
        log.setEmpId(empId);
        log.setType(2);               // 2=減少
        log.setPoints(-cost);          // 消耗點數
        log.setBalance(leftPoints);   // 剩餘餘額
        log.setInfo("商城兌換: " + product.getName());
        log.setCreateTime(LocalDateTime.now());
        log.setOperatorId(empId);

        pointsLogMapper.insert(log);

        // 8. 建立訂單主表
        Orders order = new Orders();
        order.setEmpId(empId);
        order.setTotalPoints(cost);
        order.setStatus(1);           // 1: 待處理
        order.setDeliveryMethod(deliveryMethod);
        order.setAddress(address);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        ordersMapper.insert(order);

        // 9. 建立訂單明細
        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(productId);
        item.setProductName(product.getName());
        item.setImage(product.getImage());
        item.setQuantity(1);
        item.setPointsPerItem(product.getPointsNeeded());

        orderItemMapper.insert(item);
    }

    @Override
    public List<Orders> listMyOrders(Integer empId) {
        return ordersMapper.findByEmpId(empId);
    }

    @Override
    public List<Orders> listAllOrders() {
        return ordersMapper.findAll();
    }

    @Override
    public void updateStatus(Integer id, Integer status) {
        ordersMapper.updateStatus(id, status, LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchExchange(BatchOrderRequest request) {
        // 1. 準備數據
        int totalPoints = 0;
        List<OrderItem> itemsToSave = new ArrayList<>();

        // 2. 預計算總額並檢查庫存
        for (var cartItem : request.getItems()) {
            Products product = productsMapper.getById(cartItem.getProductId());

            // 嚴謹檢查：商品是否存在、是否上架、庫存是否足夠
            if (product == null || product.getStatus() != 1) {
                throw new RuntimeException("包含已下架或不存在的商品");
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("商品 " + product.getName() + " 庫存不足");
            }

            int itemCost = product.getPointsNeeded() * cartItem.getQuantity();
            totalPoints += itemCost;

            // 準備明細數據
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setImage(product.getImage());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPointsPerItem(product.getPointsNeeded());
            itemsToSave.add(orderItem);
        }

        // 3. 扣除員工點數
        Integer currentPoints = empMapper.getPoints(request.getEmpId());
        if (currentPoints == null || currentPoints < totalPoints) {
            throw new RuntimeException("總點數不足");
        }

        int newBalance = currentPoints - totalPoints;
        empMapper.updatePoints(request.getEmpId(), newBalance);

        // 4. 寫入點數日誌
        PointsLog log = new PointsLog();
        log.setEmpId(request.getEmpId());
        log.setType(2);
        log.setPoints(-totalPoints);
        log.setBalance(newBalance);
        log.setInfo("商城兌換：購物車批量兌換");
        log.setCreateTime(LocalDateTime.now());
        log.setOperatorId(request.getEmpId());

        pointsLogMapper.insert(log);

        // 5. 插入訂單主表
        Orders order = new Orders();
        order.setEmpId(request.getEmpId());
        order.setTotalPoints(totalPoints);
        order.setStatus(1); // 1: 待發貨
        order.setDeliveryMethod(request.getDeliveryMethod());
        order.setAddress(request.getAddress());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        ordersMapper.insert(order);

        // 6. 迴圈插入明細表 & 扣庫存
        for (OrderItem item : itemsToSave) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);

            // 再次檢查並扣除庫存 (雙重保險)
            int rows = productsMapper.reduceStock(item.getProductId(), item.getQuantity());
            if (rows == 0) {
                // 這裡拋出異常會觸發 @Transactional 回滾，前面的扣點數、建訂單都會取消
                throw new RuntimeException("商品 " + item.getProductName() + " 庫存變動，結帳失敗");
            }
        }

        // 7. 清空購物車
        cartMapper.clearCart(request.getEmpId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Integer orderId) {
        // 1. 查詢訂單
        Orders order = ordersMapper.getById(orderId);
        if (order == null) {
            throw new RuntimeException("訂單不存在");
        }

        // 邏輯修正：通常只有狀態 1 (待發貨) 可以取消
        // 如果狀態是 2 (已發貨) 或 3 (已完成)，通常不允許直接 Cancel
        if (order.getStatus() != 1) {
            throw new RuntimeException("訂單狀態無法取消 (僅待處理訂單可取消)");
        }

        // 2. 修改訂單狀態為「已取消」(4)
        ordersMapper.updateStatus(orderId, 4, LocalDateTime.now());

        // 3. 庫存回補
        List<OrderItem> items = orderItemMapper.getByOrderId(orderId);
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                productsMapper.increaseStock(item.getProductId(), item.getQuantity());
            }
        }

        // 4. 點數退還
        Integer empId = order.getEmpId();
        Integer refundPoints = order.getTotalPoints();

        Integer currentPoints = empMapper.getPoints(empId);
        int newBalance = currentPoints + refundPoints;

        empMapper.updatePoints(empId, newBalance);

        // 5. 寫入點數變動日誌
        Integer operatorId = CurrentHolder.getCurrentId();
        if (operatorId == null) operatorId = empId; // 如果找不到當前操作者，記錄為員工本人(如果是員工自己取消)

        PointsLog log = new PointsLog();
        log.setEmpId(empId);
        log.setType(1);               // 1=增加 (退款)
        log.setPoints(refundPoints);
        log.setBalance(newBalance);
        log.setInfo("訂單取消退款 (訂單號: " + orderId + ")");
        log.setCreateTime(LocalDateTime.now());
        log.setOperatorId(operatorId);

        pointsLogMapper.insert(log);
    }

    // 統一使用 ordersMapper
    @Override
    public List<EmpReportVO> getTop10Products() {
        return ordersMapper.getTop10Products();
    }
    @Override
    public List<EmpReportVO> getCategorySales() {
        return ordersMapper.getCategorySales();
    }
    @Override
    public List<EmpReportVO> getSalesTrend() {
        return ordersMapper.getSalesTrend();
    }
}