package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfMall;

/**
 * 积分商城Service接口
 *
 * @author liwangfu
 */
public interface ILwfMallService
{
    public LwfMall selectLwfMallByMallId(Long mallId);

    public List<LwfMall> selectLwfMallList(LwfMall lwfMall);

    public int insertLwfMall(LwfMall lwfMall);

    public int updateLwfMall(LwfMall lwfMall);

    public int deleteLwfMallByMallIds(Long[] mallIds);

    public int deleteLwfMallByMallId(Long mallId);
}
