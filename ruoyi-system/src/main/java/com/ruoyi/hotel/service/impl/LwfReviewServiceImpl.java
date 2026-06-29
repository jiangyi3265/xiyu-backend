package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfReviewMapper;
import com.ruoyi.hotel.domain.LwfReview;
import com.ruoyi.hotel.service.ILwfReviewService;

/**
 * 订单评价 Service 业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfReviewServiceImpl implements ILwfReviewService
{
    @Autowired
    private LwfReviewMapper lwfReviewMapper;

    @Override
    public LwfReview selectLwfReviewByReviewId(Long reviewId)
    {
        return lwfReviewMapper.selectLwfReviewByReviewId(reviewId);
    }

    @Override
    public List<LwfReview> selectLwfReviewList(LwfReview lwfReview)
    {
        return lwfReviewMapper.selectLwfReviewList(lwfReview);
    }

    @Override
    public int insertLwfReview(LwfReview lwfReview)
    {
        return lwfReviewMapper.insertLwfReview(lwfReview);
    }

    @Override
    public int updateLwfReview(LwfReview lwfReview)
    {
        return lwfReviewMapper.updateLwfReview(lwfReview);
    }

    @Override
    public int deleteLwfReviewByReviewIds(Long[] reviewIds)
    {
        return lwfReviewMapper.deleteLwfReviewByReviewIds(reviewIds);
    }

    @Override
    public int deleteLwfReviewByReviewId(Long reviewId)
    {
        return lwfReviewMapper.deleteLwfReviewByReviewId(reviewId);
    }
}
