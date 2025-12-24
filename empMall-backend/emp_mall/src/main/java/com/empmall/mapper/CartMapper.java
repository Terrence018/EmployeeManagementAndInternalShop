package com.empmall.mapper;

import com.empmall.pojo.Cart;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CartMapper {

    // 1. 加入購物車 (處理重複商品則累加數量)
    @Insert("insert into cart(emp_id, product_id, quantity, create_time, update_time) " +
            "values(#{empId}, #{productId}, #{quantity}, #{createTime}, #{updateTime}) " +
            "on duplicate key update quantity = quantity + #{quantity}, update_time = #{updateTime}")
    void addToCart(Cart cart);
    
    // 2. 查詢購物車清單 (聯表查詢商品詳細資訊)
    @Select("select c.*, p.name as productName, p.image as productImage, " +
            "p.points_needed as pointsNeeded, p.stock as stock " +
            "from cart c left join products p on c.product_id = p.id " +
            "where c.emp_id = #{empId} order by c.create_time desc")
    List<Cart> listCart(Integer empId);

    // 3. 修改商品數量
    @Update("update cart set quantity = #{quantity}, update_time = now() where id = #{id}")
    void updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    // 4. 刪除單個購物車項目
    @Delete("delete from cart where id = #{id}")
    void deleteById(Integer id);

    // 5. 清空購物車 (結帳後執行)
    @Delete("delete from cart where emp_id = #{empId}")
    void clearCart(Integer empId);
}