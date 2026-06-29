package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 积分记录对象 lwf_point_log
 *
 * @author liwangfu
 */
public class LwfPointlog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long logId;

    /** 会员ID */
    private Long memberId;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 积分 */
    @Excel(name = "积分")
    private Integer points;

    public void setLogId(Long logId) { this.logId = logId; }
    public Long getLogId() { return logId; }

    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getMemberId() { return memberId; }

    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setPoints(Integer points) { this.points = points; }
    public Integer getPoints() { return points; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("memberId", getMemberId())
            .append("type", getType())
            .append("title", getTitle())
            .append("points", getPoints())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
