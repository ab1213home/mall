package com.jiang.mall.util;

import java.io.File;
import java.util.Objects;

public class FileUtils {

    /**
     * 计算文件夹的大小
     * 此方法通过递归遍历文件夹中的所有文件和子文件夹来计算总大小
     *
     * @param folder 要计算大小的文件夹
     * @return 文件夹的大小，以字节为单位
     */
    public static long getFolderSize(File folder) {
        // 初始化文件夹大小为0
        long size = 0;
        // 遍历文件夹中的所有文件和子文件夹
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                // 如果是文件，则累加文件的大小到总大小中
                size += file.length();
            } else if (file.isDirectory()) {
                // 如果是子文件夹，则递归调用getFolderSize方法，累加子文件夹的大小到总大小中
                size += getFolderSize(file);
            }
        }
        // 返回文件夹的总大小
        return size;
    }

    /**
     * 计算给定文件夹中的文件数量
     * 此方法通过递归遍历文件夹中的所有文件和子文件夹来计算总数
     *
     * @param folder 要计算文件数量的文件夹，不能为null
     * @return 文件夹中的文件数量
     */
    public static int getFileCount(File folder) {
        // 初始化文件计数器
        int count = 0;

        // 遍历文件夹中的所有文件和子文件夹
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            // 如果是文件，则计数器加一
            if (file.isFile()) {
                count++;
            } else if (file.isDirectory()) {
                // 如果是文件夹，则递归调用getFileCount方法，将子文件夹的文件数加到计数器中
                count += getFileCount(file);
            }
        }

        // 返回文件夹中的文件数量
        return count;
    }
}
