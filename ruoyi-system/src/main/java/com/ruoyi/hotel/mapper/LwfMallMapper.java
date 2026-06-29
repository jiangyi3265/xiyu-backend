package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfMall;

/**
 * 积分商城Mapper接口
 *
 * @author liwangfu
 */
public interface LwfMallMapper
{
    public LwfMall selectLwfMallByMallId(Long mallId);

    public List<LwfMall> selectLwfMallList(LwfMall lwfMall);

    public int insertLwfMall(LwfMall lwfMall);

    public int updateLwfMall(LwfMall lwfMall);

    public int deleteLwfMallByMallId(Long mallId);

    public int deleteLwfMallByMallIds(Long[] mallIds);
}
