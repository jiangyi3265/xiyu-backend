package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfMallMapper;
import com.ruoyi.hotel.domain.LwfMall;
import com.ruoyi.hotel.service.ILwfMallService;

/**
 * 积分商城Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfMallServiceImpl implements ILwfMallService
{
    @Autowired
    private LwfMallMapper lwfMallMapper;

    @Override
    public LwfMall selectLwfMallByMallId(Long mallId)
    {
        return lwfMallMapper.selectLwfMallByMallId(mallId);
    }

    @Override
    public List<LwfMall> selectLwfMallList(LwfMall lwfMall)
    {
        return lwfMallMapper.selectLwfMallList(lwfMall);
    }

    @Override
    public int insertLwfMall(LwfMall lwfMall)
    {
        return lwfMallMapper.insertLwfMall(lwfMall);
    }

    @Override
    public int updateLwfMall(LwfMall lwfMall)
    {
        return lwfMallMapper.updateLwfMall(lwfMall);
    }

    @Override
    public int deleteLwfMallByMallIds(Long[] mallIds)
    {
        return lwfMallMapper.deleteLwfMallByMallIds(mallIds);
    }

    @Override
    public int deleteLwfMallByMallId(Long mallId)
    {
        return lwfMallMapper.deleteLwfMallByMallId(mallId);
    }
}
