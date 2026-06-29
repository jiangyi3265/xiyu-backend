package com.ruoyi.web.app.domain;

import java.math.BigDecimal;

/**
 * C端下单请求体
 *
 * @author liwangfu
 */
public class AppOrderReq
{
    /** 业务类型(room/dish/promo/mall/coupon) */
    private String kind;
    /** 配图场景 */
    private String scene;
    /** 商品标题 */
    private String title;
    /** 描述(存入备注) */
    private String desc;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 数量 */
    private Integer qty;
    /** 商品金额(可空，空则按 unitPrice*qty 计算) */
    private BigDecimal amount;
    /** 选用的会员券ID(可空) */
    private Long couponMcId;
    /** 是否使用积分抵扣 */
    private Boolean usePoints;
    /** 房型ID(客房订单，用于库存锁定 + 后端核价) */
    private Long roomId;
    /** 商品ID(餐饮/周年庆订单，用于后端核价防篡改) */
    private Long productId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    /** 入住日期(客房订单，yyyy-MM-dd) */
    private String checkIn;
    /** 离店日期(客房订单，yyyy-MM-dd) */
    private String checkOut;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getCheckIn() { return checkIn; }
    public void setCheckIn(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOut() { return checkOut; }
    public void setCheckOut(String checkOut) { this.checkOut = checkOut; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Long getCouponMcId() { return couponMcId; }
    public void setCouponMcId(Long couponMcId) { this.couponMcId = couponMcId; }

    public Boolean getUsePoints() { return usePoints; }
    public void setUsePoints(Boolean usePoints) { this.usePoints = usePoints; }
}
