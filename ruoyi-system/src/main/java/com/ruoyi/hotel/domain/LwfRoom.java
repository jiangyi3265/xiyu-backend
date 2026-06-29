package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 房型对象 lwf_room
 *
 * @author liwangfu
 */
public class LwfRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 房型ID */
    private Long roomId;

    /** 房型名称 */
    @Excel(name = "房型名称")
    @NotBlank(message = "房型名称不能为空")
    private String name;

    /** 面积 */
    @Excel(name = "面积")
    private Integer area;

    /** 床型 */
    @Excel(name = "床型")
    private String bed;

    /** 窗户 */
    @Excel(name = "窗户")
    private String win;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 配图场景(0-3) */
    private String scene;

    /** 标签(逗号分隔) */
    private String tags;

    /** 房型特色 */
    private String feature;

    /** 取消规则 */
    private String cancelRule;

    /** 设施服务(逗号分隔) */
    private String facilities;

    /** 每日可售房量(库存) */
    @Excel(name = "每日库存")
    private Integer stock;

    /** 显示顺序 */
    private Integer sort;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getRoomId() { return roomId; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setArea(Integer area) { this.area = area; }
    public Integer getArea() { return area; }

    public void setBed(String bed) { this.bed = bed; }
    public String getBed() { return bed; }

    public void setWin(String win) { this.win = win; }
    public String getWin() { return win; }

    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPrice() { return price; }

    public void setScene(String scene) { this.scene = scene; }
    public String getScene() { return scene; }

    public void setTags(String tags) { this.tags = tags; }
    public String getTags() { return tags; }

    public void setFeature(String feature) { this.feature = feature; }
    public String getFeature() { return feature; }

    public void setCancelRule(String cancelRule) { this.cancelRule = cancelRule; }
    public String getCancelRule() { return cancelRule; }

    public void setFacilities(String facilities) { this.facilities = facilities; }
    public String getFacilities() { return facilities; }

    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getStock() { return stock; }

    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getSort() { return sort; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("roomId", getRoomId())
            .append("name", getName())
            .append("area", getArea())
            .append("bed", getBed())
            .append("win", getWin())
            .append("price", getPrice())
            .append("scene", getScene())
            .append("tags", getTags())
            .append("feature", getFeature())
            .append("cancelRule", getCancelRule())
            .append("facilities", getFacilities())
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
