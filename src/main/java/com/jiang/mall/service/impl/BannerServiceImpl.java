package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.BannerMapper;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerVo;
import com.jiang.mall.service.IBannerService;
import com.jiang.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

    private BannerMapper bannerMapper;

    @Autowired
    public void setBannerMapper(BannerMapper bannerMapper) {
        this.bannerMapper = bannerMapper;
    }

    @Override
    public List<BannerVo> getBannerList(Integer pageNum, Integer pageSize) {
        Page<Banner> bannerPage = new Page<>(pageNum, pageSize);
        List<Banner> banners = bannerMapper.selectPage(bannerPage, null).getRecords();
        List<BannerVo> bannerVos = BeanCopyUtils.copyBeanList(banners, BannerVo.class);
        return bannerVos;
    }

    @Override
    public Integer getBannerNum() {
        List<Banner> banners = bannerMapper.selectList(null);
        return banners.size();
    }

    @Override
    public boolean deleteBanner(Integer id) {
	    return bannerMapper.deleteById(id) == 1;
    }


    @Override
    public ResponseResult getBanner(Integer id) {

        Banner banner = bannerMapper.selectById(id);

        if (banner != null){
            BannerVo bannerVo = BeanCopyUtils.copyBean(banner, BannerVo.class);
            return ResponseResult.okResult(bannerVo);
        }

        return ResponseResult.failResult();
    }

    @Override
    public boolean insertBanner(Banner banner) {
	    return bannerMapper.insert(banner) == 1;
    }

    @Override
    public boolean updateBanner(Banner banner) {
	    return bannerMapper.updateById(banner) == 1;
    }


//    函数的功能是删除传入的用户ID列表中的所有用户。
    @Override
    public ResponseResult deleteBanner(List<Integer> ids) {
        int res = bannerMapper.deleteByIds(ids);
        if (res > 0){
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }




}
