package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfMemberCoupon;

/**
 * 会员优惠券Service接口
 *
 * @author liwangfu
 */
public interface ILwfMemberCouponService
{
    public LwfMemberCoupon selectLwfMemberCouponByMcId(Long mcId);

    public List<LwfMemberCoupon> selectLwfMemberCouponList(LwfMemberCoupon lwfMemberCoupon);

    public int insertLwfMemberCoupon(LwfMemberCoupon lwfMemberCoupon);

    public int updateLwfMemberCoupon(LwfMemberCoupon lwfMemberCoupon);

    public int deleteLwfMemberCouponByMcIds(Long[] mcIds);

    public int deleteLwfMemberCouponByMcId(Long mcId);
}
