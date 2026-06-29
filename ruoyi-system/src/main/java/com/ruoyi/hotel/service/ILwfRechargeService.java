package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfRecharge;

/**
 * 充值套餐Service接口
 *
 * @author liwangfu
 */
public interface ILwfRechargeService
{
    public LwfRecharge selectLwfRechargeByRechargeId(Long rechargeId);

    public List<LwfRecharge> selectLwfRechargeList(LwfRecharge lwfRecharge);

    public int insertLwfRecharge(LwfRecharge lwfRecharge);

    public int updateLwfRecharge(LwfRecharge lwfRecharge);

    public int deleteLwfRechargeByRechargeIds(Long[] rechargeIds);

    public int deleteLwfRechargeByRechargeId(Long rechargeId);
}
