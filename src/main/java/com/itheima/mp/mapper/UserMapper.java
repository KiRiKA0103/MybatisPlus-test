package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.mp.domain.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author kirika
 * @since 2024-01-13
 */
public interface UserMapper extends BaseMapper<User> {

    @Update("UPDATE user SET balance = balance - #{amount} ${ew.customSqlSegment}")
    void deductBalanceByIds(@Param("amount") Integer amount, @Param("ew") QueryWrapper<User> wrapper);

    @Update("UPDATE user SET balance = balance - #{money} WHERE id = #{id}")
    void deductBalanceById(@Param("id") Long id,@Param("money") Integer money);
}
