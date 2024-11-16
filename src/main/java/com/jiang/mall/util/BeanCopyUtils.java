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

package com.jiang.mall.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private static final ModelMapper modelMapper = new ModelMapper();

    private static final Logger logger = LoggerFactory.getLogger(BeanCopyUtils.class);

    private BeanCopyUtils() {
    }

    /**
     * 将对象转换为指定类型的实例
     * 该方法使用ModelMapper进行属性复制，实现对象间属性值的拷贝
     *
     * @param source 要转换的对象
     * @param clazz 目标类型，必须是Object的子类
     * @param <V> 目标类型的泛型表示
     * @return 转换后的对象，如果转换失败则返回null
     */
     public static <V> @Nullable V copyBean(Object source, Class<V> clazz) {
         if (source == null) {
             // source为null
             logger.warn("源对象不能为null");
             return null;
         }
         try {
             // 使用ModelMapper将source对象的属性值复制到目标对象
             return modelMapper.map(source, clazz);
         } catch (Exception e) {
             // 异常处理：打印堆栈跟踪，可进行定制化异常处理逻辑
             logger.error("在Bean复制期间发生错误：", e);
             throw new RuntimeException(e);
         }
     }

     /**
      * 将源列表中的每个对象转换为目标类类型的列表
      *
      * @param <O> 源对象列表的类型
      * @param <V> 目标对象列表的类型
      * @param list 源对象列表
      * @param clazz 目标对象的类类型
      * @return 转换后的目标对象列表
      */
     public static <O, V> List<V> copyBeanList(@NotNull List<O> list, Class<V> clazz) {
         // 使用流式操作将每个源对象转换为目标对象，并收集到新的列表中
         return list.stream()
                 .map(o -> copyBean(o, clazz))
                 .collect(Collectors.toList());
     }
}