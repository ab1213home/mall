package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.UserCode;
import com.jiang.mall.service.IUserCodeService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.EmailUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

import static com.jiang.mall.domain.entity.Config.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private IUserCodeService userCodeService;

    @Autowired
    private IUserService userService;

    @PostMapping("/sendRegister")
    public ResponseResult sendRegister(@RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       @RequestParam("captcha") String captcha,
                                       HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("UserIsLogin")!=null){
            if ("true".equals(session.getAttribute("UserIsLogin"))) {
	            return ResponseResult.failResult("您已登录，请勿重复注册");
            }
        }
        if (!AllowSendEmail){
			return ResponseResult.failResult("管理员不允许发送邮件");
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

        // 检查注册信息是否完整
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(confirmPassword)||!StringUtils.hasText(email)) {
            return ResponseResult.failResult("请输入完整的注册信息");
        }

        // 验证密码一致性
        if (!password.equals(confirmPassword)) {
            return ResponseResult.failResult("两次密码输入不一致");
        }
        if (userCodeService.inspectByEmail(email)){
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }
        // 检查用户名是否已注册
        if (userService.queryByUserName(username)) {
            return ResponseResult.failResult("用户名已存在");
        }
        String code = generateRandomCode(8);
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+username+"用户，您正在尝试使用"+EmailPurpose.REGISTER.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        if (EmailUtils.sendEmail(email, "【"+SENDER_END+"】验证码通知", htmlContent)){
            UserCode userCode = new UserCode(username,email, password, code, EmailPurpose.REGISTER, EmailStatus.SUCCESS);
            if (userCodeService.save(userCode)){
                return ResponseResult.okResult();
            }else {
                return ResponseResult.serverErrorResult("未知原因注册失败");
            }
        }else {
            UserCode userCode = new UserCode(username,email, password, code, EmailPurpose.REGISTER, EmailStatus.FAILED);
            userCodeService.save(userCode);
            return ResponseResult.failResult("邮件发送失败，请重试");
        }
    }

    @GetMapping("/getChecking")
    public ResponseResult getChecking(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        if (userCodeService.checkingByUserId((Integer)result.getData())){
            return ResponseResult.okResult("在"+expiration_time+"分钟内已经验证通过");
        }
        return ResponseResult.failResult("验证码已过期，请重新获取");
    }

    /**
     * 发送重置密码验证码
     *
     * @param username 用户名（可选）
     * @param email 邮箱（可选）
     * @param captcha 验证码（必填）
     * @param session HTTP会话
     * @return 操作结果
     */
    @PostMapping("/sendResetPassword")
    public ResponseResult sendResetPassword(@RequestParam(required = false) String username,
                                            @RequestParam(required = false) String email,
                                            @RequestParam("captcha") String captcha,
                                            HttpSession session) {
        // 检查系统是否允许发送邮件
        if (!AllowSendEmail) {
            return ResponseResult.failResult("管理员不允许发送邮件");
        }
        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
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
        // 检查用户名和邮箱参数
        if (StringUtils.hasText(email) && StringUtils.hasText(username)) {
            return ResponseResult.failResult("用户名和邮箱不能同时有值");
        } else if (!StringUtils.hasText(email) && !StringUtils.hasText(username)) {
            return ResponseResult.failResult("用户名或邮箱必须有一个有值");
        }
        // 验证用户名或邮箱是否存在
        else if (StringUtils.hasText(username) && !userService.queryByUserName(username)) {
            return ResponseResult.failResult("用户名不存在");
        }else if (StringUtils.hasText(email) && !email.matches(regex_email)&&!userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱不存在");
        }
        // 获取用户信息
        User user;
        if (StringUtils.hasText(username)) {
            user = userService.getUserByUserName(username);
        }else{
            user = userService.getUserByEmail(email);
        }
        // 再次验证用户是否存在
        if (user == null) {
            return ResponseResult.failResult("用户不存在");
        }
        // 检查邮箱是否请求过多验证码
        if (userCodeService.inspectByEmail(user.getEmail())) {
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 生成验证码和邮件内容
        String code = generateRandomCode(8);
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+user.getUsername()+"用户，您正在尝试使用"+EmailPurpose.REGISTER.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        // 发送邮件并处理结果
        if (EmailUtils.sendEmail(email, "【"+SENDER_END+"】验证码通知", htmlContent)){
            UserCode userCode = new UserCode(username,email , code, EmailPurpose.RESET_PASSWORD, EmailStatus.SUCCESS, user.getId());
            if (userCodeService.save(userCode)){
                return ResponseResult.okResult();
            }else {
                return ResponseResult.serverErrorResult("未知原因重置密码失败");
            }
        }else {
            UserCode userCode = new UserCode(username,email , code, EmailPurpose.RESET_PASSWORD, EmailStatus.FAILED, user.getId());
            userCodeService.save(userCode);
            return ResponseResult.failResult("邮件发送失败，请重试");
        }
    }

    @PostMapping("/sendChecking")
    public ResponseResult sendChecking(@RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("captcha") String captcha,
                                       HttpSession session) {
        if (!AllowSendEmail){
			return ResponseResult.failResult("管理员不允许发送邮件");
		}
        ResponseResult result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
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
        if (userCodeService.inspectByEmail(email)){
             return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }

        String code = generateRandomCode(8);
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+username+"用户，您正在尝试使用"+EmailPurpose.REGISTER.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        if (EmailUtils.sendEmail(email, "【"+SENDER_END+"】验证码通知", htmlContent)){
            UserCode userCode = new UserCode(username,email, code, EmailPurpose.RESET_PASSWORD, EmailStatus.SUCCESS, (Integer)result.getData());
            if (userCodeService.save(userCode)){
                return ResponseResult.okResult();
            }else {
                return ResponseResult.serverErrorResult("未知原因注册失败");
            }
        }else {
            UserCode userCode = new UserCode(username,email , code, EmailPurpose.RESET_PASSWORD, EmailStatus.FAILED, (Integer)result.getData());
            userCodeService.save(userCode);
            return ResponseResult.failResult("邮件发送失败，请重试");
        }
    }

    public static String generateRandomCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // 生成0到9之间的随机数字
            sb.append(digit);
        }

        return sb.toString();
    }
}