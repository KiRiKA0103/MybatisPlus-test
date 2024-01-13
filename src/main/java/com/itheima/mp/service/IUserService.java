package com.itheima.mp.service;

import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.UserVO;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author kirika
 * @since 2024-01-13
 */
public interface IUserService extends IService<User> {

    void deductBalance(Long id, Integer money);

    UserVO queryUserAndAddressById(Long userId);

    List<UserVO> queryUserAndAddressByIds(List<Long> ids);


    PageDTO<UserVO> queryUsersPage(UserQuery query);
}
