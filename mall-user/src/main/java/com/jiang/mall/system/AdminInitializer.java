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

package com.jiang.mall.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.dao.UserGroupRelationMapper;
import com.jiang.mall.dao.UserMapper;
import com.jiang.mall.domain.entity.User;
import com.jiang.mall.domain.entity.UserGroupRelation;
import com.jiang.mall.util.RandomUtil;
import com.jiang.mall.util.SecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private UserMapper userMapper;

	@Autowired
	public void setUserMapper(UserMapper userMapper) {
		this.userMapper = userMapper;
	}

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private UserGroupRelationMapper userGroupRelationMapper;

	@Autowired
	public void setUserGroupRelationMapper(UserGroupRelationMapper userGroupRelationMapper) {
		this.userGroupRelationMapper = userGroupRelationMapper;
	}

    @Override
    public void run(String... args) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", "admin");
        Long adminCount = userMapper.selectCount(queryWrapper);

        if (adminCount == 0) {
            // 生成随机密码（生产环境建议随机生成）
            String rawPassword = RandomUtil.generatePassword(12);
			String hashedPassword = SecureUtil.sha256Hex(rawPassword, generalConfig.getAesSalt());

            // 创建管理员用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(hashedPassword);
			admin.setEmail("admin@example.com");
			admin.setActive(true);
			admin.setTotpEnabled(false);
            userMapper.insert(admin);

			UserGroupRelation userGroupRelation = new UserGroupRelation();
			userGroupRelation.setUserId(admin.getId());
			userGroupRelation.setGroupId(4L);
			userGroupRelationMapper.insert(userGroupRelation);

            // 日志记录
			logger.info("已生成初始管理员帐户。用户名: admin, 密码: {}", rawPassword);
        }
    }
}