package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.BannerMapper;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerVo;
import com.jiang.mall.service.IBannerService;
import com.example.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 蒋神，HJL
 * @since 2024/8/5
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

    @Autowired
    private BannerMapper bannerMapper;


//    函数用于获取轮播图列表
    @Override
    public ResponseResult getBannerList(Integer pageNum, Integer pageSize) {
        Page<Banner> bannerPage = new Page<>(pageNum, pageSize);
        List<Banner> banners = bannerMapper.selectPage(bannerPage, null).getRecords();
        List<BannerVo> bannerVos = BeanCopyUtils.copyBeanList(banners, BannerVo.class);
        return ResponseResult.okResult(bannerVos);
    }



    @Override
    public ResponseResult getBanner(Integer id) {

//        通过ID查询数据库获取单个轮播图信息；
        Banner banner = bannerMapper.selectById(id);

//        若查询结果不为空，则将查询结果对象转换为BannerVo对象，并返回成功响应；
        if (banner != null){
            BannerVo bannerVo = BeanCopyUtils.copyBean(banner, BannerVo.class);
            return ResponseResult.okResult(bannerVo);
        }

//        若查询结果为空，则返回失败响应。
        return ResponseResult.failResult();
    }


//    过调用bannerMapper.insert方法将Banner对象存入数据库。
//    若插入成功，返回成功响应；否则，返回失败响应。
    @Override
    public ResponseResult insertBanner(Banner banner) {
        int res = bannerMapper.insert(banner);
        if (res == 1){
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }


//    通过调用bannerMapper.updateById(banner)方法来更新传入的Banner对象。
    @Override
    public ResponseResult updateBanner(Banner banner) {
        int res = bannerMapper.updateById(banner);
//        如果更新成功，返回ResponseResult.okResult()表示操作成功；
//        如果更新失败（即更新的行数不为1），则返回ResponseResult.failResult()表示操作失败。
        if (res == 1){
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }


//    函数的功能是删除传入的用户ID列表中的所有用户。
    @Override
    public ResponseResult deleteBanner(List<Integer> ids) {
        int res = bannerMapper.deleteBatchIds(ids);
        if (res > 0){
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }




}
