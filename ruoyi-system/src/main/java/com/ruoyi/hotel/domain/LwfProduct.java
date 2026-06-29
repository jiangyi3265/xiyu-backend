package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 产品对象 lwf_product
 *
 * @author liwangfu
 */
public class LwfProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 产品ID */
    private Long productId;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 标题 */
    @Excel(name = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 主标题 */
    private String main;

    /** 副标题 */
    private String sub;

    /** 主题 */
    private String theme;

    /** 角标 */
    private String badge;

    /** 场景 */
    private String scene;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 原价 */
    private BigDecimal oldPrice;

    /** 适用人群 */
    private String people;

    /** 使用条款 */
    private String terms;

    /** 包含项目 */
    private String items;

    /** 显示顺序 */
    private Integer sort;

    /** 状态(0正常 1停用) */
    private String status;

    public void setProductId(Long productId) { this.productId = productId; }
    public Long getProductId() { return productId; }

    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setMain(String main) { this.main = main; }
    public String getMain() { return main; }

    public void setSub(String sub) { this.sub = sub; }
    public String getSub() { return sub; }

    public void setTheme(String theme) { this.theme = theme; }
    public String getTheme() { return theme; }

    public void setBadge(String badge) { this.badge = badge; }
    public String getBadge() { return badge; }

    public void setScene(String scene) { this.scene = scene; }
    public String getScene() { return scene; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPrice() { return price; }

    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }
    public BigDecimal getOldPrice() { return oldPrice; }

    public void setPeople(String people) { this.people = people; }
    public String getPeople() { return people; }

    public void setTerms(String terms) { this.terms = terms; }
    public String getTerms() { return terms; }

    public void setItems(String items) { this.items = items; }
    public String getItems() { return items; }

    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getSort() { return sort; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("type", getType())
            .append("title", getTitle())
            .append("main", getMain())
            .append("sub", getSub())
            .append("theme", getTheme())
            .append("badge", getBadge())
            .append("scene", getScene())
            .append("price", getPrice())
            .append("oldPrice", getOldPrice())
            .append("people", getPeople())
            .append("terms", getTerms())
            .append("items", getItems())
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
