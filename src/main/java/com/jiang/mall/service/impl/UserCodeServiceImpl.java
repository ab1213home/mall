package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.UserCodeMapper;
import com.jiang.mall.domain.entity.UserCode;
import com.jiang.mall.service.IUserCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.jiang.mall.domain.entity.Config.*;

@Service
public class UserCodeServiceImpl extends ServiceImpl<UserCodeMapper, UserCode> implements IUserCodeService {

	@Autowired
	private UserCodeMapper userCodeMapper;

	@Override
	public boolean inspectByEmail(String email) {
		// 当前时间
        Date now = new Date();
        // 一天前的时间
        Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

        // 构建查询条件
        QueryWrapper<UserCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.ge("create_at", yesterday);
        queryWrapper.le("create_at", now);

		int count = 0;
		int success = 0;
		int fail = 0;
        // 查询并返回结果数量
        List<UserCode> list = userCodeMapper.selectList(queryWrapper);
		for (UserCode userCode : list) {
			count++;
			if (userCode.isUsed()) {
				success++;
			} else {
				fail++;
			}
		}
		if (count>max_request_num){
			return true;
		}
		return fail / (double) count > max_fail_rate;
	}

	/**
	 * 根据邮箱查询验证码是否存在且未过期
	 * <p>
	 * 该方法主要用于验证一个邮箱地址是否有对应的未过期的验证码它首先计算出
	 * 从当前时间向前推expiration_time分钟的时间点，然后查询这个时间点之后，
	 * 当前时间之前，且邮箱地址匹配的验证码记录如果存在这样的记录，则返回true，
	 * 表示验证码存在且在有效期内；否则返回false
	 *
	 * @param email 邮箱地址，用于查询验证码记录
	 * @return 如果存在未过期的验证码则返回true，否则返回false
	 */
	@Override
	public boolean queryByEmail(String email) {
	    // 当前时间
	    Date now = new Date();
	    // expiration_time前的时间
	    Date yesterday = new Date(now.getTime() - (long) expiration_time * 60 * 1000);

	    // 构建查询条件
	    QueryWrapper<UserCode> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("email", email);
	    queryWrapper.ge("create_at", yesterday);
	    queryWrapper.le("create_at", now);
	    queryWrapper.eq("is_used", true);

	    // 查询并返回结果数量
	    List<UserCode> list = userCodeMapper.selectList(queryWrapper);
	    return !list.isEmpty();
	}
}
