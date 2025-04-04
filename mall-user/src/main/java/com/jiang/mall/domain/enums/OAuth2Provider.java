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

import java.util.Arrays;

@Getter
public enum OAuth2Provider {
	GITHUB("github", "GitHub"),
    GITEE("Gitee", "Gitee"),
    WECHAT("wechat", "微信");

    private final String code;
    private final String name;

    OAuth2Provider(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // 根据code获取枚举
    public static OAuth2Provider fromCode(String code) {
        return Arrays.stream(values())
                .filter(p -> p.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的第三方服务商"));
    }
}
