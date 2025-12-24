package com.empmall.service.impl;

import com.empmall.mapper.ProductsMapper;
import com.empmall.pojo.PageBean;
import com.empmall.pojo.ProductLog;
import com.empmall.pojo.ProductQueryParam;
import com.empmall.pojo.Products;
import com.empmall.service.ProductLogService;
import com.empmall.service.ProductsService;
import com.empmall.utils.CurrentHolder;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductsServiceImpl implements ProductsService {

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private ProductLogService productLogService;

    @Override
    public PageBean<Products> list(ProductQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<Products> list = productsMapper.list(param);
        Page<Products> p = (Page<Products>) list;
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public Products getById(Integer id) {
        return productsMapper.getById(id);
    }

    // --- 輔助屬性 ---
    private static final Map<Integer, String> CATEGORY_MAP = new HashMap<>();
    static {
        CATEGORY_MAP.put(1, "3C數位");
        CATEGORY_MAP.put(2, "辦公用品");
        CATEGORY_MAP.put(3, "食品飲料");
        CATEGORY_MAP.put(4, "電子票券");
        CATEGORY_MAP.put(5, "生活百貨");
        CATEGORY_MAP.put(6, "圖書進修");
        CATEGORY_MAP.put(7, "品牌周邊");
        CATEGORY_MAP.put(8, "運動保健");
    }

    private String getCategoryName(Integer id) {
        return CATEGORY_MAP.getOrDefault(id, "未知分類(" + id + ")");
    }

    private String getStatusName(Integer status) {
        return status == 1 ? "上架" : "下架";
    }

    /**
     * 儲存商品 (包含 新增 和 修改 的判斷)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(Products products) {
        // 判斷 ID 是否為空
        if (products.getId() == null) {

            //ID為空進行新增
            products.setCreateTime(LocalDateTime.now());
            products.setUpdateTime(LocalDateTime.now());

            // 1. 執行插入 (Mapper 需使用 useGeneratedKeys 才能拿回 ID)
            productsMapper.insert(products);

            // 2. 紀錄新增日誌 (新增沒有舊資料，不需要比對)
            recordLog("新增商品 [" + products.getName() + "]");

        } else {

            // ID 不為空進行修改update

            // 這裡直接呼叫 update 方法，重用邏輯，避免代碼重複
            // 注意：同類別互叫可能會導致 Transaction 失效，但在這裡 save 已經有 @Transactional 了，所以沒問題
            this.update(products);
        }
    }

    /**
     * 更新商品 (專門處理修改邏輯)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Products products) {
        // 1. 先查詢舊資料 (比對基礎)
        Products oldProduct = productsMapper.getById(products.getId());

        if (oldProduct == null) {
            throw new RuntimeException("商品不存在，無法編輯");
        }

        // 2. 執行更新
        products.setUpdateTime(LocalDateTime.now());
        productsMapper.update(products); // 這裡用的是 Mapper 的 update (動態更新)

        // 3. 產生差異日誌
        String diff = generateProductDiff(oldProduct, products);

        // 4. 判斷是否有變更
        String logContent;
        if (diff.length() > 0) {
            logContent = "編輯商品 [" + oldProduct.getName() + "]：" + diff;
        } else {
            logContent = "編輯商品 [" + oldProduct.getName() + "] (無內容變更)";
        }

        // 5. 寫入日誌
        recordLog(logContent);
    }

    // 批量刪除
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Integer> ids) {
        // 1. 先查詢名稱
        List<Products> productsList = productsMapper.selectBatchIds(ids);
        String names = productsList.stream()
                .map(Products::getName)
                .collect(Collectors.joining(", "));

        // 2. 執行刪除
        productsMapper.deleteBatchIds(ids);

        // 3. 紀錄日誌
        String prefix = ids.size() == 1 ? "刪除商品：" : "批量刪除商品：";
        recordLog(prefix + names);
    }

    //紀錄日誌
    private void recordLog(String content) {
        try {
            Integer currentId = CurrentHolder.getCurrentId();
            if (currentId == null) currentId = 1; // 預設為管理員

            ProductLog log = new ProductLog();
            log.setOperateUser(currentId);
            log.setOperateTime(LocalDateTime.now());
            log.setContent(content);

            productLogService.insertLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //產生差異日誌
    private String generateProductDiff(Products oldP, Products newP) {
        StringBuilder changes = new StringBuilder();

        if (newP.getName() != null && !newP.getName().equals(oldP.getName())) {
            changes.append("名稱:[").append(oldP.getName()).append("→").append(newP.getName()).append("] ");
        }
        if (newP.getPointsNeeded() != null && !newP.getPointsNeeded().equals(oldP.getPointsNeeded())) {
            changes.append("所需點數:[").append(oldP.getPointsNeeded()).append("→").append(newP.getPointsNeeded()).append("] ");
        }
        if (newP.getStock() != null && !newP.getStock().equals(oldP.getStock())) {
            changes.append("庫存:[").append(oldP.getStock()).append("→").append(newP.getStock()).append("] ");
        }
        if (newP.getCategory() != null && !newP.getCategory().equals(oldP.getCategory())) {
            String oldCat = getCategoryName(oldP.getCategory());
            String newCat = getCategoryName(newP.getCategory());
            changes.append("分類:[").append(oldCat).append("→").append(newCat).append("] ");
        }
        if (newP.getStatus() != null && !newP.getStatus().equals(oldP.getStatus())) {
            String oldSt = getStatusName(oldP.getStatus());
            String newSt = getStatusName(newP.getStatus());
            changes.append("狀態:[").append(oldSt).append("→").append(newSt).append("] ");
        }
        if (newP.getDescription() != null && !newP.getDescription().equals(oldP.getDescription())) {
            changes.append("[描述已更新] ");
        }
        if (newP.getImage() != null && !newP.getImage().equals(oldP.getImage())) {
            changes.append("[圖片已更新] ");
        }

        return changes.toString();
    }
}