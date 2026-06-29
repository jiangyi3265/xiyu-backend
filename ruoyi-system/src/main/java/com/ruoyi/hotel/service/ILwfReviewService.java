package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfReview;

/**
 * 订单评价 Service 接口
 *
 * @author liwangfu
 */
public interface ILwfReviewService
{
    public LwfReview selectLwfReviewByReviewId(Long reviewId);

    public List<LwfReview> selectLwfReviewList(LwfReview lwfReview);

    public int insertLwfReview(LwfReview lwfReview);

    public int updateLwfReview(LwfReview lwfReview);

    public int deleteLwfReviewByReviewIds(Long[] reviewIds);

    public int deleteLwfReviewByReviewId(Long reviewId);
}
