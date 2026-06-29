package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfMemberCoupon;

/**
 * 会员优惠券Mapper接口
 *
 * @author liwangfu
 */
public interface LwfMemberCouponMapper
{
    public LwfMemberCoupon selectLwfMemberCouponByMcId(Long mcId);

    public List<LwfMemberCoupon> selectLwfMemberCouponList(LwfMemberCoupon lwfMemberCoupon);

    public int insertLwfMemberCoupon(LwfMemberCoupon lwfMemberCoupon);

    public int updateLwfMemberCoupon(LwfMemberCoupon lwfMemberCoupon);

    public int deleteLwfMemberCouponByMcId(Long mcId);

    public int deleteLwfMemberCouponByMcIds(Long[] mcIds);
}
