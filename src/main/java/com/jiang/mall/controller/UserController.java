package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
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

import static com.jiang.mall.domain.entity.Propertie.*;
import static com.jiang.mall.util.TimeUtils.getDaysUntilNextBirthday;

/**
 * <p>
 * 前端控制器
 * </p>
 *作者： 蒋神 HJL
 * @since 2024-08-05
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/registerStep1")
    public ResponseResult registerStep1(@RequestParam("username") String username,
                                             @RequestParam("password") String password,
                                             @RequestParam("confirmPassword") String confirmPassword,
                                             @RequestParam("email") String email,
                                             @RequestParam("captcha") String captcha,
                                             HttpSession session) {
        if (session.getAttribute("UserIsLogin")!=null){
            if (session.getAttribute("UserIsLogin").equals("true"))
                return ResponseResult.failResult("您已登录，请勿重复注册");
        }
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }
        if (StringUtils.hasText(email) && !email.matches(regex_email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }

        // 获取session中的验证码
        Object captchaObj = session.getAttribute("captcha");
        if (captchaObj == null) {
            return ResponseResult.failResult("会话中的验证码已过期，请重新获取");
        }
        String captchaCode = captchaObj.toString();

        // 判断验证码
        if (!captchaCode.toLowerCase().equals(captcha)) {
            return ResponseResult.failResult("验证码错误");
        }
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)||!StringUtils.hasText(email)) {
            return ResponseResult.failResult("请输入完整的注册信息");
        }
        if (!password.equals(confirmPassword)) {
            return ResponseResult.failResult("两次密码输入不一致");
        }

        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }

        if (userService.queryByUserName(username)) {
            return ResponseResult.failResult("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        int UserId = userService.registerStep(user);
        if (UserId>0) {
            session.setAttribute("UserId",UserId);
            return ResponseResult.okResult();
        }else {
            return ResponseResult.failResult("未知原因注册失败");
        }
    }

    @PostMapping("/registerStep2")
    public ResponseResult registerStep2(@RequestParam("phone") String phone,
                                @RequestParam("firstName") String firstName,
                                @RequestParam("lastName") String lastName,
                                @RequestParam("birthday") String birthDate,
                                HttpSession session) {
        // 获取session中的账号id
        if (session.getAttribute("UserId")==null){
            return ResponseResult.failResult("请先完成第一步注册，会话已过期");
        }
        if (session.getAttribute("UserIsLogin")!=null){
            if (session.getAttribute("UserIsLogin").equals("true"))
                return ResponseResult.failResult("您已登录，请勿重复注册");
        }
        int UserId = (int) session.getAttribute("UserId");

        if (StringUtils.hasText(phone) && !phone.matches(regex_phone)){
            return ResponseResult.failResult("手机号格式不正确");
        }

        User user = new User();
        user.setId(UserId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate localDate = LocalDate.parse(birthDate, formatter);
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            user.setBirthDate(date);
        } catch (DateTimeParseException e) {
            return ResponseResult.failResult("日期格式不正确，请使用yyyy-MM-dd");
        }
        user.setPhone(phone);
        if (userService.registerStep(user)>0) {
            session.removeAttribute("UserId");
            return ResponseResult.okResult();
        }else {
            return ResponseResult.failResult("用户个人信息保存失败");
        }
    }

    @PostMapping(value = "/login")
    public ResponseResult login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("captcha") String captcha,
                        HttpSession session) {
        if (session.getAttribute("UserIsLogin")!=null){
            if (session.getAttribute("UserIsLogin").equals("true"))
                return ResponseResult.failResult("您已登录，请勿重复登录");
        }
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }
        // 获取session中的验证码
        Object captchaObj = session.getAttribute("captcha");
        if (captchaObj == null) {
            return ResponseResult.failResult("会话中的验证码已过期，请重新获取");
        }
        String captchaCode = captchaObj.toString();

        // 判断验证码
        if (!captchaCode.toLowerCase().equals(captcha)) {
            session.removeAttribute("captcha");
            return ResponseResult.failResult("验证码错误");
        }
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ResponseResult.failResult("用户名或密码不能为空");
        }

        User user = userService.login(username, password);
        if (user != null) {
            //session.removeAttribute("captcha");
            session.setAttribute("UserId", user.getId());
            session.setAttribute("UserName", username);
            session.setAttribute("UserRole", user.getRoleId());
            session.setAttribute("UserEmail", user.getEmail());
            session.setAttribute("UserPhone", user.getPhone());
            session.setAttribute("UserFirstName", user.getFirstName());
            session.setAttribute("UserLastName", user.getLastName());
            session.setAttribute("UserBirthDate", user.getBirthDate());
            if (user.getRoleId() >= AdminRoleId) {
                session.setAttribute("UserIsAdmin", "true");
            } else {
                session.setAttribute("UserIsAdmin", "false");
            }
            session.setAttribute("UserIsLogin","true");
            //session过期时间设置为7200秒
            session.setMaxInactiveInterval(60 * 60 * 2);
            return ResponseResult.okResult();
        } else {
            return ResponseResult.failResult("用户不存在或用户名或密码错误");
        }
    }

    @PostMapping("/modify/password")
    public ResponseResult modifyPassword(@RequestParam("oldPassword") String oldPassword,
                                  @RequestParam("newPassword") String newPassword,
                                  @RequestParam("confirmPassword")String confirmPassword,
                                  HttpSession session) {
        if (newPassword.isEmpty()){
            return ResponseResult.failResult("新密码不能为空！");
        }
        if (!newPassword.equals(confirmPassword)){
            return ResponseResult.failResult("两次输入的密码不一致！");
        }
        if (newPassword.equals(oldPassword)){
            return ResponseResult.failResult("新旧密码不能相同！");
        }
        if (!StringUtils.hasText(oldPassword)){
            return ResponseResult.failResult("旧密码不能为空！");
        }
        if (session.getAttribute("UserIsLogin")==null){
            return ResponseResult.failResult("请先登录");
        }
        if (session.getAttribute("UserId")==null){
            return ResponseResult.failResult("请先登录");
        }
        Integer UserId = (Integer) session.getAttribute("UserId");
        if (!userService.modifyPassword(UserId, oldPassword, newPassword)){
            return ResponseResult.failResult("修改失败！");
        }
        if (session.getAttribute("UserIsAdmin")!=null){
            session.removeAttribute("UserIsAdmin");
        }
        if (session.getAttribute("UserName")!=null){
            session.removeAttribute("UserName");
        }
        if (session.getAttribute("UserRole")!=null){
            session.removeAttribute("UserRole");
        }
        if (session.getAttribute("UserEmail")!=null){
            session.removeAttribute("UserEmail");
        }
        if (session.getAttribute("UserPhone")!=null){
            session.removeAttribute("UserPhone");
        }
        if (session.getAttribute("UserFirstName")!=null){
            session.removeAttribute("UserFirstName");
        }
        if (session.getAttribute("UserLastName")!=null){
            session.removeAttribute("UserLastName");
        }
        if (session.getAttribute("UserBirthDate")!=null){
            session.removeAttribute("UserBirthDate");
        }
        if (session.getAttribute("UserIsLogin")!=null){
            session.removeAttribute("UserIsLogin");
        }
        if (session.getAttribute("UserId")!=null){
            session.removeAttribute("UserId");
        }
        return ResponseResult.okResult("修改密码成功！");
    }

    @PostMapping("/modify/info")
    public ResponseResult modifyUserInfo(@RequestBody User userInfo,
                                         HttpSession session) {
        if (session.getAttribute("UserIsLogin")==null){
            return ResponseResult.failResult("请先登录");
        }
        if (session.getAttribute("UserId")==null){
            return ResponseResult.failResult("请先登录");
        }
        if (StringUtils.hasText(userInfo.getPhone()) && !userInfo.getPhone().matches(regex_phone)){
            return ResponseResult.failResult("手机号格式不正确");
        }
        if (StringUtils.hasText(userInfo.getEmail()) && !userInfo.getEmail().matches(regex_email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }
        if (userService.queryByEmail(userInfo.getEmail())){
            return ResponseResult.failResult("邮箱已存在");
        }
        Integer userId = (Integer) session.getAttribute("UserId");
        userInfo.setId(userId);
        if (!userService.modifyUserInfo(userInfo))
            return ResponseResult.failResult("修改失败！");
        return ResponseResult.okResult("用户信息更新成功！");
    }

    @PostMapping("/modify/lock")
    public ResponseResult lockUser(HttpSession session) {
        if (session.getAttribute("UserIsLogin")==null){
            return ResponseResult.failResult("请先登录");
        }
        if (session.getAttribute("UserId")==null){
            return ResponseResult.failResult("请先登录");
        }
        Integer UserId = (Integer) session.getAttribute("UserId");
        if (!userService.lockUser(UserId))
            return ResponseResult.failResult("修改失败！");
        return ResponseResult.okResult("用户锁定成功！");
    }


    @PostMapping("/modify/self-lock")
    public ResponseResult selfLock(@RequestParam("UserId") Integer UserId, HttpSession session) {
		User user = userService.getUserInfo(UserId);
		User adminUser = userService.getUserInfo((Integer) session.getAttribute("UserId"));
		if (adminUser.getRoleId() >user.getRoleId()){
            if (!userService.lockUser(UserId)){
                return ResponseResult.failResult("用户锁定失败！");
            }
            return ResponseResult.okResult("用户锁定成功！");
		}else {
			return ResponseResult.failResult("权限不足，修改失败！");
		}
    }


//    函数用于处理HTTP GET请求"/logout"。它接收一个HTTP会话参数session，
//    移除其中的"userId"信息，然后返回一个表示操作成功的响应结果。
    @GetMapping("/logout")
    public ResponseResult logout(HttpSession session){
        if (session.getAttribute("UserIsAdmin")!=null){
            session.removeAttribute("UserIsAdmin");
        }
        if (session.getAttribute("UserName")!=null){
            session.removeAttribute("UserName");
        }
        if (session.getAttribute("UserRole")!=null){
            session.removeAttribute("UserRole");
        }
        if (session.getAttribute("UserEmail")!=null){
            session.removeAttribute("UserEmail");
        }
        if (session.getAttribute("UserPhone")!=null){
            session.removeAttribute("UserPhone");
        }
        if (session.getAttribute("UserFirstName")!=null){
            session.removeAttribute("UserFirstName");
        }
        if (session.getAttribute("UserLastName")!=null){
            session.removeAttribute("UserLastName");
        }
        if (session.getAttribute("UserBirthDate")!=null){
            session.removeAttribute("UserBirthDate");
        }
        if (session.getAttribute("UserIsLogin")!=null){
            session.removeAttribute("UserIsLogin");
        }
        if (session.getAttribute("UserId")!=null){
            session.removeAttribute("UserId");
        }
        return ResponseResult.okResult();
    }


//    检查用户是否已登录。
    @GetMapping("/isLogin")
    public ResponseResult isLogin(HttpSession session){
        if (session.getAttribute("UserId") == null){
            return ResponseResult.failResult("未登录！");
        }
        if (session.getAttribute("UserIsLogin") == null){
            return ResponseResult.failResult("未登录！");
        }
        if (session.getAttribute("UserIsLogin").equals("false")){
            return ResponseResult.failResult("未登录！");
        }
        Integer userId = (Integer) session.getAttribute("UserId");
        String username = (String) session.getAttribute("UserName");
        UserVo user = new UserVo(userId, username);
        return ResponseResult.okResult(user);
    }

    @GetMapping("/isAdminUser")
    public ResponseResult isAdminUser(HttpSession session){
        if (session.getAttribute("UserIsAdmin") == null){
            return ResponseResult.failResult("系统错误！");
        }else {
            if (session.getAttribute("UserIsAdmin").equals("false")){
                return ResponseResult.okResult(false);
            }else {
                return ResponseResult.okResult(true);
            }
        }
    }


    @GetMapping("/getDays")
    public ResponseResult getDaysNextBirthday(HttpSession session){
        if (session.getAttribute("UserBirthDate") == null){
            return ResponseResult.failResult("未设置生日！");
        }
        if (session.getAttribute("UserBirthDate").equals("")){
            return ResponseResult.failResult("未设置生日！");
        }
        if (session.getAttribute("UserBirthDate").equals("null")){
            return ResponseResult.failResult("未设置生日！");
        }
        Date birthDate = (Date) session.getAttribute("UserBirthDate");
        return ResponseResult.okResult(getDaysUntilNextBirthday(birthDate));
    }


//    通过HTTP GET请求提供分页查询用户列表的服务，
//    允许客户端通过网页URL指定或使用默认值来确定查询的页码和每页显示数量，
//    并将查询结果以封装好的形式返回给客户端。
    @GetMapping("/admin/list")
    public ResponseResult getUserList(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        return userService.getUserList(pageNum,pageSize);
    }


//    当客户端发送GET请求到/admin/{id}这个URL，
//    并提供一个具体的id时，服务器会调用userService.getUser(id)方法，从数据库中查找对应ID的用户信息，
//    并将查询结果封装成ResponseResult对象返回给客户端。
    @GetMapping("/admin/{id}")
    public ResponseResult getUser(@PathVariable("id") Integer id){
        return userService.getUser(id);
    }


//    理用户信息更新的请求，将请求体中的用户信息更新到系统中。
    @PostMapping("/admin/update")
    public ResponseResult updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }



//    通过POST请求访问"/admin/delete"路径，接收一个包含多个整数ID的请求体，
//    然后调用userService对象的deleteUser方法，传入接收到的ID列表，
//    并返回deleteUser方法的返回结果。
    @PostMapping("/admin/delete")
    public ResponseResult deleteUser(@RequestBody List<Integer> ids){
        return userService.deleteUser(ids);
    }

}
