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

package com.jiang.mall.domain.dto;

import lombok.Data;

@Data
public class GiteeUserDto {
	private Long id;
    private String login;
    private String name;
    private String avatar_url;
	private String email;
	private String blog;
	private String bio;
	private String remark;
	private String type;
	private String html_url;
	private String created_at;
	private String updated_at;
	private String followers_url;
	private String following_url;
	private String gists_url;
	private String starred_url;
	private String subscriptions_url;
	private String organizations_url;
	private String repos_url;
	private String events_url;
	private String received_events_url;
	private Integer followers;
	private Integer following;
	private Integer public_repos;
	private Integer public_gists;
	private Integer stared;
	private Integer watched;
	private String member_role;
	private String weibo;
	private String company;
	private String location;
	private String twitter_username;
	private String hireable;

}
