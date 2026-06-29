package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfRechargeMapper;
import com.ruoyi.hotel.domain.LwfRecharge;
import com.ruoyi.hotel.service.ILwfRechargeService;

/**
 * 充值套餐Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfRechargeServiceImpl implements ILwfRechargeService
{
    @Autowired
    private LwfRechargeMapper lwfRechargeMapper;

    @Override
    public LwfRecharge selectLwfRechargeByRechargeId(Long rechargeId)
    {
        return lwfRechargeMapper.selectLwfRechargeByRechargeId(rechargeId);
    }

    @Override
    public List<LwfRecharge> selectLwfRechargeList(LwfRecharge lwfRecharge)
    {
        return lwfRechargeMapper.selectLwfRechargeList(lwfRecharge);
    }

    @Override
    public int insertLwfRecharge(LwfRecharge lwfRecharge)
    {
        return lwfRechargeMapper.insertLwfRecharge(lwfRecharge);
    }

    @Override
    public int updateLwfRecharge(LwfRecharge lwfRecharge)
    {
        return lwfRechargeMapper.updateLwfRecharge(lwfRecharge);
    }

    @Override
    public int deleteLwfRechargeByRechargeIds(Long[] rechargeIds)
    {
        return lwfRechargeMapper.deleteLwfRechargeByRechargeIds(rechargeIds);
    }

    @Override
    public int deleteLwfRechargeByRechargeId(Long rechargeId)
    {
        return lwfRechargeMapper.deleteLwfRechargeByRechargeId(rechargeId);
    }
}
