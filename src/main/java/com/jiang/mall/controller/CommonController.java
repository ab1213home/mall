package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.wf.captcha.SpecCaptcha;
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


/**
 * 公共控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Controller
@RequestMapping("/common")
public class CommonController {

    // 文件上传的默认路径
    public static String FILE_UPLOAD_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\upload\\";

    /**
     * 生成验证码并作为响应返回
     * 该方法通过HttpServletRequest和HttpServletResponse对象进行操作，生成并返回一个验证码图像
     * 验证码文本被存储在用户会话中，以便后续验证使用
     *
     * @param request 用于获取请求信息，以便设置session属性
     * @param response 用于设置响应头，内容类型，并输出验证码图像
     * @throws IOException 如果在写入响应体过程中发生I/O错误
     * @throws FontFormatException 如果加载自定义字体时发生格式错误
     */
    @RequestMapping("/captcha")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException, FontFormatException {
        // 禁止缓存响应数据，确保不同浏览器或缓存服务器下验证码图像不会被缓存
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        // 设置响应内容类型为PNG图像，告知浏览器将接收的数据显示为图像
        response.setContentType("image/png");
        // 创建一个自定义的验证码对象，参数分别为宽度、高度和字符数
        SpecCaptcha captcha = new SpecCaptcha(160, 40, 4);
        // 设置验证码字符类型为纯数字，增加用户辨识的易用性
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        // 以下代码行被注释掉，因此没有设置自定义字体
        // captcha.setFont(Captcha.FONT_8, 40);
        // 将生成的验证码文本存储在session中，以便后续表单提交时验证
        request.getSession().setAttribute("captcha", captcha.text().toLowerCase());
        // 将验证码图像输出到HTTP响应中，实现浏览器展示验证码图像
        captcha.out(response.getOutputStream());
    }

    /**
     * 文件上传处理方法
     * 该方法用于处理文件上传请求，接收上传的文件并将其保存到指定路径
     * @param file 用户上传的文件，类型为MultipartFile
     * @return 返回文件上传结果，包含上传后的文件访问路径
     * @throws IOException 如果文件读写过程中发生错误
     */
    @RequestMapping("/uploadfile")
    @ResponseBody
    public ResponseResult uploadfile(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()){
            return ResponseResult.failResult("文件不能为空");
        }

        // 检查并创建上传文件的目录
        File dir = new File(FILE_UPLOAD_PATH);
        if (!dir.exists() && !dir.isDirectory()){
            dir.mkdir();
            System.out.println("创建文件夹成功");
        }

        // 生成文件名，防止重名文件被覆盖
        String filename = file.getOriginalFilename();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String newName = sdf.format(new Date()) + filename;
        file.transferTo(new File(FILE_UPLOAD_PATH + newName));

        // 构建成功响应结果，包含文件访问路径
        ResponseResult result = ResponseResult.okResult();
        result.setData("http://localhost:8080/upload/" + newName);
        return  result;
    }
}
