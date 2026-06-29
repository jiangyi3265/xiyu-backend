package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.NotBlank;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 横幅对象 lwf_banner
 *
 * @author liwangfu
 */
public class LwfBanner extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 横幅ID */
    private Long bannerId;

    /** 标题 */
    @Excel(name = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 副标题 */
    private String sub;

    /** 场景 */
    private String scene;

    /** 显示顺序 */
    private Integer sort;

    /** 状态(0正常 1停用) */
    private String status;

    public void setBannerId(Long bannerId) { this.bannerId = bannerId; }
    public Long getBannerId() { return bannerId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setSub(String sub) { this.sub = sub; }
    public String getSub() { return sub; }

    public void setScene(String scene) { this.scene = scene; }
    public String getScene() { return scene; }

    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getSort() { return sort; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("bannerId", getBannerId())
            .append("title", getTitle())
            .append("sub", getSub())
            .append("scene", getScene())
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
