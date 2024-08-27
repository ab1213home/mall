package com.jiang.mall.util;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    public static <V> V copyBean(Object source,Class<V> clazz) {
        //创建目标对象
        V target = null;
        try {
            target = clazz.newInstance();
            //实现属性copy
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return target;
    }

    public static <O,V> List<V> copyBeanList(List<O> list, Class<V> clazz){
        return list.stream()
                .map(o -> copyBean(o, clazz))
                .collect(Collectors.toList());
    }
}
