package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfCoupon;

/**
 * 优惠券Service接口
 *
 * @author liwangfu
 */
public interface ILwfCouponService
{
    public LwfCoupon selectLwfCouponByCouponId(Long couponId);

    public List<LwfCoupon> selectLwfCouponList(LwfCoupon lwfCoupon);

    public int insertLwfCoupon(LwfCoupon lwfCoupon);

    public int updateLwfCoupon(LwfCoupon lwfCoupon);

    public int deleteLwfCouponByCouponIds(Long[] couponIds);

    public int deleteLwfCouponByCouponId(Long couponId);
}
