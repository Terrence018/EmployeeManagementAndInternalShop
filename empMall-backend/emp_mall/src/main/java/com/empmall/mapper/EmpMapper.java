package com.empmall.mapper;

import com.empmall.pojo.Emp;
import com.empmall.pojo.EmpQueryParam;
import com.empmall.pojo.EmpReportVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;


//員工信息
@Mapper
public interface EmpMapper {

    /**
     * 查詢員工資訊
     */
public List<Emp> list(EmpQueryParam empQueryParam);

    /**
     * 新增員工基本信息
     */
    void insert(Emp emp);

    /**
     * 根據ID刪除員工基本信息
     */
    void deleteByIds(List<Integer> ids);


    /**
     * 根據ID查詢員工基本信息 及 工作經歷信息
     */
    Emp getById(Integer id);


    /**
     * 根據ID更新員工基本信息
     */
    void updateById(Emp emp);


    /**
     * 統計員工職位人
     */
    @MapKey("pos")
    List<Map<String,Object>> countEmpJobData();


    /**
     * 統計員工性別人數
     */
    @MapKey("name")
    List<Map<String, Object>> getEmpGenderData();


    /**
     * 根據用戶名稱和密碼查詢員工信息
     */
    @Select("select id, username, name, role, points from emp where username=#{username} and password=#{password} and status = 1")
    Emp selectByUsernameAndPassword(Emp emp);

    /**
     * 根據ID更新員工點數
     */
    @Update("update emp set points = #{points} where id = #{id}")
    void updatePoints(Integer id, Integer points);

    /**
     * 根據ID查詢員工現在的點數 (為了安全，兌換前要再查一次餘額)
     */
    @Select("select points from emp where id = #{id}")
    Integer getPoints(Integer id);

    /**
     * 統計各性別員工人數
     * 使用 IF 函數將數字性別轉為文字
     */
    @Select("SELECT IF(gender = 1, '男', IF(gender = 2, '女', '未設定')) AS categoryName, COUNT(*) AS count " +
            "FROM emp " +
            "GROUP BY gender")
    List<EmpReportVO> countByGender();

    /**
     * 統計各部門員工人數
     * 使用 LEFT JOIN 連接部門表，並使用 IFNULL 處理沒有部門的員工
     */
    @Select("SELECT IFNULL(d.name, '未分配部門') AS categoryName, COUNT(e.id) AS count " +
            "FROM emp e " +
            "LEFT JOIN dept d ON e.dept_id = d.id " +
            "GROUP BY categoryName")
    List<EmpReportVO> countByDept();


    /**
     * 更新員工點數餘額 (原子操作)
     * @param id 員工ID
     * @param points 變動的點數 (正數加，負數減)
     */
    @Update("UPDATE emp SET points = IFNULL(points, 0) + #{points} WHERE id = #{id}")
    void updatePointsBalance(@Param("id") Integer id, @Param("points") Integer points);

    /**
     * 專門給員工用的更新方法 (只改手機、密碼、更新時間)
     */
    void updatePersonal(Emp emp);



    // 1. 根據 Email 查詢員工 (確認員工是否存在)
    @Select("select * from emp where email = #{email}")
    Emp getByEmail(String email);

    // 2. 修改密碼 (根據 Email 更新)
    @Update("update emp set password = #{newPassword}, update_time = now() where email = #{email}")
    void updatePasswordByEmail(String email, String newPassword);


    /**
     * 根據ID列表批量查詢員工 (用於刪除前的日誌記錄)
     */
    List<Emp> selectBatchIds(@Param("ids") List<Integer> ids);


    /**
     * 1. 統計薪資區間
     * (因為 SQL 較長，建議寫在 XML 裡，或者這裡用 @Select 也可以，下面 XML 範例會寫在 XML)
     */
    List<EmpReportVO> countBySalary();

    /**
     * 2. 統計點數排行榜 (取前 10 名)
     * 這裡我們把「員工姓名」對應到 EmpReportVO 的 categoryName
     * 把「點數」對應到 EmpReportVO 的 count
     */
    @Select("SELECT name AS categoryName, points AS count FROM emp ORDER BY points DESC LIMIT 10")
    List<EmpReportVO> getPointsRank();

    /**
     * 3. 統計各年度入職人數 (趨勢圖)
     * 這裡我們把「年份」轉成字串，對應到 categoryName
     */
    @Select("SELECT DATE_FORMAT(entry_date, '%Y') AS categoryName, COUNT(*) AS count " +
            "FROM emp " +
            "WHERE entry_date IS NOT NULL " +
            "GROUP BY DATE_FORMAT(entry_date, '%Y') " + // <--- 確保這裡跟 SELECT 的表達式一模一樣
            "ORDER BY categoryName ASC")
    List<EmpReportVO> countByEntryYear();



}
