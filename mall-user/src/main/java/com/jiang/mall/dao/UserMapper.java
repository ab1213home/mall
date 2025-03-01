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

package com.jiang.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiang.mall.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * User的映射接口，继承自BaseMapper<User>
 * 本接口用于定义与数据库中tb_users表进行交互的方法，专门用于处理User实体的CRUD操作
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote User的映射接口
 * @version 1.0
 * @since 2024年9月8日
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM tb_users WHERE id = #{userId} AND is_active = true LIMIT 1")
    User selectUserByIdAndActive(Long userId);

    @Select("SELECT * FROM tb_users WHERE username = #{username} AND is_active = true LIMIT 1")
    User selectByUsernameAndIsActive(String username);

    @Select("SELECT * FROM tb_users WHERE email = #{email} AND is_active = true LIMIT 1")
    User selectByEmailAndIsActive(String email);

    @Select("SELECT * FROM tb_users WHERE username = #{username} LIMIT 1")
    User selectByUsername(String username);

    @Select("SELECT * FROM tb_users WHERE email = #{email} LIMIT 1")
    User selectByEmail(String email);

    @Select("SELECT COUNT(*) FROM tb_users WHERE email = #{email}")
	int selectCountByEmail(String email);

    @Select("SELECT COUNT(*) FROM tb_users WHERE username = #{username}")
    int selectCountByUsername(String username);

    @Update("UPDATE tb_users SET password = #{password} , updater = #{id} , updated_at = NOW() WHERE id = #{id}")
    int modifyPasswordById(Long id, String password);

    @Update("UPDATE tb_users SET is_active = false , updater = #{updater} , updated_at = NOW() WHERE id = #{id}")
    int lockById(Long id,Long updater);

    @Update("UPDATE tb_users SET is_active = true , updater = #{updater} , updated_at = NOW() WHERE id = #{id}")
    int unlockById(Long id, Long updater);

    @Select("SELECT COUNT(*) FROM tb_users WHERE id = #{id} AND password = #{password} AND is_active = true")
    int validatePassword(Long id, String password);

    @Update("UPDATE tb_users SET email = #{email} , updater = #{id} , updated_at = NOW() WHERE id = #{id} AND is_active = true")
    int updateEmail(Long id, String email);

    @Update("UPDATE tb_users SET default_address_id = #{addressId} , updater = #{id} , updated_at = NOW() WHERE id = #{id} AND is_active = true")
    void updateDefaultAddressId(Long id, Long addressId);

}
