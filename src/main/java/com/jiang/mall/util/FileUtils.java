package com.jiang.mall.util;

import java.io.File;
import java.util.Objects;

public class FileUtils {
	public static long getFolderSize(File folder) {
        long size = 0;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                size += file.length();
            } else if (file.isDirectory()) {
                size += getFolderSize(file);
            }
        }
        return size;
    }

    public static int getFileCount(File folder) {
        int count = 0;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                count++;
            } else if (file.isDirectory()) {
                count += getFileCount(file);
            }
        }
        return count;
    }
}
