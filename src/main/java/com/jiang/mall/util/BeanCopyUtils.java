package com.jiang.mall.util;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    /**
     * 将对象转换为指定类型的实例
     * 该方法使用Spring的BeanUtils进行属性复制，实现对象间属性值的拷贝
     *
     * @param source 要转换的对象
     * @param clazz 目标类型，必须是Object的子类
     * @param <V> 目标类型的泛型表示
     * @return 转换后的对象，如果转换失败则返回null
     */
    public static <V> V copyBean(Object source, Class<V> clazz) {
        V target = null;
        try {
            // 创建目标对象的实例
            target = clazz.newInstance();
            // 使用BeanUtils.copyProperties方法将source对象的属性值复制到target对象
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            // 异常处理：打印堆栈跟踪，可进行定制化异常处理逻辑
            throw new RuntimeException(e);
        }
        // 返回转换后的对象
        return target;
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
    public static <O,V> List<V> copyBeanList(List<O> list, Class<V> clazz){
        // 使用流式操作将每个源对象转换为目标对象，并收集到新的列表中
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }
}
