package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.service.IUserService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.jiang.mall.domain.entity.Config.*;


/**
 * 公共控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Controller
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private IUserService userService;

    /**
     * 生成验证码并作为响应返回
     * 该方法通过HttpServletRequest和HttpServletResponse对象进行操作，生成并返回一个验证码图像
     * 验证码文本被存储在用户会话中，以便后续验证使用
     *
     * @param request 用于获取请求信息，以便设置session属性
     * @param response 用于设置响应头，内容类型，并输出验证码图像
     * @throws IOException 如果在写入响应体过程中发生I/O错误
     */
    @RequestMapping("/captcha")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
     * @param session 当前用户的会话对象
     * @return 返回文件上传结果，包含上传后的文件访问路径
     * @throws IOException 如果文件读写过程中发生错误
     */
    @RequestMapping("/uploadFile")
    @ResponseBody
    public ResponseResult upLoadFile(@RequestParam("file")MultipartFile file, HttpSession session) throws IOException {
        if (!AllowUploadFile){
            return ResponseResult.failResult("上传文件被禁止");
        }
        ResponseResult result = userService.checkAdminUser(session);
		if (!result.isSuccess()) {
			// 如果未登录，则直接返回
		    return result;
		}
        // 检查文件是否为空
        if (file.isEmpty()){
            return ResponseResult.failResult("文件不能为空");
        }
        // 文件的原始名称
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseResult.failResult("文件名称不能为空");
        }

        // 解析出文件后缀
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return ResponseResult.failResult("文件后缀不能为空");
        }

        String suffix = fileName.substring(index + 1);

        if (!imageSuffix.contains(suffix.trim().toLowerCase())) {
            return ResponseResult.failResult("非法的文件类型");
        }

        // 获取系统中的临时目录
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // 临时文件使用 UUID 随机命名
        Path tempFile = tempDir.resolve(Paths.get(UUID.randomUUID().toString()));

        // copy 到临时文件
        file.transferTo(tempFile);

        try {
            // 使用 ImageIO 读取文件
            if (ImageIO.read(tempFile.toFile()) == null) {
                return ResponseResult.failResult("非法的文件类型");
            }
            // 至此，这的确是一个图片资源文件

            // 检查并创建上传文件的目录
            File dir = new File(FILE_UPLOAD_PATH);
            if (!dir.exists() && !dir.isDirectory()){
                dir.mkdir();
            }

            // 生成文件名，防止重名文件被覆盖
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String newName = sdf.format(new Date()) + fileName;
            file.transferTo(new File(FILE_UPLOAD_PATH + newName));

            // 返回相对访问路径
            result = ResponseResult.okResult();
            result.setData("/upload/" + newName);
            return result;

        } finally {
            // 始终删除临时文件
            Files.delete(tempFile);
        }
    }

    /**
     * 处理用户头像上传请求
     *
     * @param file 用户上传的文件
     * @param session 用户会话
     * @return 包含上传结果和文件访问路径的响应对象
     * @throws IOException 文件处理或I/O过程中可能出现的异常
     */
    @RequestMapping("/uploadFaces")
    @ResponseBody
    public ResponseResult upLoadFaces(@RequestParam("file")MultipartFile file, HttpSession session) throws IOException {
        // 检查是否允许上传文件
        if (!AllowUploadFile){
            return ResponseResult.failResult("上传文件被禁止");
        }

        // 检查用户登录状态
        ResponseResult result = userService.checkUserLogin(session);
        if (!result.isSuccess()) {
            // 如果未登录，则直接返回
            return result;
        }
        Integer userId = (Integer) result.getData();

        // 检查上传文件是否为空
        if (file.isEmpty()){
            return ResponseResult.failResult("文件不能为空");
        }

        // 设置用户头像上传路径
        String FACE_UPLOAD_PATH = FILE_UPLOAD_PATH + "faces/";

        // 文件的原始名称
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return ResponseResult.failResult("文件名称不能为空");
        }

        // 解析出文件后缀
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return ResponseResult.failResult("文件后缀不能为空");
        }

        String suffix = fileName.substring(index + 1);

        if (!imageSuffix.contains(suffix.trim().toLowerCase())) {
            return ResponseResult.failResult("非法的文件类型");
        }

        // 获取系统中的临时目录
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // 临时文件使用 UUID 随机命名
        Path tempFile = tempDir.resolve(Paths.get(UUID.randomUUID().toString()));

        // copy 到临时文件
        file.transferTo(tempFile);

        try {
            // 使用 ImageIO 读取文件
            if (ImageIO.read(tempFile.toFile()) == null) {
                return ResponseResult.failResult("非法的文件类型");
            }
            // 至此，这的确是一个图片资源文件

            // 检查并创建上传文件的目录
            File dir = new File(FILE_UPLOAD_PATH);
            if (!dir.exists() && !dir.isDirectory()){
                dir.mkdir();
            }

            // 生成唯一的文件名，避免文件重名覆盖
            String filename = file.getOriginalFilename();
            int dotIndex = filename != null ? filename.lastIndexOf('.') : 0;
            String extension = dotIndex > 0 ? filename.substring(dotIndex) : "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss-" + userId);
            String newName = sdf.format(new Date()) + extension;
            file.transferTo(new File(FACE_UPLOAD_PATH + newName));

            // 返回相对访问路径
            result = ResponseResult.okResult();
            result.setData("/faces/" + newName);
            return result;

        } finally {
            // 始终删除临时文件
            Files.delete(tempFile);
        }
    }



}