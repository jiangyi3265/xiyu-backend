package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.LwfMember;

/**
 * 会员Mapper接口
 *
 * @author liwangfu
 */
public interface LwfMemberMapper
{
    public LwfMember selectLwfMemberByMemberId(Long memberId);

    public LwfMember selectLwfMemberByPhone(String phone);

    public List<LwfMember> selectLwfMemberList(LwfMember lwfMember);

    public int insertLwfMember(LwfMember lwfMember);

    public int updateLwfMember(LwfMember lwfMember);

    public int deleteLwfMemberByMemberId(Long memberId);

    public int deleteLwfMemberByMemberIds(Long[] memberIds);
}
