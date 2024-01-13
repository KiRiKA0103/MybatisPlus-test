package com.itheima.mp.service;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.mp.domain.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private IUserService userService;
    @Test
    void testPageQuery() {
        // 构建分页查询条件 new Page()的两个参数分别是：页码、每页大小
        Page<User> page = new Page<>(2, 2);
        page.addOrder(new OrderItem("balance", false));

        // 1.分页查询
        Page<User> p = userService.page(page);
        // 2.总条数
        System.out.println("total = " + p.getTotal());
        // 3.总页数
        System.out.println("pages = " + p.getPages());
        // 4.数据
        List<User> records = p.getRecords();
        records.forEach(System.out::println);
    }

}