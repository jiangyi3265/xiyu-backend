package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.LwfPointlog;

/**
 * 积分记录Service接口
 *
 * @author liwangfu
 */
public interface ILwfPointlogService
{
    public LwfPointlog selectLwfPointlogByLogId(Long logId);

    public List<LwfPointlog> selectLwfPointlogList(LwfPointlog lwfPointlog);

    public int insertLwfPointlog(LwfPointlog lwfPointlog);

    public int updateLwfPointlog(LwfPointlog lwfPointlog);

    public int deleteLwfPointlogByLogIds(Long[] logIds);

    public int deleteLwfPointlogByLogId(Long logId);
}
