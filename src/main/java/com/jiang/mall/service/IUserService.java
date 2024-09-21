package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.UserVo;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IUserService extends IService<User> {

    User login(String username, String password);

    boolean modifyPassword(Integer userId, String oldPassword, String newPassword);

    User getUserInfo(Integer userId);

    boolean modifyUserInfo(User newUser);

    boolean lockUser(Integer userId);

    boolean queryByUserName(String userName);

    boolean queryByEmail(String email);

    int registerStep(User user);

    List<UserVo> getUserList(Integer pageNum, Integer pageSize, Integer userId);

    boolean updateUser(User user);

	Integer queryDefaultAddressById(Integer userId);

    boolean updateDefaultAddress(Integer id, Integer userId);

	boolean unlockUser(Integer userId);

    ResponseResult hasPermission(Integer oldUserId, HttpSession session);

    ResponseResult checkAdminUser(HttpSession session);

    ResponseResult checkUserLogin(HttpSession session);
}
