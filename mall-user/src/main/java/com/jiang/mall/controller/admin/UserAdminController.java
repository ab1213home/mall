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

package com.jiang.mall.controller.admin;

import com.jiang.mall.annotation.Permission;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.enums.PermissionType;
import com.jiang.mall.domain.vo.UserAdminVo;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/user/admin")
public class UserAdminController {

    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private II18nService i18nService;

    @Autowired
    public void setI18nService(II18nService i18nService) {
        this.i18nService = i18nService;
    }

    private GeneralConfig generalConfig;

    @Autowired
    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }


    @GetMapping("/getList")
    @Permission(value = PermissionType.SYSTEM, permission = "user:list")
    public ResponseResult<Object> getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "20") Integer pageSize
                                              ){
        List<UserAdminVo> userList = userService.getUserList(pageNum,pageSize);
        if (userList == null){
            return ResponseResult.failResult("获取用户列表失败！");
        }
        return ResponseResult.okResult(userList);
    }

    @GetMapping("/getNum")
    @Permission(value = PermissionType.SYSTEM, permission = "user:list")
    public ResponseResult<Object> getUserNum(HttpSession session){
        return ResponseResult.okResult(userService.getUserNum());
    }

    @PostMapping("/update")
    @Permission(value = PermissionType.SYSTEM, permission = "user:update")
    public ResponseResult<Object> updateUser(@RequestParam("id") Long id,
                                             @RequestParam("phone") String phone,
                                             @RequestParam("firstName") String firstName,
                                             @RequestParam("lastName") String lastName,
                                             @RequestParam("birthDate") String birthDate,
                                             @RequestParam("avatar") String avatar,
                                             HttpSession session) {
        if (!i18nService.checkId(id)){
            return ResponseResult.failResult(i18nService.getMessage("id.error"));
        }
        User user = new User();
        user.setId(id);
        user.setPhone(phone);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAvatar(avatar);
        // 验证和转换生日日期格式
        if (birthDate != null){
            try {
                LocalDate localDate = LocalDate.parse(birthDate, generalConfig.getDateFormatPattern());
                // 检查生日是否在过去
                if (localDate.isAfter(LocalDate.now())) {
                    return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.future"));
                }
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                user.setBirthDate(date);
            } catch (DateTimeParseException e) {
                return ResponseResult.failResult(i18nService.getMessage("user.error.birthday.format"));
            }
        }
//        if (!userService.updateUser(user)){
//            return ResponseResult.serverErrorResult(i18nService.getMessage("user.update.error"));
//        }else {
//            return ResponseResult.okResult(i18nService.getMessage("user.update.success"));
//        }
        return ResponseResult.okResult();
    }

    @GetMapping("/getOnlineList")
    @Permission(value = PermissionType.SYSTEM, permission = "user:list")
    public ResponseResult<Object> getOnlineList(){
        return ResponseResult.okResult(userService.getOnlineUser());
    }

    /**
     * 管理员锁定账户功能
     * 该方法允许管理员锁定其账户
     *
     * @param userId 用户ID，用于标识需要锁定的用户
     * @param session HttpSession对象，用于检查用户是否已登录及权限验证
     * @return ResponseResult表示操作结果，包含成功、失败、未找到资源、服务器错误等状态
     */
    @PostMapping("/lock")
    @Permission(value = PermissionType.SYSTEM, permission = "user:lock")
    public ResponseResult<Object> selfLock(@RequestParam("userId") Long userId,
                                           @RequestHeader("X-Real-IP") String clientIp,
                                           @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                           HttpSession session) {
        if (!i18nService.checkId(userId)){
            return ResponseResult.failResult(i18nService.getMessage("id.error"));
        }

		UserCache user = userService.getUserFromRedis(session.getId());
		if (Objects.equals(user.getId(), userId)){
			return ResponseResult.serverErrorResult(i18nService.getMessage("user.lock.error"));
		}
        Boolean lock = userService.lock(userId, session.getId(), clientIp, fingerprint);
        // 尝试锁定用户
        if (lock==null){
            // 如果用户不存在，则返回未找到资源的错误信息
            return ResponseResult.notFoundResourceResult(i18nService.getMessage("user.modify.lock.error.notFound"));
        }else if (lock){
            // 锁定成功，返回成功信息
            return ResponseResult.okResult(i18nService.getMessage("user.lock.success"));
        }else {
            // 锁定失败，返回错误信息
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.lock.error"));
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
    @PostMapping("/unlock")
    @Permission(value = PermissionType.SYSTEM, permission = "user:lock")
    public ResponseResult<Object> unlockUser(@RequestParam("userId") Long userId,
											 @RequestHeader("X-Real-IP") String clientIp,
											 @RequestHeader("X-Real-FINGERPRINT") String fingerprint,
                                             HttpSession session) {
        if (!i18nService.checkId(userId)){
            return ResponseResult.failResult(i18nService.getMessage("id.error"));
        }

        Boolean unlock = userService.unlock(userId,session.getId(), clientIp, fingerprint);

        if (unlock==null){
            // 如果用户不存在，则返回未找到资源的错误信息
            return ResponseResult.notFoundResourceResult(i18nService.getMessage("user.modify.lock.error.notFound"));
        }else if (!unlock){
            // 解锁失败，返回错误信息
            return ResponseResult.serverErrorResult(i18nService.getMessage("user.unlock.error"));
        }else {
            // 解锁成功，返回成功信息
            return ResponseResult.okResult(i18nService.getMessage("user.unlock.success"));
        }
    }

}
