package com.jiang.mall.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入数据填充方法
     * 该方法在插入数据前自动填充创建者、创建时间和更新时间的字段
     * 主要用于确保数据库中这些字段的一致性和可追踪性
     *
     * @param metaObject 元数据对象，代表了要插入的数据实体
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取当前的HTTP请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 从会话中获取当前用户的ID
        Object userId = request.getSession().getAttribute("UserId");
        if (userId != null) {
            // 如果用户ID不为空，则设置创建者字段为当前用户ID
            this.setFieldValByName("creator", userId.toString(), metaObject);
        } else {
            // 如果用户ID为空，则默认设置创建者字段为"0"
            this.setFieldValByName("creator", "0", metaObject);
        }
        // 设置创建时间为当前时间
        this.setFieldValByName("createdAt", LocalDateTime.now(), metaObject);
        // 设置更新时间为当前时间
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
    }

    /**
     * 更新填充字段
     *
     * @param metaObject 元对象，用于操作对象的元数据
     * 方法体内部通过调用setFieldValByName方法，更新字段"updatedAt"的值为当前的LocalDateTime时间
     * 该方法在更新数据时自动填充更新时间字段，确保数据库中这些字段的一致性和可追踪性
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updatedAt", LocalDateTime.now(), metaObject);
    }

}