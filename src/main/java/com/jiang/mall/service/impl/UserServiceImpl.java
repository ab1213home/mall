package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtils;
import com.jiang.mall.util.MD5Utils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jiang.mall.util.MD5Utils.encryptToMD5;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author WH
 * @since 2023-06-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Resource
	private UserMapper userMapper;

	/**
	 * 用户登录方法
	 * 通过用户名和密码尝试登录系统。用户名密码是经过MD5加密的，以提高安全性。
	 *
	 * @param username 用户名，用于登录验证
	 * @param password 明文密码，用于登录验证
	 * @return 如果验证成功，返回对应的User对象；如果验证失败或用户不存在，返回null
	 */
	@Override
	public User login(String username, String password) {
	    // 创建查询条件，指定用户名和账号激活状态
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("username", encryptToMD5(username));
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

	    // 验证用户密码是否匹配，如果匹配则返回用户对象，否则返回null
	    if (user != null && encryptToMD5(password).equals(user.getPassword())) {
	        return user;
	    }

	    // 密码不匹配或用户不存在，返回null
	    return null;
	}

	/**
	 * 修改用户密码的方法。用户名密码是经过MD5加密的，以提高安全性。
	 *
	 * @param userId 用户ID，用于查询用户信息。
	 * @param oldPassword 用户当前密码，用于验证身份。
	 * @param newPassword 用户新密码，待验证通过后设置。
	 * @return 如果密码修改成功，返回true；否则返回false。
	 */
    @Override
    public boolean modifyPassword(Integer userId, String oldPassword, String newPassword) {
		// 创建查询条件，指定用户ID和账号激活状态。
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id",userId);
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper);

	    // 验证用户是否存在且旧密码是否正确。
	    if (user != null && encryptToMD5(oldPassword).equals(user.getPassword())) {
	        // 如果验证成功，更新用户密码为新密码。
	        user.setPassword(encryptToMD5(newPassword));
			// 通过ID更新用户信息。
			int result =userMapper.updateById(user);
		    // 返回密码更新是否成功的标志。
		    return result > 0;
	    }else {
			// 如果用户不存在或旧密码验证失败，返回false。
			return false;
	    }
	}

	@Override
    public User getUserInfo(Integer userId) {
		return userMapper.selectById(userId);
	}


	/**
	 * 修改用户信息。
	 * 此方法用于更新用户的信息。
	 * 它首先检查用户是否存在于数据库中且当前状态为非激活状态。
	 * 如果用户存在且满足条件，则更新用户的密码和激活状态。
	 * 如果用户不存在，则记录日志并返回false。
	 *
	 * @param newUser 包含新用户信息的对象，其中ID用于查找用户，密码和激活状态用于更新用户信息。
	 * @return 如果用户信息成功更新，则返回true；否则返回false。
	 */
    @Override
    public boolean modifyUserInfo(User newUser) {
	    // 创建查询条件，特定于用户的ID和非激活状态。
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id", newUser.getId());
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper);

	    // 如果用户存在且当前是非激活状态，则进行更新。
	    if (user != null) {
	        // 维持原密码不变，确保用户不会因为信息修改而失去访问权限。
	        newUser.setPassword(user.getPassword());
			newUser.setUsername(user.getUsername());
	        // 将用户状态设置为激活，确保用户不会因为信息修改而失去访问权限。
	        newUser.setIsActive(true);
	        // 更新数据库中的用户信息。
	        int result = userMapper.updateById(newUser);
	        // 检查更新是否成功，并返回结果。
	        return result > 0;
	    } else {
	        // 如果用户不存在，则记录日志并返回false。
	        logger.info("尝试更新不存在的用户信息，ID: {}", newUser.getId());
	        return false;
	    }
	}

	/**
	 * 锁定用户方法。
	 * 通过设置用户的活跃状态为false来锁定用户账号。
	 *
	 * @param userId 用户ID，用于查询和锁定特定用户。
	 * @return 如果用户成功被锁定，返回true；如果用户不存在或锁定失败，返回false。
	 */
    @Override
    public boolean lockUser(Integer userId) {
	    // 创建查询条件，指定用户ID和当前为激活状态
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id", userId);
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

	    // 如果用户存在
	    if (user != null) {
	        // 将用户活跃状态设置为false，即锁定用户
	        user.setIsActive(false);
	        // 更新数据库中的用户信息
	        int result = userMapper.updateById(user);
	        // 检查更新是否成功，并返回结果
	        return result > 0;
	    } else {
	        // 记录日志，提示尝试锁定不存在的用
	        logger.info("尝试锁定不存在的用户信息，ID: {}", userId);
	        return false;
	    }
	}

	@Override
    public boolean register(User newUser) {
		newUser.setIsActive(true);
		newUser.setPassword(encryptToMD5(newUser.getPassword()));
		newUser.setUsername(encryptToMD5(newUser.getUsername()));
		newUser.setRoleId(1);
		return userMapper.insert(newUser) > 0;
	}

	@Override
    public boolean queryByUserName(String userName) {
		// 创建查询条件，指定用户ID和当前为激活状态
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("username", encryptToMD5(userName));

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

		return user!=null;
	}

	@Override
    public boolean queryByEmail(String email) {
				// 创建查询条件，指定用户ID和当前为激活状态
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("email",email);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

		return user!=null;
	}

	@Override
    public int registerStep(User user) {
		if(user.getUsername()!=null && user.getPassword()!=null && user.getEmail()!=null){
			user.setIsActive(true);
			user.setPassword(encryptToMD5(user.getPassword()));
			user.setUsername(encryptToMD5(user.getUsername()));
			user.setBirthDate(null);
			user.setPhone(null);
			user.setFirstName(null);
			user.setLastName(null);
			user.setRoleId(1);
			return userMapper.insert(user)>0?user.getId():0;
		}else{
			user.setUsername(null);
			user.setPassword(null);
			user.setEmail(null);
			return userMapper.updateById(user)>0?user.getId():0;
		}
	}

    @Override
    public ResponseResult getUserList(Integer pageNum, Integer pageSize) {
        Page<User> userPage = new Page<>(pageNum, pageSize);
        List<User> users = userMapper.selectPage(userPage, null).getRecords();
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(users, UserVo.class);
        return ResponseResult.okResult(userVos);
    }

    @Override
    public ResponseResult getUser(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseResult.failResult();
        } else {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return ResponseResult.okResult(userVo);
        }
    }

    @Override
    public ResponseResult updateUser(User user) {
        int result = userMapper.updateById(user);
        if (result == 1) {
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }

    @Override
    public ResponseResult deleteUser(List<Integer> ids) {
        int result = userMapper.deleteByIds(ids);
        if (result > 0) {
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }
}
