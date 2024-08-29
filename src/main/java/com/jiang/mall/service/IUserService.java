package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WH
 * @since 2023-06-25
 */
public interface IUserService extends IService<User> {

    User login(String username, String password);

    boolean modifyPassword(Integer userId, String oldPassword, String newPassword);

    User getUserInfo(Integer userId);

    boolean modifyUserInfo(User newUser);

    boolean lockUser(Integer userId);

    boolean register(User newUser);

    boolean queryByUserName(String userName);

    boolean queryByEmail(String email);

    int registerStep(User user);

    ResponseResult getUserList(Integer pageNum, Integer pageSize);

    ResponseResult getUser(Integer id);

    ResponseResult updateUser(User user);

    ResponseResult deleteUser(List<Integer> ids);
}
