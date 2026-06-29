package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfReview;

/**
 * 订单评价 Mapper 接口
 *
 * @author liwangfu
 */
public interface LwfReviewMapper
{
    public LwfReview selectLwfReviewByReviewId(Long reviewId);

    public List<LwfReview> selectLwfReviewList(LwfReview lwfReview);

    public int insertLwfReview(LwfReview lwfReview);

    public int updateLwfReview(LwfReview lwfReview);

    public int deleteLwfReviewByReviewId(Long reviewId);

    public int deleteLwfReviewByReviewIds(Long[] reviewIds);
}
