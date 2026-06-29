package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfMember;

/**
 * 会员Service接口
 *
 * @author liwangfu
 */
public interface ILwfMemberService
{
    public LwfMember selectLwfMemberByMemberId(Long memberId);

    public LwfMember selectLwfMemberByPhone(String phone);

    public List<LwfMember> selectLwfMemberList(LwfMember lwfMember);

    public int insertLwfMember(LwfMember lwfMember);

    public int updateLwfMember(LwfMember lwfMember);

    public int deleteLwfMemberByMemberIds(Long[] memberIds);

    public int deleteLwfMemberByMemberId(Long memberId);
}
