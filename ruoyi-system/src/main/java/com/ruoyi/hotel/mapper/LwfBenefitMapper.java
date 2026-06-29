package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfBenefit;

/**
 * 会员权益Mapper接口
 *
 * @author liwangfu
 */
public interface LwfBenefitMapper
{
    public LwfBenefit selectLwfBenefitByBenefitId(Long benefitId);

    public List<LwfBenefit> selectLwfBenefitList(LwfBenefit lwfBenefit);

    public int insertLwfBenefit(LwfBenefit lwfBenefit);

    public int updateLwfBenefit(LwfBenefit lwfBenefit);

    public int deleteLwfBenefitByBenefitId(Long benefitId);

    public int deleteLwfBenefitByBenefitIds(Long[] benefitIds);
}
