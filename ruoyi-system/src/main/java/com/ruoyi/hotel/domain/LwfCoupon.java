package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Date;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 优惠券对象 lwf_coupon
 *
 * @author liwangfu
 */
public class LwfCoupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 优惠券ID */
    private Long couponId;

    /** 优惠券标题 */
    @Excel(name = "优惠券标题")
    @NotBlank(message = "优惠券标题不能为空")
    private String title;

    /** 分类 */
    private Integer cat;

    /** 优惠金额 */
    @Excel(name = "优惠金额")
    private BigDecimal amount;

    /** 使用门槛金额 */
    private BigDecimal condAmount;

    /** 描述 */
    private String descr;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date validTime;

    /** 发放总量 */
    private Integer totalQty;

    /** 状态(0正常 1停用) */
    private String status;

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

    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }
    public Integer getTotalQty() { return totalQty; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("couponId", getCouponId())
            .append("title", getTitle())
            .append("cat", getCat())
            .append("amount", getAmount())
            .append("condAmount", getCondAmount())
            .append("descr", getDescr())
            .append("validTime", getValidTime())
            .append("totalQty", getTotalQty())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
