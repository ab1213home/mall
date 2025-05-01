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
            "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&state=%s",
            "https://github.com/login/oauth/access_token",
            "https://api.github.com/user",
			"/images/github.png"
			),
    GITEE(  1,
            "gitee",
            "https://gitee.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=user_info&state=%s",
            "https://gitee.com/oauth/token",
            "https://gitee.com/api/v5/user?access_token=%s",
		    "/images/gitee.png"
		    ),
	WECHAT( 2,
            "wechat",
            "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s",
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
            "https://api.weixin.qq.com/sns/userinfo",
			"/images/wechat.png"
	);

    private final Integer key;
    private final String name;
    private final String auth;
    private final String token;
    private final String user;
    private final String login;
	private final String bind;
	private final String unbind;
	private final String ico;
	private final String callback;

    OAuthProvider(Integer key, String name, String auth, String token, String user, String ico) {
        this.key = key;
        this.name = name;
        this.auth = auth;
        this.token = token;
        this.user = user;
		this.login = "/oauth/login/"+name;
		this.bind = "/oauth/bind/"+name;
		this.unbind = "/oauth/unbind/"+name;
		this.ico = ico;
		this.callback = "/oauth/callback/"+name;
    }

    public static OAuthProvider fromKey(Integer key) {
        return Arrays.stream(values())
                .filter(p -> Objects.equals(p.key, key))
                .findFirst()
                .orElse(null);
    }

	public static OAuthProvider fromName(String name) {
        return Arrays.stream(values())
                .filter(p -> Objects.equals(p.name, name))
                .findFirst()
                .orElse(null);
    }
}
