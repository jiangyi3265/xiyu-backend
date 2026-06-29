package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfBenefit;

/**
 * 会员权益Service接口
 *
 * @author liwangfu
 */
public interface ILwfBenefitService
{
    public LwfBenefit selectLwfBenefitByBenefitId(Long benefitId);

    public List<LwfBenefit> selectLwfBenefitList(LwfBenefit lwfBenefit);

    public int insertLwfBenefit(LwfBenefit lwfBenefit);

    public int updateLwfBenefit(LwfBenefit lwfBenefit);

    public int deleteLwfBenefitByBenefitIds(Long[] benefitIds);

    public int deleteLwfBenefitByBenefitId(Long benefitId);
}
