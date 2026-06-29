package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.NotBlank;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会员权益对象 lwf_benefit
 *
 * @author liwangfu
 */
public class LwfBenefit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 权益ID */
    private Long benefitId;

    /** 权益名称 */
    @Excel(name = "权益名称")
    @NotBlank(message = "权益名称不能为空")
    private String name;

    /** 权益值 */
    @Excel(name = "权益值")
    private String val;

    /** 图标 */
    private String icon;

    /** 显示顺序 */
    private Integer sort;

    /** 状态(0正常 1停用) */
    private String status;

    public void setBenefitId(Long benefitId) { this.benefitId = benefitId; }
    public Long getBenefitId() { return benefitId; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setVal(String val) { this.val = val; }
    public String getVal() { return val; }

    public void setIcon(String icon) { this.icon = icon; }
    public String getIcon() { return icon; }

    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getSort() { return sort; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("benefitId", getBenefitId())
            .append("name", getName())
            .append("val", getVal())
            .append("icon", getIcon())
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
