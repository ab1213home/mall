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
                                              @RequestParam(defaultValue = "20") Integer pageSize,
                                              HttpSession session){
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

}
