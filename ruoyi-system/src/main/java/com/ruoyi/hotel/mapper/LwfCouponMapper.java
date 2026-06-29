package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfCoupon;

/**
 * 优惠券Mapper接口
 *
 * @author liwangfu
 */
public interface LwfCouponMapper
{
    public LwfCoupon selectLwfCouponByCouponId(Long couponId);

    public List<LwfCoupon> selectLwfCouponList(LwfCoupon lwfCoupon);

    public int insertLwfCoupon(LwfCoupon lwfCoupon);

    public int updateLwfCoupon(LwfCoupon lwfCoupon);

    public int deleteLwfCouponByCouponId(Long couponId);

    public int deleteLwfCouponByCouponIds(Long[] couponIds);
}
