package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfBenefitMapper;
import com.ruoyi.hotel.domain.LwfBenefit;
import com.ruoyi.hotel.service.ILwfBenefitService;

/**
 * 会员权益Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfBenefitServiceImpl implements ILwfBenefitService
{
    @Autowired
    private LwfBenefitMapper lwfBenefitMapper;

    @Override
    public LwfBenefit selectLwfBenefitByBenefitId(Long benefitId)
    {
        return lwfBenefitMapper.selectLwfBenefitByBenefitId(benefitId);
    }

    @Override
    public List<LwfBenefit> selectLwfBenefitList(LwfBenefit lwfBenefit)
    {
        return lwfBenefitMapper.selectLwfBenefitList(lwfBenefit);
    }

    @Override
    public int insertLwfBenefit(LwfBenefit lwfBenefit)
    {
        return lwfBenefitMapper.insertLwfBenefit(lwfBenefit);
    }

    @Override
    public int updateLwfBenefit(LwfBenefit lwfBenefit)
    {
        return lwfBenefitMapper.updateLwfBenefit(lwfBenefit);
    }

    @Override
    public int deleteLwfBenefitByBenefitIds(Long[] benefitIds)
    {
        return lwfBenefitMapper.deleteLwfBenefitByBenefitIds(benefitIds);
    }

    @Override
    public int deleteLwfBenefitByBenefitId(Long benefitId)
    {
        return lwfBenefitMapper.deleteLwfBenefitByBenefitId(benefitId);
    }
}
