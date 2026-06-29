package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfRecharge;

/**
 * 充值套餐Mapper接口
 *
 * @author liwangfu
 */
public interface LwfRechargeMapper
{
    public LwfRecharge selectLwfRechargeByRechargeId(Long rechargeId);

    public List<LwfRecharge> selectLwfRechargeList(LwfRecharge lwfRecharge);

    public int insertLwfRecharge(LwfRecharge lwfRecharge);

    public int updateLwfRecharge(LwfRecharge lwfRecharge);

    public int deleteLwfRechargeByRechargeId(Long rechargeId);

    public int deleteLwfRechargeByRechargeIds(Long[] rechargeIds);
}
