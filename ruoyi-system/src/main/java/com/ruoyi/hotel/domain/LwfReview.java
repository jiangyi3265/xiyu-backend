package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单评价对象 lwf_review
 *
 * @author liwangfu
 */
public class LwfReview extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long reviewId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 会员ID */
    private Long memberId;

    /** 会员姓名 */
    @Excel(name = "会员")
    private String memberName;

    /** 订单标题 */
    @Excel(name = "订单标题")
    private String orderTitle;

    /** 评分(1-5) */
    @Excel(name = "评分")
    private Integer rating;

    /** 评价内容 */
    @Excel(name = "评价内容")
    private String content;

    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public Long getReviewId() { return reviewId; }

    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getOrderId() { return orderId; }

    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getMemberId() { return memberId; }

    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberName() { return memberName; }

    public void setOrderTitle(String orderTitle) { this.orderTitle = orderTitle; }
    public String getOrderTitle() { return orderTitle; }

    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getRating() { return rating; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reviewId", getReviewId())
            .append("orderId", getOrderId())
            .append("memberId", getMemberId())
            .append("memberName", getMemberName())
            .append("orderTitle", getOrderTitle())
            .append("rating", getRating())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
