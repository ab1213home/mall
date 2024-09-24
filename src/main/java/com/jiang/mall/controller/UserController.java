package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtils;
import com.jiang.mall.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.jiang.mall.controller.AdminController.AllowRegistration;
import static com.jiang.mall.domain.entity.Propertie.*;
import static com.jiang.mall.util.CheckUser.*;
import static com.jiang.mall.util.TimeUtils.getDaysUntilNextBirthday;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 处理用户注册第一步的请求
     *
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param email 邮箱
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/registerStep1")
    public ResponseResult registerStep1(@RequestParam("username") String username,
                                        @RequestParam("password") String password,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        @RequestParam("email") String email,
                                        @RequestParam("captcha") String captcha,
                                        HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("UserIsLogin")!=null){
            if ("true".equals(session.getAttribute("UserIsLogin"))) {
	            return ResponseResult.failResult("您已登录，请勿重复注册");
            }
        }

        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }

        // 验证邮箱格式
        if (StringUtils.hasText(email) && !email.matches(regex_email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }

        // 获取并验证会话中的验证码
        Object captchaObj = session.getAttribute("captcha");
        if (captchaObj == null) {
            return ResponseResult.failResult("会话中的验证码已过期，请重新获取");
        }
        String captchaCode = captchaObj.toString();
        if (!captchaCode.toLowerCase().equals(captcha)) {
            return ResponseResult.failResult("验证码错误");
        }
        if (!AllowRegistration){
            return ResponseResult.failResult("管理员用户不允许注册");
        }
        // 检查注册信息是否完整
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)||!StringUtils.hasText(email)) {
            return ResponseResult.failResult("请输入完整的注册信息");
        }

        // 验证密码一致性
        if (!password.equals(confirmPassword)) {
            return ResponseResult.failResult("两次密码输入不一致");
        }

        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }

        // 检查用户名是否已注册
        if (userService.queryByUserName(username)) {
            return ResponseResult.failResult("用户名已存在");
        }

        // 创建并注册用户
        User user = new User(username,password,email);
        int userId = userService.registerStep(user);
        if (userId>0) {
            session.setAttribute("UserId",userId);
            return ResponseResult.okResult();
        }else {
            return ResponseResult.serverErrorResult("未知原因注册失败");
        }
    }


    /**
     * 完成注册过程的第二步
     * <p>
     * 这个方法用于接收用户的基本个人信息，如手机号、姓名和生日，并在系统中进行记录
     * 只有在第一步注册已完成的情况下，用户才能访问此端点
     *
     * @param phone 用户的手机号码
     * @param firstName 用户的名字
     * @param lastName 用户的姓氏
     * @param birthDate 用户的出生日期，格式为yyyy-MM-dd
     * @param session HTTP会话，用于存储用户会话信息
     * @return ResponseResult表示注册结果或错误信息
     */
    @PostMapping("/registerStep2")
    public ResponseResult registerStep2(@RequestParam("phone") String phone,
                                        @RequestParam("firstName") String firstName,
                                        @RequestParam("lastName") String lastName,
                                        @RequestParam("birthday") String birthDate,
                                        HttpSession session) {
        // 检查会话中是否包含账号id，以确保用户已开始注册过程
        if (session.getAttribute("UserId")==null){
            return ResponseResult.failResult("请先完成第一步注册，会话已过期");
        }
        // 防止已登录用户重复注册
        if (session.getAttribute("UserIsLogin")!=null){
            if ("true".equals(session.getAttribute("UserIsLogin"))) {
                return ResponseResult.failResult("您已登录，请勿重复注册");
            }
        }
        int UserId = (int) session.getAttribute("UserId");

        // 验证手机号格式
        if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
            return ResponseResult.failResult("手机号格式不正确");
        }

        // 创建User对象以保存用户信息
        User user = new User(UserId,firstName,lastName,phone);
        // 验证和转换生日日期格式
        try {
            LocalDate localDate = LocalDate.parse(birthDate, formatter);
            if (localDate.isAfter(LocalDate.now())) {
                return ResponseResult.failResult("生日不能在未来，请输入正确的日期");
            }
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            user.setBirthDate(date);
        } catch (DateTimeParseException e) {
            return ResponseResult.failResult("日期格式不正确，请使用yyyy-MM-dd");
        }
        // 调用服务层方法保存用户个人信息
        if (userService.registerStep(user)>0) {
            // 注册成功后清除会话中的用户id
            session.removeAttribute("userId");
            return ResponseResult.okResult();
        }else {
            // 处理个人信息保存失败的情况
            return ResponseResult.serverErrorResult("用户个人信息保存失败");
        }
    }

    /**
     * 处理用户登录请求
     *
     * @param username 用户名
     * @param password 密码
     * @param captcha 验证码
     * @param session HttpSession，用于存储会话信息
     * @return ResponseResult 登录结果
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("captcha") String captcha,
                                HttpSession session) {
        // 检查用户是否已经登录，避免重复登录
        if (session.getAttribute("UserIsLogin")!=null){
            if ("true".equals(session.getAttribute("UserIsLogin"))) {
                return ResponseResult.failResult("您已登录，请勿重复登录");
            }
        }
        // 验证验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }
        // 获取session中的验证码
        Object captchaObj = session.getAttribute("captcha");
        // 检查验证码是否过期
        if (captchaObj == null) {
            return ResponseResult.failResult("会话中的验证码已过期，请重新获取");
        }
        String captchaCode = captchaObj.toString();

        // 校验验证码是否正确
        if (!captchaCode.toLowerCase().equals(captcha)) {
            session.removeAttribute("captcha");
            return ResponseResult.failResult("验证码错误");
        }
        // 检查用户名和密码是否为空
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ResponseResult.failResult("用户名或密码不能为空");
        }

        // 调用userService的login方法进行用户登录验证
        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功，存储用户信息到session
            session.setAttribute("UserId", user.getId());
            session.setAttribute("UserName", username);
            session.setAttribute("UserRole", user.getRoleId());
            session.setAttribute("UserEmail", user.getEmail());
            if (user.getBirthDate()!=null){
                session.setAttribute("UserBirthDate", user.getBirthDate());
            }
            if (user.getPhone()!=null){
                session.setAttribute("UserPhone", user.getPhone());
            }
            if (user.getFirstName()!=null) {
                session.setAttribute("UserFirstName", user.getFirstName());
            }
            if (user.getLastName()!=null){
                session.setAttribute("UserLastName", user.getLastName());
            }
            if (user.getDefaultAddressId()!=null){
                session.setAttribute("UserDefaultAddressId", user.getDefaultAddressId());
            }
            // 判断用户角色，如果是管理员，设置相应标志
            if (user.getRoleId() >= AdminRoleId) {
                session.setAttribute("UserIsAdmin", "true");
            } else {
                session.setAttribute("UserIsAdmin", "false");
            }
            session.setAttribute("UserIsLogin","true");
            // 设置session过期时间
            session.setMaxInactiveInterval(60 * 60 * 2);
            return ResponseResult.okResult();
        } else {
            // 登录失败，返回相应错误信息
            return ResponseResult.failResult("用户不存在或用户名或密码错误");
        }
    }

    /**
     * 修改密码的处理方法
     * <p>
     * 该方法负责处理用户修改密码的请求它包括验证新旧密码、确认密码的一致性，
     * 以及在密码修改后清除相关的会话属性
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmPassword 确认密码
     * @param session HTTP会话，用于判断用户是否登录及存储用户信息
     * @return ResponseResult 修改密码结果的响应对象
     */
    @PostMapping("/modify/password")
    public ResponseResult modifyPassword(@RequestParam("oldPassword") String oldPassword,
                                         @RequestParam("newPassword") String newPassword,
                                         @RequestParam("confirmPassword")String confirmPassword,
                                         HttpSession session) {
        // 检查新密码是否为空
        if (newPassword.isEmpty()){
            return ResponseResult.failResult("新密码不能为空！");
        }
        // 检查两次输入的密码是否一致
        if (!newPassword.equals(confirmPassword)){
            return ResponseResult.failResult("两次输入的密码不一致！");
        }
        // 检查新旧密码是否相同
        if (newPassword.equals(oldPassword)){
            return ResponseResult.failResult("新旧密码不能相同！");
        }
        // 检查旧密码是否为空
        if (!StringUtils.hasText(oldPassword)){
            return ResponseResult.failResult("旧密码不能为空！");
        }
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
		if (!result.isSuccess()) {
		    return result; // 如果未登录，则直接返回
		}
	    Integer userId = (Integer) result.getData();
        // 尝试修改密码，如果失败则返回错误响应
        if (!userService.modifyPassword(userId, oldPassword, newPassword)){
            return ResponseResult.serverErrorResult("修改失败！");
        }
        // 清除会话中的用户信息，因为密码已修改
        logout(session);
        // 密码修改成功，返回成功响应
        return ResponseResult.okResult("修改密码成功！");
    }

    /**
     * 修改用户信息
     * <p>
     * 该方法允许用户修改自己的信息，同时也允许管理员修改其他用户的信息。
     * 它接收一个HTTP会话对象来验证用户是否登录，并根据会话信息执行相应的权限检查。
     * 对于管理员来说，该方法还支持修改用户的角色权限，但需要确保权限的正确性。
     *
     * @param id       用户ID，可选参数
     * @param phone    手机号，必填参数
     * @param firstName    名字，必填参数
     * @param lastName     姓氏，必填参数
     * @param birthDate    生日日期，必填参数，格式为yyyy-MM-dd
     * @param email        电子邮件，必填参数
     * @param isAdmin      是否是管理员，可选参数
     * @param roleId       角色ID，可选参数
     * @param session      HTTP会话对象
     * @return 修改用户信息的结果
     */
    @PostMapping("/modify/info")
    public ResponseResult modifyUserInfo(@RequestParam(required = false) Integer id,
                                         @RequestParam("phone") String phone,
                                         @RequestParam("firstName") String firstName,
                                         @RequestParam("lastName") String lastName,
                                         @RequestParam("birthday") String birthDate,
                                         @RequestParam("email") String email,
                                         @RequestParam(required = false) boolean isAdmin,
                                         @RequestParam(required = false) Integer roleId,
                                         HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
        if (!result.isSuccess()) {
            return result; // 如果未登录，则直接返回
        }
        Integer userId = (Integer) result.getData();

        // 验证手机号格式是否正确
        if (StringUtils.hasText(phone) && !phone.matches(regex_phone)) {
            return ResponseResult.failResult("手机号格式不正确");
        }
        // 验证邮箱格式是否正确
        if (StringUtils.hasText(email) && !email.matches(regex_email)) {
            return ResponseResult.failResult("邮箱格式不正确");
        }
        // 设置用户ID到用户信息对象中
        User userInfo = new User(userId, firstName, lastName, phone, email);
        // 验证和转换生日日期格式
        try {
            LocalDate localDate = LocalDate.parse(birthDate, formatter);
            // 检查生日是否在过去
            if (localDate.isAfter(LocalDate.now())) {
                return ResponseResult.failResult("生日不能在未来，请输入正确的日期");
            }
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            userInfo.setBirthDate(date);
        } catch (DateTimeParseException e) {
            return ResponseResult.failResult("日期格式不正确，请使用yyyy-MM-dd");
        }
        if (id != null) {
            // 管理员后台修改信息时，不允许修改自己的信息
            if (Objects.equals(id, userId)) {
                return ResponseResult.failResult("不能通过管理后台修改自己的信息");
            }
            result = userService.hasPermission(id,session);
            // 如果用户未登录或不是管理员，则返回错误信息
            if (!result.isSuccess()) {
                return result;
            }
            // 检查邮箱是否已被其他用户使用
            User oldUser = userService.getUserInfo(id);
            if (!oldUser.getEmail().equals(email) && userService.queryByEmail(email)) {
                return ResponseResult.failResult("邮箱已存在");
            }
            // 角色权限检查，确保权限的正确性
            if (isAdmin) {
                if (roleId < AdminRoleId) {
                    return ResponseResult.failResult("不能修改为管理员，用户权限为管理员至少为" + AdminRoleId + "!");
                }
            } else {
                if (roleId >= AdminRoleId) {
                    return ResponseResult.failResult("不能修改为非管理员，用户权限为管理员至少为" + AdminRoleId + "!");
                }
            }
            if (roleId > (Integer) session.getAttribute("UserRole")) {
                return ResponseResult.failResult("不能修改成比自己权限高");
            }
            if (oldUser.getRoleId() > (Integer) session.getAttribute("UserRole")) {
                return ResponseResult.failResult("不能修改比自己权限高的用户");
            }
            // 设置用户ID
            userInfo.setId(id);
            // 设置角色ID
            userInfo.setRoleId(roleId);
            if (userService.updateUser(userInfo)) {
                return ResponseResult.okResult("修改成功");
            } else {
                return ResponseResult.serverErrorResult("修改失败");
            }
        } else {
            User oldUser = userService.getUserInfo(userId);
            // 检查邮箱是否已被其他用户使用
            if (!oldUser.getEmail().equals(email) && userService.queryByEmail(email)) {
                return ResponseResult.failResult("邮箱已存在");
            }
            // 尝试修改用户信息，如果失败则返回错误结果
            if (!userService.modifyUserInfo(userInfo))
                return ResponseResult.serverErrorResult("修改失败！");
            // 更新会话中的用户信息属性
            session.setAttribute("UserEmail", userInfo.getEmail());
            session.setAttribute("UserPhone", userInfo.getPhone());
            session.setAttribute("UserFirstName", userInfo.getFirstName());
            session.setAttribute("UserLastName", userInfo.getLastName());
            session.setAttribute("UserBirthDate", userInfo.getBirthDate());
            // 返回操作成功的结果，告知用户信息更新成功
            return ResponseResult.okResult("用户信息更新成功！");
        }
    }

    /**
     * 处理用户锁定请求的函数
     * 主要功能是基于当前会话判断用户是否已登录，然后尝试锁定该用户
     *
     * @param session 当前的HTTP会话，用于检查用户登录状态
     * @return 根据用户锁定操作的结果返回不同的响应结果
     *         如果用户未登录，返回表示未登录的响应结果
     *         如果用户锁定成功，返回表示成功的响应结果
     *         如果用户锁定失败，返回表示服务器错误的响应结果
     */
    @PostMapping("/modify/self-lock")
    public ResponseResult lockUser(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        // 获取已登录用户的ID
        Integer userId = (Integer) result.getData();
        // 尝试锁定用户，如果失败则返回错误信息
        if (!userService.lockUser(userId))
            return ResponseResult.serverErrorResult("修改失败！");
        // 用户锁定成功，返回成功信息
        return ResponseResult.okResult("用户锁定成功！");
    }

    /**
     * 管理员锁定账户功能
     * 该方法允许管理员锁定其账户
     *
     * @param userId 用户ID，用于标识需要锁定的用户
     * @param session HttpSession对象，用于检查用户是否已登录及权限验证
     * @return ResponseResult表示操作结果，包含成功、失败、未找到资源、服务器错误等状态
     */
    @PostMapping("/modify/lock")
    public ResponseResult selfLock(@RequestParam("userId") Integer userId,
                                   HttpSession session) {
        // 根据userId获取用户信息
        User user = userService.getUserInfo(userId);
        // 如果用户不存在，则返回未找到资源的错误信息
        if (user == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 检查当前会话是否拥有操作权限
        ResponseResult result = userService.hasPermission(user.getId(), session);
        // 如果用户未登录或权限不足，则返回相应的错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试锁定用户
        if (!userService.lockUser(userId)){
            // 锁定失败，返回错误信息
            return ResponseResult.serverErrorResult("用户锁定失败！");
        }else {
            // 锁定成功，返回成功信息
            return ResponseResult.okResult("用户锁定成功！");
        }
    }

    /**
     * 处理管理员解锁用户账户的函数
     * 该函数仅通过POST请求的'/modify/unlock'路径访问
     * 主要功能是基于当前会话判断用户是否已登录，并且具有管理员权限，然后尝试解锁指定用户
     *
     * @param userId 要解锁的用户ID
     * @param session 当前的HTTP会话，用于检查用户登录状态及权限
     * @return 根据解锁操作的结果返回不同的响应结果
     * 如果用户未登录或没有管理员权限，返回表示无权限的响应结果
     * 如果解锁成功，返回表示成功的响应结果
     * 如果解锁失败，返回表示服务器错误的响应结果
     */
    @PostMapping("/modify/unlock")
    public ResponseResult unlockUser(@RequestParam("userId") Integer userId,
                                     HttpSession session) {
        // 根据userId获取用户信息
        User user = userService.getUserInfo(userId);
        // 如果用户不存在，则返回未找到资源的错误信息
        if (user == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 检查当前会话是否拥有操作权限
        ResponseResult result = userService.hasPermission(user.getUpdater(), session);
        // 如果用户未登录或权限不足，则返回相应的错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试解锁用户，如果失败则返回错误信息
        if (!userService.unlockUser(userId))
            return ResponseResult.serverErrorResult("解锁失败！");

        // 解锁成功，返回成功信息
        return ResponseResult.okResult("用户解锁成功！");
    }

    /**
     * 处理用户登出请求
     * 该方法通过移除会话中所有的用户相关属性来实现登出功能
     *
     * @param session HttpSession对象，用于存储用户会话信息
     * @return 返回一个ResponseResult对象，表示登出操作的结果
     */
    @GetMapping("/logout")
    public ResponseResult logout(HttpSession session){
        // 检查会话中是否存在用户是否为管理员的属性，并移除
        if (session.getAttribute("UserIsAdmin")!=null){
            session.removeAttribute("UserIsAdmin");
        }
        // 检查会话中是否存在用户名属性，并移除
        if (session.getAttribute("UserName")!=null){
            session.removeAttribute("UserName");
        }
        // 检查会话中是否存在用户角色属性，并移除
        if (session.getAttribute("UserRole")!=null){
            session.removeAttribute("UserRole");
        }
        // 检查会话中是否存在用户邮箱属性，并移除
        if (session.getAttribute("UserEmail")!=null){
            session.removeAttribute("UserEmail");
        }
        // 检查会话中是否存在用户电话属性，并移除
        if (session.getAttribute("UserPhone")!=null){
            session.removeAttribute("UserPhone");
        }
        // 检查会话中是否存在用户名属性，并移除
        if (session.getAttribute("UserFirstName")!=null){
            session.removeAttribute("UserFirstName");
        }
        // 检查会话中是否存在用户姓氏属性，并移除
        if (session.getAttribute("UserLastName")!=null){
            session.removeAttribute("UserLastName");
        }
        // 检查会话中是否存在用户出生日期属性，并移除
        if (session.getAttribute("UserBirthDate")!=null){
            session.removeAttribute("UserBirthDate");
        }
        // 检查会话中是否存在用户登录状态属性，并移除
        if (session.getAttribute("UserIsLogin")!=null){
            session.removeAttribute("UserIsLogin");
        }
        // 检查会话中是否存在用户ID属性，并移除
        if (session.getAttribute("UserId")!=null){
            session.removeAttribute("userId");
        }
        // 检查会话中是否存在用户默认地址ID属性，并移除
        if (session.getAttribute("UserDefaultAddressId")!=null){
            session.removeAttribute("UserDefaultAddressId");
        }
        // 返回登出成功的结果
        return ResponseResult.okResult();
    }

    /**
     * 检查用户是否登录
     * 通过检查会话（session）中的用户信息来判断用户是否已登录
     * 如果用户已登录，则返回用户的详细信息
     *
     * @param session HTTP会话，用于获取用户登录状态和相关信息
     * @return ResponseResult 包含用户是否登录的结果或用户详细信息
     */
    @GetMapping("/isLogin")
    public ResponseResult isLogin(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkUserLogin(session);
		if (!result.isSuccess()) {
		    // 如果未登录，则直接返回
		    return result;
		}
	    // 获取用户ID
	    Integer userId = (Integer) result.getData();
        // 从会话中获取用户名、邮箱和角色ID
        String username = (String) session.getAttribute("UserName");
        String email = (String) session.getAttribute("UserEmail");
        Integer roleId = (Integer) session.getAttribute("UserRole");

        // 检查会话中是否设置用户是否为管理员的标志
        if (session.getAttribute("UserIsAdmin") == null){
            // 如果未设置，则返回系统错误
            return ResponseResult.serverErrorResult("系统错误！");
        }
        // 将用户是否为管理员的标志转换为布尔值
        boolean isAdmin = !session.getAttribute("UserIsAdmin").equals("false");

        // 创建UserVo对象以封装用户信息
        UserVo userVo = new UserVo(userId,username,email,isAdmin,roleId);

        // 尝试从会话中获取并设置用户的电话号码
        if (session.getAttribute("UserPhone") != null){
            userVo.setPhone((String) session.getAttribute("UserPhone"));
        }
        // 尝试从会话中获取并设置用户的名
        if (session.getAttribute("UserFirstName") != null){
            userVo.setFirstName((String) session.getAttribute("UserFirstName"));
        }
        // 尝试从会话中获取并设置用户的姓
        if (session.getAttribute("UserLastName") != null){
            userVo.setLastName((String) session.getAttribute("UserLastName"));
        }
        // 尝试从会话中获取并设置用户的出生日期，并计算下个生日的天数
        if (session.getAttribute("UserBirthDate") != null){
            userVo.setBirthDate((Date) session.getAttribute("UserBirthDate"));
            userVo.setNextBirthday(TimeUtils.getDaysUntilNextBirthday(userVo.getBirthDate()));
        }
        // 尝试从会话中获取并设置用户的默认地址ID
        if (session.getAttribute("UserDefaultAddressId") != null){
            userVo.setDefaultAddressId((Integer) session.getAttribute("UserDefaultAddressId"));
        }

        // 返回包含用户信息的结果
        return ResponseResult.okResult(userVo);
    }

    /**
     * 检查当前用户是否为管理员用户
     *
     * @param session HTTP会话对象，用于获取用户是否为管理员的信息
     * @return 如果会话中没有"UserIsAdmin"属性，返回服务器错误结果；
     *         如果"UserIsAdmin"属性值为"false"，返回OK结果包含false；
     *         否则，返回OK结果包含true，表示用户是管理员
     */
    @GetMapping("/isAdminUser")
    public ResponseResult isAdminUser(HttpSession session){
        // 检查会话中是否含有用户是否为管理员的标记
        if (session.getAttribute("UserIsAdmin") == null){
            // 如果没有该标记，返回服务器错误
            return ResponseResult.serverErrorResult("系统错误！");
        }else {
            // 如果管理员标记为"false"，表示用户不是管理员
            if (session.getAttribute("UserIsAdmin").equals("false")){
                // 返回OK结果，包含标志值false
                return ResponseResult.okResult(false);
            }else {
                // 其他情况下，默认用户是管理员
                // 返回OK结果，包含标志值true
                return ResponseResult.okResult(true);
            }
        }
    }

    /**
     * 获取距离下一次生日的天数
     *
     * @param session HttpSession对象，用于获取用户 session 信息
     * @return ResponseResult包含距离下一次生日的天数或错误信息
     *
     * 本方法首先检查用户是否设置了生日（通过UserBirthDate键存储在session中），
     * 如果没有设置或者设置的值是"null"字符串，则返回失败结果并提示用户未设置生日。
     * 如果生日已设置，则计算距离下一次生日的天数，并返回成功结果。
     */
    @GetMapping("/getDays")
    public ResponseResult getDaysNextBirthday(HttpSession session){
        ResponseResult result = checkUserLogin(session);
		if (!result.isSuccess()) {
		    // 如果未登录，则直接返回
		    return result;
		}
        // 检查session中是否设置了用户生日
        if (session.getAttribute("UserBirthDate") == null){
            return ResponseResult.failResult("未设置生日！");
        }
        // 检查用户生日是否为空字符串
        if (session.getAttribute("UserBirthDate").equals("")){
            return ResponseResult.failResult("未设置生日！");
        }
        // 检查用户生日是否为"null"字符串
        if (session.getAttribute("UserBirthDate").equals("null")){
            return ResponseResult.failResult("未设置生日！");
        }
        // 从session中获取用户生日日期
        Date birthDate = (Date) session.getAttribute("UserBirthDate");
        // 计算并返回距离下一次生日的天数
        return ResponseResult.okResult(getDaysUntilNextBirthday(birthDate));
    }

    @GetMapping("/getList")
    public ResponseResult getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "5") Integer pageSize,
                                      HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
        if (!result.isSuccess()) {
            return result;
        }
        List<UserVo> userList = userService.getUserList(pageNum,pageSize,(Integer)result.getData());
        if (userList == null){
            return ResponseResult.failResult("获取用户列表失败！");
        }
        return ResponseResult.okResult(userList);
    }

    @GetMapping("/getNum")
    public ResponseResult getUserNum(HttpSession session){
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = checkAdminUser(session);
        if (!result.isSuccess()) {
            return result;
        }
        return ResponseResult.okResult(userService.getUserNum());
    }
}
