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

package com.jiang.mall.domain.enums;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public enum FileType {

	jpg("jpg","图片"),
	jpeg("jpeg","图片"),
	png("png","图片"),
	gif("gif","图片"),
	bmp("bmp","图片"),
	mp3("mp3","音频"),
	wav("wav","音频"),
	aac("aac","音频"),
	flac("flac","音频"),
	pdf("pdf","文档"),
	doc("doc","文档"),
	docx("docx","文档"),
	txt("txt","文档"),
	xls("xls","文档"),
	xlsx("xlsx","文档");

	private final String value;
	private final String name;

	FileType(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(String value, String defaultValue) {
        for (FileType fileType : FileType.values()) {
            if (Objects.equals(fileType.getValue(), value)) {
                return fileType.getName();
            }
        }
        return defaultValue;
    }

	public static @NotNull Map<String,String> toMap() {
		Map<String,String> map = new HashMap<>();
		for (FileType fileType : FileType.values()) {
			map.put(fileType.getValue(),fileType.getName());
		}
		return map;
	}
}
