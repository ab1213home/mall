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

package com.jiang.mall.controller.File;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IFileService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jiang.mall.domain.config.File.*;

/**
 * 公共控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Controller
@RequestMapping("/common")
public class FileUploadController {

    private IUserService userService;

    /**
     * 注入用户服务对象
     *
     * @param userService 用户服务对象
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private IFileService fileService;

    @Autowired
    public void setFileService(IFileService fileService) {
        this.fileService = fileService;
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
    public ResponseResult<Object> upLoadFile(@RequestParam("file")MultipartFile file, HttpSession session) throws IOException {
        if (!AllowUploadFile){
            return ResponseResult.failResult("上传文件被禁止");
        }
        ResponseResult<Object> result = userService.checkAdminUser(session);

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
        // 生成文件名，防止重名文件被覆盖
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String newName = sdf.format(new Date()) + file.getOriginalFilename();
        Boolean res = fileService.writeFile(file,FILE_UPLOAD_PATH,newName);
        if (!res){
            return ResponseResult.failResult("非法的文件类型");
        }else{
            return ResponseResult.okResult("/upload/" + newName,"上传成功");
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
    public ResponseResult<Object> upLoadFaces(@RequestParam("file")MultipartFile file, HttpSession session) throws IOException {
        // 检查是否允许上传文件
        if (!AllowUploadFile){
            return ResponseResult.failResult("上传文件被禁止");
        }

        // 检查用户登录状态
        ResponseResult<Object> result = userService.checkUserLogin(session);
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

        // 生成文件名，防止重名文件被覆盖
        String extension = index > 0 ? fileName.substring(index) : "";
        UserVo user = (UserVo) session.getAttribute("User");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss-" + userId+"-"+user.getUsername());
        String newName = sdf.format(new Date()) + extension;
        Boolean res = fileService.writeFile(file,FACE_UPLOAD_PATH,newName);
        if (!res){
            return ResponseResult.failResult("非法的文件类型");
        }else{
            return ResponseResult.okResult("/faces/" + newName,"上传成功");
        }
    }

}