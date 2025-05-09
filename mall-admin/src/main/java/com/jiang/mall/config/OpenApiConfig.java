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

package com.jiang.mall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
		Contact contact = new Contact()
				.name("Jiang Rongjun")
				.email("jiangrongjun2004@163.com")
				.url("https://github.com/ab1213home")
				.extensions(new HashMap<String, Object>());;
		License license = new License()
				.name("Mulan PSL v2")
				.url("https://license.coscl.org.cn/MulanPSL2")
				.extensions(new HashMap<String, Object>());;
		Info info = new Info()
				.title("Jiang Mall")
				.version("2.0.1")
				.description("Jiang Mall's Description of JSON.")
				.termsOfService("https://github.com/ab1213home/mall.git")
				.contact(contact)
				.license(license);
        return new OpenAPI()
		        .openapi("3.0.1")
		        .info(info);
    }
}