package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfCouponMapper;
import com.ruoyi.hotel.domain.LwfCoupon;
import com.ruoyi.hotel.service.ILwfCouponService;

/**
 * 优惠券Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfCouponServiceImpl implements ILwfCouponService
{
    @Autowired
    private LwfCouponMapper lwfCouponMapper;

    @Override
    public LwfCoupon selectLwfCouponByCouponId(Long couponId)
    {
        return lwfCouponMapper.selectLwfCouponByCouponId(couponId);
    }

    @Override
    public List<LwfCoupon> selectLwfCouponList(LwfCoupon lwfCoupon)
    {
        return lwfCouponMapper.selectLwfCouponList(lwfCoupon);
    }

    @Override
    public int insertLwfCoupon(LwfCoupon lwfCoupon)
    {
        return lwfCouponMapper.insertLwfCoupon(lwfCoupon);
    }

    @Override
    public int updateLwfCoupon(LwfCoupon lwfCoupon)
    {
        return lwfCouponMapper.updateLwfCoupon(lwfCoupon);
    }

    @Override
    public int deleteLwfCouponByCouponIds(Long[] couponIds)
    {
        return lwfCouponMapper.deleteLwfCouponByCouponIds(couponIds);
    }

    @Override
    public int deleteLwfCouponByCouponId(Long couponId)
    {
        return lwfCouponMapper.deleteLwfCouponByCouponId(couponId);
    }
}
