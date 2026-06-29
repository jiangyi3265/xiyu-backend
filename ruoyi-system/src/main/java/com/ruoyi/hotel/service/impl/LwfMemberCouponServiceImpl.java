package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfMemberCouponMapper;
import com.ruoyi.hotel.domain.LwfMemberCoupon;
import com.ruoyi.hotel.service.ILwfMemberCouponService;

/**
 * 会员优惠券Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfMemberCouponServiceImpl implements ILwfMemberCouponService
{
    @Autowired
    private LwfMemberCouponMapper lwfMemberCouponMapper;

    @Override
    public LwfMemberCoupon selectLwfMemberCouponByMcId(Long mcId)
    {
        return lwfMemberCouponMapper.selectLwfMemberCouponByMcId(mcId);
    }

    @Override
    public List<LwfMemberCoupon> selectLwfMemberCouponList(LwfMemberCoupon lwfMemberCoupon)
    {
        return lwfMemberCouponMapper.selectLwfMemberCouponList(lwfMemberCoupon);
    }

    @Override
    public int insertLwfMemberCoupon(LwfMemberCoupon lwfMemberCoupon)
    {
        return lwfMemberCouponMapper.insertLwfMemberCoupon(lwfMemberCoupon);
    }

    @Override
    public int updateLwfMemberCoupon(LwfMemberCoupon lwfMemberCoupon)
    {
        return lwfMemberCouponMapper.updateLwfMemberCoupon(lwfMemberCoupon);
    }

    @Override
    public int deleteLwfMemberCouponByMcIds(Long[] mcIds)
    {
        return lwfMemberCouponMapper.deleteLwfMemberCouponByMcIds(mcIds);
    }

    @Override
    public int deleteLwfMemberCouponByMcId(Long mcId)
    {
        return lwfMemberCouponMapper.deleteLwfMemberCouponByMcId(mcId);
    }
}
