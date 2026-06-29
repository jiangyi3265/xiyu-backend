package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.LwfPointlogMapper;
import com.ruoyi.hotel.domain.LwfPointlog;
import com.ruoyi.hotel.service.ILwfPointlogService;

/**
 * 积分记录Service业务层处理
 *
 * @author liwangfu
 */
@Service
public class LwfPointlogServiceImpl implements ILwfPointlogService
{
    @Autowired
    private LwfPointlogMapper lwfPointlogMapper;

    @Override
    public LwfPointlog selectLwfPointlogByLogId(Long logId)
    {
        return lwfPointlogMapper.selectLwfPointlogByLogId(logId);
    }

    @Override
    public List<LwfPointlog> selectLwfPointlogList(LwfPointlog lwfPointlog)
    {
        return lwfPointlogMapper.selectLwfPointlogList(lwfPointlog);
    }

    @Override
    public int insertLwfPointlog(LwfPointlog lwfPointlog)
    {
        return lwfPointlogMapper.insertLwfPointlog(lwfPointlog);
    }

    @Override
    public int updateLwfPointlog(LwfPointlog lwfPointlog)
    {
        return lwfPointlogMapper.updateLwfPointlog(lwfPointlog);
    }

    @Override
    public int deleteLwfPointlogByLogIds(Long[] logIds)
    {
        return lwfPointlogMapper.deleteLwfPointlogByLogIds(logIds);
    }

    @Override
    public int deleteLwfPointlogByLogId(Long logId)
    {
        return lwfPointlogMapper.deleteLwfPointlogByLogId(logId);
    }
}
