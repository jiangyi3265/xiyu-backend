package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 充值套餐对象 lwf_recharge
 *
 * @author liwangfu
 */
public class LwfRecharge extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 充值ID */
    private Long rechargeId;

    /** 标题 */
    @Excel(name = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 充值金额 */
    @Excel(name = "充值金额")
    private BigDecimal amount;

    /** 赠送金额 */
    @Excel(name = "赠送金额")
    private BigDecimal give;

    /** 优惠券 */
    private BigDecimal coupon;

    /** 赠送时长 */
    private Integer hours;

    /** 是否热门 */
    private String hot;

    /** 赠品名称 */
    private String giftName;

    /** 赠品价格 */
    private BigDecimal giftPrice;

    /** 赠品数量 */
    private Integer giftQty;

    /** 赠品有效期 */
    private String giftValid;

    /** 显示顺序 */
    private Integer sort;

    /** 状态(0正常 1停用) */
    private String status;

    public void setRechargeId(Long rechargeId) { this.rechargeId = rechargeId; }
    public Long getRechargeId() { return rechargeId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getAmount() { return amount; }

    public void setGive(BigDecimal give) { this.give = give; }
    public BigDecimal getGive() { return give; }

    public void setCoupon(BigDecimal coupon) { this.coupon = coupon; }
    public BigDecimal getCoupon() { return coupon; }

    public void setHours(Integer hours) { this.hours = hours; }
    public Integer getHours() { return hours; }

    public void setHot(String hot) { this.hot = hot; }
    public String getHot() { return hot; }

    public void setGiftName(String giftName) { this.giftName = giftName; }
    public String getGiftName() { return giftName; }

    public void setGiftPrice(BigDecimal giftPrice) { this.giftPrice = giftPrice; }
    public BigDecimal getGiftPrice() { return giftPrice; }

    public void setGiftQty(Integer giftQty) { this.giftQty = giftQty; }
    public Integer getGiftQty() { return giftQty; }

    public void setGiftValid(String giftValid) { this.giftValid = giftValid; }
    public String getGiftValid() { return giftValid; }

    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getSort() { return sort; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("rechargeId", getRechargeId())
            .append("title", getTitle())
            .append("amount", getAmount())
            .append("give", getGive())
            .append("coupon", getCoupon())
            .append("hours", getHours())
            .append("hot", getHot())
            .append("giftName", getGiftName())
            .append("giftPrice", getGiftPrice())
            .append("giftQty", getGiftQty())
            .append("giftValid", getGiftValid())
            .append("sort", getSort())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
