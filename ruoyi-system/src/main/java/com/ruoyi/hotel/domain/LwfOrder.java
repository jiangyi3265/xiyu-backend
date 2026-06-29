package com.ruoyi.hotel.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单对象 lwf_order
 *
 * @author liwangfu
 */
public class LwfOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 会员ID */
    private Long memberId;

    /** 会员姓名 */
    @Excel(name = "会员姓名")
    private String memberName;

    /** 门店 */
    private String shop;

    /** 业务类型 */
    @Excel(name = "业务类型")
    private String kind;

    /** 配图场景 */
    private String scene;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 角标文案 */
    private String tagText;

    /** 商品标题 */
    @Excel(name = "商品标题")
    private String title;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 数量 */
    private Integer qty;

    /** 商品金额 */
    private BigDecimal amount;

    /** 实付金额 */
    @Excel(name = "实付金额")
    private BigDecimal payAmount;

    /** 下单所用会员券ID（支付成功时核销） */
    private Long couponMcId;

    /** 下单抵扣积分（支付成功时扣减） */
    private Integer usedPoints;

    /** 关联业务ID（储值=套餐ID，客房=房型ID） */
    private Long refId;

    /** 入住日期（客房订单，yyyy-MM-dd） */
    private String checkIn;

    /** 离店日期（客房订单，yyyy-MM-dd） */
    private String checkOut;

    /** 是否已评价(0否 1是) */
    private String reviewed;

    public void setReviewed(String reviewed) { this.reviewed = reviewed; }
    public String getReviewed() { return reviewed; }

    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }
    public String getCheckIn() { return checkIn; }

    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }
    public String getCheckOut() { return checkOut; }

    public void setCouponMcId(Long couponMcId) { this.couponMcId = couponMcId; }
    public Long getCouponMcId() { return couponMcId; }

    public void setUsedPoints(Integer usedPoints) { this.usedPoints = usedPoints; }
    public Integer getUsedPoints() { return usedPoints; }

    public void setRefId(Long refId) { this.refId = refId; }
    public Long getRefId() { return refId; }

    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getOrderId() { return orderId; }

    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getOrderNo() { return orderNo; }

    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getMemberId() { return memberId; }

    public void setMemberName(String memberName) { this.memberName = memberName; }
    public String getMemberName() { return memberName; }

    public void setShop(String shop) { this.shop = shop; }
    public String getShop() { return shop; }

    public void setKind(String kind) { this.kind = kind; }
    public String getKind() { return kind; }

    public void setScene(String scene) { this.scene = scene; }
    public String getScene() { return scene; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    public void setTagText(String tagText) { this.tagText = tagText; }
    public String getTagText() { return tagText; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    public void setQty(Integer qty) { this.qty = qty; }
    public Integer getQty() { return qty; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getAmount() { return amount; }

    public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
    public BigDecimal getPayAmount() { return payAmount; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("memberId", getMemberId())
            .append("memberName", getMemberName())
            .append("shop", getShop())
            .append("kind", getKind())
            .append("scene", getScene())
            .append("status", getStatus())
            .append("tagText", getTagText())
            .append("title", getTitle())
            .append("unitPrice", getUnitPrice())
            .append("qty", getQty())
            .append("amount", getAmount())
            .append("payAmount", getPayAmount())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
