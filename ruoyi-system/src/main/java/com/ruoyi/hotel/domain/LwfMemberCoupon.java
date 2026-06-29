package com.ruoyi.hotel.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会员持有优惠券对象 lwf_member_coupon
 *
 * @author liwangfu
 */
public class LwfMemberCoupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long mcId;

    /** 会员ID */
    private Long memberId;

    /** 来源券模板ID */
    private Long couponId;

    /** 券名称 */
    private String title;

    /** 分类(0优惠券 1权益券 2礼遇券 3其他券) */
    private Integer cat;

    /** 面额 */
    private BigDecimal amount;

    /** 使用门槛 */
    private BigDecimal condAmount;

    /** 说明 */
    private String descr;

    /** 有效期至 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date validTime;

    /** 状态(0待使用 1已使用 2已失效) */
    private String status;

    /** 失效原因 */
    private String reason;

    /** 使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date usedTime;

    public void setMcId(Long mcId) { this.mcId = mcId; }
    public Long getMcId() { return mcId; }

    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getMemberId() { return memberId; }

    public void setCouponId(Long couponId) { this.couponId = couponId; }
    public Long getCouponId() { return couponId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setCat(Integer cat) { this.cat = cat; }
    public Integer getCat() { return cat; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getAmount() { return amount; }

    public void setCondAmount(BigDecimal condAmount) { this.condAmount = condAmount; }
    public BigDecimal getCondAmount() { return condAmount; }

    public void setDescr(String descr) { this.descr = descr; }
    public String getDescr() { return descr; }

    public void setValidTime(Date validTime) { this.validTime = validTime; }
    public Date getValidTime() { return validTime; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setReason(String reason) { this.reason = reason; }
    public String getReason() { return reason; }

    public void setUsedTime(Date usedTime) { this.usedTime = usedTime; }
    public Date getUsedTime() { return usedTime; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("mcId", getMcId())
            .append("memberId", getMemberId())
            .append("couponId", getCouponId())
            .append("title", getTitle())
            .append("cat", getCat())
            .append("amount", getAmount())
            .append("condAmount", getCondAmount())
            .append("descr", getDescr())
            .append("validTime", getValidTime())
            .append("status", getStatus())
            .append("reason", getReason())
            .append("usedTime", getUsedTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
