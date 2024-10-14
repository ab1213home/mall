package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CodeMapper;
import com.jiang.mall.domain.entity.Code;
import com.jiang.mall.service.ICodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.jiang.mall.domain.entity.Config.*;

@Service
public class CodeServiceImpl extends ServiceImpl<CodeMapper, Code> implements ICodeService {

	private CodeMapper codeMapper;

	@Autowired
	public void setCodeMapper(CodeMapper codeMapper) {
		this.codeMapper = codeMapper;
	}

	/**
	 * 根据邮箱检查发送状态
	 * 本方法主要用于检查在过去24小时内，给定邮箱接收到的验证码的发送状态
	 * 它通过统计成功、失败和其他状态的验证码数量，结合总的请求数量，
	 * 来判断是否达到了最大失败率阈值或超过了最大请求数量
	 *
	 * @param email 需要检查的邮箱地址
	 * @return 如果失败率超过最大失败率阈值或请求数量超过最大请求数量，返回true；否则返回false
	 */
	@Override
	public boolean inspectByEmail(String email) {
	    // 当前时间
	    Date now = new Date();
	    // 一天前的时间
	    Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

	    // 构建查询条件
	    QueryWrapper<Code> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("email", email);
	    queryWrapper.ge("created_at", yesterday);
	    queryWrapper.le("created_at", now);

	    int count = 0;
	    int success = 0;
	    int fail = 0;
	    // 查询并返回结果数量
	    List<Code> list = codeMapper.selectList(queryWrapper);
	    for (Code code : list) {
	        count++;
	        if (code.getStatus() == EmailStatus.SUCCESS.getValue()) {
	            success++;
	        } else if (code.getStatus() == EmailStatus.FAILED.getValue()) {
	            continue;
	        } else {
	            fail++;
	        }
	    }
	    // 检查请求数量是否小于等于最小请求数量
	    if (count <= min_request_num) {
	        return false;
	    }
	    // 检查请求数量是否大于最大请求数量
	    if (count > max_request_num) {
	        return true;
	    }
	    // 计算失败率并判断是否超过最大失败率阈值
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
	    QueryWrapper<Code> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("email", email);
	    queryWrapper.ge("created_at", yesterday);
	    queryWrapper.le("created_at", now);
	    queryWrapper.eq("status", EmailStatus.EXPIRED.getValue());

	    // 查询并返回结果数量
	    List<Code> list = codeMapper.selectList(queryWrapper);
	    return !list.isEmpty();
	}


	/**
	 * 根据用户ID检查是否存在有效的用户代码
	 *
	 * @param id 用户ID
	 * @return 如果存在有效的用户代码，则返回true；否则返回false
	 */
	@Override
	public boolean checkingByUserId(Integer id) {
	    // 当前时间
	    Date now = new Date();
	    // 计算expiration_time前的时间
	    Date yesterday = new Date(now.getTime() - (long) expiration_time * 60 * 1000);

	    // 构建查询条件
	    QueryWrapper<Code> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("user_id", id);
	    queryWrapper.ge("created_at", yesterday);
	    queryWrapper.le("created_at", now);
	    queryWrapper.eq("status", EmailStatus.EXPIRED.getValue());

	    // 查询并返回结果数量
	    List<Code> list = codeMapper.selectList(queryWrapper);
	    // 如果列表不为空，则返回true，表示存在有效的用户代码
	    return !list.isEmpty();
	}

	/**
	 * 根据邮箱查询验证码
	 * <p>
	 * 本方法主要用于查询一个邮箱在有效期内的最新验证码
	 * 它首先计算出expiration_time分钟前的时间，然后根据邮箱、创建时间范围和状态来查询验证码记录
	 * 如果找到符合条件的记录，则返回最新的验证码对象，否则返回null
	 *
	 * @param email 需要查询的邮箱地址
	 * @return 返回查询到的验证码对象，如果没有找到则返回null
	 */
	@Override
	public Code queryCodeByEmail(String email) {
	    // 获取当前时间
	    Date now = new Date();
	    // 计算expiration_time分钟前的时间，作为验证码的有效期起点
	    Date yesterday = new Date(now.getTime() - (long) expiration_time * 60 * 1000);

	    // 构建查询条件：针对特定邮箱、在有效期内的验证码
	    QueryWrapper<Code> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("email", email); // 邮箱必须匹配参数email
	    queryWrapper.ge("created_at", yesterday); // 创建时间大于等于yesterday，即expiration_time分钟内创建的
	    queryWrapper.le("created_at", now); // 创建时间小于等于当前时间，即还未过期的
	    queryWrapper.eq("status", EmailStatus.SUCCESS.getValue()); // 验证码发送状态为成功

	    // 执行查询
	    List<Code> list = codeMapper.selectList(queryWrapper);
		// 如果列表为空，则返回null
		if (list.isEmpty()) {
	        return null;
	    }
		// 按照创建时间降序排序
	    list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
	    // 只保留最后一条记录为有效状态，其余设置为失效状态
	    for (int i = 1; i < list.size(); i++) {
	        Code code = list.get(i);
	        code.setStatus(EmailStatus.EXPIRED.getValue());
	        // 更新数据库中的状态
	        codeMapper.updateById(code);
	    }

	    // 返回最后一条记录，即最新的有效验证码
	    return list.get(0);
	}

	/**
	 * 使用验证码
	 * <p>
	 * 本方法主要用于将验证码的状态从未使用（0）更改为已使用（2），并更新数据库中的记录。
	 * 它首先根据用户ID和验证码对象更新数据库中的记录，如果更新成功则返回true，否则返回false。
	 *
	 * @param userId   用户ID
	 * @param code 验证码对象
	 * @return 如果更新成功则返回true，否则返回false
	 */
	@Override
	public boolean useCode(int userId, Code code) {
	    // 设置用户ID，以便确定哪位用户的验证码将被更新
	    code.setUserId(userId);
	    // 将验证码状态更改为“已使用”
	    code.setStatus(EmailStatus.USED.getValue());
	    // 更新数据库中的验证码记录，并返回更新结果
	    return codeMapper.updateById(code) > 0;
	}
}
