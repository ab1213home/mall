/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.controller.User;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserRecordService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;

import static com.jiang.mall.controller.User.LoginController.logout;
import static com.jiang.mall.domain.config.User.*;
import static com.jiang.mall.util.EncryptAndDecryptUtils.isSha256Hash;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user")
public class ModifyController {

	private IUserService userService;

    /**
     * 设置用户服务实例
     *
     * @param userService 用户服务实例
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private IVerificationCodeService verificationCodeService;

    /**
     * 设置验证码服务实例
     *
     * @param verificationCodeService 验证码服务实例
     */
    @Autowired
    public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    private IUserRecordService userRecordService;

    @Autowired
    public void setLoginRecordService(IUserRecordService userRecordService) {
        this.userRecordService = userRecordService;
    }

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	    @PostMapping("/modify/email")
    public ResponseResult<Object> modifyEmail(@RequestParam("email") String email,
                                      @RequestParam("code") String code,
                                      HttpSession session) {
        if (email==null||code==null){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(email)||!email.matches(regex_email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }
        if (!StringUtils.hasText(code)){
            return ResponseResult.failResult("验证码不能为空");
        }
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            return result;
        }
        Long userId = (Long) result.getData();
        VerificationCode userVerificationCode = verificationCodeService.queryCodeByEmail(email);
        if (userVerificationCode ==null){
            return ResponseResult.failResult("验证码错误或已过期");
        }
        if (!Objects.equals(userVerificationCode.getCode(),code)){
            return ResponseResult.failResult("验证码错误");
        }
//        User user = userService.getUserInfo(userId);
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        if (userService.updateById(user)) {
            verificationCodeService.useCode(userId, userVerificationCode);
            userRecordService.successModifyEmailRecord(user, email);
            return ResponseResult.okResult();
        }else {
            return ResponseResult.serverErrorResult("未知原因修改邮箱失败");
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
    public ResponseResult<Object> modifyPassword(@RequestParam("oldPassword") String oldPassword,
                                         @RequestParam("newPassword") String newPassword,
                                         @RequestParam("confirmPassword")String confirmPassword,
                                         HttpSession session) {
        if (oldPassword==null||newPassword==null||confirmPassword==null){
            return ResponseResult.failResult("参数错误");
        }
        // 检查新密码是否为空
        if (!isSha256Hash(newPassword)){
            return ResponseResult.failResult("新密码不能为空！");
        }
        if (!isSha256Hash(confirmPassword)){
            return ResponseResult.failResult("确认密码不能为空！");
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
        ResponseResult<Object> result = userService.checkUserLogin(session);
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
    public ResponseResult<Object> modifyUserInfo(@RequestParam(required = false) Long id,
                                         @RequestParam(required = false) String phone,
                                         @RequestParam(required = false) String firstName,
                                         @RequestParam(required = false) String lastName,
                                         @RequestParam(required = false) String birthDate,
                                         @RequestParam(required = false) String email,
                                         @RequestParam(required = false) String img,
                                         @RequestParam(required = false) boolean isAdmin,
                                         @RequestParam(required = false) Integer roleId,
                                         HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            return result; // 如果未登录，则直接返回
        }
        Long userId = (Long) result.getData();

        // 验证手机号格式是否正确
        if (StringUtils.hasText(phone)&&!phone.matches(regex_phone)) {
            return ResponseResult.failResult("手机号格式不正确");
        }

        // 设置用户ID到用户信息对象中
        User userInfo = new User(userId, firstName, lastName, phone,img);
        // 验证和转换生日日期格式
        if (birthDate != null){
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
        }
        if (id != null) {
            // 管理员后台修改信息时，不允许修改自己的信息
            if (Objects.equals(id, userId)) {
                return ResponseResult.failResult("不能通过管理后台修改自己的信息");
            }
            // 验证邮箱格式是否正确
            if (StringUtils.hasText(email) || !email.matches(regex_email)) {
                return ResponseResult.failResult("邮箱格式不正确");
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
            userInfo.setEmail(email);
            if (userService.updateUser(userInfo)) {
                return ResponseResult.okResult("修改成功");
            } else {
                return ResponseResult.serverErrorResult("修改失败");
            }
        } else {
            // 尝试修改用户信息，如果失败则返回错误结果
            if (!userService.modifyUserInfo(userInfo))
                return ResponseResult.serverErrorResult("修改失败！");
            // 更新会话中的用户信息属性
            UserVo user= (UserVo) session.getAttribute("User");
            user.setPhone(userInfo.getPhone());
            user.setFirstName(userInfo.getFirstName());
            user.setLastName(userInfo.getLastName());
            user.setBirthDate(userInfo.getBirthDate());
            session.setAttribute("User",user);
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
    public ResponseResult<Object> lockUser(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        // 获取已登录用户的ID
        Long userId = (Long) result.getData();
        // 尝试锁定用户，如果失败则返回错误信息
        if (userService.lockUser(userId))
            return ResponseResult.serverErrorResult("修改失败！");
        // 用户锁定成功，返回成功信息
        logout(session);
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
    public ResponseResult<Object> selfLock(@RequestParam("userId") Long userId,
                                   HttpSession session) {
        if (userId == null|| userId <= 0) {
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(userId.toString())){
            return ResponseResult.failResult("请输入用户ID");
        }
        // 根据userId获取用户信息
        User user = userService.getUserInfo(userId);
        // 如果用户不存在，则返回未找到资源的错误信息
        if (user == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 检查当前会话是否拥有操作权限
        ResponseResult<Object> result = userService.hasPermission(user.getId(), session);
        // 如果用户未登录或权限不足，则返回相应的错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试锁定用户
        if (userService.lockUser(userId)){
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
    public ResponseResult<Object> unlockUser(@RequestParam("userId") Long userId,
                                     HttpSession session) {
        if (userId == null|| userId <= 0) {
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(userId.toString())){
            return ResponseResult.failResult("请输入用户ID");
        }
        // 根据userId获取用户信息
        User user = userService.getUserInfo(userId);
        // 如果用户不存在，则返回未找到资源的错误信息
        if (user == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 检查当前会话是否拥有操作权限
        ResponseResult<Object> result = userService.hasPermission(user.getUpdater(), session);
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
}