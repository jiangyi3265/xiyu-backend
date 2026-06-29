package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.NotBlank;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分商城对象 lwf_mall
 *
 * @author liwangfu
 */
public class LwfMall extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 商品ID */
    private Long mallId;

    /** 商品名称 */
    @Excel(name = "商品名称")
    @NotBlank(message = "商品名称不能为空")
    private String title;

    /** 分类 */
    private Integer cat;

    /** 场景 */
    private String scene;

    /** 积分 */
    @Excel(name = "积分")
    private Integer points;

    /** 显示顺序 */
    private Integer sort;

    /** 状态(0正常 1停用) */
    private String status;

    public void setMallId(Long mallId) { this.mallId = mallId; }
    public Long getMallId() { return mallId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setCat(Integer cat) { this.cat = cat; }
    public Integer getCat() { return cat; }

    public void setScene(String scene) { this.scene = scene; }
    public String getScene() { return scene; }

    public void setPoints(Integer points) { this.points = points; }
    public Integer getPoints() { return points; }

    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getSort() { return sort; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("mallId", getMallId())
            .append("title", getTitle())
            .append("cat", getCat())
            .append("scene", getScene())
            .append("points", getPoints())
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
