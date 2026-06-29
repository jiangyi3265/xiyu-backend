package com.ruoyi.hotel.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 会员对象 lwf_member
 *
 * @author liwangfu
 */
public class LwfMember extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会员ID */
    private Long memberId;

    /** 手机号 */
    @Excel(name = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 密码(只写不读) */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 姓名/昵称 */
    @Excel(name = "姓名")
    private String name;

    /** 头像 */
    private String avatar;

    /** 会员等级 */
    @Excel(name = "会员等级")
    private String level;

    /** 可用积分 */
    @Excel(name = "可用积分")
    private Integer points;

    /** 储值余额 */
    @Excel(name = "储值余额")
    private BigDecimal balance;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 微信 openid */
    private String openid;

    /** 联系/收货地址 */
    private String address;

    public void setOpenid(String openid) { this.openid = openid; }
    public String getOpenid() { return openid; }

    public void setAddress(String address) { this.address = address; }
    public String getAddress() { return address; }

    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getMemberId() { return memberId; }

    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }

    public void setPassword(String password) { this.password = password; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getAvatar() { return avatar; }

    public void setLevel(String level) { this.level = level; }
    public String getLevel() { return level; }

    public void setPoints(Integer points) { this.points = points; }
    public Integer getPoints() { return points; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getBalance() { return balance; }

    public void setStatus(String status) { this.status = status; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("memberId", getMemberId())
            .append("phone", getPhone())
            .append("name", getName())
            .append("avatar", getAvatar())
            .append("level", getLevel())
            .append("points", getPoints())
            .append("balance", getBalance())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
