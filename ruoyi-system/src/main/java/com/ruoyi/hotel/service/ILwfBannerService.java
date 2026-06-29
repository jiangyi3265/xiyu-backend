package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfBanner;

/**
 * 横幅Service接口
 *
 * @author liwangfu
 */
public interface ILwfBannerService
{
    public LwfBanner selectLwfBannerByBannerId(Long bannerId);

    public List<LwfBanner> selectLwfBannerList(LwfBanner lwfBanner);

    public int insertLwfBanner(LwfBanner lwfBanner);

    public int updateLwfBanner(LwfBanner lwfBanner);

    public int deleteLwfBannerByBannerIds(Long[] bannerIds);

    public int deleteLwfBannerByBannerId(Long bannerId);
}
