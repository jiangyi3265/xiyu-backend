package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfBanner;

/**
 * 横幅Mapper接口
 *
 * @author liwangfu
 */
public interface LwfBannerMapper
{
    public LwfBanner selectLwfBannerByBannerId(Long bannerId);

    public List<LwfBanner> selectLwfBannerList(LwfBanner lwfBanner);

    public int insertLwfBanner(LwfBanner lwfBanner);

    public int updateLwfBanner(LwfBanner lwfBanner);

    public int deleteLwfBannerByBannerId(Long bannerId);

    public int deleteLwfBannerByBannerIds(Long[] bannerIds);
}
