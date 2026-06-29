package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfMemberMapper;
import com.ruoyi.hotel.domain.LwfMember;
import com.ruoyi.hotel.service.ILwfMemberService;

/**
 * 会员Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfMemberServiceImpl implements ILwfMemberService
{
    @Autowired
    private LwfMemberMapper lwfMemberMapper;

    @Override
    public LwfMember selectLwfMemberByMemberId(Long memberId)
    {
        return lwfMemberMapper.selectLwfMemberByMemberId(memberId);
    }

    @Override
    public LwfMember selectLwfMemberByPhone(String phone)
    {
        return lwfMemberMapper.selectLwfMemberByPhone(phone);
    }

    @Override
    public List<LwfMember> selectLwfMemberList(LwfMember lwfMember)
    {
        return lwfMemberMapper.selectLwfMemberList(lwfMember);
    }

    @Override
    public int insertLwfMember(LwfMember lwfMember)
    {
        return lwfMemberMapper.insertLwfMember(lwfMember);
    }

    @Override
    public int updateLwfMember(LwfMember lwfMember)
    {
        return lwfMemberMapper.updateLwfMember(lwfMember);
    }

    @Override
    public int deleteLwfMemberByMemberIds(Long[] memberIds)
    {
        return lwfMemberMapper.deleteLwfMemberByMemberIds(memberIds);
    }

    @Override
    public int deleteLwfMemberByMemberId(Long memberId)
    {
        return lwfMemberMapper.deleteLwfMemberByMemberId(memberId);
    }
}
