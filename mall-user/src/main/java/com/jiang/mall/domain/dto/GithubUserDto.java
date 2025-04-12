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
public class GithubUserDto {
	private String login;
	private Long id;
	private String node_id;
	private String avatar_url;
	private String gravatar_id;
	private String url;
	private String html_url;
	private String followers_url;
	private String following_url;
	private String gists_url;
	private String starred_url;
	private String subscriptions_url;
	private String organizations_url;
	private String repos_url;
	private String events_url;
	private String received_events_url;
	private String type;
	private boolean site_admin;
	private String name;
	private String company;
	private String blog;
	private String location;
	private String email;
	private String hireable;
	private String bio;
	private String twitter_username;
	private long public_repos;
	private long public_gists;
	private long followers;
	private long following;
	private String created_at;
	private String updated_at;
	private long private_gists;
	private long total_private_repos;
	private long owned_private_repos;
	private long disk_usage;
	private long collaborators;
	private boolean two_factor_authentication;
}
