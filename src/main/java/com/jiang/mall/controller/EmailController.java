/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 *
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 *
 * Mulan Permissive Software License，Version 2
 *
 * Mulan Permissive Software License，Version 2 (Mulan PSL v2)
 *
 * January 2020 http://license.coscl.org.cn/MulanPSL2
 *
 * Your reproduction, use, modification and distribution of the Software shall
 * be subject to Mulan PSL v2 (this License) with the following terms and
 * conditions:
 *
 * 0. Definition
 *
 * Software means the program and related documents which are licensed under
 * this License and comprise all Contribution(s).
 *
 * Contribution means the copyrightable work licensed by a particular
 * Contributor under this License.
 *
 * Contributor means the Individual or Legal Entity who licenses its
 * copyrightable work under this License.
 *
 * Legal Entity means the entity making a Contribution and all its
 * Affiliates.
 *
 * Affiliates means entities that control, are controlled by, or are under
 * common control with the acting entity under this License, ‘control’ means
 * direct or indirect ownership of at least fifty percent (50%) of the voting
 * power, capital or other securities of controlled or commonly controlled
 * entity.
 *
 * 1. Grant of Copyright License
 *
 * Subject to the terms and conditions of this License, each Contributor hereby
 * grants to you a perpetual, worldwide, royalty-free, non-exclusive,
 * irrevocable copyright license to reproduce, use, modify, or distribute its
 * Contribution, with modification or not.
 *
 * 2. Grant of Patent License
 *
 * Subject to the terms and conditions of this License, each Contributor hereby
 * grants to you a perpetual, worldwide, royalty-free, non-exclusive,
 * irrevocable (except for revocation under this Section) patent license to
 * make, have made, use, offer for sale, sell, import or otherwise transfer its
 * Contribution, where such patent license is only limited to the patent claims
 * owned or controlled by such Contributor now or in future which will be
 * necessarily infringed by its Contribution alone, or by combination of the
 * Contribution with the Software to which the Contribution was contributed.
 * The patent license shall not apply to any modification of the Contribution,
 * and any other combination which includes the Contribution. If you or your
 * Affiliates directly or indirectly institute patent litigation (including a
 * cross claim or counterclaim in a litigation) or other patent enforcement
 * activities against any individual or entity by alleging that the Software or
 * any Contribution in it infringes patents, then any patent license granted to
 * you under this License for the Software shall terminate as of the date such
 * litigation or activity is filed or taken.
 *
 * 3. No Trademark License
 *
 * No trademark license is granted to use the trade names, trademarks, service
 * marks, or product names of Contributor, except as required to fulfill notice
 * requirements in section 4.
 *
 * 4. Distribution Restriction
 *
 * You may distribute the Software in any medium with or without modification,
 * whether in source or executable forms, provided that you provide recipients
 * with a copy of this License and retain copyright, patent, trademark and
 * disclaimer statements in the Software.
 *
 * 5. Disclaimer of Warranty and Limitation of Liability
 *
 * THE SOFTWARE AND CONTRIBUTION IN IT ARE PROVIDED WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED. IN NO EVENT SHALL ANY CONTRIBUTOR OR
 * COPYRIGHT HOLDER BE LIABLE TO YOU FOR ANY DAMAGES, INCLUDING, BUT NOT
 * LIMITED TO ANY DIRECT, OR INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING
 * FROM YOUR USE OR INABILITY TO USE THE SOFTWARE OR THE CONTRIBUTION IN IT, NO
 * MATTER HOW IT’S CAUSED OR BASED ON WHICH LEGAL THEORY, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGES.
 *
 * 6. Language
 *
 * THIS LICENSE IS WRITTEN IN BOTH CHINESE AND ENGLISH, AND THE CHINESE VERSION
 * AND ENGLISH VERSION SHALL HAVE THE SAME LEGAL EFFECT. IN THE CASE OF
 * DIVERGENCE BETWEEN THE CHINESE AND ENGLISH VERSIONS, THE CHINESE VERSION
 * SHALL PREVAIL.
 *
 * END OF THE TERMS AND CONDITIONS
 *
 * How to Apply the Mulan Permissive Software License，Version 2
 * (Mulan PSL v2) to Your Software
 *
 * To apply the Mulan PSL v2 to your work, for easy identification by
 * recipients, you are suggested to complete following three steps:
 *
 * i. Fill in the blanks in following statement, including insert your software
 * name, the year of the first publication of your software, and your name
 * identified as the copyright owner;
 *
 * ii. Create a file named "LICENSE" which contains the whole context of this
 * License in the first directory of your software package;
 *
 * iii. Attach the statement to the appropriate annotated syntax at the
 * beginning of each source file.
 *
 * Copyright (c) [Year] [name of copyright holder]
 * [Software Name] is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.util.EmailUtils;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Random;

import static com.jiang.mall.domain.config.Email.*;
import static com.jiang.mall.domain.config.User.regex_email;
import static com.jiang.mall.util.EncryptAndDecryptUtils.isSha256Hash;

/**
 * 邮箱验证码控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月20日
 */
@RestController
@RequestMapping("/email")
public class EmailController {

    private IVerificationCodeService verificationCodeService;

    /**
     * 注入CodeService
     *
     * @param verificationCodeService 验证码服务实例
     */
    @Autowired
    public void setVerificationCodeService(IVerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    private IUserService userService;

    /**
     * 注入UserService
     *
     * @param userService UserService
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 处理注册请求
     *
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 注册结果
     */
    @PostMapping("/sendRegister")
    public ResponseResult sendRegister(@RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       @RequestParam("captcha") String captcha,
                                       HttpSession session) {
        // 检查用户是否已登录
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult("您已登录，请勿重复注册");
            }
        }
        // 检查是否允许发送注册邮件
        if (!AllowSendEmail){
            return ResponseResult.failResult("管理员不允许发送邮件");
        }
        // 验证输入参数是否完整
        if (username==null||email==null||password==null||confirmPassword==null||captcha==null){
            return ResponseResult.failResult("非法请求");
        }
        // 验证用户名非空
        if (!StringUtils.hasText(username)) {
            return ResponseResult.failResult("用户名不能为空");
        }
        // 验证密码非空
        if (!isSha256Hash(password)) {
            return ResponseResult.failResult("密码不能为空");
        }
        // 验证确认密码非空
        if (!isSha256Hash(confirmPassword)) {
            return ResponseResult.failResult("确认密码不能为空");
        }
        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }

        // 验证邮箱格式
        if (!StringUtils.hasText(email) || !email.matches(regex_email)){
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

        // 验证密码一致性
        if (!password.equals(confirmPassword)) {
            return ResponseResult.failResult("两次密码输入不一致");
        }
        // 检查是否在特定时间内请求过多验证码
        if (verificationCodeService.inspectByEmail(email)){
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
        // 生成验证码
        String code = generateRandomCode(8);
        // 构造邮件内容
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+username+"用户，您正在尝试使用"+EmailPurpose.REGISTER.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        // 发送邮件
        if (EmailUtils.sendEmail(email, "【"+SENDER_END+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(username,email, password, code, EmailPurpose.REGISTER, EmailStatus.SUCCESS);
            if (verificationCodeService.save(userVerificationCode)){
                return ResponseResult.okResult();
            }else {
                return ResponseResult.serverErrorResult("未知原因注册失败");
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(username,email,password, code, EmailPurpose.REGISTER, EmailStatus.FAILED);
            verificationCodeService.save(userVerificationCode);
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
        if (verificationCodeService.checkingByUserId((Long)result.getData())){
            return ResponseResult.okResult("在"+expiration_time+"分钟内已经验证通过");
        }
        return ResponseResult.failResult("验证码已过期，请重新获取");
    }

    /**
     * 发送重置密码验证码
     *
     * @param username 用户名或者邮箱
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 操作结果
     */
    @PostMapping("/sendResetPassword")
    public ResponseResult sendResetPassword(@RequestParam("username") String username,
                                            @RequestParam("captcha") String captcha,
                                            HttpSession session) {
        if (session.getAttribute("User")!=null){
            UserVo user = (UserVo) session.getAttribute("User");
            if (user.getId()!=null){
                return ResponseResult.failResult("您已登录，请退出");
            }
        }
        // 检查系统是否允许发送邮件
        if (!AllowSendEmail) {
            return ResponseResult.failResult("管理员不允许发送邮件");
        }
        if (username==null||captcha==null){
            return ResponseResult.failResult("非法请求");
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
        // 检查用户名是否为空
        if (!StringUtils.hasText(username)) {
            return ResponseResult.failResult("用户名(邮箱)不能为空");
        }

        // 获取用户信息
        User user=userService.getUserByUserNameOrEmail(username);

        // 再次验证用户是否存在
        if (user == null) {
            return ResponseResult.failResult("用户不存在");
        }
        // 检查邮箱是否请求过多验证码
        if (verificationCodeService.inspectByEmail(user.getEmail())) {
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 生成验证码和邮件内容
        String code = generateRandomCode(8);
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+user.getUsername()+"用户，您正在尝试使用"+EmailPurpose.RESET_PASSWORD.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        // 发送邮件并处理结果
        if (EmailUtils.sendEmail(user.getEmail(), "【"+SENDER_END+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(username,user.getEmail() , code, EmailPurpose.RESET_PASSWORD, EmailStatus.SUCCESS, user.getId());
            if (verificationCodeService.save(userVerificationCode)){
                return ResponseResult.okResult(user.getEmail(),"发送验证码成功！");
            }else {
                return ResponseResult.serverErrorResult("未知原因重置密码失败");
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(username,user.getEmail() , code, EmailPurpose.RESET_PASSWORD, EmailStatus.FAILED, user.getId());
            verificationCodeService.save(userVerificationCode);
            return ResponseResult.failResult("邮件发送失败，请重试");
        }
    }

    @PostMapping("/sendChangeEmail")
    public ResponseResult sendChangeEmail(@RequestParam("password") String password,
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
        Long userId = (Long) result.getData();

        if (password==null||captcha==null||email==null){
            return ResponseResult.failResult("非法请求");
        }
        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }

        if (!isSha256Hash(password)){
            return ResponseResult.failResult("密码不能为空");
        }

        // 验证邮箱格式
        if (!StringUtils.hasText(email) || !email.matches(regex_email)){
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

        User user = userService.getUserInfo(userId);
        if (!Objects.equals(user.getPassword(), password)) {
            return ResponseResult.failResult("密码错误");
        }
        if (!StringUtils.hasText(email)){
            return ResponseResult.failResult("邮箱不能为空");
        }

        if (Objects.equals(user.getEmail(), email)){
            return ResponseResult.failResult("新邮箱不能与旧邮箱相同");
        }

        if (verificationCodeService.inspectByEmail(email)){
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }
        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }

        String code = generateRandomCode(8);
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+user.getUsername()+"用户，您正在尝试使用"+EmailPurpose.CHANGE_EMAIL.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";
        if (EmailUtils.sendEmail(email, "【"+SENDER_END+"】验证码通知", htmlContent)){
            VerificationCode userVerificationCode = new VerificationCode(user.getUsername(),email, password, code, EmailPurpose.CHANGE_EMAIL, EmailStatus.SUCCESS);
            if (verificationCodeService.save(userVerificationCode)){
                return ResponseResult.okResult();
            }else {
                return ResponseResult.serverErrorResult("未知原因"+EmailPurpose.CHANGE_EMAIL.getName()+"失败");
            }
        }else {
            VerificationCode userVerificationCode = new VerificationCode(user.getUsername(),email, password, code, EmailPurpose.CHANGE_EMAIL, EmailStatus.FAILED);
            verificationCodeService.save(userVerificationCode);
            return ResponseResult.failResult("邮件发送失败，请重试");
        }
    }

    //TODO: 2024/10/17 放弃接口
    /**
     * 处理发送验证码的请求
     *
     * @param username 用户名
     * @param email 邮箱
     * @param captcha 验证码
     * @param session HTTP会话
     * @return 响应结果
     */
    @PostMapping("/sendChecking")
    public ResponseResult sendChecking(@RequestParam("username") String username,
                                       @RequestParam("email") String email,
                                       @RequestParam("captcha") String captcha,
                                       HttpSession session) {
        // 检查管理员是否允许发送邮件
        if (!AllowSendEmail){
            return ResponseResult.failResult("管理员不允许发送邮件");
        }

        // 检查用户是否已登录
        ResponseResult result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }

        // 验证请求参数是否为空
        if (username==null||captcha==null||email==null){
            return ResponseResult.failResult("非法请求");
        }

        // 检查验证码是否为空
        if (!StringUtils.hasText(captcha)) {
            return ResponseResult.failResult("验证码不能为空");
        }

        // 检查用户名是否为空
        if (!StringUtils.hasText(username)){
            return ResponseResult.failResult("用户名不能为空");
        }

        // 验证邮箱格式
        if (!StringUtils.hasText(email) || !email.matches(regex_email)){
            return ResponseResult.failResult("邮箱格式不正确");
        }

        // 验证会话中的验证码
        Object captchaObj = session.getAttribute("captcha");
        if (captchaObj == null) {
            return ResponseResult.failResult("会话中的验证码已过期，请重新获取");
        }
        String captchaCode = captchaObj.toString();
        if (!captchaCode.toLowerCase().equals(captcha)) {
            return ResponseResult.failResult("验证码错误");
        }

        // 检查是否在特定时间内请求过多验证码
        if (verificationCodeService.inspectByEmail(email)){
            return ResponseResult.failResult("该邮箱在特定时间内请求过多验证码");
        }

        // 检查邮箱是否已注册
        if (userService.queryByEmail(email)) {
            return ResponseResult.failResult("邮箱已存在");
        }

        // 生成随机验证码
        String code = generateRandomCode(8);

        // 构造验证码邮件内容
        String htmlContent = "<html><body>" +
                "<h1>【"+SENDER_END+"】验证码通知</h1>" +
                "<p>尊敬的"+username+"用户，您正在尝试使用"+EmailPurpose.REGISTER.getName()+"功能。</p>" +
                "<div style='font-size: 24px; color: #007bff; font-weight: bold; text-align: center;'>" +
                "您的验证码是：<span style='font-size: 36px;'>"+code+"</span></div>" +
                "<p>请在接下来的 "+expiration_time+" 分钟内使用此验证码完成操作。为保证账户安全，请勿向任何人透露此验证码。</p>" +
                "<p>如果您没有发起此操作，请忽略此邮件。</p>" +
                "<div style='text-align: center; color: #999999; font-size: 12px;'>本邮件由系统自动发送，请勿回复。</div>" +
                "</body></html>";

        // 发送验证码邮件
        if (EmailUtils.sendEmail(email, "【"+SENDER_END+"】验证码通知", htmlContent)){
            // 邮件发送成功，保存验证码信息
            VerificationCode userVerificationCode = new VerificationCode(username,email, code, EmailPurpose.RESET_PASSWORD, EmailStatus.SUCCESS, (Long)result.getData());
            if (verificationCodeService.save(userVerificationCode)){
                return ResponseResult.okResult();
            }else {
                return ResponseResult.serverErrorResult("未知原因注册失败");
            }
        }else {
            // 邮件发送失败，记录失败信息
            VerificationCode userVerificationCode = new VerificationCode(username,email , code, EmailPurpose.RESET_PASSWORD, EmailStatus.FAILED, (Long)result.getData());
            verificationCodeService.save(userVerificationCode);
            return ResponseResult.failResult("邮件发送失败，请重试");
        }
    }

    public static @NotNull String generateRandomCode(int length) {
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