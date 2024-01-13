package com.itheima.mp.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.dto.UserFormDTO;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author kirika
 * @since 2024-01-13
 */
@Api(tags = "用户管理接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    @PostMapping
    @ApiOperation("新增用户")
    public void saveUser(@RequestBody UserFormDTO userFormDTO) {
        // 1.转换DTO为PO
        User user = BeanUtil.copyProperties(userFormDTO, User.class);
        // 2.新增
        userService.save(user);
    }

    @PutMapping
    @ApiOperation("更新用户")
    public void updateUser(@RequestBody UserFormDTO userFormDTO) {
        User user = BeanUtil.copyProperties(userFormDTO, User.class);

//        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
//                .eq(User::getId, user.getId())
//                .eq(User::getStatus, UserStatus.NORMAL);
//        userService.update(user,wrapper);

        // 使用lambdaUpdate更新
        userService.lambdaUpdate().eq(User::getId, user.getId()).update(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public void removeUserById(@PathVariable("id") Long userId) {
        userService.removeById(userId);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户")
    public UserVO queryUserById(@PathVariable("id") Long userId) {

//        // 1.查询用户
//        User user = userService.getById(userId);
//        // 2.处理vo
//        userVO = BeanUtil.copyProperties(user, UserVO.class);

        // 基于自定义service方法查询
        return userService.queryUserAndAddressById(userId);
    }

    @GetMapping
    @ApiOperation("根据id集合查询用户")
    public List<UserVO> queryUserByIds(@RequestParam("ids") List<Long> ids) {
//        // 1.查询用户
//        List<User> users = userService.listByIds(ids);
//        // 2.处理vo
//        List<UserVO> userVOList = BeanUtil.copyToList(users, UserVO.class);

        return userService.queryUserAndAddressByIds(ids);
    }

    @PutMapping("{id}/deduction/{money}")
    @ApiOperation("扣减用户余额")
    public void deductBalance(@PathVariable("id") Long id, @PathVariable("money") Integer money) {
        userService.deductBalance(id, money);
    }

    @GetMapping("/list")
    @ApiOperation("根据复杂条件查询用户")
    public List<UserVO> queryUsers(UserQuery query) {
        // 1.组织条件
        String username = query.getName();
        Integer status = query.getStatus();
        Integer minBalance = query.getMinBalance();
        Integer maxBalance = query.getMaxBalance();

//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
//                .like(username != null, User::getUsername, username)
//                .eq(status != null, User::getStatus, status)
//                .ge(minBalance != null, User::getBalance, minBalance)
//                .le(maxBalance != null, User::getBalance, maxBalance);
//        // 2.查询用户
//        List<User> users = userService.list(wrapper);

        List<User> users = userService.lambdaQuery()
                .like(username != null, User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .ge(minBalance != null, User::getBalance, minBalance)
                .le(maxBalance != null, User::getBalance, maxBalance)
                .list();
        // 3.处理vo
        return BeanUtil.copyToList(users, UserVO.class);
    }


    @GetMapping("/page")
    public PageDTO<UserVO> queryUsersPage(UserQuery query){
        return userService.queryUsersPage(query);
    }





}
