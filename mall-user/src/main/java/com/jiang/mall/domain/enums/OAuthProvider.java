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
import java.util.Objects;

@Getter
public enum OAuthProvider {
	GITHUB( 0,
            "gitHub",
            "https://github.com/login/oauth/authorize",
            "https://github.com/login/oauth/access_token",
            "https://api.github.com/user"),
    GITEE(  1,
            "gitee",
            "https://gitee.com/oauth/authorize",
            "https://gitee.com/oauth/token",
            "https://gitee.com/api/v5/user");

    private final Integer key;
    private final String name;
    private final String authUrl;
    private final String tokenPath;
    private final String userInfoUri;

    OAuthProvider(Integer key, String name, String authUrl, String tokenPath, String userInfoUri) {
        this.key = key;
        this.name = name;
        this.authUrl = authUrl;
        this.tokenPath = tokenPath;
        this.userInfoUri = userInfoUri;
    }

    public static OAuthProvider fromKey(Integer key) {
        return Arrays.stream(values())
                .filter(p -> Objects.equals(p.key, key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("无效的第三方服务商"));
    }
}
