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
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.jiang.mall.domain.entity.Config.AdminRoleId;
import static com.jiang.mall.domain.entity.Config.regex_email;
import static com.jiang.mall.util.TimeUtils.getDaysUntilNextBirthday;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
	    this.userMapper = userMapper;
	}

	/**
	 * 检查用户是否已登录
	 * <p>
	 * 此方法检查会话中的 "User" 属性以确认用户是否已经登录。
	 * 如果用户未登录，则返回失败的结果；如果已登录，则返回用户ID。
	 *
	 * @param session 当前用户的会话
	 * @return 如果用户已登录，返回用户ID；否则返回失败结果
	 */
	public ResponseResult checkUserLogin(HttpSession session) {
	    // 检查用户是否已登录
	    if (session.getAttribute("User") != null) {
	        UserVo user = (UserVo) session.getAttribute("User");
			if (user.getId()==null){
				return ResponseResult.failResult("用户信息获取失败！");
			}else{
				return ResponseResult.okResult(user.getId());
			}
	    }else {
			return ResponseResult.notLoggedResult("您未登录，请先登录");
	    }
	}

	/**
	 * 用于获取用户数量
	 *
	 * @return 用户数量
	 */
	@Override
	public Integer getUserNum() {
	    // 通过userMapper查询所有用户，null参数表示不使用任何条件
	    List<User> userList = userMapper.selectList(null);
	    // 返回用户列表的大小，即用户数量
	    return userList.size();
	}


	/**
	 * 检查当前用户是否为管理员
	 * 此方法首先调用checkUserLogin方法验证用户是否已登录
	 * 如果用户未登录，则返回相应的未登录结果
	 * 如果用户已登录但不是管理员，则返回无权限访问的结果
	 * 如果用户已登录且是管理员，则返回成功的验证结果
	 *
	 * @param session HTTP会话，用于获取用户登录状态和管理员状态
	 * @return ResponseResult 包含验证结果的对象，包括用户是否已登录和是否有管理员权限
	 */
	public ResponseResult checkAdminUser(HttpSession session) {
	    // 检查用户是否已登录
	    ResponseResult result = checkUserLogin(session);
	    if (!result.isSuccess()) {
	        // 如果未登录，则直接返回
	        return result;
	    }
		UserVo user = (UserVo) session.getAttribute("User");
		if (user.isAdmin()){
			return ResponseResult.okResult(user.getId());
		}else{
			return ResponseResult.failResult("您没有权限访问此页面");
		}
	}

	/**
	 * 检查用户是否具有修改特定资源的权限
	 * 此方法主要用于确保用户有权修改特定的用户信息，如轮播图等
	 * 它首先确认用户已登录并具有管理员权限，然后检查用户是否试图修改属于自己角色权限之下的资源
	 *
	 * @param oldUserId  需要修改的用户的ID
	 * @param session  当前用户的会话
	 * @return  包含权限检查结果的响应对象，如果用户无权修改，则返回相应的错误信息
	 */
	public ResponseResult hasPermission(Integer oldUserId, HttpSession session){
	    // 检查会话中是否设置表示用户已登录的标志
	    ResponseResult result = checkAdminUser(session);
	    // 如果用户未登录或没有管理员权限，则返回相应的错误信息
	    if (!result.isSuccess()) {
	        return result;
	    }
		UserVo user = (UserVo) session.getAttribute("User");
	    // 获取创建修改用户的信息
		if (userMapper.selectById(oldUserId) == null) {
//			return ResponseResult.notLoggedResult("用户不存在");
			return ResponseResult.okResult(result.getData());
		}
		User old_user = userMapper.selectById(oldUserId);
	    // 检查尝试修改轮播图的用户的权限是否足够
	    if (old_user.getRoleId() >user.getRoleId()) {
	        return ResponseResult.notLoggedResult("您没有权限修改此资源");
	    }
	    return ResponseResult.okResult(result.getData());
	}

	/**
	 * 用户登录方法
	 * 通过用户名(邮箱)和密码尝试登录系统。用户密码是经过MD5加密的，以提高安全性。
	 *
	 * @param username 用户名或者邮箱，用于登录验证
	 * @param password 明文密码，用于登录验证
	 * @return 如果验证成功，返回对应的User对象；如果验证失败或用户不存在，返回null
	 */
	@Override
	public User login(String username, String password) {
	    // 创建查询条件，指定用户名和账号激活状态
	    QueryWrapper<User> queryWrapper_username = new QueryWrapper<>();
	    queryWrapper_username.eq("username", username);
	    queryWrapper_username.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息
	    User user_username = userMapper.selectOne(queryWrapper_username);

	    // 根据邮箱格式匹配用户
	    if (username.matches(regex_email)) {
	        // 创建基于邮箱的查询条件
	        QueryWrapper<User> queryWrapper_email = new QueryWrapper<>();
	        queryWrapper_email.eq("email", username);
	        queryWrapper_email.eq("is_active", true);
	        User user_email = userMapper.selectOne(queryWrapper_email);
//encryptToSHA256(password,AES_SALT)
	        // 判断邮箱是否对应用户
	        if (user_email == null) {
	            // 如果邮箱未注册，检查用户名是否注册
	            if (user_username == null) {
					// 用户名和邮箱均未注册
	                return null;
	            } else if (user_username.getPassword().equals(password)) {
					// 用户名注册且密码匹配
	                return user_username;
	            } else {
					// 用户名注册但密码不匹配
	                return null;
	            }
	        } else {
	            // 邮箱已注册，检查是否与用户名对应同一用户
		        if (user_username==null){
					if (user_email.getPassword().equals(password)){
						return user_email;
					}else {
						return null;
					}
		        }else if (Objects.equals(user_email.getId(), user_username.getId())) {
	                // 同一用户，检查密码
	                if (user_username.getPassword().equals(password)) {
						// 密码匹配
	                    return user_username;
	                } else {
						// 密码不匹配
	                    return null;
	                }
	            } else {
	                // 不是同一用户，分别检查密码
	                if (user_email.getPassword().equals(password)) {
						// 邮箱用户密码匹配
	                    return user_email;
	                } else if (user_username.getPassword().equals(password)) {
						// 用户名用户密码匹配
	                    return user_username;
	                } else {
						// 密码均不匹配
	                    return null;
	                }
	            }
	        }
	    } else {
	        // 如果不是邮箱格式，直接检查用户名是否注册
	        if (user_username == null) {
				// 用户名未注册
	            return null;
	        } else if (user_username.getPassword().equals(password)) {
				// 用户名注册且密码匹配
	            return user_username;
	        } else {
				// 用户名注册但密码不匹配
	            return null;
	        }
	    }
	}

	/**
	 * 根据用户名或电子邮件地址获取用户信息
	 *
	 * @param username 用户名或电子邮件地址
	 * @return 如果找到对应的用户信息，则返回User对象；否则返回null
	 */
	@Override
	public User getUserByUserNameOrEmail(String username) {
	    // 创建查询条件，指定用户名
	    QueryWrapper<User> queryWrapper_username = new QueryWrapper<>();
	    queryWrapper_username.eq("username", username);

	    // 根据查询条件尝试获取用户信息
	    User user_username = userMapper.selectOne(queryWrapper_username);

	    // 根据邮箱格式匹配用户
	    if (username.matches(regex_email)) {
	        // 创建基于邮箱的查询条件
	        QueryWrapper<User> queryWrapper_email = new QueryWrapper<>();
	        queryWrapper_email.eq("email", username);
	        User user_email = userMapper.selectOne(queryWrapper_email);

	        // 判断邮箱是否对应用户
	        if (user_email == null) {
	            // 如果邮箱未注册，检查用户名是否注册
	            return user_username;
	        } else {
	            // 邮箱已注册，检查是否与用户名对应同一用户
		        if (user_username==null){
					return user_email;
		        }else if (Objects.equals(user_email.getId(), user_username.getId())) {
	                return user_username;
	            } else {
	                return null;
	            }
	        }
	    } else {
	        // 如果不是邮箱格式，直接检查用户名是否注册
	        return user_username;
	    }
	}

	@Override
	public boolean modifyPassword(Integer userId, String newPassword) {
		// 创建查询条件，指定用户ID和账号激活状态。
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id",userId);

	    // 根据查询条件尝试获取用户信息。
	    User user = userMapper.selectOne(queryWrapper);

	    // 验证用户是否存在且旧密码是否正确。
	    if (user != null) {
	        // 如果验证成功，更新用户密码为新密码。
	        user.setPassword(newPassword);
			user.setIsActive(true);
			// 通过ID更新用户信息。
		    return userMapper.updateById(user) > 0;
	    }else {
			// 如果用户不存在或旧密码验证失败，返回false。
			return false;
	    }
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
	    if (user != null && oldPassword.equals(user.getPassword())) {
	        // 如果验证成功，更新用户密码为新密码。
	        user.setPassword(newPassword);
			// 通过ID更新用户信息。
		    return userMapper.updateById(user) > 0;
	    }else {
			// 如果用户不存在或旧密码验证失败，返回false。
			return false;
	    }
	}

	/**
	 * 根据用户ID获取用户信息
	 * 此方法覆盖了父类的抽象方法，用于根据用户ID获取相应的用户信息
	 * 它委托给userMapper的selectById方法来实现数据库查询
	 *
	 * @param userId 用户ID，用于标识特定的用户
	 * @return 返回查询到的用户信息对象，如果未找到则返回null
	 */
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
	        newUser.setIsActive(user.getIsActive());
			newUser.setRoleId(user.getRoleId());
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
	        logger.info("尝试锁定不存在的用户，ID: {}", userId);
	        return false;
	    }
	}

	/**
	 * 根据用户名查询用户是否存在
	 * 该方法通过加密用户名为MD5格式，然后尝试从数据库中查询匹配该MD5值的用户
	 * 如果找到匹配的用户，则返回true；否则返回false
	 *
	 * @param userName 待查询的用户名
	 * @return 如果用户存在则返回true，否则返回false
	 */
	@Override
	public boolean queryByUserName(String userName) {
	    // 创建一个查询包装器，用于构建查询条件
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    // 设置查询条件
	    queryWrapper.eq("username", userName);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

	    // 判断用户是否存在，存在则返回true，否则返回false
	    return user != null;
	}

	/**
	 * 根据邮箱查询用户是否存在
	 * 该方法通过加密邮箱为MD5格式，然后尝试从数据库中查询匹配该MD5值的用户
	 * 如果找到匹配的用户，则返回true；否则返回false
	 *
	 * @param email 待查询的邮箱
	 * @return 如果用户存在则返回true，否则返回false
	 */
	@Override
	public boolean queryByEmail(String email) {
		// 创建查询条件，匹配传入的邮箱
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("email", email);

		// 根据查询条件尝试获取用户信息
		User user = userMapper.selectOne(queryWrapper);

		// 如果用户信息不为空，说明用户存在，返回true；否则返回false
		return user != null;
	}


	/**
	 * 用户注册步骤
	 *
	 * @param user 待注册/更新的用户信息
	 * @return 注册/更新成功返回用户ID，否则返回0
	 */
	@Override
	public int registerStep(User user) {
	    // 检查用户信息是否完整
	    if(user.getUsername()!=null && user.getPassword()!=null && user.getEmail()!=null){
	        // 设置用户账户为激活状态
	        user.setIsActive(true);
	        // 加密用户密码和用户名以确保安全性
	        user.setPassword(user.getPassword());
	        user.setUsername(user.getUsername());
	        // 设置用户角色为普通用户
	        user.setRoleId(1);
	        // 插入用户信息，若成功则返回用户ID，否则返回0
	        return userMapper.insert(user)>0?user.getId():0;
	    }else{
	        // 对于信息不完整的用户，清除其账户信息
	        user.setUsername(null);
	        user.setPassword(null);
	        user.setEmail(null);
	        // 更新用户信息，若成功则返回用户ID，否则返回0
	        return userMapper.updateById(user)>0?user.getId():0;
	    }
	}


    /**
     * 获取用户列表
     * <p>
     * 此方法根据用户的ID查询数据库中的用户列表，并为每个用户计算下一次生日和是否为管理员状态
     * 它首先通过用户ID获取用户信息，然后根据此用户的角色ID从数据库中查询相应的用户列表
     * 最后，它会为每个用户计算下一次生日的天数，并设置是否为管理员的状态
     *
     * @param pageNum 当前页码
     * @param pageSize 页面大小
     * @param userId 用户ID
     * @return 用户列表的Vo对象
     */
	@Override
    public List<UserVo> getUserList(Integer pageNum, Integer pageSize, Integer userId) {
        // 通过用户ID获取用户信息
        User user = userMapper.selectById(userId);
        // 创建分页对象
        Page<User> userPage = new Page<>(pageNum, pageSize);
        // 创建查询条件对象，并限制角色ID
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().le(User::getRoleId,user.getRoleId()+0.1);
        // 根据分页和查询条件获取用户列表
        List<User> users = userMapper.selectPage(userPage,queryWrapper).getRecords();
        // 将用户列表转换为Vo对象列表
        List<UserVo> userVos = BeanCopyUtils.copyBeanList(users, UserVo.class);
        // 为每个Vo对象计算下一次生日和设置是否为管理员状态
        for (UserVo userVo : userVos) {
			userVo.setActive(userMapper.selectById(userVo.getId()).getIsActive());
			if (userVo.getBirthDate() != null) {
				userVo.setNextBirthday(getDaysUntilNextBirthday(userVo.getBirthDate()));
			}
            userVo.setAdmin(userVo.getRoleId() >= AdminRoleId);
        }
        // 返回处理后的用户列表Vo对象
        return userVos;
    }


    @Override
    public boolean updateUser(User newUser) {

	    User user = userMapper.selectById(newUser.getId());

	    // 如果用户存在且当前是非激活状态，则进行更新。
	    if (user != null) {
	        // 维持原密码不变，确保用户不会因为信息修改而失去访问权限。
	        newUser.setPassword(user.getPassword());
			newUser.setUsername(user.getUsername());
	        newUser.setIsActive(user.getIsActive());
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
	 * 根据用户ID查询其默认地址ID
	 * 本方法通过用户ID查询数据库中活跃的用户信息，并返回用户的默认地址ID
	 * 如果数据库中没有匹配的用户或用户不活跃，则返回null
	 *
	 * @param userId 用户ID
	 * @return 用户的默认地址ID，如果不存在则返回null
	 */
	@Override
	public Integer queryDefaultAddressById(Integer userId) {
	    // 创建查询条件包装器
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    // 添加查询条件：用户ID和用户活跃状态
	    queryWrapper.eq("id", userId);
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);
	    // 返回用户的默认地址ID，如果用户不存在或没有默认地址，则返回null
	    return user.getDefaultAddressId();
	}

	/**
	 * 更新用户的默认地址
	 *
	 * @param id 新的默认地址的ID
	 * @param userId 用户的ID
	 * @return 如果更新成功返回true，否则返回false
	 */
	@Override
	public boolean updateDefaultAddress(Integer id, Integer userId) {
	    // 创建查询条件包装器
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    // 添加查询条件：用户ID等于userId且用户处于激活状态
	    queryWrapper.eq("id", userId);
	    queryWrapper.eq("is_active", true);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);
	    // 检查获取的用户信息是否非空
	    if (user != null) {
	        // 创建一个新的User对象用于更新默认地址
	        User newUser = new User();
	        // 设置新的默认地址ID
	        newUser.setDefaultAddressId(id);
	        // 设置用户ID
	        newUser.setId(userId);
	        // 尝试更新用户信息，并判断是否成功
	        return userMapper.updateById(newUser) > 0;
	    }
	    // 如果用户信息为空，返回false
	    return false;
	}

	/**
	 * 解锁用户方法。
	 * 通过设置用户的活跃状态为true来解锁用户账号。
	 *
	 * @param userId 用户ID，用于查询和锁定特定用户。
	 * @return 如果用户成功被解锁，返回true；如果用户不存在或解锁失败，返回false。
	 */
	@Override
	public boolean unlockUser(Integer userId) {
	    // 创建查询条件对象
	    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
	    // 设置查询条件：用户ID等于userId且用户当前是锁定状态（is_active为false）
	    queryWrapper.eq("id", userId);
	    queryWrapper.eq("is_active", false);

	    // 根据查询条件尝试获取用户信息
	    User user = userMapper.selectOne(queryWrapper);

	    // 如果用户存在
	    if (user != null) {
	        // 将用户状态设置为解锁
	        user.setIsActive(true);
	        // 更新数据库中的用户信息
	        int result = userMapper.updateById(user);
	        // 检查更新是否成功，并返回结果
	        return result > 0;
	    } else {
	        // 记录日志，提示尝试解锁不存在的用户
	        logger.info("尝试解锁不存在的用户，ID: {}", userId);
	        return false;
	    }
	}
}
