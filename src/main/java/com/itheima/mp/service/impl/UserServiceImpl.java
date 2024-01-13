package com.itheima.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.itheima.mp.domain.dto.PageDTO;
import com.itheima.mp.domain.po.Address;
import com.itheima.mp.domain.po.User;
import com.itheima.mp.domain.query.UserQuery;
import com.itheima.mp.domain.vo.AddressVO;
import com.itheima.mp.domain.vo.UserVO;
import com.itheima.mp.enums.UserStatus;
import com.itheima.mp.mapper.UserMapper;
import com.itheima.mp.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author kirika
 * @since 2024-01-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void deductBalance(Long id, Integer money) {
        // 1.查询用户
        User user = getById(id);
        // 2.判断用户状态
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常");
        }
        // 3.判断用户余额
        if (user.getBalance() < money) {
            throw new RuntimeException("用户余额不足");
        }
        // 4.扣减余额
        userMapper.deductBalanceById(id, money);
    }

    @Override
    public UserVO queryUserAndAddressById(Long userId) {
        // 1.查询用户
        User user = getById(userId);
        // 2.判断用户状态
        if (user == null || user.getStatus() == UserStatus.FREEZE) {
            throw new RuntimeException("用户状态异常");
        }

        List<Address> addressList = Db.lambdaQuery(Address.class)
                .eq(Address::getUserId, userId)
                .list();
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setAddresses(BeanUtil.copyToList(addressList, AddressVO.class));

        return userVO;
    }

    @Override
    public List<UserVO> queryUserAndAddressByIds(List<Long> ids) {
        // 查询用户
        // List<User> userList = lambdaQuery().in(User::getId,ids).list();
        List<User> userList = userMapper.selectBatchIds(ids);
        // List<User> userList = listByIds(ids)

        if (userList == null || userList.size() == 0) {
            return Collections.emptyList();
        }

        List<Long> userIds = userList.stream().map(User::getId).collect(Collectors.toList());

        List<Address> addressList = Db.lambdaQuery(Address.class).in(Address::getUserId,userIds).list();

        List<AddressVO> addressVOList = BeanUtil.copyToList(addressList, AddressVO.class);

        Map<Long,List<AddressVO>> addressMap = new HashMap<>(0);
        if(CollUtil.isNotEmpty(addressVOList)){
            addressMap = addressVOList.stream().collect(Collectors.groupingBy(AddressVO::getUserId));
        }

        List<UserVO> userVOList = new ArrayList<>(userList.size());
        for(User user:userList){
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            userVO.setAddresses(addressMap.get(user.getId()));
            userVOList.add(userVO);

        }

        return userVOList;
    }

    @Override
    public PageDTO<UserVO> queryUsersPage(UserQuery query) {
//        // 1.构建条件
//        // 1.1.分页条件
//        Page<User> page = Page.of(query.getPageNo(), query.getPageSize());
//        // 1.2.排序条件
//        if (query.getSortBy() != null) {
//            page.addOrder(new OrderItem(query.getSortBy(), query.getIsAsc()));
//        }else{
//            // 默认按照更新时间排序
//            page.addOrder(new OrderItem("update_time", false));
//        }

        Page<User> page = query.toMpPageDefaultSortByUpdateTimeDesc();


        // 2.查询
        Page<User> p = lambdaQuery()
                .like(query.getName()!=null,User::getUsername,query.getName())
                .eq(query.getStatus()!=null,User::getStatus,query.getStatus())
                .ge(query.getMinBalance()!=null,User::getBalance,query.getMinBalance())
                .le(query.getMaxBalance()!=null,User::getBalance,query.getMaxBalance())
                .page(page);

        // 3.数据非空校验
        PageDTO<UserVO> dto = new PageDTO<>();
//        dto.setTotal(p.getTotal());
//        dto.setPages(p.getPages());
//        List<User> records = page.getRecords();
//        if (CollUtil.isEmpty(records)) {
//            // 无数据，返回空结果
//            dto.setList(Collections.emptyList());
//            return dto;
//        }
//        // 4.有数据，转换
//        List<UserVO> list = BeanUtil.copyToList(records, UserVO.class);
//        dto.setList(list);

        dto = PageDTO.of(p, UserVO.class);

        return dto;
    }


}
