package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfBannerMapper;
import com.ruoyi.hotel.domain.LwfBanner;
import com.ruoyi.hotel.service.ILwfBannerService;

/**
 * 横幅Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfBannerServiceImpl implements ILwfBannerService
{
    @Autowired
    private LwfBannerMapper lwfBannerMapper;

    @Override
    public LwfBanner selectLwfBannerByBannerId(Long bannerId)
    {
        return lwfBannerMapper.selectLwfBannerByBannerId(bannerId);
    }

    @Override
    public List<LwfBanner> selectLwfBannerList(LwfBanner lwfBanner)
    {
        return lwfBannerMapper.selectLwfBannerList(lwfBanner);
    }

    @Override
    public int insertLwfBanner(LwfBanner lwfBanner)
    {
        return lwfBannerMapper.insertLwfBanner(lwfBanner);
    }

    @Override
    public int updateLwfBanner(LwfBanner lwfBanner)
    {
        return lwfBannerMapper.updateLwfBanner(lwfBanner);
    }

    @Override
    public int deleteLwfBannerByBannerIds(Long[] bannerIds)
    {
        return lwfBannerMapper.deleteLwfBannerByBannerIds(bannerIds);
    }

    @Override
    public int deleteLwfBannerByBannerId(Long bannerId)
    {
        return lwfBannerMapper.deleteLwfBannerByBannerId(bannerId);
    }
}
