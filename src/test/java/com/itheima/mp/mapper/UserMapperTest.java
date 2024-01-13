package com.itheima.mp.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.po.UserInfo;
import org.apache.ibatis.annotations.Update;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        User user = User.builder().build();
        user.setUsername(UUID.randomUUID().toString().substring(0, 8));
        user.setPassword("123");
        user.setPhone("18688990011");
        user.setBalance(200);
        user.setInfo(UserInfo.builder().age(24).intro("冒险者").gender("female").build());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Test
    void testSelectById() {
        User user = userMapper.selectById(5L);
        System.out.println("user = " + user);
    }


    @Test
    void testSelectBatchIds() {
        List<User> users = userMapper.selectBatchIds(List.of(1L, 2L, 3L, 4L));
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateById() {
        User user = User.builder().build();
        user.setId(5L);
        user.setBalance(20000);
        userMapper.updateById(user);
    }

    @Test
    void testDeleteById() {
        userMapper.deleteById(5L);
    }

    @Test
    void testSelectQueryWrapper() {
        // 1.构建查询条件 where name like "%o%" AND balance >= 1000
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .select("id", "username", "info", "balance")
                .like("username", "o")
                .ge("balance", 1000);
        // 2.查询数据
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
    }

    @Test
    void testUpdateQueryWrapper() {
        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .in("id", List.of(1L, 2L, 3L));
        User user = User.builder()
                .balance(2000)
                .info(UserInfo.builder().age(64).intro("僵尸 ").gender("female").build())
                .build();
        userMapper.update(user, wrapper);
    }


    @Test
    void testUpdateWrapper() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                .setSql("balance = balance+500")
                .le("balance", 10000);
        userMapper.update(wrapper);
    }

    @Test
    void testLambdaQueryWrapper() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .select(User::getUsername, User::getBalance, User::getInfo)
                .lt(User::getId, 5L);

        List<User> users = userMapper.selectList(wrapper);
        users.forEach(System.out::println);
    }

    @Test
    void testLambdaUpdateWrapper() {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .setSql("balance = balance-500")
                .ge(User::getBalance, 2000);
        userMapper.update(wrapper);
    }

    @Test
    void testCustomWrapper() {
        // 1.准备自定义查询条件
        List<Long> ids = List.of(1L, 2L, 4L);
        Integer amount = 200;
        // 使用wrapper构造添加
        QueryWrapper<User> wrapper = new QueryWrapper<User>().in("id", ids);

        // 2.调用mapper的自定义方法，直接传递Wrapper
        userMapper.deductBalanceByIds(amount, wrapper);
    }

}