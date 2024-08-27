package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/common")
public class CommonController {

    public static String FILE_UPLOAD_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\upload\\";


//    函数的功能是生成验证码并将其作为PNG图像响应返回给客户端
    @RequestMapping("/captcha")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, FontFormatException {
        // 3个设置都是为了不缓存响应数据，同时设置可以兼容不同的浏览器或缓存服务器
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        // 响应内容是一个PNG格式的图像
        response.setContentType("image/png");

        // 生成不同类型验证码，三个参数分别为宽、高、位数
        // png类型
//        SpecCaptcha captcha = new SpecCaptcha(180, 40, 4);
        // gif类型
//        GifCaptcha captcha = new GifCaptcha(180, 40, 4);
        // 中文类型
//        ChineseCaptcha captcha = new ChineseCaptcha(180, 40, 4);
        // 中文gif类型
//        ChineseGifCaptcha captcha = new ChineseGifCaptcha(180, 40, 4);
        // 算术类型
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(180, 40, 4);

        // 设置字符类型
        captcha.setCharType(Captcha.TYPE_DEFAULT);

        // 设置字体
//        captcha.setFont(Captcha.FONT_8, 40);

        // 验证码存入 session
        request.getSession().setAttribute("verifyCode", captcha.text().toLowerCase());

        // 输出图片流
        captcha.out(response.getOutputStream());
    }



    @RequestMapping("/uploadfile")
    @ResponseBody
    public ResponseResult uploadfile(MultipartFile file) throws IOException {
//        若上传的文件为空，则返回失败信息“文件不能为空”。
        if (file.isEmpty()){
            return ResponseResult.failResult("文件不能为空");
        }

//      创建或确认上传路径FILE_UPLOAD_PATH存在，若不存在则新建文件夹，并输出“创建文件夹成功”至控制台。
        File dir = new File(FILE_UPLOAD_PATH);
        if (!dir.exists() && !dir.isDirectory()){
            dir.mkdir();
            System.out.println("创建文件夹成功");
        }

//        获取上传文件原名，并为文件重命名：当前日期时间格式化字符串加上原文件名作为新文件名。
        String filename = file.getOriginalFilename();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String newName = sdf.format(new Date()) + filename;
        file.transferTo(new File(FILE_UPLOAD_PATH + newName));

//        构建返回结果，包含文件访问URL（基于新文件名），并返回成功结果。
        ResponseResult result = ResponseResult.okResult();
        result.setData("http://localhost:8080/upload/" + newName);
        return  result;
    }
}
